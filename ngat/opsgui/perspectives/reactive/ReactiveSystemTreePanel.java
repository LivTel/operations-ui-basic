/**
 * Display the reactive system structure as a tree hierarchy. 
 */
package ngat.opsgui.perspectives.reactive;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import ngat.rcs.ers.Criterion;
import ngat.rcs.ers.Filter;
import ngat.rcs.ers.ReactiveSystemStructureProvider;
import ngat.rcs.ers.Rule;

/**
 * @author eng
 * 
 */
public class ReactiveSystemTreePanel extends JPanel {

	private ReactiveSystemStructureProvider ersStructureProvider;

	// Tree will use a DefaultTreeModel with root node
	private JTree tree;

	/** Root node of tree. */
	private DefaultMutableTreeNode root;

	// filters
	private List<Filter> filters;

	// crits
	private List<Criterion> crits;

	// rules
	private List<Rule> rules;

	// filter to criterion map
	private Map<String, List<Criterion>> filter2Crit;

	// criterion to rule map
	private Map<String, Rule> crit2Rule;

	// rule to rule map
	private Map<String, Rule> rule2Rule;

	Map<String, Filter> filterNameMap;
	Map<String, Criterion> criterionNameMap;
	Map<String, Rule> ruleNameMap;

	public ReactiveSystemTreePanel(
			ReactiveSystemStructureProvider ersStructureProvider)
			throws Exception {
		super();
		this.ersStructureProvider = ersStructureProvider;

		setLayout(new BorderLayout());

		// extract structure from ERS Structure Provider...

		// filters
		filters = ersStructureProvider.listFilters();

		// crits
		crits = ersStructureProvider.listCriteria();

		// rules
		rules = ersStructureProvider.listRules();

		// filter to criterion map
		filter2Crit = ersStructureProvider.getFilterCriterionMapping();

		// criterion to rule map
		crit2Rule = ersStructureProvider.getCriterionRuleMapping();

		// rule to rule map
		rule2Rule = ersStructureProvider.getRuleRuleMapping();
		System.err.println("Found mapping r:r: " + rule2Rule);

		// create name to object mappings
		filterNameMap = createFilterNameMapping(filters);
		criterionNameMap = createCriterionNameMapping(crits);
		ruleNameMap = createRuleNameMapping(rules);

		// Create root node.
		root = new DefaultMutableTreeNode("ERS");

		// Treemodel
		DefaultTreeModel tm = new DefaultTreeModel(root);

		// Create the tree based on the root.
		tree = new JTree(tm);

		// DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
		// tree.setRoot(root); // or similar

		// We need to extract structure from bottom up as thats how it gets to
		// us...

		// Recursive procedure but first...
		// look for top level rules - these have no forward mappings

		List<Rule> top = extractTopLevelRules();

		// now start recursion - find all feeds into these rules..
		// they will each EITHER have rules feeding them OR a criterion but not
		// both
		// these nodes will all be directly below the root node so pass it as
		// ref...
		decompose(top, root); //
		tree.repaint();

		JScrollPane scroll = new JScrollPane(tree);
		scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		add(scroll);

	}

	/**
	 * Extract the list of top level rules.
	 * 
	 * @return A list of top level rules.
	 */
	private List<Rule> extractTopLevelRules() {

		// iterate over list of rule and find some that dont have any feed outs
		List<Rule> top = new Vector<Rule>();
		for (int i = 0; i < rules.size(); i++) {
			Rule rule = rules.get(i);
			// if this rule has no feeds then its top level
			if (!rule2Rule.containsKey(rule.getRuleName())) {
				top.add(rule);
				System.err.println("Adding rule: " + rule.getRuleName()
						+ " to top level rules");
			}
		}

		return top;

	}

	private Map<String, Rule> createRuleNameMapping(List<Rule> rules) {

		Map<String, Rule> rmap = new HashMap<String, Rule>();
		for (int i = 0; i < rules.size(); i++) {
			Rule r = rules.get(i);
			rmap.put(r.getRuleName(), r);
		}
		return rmap;
	}

