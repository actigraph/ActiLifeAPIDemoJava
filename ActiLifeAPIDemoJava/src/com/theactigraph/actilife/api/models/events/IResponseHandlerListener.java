package com.theactigraph.actilife.api.models.events;

import java.util.EventListener;

public interface IResponseHandlerListener extends EventListener {
	public void messageToDisplay(MessageEventObject o);

	public void messageToDebug(MessageEventObject o);

	public void exceptionRaised(ExceptionEventObject o);

	public void deviceDiscovered(DeviceEventObject o);

	public void gotRealTimeData(RealTimeSampleEventObject o);
	
	public void fileCreated(FileCreatedEventObject o);
}
