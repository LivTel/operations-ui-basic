/**
 * 
 */
package ngat.opsgui.base;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.rmi.Naming;
import java.util.SimpleTimeZone;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.TimeZone;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;

import org.jdom.Element;
import org.jfree.chart.ChartPanel;

import ngat.astrometry.ISite;
import ngat.ems.test.TestSkyModelMonitor;
import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.opsgui.components.AuxSystemsSummaryPanel;
import ngat.opsgui.components.RcsSummaryPanel;
import ngat.opsgui.components.StateVariableSummaryPanel;
import ngat.opsgui.login.Display;
import ngat.opsgui.login.Layout;
import ngat.opsgui.login.PerspectiveDescriptor;
import ngat.opsgui.login.ServiceDescriptor;
import ngat.opsgui.login.UiConfig;
import ngat.opsgui.login.User;
import ngat.opsgui.login.UserDescriptor;
import ngat.opsgui.perspectives.astrometry.AstrometryPerspective;
import ngat.opsgui.perspectives.instruments.InstrumentsPerspective;
import ngat.opsgui.perspectives.meteorology.MeteorologyPerspective;
import ngat.opsgui.perspectives.operations.OperationsPerspective;
import ngat.opsgui.perspectives.phase2.Phase2Perspective;
import ngat.opsgui.perspectives.reactive.ReactiveSystemPerspective;
import ngat.opsgui.perspectives.scheduling.SchedulingPerspective;
import ngat.opsgui.perspectives.services.ServicesPerspective;
import ngat.opsgui.perspectives.tracking.TrackingPerspective;
import ngat.opsgui.services.InstrumentStatusHandlerService;
import ngat.opsgui.services.LegacyIdHandler;
import ngat.opsgui.services.LegacyOcrHandler;
import ngat.opsgui.services.LegacyServices;
import ngat.opsgui.services.LegacyStateModelHandler;
import ngat.opsgui.services.MeteorologyStatusHandlerService;
import ngat.opsgui.services.OperationsEventHandlerService;
import ngat.opsgui.services.ReactiveSystemUpdateHandlerService;
import ngat.opsgui.services.SchedulingHandlerService;
import ngat.opsgui.services.ServiceManager;
import ngat.opsgui.services.Phase2CacheService;
import ngat.opsgui.services.ServiceProvider;
import ngat.opsgui.services.SkyModelHandlerService;
import ngat.opsgui.services.TelescopeStatusHandlerService;
import ngat.opsgui.xcomp.GroupDisplayPanel;
import ngat.opsgui.xcomp.LogEntryPanel;
import ngat.opsgui.xcomp.TempLoginDialog;
import ngat.rcs.ers.ReactiveSystemMonitor;
import ngat.rcs.ers.ReactiveSystemStructureProvider;
import ngat.rcs.telemetry.GroupOperationsMonitor;
import ngat.rcsgui.stable.ColorStatePanel2;
import ngat.rcsgui.stable.ObservationPanel;
import ngat.rcsgui.stable.ObservationUpdateListener;
import ngat.rcsgui.stable.OperationsPanel;
import ngat.rcsgui.stable.RcsStatePanel;
import ngat.rcsgui.stable.ToolMenu;
import ngat.tcm.Telescope;
import ngat.util.XmlConfigurable;
import ngat.util.XmlConfigurator;
import ngat.util.logging.BasicLogFormatter;
import ngat.util.logging.ConsoleLogHandler;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * The main GUI class.
 * 
 * @author eng
 * 
 */
public class Gui implements XmlConfigurable {

	// Identity versioning etc
	public static final String VERSION = "@version@";

	public static final String BUILD = "@build@";

	public static final String BUILD_DATE = "@build.date@";

	public static final String TITLE_BASE = "LT Operations UI";

	/** How long we wait for each startup connections. */
	private static final long STARTUP_TIMEOUT = 15000L;

	/** How many startup attempts. */
	private static final int MAX_STARTUP_ATTEMPTS = 4;

	public static String FILE_EXIT_COMMAND = "file-exit";
	
	public static String LOG_SEND_COMMAND = "log-send";
	
	/** Logging. */
	private LogGenerator logger;

	/** Mapping of name -> perspective. */
	private Map<String, Perspective> perspectives;

	/** Mapping of name -> perspective-descriptor. */
	private Map<String, PerspectiveDescriptor> perspectiveNamesMap;

	/** List of available perspectives. */
	private List<PerspectiveDescriptor> availablePerspectives;

	/** The currently selected perspective. */
	// private Perspective currentPerspective;

	/** The currently logged-in user. */
	private User currentUser;

	/** Only ONE display can have a sidebar. */
	private GuiSideBar sideBar;

	/** Only ONE display can have a top panel. */
	private TopPanel topPanel;

	/** Services manager. */
	private ServiceManager svcMgr;

	/** Telescope services endpoint. */
	private Telescope telescope;

	/** Instrument services endpoint. */
	private InstrumentRegistry ireg;

