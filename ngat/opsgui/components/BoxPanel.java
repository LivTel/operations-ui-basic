package ngat.opsgui.components;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JPanel;


/** A panel to hold a number of line panels.
 * Usage:
 * BoxPanel box = new BoxPanel(200);
 * LinePanel line = new LinePanel(200);
 * box.addLine(line)
 * box.addLine(line2)
 * box.addLine(line3)
 * box.fix()
 * container.add(box);
 */
public class BoxPanel extends JPanel {

    /** Nu,ber of lines.*/
    private int nl;

    /** Width of box.*/
    private int xwidth;

    public BoxPanel(int xwidth) {
	super(true);
	this.xwidth = xwidth;
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	nl = 0;
    }

    public void addLine(LinePanel line) {
	nl++;
	add(line);	
    }
    
    public void fix() {
	Dimension size = new Dimension(xwidth, 20*nl);
	setPreferredSize(size);
	setMaximumSize(size);
    }

}