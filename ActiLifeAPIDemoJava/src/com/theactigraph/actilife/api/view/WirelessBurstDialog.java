package com.theactigraph.actilife.api.view;

import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.api.models.Action;

/**
 * Dialog to burst X minutes of data to a file.
 * 
 * @author jeremy.moore
 */
@SuppressWarnings("serial")
public class WirelessBurstDialog extends JDialogActionSender {
	/**
	 * Device being operated on.
	 */
	private String deviceAntId;

	private javax.swing.JButton btnBurst;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JSpinner spnMinutes;

	/**
	 * Creates new form NewJDialog
	 */
	public WirelessBurstDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	private void initComponents() {

        btnBurst = new javax.swing.JButton();
        spnMinutes = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Burst");
        setResizable(false);

        btnBurst.setText("Burst");
		btnBurst.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JFileChooser fileopen = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter(".gt3x files", "gt3x");
				fileopen.addChoosableFileFilter(filter);
				int ret = fileopen.showSaveDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					StringMap args = new StringMap();
					args.put("device_ant_id", deviceAntId);
					args.put("minutes", spnMinutes.getValue().toString());
					args.put("file_use_metric_units", "false");
					args.put("file_format", "agd");
					args.put("file_output_path", file.getPath());
					onActionRequested(Action.WIRELESS_DEVICE_BURST, args);
				}
			}
		});
		
		spnMinutes.setValue(5);

        jLabel1.setText("Minutes to download:");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnBurst)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spnMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(54, 54, 54))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnMinutes, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnBurst)
                .addGap(37, 37, 37))
        );

        pack();
	}


	/**
	 * Informs this dialog of which device we are doing a burst download for.
	 * If a new device is set, we should clear the text area.
	 * 
	 * @param deviceAntId
	 */
	public void setDeviceAntId(String deviceAntId) {
		if (this.deviceAntId != deviceAntId) {
			this.deviceAntId = deviceAntId;
		}
	}
}
