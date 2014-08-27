package ngat.rcsgui.stable;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.rmi.*;

import ngat.message.GUI_RCS.START;
import ngat.message.GUI_RCS.SYSTEM;
import ngat.message.base.COMMAND;
import ngat.message.base.COMMAND_DONE;
import ngat.net.IConnection;
import ngat.net.camp.CAMPClient;
import ngat.net.cil.*;
import ngat.opsgui.base.Resources;
import ngat.rcs.tmm.*;
import ngat.rcs.tms.TaskModeControllerManagement;
import ngat.ems.*;
import ngat.rcs.newstatemodel.*;

public class ToolMenu {

	public static final int WEATHER_CLEAR = 10;

	public static final int SEEING_GOOD = 1;

	public static final int SEEING_AVER = 2;

	public static final int SEEING_POOR = 3;

	public static final int SEEING_USAB = 4;

	public static final int PHOTOM = 1;

	public static final int SPECTRO = 2;

	String host;

	JFrame frame;

	JMenu menu;

	JMenuItem authItem;
	JMenuItem weatherClearItem;
	JMenuItem intentItem;
	JMenu seeMenu;
	JMenu photMenu;
	JMenuItem cmdItem;
	JMenu smItem;
	JMenu subSystemMenu;

	JMenuItem helpItem;

	// StateModel stateModel;
	// SkyModel skyModel;

	// TaskModeControllerManagement soca;

	CilService cilService;

	boolean authorized = false;

	/** Operational state. */
	private int cop;

	public ToolMenu(JFrame frame, String host) {
		this.frame = frame;
		this.host = host;
		menu = createMenu();

	}

	public JMenu getMenu() {
		return menu;
	}

	private JMenu createMenu() {

		JMenu menu = new JMenu("Control");

		ToolMenuListener xl = new ToolMenuListener();

		menu.add(new JLabel("Legacy Tools"));
		menu.addSeparator();

		// Authorization
		authItem = menu.add(createMenuItem("Authorization", xl, "auth"));
		authItem.setForeground(Color.red);

		// TCS command
		cmdItem = menu.add(createMenuItem("TCS Command", xl, "command"));

		// weather override
		weatherClearItem = menu.add(createMenuItem("Weather Override", xl,
				"weather-override"));

		// RCS and subsystem control
		subSystemMenu = new JMenu("Systems");
		subSystemMenu.add(createMenuItem("Engineering start)", xl, "start-rcs-eng", false));
		subSystemMenu.add(createMenuItem("Auto start", xl, "start-rcs-auto", false));
		subSystemMenu.add(createMenuItem("Restart (Engineering)", xl,"restart-rcs-eng", true));
		subSystemMenu.add(createMenuItem("Restart (Automated)", xl,"restart-rcs-auto", true));
		subSystemMenu.add(createMenuItem("Halt RCS", xl, "halt-rcs", false));
		subSystemMenu.add(createMenuItem("Reboot OCC", xl, "reboot-occ", false));
		subSystemMenu.add(createMenuItem("Shutdown OCC", xl, "shutdown-occ", false));

		menu.add(subSystemMenu);

		// Sky - seeing
		seeMenu = new JMenu("Set Seeing");

		ButtonGroup bg1 = new ButtonGroup();

		// X
		JRadioButtonMenuItem rb = createRadioMenuItem("GOOD", xl, "see-good");
		seeMenu.add(rb);
		bg1.add(rb);

		// A
		rb = createRadioMenuItem("AVERAGE", xl, "see-aver");
		seeMenu.add(rb);
		bg1.add(rb);

		// P
		rb = createRadioMenuItem("POOR", xl, "see-poor");
		seeMenu.add(rb);
		bg1.add(rb);

		// U
		rb = createRadioMenuItem("USABLE", xl, "see-usab");
		seeMenu.add(rb);
		bg1.add(rb);

		menu.add(seeMenu);

		// Sky - photom
		photMenu = new JMenu("Set Photom");

		ButtonGroup bg2 = new ButtonGroup();

		// Photom
		rb = createRadioMenuItem("Photometric", xl, "photom");
		photMenu.add(rb);
		bg2.add(rb);

		rb = createRadioMenuItem("Spectroscopic", xl, "spectro");
		photMenu.add(rb);
		bg2.add(rb);

		menu.add(photMenu);

		// State model
		smItem = (JMenu) menu.add(new JMenu("State model"));
		JMenu fireMenu = (JMenu) (smItem.add(new JMenu("Fire Event")));
		for (int i = 1; i < 27; i++) {
			String etypeName = EnvironmentChangeEvent.typeToString(i);
			JMenuItem item = createMenuItem(etypeName, xl,
					"fire-" + etypeName.toLowerCase());
			item.setEnabled(false);
			fireMenu.add(item);
		}

		// menu.add(createMenuItem("AUTO", xl, "sm-auto"));
		// menu.add(createMenuItem("ENG", xl, "sm-eng"));

		// Intention
		intentItem = menu.add(createIntentMenuItem("OpState"));

		// create various tmc menu items
		JMenu tmcMenu = (JMenu) menu.add(new JMenu("TMC"));
		tmcMenu.add(createTmcMenuItem("Disable SOCA", "tmc-soca", Color.red,
				true));
		tmcMenu.add(createTmcMenuItem("Disable TOCA", "tmc-toca", Color.red,
				false));
		tmcMenu.add(createTmcMenuItem("Disable BGCA", "tmc-bgca", Color.red,
				false));
		tmcMenu.add(createTmcMenuItem("Disable CAL", "tmc-cal", Color.red,
				false));

		helpItem = menu.add(createMenuItem("Assistant", xl, "assist"));

		return menu;

	}

