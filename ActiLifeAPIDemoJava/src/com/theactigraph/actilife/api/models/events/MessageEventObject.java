package com.theactigraph.actilife.api.models.events;

import java.util.EventObject;

@SuppressWarnings("serial")
public class MessageEventObject extends EventObject {
	
	private String message;
	
	public MessageEventObject(Object source, String message) {
		super(source);
		this.setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
