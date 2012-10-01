package com.theactigraph.actilife.api.models;

public class RealTimeSample {

	private String time;
	private float axis1;
	private float axis2;
	private float axis3;
	private float lux;
	private float hr;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public float getAxis1() {
		return axis1;
	}

	public void setAxis1(float axis1) {
		this.axis1 = axis1;
	}

	public float getAxis2() {
		return axis2;
	}

	public void setAxis2(float axis2) {
		this.axis2 = axis2;
	}

	public float getAxis3() {
		return axis3;
	}

	public void setAxis3(float axis3) {
		this.axis3 = axis3;
	}

	public float getLux() {
		return lux;
	}

	public void setLux(float lux) {
		this.lux = lux;
	}

	public float getHr() {
		return hr;
	}

	public void setHr(float hr) {
		this.hr = hr;
	}
}
