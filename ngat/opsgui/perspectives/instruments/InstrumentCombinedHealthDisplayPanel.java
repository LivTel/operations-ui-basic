/**
 * 
 */
package ngat.opsgui.perspectives.instruments;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.icm.InstrumentStatus;
import ngat.message.ISS_INST.GET_STATUS_DONE;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.base.Resources;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeAxisPanel;
import ngat.opsgui.util.TimeDisplayController;
import ngat.tcm.AutoguiderActiveStatus;

/**
 * Displays history of all instruments' health state.
 * 
 * @author eng
 * 
 */
public class InstrumentCombinedHealthDisplayPanel extends JPanel {

	public static final int OKAY = 1;
	public static final int WARN = 2;
	public static final int FAIL = 5;
	public static final int OFFLINE = 8;
	public static final int DISABLED = 9;

	private StateColorMap instrumentStateColors;

	private StateColorMap autoguiderStateColors;

	private Map<String, StatusHistoryPanel> panels;

	private TimeDisplayController tdc;

	private ControlPanel controlPanel;

	/**
	 * 
	 */
	public InstrumentCombinedHealthDisplayPanel(InstrumentRegistry ireg) throws Exception {
		super();

		instrumentStateColors = new StateColorMap(Color.gray, "UNKNOWN");
		instrumentStateColors.addColorLabel(OKAY, Color.green, "OKAY");
		instrumentStateColors.addColorLabel(WARN, Color.orange, "WARN");
		instrumentStateColors.addColorLabel(FAIL, Color.red, "FAIL");
		instrumentStateColors.addColorLabel(OFFLINE, Color.blue, "OFFLINE");
		instrumentStateColors.addColorLabel(DISABLED, Color.pink, "DISABLED");

		autoguiderStateColors = new StateColorMap(Color.gray, "UNKNOWN");
		autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_OKAY, Color.green, "OKAY");
		autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_FAIL_HIGH, Color.red,
				"FAIL_HI");
		// autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_FAIL_LOW,
		// Color.pink, "FAIL_LO");
		autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_FAIL_LOW, Color.red,
				"FAIL_LO");
		autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_WARN_HIGH, Color.orange,
				"WARN_HI");
		// autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_WARN_LOW,
		// Color.magenta, "WARN_LO");
		autoguiderStateColors.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_WARN_LOW, Color.orange,
				"WARN_LO");
		autoguiderStateColors.addColorLabel(OFFLINE, Color.blue, "OFFLINE");

		panels = new HashMap<String, StatusHistoryPanel>();

		tdc = new TimeDisplayController(1 * 3600 * 1000L);
		TimeAxisPanel tap = new TimeAxisPanel();
		tap.setPreferredSize(new Dimension(500, 40));
		tdc.addTimeDisplay(tap);

		JPanel tapContainer = new JPanel();
		tapContainer.setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel label = new JLabel("Time");
		label.setPreferredSize(new Dimension(90, 30));
		tapContainer.add(label);
		tapContainer.add(tap);

		setLayout(new GridLayout(12, 1));

		List instList = ireg.listInstruments();
		for (int ii = 0; ii < instList.size(); ii++) {

			InstrumentDescriptor id = (InstrumentDescriptor) instList.get(ii);

			List<InstrumentDescriptor> sublist = id.listSubcomponents();
			if (sublist.size() != 0) {
				for (int is = 0; is < sublist.size(); is++) {
					InstrumentDescriptor sid = sublist.get(is);
					JPanel instPanel = createInstrumentPanel(id.getInstrumentName() + "_" + sid.getInstrumentName(),
							instrumentStateColors);
					add(instPanel);
				}
			} else {
				JPanel instPanel = createInstrumentPanel(id.getInstrumentName(), instrumentStateColors);
				add(instPanel);
			}

		}

		JPanel agInstPanel = createInstrumentPanel("AUTOGUIDER", autoguiderStateColors);
		add(agInstPanel);

		add(tapContainer);

		// controlPanel = new ControlPanel();
		// add(controlPanel);

	}

	public JPanel createInstrumentPanel(String instName, StateColorMap map) {

		// the name will be RATCAM or FRODO_RED or RINGO3_E etc

		JPanel p = new JPanel();
		p.setLayout(new FlowLayout(FlowLayout.LEADING));

		JLabel label = new JLabel(instName);
		label.setPreferredSize(new Dimension(90, 30));
		p.add(label);

		StatusHistoryPanel shp = new StatusHistoryPanel(tdc);
		shp.setMap(map);
		shp.setPreferredSize(new Dimension(500, 30));

		p.add(shp);
		tdc.addTimeDisplay(shp);
		panels.put(instName, shp);

		return p;

	}

	public void updateStatus(InstrumentStatus status) {

		// System.err.println("ICHD: UPDATE STATUS: "+id.getInstrumentName()+" "+status);

		// determine the correct history to update...
		InstrumentDescriptor id = status.getInstrument();
		String instName = id.getInstrumentName();

		boolean online = status.isOnline();
		// boolean funcState = status.isFunctional();
		boolean enabled = status.isEnabled();

		// sub insts copy their owners enabled,online statii but have their own
		// func status ?
		List<InstrumentDescriptor> sublist = id.listSubcomponents();
		if (sublist.size() != 0) {
			for (int is = 0; is < sublist.size(); is++) {
				InstrumentDescriptor sid = sublist.get(is);
				if (!online) {
					StatusHistoryPanel shp = panels.get(instName + "_" + sid.getInstrumentName());
					shp.addHistory(status.getStatusTimeStamp(), OFFLINE);
				} else {

					int funcState = decodeFunctionalStatus(sid, status);
					int state = (enabled ? (online ? funcState : OFFLINE) : DISABLED);
					// locate sub instrument panel
					StatusHistoryPanel shp = panels.get(instName + "_" + sid.getInstrumentName());
					shp.addHistory(status.getStatusTimeStamp(), state);
				}
			}
		} else {

			if (!online) {
				StatusHistoryPanel shp = panels.get(instName);
				shp.addHistory(status.getStatusTimeStamp(), OFFLINE);
			} else {

				int funcState = decodeFunctionalStatus(id, status);
				int state = (enabled ? (online ? funcState : OFFLINE) : DISABLED);

				StatusHistoryPanel shp = panels.get(instName);
				shp.addHistory(status.getStatusTimeStamp(), state);
			}
		}

	}

	private int decodeFunctionalStatus(InstrumentDescriptor id, InstrumentStatus status) {

		System.err.println("FUNCSTATE: For: " + id.getInstrumentName());

		String key = GET_STATUS_DONE.KEYWORD_DETECTOR_TEMPERATURE_INSTRUMENT_STATUS;
		String prefix = id.getTemperatureKeywordPrefix();
		if (prefix == null)
			prefix = "";
		String suffix = id.getTemperatureKeywordSuffix();
		if (suffix == null)
			suffix = "";
		key = prefix + key + suffix;

		String strFuncState = (String) status.getStatus().get(key);
		System.err.println("FUNCSTATE: Use key: " + key);
		System.err.println("FUNCSTATE: Value: " + strFuncState);
		int funcState = 0;
		if (strFuncState.equals(GET_STATUS_DONE.VALUE_STATUS_FAIL))
			funcState = FAIL;
		else if (strFuncState.equals(GET_STATUS_DONE.VALUE_STATUS_WARN))
			funcState = WARN;
		else if (strFuncState.equals(GET_STATUS_DONE.VALUE_STATUS_OK)
				|| strFuncState.equals(GET_STATUS_DONE.VALUE_STATUS_UNKNOWN))
			funcState = OKAY;

		System.err.println("FUNCSTATE: Code: " + funcState);
		return funcState;
	}

	public void updateAutoguiderStatus(AutoguiderActiveStatus agActiveStatus) {

		// we are only interested in CASS. this may change and we have multiple
		// AGs
		// identified by instName as there agName
		// TODO String instName = agActiveStatus.getAutoguiderName();

		System.err.println("ACMP: Received: " + agActiveStatus);

		if (!agActiveStatus.getAutoguiderName().equalsIgnoreCase("CASS")) {
			System.err.println("ACMP: Ignore status from autoguider: " + agActiveStatus.getAutoguiderName());
			return;
		}

		// determine the correct history to update...
		String instName = "AUTOGUIDER";

		boolean onlineState = agActiveStatus.isOnline();
		int tempStatus = agActiveStatus.getTemperatureStatus();
		boolean activeState = agActiveStatus.isActiveStatus();

		StatusHistoryPanel shp = panels.get(instName);

		if (!onlineState)
			shp.addHistory(agActiveStatus.getStatusTimeStamp(), OFFLINE);
		else
			shp.addHistory(agActiveStatus.getStatusTimeStamp(), tempStatus);

	}

	/**
	 * @author eng
	 * 
	 */
	private class ControlPanel extends JPanel {
		// implements TimeDisplayListener { // in order to obtain start/end
		// limits

		private ActionListener l;

		private JButton zoomInButton;

		private JButton zoomOutButton;

		private JButton panLeftButton;

		private JButton panRightButton;

		private JButton gotoStartButton;

		private JButton gotoEndButton;

		/**
		 * 
		 */
		public ControlPanel() {
			super();

			setLayout(new FlowLayout(FlowLayout.LEADING));

			l = new ControlActionListener();

			zoomInButton = createButton(Resources.getIcon("zoom.in.icon"), "Zoom in", "zoom-in");
			add(zoomInButton);
			zoomOutButton = createButton(Resources.getIcon("zoom.out.icon"), "Zoom out", "zoom-out");
			add(zoomOutButton);
			panLeftButton = createButton(Resources.getIcon("pan.left.icon"), "Pan left", "pan-left");
			add(panLeftButton);
			panRightButton = createButton(Resources.getIcon("pan.right.icon"), "Pan right", "pan-right");
			add(panRightButton);
			gotoStartButton = createButton(Resources.getIcon("goto.start.icon"), "Start", "goto-start");
			add(gotoStartButton);
			gotoEndButton = createButton(Resources.getIcon("goto-end-icon"), "End", "goto-end");
			add(gotoEndButton);

		}

		private JButton createButton(Icon icon, String text, String actionCommand) {

			JButton b = ComponentFactory.makeIconButton(icon);
			b.setToolTipText(text);
			b.setActionCommand(actionCommand);
			b.addActionListener(l);

			return b;
		}

	}

	public class ControlActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			String cmd = ae.getActionCommand();

			if (cmd.equals("zoom-in"))
				tdc.requestZoom(2.0);
			else if (cmd.equals("zoom-out"))
				tdc.requestZoom(0.5);
			else if (cmd.equals("pan-left"))
				tdc.requestPan(-0.5);
			else if (cmd.equals("pan-right"))
				tdc.requestPan(0.5);
			else if (cmd.equals("goto-start"))
				tdc.requestPanToStart();
			else if (cmd.equals("goto-end"))
				tdc.requestSynchronize(true);

		}

	}

}
