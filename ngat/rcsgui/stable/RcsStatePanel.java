/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * @author eng
 *
 */
public class RcsStatePanel extends JPanel {

	private JTextField statusField;
	
	private JTextField opsMgrField;
	
	private JTextField obsmodeField;

	private JTextField uptimeField;
	
	/**
	 * 
	 */
	public RcsStatePanel() {
		super();
		
		setLayout(new FlowLayout(FlowLayout.LEADING));
		
		statusField = new JTextField(8);
		opsMgrField = new JTextField(8);
		obsmodeField = new JTextField(8);
		uptimeField = new JTextField(6);
		uptimeField.setForeground(Color.blue);
		
		add(new JLabel("Status"));
		add(statusField);
		add(new JLabel("Ops Mgr"));
		add(opsMgrField);
		add(new JLabel("Obs Mode"));
		add(obsmodeField);
		add(new JLabel("Up"));
		add(uptimeField);
		
		
	}
	
	public void updateNewStateField(int cs) {

		String sl = "UNKNOWN";
		Color scol = Color.gray;

		switch (cs) {
		case 1:
			sl = "ENGINEERING";
			break;
		case 2:
			sl = "STANDBY";
			break;
		case 3:
			sl = "STARTING";
			break;
		case 4:
			sl = "OPENING";
			break;
		case 5:
			sl = "OPERATIONAL";
			break;
		case 6:
			sl = "CLOSING";
			break;
		case 7:
			sl = "STOPPING";
			break;
		case 8:
			sl = "SHUTDOWN";
			break;
		}

		statusField.setText(sl);
	
	}

	public void updateNewOpField(int cs) {

		String sl = "UNKNOWN";
		Color scol = Color.gray;

		switch (cs) {
		case 1:
			sl = "IDLE";
			scol = Color.orange;
			break;
		case 2:
			sl = "INITIALIZING";
			scol = Color.yellow;
			break;
		case 3:
			sl = "FINALIZING";
			scol = Color.yellow;
			break;
		case 4:
			sl = "OBSERVING";
			scol = Color.green;
			break;
		case 5:
			sl = "MODE_SWITCH";
			scol = Color.cyan.darker();
			break;
		}

		opsMgrField.setText(sl);
		opsMgrField.setBackground(scol);

	}
	
	public void updateObsMode(String modeStr, int aidId) {
		obsmodeField.setText(modeStr);
	}
	
	public void updateUptime(long uptime) {
		
		// uptime is in musec
		
		// colorize and format to: zH xM [yS] 
		Color color = Color.green;
		if (uptime < 10*60*1000)
			color = Color.red;
		else if
			(uptime < 30*60*1000)
			color = Color.orange;
		else if
		(uptime < 60*60*1000)
		color = Color.cyan;
		
		uptimeField.setBackground(color);
		
		int um = (int)Math.floor(uptime/60000.0);
		int us = (int)Math.floor((uptime - 60000.0*um)/1000);
		uptimeField.setText(""+um+"m "+us+"s");
	}
}
