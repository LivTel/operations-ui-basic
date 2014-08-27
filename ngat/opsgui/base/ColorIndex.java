/**
 * 
 */
package ngat.opsgui.base;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateColorMapEntry;

/** A component to display a color index key.
 * @author eng
 *
 */
public class ColorIndex extends JPanel {

	/** Horizontal keys.*/
	public static final int HORIZONTAL = 1;
	
	/** Vertical keys.*/
	public static final int VERTICAL = 2;
	
	/** Grid keys.*/
	public static final int GRID = 3;
	
	
	/** Determines keyLayout of key.*/
	private int keyLayout;
	
	/** Number of rows in grid.*/
	private int gridRows;
	
	/** Number of columns in grid.*/
	private int gridColumns;
	
	/** Index mappings.*/
	private StateColorMap colorMap;

	/**
	 * @param colorMap the color mapping.
	 * @param keyLayout the keyLayout to set
	 */
	public ColorIndex(StateColorMap colorMap, int keyLayout) {
		super();
		this.colorMap = colorMap;
		this.keyLayout = keyLayout;
		
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		// set out the elements NOT
		
	}

	
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paint(java.awt.Graphics)
	 */
	@Override
	public void paint(Graphics g) {		
		super.paint(g);
		
		FontMetrics fm = g.getFontMetrics();
		
		List<StateColorMapEntry> maplist = colorMap.getMappings();
		
		// find the longest state label
		int maxwidth = 0;
		for (int il = 0; il < maplist.size(); il++) {
			StateColorMapEntry entry = maplist.get(il);
			String label = " "+entry.label+" "; // pad out with spaces each end
			
			int labelwidth = fm.stringWidth(label);
			if (labelwidth > maxwidth)
				maxwidth = labelwidth;
		}
		System.err.println("Max width = "+maxwidth);
		switch (keyLayout) {
		case VERTICAL:
			
			int loff = 5;
			int roff = 5;
			int box = 16;
			int gap = 4;
			int ll = maxwidth;
			
			int bw = loff+roff+gap+box+ll;
			int bh = 2*gap+box;
			
			///setPreferredSize(new Dimension(bw, bh*maplist.size()));
			
			for (int il = 0; il < maplist.size(); il++) {
				StateColorMapEntry entry = maplist.get(il);
				
				g.setColor(entry.color);
				g.fillRect(loff, (il)*bh+gap, box, box);
				g.setColor(Color.black);
				g.drawString(entry.label, loff+box+gap, (il)*bh+gap+box);
			//System.err.println("Index Add "+entry.label);
			}
			
			break;
		case HORIZONTAL:
			
			break;
		case GRID:
			
			break;
		}
		
		
		
	}



	/**
	 * @return the keyLayout
	 */
	public int getKeyLayout() {
		return keyLayout;
	}

	/**
	 * @param keyLayout the keyLayout to set
	 */
	public void setKeyLayout(int keyLayout) {
		this.keyLayout = keyLayout;
	}

	/**
	 * @return the gridRows
	 */
	public int getGridRows() {
		return gridRows;
	}

	/**
	 * @param gridRows the gridRows to set
	 */
	public void setGridRows(int gridRows) {
		this.gridRows = gridRows;
	}

	/**
	 * @return the gridColumns
	 */
	public int getGridColumns() {
		return gridColumns;
	}

	/**
	 * @param gridColumns the gridColumns to set
	 */
	public void setGridColumns(int gridColumns) {
		this.gridColumns = gridColumns;
	}

	
	
}
