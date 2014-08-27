/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import ngat.phase2.IExecutionFailureContext;
import ngat.phase2.IProgram;
import ngat.phase2.IProposal;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITag;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.IUser;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.sms.ExecutionResource;
import ngat.sms.ExecutionResourceBundle;
import ngat.sms.ExecutionResourceUsageEstimationModel;
import ngat.sms.GroupItem;
import ngat.sms.bds.TestResourceUsageEstimator;
import ngat.util.BooleanLock;

/**
 * @author eng
 * 
 */
public class ObservationPanel extends JPanel {

	public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	// Ident
	JTextField tagField;
	JTextField userField;
	JTextField programField;
	JTextField proposalField;

	// Group
	JTextField groupField;
	JTextField timingField;
	JTextField priorityField;

	// Constraints
	JList constrList;

	// Progress
	JTextField startTimeField;
	JTextField elapsedTimeField;
	JTextField remainingTimeField;
	JTextField statusField;
	long startTime;
	
	JProgressBar progress;
	//ngat.rcs.sciops.ExecutionTimingCalculator etc;
	ProgressMonitorThread pmt;
	private ExecutionResourceUsageEstimationModel xrm;

	JTextField execTimeField;

	/** Layout manager. */
	GridBagLayout grid;

	/** Layout constraints. */
	GridBagConstraints gc;

	/** Layout manager 2. */
	GridBagLayout grid1;

	/** Layout constraints 2. */
	GridBagConstraints gc1;

	public ObservationPanel() {
		super(true);
		pmt = new ProgressMonitorThread();
		//etc = new ngat.rcs.sciops.ExecutionTimingCalculator();
		xrm = new TestResourceUsageEstimator();
		createPanel();
		pmt.start();
	}

	private void createPanel() {
		grid = new GridBagLayout();
		gc = new GridBagConstraints();
		grid1 = new GridBagLayout();
		gc1 = new GridBagConstraints();
		gc.anchor = GridBagConstraints.WEST;
		gc1.anchor = GridBagConstraints.WEST;
		gc.ipadx = 10;
		gc1.ipadx = 10;

		gc.insets = new Insets(0, 4, 0, 4);
		gc1.insets = new Insets(0, 4, 0, 6);

		setLayout(grid);

		JPanel idPanel = createIdPanel();
		makeComp(this, grid, gc, idPanel, 0, 0, GridBagConstraints.REMAINDER, 2);

		JPanel groupPanel = createGroupPanel();
		makeComp(this, grid, gc, groupPanel, 0, 2, GridBagConstraints.REMAINDER, 2);

		// JPanel obcPanel = createObcPanel();
		JPanel progressPanel = createProgressPanel();
		makeComp(this, grid, gc, progressPanel, 0, 4,GridBagConstraints.REMAINDER,2);
	}

	private JPanel createIdPanel() {

		JPanel panel = new JPanel(true);
		panel.setLayout(grid);
		panel.setBorder(new TitledBorder("Identity"));

		makeComp(panel, grid, gc, new JLabel("TAG"), 0, 0, 1, 1);
		tagField = new JTextField(10);
		makeComp(panel, grid, gc, tagField, 1, 0, 2, 1);

		makeComp(panel, grid, gc, new JLabel("PI"), 3, 0, 1, 1);
		userField = new JTextField(20);
		makeComp(panel, grid, gc, userField, 4, 0, 2, 1);

		makeComp(panel, grid, gc, new JLabel("Program"), 0, 1, 1, 1);
		programField = new JTextField(10);
		makeComp(panel, grid, gc, programField, 1, 1, 1, 1);

		makeComp(panel, grid, gc, new JLabel("Proposal"), 3, 1, 1, 1);
		proposalField = new JTextField(10);
		makeComp(panel, grid, gc, proposalField, 4, 1, 1, 1);

		return panel;
	}