	private JMenuItem createMenuItem(String label, ActionListener l,
			String actionCommand) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(l);
		item.setActionCommand(actionCommand);
		return item;
	}

	private JMenuItem createMenuItem(String label, ActionListener l,
			String actionCommand, boolean enable) {
		JMenuItem item = createMenuItem(label, l, actionCommand);
		item.setEnabled(enable);
		return item;
	}

	
	private JRadioButtonMenuItem createRadioMenuItem(String label,
			ActionListener listener, String action) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(label);
		item.addActionListener(listener);
		item.setActionCommand(action);
		return item;
	}

	private JMenuItem createTmcMenuItem(String label, String actionCommand,
			Color color, boolean enable) {
		JMenuItem item = new JMenuItem(label);
		item.setForeground(color);
		item.addActionListener(new TmcMenuListener(item));
		item.setActionCommand(actionCommand);
		item.setEnabled(enable);
		return item;
	}

	private JMenuItem createIntentMenuItem(String label) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(new IntentItemListener(item));

		return item;
	}

	private class ToolMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			String cmd = ae.getActionCommand();

			if (cmd.equals("weather-override")) {
				// send event 9 to SM
				sendWeatherOverride(WEATHER_CLEAR);

			} else if (cmd.equals("auth")) {
				authorize();
			} else if (cmd.equals("see-good")) {
				// send 10* see to SKYM
				updateSeeingValue(SEEING_GOOD);
			} else if (cmd.equals("see-aver")) {
				// send 10* see to SKYM
				updateSeeingValue(SEEING_AVER);
			} else if (cmd.equals("see-poor")) {
				// send 10* see to SKYM
				updateSeeingValue(SEEING_POOR);
			} else if (cmd.equals("see-usab")) {
				// send 10* see to SKYM
				updateSeeingValue(SEEING_USAB);
			} else if (cmd.equals("photom")) {
				// send phot to SKYM
				updatePhotomValue(PHOTOM);
			} else if (cmd.equals("spectro")) {
				// send phot to SKYM
				updatePhotomValue(SPECTRO);
			} else if (cmd.equals("intention")) {
				// send intent to SM

			} else if (cmd.equals("soca")) {
				// send disable to SOCA

				/*
				 * try { tmc = (TaskModeControllerManagement)
				 * Naming.lookup("rmi://" + rcsHost + "/SOCAModeController"); }
				 * catch (Exception e) { e.printStackTrace();
				 * JOptionPane.showMessageDialog(null,
				 * "Unable to contact SOCA controller", "SOCA Offline: " +
				 * e.getMessage(), JOptionPane.ERROR_MESSAGE); return;
				 */

			} else if (cmd.equals("command")) {
				// send command to CIL
				sendCommand();
			} else if (cmd.equals("state-model")) {
				// send command to CIL
				sendStateModelEvent();

			} else if (cmd.equals("sm-auto")) {
				sendSMEvent(EnvironmentChangeEvent.INTENT_OPERATIONAL);
			} else if (cmd.equals("sm-eng")) {
				sendSMEvent(EnvironmentChangeEvent.INTENT_ENGINEERING);
			} else if (cmd.startsWith("fire-")) {

			} else if (cmd.equals("start-rcs-eng")) {
				START start = new START("opsgui:control");
				start.setEngineering(true);
				sendRcsCommand("RCS ENG startup request", start);
			} else if (cmd.equals("start-rcs-auto")) {
				START start = new START("rcsgui:control");
				start.setEngineering(false);
				sendRcsCommand("RCS AUTO startup request", start);
			} else if (cmd.equals("restart-rcs-eng")) {
				SYSTEM sys = new SYSTEM("rcsgui:control");
				sys.setLevel(SYSTEM.RESTART_ENGINEERING);
				sendRcsCommand("RCS ENG restart request", sys);
			} else if (cmd.equals("restart-rcs-auto")) {
				SYSTEM sys = new SYSTEM("rcsgui:control");
				sys.setLevel(SYSTEM.RESTART_AUTOMATIC);
				sendRcsCommand("RCS AUTO restart request", sys);
			} else if (cmd.equals("halt-rcs")) {
				SYSTEM sys = new SYSTEM("rcsgui:control");
				sys.setLevel(SYSTEM.HALT);
				sendRcsCommand("System HALT request", sys);
			} else if (cmd.equals("reboot-occ")) {
				SYSTEM sys = new SYSTEM("rcsgui:control");
				sys.setLevel(SYSTEM.REBOOT);
				sendRcsCommand("System REBOOT request", sys);
			} else if (cmd.equals("shutdown-occ")) {
				SYSTEM sys = new SYSTEM("rcsgui:control");
				sys.setLevel(SYSTEM.SHUTDOWN);
				sendRcsCommand("System SHUTDOWN request", sys);
			}

		}

	}

	private class TmcMenuListener implements ActionListener {

		private JMenuItem item;

		/**
		 * @param item
		 */
		public TmcMenuListener(JMenuItem item) {
			this.item = item;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {

			String cmd = ae.getActionCommand();

			if (cmd.startsWith("tmc")) {

				String tmcName = cmd.substring(4).toUpperCase(); // tmc-xxx
				System.err.println("TMC Name: [" + tmcName + "]");

				modeEnable(tmcName, item);

			}
		}

	}

	private class IntentItemListener implements ActionListener {

		private JMenuItem item;

		/**
		 * @param item
		 *            The item we need to tweak
		 */
		public IntentItemListener(JMenuItem item) {
			this.item = item;
		}

		@Override
		public void actionPerformed(ActionEvent ae) {

			sendIntentOverride(item);

		}

	}

	private void authorize() {

		JPasswordField pf = new JPasswordField();

		int code = JOptionPane.showConfirmDialog(frame, pf,
				"Command Authorization", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, Resources.getIcon("clippy.icon"));

		if (code == JOptionPane.CANCEL_OPTION)
			return;

		String password = new String(pf.getPassword());

		if (password == null)
			return;

		if (password.equals("flaming%golah")) {
			JOptionPane.showMessageDialog(frame, "Successfully authorized",
					"Command Authorization", JOptionPane.INFORMATION_MESSAGE,
					Resources.getIcon("clippy.icon"));
			authorized = true;
			authItem.setForeground(Color.green);
		} else {
			JOptionPane.showMessageDialog(frame, "Authorization failed",
					"Command Authorization", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

	}

	private void sendSMEvent(int event) {

		String strtype = EnvironmentChangeEvent.typeToString(event);

		int option = -1;

		if (event == EnvironmentChangeEvent.INTENT_OPERATIONAL
				|| event == EnvironmentChangeEvent.INTENT_ENGINEERING) {

			option = JOptionPane.showConfirmDialog(frame,
					"Did you want to put the telescope into " + strtype
							+ " mode ?", "Select Operational Mode",
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
					Resources.getIcon("clippy.icon"));
		} else {
			option = JOptionPane.showConfirmDialog(frame, ""
					+ "Confirm you want to fire Event: " + strtype,
					"Fire State-Model Event", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

		if (option == JOptionPane.CANCEL_OPTION)
			return;

		try {

			EnvironmentalChangeListener ecl = (EnvironmentalChangeListener) Naming
					.lookup("rmi://" + host + "/StateModel");

			EnvironmentChangeEvent cev = new EnvironmentChangeEvent(event);
			ecl.environmentChanged(cev);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"StateModel Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

	}

	private void sendWeatherOverride(int event) {

		int option = JOptionPane.showConfirmDialog(frame,
				"Confirm you want to override the weather state",
				"Override weather state", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, Resources.getIcon("clippy.icon"));

		if (option == JOptionPane.CANCEL_OPTION)
			return;

		try {

			EnvironmentalChangeListener ecl = (EnvironmentalChangeListener) Naming
					.lookup("rmi://" + host + "/StateModel");

			EnvironmentChangeEvent cev = new EnvironmentChangeEvent(event);
			ecl.environmentChanged(cev);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"StateModel Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

	}

	private void sendIntentOverride(JMenuItem item) {

		String dirn = null;
		int event = -1;

		// see first if the system is in auto or eng
		if (cop == StandardStateModel.INIT_STATE) {
			dirn = "ENG -> OPER";
			event = EnvironmentChangeEvent.INTENT_OPERATIONAL;
		} else {
			dirn = "OPER -> ENG";
			event = EnvironmentChangeEvent.INTENT_ENGINEERING;
		}
		int option = JOptionPane.showConfirmDialog(frame,
				"Confirm you want to override the intention state " + dirn,
				"Override intention state", JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE, Resources.getIcon("clippy.icon"));

		if (option == JOptionPane.CANCEL_OPTION)
			return;

		try {

			EnvironmentalChangeListener ecl = (EnvironmentalChangeListener) Naming
					.lookup("rmi://" + host + "/StateModel");

			EnvironmentChangeEvent cev = new EnvironmentChangeEvent(event);
			ecl.environmentChanged(cev);

			// JOptionPane.showMessageDialog(frame, "Sent event code: " + event,
			// "Environment Change Event",
			// JOptionPane.INFORMATION_MESSAGE);

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"StateModel Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

	}

	private void sendStateModelEvent() {

		if (!authorized) {
			JOptionPane.showMessageDialog(frame,
					"You are NOT authorized\nDont do that again !",
					"Authorization Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
			return;
		}

		String code = (String) JOptionPane.showInputDialog(frame,
				"Enter the code for the event", "Fire state-model event",
				JOptionPane.QUESTION_MESSAGE, Resources.getIcon("clippy.icon"),
				null, null);

		// ignore null
		if (code == null || code.equals(""))
			return;

		try {
			int icode = Integer.parseInt(code);
			String strtype = EnvironmentChangeEvent.typeToString(icode);

			int option = JOptionPane.showConfirmDialog(frame,
					"Confirm you want to send state model event: " + icode
							+ " (" + strtype + ")",
					"State model event trigger", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE,
					Resources.getIcon("clippy.icon"));

			if (option == JOptionPane.CANCEL_OPTION)
				return;

			EnvironmentalChangeListener ecl = (EnvironmentalChangeListener) Naming
					.lookup("rmi://" + host + "/StateModel");

			EnvironmentChangeEvent cev = new EnvironmentChangeEvent(icode);
			ecl.environmentChanged(cev);

		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"StateModel Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

	}

	private void sendCommand() {

		if (!authorized) {
			JOptionPane.showMessageDialog(frame, "Not authorized",
					"Authorization Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
			return;
		}

		String command = JOptionPane.showInputDialog(frame, "Command");

		// ignore null
		if (command == null || command.equals(""))
			return;

		try {
			CilCommandHandler cch = new CilCommandHandler(frame);

			CilService cil = (CilService) Naming.lookup("rmi://" + host
					+ "/TCSCilService");
			cil.sendMessage(command, cch, 60000L);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"CIL Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}

	}

	private void sendRcsCommand(String op, COMMAND command) {
	
		CAMPClient client = new SimpleCAMPClient(op, host, 9110, command);
		(new Thread(client)).start();
	}

	private void updateSeeingValue(int value) {

		double svalue = 0.0;
		String sname = "";

		switch (value) {
		case SEEING_GOOD:
			svalue = 0.5;
			sname = "GOOD";
			break;
		case SEEING_AVER:
			svalue = 1.0;
			sname = "AVER";
			break;
		case SEEING_POOR:
			svalue = 1.5;
			sname = "POOR";
			break;
		case SEEING_USAB:
			svalue = 5.0;
			sname = "USAB";
			break;
		}

		int option = JOptionPane.showConfirmDialog(frame,
				"Confirm you want to set the Sky model seeing to: " + sname
						+ " (" + svalue + ")", "Set sky model seeing",
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE,
				Resources.getIcon("clippy.icon"));

		if (option == JOptionPane.CANCEL_OPTION)
			return;

		try {

			// TODO add the user's login name to the source tag.
			MutableSkyModel sm = (MutableSkyModel) Naming.lookup("rmi://"
					+ host + "/SkyModel");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 8000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 7000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 6000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 5000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 4000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 3000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 2000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis() - 1000L, true, "OPSUI", "NONE");
			sm.updateSeeing(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis(), true, "OPSUI", "NONE");

		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"SkyModel Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}
	}

	private void updatePhotomValue(int value) {

		double svalue = 1.0;
		String sname = "";

		switch (value) {
		case PHOTOM:
			svalue = 0.0;
			sname = "Photometric";
			break;
		case SPECTRO:
			svalue = 1.0;
			sname = "Spectroscopic";
			break;
		}

		int option = JOptionPane
				.showConfirmDialog(frame,
						"Confirm you want to set the Sky model extinction to: "
								+ sname, "Set sky model extinction",
						JOptionPane.OK_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						Resources.getIcon("clippy.icon"));

		if (option == JOptionPane.CANCEL_OPTION)
			return;

		try {

			MutableSkyModel sm = (MutableSkyModel) Naming.lookup("rmi://"
					+ host + "/SkyModel");
			sm.updateExtinction(svalue, 700.0, 0.5 * Math.PI, 0.0,
					System.currentTimeMillis(), true);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(frame,
					"Something bad has happened: \n" + e.getMessage(),
					"SkyModel Error", JOptionPane.ERROR_MESSAGE,
					Resources.getIcon("clippy.icon"));
		}
	}

	/**
	 * Enable or disable a tak mode - currently just SOCA.
	 * 
	 * @param enable
	 *            True if we want to enable, false if disable.
	 */
	private void modeEnable(String tmcName, JMenuItem tmcButton) {
		TaskModeControllerManagement tmc = null;
		boolean tmcEnabled = false;

		try {
			tmc = (TaskModeControllerManagement) Naming.lookup("rmi://" + host
					+ "/" + tmcName + "ModeController");
			tmcEnabled = tmc.isEnabled();
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Unable to contact " + tmcName
					+ " controller", tmcName + " Offline: " + e.getMessage(),
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (tmcEnabled) {
			int confirm = JOptionPane.showConfirmDialog(null,
					"Do you want to disable " + tmcName + " mode ?",
					"Task mode disable", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.NO_OPTION)
				return;
			try {
				tmc.disable();
				tmcEnabled = false;
				if (tmcButton != null) {
					tmcButton.setText("Enable " + tmcName);
					tmcButton.setForeground(Color.green);
				}
				// socaStatusLabel.setMonitorEnabled(false);
				JOptionPane.showMessageDialog(null, tmcName
						+ " controller is disabled", tmcName + " Disabled",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Unable to disable "
						+ tmcName + " controller",
						tmcName + " Error: " + e.getMessage(),
						JOptionPane.ERROR_MESSAGE);
			}
		} else {
			int confirm = JOptionPane.showConfirmDialog(null,
					"Do you want to re-enable " + tmcName + " mode ?", tmcName
							+ " re-enable", JOptionPane.YES_NO_OPTION);
			if (confirm == JOptionPane.NO_OPTION)
				return;
			try {
				tmc.enable();
				tmcEnabled = true;
				if (tmcButton != null) {
					tmcButton.setText("Disable " + tmcName);
					tmcButton.setForeground(Color.red);
				}
				// socaStatusLabel.setMonitorEnabled(true);
				JOptionPane.showMessageDialog(null, tmcName
						+ " controller is re-enabled", tmcName + " re-enabled",
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception e) {
				JOptionPane.showMessageDialog(null, "Unable to re-enable "
						+ tmcName + " controller",
						tmcName + " Error: " + e.getMessage(),
						JOptionPane.ERROR_MESSAGE);
			}
		}

	}

	// various update handlers.

	public void updateOperationalState(int state) {

		cop = state;
		if (cop == StandardStateModel.INIT_STATE) {
			intentItem.setText("OPER");
			intentItem.setForeground(Color.blue);
			intentItem.setBackground(Color.green);
		} else {
			intentItem.setText("ENG");
			intentItem.setForeground(Color.cyan);
			intentItem.setBackground(Color.red);
		}
	}

	public static void main(String args[]) {

		try {

			JFrame f = new JFrame("ToolMenuTest");

			f.getContentPane().add(new JLabel("Testing of the tool menu"));

			ToolMenu t = new ToolMenu(f, "localhost");
			JMenuBar bar = new JMenuBar();
			bar.add(t.getMenu());
			f.setJMenuBar(bar);
			f.pack();
			f.setVisible(true);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}