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

	/**
	 * Attempts to connect to ActiLife via a named pipe. Starts watching for
	 * responses from ActiLife. Notifies listeners (view) when data is received.
	 */
	public PipeController() {
		startPipeWatcher();
		gson = new GsonBuilder().setPrettyPrinting().create();
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
		onMessageToDebug(jsonText);
		try {

			StringMap json = (StringMap) gson.fromJson(jsonText, Object.class);
			Object success = json.get("success");
			Object response = json.get("response");
			Object error = json.get("error");

			// required to know if the action was successful or not
			if (success == null) {
				onMessageToDisplay("A malformed response was received:\n\nMissing \"success\" element.");
				return;
			}
			// required to know which action this response is for
			if (response == null) {
				onMessageToDisplay("A malformed response was received:\n\nMissing \"response\" element.");
				return;
			}
			// optionally need to know the error if not successful
			if (Boolean.parseBoolean(success.toString()) == false) {
				if (error == null) {
					onMessageToDisplay("A malformed response was received:\n\nMissing \"error\" element.");
				} else {
					onMessageToDisplay("An error occured processing the "
							+ json.get("response") + " command:\n\n"
							+ json.get("error"));
				}
				return;
			}
			// actilife_minimize
			if (response.toString().equalsIgnoreCase("actilife_minimize")) {
				return;
			}
			// actilife_restore
			if (response.toString().equalsIgnoreCase("actilife_restore")) {
				return;
			}
			// wireless_scan_start
			if (response.toString().equalsIgnoreCase("wireless_scan_start")) {
				if (json.containsKey("payload")) {
					StringMap device = (StringMap) json.get("payload");
					// build up new device model
					Device d = new Device();
					if (device.get("device_ant_id") != null) {
						// integer is coming across as
						d.setAntId(Integer.toString((int) Float
								.parseFloat(device.get("device_ant_id")
										.toString())));
					}
					if (device.get("device_serial") != null) {
						d.setSerial(device.get("device_serial").toString());
					}
					if (device.get("device_subject") != null) {
						d.setSubject(device.get("device_subject").toString());
					}
					if (device.get("device_status") != null) {
						d.setStatus(device.get("device_status").toString());
					}
					if (device.get("device_battery") != null) {
						d.setBattery(Float.parseFloat(device.get(
								"device_battery").toString()));
					}
					onDeviceDiscovered(d);
				}
				return;
			}
			// wireless_scan_stop
			if (response.toString().equalsIgnoreCase("wireless_scan_stop")) {
				return;
			}
			// wireless_device_realtime_start
			if (response.toString().equalsIgnoreCase(
					"wireless_device_realtime_start")) {
				if (json.containsKey("payload")) {
					ArrayList samples = (ArrayList) json.get("payload");
					Iterator samplesIterator = samples.iterator();
					while (samplesIterator.hasNext()) {
						StringMap sample = (StringMap) samplesIterator.next();
						ArrayList axis = (ArrayList) sample.get("axis");
						// build up new sample model
						RealTimeSample rts = new RealTimeSample();
						rts.setTime(sample.get("timestamp").toString());
						if (sample.get("heartrate") != null) {
							rts.setHr(Float.parseFloat(sample.get("heartrate")
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
			// wireless_device_realtime_stop
			if (response.toString().equalsIgnoreCase(
					"wireless_device_realtime_stop")) {
				return;
			}
			// wireless_scan_stop
			if (response.toString()
					.equalsIgnoreCase("wireless_device_identify")) {
				return;
			}
			// wireless_device_burst
			if (response.toString().equalsIgnoreCase("wireless_device_burst")) {
				if (json.containsKey("payload")) {
					StringMap r = (StringMap) json.get("payload");
					if (r.containsKey("file_output_path")) {
						onMessageToDisplay(r.get("file_output_path").toString()
								+ " was successfully downloaded.");
					}
				}
				return;
			}
			// usb_list
			if (response.toString().equalsIgnoreCase("usb_list")) {
				if (json.containsKey("payload")) {
					StringMap device = (StringMap) json.get("payload");
					// build up new device model
					Device d = new Device();
					if (device.get("device_serial") != null) {
						d.setSerial(device.get("device_serial").toString());
					}
					if (device.get("device_status") != null) {
						d.setStatus(device.get("device_status").toString());
					}
					if (device.get("device_subject") != null) {
						d.setSubject(device.get("device_subject").toString());
					}
					if (device.get("device_battery") != null) {
						d.setBattery(Float.parseFloat(device.get(
								"device_battery").toString()));
					}
					onDeviceDiscovered(d);
				}
				return;
			}
			// usb_initialize
			if (response.toString().equalsIgnoreCase("usb_initialize")) {
				onMessageToDisplay("Device intialized");
				return;
			}
			// usb_download
			if (response.toString().equalsIgnoreCase("usb_download")) {
				onMessageToDisplay("Device downloaded");
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

		action.put("args", requestedArgs);

		switch (o.getAction()) {
		case ACTILIFE_MINIMIZE:
			action.put("action", "actilife_minimize");
			break;
		case ACTILIFE_RESTORE:
			action.put("action", "actilife_restore");
			break;
		case WIRELESS_SCAN_START:
			action.put("action", "wireless_scan_start");
			break;
		case WIRELESS_SCAN_STOP:
			action.put("action", "wireless_scan_stop");
			break;
		case WIRELESS_DEVICE_IDENTIFY:
			action.put("action", "wireless_device_identify");
			break;
		case WIRELESS_DEVICE_REALTIME_START:
			action.put("action", "wireless_device_realtime_start");
			break;
		case WIRELESS_DEVICE_REALTIME_STOP:
			action.put("action", "wireless_device_realtime_stop");
			break;
		case WIRELESS_DEVICE_BURST:
			action.put("action", "wireless_device_burst");
			break;
		case USB_LIST:
			action.put("action", "usb_list");
			break;
		case USB_DOWNLOAD:
			action.put("action", "usb_download");
			break;
		case USB_INITIALIZE:
			action.put("action", "usb_initialize");
			break;
		default:
			return;
		}

		String actionJSON = gson.toJson(action);
		onMessageToDebug(actionJSON);
		try {
			synchronized (pipeLock) {
				pipe.write(actionJSON.getBytes());
			}
		} catch (Exception e) {
			onExceptionRaised(e);
		}
	}
}
