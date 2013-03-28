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
public class WirelessBurstDialog extends WirelessJDialogActionSender {
	
	private javax.swing.JButton btnBurst;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JSpinner spnMinutes;

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

					StringBuilder path = new StringBuilder(file.getPath());
					if (!file.getPath().endsWith(".gt3x"))
						path.append(".gt3x");
					
					StringMap args = new StringMap();
					args.put("AntID", deviceAntId);
					args.put("AntPIN", antPin);
					args.put("Minutes", spnMinutes.getValue().toString());
					args.put("FileUseMetricUnits", "false");
					args.put("FileFormat", "agd");
					args.put("FileOutputPath", path);
					onActionRequested(Action.WIRELESS_BURST, args);
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
}
