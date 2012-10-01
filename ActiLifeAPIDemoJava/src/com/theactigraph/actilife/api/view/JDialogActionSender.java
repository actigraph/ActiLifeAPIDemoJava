package com.theactigraph.actilife.api.view;

import java.awt.Frame;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.JDialog;

import com.google.gson.internal.StringMap;
import com.theactigraph.actilife.api.models.Action;
import com.theactigraph.actilife.api.models.events.ActionEventObject;
import com.theactigraph.actilife.api.models.events.IActionSenderListener;

@SuppressWarnings("serial")
public class JDialogActionSender extends JDialog {

	/**
	 * List of listeners for handling responses.
	 */
	protected transient Vector<IActionSenderListener> actionSenderListeners;

	public JDialogActionSender(Frame parent, boolean modal) {
		super(parent, modal);
	}

	/**
	 * Adds an event listener.
	 * 
	 * @param l
	 *            listener to be added
	 */
	synchronized public void addListener(IActionSenderListener l) {
		if (actionSenderListeners == null) {
			actionSenderListeners = new Vector<IActionSenderListener>();
		}
		actionSenderListeners.addElement(l);
	}

	/**
	 * Removes an event listener.
	 * 
	 * @param l
	 *            listener to be removed
	 */
	synchronized public void removeListener(IActionSenderListener l) {
		if (actionSenderListeners == null) {
			actionSenderListeners = new Vector<IActionSenderListener>();
		}
		actionSenderListeners.removeElement(l);
	}

	/**
	 * Requests an action to be performed for all who listen.
	 * 
	 * @param action
	 *            action to perform
	 */
	@SuppressWarnings("unchecked")
	protected void onActionRequested(Action action, StringMap args) {
		if (actionSenderListeners != null && !actionSenderListeners.isEmpty()) {
			ActionEventObject event = new ActionEventObject(this, action, args);
			Vector<IActionSenderListener> targets;
			synchronized (this) {
				targets = (Vector<IActionSenderListener>) actionSenderListeners
						.clone();
			}
			Enumeration<IActionSenderListener> e = targets.elements();
			while (e.hasMoreElements()) {
				IActionSenderListener l = (IActionSenderListener) e
						.nextElement();
				l.actionRequested(event);
			}
		}
	}
}
