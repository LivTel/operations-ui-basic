/**
 * 
 */
package ngat.opsgui.perspectives.reactive;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import ngat.rcs.ers.Criterion;
import ngat.rcs.ers.Filter;
import ngat.rcs.ers.ReactiveSystemStructureProvider;
import ngat.rcs.ers.Rule;

/**
 * @author eng
 * 
 */
public class ReactiveSystemsSummaryPanel extends JPanel {

	private ReactiveSystemStructureProvider ersStructureProvider;

	public ReactiveSystemsSummaryPanel(
			ReactiveSystemStructureProvider ersStructureProvider)
			throws Exception {
		super();
		this.ersStructureProvider = ersStructureProvider;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		// extract structure from ERS Structure Provider...
		
		// filters
		List<Filter> filters = ersStructureProvider.listFilters();

		JList flist = new JList((Vector) filters);
		flist.setVisibleRowCount(15);

		JScrollPane p2 = new JScrollPane(flist);
		p2.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// crits
		List<Criterion> crits = ersStructureProvider.listCriteria();

		JList clist = new JList((Vector) crits);
		clist.setVisibleRowCount(15);

		JScrollPane p3 = new JScrollPane(clist);
		p3.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		// rules
		List<Rule> rules = ersStructureProvider.listRules();

		JList rlist = new JList((Vector) rules);
		rlist.setVisibleRowCount(15);

		JScrollPane p1 = new JScrollPane(rlist);
		p1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		add(p2);
		add(p3);
		add(p1);
		
	}

}
