/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/** A basic summary pane.
 * @author eng
 *
 */
public abstract class SummaryPane extends JPanel {

	private Color borderColor;
	
	private TitledBorder tb;
	
	private Border lineBorder;
	
	private Color onlineBackgroundColor;
	
	private Color offlineBackgroundColor;
	
	private volatile boolean svcAvailable;
	
	public SummaryPane(String title) {
		super(true);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		 tb = new TitledBorder(title);
		//tb.setTitleFont(Resources.getFont("border.title.font"));

		tb.setTitleColor(Color.gray);	
		tb.setTitlePosition(TitledBorder.DEFAULT_POSITION);
		lineBorder = BorderFactory.createLineBorder(Color.gray);
		tb.setBorder(lineBorder);
		setBorder(tb);
		
		// use default value for now, this looks weird, we actually want to set the background of
		// a sub-panel created by implementations of createPanel()....
		/*onlineBackgroundColor = getBackground();
		int red = onlineBackgroundColor.getRed();
		int blue = onlineBackgroundColor.getBlue();
		int green = onlineBackgroundColor.getGreen();
		blue /= 2;
		green /= 2;
		offlineBackgroundColor = new Color(red,green,blue);*/
	}
	
	/** Overwrite to create the panel.*/
	public abstract void createPanel();
	
	
	public void updateServiceStatus(long time, boolean available, String message) {
		// no change so do nothing otherwise we will be repainting every few seconds
		if (available == svcAvailable)
			return;
		
		if (available) {
			tb.setTitleColor(Color.blue);
			lineBorder = BorderFactory.createLineBorder(Color.blue);
			tb.setBorder(lineBorder);
			//setBackground(onlineBackgroundColor);
		} else {
			tb.setTitleColor(Color.red);
			lineBorder = BorderFactory.createLineBorder(Color.red);
			tb.setBorder(lineBorder);
			//setBackground(offlineBackgroundColor);
		}	
		// record state we just set
		svcAvailable = available;
		repaint();
	}
}
	
	
