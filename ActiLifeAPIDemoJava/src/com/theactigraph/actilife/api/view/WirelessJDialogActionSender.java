package com.theactigraph.actilife.api.view;

import java.awt.Frame;

public class WirelessJDialogActionSender extends JDialogActionSender {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Device being operated on.
	 */
	protected String deviceAntId;
	
	protected String antPin;

	public WirelessJDialogActionSender(Frame parent, boolean modal) {
		super(parent, modal);
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

	public void setAntPin(String antPin) {
		this.antPin = antPin;
	}

}
