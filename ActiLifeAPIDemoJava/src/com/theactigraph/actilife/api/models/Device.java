package com.theactigraph.actilife.api.models;

public class Device {
	private String antId;
	private String serial;
	private String type;
	private String status;
	private String subject;
	private float battery;

	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public float getBattery() {
		return battery;
	}

	public void setBattery(float battery) {
		this.battery = battery;
	}

	public String getAntId() {
		return antId;
	}

	public void setAntId(String antId) {
		this.antId = antId;
	}
}
