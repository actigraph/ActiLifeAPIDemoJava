package com.theactigraph.actilife.api.controller;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.api.models.Device;
import com.theactigraph.actilife.api.models.RealTimeSample;
import com.theactigraph.actilife.api.models.events.ActionEventObject;
import com.theactigraph.actilife.api.models.events.DeviceEventObject;
import com.theactigraph.actilife.api.models.events.ExceptionEventObject;
import com.theactigraph.actilife.api.models.events.IActionSenderListener;
import com.theactigraph.actilife.api.models.events.IResponseHandlerListener;
import com.theactigraph.actilife.api.models.events.MessageEventObject;
import com.theactigraph.actilife.api.models.events.RealTimeSampleEventObject;

/**
 * Responsible for all communication with ActiLife. Custom events are fired when
 * responses are validated and parsed into typed models.
 * 
 * @author jeremy.moore
 */
public class PipeController implements IActionSenderListener {
	/**
	 * List of listeners for handling responses.
	 */
	private transient Vector<IResponseHandlerListener> responseHandlerListeners;
	/**
	 * Handle to pipe for commands and responses.
	 */
	private RandomAccessFile pipe = null;
	/**
	 * Lock for synchronizing pipe access.
	 */
	private final Object pipeLock = new Object();
	/**
	 * JSON Parser
	 */
	private Gson gson = null;

	private String lastAction = null;

	/**
	 * Attempts to connect to ActiLife via a named pipe. Starts watching for
	 * responses from ActiLife. Notifies listeners (view) when data is received.
	 */
	public PipeController() {
		startPipeWatcher();
		gson = new GsonBuilder().create();
	}

	/**
	 * Continuously check the pipe for responses from ActiLife. Responses are
	 * UTF8 encoded and \r\n terminated.
	 */
	private void startPipeWatcher() {
		Runnable pipeWatcher = new Runnable() {
			public void run() {
				while (true) {
					try {
						if (pipe == null) {
							pipe = new RandomAccessFile(
									"\\\\.\\pipe\\actilifeapi", "rw");
						}
						StringBuilder buffer = new StringBuilder();
						byte[] b = new byte[1024];
						while (true) {
							long pipeLength = pipe.length();
							if (pipeLength == 0) {
								try {
									Thread.sleep(100);
								} catch (InterruptedException ex) {
									onExceptionRaised(ex);
								}
								continue;
							}
							int bytesRead = 0;
							synchronized (pipeLock) {
								bytesRead = pipe.read(b, 0, b.length);
							}
							if (bytesRead > 0) {
								buffer.append(new String(b, 0, bytesRead));
							}
							if (buffer.length() > 0
									&& buffer.toString().endsWith("\r\n")) {
								handleResponse(buffer.toString());
								buffer.delete(0, buffer.length() - 1);
							}
						}

					} catch (IOException ioe) {
						onExceptionRaised(ioe);
					}
				}
			}
		};
		new Thread(pipeWatcher).start();
	}

