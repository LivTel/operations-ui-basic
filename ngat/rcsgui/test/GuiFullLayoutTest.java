/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.rmi.Naming;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.ems.MeteorologyStatusProvider;
import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.opsgui.base.GuiSideBar;
import ngat.opsgui.base.Resources;
import ngat.opsgui.perspectives.tracking.AitoffPlot;
import ngat.opsgui.services.MeteorologyStatusHandlerService;
import ngat.opsgui.services.ServiceManager;
import ngat.opsgui.services.TelescopeStatusHandlerService;
import ngat.opsgui.test.MeteoTestPanel;
import ngat.phase2.XGroup;
import ngat.phase2.XProgram;
import ngat.phase2.XProposal;
import ngat.phase2.XTag;
import ngat.phase2.XUser;
import ngat.rcs.ers.test.BasicReactiveSystem;
import ngat.rcsgui.stable.OperationsPanel;
import ngat.sms.GroupItem;
import ngat.sms.SchedulingStatusProvider;
import ngat.tcm.TelescopeStatusProvider;
import ngat.util.XmlConfigurator;

/**
 * @author eng
 * 
 */
public class GuiFullLayoutTest {

	static Dimension TOP_SIZE = new Dimension(694, 150);
	static Dimension TABS_SIZE = new Dimension(654, 200);
	static Dimension PBAR_SIZE = new Dimension(40, 486);

	static Font BIG_FONT = new Font("serif", Font.BOLD + Font.ITALIC, 26);
	private String base;
	private String remoteHost;

	int ig = 0;
	String[] gnames = new String[] {
			"SBN-686+34Red", 
			"G2343-ULAS-78UGR", 
			"BLLAC-2345-54566", 
			"BigStarObs_4545",
			"SomeOldTargetInRed" 
			};
	
	OperationsPanel opsPanel;

	public GuiFullLayoutTest(String base, String remoteHost) {
		this.base = base;
		this.remoteHost = remoteHost;
	}

