/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.AbstractButton;
import javax.swing.BorderFactory;
import javax.swing.ButtonModel;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

/**
 * @author eng
 *
 */
public class MonitorStateDisplay2 extends JCheckBox {

	private boolean monitorEnabled;
	
	private boolean monitorActivated;
	 
	private Color bgcolor;
	private Color fgcolor;
	 
	/**
	 * @param text
	 */
	public MonitorStateDisplay2(String text) {
		super(text);
		setIcon(new CheckBoxIcon());
		setHorizontalAlignment(SwingConstants.TRAILING);
		setVerticalAlignment(SwingConstants.CENTER);
		setVerticalTextPosition(CENTER);
		setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.blue), 
				BorderFactory.createEmptyBorder(1, 4, 1, 8)));
		setBorderPainted(true);
	}
	
	/**
	 * @param enabled the enabled to set
	 */
	public void setMonitorEnabled(boolean enabled) {
		this.monitorEnabled = enabled;
		System.err.println("MonStateDisp["+getText()+"]: set enabled: "+enabled);
		repaint();
	}
	
	public void setMonitorActivated(boolean activated) {
		this.monitorActivated = activated;
		if (!activated){
			setBackground(Color.lightGray);
			setForeground(Color.darkGray);
		}
	}	
	

	private class CheckBoxIcon implements Icon {
		
		  @Override
		public void paintIcon(Component component, Graphics g, int x, int y) {
		    AbstractButton abstractButton = (AbstractButton)component;
		    ButtonModel buttonModel = abstractButton.getModel();
		   
		    if (monitorActivated) {
				if (monitorEnabled) {
					bgcolor = Color.green;
					fgcolor = Color.black;
				} else {
					bgcolor = Color.red;
					fgcolor = Color.black;
				}
			} else {
				bgcolor = Color.gray;
				fgcolor = Color.blue;
			}
		   
		    g.setColor(bgcolor);		    
		    g.fillRect(1, 1, 15, 15);
		    g.setColor(fgcolor);
		    g.fillOval(6, 6, 5, 5);
		    
		  }
		  @Override
		public int getIconWidth() {
		    return 15;
		  }
		  
		  @Override
		public int getIconHeight() {
		    return 15;
		  }
		  
	}
	
}