	private JPanel createGroupPanel() {

		JPanel panel = new JPanel(true);
		panel.setLayout(grid);
		panel.setBorder(new TitledBorder("Group"));

		makeComp(panel, grid, gc, new JLabel("Group"), 0, 0, 1, 1);
		groupField = new JTextField(20);
		makeComp(panel, grid, gc, groupField, 1, 0, 2, 1);

		makeComp(panel, grid, gc, new JLabel("Priority"), 3, 0, 1, 1);
		priorityField = new JTextField(10);
		makeComp(panel, grid, gc, priorityField, 4, 0, 1, 1);

		makeComp(panel, grid, gc, new JLabel("Timing"), 0, 1, 1, 1);
		timingField = new JTextField(20);
		makeComp(panel, grid, gc, timingField, 1, 1, 2, 1);

		return panel;

	}

	private JPanel createObcPanel() {

		JPanel panel = new JPanel(true);
		panel.setLayout(grid);

		constrList = new JList();

		return panel;

	}

	private JPanel createProgressPanel() {

		JPanel panel = new JPanel(true);
		panel.setLayout(new GridLayout(5, 2));
		panel.setBorder(new TitledBorder("Progress"));

		panel.add(new JLabel("Started at"));
		startTimeField = new JTextField(20);
		panel.add(startTimeField);

		panel.add(new JLabel("Predicted time (s)"));
		execTimeField = new JTextField(6);
		panel.add(execTimeField);

		panel.add(new JLabel("Status"));
		statusField = new JTextField(20);
		panel.add(statusField);

		panel.add(new JLabel("Remaining (s)"));
		remainingTimeField = new JTextField(6);
		panel.add(remainingTimeField);
		
		panel.add(new JLabel("Progress"));
		progress = new JProgressBar();	
		panel.add(progress);

		return panel;

	}

	public void update(GroupItem group) {
		if (group == null)
			return;

		System.err.println("Update group: " + group);

		ITag tag = group.getTag();
		IUser user = group.getUser();
		IProgram program = group.getProgram();
		IProposal proposal = group.getProposal();

		String tagname = (tag != null ? tag.getName() : "UNKNOWN");
		String username = (user != null ? user.getName() : "UNKNOWN");
		String progname = (program != null ? program.getName() : "UNKNOWN");
		String propname = (proposal != null ? proposal.getName() : "UNKNOWN");

		// ident
		tagField.setText(tagname);
		userField.setText(username);
		programField.setText(progname);
		proposalField.setText(propname);

		// group
		groupField.setText(group.getName());

		ITimingConstraint timing = group.getTimingConstraint();

		double tp = 0;
		String timingName = "unknown";
		if (timing instanceof XFlexibleTimingConstraint) {
			timingName = "Flex";
		} else if (timing instanceof XMonitorTimingConstraint) {
			XMonitorTimingConstraint xmon = (XMonitorTimingConstraint) timing;
			long period = xmon.getPeriod();
			long window = xmon.getWindow();
			timingName = "Mon (Period=" + (period / 3600000) + "h, Window=" + (window / 3600000) + "h)";
			tp = 1.0;
		} else if (timing instanceof XEphemerisTimingConstraint) {
			timingName = "Phase";
		} else if (timing instanceof XMinimumIntervalTimingConstraint) {
			XMinimumIntervalTimingConstraint xmin = (XMinimumIntervalTimingConstraint) timing;
			long minint = xmin.getMinimumInterval();
			int maxrep = xmin.getMaximumRepeats();
			timingName = "MinInt (Interval=" + (minint / 3600000) + "h, Repeats=" + maxrep + ")";
			tp = 1.0;
		} else if (timing instanceof XFixedTimingConstraint) {
			XFixedTimingConstraint fix = (XFixedTimingConstraint) timing;
			long date = fix.getFixedTime();
			long slack = fix.getSlack();
			timingName = "Fixed at " + (sdf.format(new Date(date))) + " Slack=" + (slack / 60000) + "m)";
		}

		String priorityName = "";
		double priority = tp;
		if (group.isUrgent()) {
			priorityName += "*";
			priority += 2.0;
		}

		Color pcol = Color.white;
		switch (proposal.getPriority()) {
		case IProposal.PRIORITY_A:
			priorityName += "A";
			priority += 4.0;
			pcol = Color.green;
			break;
		case IProposal.PRIORITY_B:
			priorityName += "B";
			priority += 2.0;
			pcol = Color.cyan;
			break;
		case IProposal.PRIORITY_C:
			priorityName += "C";
			pcol = Color.orange;
			break;
		case IProposal.PRIORITY_Z:
			priorityName += "Z";
			priority -= 10.0;
			pcol = Color.red;
			break;
		}
		double po = proposal.getPriorityOffset();
		if (po < 0.0)
			priorityName += "-";
		else if (po > 0.0)
			priorityName += "+";

		priority += po;

		timingField.setText(timingName);
		priorityField.setBackground(pcol);
		priorityField.setText(priorityName + " (" + priority + ")");

		// obs constr
		java.util.List clist = group.listObservingConstraints();

		// sequence
		ISequenceComponent root = group.getSequence();
		// seqRootNode.removeAllChildren();
		// createSubTree(seqRootNode, root);
		// ((DefaultTreeModel)seqTree.getModel()).reload();
		// seqText.setText(ngat.rcs.sciops.DisplaySeq.display(0,root));

		// progress
		// start progress monitor updates.
		statusField.setText("EXECUTING");
		statusField.setBackground(Color.GREEN);

		//long exec = etc.calcExecTimeOfSequence((XIteratorComponent) root);
		
		ExecutionResourceBundle xrb = xrm.getEstimatedResourceUsage(group);
		ExecutionResource xt = xrb.getResource("TIME");
		long exec = (long)xt.getResourceUsage(); // this is millis
		
		
		startTime = System.currentTimeMillis();
		startTimeField.setText(sdf.format(new Date()));
		execTimeField.setText("" + (exec / 1000)); // seconds total
		remainingTimeField.setText("" + (exec / 1000)); // seconds to go
		
		progress.setMaximum((int)(exec/1000));
		progress.setValue(0);
		progress.setIndeterminate(false);
		
		pmt.release(exec);
	}

