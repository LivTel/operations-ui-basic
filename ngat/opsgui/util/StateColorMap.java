/**
 * 
 */
package ngat.opsgui.util;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * @author eng
 *
 */
public class StateColorMap {

	/** The default color, used when there is no mapping.*/
	private Color defaultColor;
	
	/** The default label, used when there is no mapping.*/
	private String defaultLabel;
	
	/** The state to color mapping.*/
	private Map<Integer, StateColorMapEntry> mappings;
	
	/** List object rendering of mappings./*/
	private List<StateColorMapEntry> list;
	
	/** Create an empty state-color mapping.*/
	public StateColorMap(Color defaultColor, String defaultLabel) {
		this.defaultColor = defaultColor;
		this.defaultLabel = defaultLabel;
		mappings = new HashMap<Integer, StateColorMapEntry>();		
	}
	
	public void addColorLabel(int state, Color color, String label) {
		mappings.put(state, new StateColorMapEntry(state, color, label));
	}
	
	public Color getColor(int state) {
		if (!mappings.containsKey(state))
			return defaultColor;
		return mappings.get(state).color;
	}
	
	public String getLabel(int state) {
		if (!mappings.containsKey(state))
			return defaultLabel;
		return mappings.get(state).label;
	}

	public List<StateColorMapEntry> getMappings() {
		if (list == null) {			
			list = new Vector<StateColorMapEntry>();
			Iterator<Integer> im = mappings.keySet().iterator();
			while (im.hasNext()) {
				int key = im.next();
				StateColorMapEntry entry = mappings.get(key);
				list.add(entry);
			}
		}
		return list;
	}
	
	/**
	 * @return the defaultColor
	 */
	public Color getDefaultColor() {
		return defaultColor;
	}

	/**
	 * @param defaultColor the defaultColor to set
	 */
	public void setDefaultColor(Color defaultColor) {
		this.defaultColor = defaultColor;
	}

	/**
	 * @return the defaultLabel
	 */
	public String getDefaultLabel() {
		return defaultLabel;
	}

	/**
	 * @param defaultLabel the defaultLabel to set
	 */
	public void setDefaultLabel(String defaultLabel) {
		this.defaultLabel = defaultLabel;
	}
	
	
}
