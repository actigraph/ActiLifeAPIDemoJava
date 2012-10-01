package com.theactigraph.actilife.api.models.events;

import java.util.EventObject;

import com.theactigraph.actilife.api.models.RealTimeSample;

@SuppressWarnings("serial")
public class RealTimeSampleEventObject extends EventObject {

	private RealTimeSample realTimeSample;

	public RealTimeSampleEventObject(Object source,
			RealTimeSample realTimeSample) {
		super(source);
		this.setRealTimeSample(realTimeSample);
	}

	public RealTimeSample getRealTimeSample() {
		return realTimeSample;
	}

	public void setRealTimeSample(RealTimeSample realTimeSample) {
		this.realTimeSample = realTimeSample;
	}
}
