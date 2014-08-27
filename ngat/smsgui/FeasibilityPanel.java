/**
 * 
 */
package ngat.smsgui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JPanel;

import ngat.phase2.XFixedTimingConstraint;
import ngat.sms.util.PrescanEntry;

/**
 * @author eng
 *
 */
public class FeasibilityPanel extends JPanel {

	PrescanEntry pse;
	Dimension DEF = new Dimension(120,10);
	int DW = 5;
	int DH = 2;
	boolean fixed  =  false;
	public FeasibilityPanel(PrescanEntry pse) {
		super(true);
		this.pse = pse;
		if (pse.group.getTimingConstraint() instanceof XFixedTimingConstraint)
			fixed = true;
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {		
		super.paint(g);
		
		int w = getSize().width;
		int h = getSize().height;
		
		boolean[] f = pse.feasible;
		int nf = f.length;
		int sw = w - 2 * DW;
		int sh  = h - 2 * DH;
				
		for (int i = 0; i < nf; i++) {
			if (f[i]) {
				if (fixed)
					g.setColor(Color.RED);
				else
					g.setColor(Color.PINK);
			} else {
				g.setColor(Color.BLUE);
			}
			double ff = (double)sw/(double)nf;
		
			g.fillRect(DW+(int)(ff*i), DH, (int)(ff)+1, sh);
			
		}		
		
		// now stick in the NOW time
		long now = System.currentTimeMillis();
		double ff = (double)(now - pse.start) / (double)(pse.end-pse.start);
		
		g.setColor(Color.GREEN);
		g.drawLine(DW+(int)(ff*sw), DH, DW+(int)(ff*sw), sh);
		
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {	
		return DEF;
	}
	
}
