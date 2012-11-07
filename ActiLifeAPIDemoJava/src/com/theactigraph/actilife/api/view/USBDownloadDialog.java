package com.theactigraph.actilife.api.view;

import java.io.File;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.api.models.Action;

/**
 * Dialog to download a device to a file.
 * 
 * @author jeremy.moore
 */
@SuppressWarnings("serial")
public class USBDownloadDialog extends JDialogActionSender {
	/**
	 * Device being operated on.
	 */
	private String deviceSerial;

	private javax.swing.JButton btnDownload;

	/**
	 * Creates new form NewJDialog
	 */
	public USBDownloadDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	private void initComponents() {

		btnDownload = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Download");
        setResizable(false);

        btnDownload.setText("Download");
        btnDownload.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				JFileChooser fileopen = new JFileChooser();
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
					args.put("Serial", deviceSerial);
					args.put("FileUseMetricUnits", "false");
					args.put("FileFormat", formats);
					args.put("FileOutputPath", file.getPath());
					args.put("AGDOptions", agdOptions);
					args.put("BioData", bioData);
					onActionRequested(Action.USB_DOWNLOAD, args);
				}
			}
		});

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(57, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                		.addComponent(btnDownload))
                .addGap(54, 54, 54))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(34, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDownload)
                .addGap(37, 37, 37))
        );

        pack();
	}
	
	/**
	 * Informs this dialog of which device we are doing a download for.
	 * 
	 * @param deviceSerial
	 */
	public void setDeviceSerial(String deviceSerial) {
		if (this.deviceSerial != deviceSerial) {
			this.deviceSerial = deviceSerial;
		}
	}
}
