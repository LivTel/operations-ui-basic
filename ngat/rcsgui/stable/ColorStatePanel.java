package ngat.rcsgui.stable;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.SimpleTimeZone;
import javax.swing.*;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateIndicator;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeAxisPanel;
import ngat.opsgui.util.TimeDisplayController;

public class ColorStatePanel extends JPanel {

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static final int DAYTIME_STATE = 1;
	public static final int BAD_WEATHER_STATE = 2;
	public static final int SYSTEM_SUSPEND_STATE = 3;
	public static final int SYSTEM_STANDBY_STATE = 4;
	public static final int SYSTEM_FAIL_STATE = 5;
	public static final int OPS_SOCA_STATE = 6;
	public static final int OPS_BGCA_STATE = 7;
	public static final int OPS_TOCA_STATE = 8;
	public static final int OPS_X_STATE = 9;
	public static final int OPS_CAL_STATE = 10;
	public static final int ENG_STATE = 11;

	public static final Color BASE_COLOR = RcsGUI.LIGHTSLATE;
	public static final Color DEFAULT_COLOR = Color.gray;
	public static final Color DAYTIME_COLOR = Color.orange;
	public static final Color BAD_WEATHER_COLOR = Color.red;
	public static final Color SYSTEM_SUSPEND_COLOR = Color.red.darker();
	public static final Color SYSTEM_STANDBY_COLOR = Color.yellow.darker();
	public static final Color SYSTEM_FAIL_COLOR = Color.red;

	public static final Color OPS_SOCA_COLOR = Color.yellow.darker();
	public static final Color OPS_BGCA_COLOR = Color.cyan;
	public static final Color OPS_TOCA_COLOR = Color.pink;
	public static final Color OPS_CAL_COLOR = Color.pink.darker();
	public static final Color OPS_X_COLOR = Color.pink.darker();
	public static final Color ENG_COLOR = Color.blue;
	
	public static final Font BTN_FONT = new Font("serif", Font.PLAIN, 9);
	public static final Color BTN_COLOR = new Color(237, 216, 130);
	public static final Dimension BTN_SIZE = new Dimension(26,20);
	public static final Dimension DIMENSION1 = new Dimension(650, 40);
	public static final Dimension DIMENSION2 = new Dimension(650, 30);
	public static final Dimension DIMENSION3 = new Dimension(100, 70);
	
	public static final long DAY = 86400 * 1000L;
	public static final long HALF_DAY = 43200 * 1000L;
	public static final long Q_DAY = 21600 * 1000L;
	public static final long HQ_DAY = 10800 * 1000L;
	
	String mode = "unknown";
	boolean eng = true;
	boolean badweather = false;
	int sysstate = 1;
	boolean daytime = false;

	StatusHistoryPanel hp;
	TimeAxisPanel tap;
	ColorStateIndex colorStateIndex;
	StateIndicator si;
	TimeDisplayController tdc;

	public ColorStatePanel() {
		super(true);

		tdc = new TimeDisplayController(HQ_DAY);
		
		hp = new StatusHistoryPanel(tdc);
		tdc.addTimeDisplay(hp);
		StateColorMap map = new StateColorMap(BASE_COLOR, "OTHER");
		map.addColorLabel(BAD_WEATHER_STATE, BAD_WEATHER_COLOR, "WEATHER");
		map.addColorLabel(DAYTIME_STATE, DAYTIME_COLOR, "DAYTIME");
		map.addColorLabel(ENG_STATE, ENG_COLOR, "ENG");
		map.addColorLabel(OPS_BGCA_STATE, OPS_BGCA_COLOR, "BGCA");
		map.addColorLabel(OPS_CAL_STATE, OPS_CAL_COLOR, "CAL");
		map.addColorLabel(OPS_SOCA_STATE, OPS_SOCA_COLOR, "SOCA");
		map.addColorLabel(OPS_X_STATE, OPS_X_COLOR, "X");
		map.addColorLabel(OPS_TOCA_STATE, OPS_TOCA_COLOR, "TOCA");
		map.addColorLabel(SYSTEM_FAIL_STATE, SYSTEM_FAIL_COLOR, "FAIL");
		map.addColorLabel(SYSTEM_STANDBY_STATE, SYSTEM_STANDBY_COLOR, "STANDBY");
		map.addColorLabel(SYSTEM_SUSPEND_STATE, SYSTEM_SUSPEND_COLOR, "SUSPEND");
		hp.setMap(map);
		hp.setPreferredSize(DIMENSION1);
		tap = new TimeAxisPanel();
		tdc.addTimeDisplay(tap);
		tap.setFont(new Font("serif", Font.PLAIN, 10));
		tap.setPreferredSize(DIMENSION2);

		setLayout(new FlowLayout(FlowLayout.LEFT));

		
		// Buttons
		JPanel panel1 = new JPanel(true);
		panel1.setLayout(new GridLayout(4,2,4,4));
		//panel1.setBorder(BorderFactory.createLineBorder(Color.black));
		
		ActionListener al = new HistoryPanelListener();
		
		JButton b1 = createButton("|<", al);
		panel1.add(b1);
		JButton b2 = createButton("<", al);
		panel1.add(b2);
		JButton b3 = createButton(">|", al);
		panel1.add(b3);
		JButton b4 = createButton(">", al);
		panel1.add(b4);
		JButton b5 = createButton("@", al);
		panel1.add(b5);
		JButton b6 = createButton("Z+", al);
		panel1.add(b6);
		JButton b7 = createButton("z-", al);
		panel1.add(b7);
		JButton b8 = createButton("X", al);
		panel1.add(b8);
		add(panel1);
		
		// History and Timeaxis
		JPanel panel2 = new JPanel(true);
		panel2.setLayout(new GridLayout(2, 1));
		panel2.add(hp);
		panel2.add(tap);
		add(panel2);

		// Key
		colorStateIndex = new ColorStateIndex();
		add(colorStateIndex);

		// Current state
		si = new StateIndicator();
		si.setMap(map);
		si.setFont(new Font("serif", Font.PLAIN, 12));
		si.setPreferredSize(DIMENSION3);
		add(si);

	}

