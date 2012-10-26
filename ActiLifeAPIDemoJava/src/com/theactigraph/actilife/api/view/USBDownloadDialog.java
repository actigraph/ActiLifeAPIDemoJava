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
					agdOptions.put("axis", "3");
					agdOptions.put("steps", "true");
					agdOptions.put("lux", "true");
					agdOptions.put("hr", "true");
					agdOptions.put("inclonometer", "true");
					// bio data
					StringMap bioData = new StringMap();
					bioData.put("subjectname", "John Doe");
					bioData.put("sex", "Male");
					bioData.put("height", "182.9"); //cm
					bioData.put("weight", "175.8"); //lb
					bioData.put("age", "32");
					bioData.put("race", "White / Caucasian");
					bioData.put("dateofbirth", "07/15/1980");
					bioData.put("limb", "Waist");
					bioData.put("side", "Right");
					bioData.put("dominance", "Dominant");
					// args
					StringMap args = new StringMap();
					args.put("device_serial", deviceSerial);
					args.put("file_use_metric_units", "false");
					args.put("file_format", formats);
					args.put("file_output_path", file.getPath());
					args.put("agd_options", agdOptions);
					args.put("bio_data", bioData);
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
