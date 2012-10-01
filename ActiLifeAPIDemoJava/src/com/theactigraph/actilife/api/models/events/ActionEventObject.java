package com.theactigraph.actilife.api.models.events;

import java.util.EventObject;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.api.models.Action;

@SuppressWarnings("serial")
public class ActionEventObject extends EventObject {
	
	private Action action;
	
	private StringMap args;
	
	public ActionEventObject(Object source, Action action, StringMap args) {
		super(source);
		this.setAction(action);
		this.setArgs(args);
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public StringMap getArgs() {
		return args;
	}

	public void setArgs(StringMap args) {
		this.args = args;
	}
}