	/**
	 * Handles responses from ActiLife. Fires custom events with data for UI to
	 * handle the data appropriately.
	 * 
	 * @param jsonText
	 *            Raw JSON response message.
	 */
	@SuppressWarnings({ "rawtypes" })
	private void handleResponse(String jsonText) {
		if (jsonText == null) {
			return;
		}
		
		//for cleaner debug output
		if (jsonText.startsWith("\n"))
			jsonText = jsonText.replaceFirst("\n", "");
		
		try {
			StringMap json = (StringMap) gson.fromJson(jsonText, Object.class);
			Object success = json.get("Success");
			Object response = json.get("Response");
			Object error = json.get("Error");

			//show the response in the debug console
			onMessageToDebug(" <= " + jsonText);
			if (!response.toString().equalsIgnoreCase(lastAction)) {
				onMessageToDebug("\n\n");
			}

			// required to know if the action was successful or not
			if (success == null) {
				onMessageToDisplay("A malformed response was received:\n\nMissing \"Success\" element.");
				return;
			}
			// required to know which action this response is for
			if (response == null) {
				onMessageToDisplay("A malformed response was received:\n\nMissing \"Response\" element.");
				return;
			}
			// optionally need to know the error if not successful
			if (Boolean.parseBoolean(success.toString()) == false) {
				if (error == null) {
					onMessageToDisplay("A malformed response was received:\n\nMissing \"Error\" element.");
				} else {
					onMessageToDisplay("An error occured processing the "
							+ response + " command:\n\n" + error);
				}
				return;
			}

			if (response.toString().equalsIgnoreCase("ActiLifeMinimize")) {
				return;
			}
			if (response.toString().equalsIgnoreCase("ActiLifeRestore")) {
				return;
			}
			if (response.toString().equalsIgnoreCase("ActiLifeVersion")) {
				if (json.containsKey("Payload")) {
					StringMap p = (StringMap) json.get("Payload");
					onMessageToDisplay("ActiLife Version: " + p.get("version"));
				}
				return;
			}
			if (response.toString().equalsIgnoreCase("APIVersion")) {
				if (json.containsKey("Payload")) {
					StringMap p = (StringMap) json.get("Payload");
					onMessageToDisplay("API Version: " + p.get("version"));
				}
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessStart")) {
				if (json.containsKey("Payload")) {
					StringMap device = (StringMap) json.get("Payload");
					// build up new device model
					Device d = new Device();
					if (device.get("AntID") != null) {
						// integer is coming across as
						d.setAntId(Integer.toString((int) Float
								.parseFloat(device.get("AntID").toString())));
					}
					if (device.get("Serial") != null) {
						d.setSerial(device.get("Serial").toString());
					}
					if (device.get("Subject") != null) {
						d.setSubject(device.get("Subject").toString());
					}
					if (device.get("Status") != null) {
						d.setStatus(device.get("Status").toString());
					}
					if (device.get("Battery") != null) {
						d.setBatteryVoltage(device.get("Battery").toString());
					}
					onDeviceDiscovered(d);
				}
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessStop")) {
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessRealtimeStart")) {
				if (json.containsKey("Payload")) {
					onMessageToDisplay(json.get("Payload").toString());
					ArrayList samples = (ArrayList) json.get("Payload");
					Iterator samplesIterator = samples.iterator();
					while (samplesIterator.hasNext()) {
						StringMap sample = (StringMap) samplesIterator.next();
						ArrayList axis = (ArrayList) sample.get("axis");
						// build up new sample model
						RealTimeSample rts = new RealTimeSample();
						rts.setTime(sample.get("timestamp").toString());
						if (sample.get("heartRate") != null) {
							rts.setHr(Float.parseFloat(sample.get("heartRate")
									.toString()));
						}
						rts.setLux(Float.parseFloat(sample.get("lux")
								.toString()));
						rts.setAxis1(Float.parseFloat(axis.get(0).toString()));
						rts.setAxis2(Float.parseFloat(axis.get(1).toString()));
						rts.setAxis3(Float.parseFloat(axis.get(2).toString()));
						onGotRealTimeData(rts);
					}
				}
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessRealtimeStop")) {
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessIdentify")) {
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessInitialize")) {
				onMessageToDisplay("Device intialized");
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessBurst")) {
				onMessageToDisplay("Burst completed");
				return;
			}
			if (response.toString().equalsIgnoreCase("WirelessBurst")) {
				if (json.containsKey("Payload")) {
					StringMap r = (StringMap) json.get("Payload");
					if (r.containsKey("FileOutputPath")) {
						onMessageToDisplay(r.get("FileOutputPath").toString()
								+ " was successfully downloaded.");
					}
				}
				return;
			}
			if (response.toString().equalsIgnoreCase("USBList")) {
				if (json.containsKey("Payload")) {
					StringMap device = (StringMap) json.get("Payload");
					// build up new device model
					Device d = new Device();
					if (device.get("Serial") != null) {
						d.setSerial(device.get("Serial").toString());
					}
					if (device.get("AntID") != null) {
						d.setAntId(device.get("AntID").toString());
					}
					if (device.get("Status") != null) {
						d.setStatus(device.get("Status").toString());
					}
					if (device.get("Subject") != null) {
						d.setSubject(device.get("Subject").toString());
					}
					if (device.get("BatteryVoltage") != null) {
						d.setBatteryVoltage(device.get("BatteryVoltage")
								.toString());
					}
					if (device.get("BatteryPercent") != null) {
						d.setBatteryPercentage(device.get("BatteryPercent")
								.toString());
					}
					if (device.get("SampleRate") != null) {
						d.setSampleRate(device.get("SampleRate").toString());
					}
					if (device.get("Firmware") != null) {
						d.setFirmware(device.get("Firmware").toString());
					}
					try {
						if (device.get("StartTime") != null) {
							d.setStartTime(device.get("StartTime").toString());
						}
					} catch (Exception e) {
					}
					try {
						if (device.get("StopTime") != null
								&& !device.get("StopTime").toString()
										.equals("0001-01-01T00:00:00Z")) {
							d.setStopTime(device.get("StopTime").toString());
						}
					} catch (Exception e) {
					}
					onDeviceDiscovered(d);
				}
				return;
			}
			if (response.toString().equalsIgnoreCase("USBInitialize")) {
				onMessageToDisplay("Device intialized");
				return;
			}
			if (response.toString().equalsIgnoreCase("USBDownload")) {
				onMessageToDisplay("Device downloaded");
				return;
			}
			if (response.toString().equalsIgnoreCase("USBIdentified")) {
				onMessageToDisplay("Device identified");
				return;
			}
		} catch (JsonParseException e) {
			onExceptionRaised(e);
		}
	}

