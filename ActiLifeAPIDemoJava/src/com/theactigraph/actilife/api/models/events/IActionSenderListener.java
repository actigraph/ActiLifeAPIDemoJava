package com.theactigraph.actilife.api.models.events;

import java.util.EventListener;

public interface IActionSenderListener extends EventListener {
	public void actionRequested(ActionEventObject o);
}
