package com.theactigraph.actilife.api.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.Utils;
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

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import java.awt.Font;
import java.awt.Color;

@SuppressWarnings("serial")
public class MainForm extends JFrameActionSender implements
		IResponseHandlerListener {

	private JTable table;
	private WirelessRealTimeDialog wirelessRealTimeDialog;
	private WirelessBurstDialog wirelessBurstDialog;

	private JFrame refToThis;
	private String lastSelectedDeviceAntId;
	private String lastSelectedDeviceSerial;

	private JButton btnInitialize;
	private JButton btnWirelessBurst;
	private JButton btnWirelessInitialize;
	private JButton btnWirelessIdentify;
	private JButton btnIdentify;
	private JButton btnDownload;
	private JButton btnWirelessRealTime;
	private JButton btnClearList;
	private JTextField txtPIN;
	private JTextArea txtDebug;

	public MainForm() {
		setTitle("ActiLife API Demo");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1100, 600);

		refToThis = this;

		wirelessRealTimeDialog = new WirelessRealTimeDialog(refToThis, false);
		wirelessRealTimeDialog.setLocationRelativeTo(refToThis);

		wirelessBurstDialog = new WirelessBurstDialog(refToThis, false);
		wirelessBurstDialog.setLocationRelativeTo(refToThis);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

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

		JPanel pnlNorth = new JPanel();
		FlowLayout fl_pnlNorth = (FlowLayout) pnlNorth.getLayout();
		fl_pnlNorth.setAlignment(FlowLayout.LEFT);
		getContentPane().add(pnlNorth, BorderLayout.NORTH);
		
		JLabel lblUsb = new JLabel("USB");
		pnlNorth.add(lblUsb);
		
		JButton btnList = new JButton("List");
		btnList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				onActionRequested(Action.USB_LIST, null);
			}
		});
		pnlNorth.add(btnList);

		btnIdentify = new JButton("Identify");
		pnlNorth.add(btnIdentify);
		btnIdentify.setEnabled(false);
		btnIdentify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringMap args = new StringMap();
				args.put("Serial", lastSelectedDeviceSerial);
				onActionRequested(Action.USB_IDENTIFY, args);
			}
		});

		btnInitialize = new JButton("Initialize");
		pnlNorth.add(btnInitialize);
		btnInitialize.setEnabled(false);
		btnInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// init options
				StringMap initOptions = new StringMap();
				initOptions.put("startdatetime", Utils.getUTCNowPlusMinutes(1));
				// no stopdatetime
				initOptions.put("SampleRate", "40");
				initOptions.put("Axis", "3");
				initOptions.put("Steps", "true");
				initOptions.put("Inclinometer", "true");
				initOptions.put("FlashLEDWhileActive", "false");
				initOptions.put("FlashLEDInDelay", "true");
				initOptions.put("HeartRate", "false");
				initOptions.put("Lux", "true");
				initOptions.put("DisableSleepMode", "true");
				initOptions.put("AntWireless", "true");
				initOptions.put("DataSummary", "true");
				// bio data
				StringMap bioData = new StringMap();
				bioData.put("SubjectName", "John Doe");
				bioData.put("Sex", "Male");
				bioData.put("Height", "182.9"); // cm
				bioData.put("Weight", "175.8"); // lb
				bioData.put("Age", "32");
				bioData.put("Race", "White / Caucasian");
				bioData.put("DateOfBirth", "1980-01-01T13:00:00Z");
				bioData.put("Limb", "Waist");
				bioData.put("Side", "Right");
				bioData.put("Dominance", "Dominant");
				// args
				StringMap args = new StringMap();
				args.put("Serial", lastSelectedDeviceSerial);
				args.put("FileUseMetricUnits", "false");
				args.put("FileFormat", "agd");
				args.put("BioData", bioData);
				args.put("InitOptions", initOptions);
				onActionRequested(Action.USB_INITIALIZE, args);
			}
		});

		btnDownload = new JButton("Download");
		pnlNorth.add(btnDownload);
		btnDownload.setEnabled(false);
		btnDownload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//default to desktop: http://fdegrelle.over-blog.com/article-11163566.html
				FileSystemView fsv = FileSystemView.getFileSystemView();
				JFileChooser fileopen = new JFileChooser(fsv.getRoots()[0]);
				FileFilter filter = new FileNameExtensionFilter(".agd files", "AGD");
				fileopen.addChoosableFileFilter(filter);
				int ret = fileopen.showSaveDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					// output formats
					LinkedList formats = new LinkedList();
					formats.add("agd");
					formats.add("gt3x");
					// agd options
					StringMap agdOptions = new StringMap();
					agdOptions.put("Axis", "3");
					agdOptions.put("Steps", "true");
					agdOptions.put("Lux", "true");
					agdOptions.put("HR", "true");
					agdOptions.put("Inclonometer", "true");
					agdOptions.put("EpochLengthInSeconds", "1");
					// bio data
					StringMap bioData = new StringMap();
					bioData.put("SubjectName", "John Doe");
					bioData.put("Sex", "Male");
					bioData.put("Height", "182.9"); //cm
					bioData.put("Weight", "175.8"); //lb
					bioData.put("Age", "32");
					bioData.put("Race", "White / Caucasian");
					bioData.put("DateOfBirth", "07/15/1980");
					bioData.put("Limb", "Waist");
					bioData.put("Side", "Right");
					bioData.put("Dominance", "Dominant");
					// args
					StringMap args = new StringMap();
					args.put("Serial", lastSelectedDeviceSerial);
					args.put("FileUseMetricUnits", "false");
					args.put("FileFormat", formats);
					args.put("FileOutputPath", file.getPath());
					args.put("AGDOptions", agdOptions);
					args.put("BioData", bioData);
					onActionRequested(Action.USB_DOWNLOAD, args);
				}
			}
		});

		JSeparator separator_2 = new JSeparator();
		pnlNorth.add(separator_2);

		btnWirelessIdentify = new JButton("Identify");
		btnWirelessIdentify.setEnabled(false);
		btnWirelessIdentify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				StringMap args = new StringMap();
				args.put("AntID", lastSelectedDeviceAntId);
				args.put("AntPIN", txtPIN.getText());
				onActionRequested(Action.WIRELESS_IDENTIFY, args);
			}
		});
		
		JLabel lblWireless = new JLabel("Wireless");
		pnlNorth.add(lblWireless);
		
		JButton btnWirelessStart = new JButton("Start");
		btnWirelessStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringMap args = new StringMap();
				args.put("AntPIN", txtPIN.getText());
				onActionRequested(Action.WIRELESS_SCAN_START, args);
				updateUI();
			}
		});
		pnlNorth.add(btnWirelessStart);
		
		JButton btnWirelessStop = new JButton("Stop");
		btnWirelessStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.WIRELESS_SCAN_STOP, null);
				updateUI();
			}
		});
		pnlNorth.add(btnWirelessStop);
		pnlNorth.add(btnWirelessIdentify);

		btnWirelessInitialize = new JButton("Initialize");
		btnWirelessInitialize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				// init options
				StringMap initOptions = new StringMap();
				initOptions.put("startdatetime", Utils.getUTCNowPlusMinutes(1));
				// no stopdatetime
				initOptions.put("SampleRate", "40");
				initOptions.put("Axis", "3");
				initOptions.put("Steps", "true");
				initOptions.put("Inclinometer", "true");
				initOptions.put("FlashLEDWhileActive", "false");
				initOptions.put("FlashLEDInDelay", "true");
				initOptions.put("HeartRate", "false");
				initOptions.put("Lux", "true");
				initOptions.put("DisableSleepMode", "true");
				initOptions.put("DataSummary", "true");
				// bio data
				StringMap bioData = new StringMap();
				bioData.put("SubjectName", "John Doe");
				bioData.put("Sex", "Male");
				bioData.put("Height", "182.9"); // cm
				bioData.put("Weight", "175.8"); // lb
				bioData.put("Age", "32");
				bioData.put("Race", "White / Caucasian");
				bioData.put("DateOfBirth", "1980-01-01T13:00:00Z");
				bioData.put("Limb", "Waist");
				bioData.put("Side", "Right");
				bioData.put("Dominance", "Dominant");
				// args
				StringMap args = new StringMap();
				args.put("AntID", lastSelectedDeviceAntId);
				args.put("AntPIN", txtPIN.getText());
				args.put("BioData", bioData);
				args.put("InitOptions", initOptions);
				onActionRequested(Action.WIRELESS_INITIALIZE, args);
			}
		});
		btnWirelessInitialize.setEnabled(false);
		pnlNorth.add(btnWirelessInitialize);

		btnWirelessBurst = new JButton("Burst");
		pnlNorth.add(btnWirelessBurst);
		btnWirelessBurst.setEnabled(false);
		btnWirelessBurst.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (wirelessBurstDialog != null) {
					wirelessBurstDialog.setDeviceAntId(lastSelectedDeviceAntId);
					wirelessBurstDialog.setAntPin(txtPIN.getText());
					wirelessBurstDialog.setVisible(true);
				}
			}
		});

		btnWirelessRealTime = new JButton("Real Time");
		pnlNorth.add(btnWirelessRealTime);
		btnWirelessRealTime.setEnabled(false);
		btnWirelessRealTime.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (wirelessRealTimeDialog != null) {
					wirelessRealTimeDialog
							.setDeviceAntId(lastSelectedDeviceAntId);
					wirelessRealTimeDialog.setAntPin(txtPIN.getText());
					wirelessRealTimeDialog.setVisible(true);
				}
			}
		});

		JSeparator separator = new JSeparator();
		pnlNorth.add(separator);

		JLabel lblPIN = new JLabel("Ant PIN");
		pnlNorth.add(lblPIN);

		txtPIN = new JTextField();
		pnlNorth.add(txtPIN);
		txtPIN.setText("0000");
		txtPIN.setColumns(4);

		JSeparator separator_1 = new JSeparator();
		pnlNorth.add(separator_1);

		btnClearList = new JButton("Clear List");
		btnClearList.setEnabled(false);
		pnlNorth.add(btnClearList);
		btnClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				((DeviceTableModel) table.getModel()).clear();
				updateUI();
			}
		});

		JScrollPane scrollPane = new JScrollPane();
		getContentPane().add(scrollPane, BorderLayout.CENTER);

		table = new JTable();
		scrollPane.setViewportView(table);
		table.setFillsViewportHeight(true);
		table.setModel(new DeviceTableModel());

		JScrollPane scrollPane_1 = new JScrollPane();
		getContentPane().add(scrollPane_1, BorderLayout.SOUTH);

		txtDebug = new JTextArea();
		txtDebug.setRows(15);
		txtDebug.setForeground(Color.GRAY);
		txtDebug.setFont(new Font("Courier New", Font.PLAIN, 12));
		scrollPane_1.setViewportView(txtDebug);
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
		btnWirelessIdentify.setEnabled(usb);
		btnWirelessInitialize.setEnabled(usb);
		btnWirelessBurst.setEnabled(wireless);
		btnWirelessRealTime.setEnabled(wireless);
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
		txtDebug.append(o.getMessage());
		if (!o.getMessage().endsWith("\n")) {
			txtDebug.append("\n");
		}
		txtDebug.setCaretPosition(txtDebug.getDocument().getLength());
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
		updateUI();
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