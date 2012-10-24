package com.theactigraph.actilife.api.view;

import javax.swing.table.DefaultTableModel;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.Utils;
import com.theactigraph.actilife.api.models.Action;

/**
 * Dialog to show captured real time streaming data from a device.
 * 
 * @author jeremy.moore
 */
@SuppressWarnings("serial")
public class WirelessRealTimeDialog extends JDialogActionSender {

	/**
	 * Model for device table. Allows for adding records on demand.
	 */
	private DefaultTableModel tblRealTimeDataModel = new DefaultTableModel();
	/**
	 * Device being operated on.
	 */
	private String deviceAntId;

	private javax.swing.JButton btnClear;
	private javax.swing.JButton btnStart;
	private javax.swing.JButton btnStop;
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JTable tblRealTimeData;

	/**
	 * Creates new form RealTimeDialog
	 */
	public WirelessRealTimeDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);

		initComponents();
	}

	private void initComponents() {

		btnStart = new javax.swing.JButton();
		btnStop = new javax.swing.JButton();
		btnClear = new javax.swing.JButton();
		jScrollPane2 = new javax.swing.JScrollPane();
		tblRealTimeData = new javax.swing.JTable();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Real Time Streaming");
		setResizable(false);

		btnStart.setText("Start");
		btnStart.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (deviceAntId == null) {
					return;
				}
				StringMap args = new StringMap();
				args.put("device_ant_id", deviceAntId);
				onActionRequested(Action.WIRELESS_DEVICE_REALTIME_START, args);
			}
		});

		btnStop.setText("Stop");
		btnStop.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				if (deviceAntId == null) {
					return;
				}
				StringMap args = new StringMap();
				args.put("device_ant_id", deviceAntId);
				onActionRequested(Action.WIRELESS_DEVICE_REALTIME_STOP, args);
			}
		});

		btnClear.setText("Clear");
		btnClear.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// cache the current row count as it is updated fast
				int rows = tblRealTimeDataModel.getRowCount();
				for (int i = 0; i < rows; i++) {
					tblRealTimeDataModel.removeRow(0);
				}
			}
		});

		tblRealTimeDataModel = new javax.swing.table.DefaultTableModel(
				new Object[][] {}, new String[] { "Time", "Axis 1", "Axis 2",
						"Axis 3", "Lux", "Heartrate" }) {
			boolean[] canEdit = new boolean[] { false, false, false, false,
					false, false };

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		};

		tblRealTimeData.setModel(tblRealTimeDataModel);
		tblRealTimeData.getTableHeader().setReorderingAllowed(false);
		jScrollPane2.setViewportView(tblRealTimeData);
		tblRealTimeData.getColumnModel().getColumn(0).setResizable(false);
		tblRealTimeData.getColumnModel().getColumn(0).setPreferredWidth(200);
		tblRealTimeData.getColumnModel().getColumn(1).setResizable(false);
		tblRealTimeData.getColumnModel().getColumn(2).setResizable(false);
		tblRealTimeData.getColumnModel().getColumn(3).setResizable(false);
		tblRealTimeData.getColumnModel().getColumn(4).setResizable(false);
		tblRealTimeData.getColumnModel().getColumn(5).setResizable(false);

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		getContentPane().setLayout(layout);
		layout.setHorizontalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.LEADING)
												.addComponent(
														jScrollPane2,
														javax.swing.GroupLayout.DEFAULT_SIZE,
														570, Short.MAX_VALUE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		btnStart)
																.addGap(18, 18,
																		18)
																.addComponent(
																		btnStop)
																.addGap(18, 18,
																		18)
																.addComponent(
																		btnClear)
																.addGap(0,
																		0,
																		Short.MAX_VALUE)))
								.addContainerGap()));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addGroup(
										layout.createParallelGroup(
												javax.swing.GroupLayout.Alignment.BASELINE)
												.addComponent(btnStart)
												.addComponent(btnStop)
												.addComponent(btnClear))
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
								.addComponent(jScrollPane2,
										javax.swing.GroupLayout.PREFERRED_SIZE,
										279,
										javax.swing.GroupLayout.PREFERRED_SIZE)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
	}

	/**
	 * Informs this dialog of which device we are doing real time streaming for.
	 * If a new device is set, we should clear the text area.
	 * 
	 * @param deviceAntId
	 */
	public void setDeviceAntId(String deviceAntId) {
		if (this.deviceAntId != deviceAntId) {
			tblRealTimeData.removeAll();
			this.deviceAntId = deviceAntId;
		}
	}

	/**
	 * Adds a row to the samples table.
	 * 
	 * @param time
	 * @param axis1
	 * @param axis2
	 * @param axis3
	 * @param lux
	 * @param hr
	 */
	public void addSample(String time, float axis1, float axis2, float axis3,
			float lux, float hr) {
		tblRealTimeDataModel.addRow(new Object[] { time, Utils.round(axis1, 2),
				Utils.round(axis2, 2), Utils.round(axis3, 2),
				Utils.round(lux, 2), Utils.round(hr, 2) });
	}
}