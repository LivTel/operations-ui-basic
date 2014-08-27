/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;

/**
 * @author eng
 * 
 */
public class LegendPanel extends JPanel {

	List<CatalogDisplay> legend;

	/**
	 * 
	 */
	public LegendPanel() {
		super();
		legend = new Vector<CatalogDisplay>();
		setBackground(Color.blue.darker().darker());
		setLayout(new BorderLayout());
		
	}

	public void addLegendEntry(CatalogDisplay display) {
		legend.add(display);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		FontMetrics fm = g.getFontMetrics();
		// find the longest state label
		int maxwidth = 0;
		for (int il = 0; il < legend.size(); il++) {
			CatalogDisplay entry = legend.get(il);
			String label = " " + entry.getDisplayDescriptor().getName() + " "; // pad
																				// out
																				// with
																				// spaces
																				// each
																				// end

			int labelwidth = fm.stringWidth(label);
			if (labelwidth > maxwidth)
				maxwidth = labelwidth;
		}
		System.err.println("Max width = " + maxwidth);

		int loff = 5;
		int roff = 5;
		int box = 16;
		int gap = 4;
		int ll = maxwidth;

		int bw = loff + roff + gap + box + ll;
		int bh = 2 * gap + box;

		// /setPreferredSize(new Dimension(bw, bh*maplist.size()));

		for (int il = 0; il < legend.size(); il++) {
			CatalogDisplay entry = legend.get(il);
			CatalogDisplayDescriptor cdd = entry.getDisplayDescriptor();
			//g.setColor(cdd.getColor());
			//g.fillRect(loff, (il) * bh + gap, box, box);
			cdd.setLabelAlignment(CatalogDisplayDescriptor.LABEL_EAST);
			cdd.paintComponent(g, cdd.getName(), false, loff,(il) * bh + gap + box);
			//g.drawString(cdd.getName(), loff + box + gap, (il) * bh + gap + box);
			
		}

	}

}
