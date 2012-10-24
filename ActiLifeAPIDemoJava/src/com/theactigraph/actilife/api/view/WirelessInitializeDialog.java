package com.theactigraph.actilife.api.view;

/**
 * Dialog to initialize a device. 
 * 
 * @author jeremy.moore
 */
@SuppressWarnings("serial")
public class WirelessInitializeDialog extends javax.swing.JDialog {
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
				//
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
