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
				SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
				initOptions.put("startdatetime", format.format(Utils.getDateAddMinutesFromNow(5)));
				// no stopdatetime
				initOptions.put("samplerate", "40");
				initOptions.put("axis", "3");
				initOptions.put("steps", "true");
				initOptions.put("inclinometer", "true");
				initOptions.put("flashledwhileactive", "false");
				initOptions.put("flashledindelay", "true");
				initOptions.put("heartrate", "false");
				initOptions.put("lux", "true");
				initOptions.put("disablesleepmode", "true");
				initOptions.put("antwireless", "true");
				initOptions.put("antwidatasummaryreless", "true");
				// bio data
				StringMap bioData = new StringMap();
				bioData.put("subjectname", "John Doe");
				bioData.put("sex", "Male");
				bioData.put("height", "182.9"); // cm
				bioData.put("weight", "175.8"); // lb
				bioData.put("age", "32");
				bioData.put("race", "White / Caucasian");
				bioData.put("dateofbirth", "07/15/1980");
				bioData.put("limb", "Waist");
				bioData.put("side", "Right");
				bioData.put("dominance", "Dominant");
				// args
				StringMap args = new StringMap();
				args.put("device_ant_id", deviceAntId);
				args.put("file_use_metric_units", "false");
				args.put("file_format", "agd");
				args.put("bio_data", bioData);
				args.put("init_options", initOptions);
				onActionRequested(Action.WIRELESS_DEVICE_INITIALIZE, args);
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
