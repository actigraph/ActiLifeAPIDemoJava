package com.theactigraph.actilife.api.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.Utils;
import com.theactigraph.actilife.api.models.Action;
import com.theactigraph.actilife.api.models.events.DeviceEventObject;
import com.theactigraph.actilife.api.models.events.ExceptionEventObject;
import com.theactigraph.actilife.api.models.events.IActionSenderListener;
import com.theactigraph.actilife.api.models.events.IResponseHandlerListener;
import com.theactigraph.actilife.api.models.events.MessageEventObject;
import com.theactigraph.actilife.api.models.events.RealTimeSampleEventObject;

@SuppressWarnings("serial")
public class MainForm extends JFrameActionSender implements
		IResponseHandlerListener {
	/**
	 * Model for device table. Allows for adding records on demand.
	 */
	private DefaultTableModel _tblDevicesModel = new DefaultTableModel();
	/**
	 * A reference to this form for use with JDialogs.
	 */
	private JFrame _this;
	/**
	 * A dialog to run non-modal to show actions and responses.
	 */
	private DebugDialog _debugDialog;
	/**
	 * A dialog to show real time data for a wireless device.
	 */
	private RealTimeDialog _wirelessRealTimeDialog;
	/**
	 * A dialog to download X minutes of raw device data via wireless.
	 */
	private BurstDialog _wirelessBurstDialog;
	/**
	 * ANT ID of the last selected device.
	 */
	private String _lastSelectedDeviceAntId;
	/**
	 * UI components;
	 */
	private javax.swing.JButton btnDebugConsole;
	private javax.swing.JButton btnLaunchActiLife;
	private javax.swing.JButton btnMinimizeActiLife;
	private javax.swing.JButton btnRestoreActiLife;
	private javax.swing.JButton btnWirelessBurstDialog;
	private javax.swing.JButton btnWirelessIdentifyDialog;
	private javax.swing.JButton btnWirelessInitializeDialog;
	private javax.swing.JButton btnWirelessRealTimeDialog;
	private javax.swing.JButton btnWirelessScanStart;
	private javax.swing.JButton btnWirelessScanStop;
	private javax.swing.JButton btnWirelessClearList;
	private javax.swing.JPanel jPanel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JTabbedPane jTabbedPane1;
	private javax.swing.JTable tblDevices;

	/**
	 * Application must be started with a reference to a pipe for communicating
	 * with ActiLife.
	 */
	public MainForm() {
		_this = this;
		initComponents();
		updateUI();
	}

	/**
	 * Helper method to refresh UI state based on current UI state.
	 */
	private void updateUI() {
		Boolean rowSelected = (tblDevices.getSelectedRow() != -1);
		btnWirelessBurstDialog.setEnabled(rowSelected);
		btnWirelessIdentifyDialog.setEnabled(rowSelected);
		btnWirelessRealTimeDialog.setEnabled(rowSelected);
		btnWirelessInitializeDialog.setEnabled(rowSelected);
	}

	/**
	 * Builds the GUI. Initially ported from Netbeans. Manually modified as
	 * needed.
	 */
	private void initComponents() {

		_debugDialog = new DebugDialog(_this, false);
		_debugDialog.setLocationRelativeTo(_this);

		_wirelessRealTimeDialog = new RealTimeDialog(_this, false);
		_wirelessRealTimeDialog.setLocationRelativeTo(_this);

		_wirelessBurstDialog = new BurstDialog(_this, false);
		_wirelessBurstDialog.setLocationRelativeTo(_this);

		btnDebugConsole = new javax.swing.JButton();
		btnLaunchActiLife = new javax.swing.JButton();
		btnMinimizeActiLife = new javax.swing.JButton();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel1 = new javax.swing.JPanel();
		jScrollPane1 = new javax.swing.JScrollPane();
		tblDevices = new javax.swing.JTable();
		btnWirelessRealTimeDialog = new javax.swing.JButton();
		btnWirelessInitializeDialog = new javax.swing.JButton();
		btnWirelessIdentifyDialog = new javax.swing.JButton();
		btnWirelessBurstDialog = new javax.swing.JButton();
		btnWirelessScanStart = new javax.swing.JButton();
		btnWirelessScanStop = new javax.swing.JButton();
		btnWirelessClearList = new javax.swing.JButton();
		btnRestoreActiLife = new javax.swing.JButton();

		setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
		setTitle("ActiLife API Demo");
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		btnDebugConsole.setText("Debug Console");
		btnDebugConsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_debugDialog != null) {
					_debugDialog.setVisible(!_debugDialog.isShowing());
				}
			}
		});

		btnLaunchActiLife.setText("Launch ActiLife");
		btnLaunchActiLife.addActionListener(new ActionListener() {
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

		btnMinimizeActiLife.setText("Minimize ActiLife");
		btnMinimizeActiLife.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.ACTILIFE_MINIMIZE, null);
			}
		});

		btnWirelessRealTimeDialog.setText("Real Time");
		btnWirelessRealTimeDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_wirelessRealTimeDialog != null) {
					_wirelessRealTimeDialog
							.setDeviceAntId(_lastSelectedDeviceAntId);
					_wirelessRealTimeDialog.setVisible(true);
				}
			}
		});

		btnWirelessInitializeDialog.setText("Initialize");
		btnWirelessInitializeDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				InitializeDialog d;
				d = new InitializeDialog(_this, true);
				d.setLocationRelativeTo(_this);
				d.setVisible(true);
			}
		});

		btnWirelessIdentifyDialog.setText("Identify");
		btnWirelessIdentifyDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StringMap args = new StringMap();
				args.put("device_ant_id", _lastSelectedDeviceAntId);
				onActionRequested(Action.WIRELESS_DEVICE_IDENTIFY, args);
			}
		});

		btnWirelessBurstDialog.setText("Burst");
		btnWirelessBurstDialog.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (_wirelessBurstDialog != null) {
					_wirelessBurstDialog
							.setDeviceAntId(_lastSelectedDeviceAntId);
					_wirelessBurstDialog.setVisible(true);
				}
			}
		});

		btnWirelessScanStart.setText("Start Scan");
		btnWirelessScanStart.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.WIRELESS_SCAN_START, null);
				updateUI();
			}
		});

		btnWirelessScanStop.setText("Stop Scan");
		btnWirelessScanStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.WIRELESS_SCAN_STOP, null);
				updateUI();
			}
		});

		btnWirelessClearList.setText("Clear List");
		btnWirelessClearList.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				while (_tblDevicesModel.getRowCount() > 0) {
					_tblDevicesModel.removeRow(0);
				}
			}
		});

		btnRestoreActiLife.setText("Restore ActiLife");
		btnRestoreActiLife.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onActionRequested(Action.ACTILIFE_RESTORE, null);
			}
		});

		_tblDevicesModel = new javax.swing.table.DefaultTableModel(
				new Object[][] {

				}, new String[] { "ANT ID", "Serial Number", "Status",
						"Subject Name", "Battery" }) {
			boolean[] canEdit = new boolean[] { false, false, false, false,
					false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};
		tblDevices.setModel(_tblDevicesModel);
		tblDevices.getSelectionModel().addListSelectionListener(
				new javax.swing.event.ListSelectionListener() {
					public void valueChanged(ListSelectionEvent event) {
						try {
							_lastSelectedDeviceAntId = _tblDevicesModel
									.getValueAt(tblDevices.getSelectedRow(), 0)
									.toString();
						} catch (ArrayIndexOutOfBoundsException e) {
							// TODO why does this happen?
						}
						updateUI();
					}
				});

		jScrollPane1.setViewportView(tblDevices);
		tblDevices.getColumnModel().getColumn(0).setResizable(false);
		tblDevices.getColumnModel().getColumn(1).setResizable(false);
		tblDevices.getColumnModel().getColumn(2).setResizable(false);
		tblDevices.getColumnModel().getColumn(3).setResizable(false);
		tblDevices.getColumnModel().getColumn(4).setResizable(false);

		org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(
				jPanel1);
		jPanel1.setLayout(jPanel1Layout);
		jPanel1Layout
				.setHorizontalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel1Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.LEADING)
										.add(jScrollPane1,
												org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
												758, Short.MAX_VALUE)
										.add(jPanel1Layout
												.createSequentialGroup()
												.add(btnWirelessScanStart)
												.add(18, 18, 18)
												.add(btnWirelessScanStop)
												.add(18, 18, 18)
												.add(btnWirelessClearList)
												.addPreferredGap(
														org.jdesktop.layout.LayoutStyle.RELATED,
														org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
														Short.MAX_VALUE)
												.add(btnWirelessInitializeDialog)
												.add(18, 18, 18)
												.add(btnWirelessIdentifyDialog)
												.add(18, 18, 18)
												.add(btnWirelessRealTimeDialog)
												.add(18, 18, 18)
												.add(btnWirelessBurstDialog)))
								.addContainerGap()));
		jPanel1Layout
				.setVerticalGroup(jPanel1Layout
						.createParallelGroup(
								org.jdesktop.layout.GroupLayout.LEADING)
						.add(jPanel1Layout
								.createSequentialGroup()
								.addContainerGap()
								.add(jPanel1Layout
										.createParallelGroup(
												org.jdesktop.layout.GroupLayout.BASELINE)
										.add(btnWirelessRealTimeDialog)
										.add(btnWirelessInitializeDialog)
										.add(btnWirelessIdentifyDialog)
										.add(btnWirelessBurstDialog)
										.add(btnWirelessScanStart)
										.add(btnWirelessScanStop)
										.add(btnWirelessClearList))
								.addPreferredGap(
										org.jdesktop.layout.LayoutStyle.UNRELATED)
								.add(jScrollPane1,
										org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
										298, Short.MAX_VALUE).addContainerGap()));

		jTabbedPane1.addTab("Wireless", jPanel1);

		org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.LEADING)
								.add(jTabbedPane1,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
										783,
										org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
								.add(layout.createSequentialGroup()
										.add(btnDebugConsole).add(18, 18, 18)
										.add(btnLaunchActiLife).add(18, 18, 18)
										.add(btnMinimizeActiLife)
										.add(18, 18, 18)
										.add(btnRestoreActiLife)))
						.addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
				.add(layout
						.createSequentialGroup()
						.addContainerGap()
						.add(layout
								.createParallelGroup(
										org.jdesktop.layout.GroupLayout.BASELINE)
								.add(btnDebugConsole).add(btnLaunchActiLife)
								.add(btnMinimizeActiLife)
								.add(btnRestoreActiLife))
						.addPreferredGap(
								org.jdesktop.layout.LayoutStyle.UNRELATED)
						.add(jTabbedPane1,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE,
								382,
								org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
						.addContainerGap(
								org.jdesktop.layout.GroupLayout.DEFAULT_SIZE,
								Short.MAX_VALUE)));

		pack();
	}

	/**
	 * Adds an event listener for nested dialogs.
	 * 
	 * @param l
	 *            listener to be added
	 */
	synchronized public void addWirelessRealTimeListener(IActionSenderListener l) {
		if (_wirelessRealTimeDialog != null) {
			_wirelessRealTimeDialog.addListener(l);
		}
		if (_wirelessBurstDialog != null) {
			_wirelessBurstDialog.addListener(l);
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
		if (_debugDialog == null || o == null || o.getMessage() == null) {
			return;
		}
		_debugDialog.appendText(o.getMessage());
		if (!o.getMessage().endsWith("\n")) {
			_debugDialog.appendText("\n");
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
		Boolean updated = false;
		float roundedBattery = Utils.round(e.getDevice().getBattery(), 2);
		Iterator rowIterator = _tblDevicesModel.getDataVector().iterator();
		while (rowIterator.hasNext()) {
			Vector<String> cols = (Vector<String>) rowIterator.next();
			if (e.getDevice() != null)
				if (cols.get(0).equals(e.getDevice().getAntId())) {
					updated = true;
					cols.set(1, e.getDevice().getSerial());
					cols.set(2, e.getDevice().getStatus());
					cols.set(3, e.getDevice().getSubject());
					cols.set(4, roundedBattery + "v");
				}
		}
		if (!updated) {
			_tblDevicesModel.addRow(new Object[] { e.getDevice().getAntId(),
					e.getDevice().getSerial(), e.getDevice().getStatus(),
					e.getDevice().getSubject(), roundedBattery + "v" });
		}
		_tblDevicesModel.fireTableDataChanged();
	}

	@Override
	public void gotRealTimeData(RealTimeSampleEventObject o) {
		if (o == null || o.getRealTimeSample() == null) {
			return;
		}
		_wirelessRealTimeDialog.addSample(o.getRealTimeSample().getTime(), o
				.getRealTimeSample().getAxis1(), o.getRealTimeSample()
				.getAxis2(), o.getRealTimeSample().getAxis3(), o
				.getRealTimeSample().getLux(), o.getRealTimeSample().getHr());
	}
}