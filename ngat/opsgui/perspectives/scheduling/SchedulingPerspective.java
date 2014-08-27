/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.simulation.SchedulingUpdateSimulator;
import ngat.opsgui.perspectives.phase2.Phase2;
import ngat.opsgui.test.DockingManager;
import ngat.opsgui.test.ComponentDescriptor;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetric;
import ngat.sms.ScoreMetricsSet;

/**
 * The perspective which shows telemetry from the scheduler / dispatcher
 * 
 * @author eng
 * 
 */
public class SchedulingPerspective extends Perspective implements SchedulingStatusUpdateListener,
		ServiceAvailabilityListener {

	// TODO TEMP this should be run via the SIM perspective and connected via
	// the ENG perspective
	private SchedulingUpdateSimulator sim;

	/** Simulation menu. */
	JMenu simulationMenu;

	/** Tagging menu. */
	JMenu tagMenu;

	/** Scheduling master tab panel. */
	JTabbedPane schedulingPane;

	DockingManager dm;

	/** Controller for sweep navigation panels. */
	SchedulingSweepNavigationController controller;

	// candidate table
	SchedulingSweepDisplayMasterPanel schedulingCandidateMasterPanel;

	// rejects table
	SchedulingRejectsPanel rejectPanel;

	// sweep stats
	SchedulingSweepStatisticsPanel statisticsPanel;

	// summary table
	SchedulingSummaryPanel summaryPanel;

	// contention
	SchedulingContentionPanel contentionPanel;

	// metrics
	SchedulingSweepMetricsPanel metricsPanel;

	// scoring
	SchedulingScorePanel scoringPanel;

	// group
	GroupAuditPanel groupPanel;

	/** Stores sweep information. */
	private List<SweepEntry> sweeps;

	/** The current (latest) sweep. */
	private SweepEntry latestSweep;

	/** Holds the current Tagged list of groups. */
	private TagList tagList;

	/** Holds the current Audit list of groups. */
	private TagList auditList;

	public SchedulingPerspective(JFrame frame) {
		super(frame);
		perspectiveName = "S";

		setLayout(new BorderLayout());

		// data sources
		sweeps = new Vector<SweepEntry>();

		tagList = new TagList();
		auditList = new TagList();

		schedulingPane = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);

		dm = new DockingManager(schedulingPane);

		controller = new SchedulingSweepNavigationController(sweeps);

		// Sweep Candidates
		SchedulingCandidatePanel schedulingCandidateDisplayPanel = new SchedulingCandidatePanel(sweeps, tagList,
				auditList);
		schedulingCandidateMasterPanel = new SchedulingSweepDisplayMasterPanel(controller,
				schedulingCandidateDisplayPanel);
		dm.attach(new ComponentDescriptor(schedulingCandidateMasterPanel, "Candidates"));
		// schedulingPane.addTab("Candidates", schedulingCandidateMasterPanel);

		controller.addSweepDisplay(schedulingCandidateMasterPanel);

		// Sweep rejects
		rejectPanel = new SchedulingRejectsPanel(sweeps, tagList);
		SchedulingSweepDisplayMasterPanel rejectsMasterPanel = new SchedulingSweepDisplayMasterPanel(controller,
				rejectPanel);
		dm.attach(new ComponentDescriptor(rejectsMasterPanel, "Rejects"));
		// schedulingPane.addTab("Rejects", rejectsMasterPanel);

		controller.addSweepDisplay(rejectsMasterPanel);

		// Sweep stats
		statisticsPanel = new SchedulingSweepStatisticsPanel(sweeps);
		SchedulingSweepDisplayMasterPanel statisticsMasterPanel = new SchedulingSweepDisplayMasterPanel(controller,
				statisticsPanel);
		dm.attach(new ComponentDescriptor(statisticsPanel, "Stats"));
		// schedulingPane.addTab("Stats", statisticsMasterPanel);

		controller.addSweepDisplay(statisticsMasterPanel);

		// Metrics
		metricsPanel = new SchedulingSweepMetricsPanel(sweeps);
		SchedulingSweepDisplayMasterPanel metricsMasterPanel = new SchedulingSweepDisplayMasterPanel(controller,
				metricsPanel);
		dm.attach(new ComponentDescriptor(metricsPanel, "Metrics"));
		// schedulingPane.addTab("Metrics", metricsMasterPanel);
		controller.addSweepDisplay(metricsMasterPanel);

		// summary
		summaryPanel = new SchedulingSummaryPanel();
		dm.attach(new ComponentDescriptor(summaryPanel, "Summary"));
		// schedulingPane.addTab("Summary", summaryPanel);

		// contention
		contentionPanel = new SchedulingContentionPanel(sweeps);
		dm.attach(new ComponentDescriptor(contentionPanel, "Contention"));
		// schedulingPane.addTab("Contention", contentionPanel);

		// scoring
		scoringPanel = new SchedulingScorePanel(sweeps, tagList);
		dm.attach(new ComponentDescriptor(scoringPanel, "Scoring"));
		// schedulingPane.addTab("Scoring", scoringPanel);

		tagList.addTagListUpdateListener(scoringPanel);

		// group
		groupPanel = new GroupAuditPanel(sweeps);
		dm.attach(new ComponentDescriptor(groupPanel, "Group"));
		// schedulingPane.addTab("Group", groupPanel);
	
		auditList.addTagListUpdateListener(groupPanel);

		// TEST LOOKAHEAD SEQUENCE
		TestLookaheadPanel tlp = new TestLookaheadPanel();
		dm.attach(new ComponentDescriptor(tlp, "LAS"));
		schedulingPane.setBackgroundAt(8, Color.orange);
		schedulingPane.setForegroundAt(8, Color.black);

		tlp.runtest();
		// MAYBE put in super constructor ????
		createMenus();

		add(schedulingPane, BorderLayout.CENTER);

	}

	/** Create menus. */

	private void createMenus() {

		simulationMenu = new JMenu("Simulation");
		SchedSimMenuListener sim = new SchedSimMenuListener();

		JMenuItem simStartMenu = new JMenuItem("Start");
		simStartMenu.setActionCommand("start-simulation");
		simStartMenu.addActionListener(sim);

		simulationMenu.add(simStartMenu);

		JMenuItem prescanStartMenu = new JMenuItem("Lookahead...");
		prescanStartMenu.setActionCommand("start-prescan");
		prescanStartMenu.addActionListener(sim);
		simulationMenu.add(simStartMenu);

		menus.add(simulationMenu);

		tagMenu = new TagMenu(tagList);

		menus.add(tagMenu);

	}

	// private SchedulingCandidatePanel createSchedulingCandidatePanel
	// (List<SweepEntry> sweeps) {
	// return new SchedulingCandidatePanel(sweeps);
	// }

	// rejects table
	// private SchedulingRejectsPanel
	// createSchedulingRejectPanel(List<SweepEntry> sweeps) {
	// return new SchedulingRejectsPanel(sweeps);
	// }

	// sweep stats
	// private JPanel createSweepStatisticsPanel() {}

	// contention
	// private JPanel createContentionPanel() {}

	// scoring/ metrics
	// private JPanel createNetricsPanel() {}

	// group
	// private JPanel createGroupPanel() {}

	@Override
	public void candidateAdded(String q, GroupItem group, ScoreMetricsSet scoreMetrics, double score, int rank)
			throws RemoteException {

		System.err.println("SchedP: recieved candidate: " + group.getName());

		if (scoreMetrics == null)
			System.err.println("SchedP: candidate has NO metrics");
		else {
			Iterator<ScoreMetric> im = scoreMetrics.listMetrics();
			while (im.hasNext()) {
				ScoreMetric sm = im.next();
				System.err.println("SchedP: candidate has : " + sm.getMetricName() + " = " + sm.getMetricValue());
			}
		}

		// local cache
		GroupItem ngroup = Phase2.getPhase2().addGroup(group);

		CandidateEntry c = new CandidateEntry(q, ngroup, scoreMetrics, score, rank);

		latestSweep.addCandidate(c);
	}

	@Override
	public void candidateRejected(String q, GroupItem group, String reason) throws RemoteException {
		// System.err.println("SchedSIM: recieved reject: " +
		// group.getName()+" : "+reason);

		// local cache
		GroupItem ngroup = Phase2.getPhase2().addGroup(group);

		// RejectEntry r = new RejectEntry(group, reason);
		// latestSweep.addReject(r);
	}

	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {
		// TODO Auto-generated method stub
		System.err.println("SchedP: received select: " + sched);
		latestSweep.setDuration(time - latestSweep.getTime());
		System.err.println("Sweep statistics: sweep: NC: " + latestSweep.getCandidates().size() + " Duration: "
				+ latestSweep.getDuration());

		sweeps.add(latestSweep);

		// this would be a good time to sort the candidates into rank order..
		latestSweep.sortByRank();
		// Collections.sort(latestSweep.getCandidates());

		controller.updateSweep();
		summaryPanel.updateSweep(sweeps.size(), latestSweep, sched);
		scoringPanel.updateData(sched);
		contentionPanel.updateData();
		// groupPanel . dostuff -ie add an entry onto the audit table...
		groupPanel.updateSweep(latestSweep, sched);
	}

	@Override
	public void scheduleSweepStarted(long time, int sweepId) throws RemoteException {
		// TODO Auto-generated method stub
		System.err.println("SP: recieved sweep start: " + sweepId);
		latestSweep = new SweepEntry(time, sweepId);
	}

	private class SchedSimMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			String command = ae.getActionCommand();

			if (command.equals("start-simulation")) {
				try {
					SchedulingUpdateSimulator sim = new SchedulingUpdateSimulator(100);
					sim.addSchedulingUpdateListener(SchedulingPerspective.this);
					sim.run(30000L);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (command.equals("start-prescan")) {
				// run a feasibility prescan..

				// FeasibilityPrescanController fsc;

			}

		}

	}

	private class TagMenu extends JMenu {

		private TagList tagList;

		/**
		 * @param tagList
		 */
		public TagMenu(final TagList tagList) {
			super("Tagging");
			this.tagList = tagList;

			JMenuItem showTagsItem = new JMenuItem("Show...");
			showTagsItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					List<GroupItem> tags = tagList.getTaggedGroups();
					for (int is = 0; is < tags.size(); is++) {
						System.err.println("Tagged group: " + tags.get(is));
					}
				}
			});
			add(showTagsItem);

			JMenuItem addTagItem = new JMenuItem("Add...");
			add(addTagItem);

			JMenuItem removeTagItem = new JMenuItem("Remove...");
			add(removeTagItem);

			JMenuItem clearTagsItem = new JMenuItem("Clear all");
			clearTagsItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					tagList.clearTags();
				}
			});
			add(clearTagsItem);

		}

	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub

	}

	@Override
	public void schedulerMessage(int arg0, String arg1) throws RemoteException {
		// TODO popup a warning message and record time so we dont get too many of these or its on a list on a  panel
		
	}

}
