package com.theactigraph.actilife.api;

import com.theactigraph.actilife.api.controller.PipeController;
import com.theactigraph.actilife.api.view.MainForm;

public class Launcher {
	/**
	 * Launches the application.
	 * 
	 * @param args
	 *            the command line arguments
	 */
	public static void main(String args[]) {

		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				final MainForm view = new MainForm();
				final PipeController controller = new PipeController();

				// controller handles action requests from view
				view.addListener(controller);
				view.addWirelessRealTimeListener(controller);

				// view listens to responses from the controller
				controller.addListener(view);
				view.setLocationRelativeTo(null);
				view.setVisible(true);
			}
		});

	}
}