	private Map<String, Criterion> createCriterionNameMapping(
			List<Criterion> crits) {

		Map<String, Criterion> cmap = new HashMap<String, Criterion>();
		for (int i = 0; i < crits.size(); i++) {
			Criterion c = crits.get(i);
			cmap.put(c.getCriterionName(), c);
		}
		return cmap;
	}

	private Map<String, Filter> createFilterNameMapping(List<Filter> filters) {

		Map<String, Filter> fmap = new HashMap<String, Filter>();
		for (int i = 0; i < filters.size(); i++) {
			Filter f = filters.get(i);
			fmap.put(f.getFilterName(), f);
		}
		return fmap;
	}

	/** Decompose - find the set of rules (if any) which feed into this rule OR its criterion and input filter.
	 * Add a node for any rule attached to the base rule (as baseNode) and any feed-in criterion and filter.
	 * @param baseSet The list of rules which we wish to decompose.
	 * @param baseNode The node belonging to the possible feed-out node.
	 */
	private void decompose(List<Rule> baseSet, DefaultMutableTreeNode baseNode) {

		// first render the base rules
		for (int ib = 0; ib < baseSet.size(); ib++) {

			Rule rule = baseSet.get(ib);

			System.err.println("Decomposing: Rule: " + rule.getRuleName());

			// create node for it
			DefaultMutableTreeNode node = new DefaultMutableTreeNode(rule);
			baseNode.add(node);
			System.err.println("Adding node: " + node + " to " + baseNode);

			List<Rule> feedin = new Vector<Rule>();

			// find any feedins for this rule OR find its crit and filter.
			for (int ic = 0; ic < rules.size(); ic++) {

				Rule candidate = rules.get(ic);
				// System.err.println("Checking potential feed-in rule: "+candidate.getRuleName());

				Rule feedto = rule2Rule.get(candidate.getRuleName());
				if (feedto != null) {
					if (feedto.getRuleName().equals(rule.getRuleName())) {
						feedin.add(candidate);
						System.err.println("Adding feedin rule: "
								+ candidate.getRuleName() + " -> "
								+ rule.getRuleName());
					}
				}
			}

			// we now have a list of rules which feed into our rule (rule).
			// or there are no feed-ins so its a lowest level rule
			if (feedin.size() == 0) {
				insertCriterion(rule, node);
			} else {
				// we have some feed-ins to our rule so decompose further
				decompose(feedin, node);
			}

		} // next rule from base list

	}

	/** Insert a criterion node for the criterion feeding into rule. */
	private void insertCriterion(Rule rule, DefaultMutableTreeNode node) {

		System.err.println("Insert criterion for: " + rule.getRuleName());

		// search through c2r
		Iterator<String> icr = crit2Rule.keySet().iterator();
		while (icr.hasNext()) {
			String cname = icr.next();
			Rule crule = crit2Rule.get(cname);
			if (crule.getRuleName().equals(rule.getRuleName())) {
				// found the crit feeding into rule
				Criterion crit = criterionNameMap.get(cname);
				DefaultMutableTreeNode cnode = new DefaultMutableTreeNode(crit);
				node.add(cnode);

				// find the feed-in filter
				insertFilter(crit, cnode);
			}

		}

	}

	/** Insert filter node for the filter feeding criterion. */
	private void insertFilter(Criterion crit, DefaultMutableTreeNode node) {

		// find the feed in criterion and filter - we need to look thro f2c and
		// get lists of crits then search these for our target

		Iterator<String> ifc = filter2Crit.keySet().iterator();
		while (ifc.hasNext()) {
			String fname = ifc.next();
			Filter filter = filterNameMap.get(fname);

			List<Criterion> critList = filter2Crit.get(fname);
			for (int ic = 0; ic < critList.size(); ic++) {
				// are any of these our crit ?
				Criterion ccrit = critList.get(ic);
				if (ccrit.getCriterionName().equals(crit.getCriterionName())) {
					// this is one, add a node to it
					DefaultMutableTreeNode fnode = new DefaultMutableTreeNode(
							filter);
					node.add(fnode);
				}

			}

		}

	}

}
