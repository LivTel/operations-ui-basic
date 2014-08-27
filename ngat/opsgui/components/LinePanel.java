/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JPanel;

/**
 * @author eng
 *
 */
public class LinePanel extends JPanel {
	
    public static final int DEFAULT_LINE_SIZE = 200;
    
    public LinePanel() {
	super(true);
        setLayout(new FlowLayout(FlowLayout.LEADING));
        setPreferredSize(new Dimension(DEFAULT_LINE_SIZE, 20));
    }
    
    public LinePanel(int width) {
	super(true);
	setLayout(new FlowLayout(FlowLayout.LEADING));
	setPreferredSize(new Dimension(width, 20));		
	setMaximumSize(new Dimension(width, 20));
    }
 
}


