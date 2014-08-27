/**
 * 
 */
package ngat.smsgui.test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Polygon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ProgressMonitor;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.opsgui.perspectives.scheduling.CandidateRow;
import ngat.opsgui.perspectives.scheduling.CandidateTable;
import ngat.opsgui.perspectives.scheduling.CandidateTableModel;
import ngat.oss.transport.RemotelyPingable;
import ngat.phase2.IExecutionFailureContext;
import ngat.phase2.IQosMetric;
import ngat.phase2.XTimePeriod;
import ngat.sms.ChargeAccountingModel;
import ngat.sms.Disruptor;
import ngat.sms.ExecutionResourceBundle;
import ngat.sms.ExecutionUpdateManager;
import ngat.sms.ExecutionUpdateMonitor;
import ngat.sms.ExecutionUpdater;
import ngat.sms.FeasibilityPrescanController;
import ngat.sms.FeasibilityPrescanMonitor;
import ngat.sms.FeasibilityPrescanUpdateListener;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusProvider;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetricsSet;
import ngat.sms.models.standard.StandardChargeAccountingModel;
import ngat.sms.util.PrescanEntry;
import ngat.smsgui.DayNightPanel;
import ngat.smsgui.DisruptorPanel;
import ngat.smsgui.FeasibilityTable;
import ngat.smsgui.FeasibilityTableModel;
import ngat.smsgui.HistoryRow;
import ngat.smsgui.HistoryTable;
import ngat.smsgui.HistoryTableModel;
import ngat.smsgui.MoonPanel;
import ngat.smsgui.SequenceTable;
import ngat.smsgui.SequenceTableModel;
import ngat.smsgui.TimePanel;
import ngat.smsgui.TimeScalePanel;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class TestGui extends UnicastRemoteObject implements SchedulingStatusUpdateListener, FeasibilityPrescanUpdateListener,
		ExecutionUpdater {
	public static final Color GRAPH_BGCOLOR = Color.black;

	public static final Color CHART_BGCOLOR = new Color(153, 203, 203);

	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss z");

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

	int ii = 0;
	ProgressMonitor progress;
	
	// HEADER
	JTextField schedNameField;
	JTextField schedOnlineField;
	JTextField xmOnlineField;

	// AREA 1
	private CandidateTableModel ctm;
	private HistoryTableModel htm;
	private FeasibilityTableModel ftm;
	private SequenceTableModel stm; // TODO rename as: lookaheadtablemodel
	
	private JPanel feasPanel;
	
	private List<CandidateRow> crows;

	private int nsweep = 0; // count sweeps we have seen

	// AREA 2
	TimePanel timePanel;
	DayNightPanel dayNightPanel;
	MoonPanel moonPanel;
	DisruptorPanel disruptorPanel;
	TimeScalePanel timeScalePanel;

	// AREA 3
	TimeSeries tsContention;
	TimeSeries tsContentionPredict;
	TimeSeries tsScore;

	private String shost;
	private ISite site;

	public TestGui(String shost, ISite site) throws RemoteException {
		super();
		this.shost = shost;
		this.site = site;
		crows = new Vector<CandidateRow>();
	}

	public void display() {

		JFrame f = new JFrame("Test GUI");

		// HEADER
		JPanel top = new JPanel(true);
		top.setLayout(new FlowLayout(FlowLayout.LEFT));

		JLabel idlabel = new JLabel("LT", new ImageIcon(""), SwingConstants.LEFT);
		top.add(idlabel);

		top.add(new JLabel("Scheduler"));
		schedNameField = new JTextField(12);
		schedNameField.setText("BDS");
		top.add(schedNameField);

		schedOnlineField = new JTextField(12);
		schedOnlineField.setText("UKNOWN");
		top.add(schedOnlineField);

		top.add(new JLabel("ExMgr"));

		xmOnlineField = new JTextField(12);
		xmOnlineField.setText("UKNOWN");
		top.add(xmOnlineField);

		// AREA 1 (tables)
		JTabbedPane jtp = new JTabbedPane();

		htm = new HistoryTableModel();
		HistoryTable histTable = new HistoryTable(htm);
		JScrollPane jspHist = new JScrollPane(histTable);
		jspHist.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jtp.addTab("History", jspHist);

		ChargeAccountingModel cam = new StandardChargeAccountingModel();

		ctm = new CandidateTableModel();
		ctm.setCam(cam);
		JTable sweepTable = new CandidateTable(ctm);
		JScrollPane jspSweep = new JScrollPane(sweepTable);
		jspSweep.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		JPanel sweepPanel = new JPanel(true);
		sweepPanel.setLayout(new BorderLayout());
		JPanel sweepControlPanel = new JPanel(true);
		sweepControlPanel.setLayout(new BoxLayout(sweepControlPanel, BoxLayout.X_AXIS));

		ActionListener sweepControlListener = new SweepControlListener();

		JButton sweepWayBackBtn = new JButton("[<<");
		sweepWayBackBtn.addActionListener(sweepControlListener);
		sweepWayBackBtn.setActionCommand("way-back");
		sweepControlPanel.add(sweepWayBackBtn);

		JButton sweepBackBtn = new JButton("<");
		sweepBackBtn.addActionListener(sweepControlListener);
		sweepBackBtn.setActionCommand("back");
		sweepControlPanel.add(sweepBackBtn);

		JButton sweepFwdBtn = new JButton(">");
		sweepFwdBtn.addActionListener(sweepControlListener);
		sweepFwdBtn.setActionCommand("fwd");
		sweepControlPanel.add(sweepFwdBtn);

		JButton sweepWayFwdBtn = new JButton(">>]");
		sweepWayFwdBtn.addActionListener(sweepControlListener);
		sweepWayFwdBtn.setActionCommand("way-fwd");
		sweepControlPanel.add(sweepWayFwdBtn);

		sweepPanel.add(jspSweep, BorderLayout.CENTER);
		sweepPanel.add(sweepControlPanel, BorderLayout.SOUTH);

		jtp.addTab("Sweep", sweepPanel);

		ftm = new FeasibilityTableModel();
		FeasibilityTable feasTable = new FeasibilityTable(ftm);
		JScrollPane jspFeas = new JScrollPane(feasTable);
		jspFeas.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		feasPanel = new JPanel(true);
		JButton prescanBtn = new JButton("Exec prescan");
		prescanBtn.addActionListener(new PrescanListener());
		feasPanel.setLayout(new BorderLayout());
		feasPanel.add(jspFeas, BorderLayout.CENTER);
		feasPanel.add(prescanBtn, BorderLayout.SOUTH);
		jtp.addTab("Feasibility", feasPanel);

		stm = new SequenceTableModel();
		SequenceTable seqTable = new SequenceTable(stm);
		JScrollPane jspSeq = new JScrollPane(seqTable);
		jspSeq.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		jtp.addTab("LookAhead", jspSeq);

		jtp.setBorder(BorderFactory.createTitledBorder("Tables"));

		// AREA 2 (time lines)

		timePanel = new TimePanel();

		long start = System.currentTimeMillis() - 9 * 3600 * 1000L;
		long end = start + 18 * 3600 * 1000L;
		timePanel.setTimeLimits(start, end);

		dayNightPanel = new DayNightPanel(timePanel, site);
		timePanel.addCategoryPanel(dayNightPanel);

		moonPanel = new MoonPanel(timePanel, site);
		timePanel.addCategoryPanel(moonPanel);

		// some disruptors
		List<Disruptor> dlist = new Vector<Disruptor>();
		for (int i = 0; i < 10; i++) {
			long t1 = start + (long) (Math.random() * (end - start));
			long t2 = t1 + (long) (Math.random() * 3600000.0);
			Disruptor d = new Disruptor("test", "testclass", new XTimePeriod(t1, t2));
			dlist.add(d);
		}
		disruptorPanel = new DisruptorPanel(timePanel, dlist);
		timePanel.addCategoryPanel(disruptorPanel);

		timeScalePanel = new TimeScalePanel(timePanel);
		timePanel.addCategoryPanel(timeScalePanel);

		timePanel.setBorder(BorderFactory.createTitledBorder("Timeline"));
		// JPanel area2 = new JPanel(true);
		// area2.setBorder(BorderFactory.createTitledBorder("Timeline"));
		// area2.add(timePanel);

		// AREA 3 (graphs)
		TimeSeriesCollection tsc1 = new TimeSeriesCollection();
		tsContention = new TimeSeries("CONT", Second.class);
		tsContention.setMaximumItemCount(500);
		tsc1.addSeries(tsContention);

		tsContentionPredict = new TimeSeries("CONT_PRED", Second.class);
		tsContentionPredict.setMaximumItemCount(500);
		tsc1.addSeries(tsContentionPredict);

		JFreeChart chart1 = makeChart("ContentionPlot", "Contention", 0.0, 5.0, tsc1, Color.cyan);
		ChartPanel cp1 = new ChartPanel(chart1);
		cp1.setPreferredSize(new Dimension(300, 150));
		XYPlot plot1 = chart1.getXYPlot();
		XYItemRenderer r1 = plot1.getRenderer();
		r1.setSeriesPaint(0, Color.yellow);
		r1.setSeriesShape(0, new Polygon(new int[] { -1, 1, 0 }, new int[] { -1, -1, 1 }, 3));
		r1.setSeriesStroke(0, new BasicStroke(1.0f));

		r1.setSeriesPaint(1, Color.cyan);
		//r1.setSeriesShape(1, new Polygon(new int[] { -1, 1, 0 }, new int[] { -1, -1, 1 }, 3));
		r1.setSeriesStroke(1, new BasicStroke(1.0f));

		XYLineAndShapeRenderer lsr1 = (XYLineAndShapeRenderer) r1;
		lsr1.setDrawOutlines(true);
		lsr1.setSeriesShapesVisible(0, true);
		lsr1.setSeriesLinesVisible(0, false);
				
		TimeSeriesCollection tsc2 = new TimeSeriesCollection();
		tsScore = new TimeSeries("SCORE", Second.class);
		tsScore.setMaximumItemCount(500);
		tsc2.addSeries(tsScore);

		JFreeChart chart2 = makeChart("ScorePlot", "Score", 0.0, 2.0, tsc2, Color.orange);
		ChartPanel cp2 = new ChartPanel(chart2);
		cp2.setPreferredSize(new Dimension(300, 150));
		XYPlot plot2 = chart2.getXYPlot();
		XYItemRenderer r2 = plot2.getRenderer();
		
		
		
		
		// AREA 4 (current)

		// Base    
		GridBagLayout grid = new GridBagLayout();
		GridBagConstraints gc = new GridBagConstraints();

        gc.anchor = GridBagConstraints.WEST;      
        gc.ipadx = 10;
        gc.insets = new Insets(0, 4, 0, 4);
      
		Container c = f.getContentPane();

		JPanel base = new JPanel(true);
		base.setLayout(new BorderLayout());

		JPanel main = new JPanel(true);
		main.setLayout(grid);
	
		makeComp(main, grid, gc, jtp, 0, 0, 20, 10);
		makeComp(main, grid, gc, timePanel, 0, 10, 15, 4);
		//makeComp(main, grid, gc, cp1, 20, 0, 4, 4);
		//makeComp(main, grid, gc, cp2, 20, 4, 4, 4);
			
		base.add(main, BorderLayout.CENTER);
		base.add(top, BorderLayout.NORTH);
		
		c.add(base);
		f.pack();
		f.setVisible(true);

	}

	/** Build the chart. */
	protected JFreeChart makeChart(String title, String ylabel, double lolim, double hilim, TimeSeriesCollection tsc,
			Color color) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(GRAPH_BGCOLOR);
		plot.setDomainGridlinePaint(Color.BLUE);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.blue);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
		XYItemRenderer r = plot.getRenderer();

		

		ValueAxis axis = plot.getDomainAxis();
		System.err.println("X axis is a " + axis.getClass().getName());
		if (axis instanceof DateAxis) {
			((DateAxis) axis).setDateFormatOverride(odf);
			System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		axis.setFixedAutoRange(12 * 3600000.0); // 12 hour

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lolim, hilim);

		// LegendTitle legendTitle = new LegendTitle(plot);
		// legendTitle.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(legendTitle);

		return chart;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.SchedulingUpdateListener#candidateAdded(java.lang.String,
	 * ngat.sms.GroupItem, ngat.sms.ScoreMetricsSet, double, int)
	 */
	@Override
	public void candidateAdded(String qid, GroupItem group, ScoreMetricsSet metrics, double score, int rank)
			throws RemoteException {

		System.err.printf("Candidate added to queue %s, Group = %s, rank = %2d, score = %3.2f \n", qid,
				group.getName(), rank, score);

		// store this item until we know the list is done
		CandidateRow crow = new CandidateRow();
		crow.group = group;
		crow.rank = rank;
		crow.score = score;
		crows.add(crow);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ngat.sms.SchedulingUpdateListener#candidateSelected(ngat.sms.ScheduleItem
	 * )
	 */
	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {
		// TODO Auto-generated method stub

		System.err.println("Candidate selected: " + sched);

		// dump the current sweep table
		ctm.setRows(crows);

		// update hist table
		if (sched != null) {
			HistoryRow hrow = new HistoryRow();
			hrow.group = sched.getGroup();
			hrow.score = sched.getScore();
			hrow.time = System.currentTimeMillis();// or use sweep time
			hrow.sweep = nsweep;
			hrow.status = HistoryRow.RUNNING;
			htm.addRow(hrow);
		}

		final double fscore = sched.getScore();
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// update the contention graph
				tsContention.add(new Second(new Date()), crows.size());

				// update the score graph
				tsScore.add(new Second(new Date()), fscore);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.sms.SchedulingUpdateListener#scheduleSweepStarted(long, int)
	 */
	@Override
	public void scheduleSweepStarted(long time, int sweep) throws RemoteException {
		System.err.printf("Sweep %2d startTime at %tT \n", sweep, time);
		nsweep = sweep;

		// clear the sweep table
		ctm.clearTable();
		crows.clear();

	}

	@Override
	public void prescanStarting(int ngc) throws RemoteException {
		System.err.println("Prescan clear, checking: "+ngc+" groups");
		ftm.clear();
		feasPanel.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		if (progress != null) {
				progress.setMaximum(ngc);
		}
	}

	@Override
	public void prescanUpdate(PrescanEntry pse) throws RemoteException {
		System.err.println("Prescan update: " + pse);
		ftm.addEntry(pse);
		if (progress != null) {
			progress.setProgress(ftm.getRowCount());
			progress.setNote("Processed "+ftm.getRowCount()+" groups");
		}
	}

	public void startPinger() {
		Pinger pinger = new Pinger();
		pinger.start();
	}

	public void startTimePanelRefresh(){ 
		TimePanelRefresh refresh = new TimePanelRefresh();
		refresh.start();	
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);
			String shost = cfg.getProperty("sched-host", "localhost");

			double lat = Math.toRadians(cfg.getDoubleValue("lat"));
			double lon = Math.toRadians(cfg.getDoubleValue("long"));
			ISite site = new BasicSite("obs", lat, lon);

			TestGui gui = new TestGui(shost, site);
			gui.display();

			System.err.println("Preparing registrations...");

			// now lookup a remote scheduler and register with it...
			SchedulingStatusProvider sched = (SchedulingStatusProvider) Naming.lookup("rmi://" + shost + "/Scheduler");
			sched.addSchedulingUpdateListener(gui);
			System.err.println("Registered as sched listener on: " + sched);

			FeasibilityPrescanMonitor fpmon = (FeasibilityPrescanMonitor) Naming.lookup("rmi://" + shost
					+ "/FeasibilityPrescanner");
			fpmon.addFeasibilityPrescanUpdateListener(gui);
			System.err.println("Registered as prescan listener on: " + fpmon);

			ExecutionUpdateMonitor xum = (ExecutionUpdateMonitor) Naming.lookup("rmi://" + shost
					+ "/ExecutionUpdateManager");
			xum.addExecutionUpdateListener(gui);
			System.err.println("Registered as Exec update listener on: " + xum);

			gui.startPinger();
			gui.startTimePanelRefresh();
			
			while (true) {
				try {
					Thread.sleep(60000L);
				} catch (InterruptedException ix) {
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void groupExecutionAbandoned(GroupItem group, long time, ExecutionResourceBundle erb,
			IExecutionFailureContext efc, Set<IQosMetric> qos) throws RemoteException {
		// TODO Auto-generated method stub
		System.err.println("Group exec abandonned: " + efc);
		htm.modifyRow(group, time, efc);
	}

	@Override
	public void groupExecutionCompleted(GroupItem group, long time, ExecutionResourceBundle erb, Set<IQosMetric> qos)
			throws RemoteException {
		// TODO Auto-generated method stub
		System.err.println("Group exec hasCompleted");

		htm.modifyRow(group, time, null);

	}

	@Override
	public void groupExposureCompleted(GroupItem arg0, long arg1, long arg2, String arg3) throws RemoteException {

	}

	private class PrescanListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			Runnable r = new Runnable() {

				@Override
				public void run() {
					try {
						FeasibilityPrescanController fpc = (FeasibilityPrescanController) Naming.lookup("rmi://"
								+ shost + "/FeasibilityPrescanner");
					
						progress = new ProgressMonitor(feasPanel, "Scanning", "", 1, 100);
												
						List candidates = fpc.prescan(System.currentTimeMillis(), 60000);

						progress.close();
						
						long start = 0L;
						long end = 0L;

						double exectot = 0.0;
						Iterator ig = candidates.iterator();
						while (ig.hasNext()) {
							PrescanEntry pse = (PrescanEntry) ig.next();
							start = pse.start;
							end = pse.end;
							exectot += pse.execTime;
						}

						// System.err.printf("Total available exectime: %tF : %4.2f H\n",
						// time, (exectot / 3600000.0));

						// the results are now back,
						// now lets do a contention scan
						int nn = (int) ((end - start) / 60000) + 1;
						double[] cc = new double[nn];
						Iterator ic = candidates.iterator();
						while (ic.hasNext()) {
							PrescanEntry pse = (PrescanEntry) ic.next();
							double xt = pse.execTime;
							double gw = pse.feasibleTotal() / pse.nx;
							double cg = xt / (xt + gw);
							for (int it = 0; it < nn; it++) {
								long t = start + it * 60000L;
								if (pse.isFeasible(t))
									cc[it]++;
								// cc[it] += cg;
							}
						}

						// plot contention
						for (int it = 0; it < nn; it++) {
							long t = start + it * 60000L;
							System.err.printf("CC %tF %tT %4.2f \n", t, t, cc[it]);
							tsContentionPredict.add(new Second(new Date(t)), cc[it]);
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			};
			(new Thread(r)).start();
		}

	}

	private class SweepControlListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			String cmd = ae.getActionCommand();
			if (cmd.equals("way-back")) {
				System.err.println("set sweep no to lowest known (earliest)");
			} else if (cmd.equals("back")) {
				System.err.println("dec sweep no");
			} else if (cmd.equals("fwd")) {
				System.err.println("inc sweep no");
			} else if (cmd.equals("way-fwd")) {
				System.err.println("set sweep no to highest known (latest)");
			}
		}
	}
	
	private class TimePanelRefresh extends Thread {
		@Override
		public void run() {
			Color color = Color.orange;
			while (true) {
				ii++;
				if (ii % 2 == 0)
					color = Color.green;
				else
					color = Color.orange;
				
				timePanel.repaint(color);				
					try {
						Thread.sleep(10000L);
					} catch (InterruptedException ix) {
					}
				}
		}
	}

	private class Pinger extends Thread {

		@Override
		public void run() {
			while (true) {
				try {
					SchedulingStatusProvider sched = (SchedulingStatusProvider) Naming.lookup("rmi://" + shost + "/Scheduler");
					((RemotelyPingable) sched).ping();
					schedOnlineField.setBackground(Color.green);
					schedOnlineField.setForeground(Color.blue);
					schedOnlineField.setText("ONLINE");
				} catch (Exception e) {
					schedOnlineField.setBackground(Color.red);
					schedOnlineField.setForeground(Color.blue);
					schedOnlineField.setText("OFFLINE");
				}
				try {
					ExecutionUpdateManager xm = (ExecutionUpdateManager) Naming.lookup("rmi://" + shost
							+ "/ExecutionUpdateManager");
					((RemotelyPingable) xm).ping();
					xmOnlineField.setBackground(Color.green);
					xmOnlineField.setForeground(Color.blue);
					xmOnlineField.setText("ONLINE");
				} catch (Exception e) {
					e.printStackTrace();
					xmOnlineField.setBackground(Color.orange);
					xmOnlineField.setForeground(Color.blue);
					xmOnlineField.setText("UNKNOWN");
				}

				try {
					Thread.sleep(60000L);
				} catch (InterruptedException ix) {
				}
			}
		}

	}
	  /** Make a GridBag component. */
    private void makeComp(JPanel panel, GridBagLayout grid,
                    GridBagConstraints c, Component comp, int x, int y, int w, int h) {
            c.gridx = x;
            c.gridy = y;
            c.gridwidth = w;
            c.gridheight = h;
            // c.anchor = GridBagConstraints.WEST;
            grid.setConstraints(comp, c);
            panel.add(comp);
    }

	@Override
	public void prescanCompleted() throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void candidateRejected(String arg0, GroupItem arg1, String arg2) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerMessage(int arg0, String arg1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
