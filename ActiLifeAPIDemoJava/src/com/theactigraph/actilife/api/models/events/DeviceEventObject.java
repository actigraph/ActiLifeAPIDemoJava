package com.theactigraph.actilife.api.models.events;

import java.util.EventObject;

import com.theactigraph.actilife.api.models.Device;

@SuppressWarnings("serial")
public class DeviceEventObject extends EventObject {
	
	private Device device;
	
	public DeviceEventObject(Object source, Device device) {
		super(source);
		this.setDevice(device);
	}

	public Device getDevice() {
		return device;
	}

	public void setDevice(Device device) {
		this.device = device;
	}
}
