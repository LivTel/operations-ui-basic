package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

public class MonitorStateDisplay extends JPanel {

	public static Dimension SIZE = new Dimension(12,12);
	
	private boolean enabled = false;
	private boolean activated = false;
	
	private String name;
	
	/**
	 * @param name
	 */
	public MonitorStateDisplay(String name) {
		super(true);
		this.name = name;
	}

	@Override
	public Dimension getPreferredSize() {
		return SIZE;
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (!activated) {
			g.setColor(Color.gray);
			g.fillRect(1,1,10,10);
			g.setColor(Color.blue);
			g.fillOval(4,4,4,4);
			g.setColor(Color.black);
			g.drawRect(1,1,10,10);
			return;
		}
		if (enabled) {
			g.setColor(Color.green);
			g.fillRect(1,1,10,10);
			g.setColor(Color.blue);
			g.fillOval(4,4,4,4);
			g.setColor(Color.black);
			g.drawRect(1,1,10,10);
		} else {
			g.setColor(Color.red);
			g.fillRect(1,1,10,10);
			g.setColor(Color.cyan);
			g.fillOval(4,4,4,4);
			g.setColor(Color.black);
			g.drawRect(1,1,10,10);
		}
	}

	/**
	 * @return the enabled
	 */
	@Override
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
		System.err.println("MonStateDisp["+name+"]: set enabled: "+enabled);
	}
	
	public void setActivated(boolean activated) {
		this.activated = activated;
	}
}
