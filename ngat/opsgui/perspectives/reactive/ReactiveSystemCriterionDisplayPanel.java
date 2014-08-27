/**
 * 
 */
package ngat.opsgui.perspectives.reactive;

import java.awt.BorderLayout;
import java.awt.Color;
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
import ngat.rcs.ers.Criterion;
import ngat.rcs.ers.ReactiveSystemStructureProvider;

/**
 * @author eng
 *
 */
public class ReactiveSystemCriterionDisplayPanel extends JPanel {

	ReactiveSystemStructureProvider ersStructureProvider;

	private Map<String, JTextField> inmap;

	
	public ReactiveSystemCriterionDisplayPanel(
			ReactiveSystemStructureProvider ersStructureProvider) throws Exception {
		super();
		this.ersStructureProvider = ersStructureProvider;
		
		inmap = new HashMap<String, JTextField>();
		
		
		setLayout(new BorderLayout());
		
		// filters
		List<Criterion> crits = ersStructureProvider.listCriteria();

		JPanel inner = new JPanel();
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
		
		for (int i = 0; i < crits.size(); i++) {
		
			Criterion crit = crits.get(i);
			
			// create and map panel
			JPanel p = new JPanel();
			p.setLayout(new FlowLayout(FlowLayout.LEFT));
			
			p.add(ComponentFactory.makeFixedWidthLabel(crit.getCriterionName(), 120));
			
			JTextField infield = new JTextField(6);
			p.add(infield);
			
			
			inmap.put(crit.getCriterionName(), infield);
			
			
			inner.add(p);
	
		}
		
		JScrollPane scroll = new JScrollPane(inner, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.setViewportView(inner);
		
		add(scroll);
		
	}
	
	
	public void handleCriterionUpdate(String critName, long time, boolean satisfied) {
		if (inmap.containsKey(critName)) {
		JTextField f = inmap.get(critName);
			if (satisfied) {
			f.setBackground(Color.green);	
			f.setText("true");
			} else {
				f.setBackground(Color.red);	
				f.setText("false");				
			}
		}
		
	}
	
	
}
