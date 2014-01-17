package com.theactigraph.actilife.api.view;

import javax.swing.filechooser.FileSystemView;
import javax.swing.table.DefaultTableModel;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.Utils;
import com.theactigraph.actilife.api.models.Action;

import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout.Alignment;
import javax.swing.JComboBox;
import javax.swing.GroupLayout;
import javax.swing.JFileChooser;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileFilter;
import java.util.Vector;

/**
 * Dialog to show captured real time streaming data from a device.
 * 
 * @author jeremy.moore
 */
@SuppressWarnings("serial")
public class ConvertFileDialog extends JDialogActionSender {

	private javax.swing.JButton btnConvert;
	private JTextField txtPath;
	private String _outputType;

	public ConvertFileDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
	}

	private void initComponents() {
		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Convert File");
		setResizable(false);
		
		txtPath = new JTextField();
		txtPath.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				//default to desktop: http://fdegrelle.over-blog.com/article-11163566.html
				FileSystemView fsv = FileSystemView.getFileSystemView();
				JFileChooser fileopen = new JFileChooser(fsv.getRoots()[0]);
				int ret = fileopen.showSaveDialog(null);
				if (ret == JFileChooser.APPROVE_OPTION) {
					File file = fileopen.getSelectedFile();
					txtPath.setText(file.getPath());
				}
			}
		});

	    Vector comboBoxItems=new Vector();
	    comboBoxItems.add("rawcsv");
	    final DefaultComboBoxModel model = new DefaultComboBoxModel(comboBoxItems);
		JComboBox cmbOutputType = new JComboBox(model);
		_outputType = "rawcsv";
		cmbOutputType.addItemListener(new ItemListener() {	
			@Override
			public void itemStateChanged(ItemEvent e) {
				_outputType = e.getItem().toString();
			}
		});

		btnConvert = new javax.swing.JButton();
		btnConvert.setText("Convert");
		btnConvert.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				StringMap args = new StringMap();
				args.put("ForceOverwrite", false);
				args.put("FileOutputFormat", _outputType);
				args.put("FileInputPath", txtPath.getText());
				args.put("FileOutputPath", txtPath.getText() + ".csv");
				StringMap csvoptions = new StringMap();
				csvoptions.put("IncludeTimestamps", true);
				csvoptions.put("IncludeColumnHeaders", true);
				csvoptions.put("IncludeMetadata", true);
				args.put("csvoptions", csvoptions);
				onActionRequested(Action.CONVERT_FILE, args);
			}
		});
		
		JLabel lblOutputType = new JLabel("Output Type");

		javax.swing.GroupLayout layout = new javax.swing.GroupLayout(
				getContentPane());
		layout.setHorizontalGroup(
			layout.createParallelGroup(Alignment.TRAILING)
				.addGroup(layout.createSequentialGroup()
					.addGap(28)
					.addGroup(layout.createParallelGroup(Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
							.addComponent(txtPath, GroupLayout.PREFERRED_SIZE, 417, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(btnBrowse)
							.addGap(18))
						.addGroup(layout.createSequentialGroup()
							.addComponent(lblOutputType)
							.addGap(18)
							.addComponent(cmbOutputType, GroupLayout.PREFERRED_SIZE, 113, GroupLayout.PREFERRED_SIZE)
							.addPreferredGap(ComponentPlacement.RELATED)
							.addComponent(btnConvert)
							.addGap(173))))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup(Alignment.LEADING)
				.addGroup(layout.createSequentialGroup()
					.addGap(33)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(txtPath, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(btnBrowse))
					.addGap(29)
					.addGroup(layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(btnConvert)
						.addComponent(lblOutputType)
						.addComponent(cmbOutputType, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addContainerGap(196, Short.MAX_VALUE))
		);
		getContentPane().setLayout(layout);

		pack();
	}
}