	public void display() throws Exception {

		InstrumentRegistry ireg = (InstrumentRegistry) Naming.lookup("rmi://" + remoteHost + "/InstrumentRegistry");
		List ilist = ireg.listInstruments();

		List<InstrumentDescriptor> instruments = new Vector<InstrumentDescriptor>();
		Iterator ii = ilist.iterator();
		while (ii.hasNext()) {
			instruments.add((InstrumentDescriptor) ii.next());
		}
		GuiSideBar sidebar = new GuiSideBar(instruments);

		TbdPanel top = new TbdPanel();
		top.setPreferredSize(TOP_SIZE);

		JTabbedPane tabs = new JTabbedPane(SwingConstants.RIGHT);
		// tabs.setBorder(BorderFactory.createLoweredBevelBorder());
		tabs.setPreferredSize(TABS_SIZE);
		tabs.setFont(BIG_FONT);
		tabs.setForeground(Color.blue);

		// JPanel pbar = new JPanel(true);
		// pbar.setPreferredSize(PBAR_SIZE);

		JPanel axes = new JPanel(true);
		axes.setLayout(new BorderLayout());

		TbdPanel ax1 = new TbdPanel();
		ax1.setPreferredSize(new Dimension(679, 424));
		ax1.setBorder(BorderFactory.createLoweredBevelBorder());

		JPanel ax2 = new JPanel(true);
		ax2.setPreferredSize(new Dimension(679, 212));
		ax2.setBorder(BorderFactory.createLoweredBevelBorder());
		ax2.setLayout(new FlowLayout(FlowLayout.LEFT));
		TbdPanel ap1 = new TbdPanel();
		ap1.setBorder(BorderFactory.createLoweredBevelBorder());
		ap1.setPreferredSize(new Dimension(205, 212));
		TbdPanel ap2 = new TbdPanel();
		ap2.setBorder(BorderFactory.createLoweredBevelBorder());
		ap2.setPreferredSize(new Dimension(205, 212));
		TbdPanel ap3 = new TbdPanel();
		ap3.setBorder(BorderFactory.createLoweredBevelBorder());
		ap3.setPreferredSize(new Dimension(205, 212));
		ax2.add(ap1);
		ax2.add(ap2);
		ax2.add(ap3);

		axes.add(ax1, BorderLayout.CENTER);
		axes.add(ax2, BorderLayout.SOUTH);

		tabs.addTab("A", new ImageIcon(base + "/DeviantArt-icon.png"), axes);

		MeteoTestPanel mtp = new MeteoTestPanel();

		tabs.addTab("M", new ImageIcon(base + "/Weather-icon.png"), mtp);

		// filters
		FilterDisplayPanelTest test1 = new FilterDisplayPanelTest(base);
		FilterDisplayPanelTest2 test2 = new FilterDisplayPanelTest2(base);
		FilterDisplayPanelTest3 test3 = new FilterDisplayPanelTest3(base);
		JTabbedPane ftabs = new JTabbedPane();
		ftabs.addTab("Weather", test1);
		ftabs.addTab("Axes", test2);
		ftabs.addTab("System", test3);
		JPanel f2 = new JPanel(true);
		f2.setLayout(new BorderLayout());
		f2.add(ftabs, BorderLayout.CENTER);
		tabs.addTab("F", new ImageIcon(base + "/3ds-Max-icon.png"), f2);

		AitoffPlot aitoff = new AitoffPlot();
		ISite site = new BasicSite("", Math.toRadians(58.0), Math.toRadians(170));
		aitoff.showTracks(site);

		TbdPanel ax3 = new TbdPanel();
		ax3.setPreferredSize(new Dimension(679, 212));
		ax3.setBorder(BorderFactory.createLoweredBevelBorder());

		JPanel trkPanel = new JPanel(true);
		trkPanel.setLayout(new BorderLayout());
		trkPanel.add(aitoff, BorderLayout.CENTER);
		trkPanel.add(ax3, BorderLayout.SOUTH);

		tabs.addTab("T", new ImageIcon(base + "/3ds-Max-icon.png"), trkPanel);

		// OPERATIONS
		JPanel opsMasterPanel = new JPanel(true);
		opsMasterPanel.setLayout(new BorderLayout());

		opsPanel = new OperationsPanel();
		JPanel bx2 = new JPanel(true);
		bx2.setPreferredSize(new Dimension(679, 212));
		bx2.setLayout(new FlowLayout(FlowLayout.LEFT));
		TbdPanel bp1 = new TbdPanel();
		bp1.setPreferredSize(new Dimension(204, 212));
		TbdPanel bp2 = new TbdPanel();
		bp2.setPreferredSize(new Dimension(204, 212));
		TbdPanel bp3 = new TbdPanel();
		bp3.setPreferredSize(new Dimension(204, 212));
		bx2.add(bp1);
		bx2.add(bp2);
		bx2.add(bp3);

		opsMasterPanel.add(opsPanel, BorderLayout.CENTER);
		opsMasterPanel.add(bx2, BorderLayout.SOUTH);

		tabs.addTab("O", new ImageIcon(base + "/WifiTrak-icon.png"), opsMasterPanel);

		// SCHEDULER
		JPanel schedPanel = new JPanel(true);
		schedPanel.setLayout(new BorderLayout());

		JTabbedPane schedTabPanel = new JTabbedPane(SwingConstants.NORTH);
		schedTabPanel.addTab("Sweep", new JPanel(true));

		schedTabPanel.addTab("Contention", new JPanel(true));
		schedTabPanel.addTab("Scoring", new JPanel(true));

		ScheduleSweepMasterPanel smp = new ScheduleSweepMasterPanel();
		schedTabPanel.addTab("History", smp);

		schedPanel.add(schedTabPanel, BorderLayout.CENTER);
		tabs.addTab("S", new ImageIcon(base + "/iStat-icon.png"), schedPanel);

		tabs.addTab(null, new ImageIcon(base + "/Drives-Network-icon.png"), new JTabbedPane(SwingConstants.NORTH));
		tabs.addTab(null, new ImageIcon(base + "/Generic-Share-Folder-icon.png"), new JTabbedPane(SwingConstants.NORTH));
		tabs.addTab(null, new ImageIcon(base + "/browser-icon.png"), new JTabbedPane(SwingConstants.NORTH));
		tabs.addTab(null, new ImageIcon(base + "/graph-icon.png"), new JTabbedPane(SwingConstants.NORTH));
		tabs.addTab(null, new ImageIcon(base + "/Group-Folder-icon.png"), new JTabbedPane(SwingConstants.NORTH));
		tabs.addTab(null, new ImageIcon(base + "/Newsvine-icon.png"), new JTabbedPane(SwingConstants.NORTH));
		tabs.addTab(null, new ImageIcon(base + "/Wikio-icon.png"), new JTabbedPane(SwingConstants.NORTH));

		JPanel all = new JPanel(true);
		all.setLayout(new BorderLayout());

		JPanel right = new JPanel(true);
		right.setLayout(new BorderLayout());

		right.add(tabs, BorderLayout.CENTER);
		right.add(top, BorderLayout.NORTH);

		all.add(sidebar, BorderLayout.CENTER);
		all.add(right, BorderLayout.EAST);

		JFrame f = new JFrame("GUI Full Layout test");
		f.setJMenuBar(createMenuBar());
		f.getContentPane().add(all);
		f.pack();
		f.setVisible(true);

		System.err.println("Full area: " + all.getSize().width + "x" + all.getSize().height);
		System.err.println("Tabs area: " + axes.getSize().width + "x" + axes.getSize().height);

		// link up the SMP to scheduler
		try {
			SchedulingStatusProvider sched = (SchedulingStatusProvider) Naming.lookup("rmi://localhost/Scheduler");
			System.err.println("TS::Located local scheduler: " + sched);
			TestSweepScheduleListener tsl = new TestSweepScheduleListener(smp);
			sched.addSchedulingUpdateListener(tsl);
			System.err.println("TS::Hooked schedule sweep listener to Scheduler");
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {

			ServiceManager srt = new ServiceManager();
			MeteorologyStatusHandlerService meteo = new MeteorologyStatusHandlerService();
			meteo.setMspUrl("rmi://" + remoteHost + "/Meteorology");
			meteo.setMsaUrl("rmi://" + remoteHost + "/MeteorologyGateway");
			meteo.setLookBackTime(7200 * 1000L);
			//srt.addService(meteo);

			meteo.addListener(mtp);
			//srt.startService(meteo, 20000L, 2000L);

			TelescopeStatusHandlerService tss = new TelescopeStatusHandlerService();
			tss.setTcmUrl("rmi://" + remoteHost + "/Telescope");
			tss.setTsaUrl("rmi://" + remoteHost + "/TelescopeGateway");
			tss.setLookBackTime(7200000L);
			//srt.addService(tss);

			MechSkyPlotHandler h = new MechSkyPlotHandler(aitoff, site);
			tss.addListener(h);
			//srt.startService(tss, 20000L, 2000L);

			BasicReactiveSystem ts = new BasicReactiveSystem(new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0)));
			XmlConfigurator.use(new File("/home/eng/rules_1_2_3.xml")).configure(ts);

			FilterDisplayPanelTest.MyListener ml1 = test1.createListener(); // weather
			FilterDisplayPanelTest2.MyListener ml2 = test2.createListener(); // axes
			FilterDisplayPanelTest3.MyListener ml3 = test3.createListener(); // axes
			System.err.println("Created RSSU");

			ts.addReactiveSystemUpdateListener(ml1);
			ts.addReactiveSystemUpdateListener(ml2);
			ts.addReactiveSystemUpdateListener(ml3);
			System.err.println("Created Added fdps as rssls");

			MeteorologyStatusProvider meteop = (MeteorologyStatusProvider) Naming.lookup("rmi://" + remoteHost
					+ "/Meteorology");
			meteop.addMeteorologyStatusUpdateListener(ts);
			System.err.println("Linked TS to meteorology");

			TelescopeStatusProvider telp = (TelescopeStatusProvider) Naming
					.lookup("rmi://" + remoteHost + "/Telescope");
			telp.addTelescopeStatusUpdateListener(ts);
			System.err.println("Linked TS to telescope");

			System.err.println("TS::Starting cache reader...");
			ts.startCacheReader();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void runOpsUpdates() {
		for (int ig = 0; ig < 5; ig++) {
			XGroup g = new XGroup();
			g.setName(gnames[ig]);
			g.setID((int) (Math.random() * 34000));
			XProposal p = new XProposal("JL12B001");
			XProgram q = new XProgram("XJL12B000");
			XTag tag = new XTag();
			tag.setName("LJMU");
			XUser u = new XUser("jonny.smithersson");
			GroupItem gi = new GroupItem(g, null);
			gi.setProgram(q);
			gi.setProposal(p);
			gi.setTag(tag);
			gi.setUser(u);

			opsPanel.update(gi);
			try {
				Thread.sleep(5000);
			} catch (InterruptedException ix) {
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Resources.setDefaults("/home/eng/rcsgui");
			TcsStatusPacket.mapCodes();
			// try {
			// UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			// UIManager.setLookAndFeel("javax.swing.plaf.synth.SynthLookAndFeel");
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

			GuiFullLayoutTest test = new GuiFullLayoutTest(args[0], args[1]);

			test.display();

			test.runOpsUpdates();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.add(new JMenu("File"));
		bar.add(new JMenu("Actions"));
		bar.add(new JMenu("Tools"));
		bar.add(new JMenu("View"));
		bar.add(new JMenu("Control"));

		// bar.setHelpMenu(new JMenu("Help"));

		return bar;
	}

	private JTabbedPane maketab(String filename) {

		JTabbedPane tab = new JTabbedPane(SwingConstants.NORTH);
		return tab;
	}

	/**
	 * @author eng
	 * 
	 */
	public class TbdPanel extends JPanel {

		/**
		 * 
		 */
		public TbdPanel() {
			super(true);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see javax.swing.JComponent#paint(java.awt.Graphics)
		 */
		@Override
		public void paint(Graphics g) {
			super.paint(g);
			Dimension d = getSize();
			g.setColor(Color.black);
			g.drawLine(0, 0, d.width, d.height);
			g.drawLine(0, d.height, d.width, 0);

			g.drawString(String.format("%4d x%4d", d.width, d.height), d.width / 2 - 30, d.height / 2 - 30);

		}

	}
}