	private JButton createButton(String text, ActionListener al) {
		JButton b = new JButton(text);
		b.setFont(BTN_FONT);
		b.setBackground(BTN_COLOR);
		b.setBorder(BorderFactory.createEtchedBorder(Color.blue, Color.cyan));
		b.setActionCommand(text);
		b.addActionListener(al);
		return b;
	}

	private void updateData() {

		// if the last call was less then XX seconds ago we dont bother updating
		// our records.
		// or we store by-the-minute or 5 minute logs
		long now = System.currentTimeMillis();

		// determine state
		int state = 0;
		String stateText = "";

		if (daytime) {
			state = DAYTIME_STATE;
			stateText = "DAYTIME";
		} else if (badweather) {
			state = BAD_WEATHER_STATE;
			stateText = "BAD WEATHER";
		} else if (eng) {
			state = ENG_STATE;
			stateText = "ENGINEERING";
		} else {

			if (sysstate == 1) {
				state = SYSTEM_SUSPEND_STATE;
				stateText = "SYSTEM SUSPEND";
			} else if (sysstate == 2) {
				state = SYSTEM_STANDBY_STATE;
				stateText = "SYSTEM STANDBY";
			} else if (sysstate == 3) {

				if (mode.equals("BGCA")) {
					state = OPS_BGCA_STATE;
					stateText = "BGCA";
				} else if (mode.equals("TOCA")) {
					state = OPS_TOCA_STATE;
					stateText = "TOCA";
				} else if (mode.equals("XCA")) {
					state = OPS_X_STATE;
					stateText = "EXPERIMENTAL";
				} else if (mode.equals("CAL")) {
					state = OPS_CAL_STATE;
					stateText = "CALIB";
				} else if (mode.equals("SOCA")) {
					state = OPS_SOCA_STATE;
					stateText = "SOCA";
				}
			} else if (sysstate == 4) {
				state = SYSTEM_FAIL_STATE;
				stateText = "SYSTEM FAIL";
			}
		}
		hp.addHistory(now, state);
		hp.repaint();
		si.updateState(state, stateText);
		tap.repaint();

	}

	public void updateState(int state) {
		if (state == 1)
			eng = true;
		else
			eng = false;
		updateData();
	}

	public void updateSysvar(int var) {

		switch (var) {
		case 9:
			badweather = true;
			break;
		case 10:
			badweather = false;
			break;
		case 18:
			daytime = true;
			break;
		case 19:
			daytime = false;
			break;

		case 1:
		case 2:
		case 3:
		case 4:
			sysstate = var;
			break;
		}
		updateData();
	}

	public void updateMode(String mode) {
		this.mode = mode;
		updateData();
	}

	class HistoryPanelListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			
			String cmd = ae.getActionCommand();
			if (cmd.equals("Z+")) {
				//long duration = hp.getDuration();
				//hp.setDuration(duration/2);
				//tap.setDuration(duration/2);
				tdc.requestZoom(0.5);
			} else if
			(cmd.equals("z-")) {
				//long duration = hp.getDuration();
				//hp.setDuration(duration*2);
				//tap.setDuration(duration*2);
				tdc.requestZoom(2.0);
			} else if
			(cmd.equals("@")) {
				//hp.setDuration(HQ_DAY);
				//tap.setDuration(HQ_DAY);
			} else if
			(cmd.equals("<")) {
				tdc.requestPan(-0.75);
			} else if
			(cmd.equals(">")) {
				tdc.requestPan(0.75);
			}
			tap.repaint();
			hp.repaint();		
		}
		
		
		
	}
	
}