	/** ERS Structure and status provider. */
	private ReactiveSystemStructureProvider ersStructureprovider;

	/** List of available services. */
	private List<ServiceDescriptor> availableServices;

	// / LEGACY STUFF
	private ColorStatePanel2 csp;
	private OperationsPanel opspanel;
	// private ObservationPanel obspanel;
	private RcsStatePanel rsp;
	private GroupDisplayPanel gdp;
	private ChartPanel skypanel;
	// private TestSkyModelMonitor tskymonitor;
	private ToolMenu toolMenu;

	/** Splash screen. */
	// SplashScreen splash;

	/** Menu bar. */
	JMenuBar menuBar;

	/** Controls file operations. */
	JMenu fileMenu;

	/** Controls which perspectives are attached. */
	JMenu viewMenu;
	
	/** Interacts with local and remote logging systems.*/
	JMenu logMenu;

	// Status area/line

	// Top area

	// Configuration - these go into Config....

	UiConfig uiConfig;

	private String rcsHost;
	private String smpHost;
	private String baseHost;
	private String schedHost;
	private String externalHost;

	private ISite site;

	/**
     * 
     */
	public Gui() {

		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate().system("GUI").subSystem("Launcher")
				.srcCompClass(this.getClass().getSimpleName()).srcCompId("Gui");

		perspectives = new HashMap<String, Perspective>();

		// Prepare lists of available perspectives and services.*/
		availablePerspectives = new Vector<PerspectiveDescriptor>();
		availableServices = new Vector<ServiceDescriptor>();

		perspectiveNamesMap = new HashMap<String, PerspectiveDescriptor>();
		svcMgr = new ServiceManager();

	}

