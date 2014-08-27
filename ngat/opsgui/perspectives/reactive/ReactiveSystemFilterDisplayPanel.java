/**
 * 
 */
package ngat.opsgui.perspectives.reactive;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ngat.opsgui.base.ComponentFactory;
import ngat.rcs.ers.Filter;
import ngat.rcs.ers.ReactiveSystemStructureProvider;

/**
 * @author eng
 *
 */
public class ReactiveSystemFilterDisplayPanel extends JPanel {

	ReactiveSystemStructureProvider ersStructureProvider;

	private Map<String, JTextField> inmap;
	
	private Map<String, JTextField> outmap;
	
	public ReactiveSystemFilterDisplayPanel(
			ReactiveSystemStructureProvider ersStructureProvider) throws Exception {
		super();
		this.ersStructureProvider = ersStructureProvider;
		
		inmap = new HashMap<String, JTextField>();
		outmap = new HashMap<String, JTextField>();
		
		setLayout(new BorderLayout());
		
		// filters
		List<Filter> filters = ersStructureProvider.listFilters();

		JPanel inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
		
		for (int i = 0; i < filters.size(); i++) {
			
			Filter f = filters.get(i);
			
			// create and map panel
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			p.add(ComponentFactory.makeFixedWidthLabel(f.getFilterName(), 120));
			
			JTextField infield = new JTextField(10);
			p.add(infield);
			
			p.add(new JLabel(" -> "));
			
			JTextField outfield = new JTextField(10);
			p.add(outfield);
			
			inmap.put(f.getFilterName(), infield);
			outmap.put(f.getFilterName(), outfield);
			
			inner.add(p);
	
		}
		
		JScrollPane scroll = new JScrollPane(inner, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(inner);
		
		add(scroll);
		
	}
	
	public void handleFilterUpdate(String filterName, long time, Number input, Number output) {
		
		if (inmap.containsKey(filterName))  {
			if (input instanceof Integer)
				inmap.get(filterName).setText(String.format("%4d", input));
			else
				inmap.get(filterName).setText(String.format("%4.2f", input));
		
		}
		
		if (outmap.containsKey(filterName)) {
			if (output instanceof Integer)
				outmap.get(filterName).setText(String.format("%4d", output));
			else
				outmap.get(filterName).setText(String.format("%4.2f", output));
		
		}
	}
	
	
}
