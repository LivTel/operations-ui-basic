/**
 * 
 */
package ngat.opsgui.perspectives.reactive;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import ngat.opsgui.base.ComponentFactory;
import ngat.rcs.ers.ReactiveSystemStructureProvider;
import ngat.rcs.ers.Rule;

/**
 * @author eng
 * 
 */
public class ReactiveSystemRuleDisplayPanel extends JPanel {

	private ReactiveSystemStructureProvider ersStructureProvider;

	private Map<String, JTextField> map;

	private List[] ruleList;

	/** Available rules. */
	private List<Rule> rules;

	/** Rule to rule map. */
	private Map<String, Rule> rule2Rule;

	/** Mapping from name to rule. */
	private Map<String, Rule> ruleNameMap;

	public ReactiveSystemRuleDisplayPanel(
			ReactiveSystemStructureProvider ersStructureProvider)
			throws Exception {
		super();
		this.ersStructureProvider = ersStructureProvider;

		// define the mapping from rulename to rule field
		map = new HashMap<String, JTextField>();

		// create an array of Vectors to store the content of each rule
		// hierarchy level
		ruleList = new Vector[5];
		for (int i = 0; i < 5; i++) {
			ruleList[i] = new Vector();
		}

		setLayout(new BorderLayout());

		JPanel inner = new JPanel();
		

		// hold off on creating the sub-panels as we dont know how many are
		// needed yet...

		// rules
		rules = ersStructureProvider.listRules();

		// rule to rule map
		rule2Rule = ersStructureProvider.getRuleRuleMapping();

		// create mapping from rulenames to rules.
		ruleNameMap = createRuleNameMapping(rules);

		// These are the top level rules which go into panel #1
		List<Rule> top = extractTopLevelRules();

		// Stick em there...
		insertRules(0, top);

		// Find the highest number with some rules
		int hm = 0;
		for (int i = 0; i < 5; i++) {

			System.err.println("Level " + i + " contains " + ruleList[i].size()
					+ " rules");

			System.err.println("Rules ... " + ruleList[i]);

			if (ruleList[i].size() > 0)
				hm = i;
		}

		// What is the highest level rule ?
		//JOptionPane.showMessageDialog(null,
				//"Waiting for you, last rule level: " + hm);
			
		inner.setLayout(new GridLayout(1, hm+1));
		
		// Now fill the panels in reverse order ie 0 is top on the right, hm is
		// 0 on left
		for (int i = hm; i >= 0; i--) {

			// Create a panel to hold the text fields
			JPanel levelPanel = new JPanel();
			levelPanel.setLayout(new BoxLayout(levelPanel, BoxLayout.Y_AXIS));

			for (int j = 0; j < ruleList[i].size(); j++) {

				Rule rule = (Rule) ruleList[i].get(j);

				// create a field for this rule and map it to field map
				JPanel p = new JPanel();
				p.setLayout(new FlowLayout(FlowLayout.LEFT));

				p.add(ComponentFactory.makeFixedWidthLabel(rule.getRuleName(),
						80));

				JTextField field = new JTextField(5);
				p.add(field);

				map.put(rule.getRuleName(), field);

				levelPanel.add(p);

			}
			
			// pad out below or the fields will spread out over the height of the panel which looks naff as they will all
			// be differently spaced.
			levelPanel.add(new Box.Filler(new Dimension(100, 5), new Dimension(100, 500), new Dimension(100, 100)));

			JScrollPane scroll = new JScrollPane(levelPanel);
			scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scroll.setViewportView(levelPanel);

			inner.add(scroll);

		}

		add(inner);

	}

	private Map<String, Rule> createRuleNameMapping(List<Rule> rules) {

		Map<String, Rule> rmap = new HashMap<String, Rule>();
		for (int i = 0; i < rules.size(); i++) {
			Rule r = rules.get(i);
			rmap.put(r.getRuleName(), r);
		}
		return rmap;
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

	/**
	 * Insert the set of rules into the specified level.
	 * 
	 * @param level
	 *            The level.
	 * @param rules
	 *            The rules.
	 * @throws Exception
	 */
	private void insertRules(int level, List<Rule> rules) throws Exception {

		for (int i = 0; i < rules.size(); i++) {

			Rule rule = rules.get(i);
			ruleList[level].add(rule);

			// extract any feed-ins to this rule at (level + 1).
			List<Rule> sublist = extractFeedIns(level + 1, rule);

			if (!sublist.isEmpty()) {

				insertRules(level + 1, sublist);

			}

		}

	}

	/**
	 * Extract a list of rules which feed into the specified rule. Store at
	 * hierarchy specified level.
	 * 
	 * @param level
	 *            The level to store at.
	 * @param rule
	 *            The rule to decompose.
	 * @return A list of rules which feed into the specified rule.
	 * @throws Exception
	 */
	private List<Rule> extractFeedIns(int level, Rule rule) throws Exception {

		List<Rule> feedin = new Vector<Rule>();

		// find any feedins for this rule OR find its criterion and filter.
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

		return feedin;

	}

	public void handleRuleUpdate(String ruleName, long time, boolean ruleTriggered) {
		if (map.containsKey(ruleName)) {
			JTextField f = map.get(ruleName);
			if (ruleTriggered) {
				f.setBackground(Color.green);
				f.setText("true");
			} else {
				f.setBackground(Color.red);
				f.setText("false");
			}
		}

	}

}
