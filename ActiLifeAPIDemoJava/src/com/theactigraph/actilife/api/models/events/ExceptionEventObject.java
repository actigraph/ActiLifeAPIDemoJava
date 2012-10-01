package com.theactigraph.actilife.api.models.events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class ExceptionEventObject extends EventObject {
	
	private Exception exception;
	
	public ExceptionEventObject(Object source, Exception exception) {
		super(source);
		this.setException(exception);
	}

	public Exception getException() {
		return exception;
	}

	public void setException(Exception exception) {
		this.exception = exception;
	}
}
