package com.theactigraph.actilife.api.view;

import java.text.SimpleDateFormat;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.Utils;
import com.theactigraph.actilife.api.models.Action;

/**
 * Dialog to initialize a device. 
 * 
 * @author jeremy.moore
 */
@SuppressWarnings("serial")
public class WirelessInitializeDialog extends JDialogActionSender {
	/**
	 * Device being operated on.
	 */
	private String deviceAntId;

	private javax.swing.JButton jButton1;
	private javax.swing.JLabel jLabel1;

	/**
	 * Creates new form InitializeDialog
	 */
	public WirelessInitializeDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	private void initComponents() {

		jButton1 = new javax.swing.JButton();
		jLabel1 = new javax.swing.JLabel();

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Initialize");
		setResizable(false);

		jButton1.setText("Initialize");
		jButton1.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				// init options
				StringMap initOptions = new StringMap();
				initOptions.put("startdatetime", Utils.ToISO8601Date(Utils.getDateAddMinutesFromNow(2)));
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
				args.put("AntID", deviceAntId);
				args.put("BioData", bioData);
				args.put("InitOptions", initOptions);
				onActionRequested(Action.WIRELESS_INITIALIZE, args);
			}
		});

		jLabel1.setText("Values are hardcoded.");

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
												.addComponent(jLabel1)
												.addComponent(jButton1))
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));
		layout.setVerticalGroup(layout
				.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
				.addGroup(
						layout.createSequentialGroup()
								.addContainerGap()
								.addComponent(jLabel1)
								.addPreferredGap(
										javax.swing.LayoutStyle.ComponentPlacement.RELATED)
								.addComponent(jButton1)
								.addContainerGap(
										javax.swing.GroupLayout.DEFAULT_SIZE,
										Short.MAX_VALUE)));

		pack();
	}

	/**
	 * Informs this dialog of which device we are talking to.
	 * 
	 * @param deviceAntId
	 */
	public void setDeviceAntId(String deviceAntId) {
		if (this.deviceAntId != deviceAntId) {
			this.deviceAntId = deviceAntId;
		}
	}
}