	public void setUser(User user) {
		this.currentUser = user;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	/** Create a splash screen for startup. */
	public void displaySplashScreen() {

		// splash = new SplashScreen();
		// splash.splashInit();

	}

	/** Configure from a file. */
	public void configure(File file) throws Exception {

		XmlConfigurator.use(file).configure(this);

	}

	/**
	 * Configure the GUI and its services.
	 * 
	 * This call configures the basic non-user and non-layout items.
	 * 
	 * Lookup base services always needed - telescope, instruments
	 * 
	 * Lookup available services - we may not use them all.
	 * 
	 * Lookup available perspectives - we may not use them all.
	 * 
	 * If this config fails we should fall-over immediately
	 * 
	 * */
	@Override
	public void configure(Element node) throws Exception {
		// splash.splashText("Configuring...");


		Element tnode = node.getChild("site");
		// TODO TEMP DEBUG do configuring stuff...

		rcsHost = tnode.getChildTextTrim("rcs.host");
		schedHost = tnode.getChildTextTrim("sched.host");
		smpHost = tnode.getChildTextTrim("smp.host");
		baseHost = tnode.getChildTextTrim("base.host");
		externalHost = tnode.getChildTextTrim("external.host");

		System.err.println("Remote providers: " + " RCS: " + rcsHost
				+ ", SMP: " + smpHost + ", BASE: " + baseHost + ", SCHED: "
				+ schedHost + ", EXT/IF: " + externalHost);


		uiConfig = new UiConfig();
		Element cnode = node.getChild("config");
		uiConfig.configure(cnode);
		
	
		// PROPER CONFIGURATION
		// Configure which Perspectives are available
		List plist = node.getChildren("perspective");
		for (int ip = 0; ip < plist.size(); ip++) {
			Element pnode = (Element) plist.get(ip);
			PerspectiveDescriptor pd = new PerspectiveDescriptor(
					pnode.getAttributeValue("name"));
			pd.configure(pnode);
			availablePerspectives.add(pd);
			perspectiveNamesMap.put(pd.getName(), pd);
		}

		// Configure which services are available
		logger.create().info().level(1).extractCallInfo()
			.msg("Creating services directly from config file: gui.xml ...").send();
		List slist = node.getChildren("service");
		for (int is = 0; is < slist.size(); is++) {
			Element snode = (Element) slist.get(is);
			ServiceDescriptor sd = new ServiceDescriptor(
					snode.getAttributeValue("name"));
			sd.configure(snode);
			availableServices.add(sd);
			logger.create()
			.info()
			.level(1)
			.extractCallInfo()
			.msg("Created SD for: " + sd.getName() +
					" Gwy " + sd.getGatewayHost()+ "/" + sd.getGatewayName()+
					" Phost: "+sd.getProviderHost()+"/"+sd.getProviderName()).send();
		}

		// TEMP configure these manually
		// splash.splashProgress(20);
		logger.create().info().level(1).extractCallInfo()
			.msg("Creating services Hard-coded...").send();
		availableServices.add(makeservice("PHASE2", "SynopticModelProvider",
				smpHost));
		availableServices.add(makeservice("TCM", "Telescope", rcsHost));
		availableServices.add(makeservice("SCHED", "ScheduleDespatcher",
				schedHost));
		availableServices.add(makeservice("METEO", "Meteorology", rcsHost));
		availableServices.add(makeservice("INSTR", "InstrumentRegistry",
				rcsHost));
		availableServices.add(makeservice("SKY", "SkyModel", rcsHost));
		availableServices.add(makeservice("OPS", "OperationsGateway", rcsHost));
		//availableServices.add(makeservice("ERS", "ReactiveSystemGateway", rcsHost));
		
		svcMgr.configureServices(availableServices);

	}

	/** Makes any initial contacts prior to launching the UI frame. */
	private void initialConnection() throws Exception {
		
		// find telescope and ireg...
		String telescopeUrl = null;
		try {
			telescopeUrl = "rmi://" + rcsHost + "/Telescope";
			telescope = (Telescope) Naming.lookup(telescopeUrl);
			// TODO access the telescope to see if its alive...
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to contact TCM: " + telescopeUrl);
		}

		String iregUrl = null;
		try {
			iregUrl = "rmi://" + rcsHost + "/InstrumentRegistry";
			ireg = (InstrumentRegistry) Naming.lookup(iregUrl);
			// TODO access the ireg to see if its alive...
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to contact Ireg: " + iregUrl);
		}

		// Obtain site information from TCM.
		try {
			site = telescope.getSiteInfo();
			logger.create().info().level(1).extractCallInfo()
					.msg("Configured site: " + site).send();
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Unable to contact telescope status provider");
		}

		// Obtain ers structure info
		String ersStructUrl = null;
		try {
			ersStructUrl = "rmi://" + rcsHost + "/ReactiveSystem";
			ersStructureprovider = (ReactiveSystemStructureProvider) Naming
					.lookup(ersStructUrl);
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception(
					"Unable to contact reactive systems structure provider: "
							+ ersStructUrl);
		}

	}

	// TEMP not for future use
	private ServiceDescriptor makeservice(String id, String name, String host) {
		ServiceDescriptor sd = new ServiceDescriptor(id);
		sd.setGatewayHost(host);
		sd.setGatewayName(name);
		sd.setLookbackTime(3600 * 1000L);
		sd.setPollingInterval(180000L);
		sd.setBroadcastInterval(10000L);
		logger.create()
				.info()
				.level(1)
				.extractCallInfo()
				.msg("Created SD for: " + sd.getName() +
					" Gwy " + sd.getGatewayHost()+ "/" + sd.getGatewayName()+
					" Phost: "+sd.getProviderHost()+"/"+sd.getProviderName()).send();
				
		return sd;
	}

	/** Launch the GUi with the supplied user layout. */
	public void display(Layout layout) {

		logger.create().info().level(1).extractCallInfo()
				.msg("Using layout: " + layout.getName()).send();

		boolean gotSidebar = false;

		List<Display> displays = layout.getDisplays();
		for (int id = 0; id < displays.size(); id++) {
			Display d = displays.get(id);
			// the first frame gets the sidebar and topbar
			if (d.countPerspectives() > 1 && !gotSidebar) {
				createDisplay(d, true);
				gotSidebar = true;
			} else
				createDisplay(d, false);
		}

	}

	/** Create a display. */
	private void createDisplay(Display display, boolean hasSidebar) {

		logger.create()
				.info()
				.level(1)
				.extractCallInfo()
				.msg("Configuring display: " + display.getName() + " with "
						+ display.countPerspectives() + " perspectives "
						+ (hasSidebar ? " WITH Sidebar" : " and NO sidebar"))
				.send();

		DisplayFrame dframe = new DisplayFrame(this, display, hasSidebar);
		// dframe.setIconImage(new
		// ImageIcon("/home/eng/opsgui/treskelion_small.png").getImage());
		dframe.setVisible(true);
	}

	/** Start up broadcasting of services. */
	public void startServiceBroadcasting() {
		System.err.println("Starting service broadcasting...");
		svcMgr.startServices();
	}

	public void startLegacyServices() {

		// grab the mode information status cats: STATE_MODEL and ID

		StateVariableSummaryPanel svp = sideBar.getStateVariableSummaryPanel();
		RcsSummaryPanel rsup = sideBar.getRcsSummaryPanel();

		LegacyStateModelHandler lsm = new LegacyStateModelHandler(csp, svp,
				rsp, rsup, toolMenu);
		LegacyIdHandler lim = new LegacyIdHandler(csp, rsp, rsup);
		LegacyOcrHandler ocm = new LegacyOcrHandler(topPanel);

		AuxSystemsSummaryPanel auxp = topPanel.getAuxSystemsSummaryPanel();

		String smpUrl = "rmi://" + smpHost + "/SynopticModelProvider";
		String schedUrl = "rmi://" + schedHost + "/ScheduleDespatcher";
		String teaUrl = "rmi://" + externalHost + "/TelescopeEmbeddedAgent";
		String phase2Url = "rmi://" + baseHost + "/Phase2Model";

		LegacyServices ls = new LegacyServices(rcsHost, 9110, lsm, lim, ocm,
				smpUrl, schedUrl, teaUrl, phase2Url, auxp);
		ls.startServices();

		// sky model
		/*
		 * System.err.println(
		 * "Startup::Starting skymodel prediction monitoring using host: " +
		 * rcsHost); final TestSkyModelMonitor ftsm = tskymonitor; Runnable r =
		 * new Runnable() {
		 * 
		 * @Override public void run() { ftsm.run(rcsHost); } }; (new
		 * Thread(r)).start();
		 */

		// observations
		ObservationUpdateListener oul = null;
		try {
			// listener updates each of the supplied components if available
			oul = new ObservationUpdateListener(null, null, opspanel, null,
					topPanel, gdp);
		} catch (Exception e) {
			e.printStackTrace();
		}

		final ObservationUpdateListener fobsListener = oul;

		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {

						GroupOperationsMonitor gom = (GroupOperationsMonitor) Naming
								.lookup("rmi://" + rcsHost
										+ "/GroupOperationsMonitor");
						System.err.println("GOM: rebind " + fobsListener);
						gom.addGroupOperationsListener(fobsListener);

					} catch (Exception cx) {
						System.err.println("Failed to rebind to GOM");
						cx.printStackTrace();
					}
					try {
						Thread.sleep(120000L);
					} catch (InterruptedException ix) {
					}
				} // next rebind
			}
		};
		(new Thread(r1)).start();

	}

	/**
	 * Create a view menu.
	 * 
	 * @return An instance of menu.
	 */
	public JMenu createViewMenu() {
		ViewMenu menu = new ViewMenu(this);
		return menu;
	}

	/**
	 * Create a (legacy) control menu.
	 * 
	 * @return An instance of menu.
	 */
	public JMenu createControlMenu(JFrame frame) {

		toolMenu = new ToolMenu(frame, rcsHost);
		JMenu menu = toolMenu.getMenu();
		return menu;
	}

	/**
	 * Create a File menu.
	 * 
	 * @return An instance of menu.
	 */
	public JMenu createFileMenu() {

		JMenu fileMenu = new JMenu("File");

		ActionListener fileMenuListener = new FileMenuListener();

		JMenuItem fileExitItem = new JMenuItem("exit");
		fileExitItem.addActionListener(fileMenuListener);
		fileExitItem.setActionCommand(FILE_EXIT_COMMAND);
		fileMenu.add(fileExitItem);
		return fileMenu;

	}
	
	public JMenu createLogMenu() {
		
		JMenu logMenu = new JMenu("Log");
		
		ActionListener logMenuListener = new LogMenuListener();
		
		JMenuItem logSendItem = new JMenuItem("send");
		logSendItem.addActionListener(logMenuListener);
		logSendItem.setActionCommand(LOG_SEND_COMMAND);
		logMenu.add(logSendItem);
		return logMenu;
		
	}

	/*
	 * private JFrame detachPerspective(Perspective perspective) {
	 * 
	 * JFrame f = new JFrame("Standalone: " +
	 * perspective.getClass().getSimpleName());
	 * f.setPreferredSize(PERSPECTIVE_AREA_SIZE);
	 * f.getContentPane().setLayout(new BorderLayout());
	 * f.getContentPane().add(perspective);
	 * f.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
	 * 
	 * final JFrame ff = f; final Perspective fperspective = perspective;
	 * f.addWindowListener(new WindowAdapter() {
	 * 
	 * public void windowIconified(WindowEvent we) { // lets remove the
	 * perspective from the frame, attach to main // gui and dispose the frame.
	 * ff.removeAll(); attachPerspective(fperspective);else if
	 * (perspectiveName.equals("PHASE2")) return createPhase2Perspective(frame,
	 * site); ff.dispose(); }
	 * 
	 * 
	 * (non-Javadoc)logger.create().info().level(1).extractCallInfo().msg(
	 * 
	 * @see
	 * java.awt.event.WindowAdapter#windowClosing(java.awt.event.WindowEvent )
	 * 
	 * @Override public void windowClosing(WindowEvent e) { ff.removeAll();
	 * attachPerspective(fperspective); ff.dispose(); }
	 * 
	 * });
	 * 
	 * JMenuBar bar = new JMenuBar();
	 * logger.create().info().level(1).extractCallInfo().msg( List<JMenu> menus
	 * = perspective.listMenus(); for (int i = 0; i < menus.size(); i++) { JMenu
	 * menu = menus.get(i); bar.add(menu); }
	 * 
	 * f.setJMenuBar(bar); f.pack(); f.setVisible(true); return f; }
	 */
	public Perspective createPerspective(String perspectiveName, JFrame frame)
			throws Exception {

		PerspectiveDescriptor pd = perspectiveNamesMap.get(perspectiveName);

		if (perspectiveName.equals("INSTR"))
			return createInstrumentsPerspective(frame);
		else if (perspectiveName.equals("SCHED"))
			return createSchedulingPerspective(frame);
		else if (perspectiveName.equals("METEO"))
			return createMeteorologyPerspective(frame);
		else if (perspectiveName.equals("PHASE2"))
			return createPhase2Perspective(frame, site);
		else if (perspectiveName.equals("ERS"))
			return null;//createReactivePerspective(frame);
		else if (perspectiveName.equals("TRACK"))
			return createTrackingPerspective(frame);
		else if (perspectiveName.equalsIgnoreCase("OPS"))
			return createOperationsPerspective(frame);
		return null;
	}

	public InstrumentsPerspective createInstrumentsPerspective(JFrame frame)
			throws Exception {
		InstrumentsPerspective instrumentsPerspective = new InstrumentsPerspective(
				frame, ireg);

		try {
			InstrumentStatusHandlerService ish = (InstrumentStatusHandlerService) svcMgr
					.createService("INSTR");
			ish.addListener(instrumentsPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		try {
			TelescopeStatusHandlerService tsh = (TelescopeStatusHandlerService) svcMgr
					.createService("TCM");
			tsh.addListener(instrumentsPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}
		instrumentsPerspective.setSidebarIcon(Resources
				.getIcon("instruments.perspective.icon"));
		return instrumentsPerspective;
	}

	public TrackingPerspective createTrackingPerspective(JFrame frame) {
		TrackingPerspective trackingPerspective = new TrackingPerspective(
				frame, site);
		// System.err.println("Temp:: Linking TrackingPerspective to: " + tss);
		trackingPerspective.setSidebarIcon(Resources
				.getIcon("tracking.perspective.icon"));
		try {
			TelescopeStatusHandlerService tss = (TelescopeStatusHandlerService) svcMgr
					.createService("TCM");
			tss.addListener(trackingPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		try {
			SchedulingHandlerService shs = (SchedulingHandlerService) svcMgr
					.createService("SCHED");
			shs.addListener(trackingPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		return trackingPerspective;
	}

	public MeteorologyPerspective createMeteorologyPerspective(JFrame frame) {
		// TODO TEMP create these panels here for now
		/*
		 * try { tskymonitor = new TestSkyModelMonitor(site.getSiteName(),
		 * 10000L); //skypanel = tskymonitor.createChartPanel(); } catch
		 * (Exception e) { e.printStackTrace(); }
		 */
		MeteorologyPerspective meteorologyPerspective = new MeteorologyPerspective(
				frame);
		// System.err.println("Temp:: Linking TrackingPerspective to: " + tss);
		meteorologyPerspective.setSidebarIcon(Resources
				.getIcon("meteorology.perspective.icon"));
		try {
			MeteorologyStatusHandlerService meteo = (MeteorologyStatusHandlerService) svcMgr
					.createService("METEO");
			meteo.addListener(meteorologyPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}
		try {
			TelescopeStatusHandlerService tsh = (TelescopeStatusHandlerService) svcMgr
					.createService("TCM");
			tsh.addListener(meteorologyPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		try {
			SkyModelHandlerService sms = (SkyModelHandlerService) svcMgr
					.createService("SKY");
			sms.addListener(meteorologyPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		return meteorologyPerspective;
	}

	public SchedulingPerspective createSchedulingPerspective(JFrame frame) {
		SchedulingPerspective schedulingPerspective = new SchedulingPerspective(
				frame);
		schedulingPerspective.setSidebarIcon(Resources
				.getIcon("scheduling.perspective.icon"));
		try {
			SchedulingHandlerService shs = (SchedulingHandlerService) svcMgr
					.createService("SCHED");
			shs.addListener(schedulingPerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		return schedulingPerspective;
	}

	public ReactiveSystemPerspective createReactivePerspective(JFrame frame)
			throws Exception {
		ReactiveSystemPerspective reactivePerspective = new ReactiveSystemPerspective(
				frame, ersStructureprovider);
		reactivePerspective.setSidebarIcon(Resources
				.getIcon("reactive.perspective.icon"));

		// link to ERS service

		try {
			ReactiveSystemUpdateHandlerService rhs = (ReactiveSystemUpdateHandlerService) svcMgr
					.createService("ERS");
			rhs.addListener(reactivePerspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		return reactivePerspective;
	}

	public AstrometryPerspective createAstrometryPerspective(JFrame frame,
			ISite site) {
		AstrometryPerspective astrometryPerspective = new AstrometryPerspective(
				frame, site);
		astrometryPerspective.setSidebarIcon(Resources
				.getIcon("astrometry.perspective.icon"));
		return astrometryPerspective;
	}

	public OperationsPerspective createOperationsPerspective(JFrame frame) {
		opspanel = new OperationsPanel();
		// obspanel = new ObservationPanel();
		csp = new ColorStatePanel2();
		rsp = new RcsStatePanel();
		gdp = new GroupDisplayPanel();
		OperationsPerspective opsPerspective = new OperationsPerspective(frame,
				csp, opspanel, rsp, gdp);
		opsPerspective.setSidebarIcon(Resources
				.getIcon("operations.perspective.icon"));

		try {
			OperationsEventHandlerService oes = (OperationsEventHandlerService) svcMgr
					.createService("OPS");
			oes.addListener(opsPerspective); 
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		return opsPerspective;
	}

	public ServicesPerspective createServicesPerspective(JFrame frame,
			List<ServiceProvider> services) {
		ServicesPerspective servicesPerspective = new ServicesPerspective(
				frame, services);
		servicesPerspective.setSidebarIcon(Resources
				.getIcon("services.perspective.icon"));
		return servicesPerspective;
	}

	public Phase2Perspective createPhase2Perspective(JFrame frame, ISite site) {
		Phase2Perspective phase2Perspective = new Phase2Perspective(frame, site);
		phase2Perspective.setSidebarIcon(Resources
				.getIcon("phase2.perspective.icon"));

		try {
			Phase2CacheService phase2CacheService = (Phase2CacheService) svcMgr
					.createService("PHASE2");
			phase2CacheService.addPhase2CacheUpdateListener(phase2Perspective);
		} catch (Exception e) {
			logger.create().error().level(1).extractCallInfo()
					.msg("Error linking to service provider").send();
		}

		return phase2Perspective;
	}

	public void createServicesForSideBar() {
		logger.create().info().level(1).extractCallInfo()
				.msg("Setting up services for sidebar...").send();

		try {
			svcMgr.createService("OPS");
			svcMgr.createService("TCM");
			svcMgr.createService("INSTR");
			svcMgr.createService("SKY");
			svcMgr.createService("SCHED");

		} catch (Exception e) {
			logger.create().error().level(2).extractCallInfo()
					.msg("Some services are not available at this time").send();
		}

		// TODO: OPS, SM,

	}

	// TODO The insturments should be passed in here or at least the IREG
	public GuiSideBar createSideBar() throws Exception {

		// createServicesForSideBar();

		// TODO test for instruments
		String iregUrl = "rmi://" + rcsHost + "/InstrumentRegistry";
		System.err.println("csb:lookup: " + iregUrl);
		InstrumentRegistry ireg = (InstrumentRegistry) Naming.lookup(iregUrl);
		List insts = ireg.listInstruments();

		// weirdness !!!
		List<InstrumentDescriptor> instList = new Vector<InstrumentDescriptor>();
		for (int i = 0; i < insts.size(); i++) {
			instList.add((InstrumentDescriptor) insts.get(i));
		}

		// TODO the location should be configured
		// sideBar = new GuiSideBar(System.getProperty("user.home"), insts);
		// TODO WHY would we need to know the base directory ?
		sideBar = new GuiSideBar(insts);

		// Add services - they should already exist anyway ?

		InstrumentStatusHandlerService ish = (InstrumentStatusHandlerService) svcMgr
				.createService("INSTR");
		ish.addListener(sideBar);

		TelescopeStatusHandlerService tel = (TelescopeStatusHandlerService) svcMgr
				.createService("TCM");
		tel.addListener(sideBar);

		SkyModelHandlerService sky = (SkyModelHandlerService) svcMgr
				.createService("SKY");
		sky.addListener(sideBar);

		// MeteorologyStatusHandlerService meteo =
		// (MeteorologyStatusHandlerService)svcMgr.createService("METEO");
		// meteo.addListener(sideBar);

		return sideBar;

	}

	public TopPanel createTopPanel() {

		topPanel = new TopPanel(site);

		return topPanel;
	}

	/**
	 * @param name
	 *            The name of the perspective.
	 * @return
	 * @see java.util.Map#get(java.lang.Object)
	 */
	public Perspective getPerspective(String name) {
		return perspectives.get(name);
	}

	/**
	 * Add a new perspective.
	 * 
	 * @param name
	 *            The name of the perspective.
	 * @param perspective
	 * @return
	 */
	public Perspective addPerspective(String name, Perspective perspective) {

		perspectives.put(name, perspective);

		return perspective;
	}

	/**
	 * Attach a perspective to the gui sidebar.
	 * 
	 * @param name
	 *            The name of the perspective.
	 * @param perspective
	 *            The perspective to attach.
	 * @return
	 * @see java.util.Map#put(java.lang.Object, java.lang.Object)
	 */
	/*
	 * public Perspective attachPerspective(Perspective perspective) { String
	 * name = perspective.getPerspectiveName().substring(0, 1); private void
	 * doFileExitAction() {
	 * 
	 * // save any settings System.err.println("Saving settings...");
	 * 
	 * // close telemetry subscriptions
	 * System.err.println("Unsubscribing to telemetry feeds...");
	 * 
	 * // exit System.exit(0);
	 * 
	 * } tabs.addTab(name, perspective); int newTabIndex = tabs.getTabCount();
	 * System.err.println("GUI:attach(): After adding tab, tab count: " +
	 * newTabIndex); tabs.setTabComponentAt(newTabIndex - 1, new
	 * Removable(perspective));
	 * 
	 * return perspective; }
	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Logger alogger = LogManager.getLogger("GUI");
		alogger.setLogLevel(3);
		ConsoleLogHandler console = new ConsoleLogHandler(
				new BasicLogFormatter());
		console.setLogLevel(3);
		alogger.addExtendedHandler(console);

		// VERY IMPORTANT - SETUP TIMEZONE INFORMATION FOR ENTIRE APPLICATION
		SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
		TimeZone.setDefault(UTC);

		TcsStatusPacket.mapCodes();

		// Where do we get resources from - this should eventually be
		// incorporated into deployment jar as:- ngat.opsgui.resources

		Resources.setDefaults(System.getProperty("user.home"));

		// MyTestGui login = new MyTestGui();
		// login.configurePerspectives(file);
		// login.configureUsers(file);

		// GuiLoginDialog dlg = new GuiLoginDialog(arg0, arg1);

		Gui gui = new Gui();

		// popup splash screen
		// gui.displaySplashScreen();

		System.err.println("OPSGUI:: Configuring...");
		// splash.display("Configuring...");
		try {

			XmlConfigurator.use(
					new File(System.getProperty("user.home") + "/gui.xml"))
					.configure(gui);
			System.err.println("OPSGUI:: Configuration ok");
		} catch (Exception e) {
			// display on splash and exit on okay
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage(),
					"OpsUI Startup Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		boolean readyToGo = false;
		String errorMessage = "";
		int nTries = 1;
		long start = System.currentTimeMillis();
		long time = start;
		while ((time < start + STARTUP_TIMEOUT)
				&& (nTries < MAX_STARTUP_ATTEMPTS) && (!readyToGo)) {

			// splash.display("Connecting to system...");
			System.err.println("OPSGUI:: Connection attempt: " + nTries);

			try {

				gui.initialConnection();
				readyToGo = true;

			} catch (Exception e) {
				// display on splash and exit on okay
				e.printStackTrace();
				errorMessage = e.getMessage();
				nTries++;
			}
			if (!readyToGo) {
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
				}
			}
		}

		if (!readyToGo) {
			JOptionPane.showMessageDialog(null, errorMessage + " (after "
					+ nTries + " attempts)", "OpsUI Startup Error",
					JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}

		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
		}

		// Popup login dialog, we need the list of users and available
		// perspectives
		// from gui.xml and users.xml, later we need the files
		// someuser_layouts.xml
		// GuiLoginDialog login = new GuiLoginDialog(userList,
		// perspectiveDescriptorList);
		// display login

		// User user = new User(id, title, firstName, lastName);

		// TODO future login using: UserDescriptor and Layout obtained from
		// login dlg
		// login selects user, user defines layouts which contain
		// displays which contain perspectiveDescriptors, which contain
		// svcDescriptors
		// create displays and when ps are created svcmgr creates the required
		// services
		// gui.login(selectedUserDescriptor, selectedLayout);

		// Temp setup
		// (1) Load user config via xml file users.xml and each user's xml file

		// (2) Present login details in combo as: title fname sname

		// (2a) Users selects login

		// (3) Present layout from small internally configured list combo

		// (3a) User selects layout

		// (4) set user login and launch selected layout

		// XmlConfigurator.use(new File(System.getProperty("user.home") +
		// "/user.xml")).configure(gui);

		// temporary login
		/*
		 * String reply = JOptionPane.showInputDialog(null,
		 * "Enter your details: <title> <Initials> <Surname>",
		 * "OPS Login details", JOptionPane.PLAIN_MESSAGE);
		 * 
		 * StringTokenizer st = new StringTokenizer(reply);
		 * 
		 * if (st.countTokens() >= 3) gui.setUser(new User(new
		 * UserDescriptor("xxx", st.nextToken(), st .nextToken(),
		 * st.nextToken()))); else if (st.countTokens() == 2) gui.setUser(new
		 * User(new UserDescriptor("xxx", "", st.nextToken(), st.nextToken())));
		 * else if (st.countTokens() == 1) gui.setUser(new User(new
		 * UserDescriptor("xxx", "", "X", st .nextToken()))); else
		 * gui.setUser(new User(new UserDescriptor("xxx", "", "Unknown",
		 * "User")));
		 */

		// NEW using UIC configured users
		List<User> users = gui.uiConfig.getUsers();

		TempLoginDialog tld = new TempLoginDialog(null, users);

		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

		// Determine the new location of the window
		int w = tld.getSize().width;
		int h = tld.getSize().height;
		int x = (dim.width - w) / 2;
		int y = (dim.height - h) / 2;
		tld.setLocation(x, y);
		tld.setVisible(true);

		User selected = tld.getSelectedUser();

		// JOptionPane.showMessageDialog(null,
		// "You will be logged in as: "+selected);
		gui.setUser(selected);

		// Let them choose from a couple of standard layouts
		// A - 2 displays, B - 1 display, C - 3 displays with Group Inspector
		// Later we will let them login via the separate LoginDialogs

		String layoutMode = null;
		if (args.length > 0)
			layoutMode = args[0];

		Layout l = createLayout(layoutMode);

		try {
			/*JOptionPane
					.showMessageDialog(null, "About to display layout: " + l);*/

			// Display the layout
			gui.display(l);

			// Start the distribution threads so gui can get updates...
			gui.startServiceBroadcasting();

			// TEMP Start legacy services these are not fully compliant
			gui.startLegacyServices();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Stuffed", "Gui Launch",
					JOptionPane.ERROR_MESSAGE);
			System.err.println("GUI failed to start...");
			e.printStackTrace();
			System.exit(2);
		}
	}

	/** TEMP method. */
	private static Layout createLayout(String layoutMode) {
		Layout layout = new Layout("test");

		if (layoutMode.equalsIgnoreCase("a")) {

			Display d1 = new Display("SchedInstOps");
			d1.addPerspective("SCHED");
			d1.addPerspective("INSTR");
			d1.addPerspective("OPS");
			//d1.addPerspective("ERS");
			layout.addDisplay(d1);

			Display d2 = new Display("MeteoTrackPhase2");
			d2.addPerspective("METEO");
			d2.addPerspective("TRACK");
			d2.addPerspective("PHASE2");
			layout.addDisplay(d2);

		} else if (layoutMode.equalsIgnoreCase("b")) {

			Display d1 = new Display("Combined");
			d1.addPerspective("SCHED");
			d1.addPerspective("INSTR");
			d1.addPerspective("OPS");
			d1.addPerspective("METEO");
			d1.addPerspective("TRACK");
			d1.addPerspective("PHASE2");
			//d1.addPerspective("ERS");
			layout.addDisplay(d1);

		} else {
			Display d1 = new Display("SchedInstOps");
			d1.addPerspective("SCHED");
			d1.addPerspective("INSTR");
			d1.addPerspective("OPS");
			//d1.addPerspective("ERS");

			layout.addDisplay(d1);

			Display d2 = new Display("MeteoTrack");
			d2.addPerspective("METEO");
			d2.addPerspective("TRACK");
			layout.addDisplay(d2);

			Display d3 = new Display("Group Inspector");
			d3.addPerspective("PHASE2");
			layout.addDisplay(d3);
		}
		return layout;
	}
	
	private class LogMenuListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent ae) {

			String cmd = ae.getActionCommand();

			if (LOG_SEND_COMMAND.equals(cmd)) {

				doLogSendAction();

			}

		}
	}

	private void doLogSendAction() {
		
		LogEntryPanel logPanel = new LogEntryPanel();
		logPanel.showLogDialog(currentUser.getDescriptor());
		
	}
	
	private class FileMenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			String cmd = ae.getActionCommand();

			if (FILE_EXIT_COMMAND.equals(cmd)) {

				doFileExitAction();

			}

		}

	}

	private void doFileExitAction() {

		// save any settings
		System.err.println("Saving settings...");

		// close telemetry subscriptions
		System.err.println("Unsubscribing to telemetry feeds...");

		// exit
		System.exit(0);

	}
	/**
	 * @author eng
	 * 
	 */
	/*
	 * public class Removable extends JLabel implements MouseListener {
	 * 
	 * Perspective perspective;
	 *//**
	 * @param perspective
	 */
	/*
	 * public Removable(Perspective perspective) {
	 * super(perspective.getSidebarIcon()); this.perspective = perspective;
	 * 
	 * setOpaque(false); addMouseListener(this);
	 * 
	 * }
	 * 
	 * @Override public void mouseClicked(MouseEvent e) { int nclick =
	 * e.getClickCount();
	 * 
	 * if (nclick == 1) { // find the perspectives tab if (basicMenusRequired) {
	 * int index = tabs.getSelectedIndex(); System.err.println(
	 * "GUI::Tab-mouseClick:: You have single clicked on tab containing: " +
	 * perspective.getClass().getSimpleName()); // tabs.setSelectedIndex(0);
	 * tabs.setSelectedComponent(perspective); } else if (nclick == 2) {
	 * System.err
	 * .println("GUI::Tab-mouseClick:: You have double clicked on tab containing: "
	 * + perspective.getClass().getSimpleName()); // /tabs.remove(0); JFrame f =
	 * Gui.this.detachPerspective(perspective); f.setVisible(true); } }
	 * 
	 * @OverrideavailableServices.add(makeservice("PHASE2", "Phase2", public
	 * void mousePressed(MouseEvent e) { // TODO Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void mouseReleased(MouseEvent e) { // TODO
	 * Auto-generated method stub
	 * 
	 * }
	 * 
	 * @Override public void mouseEntered(MouseEvent e) { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 * 
	 * @Override public void mouseExited(MouseEvent e) { // TODO Auto-generated
	 * method stub
	 * 
	 * }
	 * 
	 * }
	 */
}
