/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.Color;
import java.awt.Graphics;

/**
 * Descriptor information for an astro-catalog displayed on the tracking display
 * 
 * @author eng
 * 
 */
public class CatalogDisplayDescriptor {

	// Plot symbols
	public static final int OPEN_CIRCLE_SYMBOL = 1;
	public static final int FILLED_CIRCLE_SYMBOL = 2;
	public static final int OPEN_SQUARE_SYMBOL = 3;
	public static final int FILLED_SQUARE_SYMBOL = 4;
	public static final int OPEN_DIAMOND_SYMBOL = 5;
	public static final int FILLED_DIAMOND_SYMBOL = 6;
	public static final int CROSS_SYMBOL = 7;
	public static final int PLUS_SYMBOL = 8;
	
	// Label alignment
	public static final int LABEL_NORTH = 1;
	public static final int LABEL_NE = 2;
	public static final int LABEL_EAST = 3;
	public static final int LABEL_SE = 4;
	public static final int LABEL_SOUTH = 5;
	public static final int LABEL_SW = 6;
	public static final int LABEL_WEST = 7;
	public static final int LABEL_NW = 8;
	

	/** The name of the catalog to display in a key.*/
	private String name;

	/** Longer description to display in tooltip/popup.*/
	private String description;

	/** Symbol used for display*/
	private int symbol;

	/** True if the text uses an italic font.*/
	private boolean italic;

	/** Rendering color of symbol and text.*/
	private Color color;

	/** Label alignment relative to symbol.*/
	private int labelAlignment = LABEL_NE;
	
	/** True if the label should be displayed.*/
	private boolean showLabel;
	
	/** true if the symbol should be displayed.*/
	private boolean showSymbol;
	
	/** true if ALL items in catalaog should be displayed.*/
	private boolean showAll;
	
	/** Number of items from catalog to display IF the showAll is NOT set.
	 * Otherwise ALL items are shown.*/
	private int showCount = 0;
	
	/** How far forward we should project the target's track if at all.*/
	private int projectForward = 0;
	
	/** How far back we should project the target's track if at all.*/
	private int projectBackward = 0;
	
	/**
	 * @param name
	 */
	public CatalogDisplayDescriptor(String name) {
		super();
		this.name = name;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the symbol
	 */
	public int getSymbol() {
		return symbol;
	}

	/**
	 * @param symbol the symbol to set
	 */
	public void setSymbol(int symbol) {
		this.symbol = symbol;
	}

	/**
	 * @return the italic
	 */
	public boolean isItalic() {
		return italic;
	}

	/**
	 * @param italic the italic to set
	 */
	public void setItalic(boolean italic) {
		this.italic = italic;
	}

	/**
	 * @return the color
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the showLabel
	 */
	public boolean isShowLabel() {
		return showLabel;
	}

	/**
	 * @param showLabel the showLabel to set
	 */
	public void setShowLabel(boolean showLabel) {
		this.showLabel = showLabel;
	}

	/**
	 * @return the showSymbol
	 */
	public boolean isShowSymbol() {
		return showSymbol;
	}

	/**
	 * @param showSymbol the showSymbol to set
	 */
	public void setShowSymbol(boolean showSymbol) {
		this.showSymbol = showSymbol;
	}

	/**
	 * @return the labelAlignment
	 */
	public int getLabelAlignment() {
		return labelAlignment;
	}

	
	
	/**
	 * @return the showAll
	 */
	public boolean isShowAll() {
		return showAll;
	}

	/**
	 * @param showAll the showAll to set
	 */
	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	/**
	 * @return the showCount
	 */
	public int getShowCount() {
		return showCount;
	}

	/**
	 * @param showCount the showCount to set
	 */
	public void setShowCount(int showCount) {
		this.showCount = showCount;
	}

	/**
	 * @param labelAlignment the labelAlignment to set
	 */
	public void setLabelAlignment(int labelAlignment) {
		this.labelAlignment = labelAlignment;
	}


	/**
	 * @return the projectForward
	 */
	public int getProjectForward() {
		return projectForward;
	}

	/**
	 * @param projectForward the projectForward to set
	 */
	public void setProjectForward(int projectForward) {
		this.projectForward = projectForward;
	}

	/**
	 * @return the projectBackward
	 */
	public int getProjectBackward() {
		return projectBackward;
	}

	/**
	 * @param projectBackward the projectBackward to set
	 */
	public void setProjectBackward(int projectBackward) {
		this.projectBackward = projectBackward;
	}

	public void paintComponent(Graphics g, String text, boolean darken, int sx, int sy) {
		if (darken)
			g.setColor(color.darker());
		else
			g.setColor(color);
		
		switch (symbol) {
		case OPEN_CIRCLE_SYMBOL:
			g.drawOval(sx - 2, sy - 2, 4, 4);
			break;
		case FILLED_CIRCLE_SYMBOL:
			g.fillOval(sx - 2, sy - 2, 4, 4);
			break;
		case OPEN_SQUARE_SYMBOL:
			g.drawRect(sx-2, sy-2, 4, 4);
			break;
		case FILLED_SQUARE_SYMBOL:
			g.fillRect(sx-2, sy-2, 4, 4);
			break;
		case OPEN_DIAMOND_SYMBOL:
			final int[] xp1 = new int[] {sx, sx-2, sx, sx+2};
			final int[] yp1 = new int[] {sy-2, sy, sy+2, sy};
			g.drawPolygon(xp1, yp1, 4);
			break;
		case FILLED_DIAMOND_SYMBOL:
			final int[] xp2 = new int[] {sx, sx-2, sx, sx+2};
			final int[] yp2 = new int[] {sy-2, sy, sy+2, sy};
			g.fillPolygon(xp2, yp2, 4);
			break;
		case CROSS_SYMBOL:
			g.drawLine(sx-2, sy-2, sx+2, sy+2);
			g.drawLine(sx-2, sy+2, sx+2, sy-2);
			break;
		case PLUS_SYMBOL:
			g.drawLine(sx-2, sy, sx+2, sy);
			g.drawLine(sx, sy-2, sx, sy+2);
			break;
		}
	
		// width of text
		int tw = g.getFontMetrics().stringWidth(text);
		
		switch (labelAlignment) {
		case LABEL_NORTH:
			g.drawString(text, sx - tw/2, sy - 5);
			break;
		case LABEL_NE:
			g.drawString(text, sx + 5, sy - 5);
			break;
		case LABEL_EAST:
			g.drawString(text, sx + 5, sy);
			break;
		case LABEL_SE:
			g.drawString(text, sx + 5, sy + 5);
			break;
		case LABEL_SOUTH:
			g.drawString(text, sx- tw/2, sy + 5);
			break;
		case LABEL_SW:
			g.drawString(text, sx - tw - 5, sy + 5);
			break;
		case LABEL_WEST:
			g.drawString(text, sx - tw - 5, sy);
			break;
		case LABEL_NW:
			g.drawString(text, sx - tw - 5, sy - 5);
			break;
		}
	}


	
}
