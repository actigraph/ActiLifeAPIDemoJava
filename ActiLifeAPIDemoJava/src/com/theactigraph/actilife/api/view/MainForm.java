package com.theactigraph.actilife.api.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.api.models.Action;
import com.theactigraph.actilife.api.models.DeviceTableModel;
import com.theactigraph.actilife.api.models.events.DeviceEventObject;
import com.theactigraph.actilife.api.models.events.ExceptionEventObject;
import com.theactigraph.actilife.api.models.events.IActionSenderListener;
import com.theactigraph.actilife.api.models.events.IResponseHandlerListener;
import com.theactigraph.actilife.api.models.events.MessageEventObject;
import com.theactigraph.actilife.api.models.events.RealTimeSampleEventObject;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JTable;
import java.awt.BorderLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.FlowLayout;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class MainForm extends JFrameActionSender implements
		IResponseHandlerListener {

	private JTable table;
	private DebugDialog debugDialog;
	private WirelessRealTimeDialog wirelessRealTimeDialog;
	private WirelessBurstDialog wirelessBurstDialog;
	private WirelessInitializeDialog wirelessInitializeDialog;
	private USBDownloadDialog usbDownloadDialog;
	private USBInitializeDialog usbInitializeDialog;

	private JFrame refToThis;
	private String lastSelectedDeviceAntId;
	private String lastSelectedDeviceSerial;

	private JButton btnInitialize;
	private JButton btnBurst;
	private JButton btnIdentify;
	private JButton btnDownload;
	private JButton btnRealTime;
	private JButton btnClearList;
	private JTextField txtPIN;

	public MainForm() {
		setTitle("ActiLife API Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1000, 600);

		refToThis = this;

		wirelessRealTimeDialog = new WirelessRealTimeDialog(refToThis, false);
		wirelessRealTimeDialog.setLocationRelativeTo(refToThis);

		wirelessBurstDialog = new WirelessBurstDialog(refToThis, false);
		wirelessBurstDialog.setLocationRelativeTo(refToThis);

		wirelessInitializeDialog = new WirelessInitializeDialog(refToThis,
				false);
		wirelessInitializeDialog.setLocationRelativeTo(refToThis);

		usbDownloadDialog = new USBDownloadDialog(refToThis, false);
		usbDownloadDialog.setLocationRelativeTo(refToThis);

		usbInitializeDialog = new USBInitializeDialog(refToThis, false);
		usbInitializeDialog.setLocationRelativeTo(refToThis);

		debugDialog = new DebugDialog(refToThis, false);
		debugDialog.setLocationRelativeTo(refToThis);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnNewMenu = new JMenu("File");
		menuBar.add(mnNewMenu);

		JMenuItem mntmFileExit = new JMenuItem("Exit");
		mntmFileExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0); // probably not the best! ;)
			}
		});
		mnNewMenu.add(mntmFileExit);

		JMenu mnNewMenu_1 = new JMenu("ActiLife");
		menuBar.add(mnNewMenu_1);

		JMenuItem mntmActiLifeLaunch = new JMenuItem("Launch");
		mntmActiLifeLaunch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String path = "C:\\Program Files (x86)\\ActiGraph\\ActiLife6\\ActiLife.exe";
					Runtime.getRuntime().exec(path);
				} catch (IOException ex) {
					Logger.getLogger(MainForm.class.getName()).log(
							Level.SEVERE, null, ex);
				}
			}
		});
		mnNewMenu_1.add(mntmActiLifeLaunch);

		JMenuItem mntmActiLifeMinimize = new JMenuItem("Minimize");
		mntmActiLifeMinimize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.ACTILIFE_MINIMIZE, null);
			}
		});
		mnNewMenu_1.add(mntmActiLifeMinimize);

		JMenuItem mntmActiLifeRestore = new JMenuItem("Restore");
		mntmActiLifeRestore.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.ACTILIFE_RESTORE, null);
			}
		});
		mnNewMenu_1.add(mntmActiLifeRestore);

		JMenu mnUsb = new JMenu("USB");
		menuBar.add(mnUsb);

		JMenuItem mntmUSBList = new JMenuItem("List");
		mntmUSBList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.USB_LIST, null);
			}
		});
		mnUsb.add(mntmUSBList);

		JMenu mnWireless = new JMenu("Wireless");
		menuBar.add(mnWireless);

		JMenuItem mntmWirelessStart = new JMenuItem("Start");
		mntmWirelessStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringMap args = new StringMap();
				args.put("AntPIN", txtPIN.getText());
				onActionRequested(Action.WIRELESS_SCAN_START, args);
				updateUI();
			}
		});
		mnWireless.add(mntmWirelessStart);

		JMenuItem mntmWirelessStop = new JMenuItem("Stop");
		mntmWirelessStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.WIRELESS_SCAN_STOP, null);
				updateUI();
			}
		});
		mnWireless.add(mntmWirelessStop);

		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		JMenuItem mntmToolsDebugConsole = new JMenuItem("Debug Console");
		mntmToolsDebugConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (debugDialog != null) {
					debugDialog.setVisible(!debugDialog.isShowing());
				}
			}
		});
		mnTools.add(mntmToolsDebugConsole);

		JMenu mnHelp = new JMenu("Help");
		menuBar.add(mnHelp);

		JMenuItem mntmHelpActilifeVersion = new JMenuItem("ActiLife Version");
		mntmHelpActilifeVersion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.ACTILIFE_VERSION, null);
			}
		});
		mnHelp.add(mntmHelpActilifeVersion);

		JMenuItem mntmHelpApiVersion = new JMenuItem("API Version");
		mntmHelpApiVersion.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.API_VERSION, null);
			}
		});
		mnHelp.add(mntmHelpApiVersion);

		JPanel pnlTopButtons = new JPanel();
		FlowLayout fl_pnlTopButtons = (FlowLayout) pnlTopButtons.getLayout();
		fl_pnlTopButtons.setAlignment(FlowLayout.LEFT);
		getContentPane().add(pnlTopButtons, BorderLayout.NORTH);

		btnIdentify = new JButton("Identify");
		btnIdentify.setEnabled(false);
		btnIdentify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringMap args = new StringMap();
				if (lastSelectedDeviceSerial != null
						&& lastSelectedDeviceSerial != "") {
					args.put("Serial", lastSelectedDeviceSerial);
					onActionRequested(Action.USB_IDENTIFY, args);
				} else if (lastSelectedDeviceAntId != null
						&& lastSelectedDeviceAntId != "") {
					args.put("AntID", lastSelectedDeviceAntId);
					args.put("AntPIN", txtPIN.getText());
					onActionRequested(Action.WIRELESS_IDENTIFY, args);
				}
			}
		});
		pnlTopButtons.add(btnIdentify);

		btnInitialize = new JButton("Initialize");
		btnInitialize.setEnabled(false);
		btnInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (lastSelectedDeviceSerial != null
						&& lastSelectedDeviceSerial != "") {
					if (usbInitializeDialog != null) {
						usbInitializeDialog.setDeviceSerial(lastSelectedDeviceSerial);
						usbInitializeDialog.setVisible(true);
					}
				} else if (lastSelectedDeviceAntId != null
						&& lastSelectedDeviceAntId != "") {
					if (wirelessInitializeDialog != null) {
						wirelessInitializeDialog.setDeviceAntId(lastSelectedDeviceAntId);
						wirelessInitializeDialog.setAntPin(txtPIN.getText());
						wirelessInitializeDialog.setVisible(true);
					}
				}
			}
		});
		pnlTopButtons.add(btnInitialize);

		btnDownload = new JButton("Download");
		btnDownload.setEnabled(false);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (usbDownloadDialog != null) {
					usbDownloadDialog.setDeviceSerial(lastSelectedDeviceSerial);
					usbDownloadDialog.setVisible(true);
				}
			}
		});
		pnlTopButtons.add(btnDownload);

		btnBurst = new JButton("Burst");
		btnBurst.setEnabled(false);
		btnBurst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (wirelessBurstDialog != null) {
					wirelessBurstDialog.setDeviceAntId(lastSelectedDeviceAntId);
					wirelessBurstDialog.setAntPin(txtPIN.getText());
					wirelessBurstDialog.setVisible(true);
				}
			}
		});
		pnlTopButtons.add(btnBurst);

		btnRealTime = new JButton("Real Time");
		btnRealTime.setEnabled(false);
		btnRealTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (wirelessRealTimeDialog != null) {
					wirelessRealTimeDialog.setDeviceAntId(lastSelectedDeviceAntId);
					wirelessRealTimeDialog.setAntPin(txtPIN.getText());
					wirelessRealTimeDialog.setVisible(true);
				}
			}
		});
		pnlTopButtons.add(btnRealTime);

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setFillsViewportHeight(true);
		table.setModel(new DeviceTableModel());
		table.getColumnModel().getColumn(2).setPreferredWidth(109);
		table.getColumnModel().getColumn(3).setPreferredWidth(133);
		table.getColumnModel().getColumn(4).setPreferredWidth(177);
		table.getColumnModel().getColumn(5).setPreferredWidth(164);
		table.getColumnModel().getColumn(6).setPreferredWidth(61);
		table.getColumnModel().getColumn(7).setPreferredWidth(70);
		table.getColumnModel().getColumn(8).setPreferredWidth(89);
		table.getColumnModel().getColumn(9).setPreferredWidth(113);

		table.getSelectionModel().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						lastSelectedDeviceSerial = "";
						lastSelectedDeviceAntId = "";

						Object tmp = table.getModel().getValueAt(
								table.getSelectedRow(), 0);
						if (tmp != null)
							lastSelectedDeviceSerial = tmp.toString();

						tmp = table.getModel().getValueAt(
								table.getSelectedRow(), 1);
						if (tmp != null)
							lastSelectedDeviceAntId = tmp.toString();

						updateUI();
					}
				});

		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.RIGHT);
		getContentPane().add(panel, BorderLayout.SOUTH);

		btnClearList = new JButton("Clear List");
		btnClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((DeviceTableModel) table.getModel()).clear();
			}
		});

		JLabel lblPIN = new JLabel("Ant PIN");
		panel.add(lblPIN);

		txtPIN = new JTextField();
		txtPIN.setText("0000");
		panel.add(txtPIN);
		txtPIN.setColumns(4);
		panel.add(btnClearList);
	}

	private void updateUI() {
		// toggle misc buttons
		boolean deviceTable = false;
		if (table.getModel().getRowCount() > 0) {
			deviceTable = true;
		}
		btnClearList.setEnabled(deviceTable);

		// toggle usb buttons
		boolean usb = false;
		if (lastSelectedDeviceSerial != null && lastSelectedDeviceSerial != "") {
			usb = true;
		}
		btnIdentify.setEnabled(usb);
		btnInitialize.setEnabled(usb);
		btnDownload.setEnabled(usb);

		// toggle wireless buttons
		boolean wireless = false;
		if (lastSelectedDeviceAntId != null && lastSelectedDeviceAntId != "") {
			wireless = true;
		}
		btnBurst.setEnabled(wireless);
		btnRealTime.setEnabled(wireless);
	}

	/**
	 * Adds an event listener for nested dialogs.
	 * 
	 * @param l
	 *            listener to be added
	 */
	synchronized public void addWirelessRealTimeListener(IActionSenderListener l) {
		if (wirelessRealTimeDialog != null) {
			wirelessRealTimeDialog.addListener(l);
		}
		if (wirelessBurstDialog != null) {
			wirelessBurstDialog.addListener(l);
		}
		if (wirelessInitializeDialog != null) {
			wirelessInitializeDialog.addListener(l);
		}
		if (usbDownloadDialog != null) {
			usbDownloadDialog.addListener(l);
		}
		if (usbInitializeDialog != null) {
			usbInitializeDialog.addListener(l);
		}
	}

	/**
	 * Displays any message received from parsing responses from ActiLife.
	 */
	@Override
	public void messageToDisplay(MessageEventObject o) {
		JOptionPane.showMessageDialog(this, o.getMessage());
	}

	/**
	 * Debugs any message received from parsing responses from ActiLife.
	 */
	@Override
	public void messageToDebug(MessageEventObject o) {
		if (debugDialog == null || o == null || o.getMessage() == null) {
			return;
		}
		debugDialog.appendText(o.getMessage());
		if (!o.getMessage().endsWith("\n")) {
			debugDialog.appendText("\n");
		}
	}

	/**
	 * Logs an exception received from parsing responses from ActiLife to the
	 * console.
	 */
	@Override
	public void exceptionRaised(ExceptionEventObject o) {
		System.out.print("An exception occurred:\n\tmessage: "
				+ o.getException().getMessage() + "\n\tstacktrace: ");
		o.getException().printStackTrace(System.out);
	}

	/**
	 * Adds newly discovered devices to the device grid.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void deviceDiscovered(DeviceEventObject e) {
		if (e == null || e.getDevice() == null) {
			return;
		}
		((DeviceTableModel) table.getModel()).addOrUpdateRow(e.getDevice());
	}

	@Override
	public void gotRealTimeData(RealTimeSampleEventObject o) {
		if (o == null || o.getRealTimeSample() == null) {
			return;
		}
		wirelessRealTimeDialog.addSample(o.getRealTimeSample().getTime(), o
				.getRealTimeSample().getAxis1(), o.getRealTimeSample()
				.getAxis2(), o.getRealTimeSample().getAxis3(), o
				.getRealTimeSample().getLux(), o.getRealTimeSample().getHr());
	}
}