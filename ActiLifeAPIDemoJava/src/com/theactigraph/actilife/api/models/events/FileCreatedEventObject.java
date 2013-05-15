package com.theactigraph.actilife.api.models.events;

import java.util.EventObject;

import com.theactigraph.actilife.api.models.Action;

public class FileCreatedEventObject extends EventObject {
	
	private static final long serialVersionUID = 1L;
	private Action action;
	private String path;

	public FileCreatedEventObject(Object source, Action action, String path) {
		super(source);
		this.setAction(action);
		this.setPath(path);
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