	/**
	 * Adds an event listener.
	 * 
	 * @param l
	 *            listener to be added
	 */
	synchronized public void addListener(IResponseHandlerListener l) {
		if (responseHandlerListeners == null) {
			responseHandlerListeners = new Vector<IResponseHandlerListener>();
		}
		responseHandlerListeners.addElement(l);
	}

	/**
	 * Removes an event listener.
	 * 
	 * @param l
	 *            listener to be removed
	 */
	synchronized public void removeListener(IResponseHandlerListener l) {
		if (responseHandlerListeners == null) {
			responseHandlerListeners = new Vector<IResponseHandlerListener>();
		}
		responseHandlerListeners.removeElement(l);
	}

	@SuppressWarnings("unchecked")
	protected void onMessageToDisplay(String message) {
		if (responseHandlerListeners != null
				&& !responseHandlerListeners.isEmpty()) {
			MessageEventObject event;
			event = new MessageEventObject(this, message);
			Vector<IResponseHandlerListener> targets;
			synchronized (this) {
				targets = (Vector<IResponseHandlerListener>) responseHandlerListeners
						.clone();
			}
			Enumeration<IResponseHandlerListener> e = targets.elements();
			while (e.hasMoreElements()) {
				IResponseHandlerListener l = (IResponseHandlerListener) e
						.nextElement();
				l.messageToDisplay(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void onMessageToDebug(String message) {
		if (responseHandlerListeners != null
				&& !responseHandlerListeners.isEmpty()) {
			MessageEventObject event;
			event = new MessageEventObject(this, message);
			Vector<IResponseHandlerListener> targets;
			synchronized (this) {
				targets = (Vector<IResponseHandlerListener>) responseHandlerListeners
						.clone();
			}
			Enumeration<IResponseHandlerListener> e = targets.elements();
			while (e.hasMoreElements()) {
				IResponseHandlerListener l = (IResponseHandlerListener) e
						.nextElement();
				l.messageToDebug(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void onExceptionRaised(Exception exception) {
		if (responseHandlerListeners != null
				&& !responseHandlerListeners.isEmpty()) {
			ExceptionEventObject event;
			event = new ExceptionEventObject(this, exception);
			Vector<IResponseHandlerListener> targets;
			synchronized (this) {
				targets = (Vector<IResponseHandlerListener>) responseHandlerListeners
						.clone();
			}
			Enumeration<IResponseHandlerListener> e = targets.elements();
			while (e.hasMoreElements()) {
				IResponseHandlerListener l = (IResponseHandlerListener) e
						.nextElement();
				l.exceptionRaised(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void onDeviceDiscovered(Device device) {
		if (responseHandlerListeners != null
				&& !responseHandlerListeners.isEmpty()) {
			DeviceEventObject event;
			event = new DeviceEventObject(this, device);
			Vector<IResponseHandlerListener> targets;
			synchronized (this) {
				targets = (Vector<IResponseHandlerListener>) responseHandlerListeners
						.clone();
			}
			Enumeration<IResponseHandlerListener> e = targets.elements();
			while (e.hasMoreElements()) {
				IResponseHandlerListener l = (IResponseHandlerListener) e
						.nextElement();
				l.deviceDiscovered(event);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected void onGotRealTimeData(RealTimeSample sample) {
		if (responseHandlerListeners != null
				&& !responseHandlerListeners.isEmpty()) {
			RealTimeSampleEventObject event = new RealTimeSampleEventObject(
					this, sample);
			Vector<IResponseHandlerListener> targets;
			synchronized (this) {
				targets = (Vector<IResponseHandlerListener>) responseHandlerListeners
						.clone();
			}
			Enumeration<IResponseHandlerListener> e = targets.elements();
			while (e.hasMoreElements()) {
				IResponseHandlerListener l = (IResponseHandlerListener) e
						.nextElement();
				l.gotRealTimeData(event);
			}
		}
	}

	/**
	 * Sends a command as requested by the UI/view/user.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void actionRequested(ActionEventObject o) {
		if (o == null || o.getAction() == null) {
			return;
		}

		StringMap action = new StringMap();
		StringMap requestedArgs = null;

		if (o.getArgs() != null) {
			requestedArgs = o.getArgs();
		}

		String actionName = null;

		switch (o.getAction()) {
		case ACTILIFE_MINIMIZE:
			actionName = "ActiLifeMinimize";
			break;
		case ACTILIFE_RESTORE:
			actionName = "ActiLifeRestore";
			break;
		case ACTILIFE_VERSION:
			actionName = "ActiLifeVersion";
			break;
		case API_VERSION:
			actionName = "APIVersion";
			break;
		case WIRELESS_SCAN_START:
			actionName = "WirelessStart";
			break;
		case WIRELESS_SCAN_STOP:
			actionName = "WirelessStop";
			break;
		case WIRELESS_IDENTIFY:
			actionName = "WirelessIdentify";
			break;
		case WIRELESS_INITIALIZE:
			actionName = "WirelessInitialize";
			break;
		case WIRELESS_REALTIME_START:
			actionName = "WirelessRealtimeStart";
			break;
		case WIRELESS_REALTIME_STOP:
			actionName = "WirelessRealtimeStop";
			break;
		case WIRELESS_BURST:
			actionName = "WirelessBurst";
			break;
		case USB_LIST:
			actionName = "USBList";
			break;
		case USB_DOWNLOAD:
			actionName = "USBDownload";
			break;
		case USB_INITIALIZE:
			actionName = "USBInitialize";
			break;
		case USB_IDENTIFY:
			actionName = "USBIdentify";
			break;
		default:
			return;
		}

		action.put("Action", actionName);
		action.put("Args", requestedArgs);

		// for prettier debug output :-/
		if (lastAction != null && !actionName.toString().equalsIgnoreCase(lastAction)) {
			onMessageToDebug("\n\n");
		}
		lastAction = actionName;

		String actionJSON = gson.toJson(action);
		onMessageToDebug(" => " + actionJSON);
		try {
			synchronized (pipeLock) {
				pipe.write(actionJSON.getBytes());
			}
		} catch (Exception e) {
			onExceptionRaised(e);
		}
	}
}