	public void completed(GroupItem group) {
		if (group == null)
			return;

		statusField.setText("COMPLETED");
		statusField.setBackground(Color.YELLOW);
		// we need to interrupt the updater for the group if its still running...
		pmt.quit();
	}

	public void failed(GroupItem group, IExecutionFailureContext error) {
		if (group == null)
			return;

		String errmsg = error.getErrorMessage();

		statusField.setText("FAILED: " + errmsg);
		statusField.setBackground(Color.RED);
		// we need to interrupt the updater for the group if its still running...
		pmt.quit();
	}

	/** Make a GridBag component. */
	private void makeComp(JPanel panel, GridBagLayout grid, GridBagConstraints c, Component comp, int x, int y, int w,
			int h) {
		c.gridx = x;
		c.gridy = y;
		c.gridwidth = w;
		c.gridheight = h;
		// c.anchor = GridBagConstraints.WEST;
		grid.setConstraints(comp, c);
		panel.add(comp);
	}
	private class ProgressMonitorThread extends Thread {

		BooleanLock release = new BooleanLock(false);

		volatile long duration;
		volatile boolean completed = false;

		long t = 0;

		ProgressMonitorThread() {
		    super();
		}

		@Override
		public void run() {

		    while (true) {

			// wait till released
			try {
			    System.err.println("ProgMonitor: waiting for release...");
			    release.waitUntilTrue(0);		
			} catch (Exception e) {}
			System.err.println("ProgMonitor: Start updating for: "+duration);


			// keep looping for a while until hit limit
			// TODO - we need to be able to reset this loop if another group comes in before we expect to have hasCompleted..
			// and also to allow a group hasCompleted message to be correctly handled
			t = 0;
			while (t < duration && (! completed)) { // t is in seconds
			    long trem =  startTime + duration - System.currentTimeMillis();
			    remainingTimeField.setText(""+(trem/1000));
			    progress.setValue((int)(t/1000));
			    System.err.println("ProgMonitor: Update with value: "+(int)(t/1000));
			    t += 10000;
			    try { Thread.sleep(10000L);} catch (Exception e) {}
			}
		    }

		}

		public void release(long duration) {
		    System.err.println("ProgMonitor:call release("+duration+")");
		    try {
			this.duration = duration;
			completed = false;
			release.setValue(true);
		    } catch (Exception e) {}
		}

		public void quit() { 
		    System.err.println("ProgMonitor:call quit()");
		       try {	
			   completed = true;
			   release.setValue(false);
			   interrupt();
		    } catch (Exception e) {}
		}

	    }


}
