package ngat.rcsgui.stable;

import java.text.*;
import java.util.*;
import java.util.List;

import ngat.sms.FeasibilityPrescanMonitor;
import ngat.sms.SchedulingStatusProvider;
import ngat.tcm.AutoguiderMonitor;
import ngat.util.logging.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.metal.MetalLookAndFeel;

import org.jfree.chart.*;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import ngat.astrometry.BasicSite;
import ngat.ems.*;
import ngat.ems.test.*;
import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusProvider;

import java.awt.event.*;
import java.awt.*;
import java.io.*;
import java.net.*;
import java.rmi.Naming;

import ngat.net.*;
import ngat.net.camp.*;
import ngat.util.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;
import ngat.message.RCS_TCS.*;
import ngat.message.ISS_INST.GET_STATUS_DONE;
import ngat.rcs.ers.test.*;
import ngat.rcs.telemetry.*;
import ngat.rcs.tms.TaskModeControllerManagement;
import ngat.rcsgui.test.FrodoGeneralPanel;
import ngat.rcsgui.test.GroupWatchMasterPanel;
import ngat.rcsgui.test.WeatherRulesUpdateHandler;
import ngat.rcsgui.test.WeatherRulesUpdatePanel;



/** This class provides a hook into the RCS Graphical User Interface. */
public class RcsGUI {

	public static final String VERSION = "@version@";

	public static final String BUILD = "@build@";

	public static final String BUILD_DATE = "@build.date@";

	public static final NumberFormat nf = NumberFormat.getInstance();

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd 'at' HH:mm:ss z");

	public static SimpleDateFormat hmsdf = new SimpleDateFormat("HHmmss");

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static final int ITEM_TYPE_INTEGER = 1;

	public static final int ITEM_TYPE_DOUBLE = 2;

	public static final int ITEM_TYPE_STRING = 3;

	public static final int ITEM_TYPE_STATE = 4;

	public static final int NOONE_IN_CONTROL = 0;

	public static final int WATCHDOG_IN_CONTROL = 1;

	public static final int RCS_IN_CONTROL = 2;

	public static final Color SLATE = new Color(102, 153, 153);

	public static final Color LIGHTSLATE = new Color(153, 203, 203);

	public static final Color YELLOWSLATE = new Color(204, 204, 51);

	public static final Color PINKSLATE = new Color(204, 151, 151);

	public static final Color ORANGESLATE = new Color(255, 165, 0);

	public static final Color OLIVE = new Color(153, 204, 153);

	public static final Font HUGE_FONT = new Font("serif", Font.BOLD, 15);

	public static final Font BIG_FONT = new Font("serif", Font.BOLD, 10);

	public static final Font SMALL_FONT = new Font("courier", Font.PLAIN, 8);

	public static final Font SMALL_FONT_2 = new Font("courier", Font.PLAIN, 9);

	public static final Font STATE_FONT = new Font("courier", Font.PLAIN, 9);

	public static final Font TITLE_FONT = new Font("serif", Font.ITALIC, 9);

	public static final Border blackline = BorderFactory.createLineBorder(Color.black);

	public static final Border raisedbevel = BorderFactory.createRaisedBevelBorder();

	public static final Border loweredbevel = BorderFactory.createLoweredBevelBorder();

	public static final Border emptyborder = BorderFactory.createEmptyBorder();

	public static ImageIcon WAIT_ICON;

	public static ImageIcon STOP_ICON;

	public static ImageIcon START_ICON;

	public static ImageIcon DEFAULT_ICON;

	public static final String DEFAULT_HOST = "localhost";

	public static final int DEFAULT_BSS_PORT = 6683;
	public static final int DEFAULT_COMMAND_PORT = 9110;

	public static final String DEFAULT_PCR_HOST = "localhost";

	public static final int DEFAULT_PCR_COMMAND_PORT = 7940;

	public static final long DEFAULT_SR_INTERVAL = 30000L;

	// START OF TEST ITEMS
	// ---------------------

	GraphTabFrame graphFrame;

	GraphPanel instTempPane;
	GraphPanel instTempPane2;
	InstrumentHealthPanel instrumentHealthPanel;
	SkyPanel skyPanel;

	// ObservationFrame obsFrame;
	ObservationPanel obsPanel;
	SequencePanel seqPanel;
	OperationsPanel opsPanel;
	EfficiencyPanel effPanel;

	ScheduleDataPanel sdp;
	ScheduleCandidatePanel scp;
	GroupWatchMasterPanel gmp;
	FeasibilityTablePanel ftp;
	BeamStatusPanel bssPanel;
    WeatherRulesUpdatePanel weatherRulesUpdatePanel;
	FITSHeadersDialog fitsDialog;

    BasicReactiveSystem testReactiveSystem;

	TestSkyModelMonitor tskymonitor;

	ObservationUpdateListener obsListener;

	TestScheduleListener schedListener;

	TestFeasibilityUpdateListener feasListener;

    WeatherRulesUpdateHandler weatherRulesUpdateHandler;

	InstrumentStatusHandler insthandler;

	AgChecker agChecker;
	MonitorStateDisplay2 agMonLabel;
	MonitorStateDisplay2 trkMonLabel;
	MonitorStateDisplay2 bssMonLabel;
	MonitorStateDisplay2 issMonLabel;
	MonitorStateDisplayHandler monitorStateDisplayHandler;

	InstrumentStatusPanel agStatusPanel;

	// --------------------
	// END OF TEST ITEMS

	/** Map of status categories to keysets. */
	private Map statusCategoryMap;

	/** Maps state=value to Color. */
	private Map stateColorMap;

	/** Maps state-model places to fields. */
	private Map stateModelIds;

	/** List of state-model state places. */
	private Vector stateModelStates;
	private Vector stateModelStates2;

	/** Maps status codes to strings. */
	private Map statusCodeMap;

	/** Maps menuitem actions to items. */
	private Map menuMap;

	/** List of Status Requestors. */
	private Vector requestors;

	/** Layout manager. */
	GridBagLayout grid;

	/** Layout constraints. */
	GridBagConstraints gc;

	/** Layout manager 2. */
	GridBagLayout grid1;

	/** Layout constraints 2. */
	GridBagConstraints gc1;

	/** Layout manager 2. */
	GridBagLayout grid2;

	/** Layout constraints 2. */
	GridBagConstraints gc2;

	ConfigurationProperties config;

	/** RCS host. */
	String rcsHost;
	String agHost;
	String bssHost;
	String schedHost;

	/** RCS Command port. */
	int rcsCmdPort;
	int bssPort;

	ConnFactory cfy = new ConnFactory();

	/** Top level frame. */
	private JFrame frame;

	/** Telescope ID. */
	private String telId;

	/** Telescope name. */
	private String telName;

	/** Telescope icon image label. */
	private Icon telIcon;

	/** Updating time label. */
	TimeLabel timeLabel;

	/** Process-In-Control (PIC) indicator. */
	JLabel picIndicator;

	/** PIC Status indicator. */
	JLabel picStatusIndicator;

	/** Current control process. */
	int processInControl;

	/** Stop/start RCS remotely. */
	// JButton stopStartButton;

	boolean startEnabled = false;

	/** Stop/start CIL remotely. */
	// JButton cilStopStartButton;
	// boolean cilEnabled = false;
	// boolean cilActive = false;

	/** Toggle Mode Button. */
    JButton toggleModeButton;

	/** Toggle Mode Listener. */
	ToggleModeAction toggleModeAction;

    JTextField dustWarningField;

	/** Weather manual override. */
	// JButton weatherOverrideButton;
	// WeatherOverrideListener weatherListener;
	JMenu logMenu;

	JMenu signalMenu;

	JMenu controlMenu;

	JMenu agentMenu;

	JMenu expertMenu;

	JMenu spyMenu;

	// JMenu testMenu;

	JMenu helpMenu;

	JPanel infoPanel;

	JPanel displayPanel;

	JPanel statePanel;

	/** Activity Panel */

	/** AIC Activity. */
	JTextField rcsAicActivityField;

	/** Agent in-charge ID. */
	JTextField rcsAicIdField;

	/** Agent in-charge description. */
	JLabel rcsAicNameField;

	/** Transient running. */
	JTextField rcsAicTaskField;

	/** Displays current RCS (primary) state. */
	JTextField currentStateField;

	JTextField currentNewStateField;
	JLabel currentNewOpField;

	/** Displays current RCS (secondary) state. */
	JTextField secondaryStateField;

	JTextField systemVariableField;
	JTextField axesVariableField;
	JTextField weatherVariableField;
	JTextField enclosureVariableField;
	JTextField mirrcoverVariableField;
	JTextField controlVariableField;
	JTextField networkVariableField;
	JTextField intentVariableField;
	JTextField periodVariableField;

	JTextField ossSchedStatusField;
	JTextField ossBaseStatusField;
	JTextField ossSynStatusField;

	ColorStatePanel colorStateIndicator;

	JButton schedBtn;
	boolean schedEnabled = true;
	MonitorStateDisplay2 socaStatusLabel;

	boolean restricted;

	/** The logged in user name. */
	String loginUserName;

	String localHost;

	/** A logger. */
	Logger logger;

	String scope;

	public VersionInfo rcsVersion;

	private String iregUrl;

	/** Create the RCS GUI. */
	public RcsGUI(String scope) {
		this.scope = scope;

		nf.setMaximumFractionDigits(2);

		grid = new GridBagLayout();
		gc = new GridBagConstraints();

		grid1 = new GridBagLayout();
		gc1 = new GridBagConstraints();

		grid2 = new GridBagLayout();
		gc2 = new GridBagConstraints();

		gc.anchor = GridBagConstraints.WEST;
		gc1.anchor = GridBagConstraints.WEST;
		gc2.anchor = GridBagConstraints.WEST;

		gc.ipadx = 10;
		gc1.ipadx = 10;
		gc2.ipadx = 10;

		gc.insets = new Insets(0, 4, 0, 4);
		gc1.insets = new Insets(0, 4, 0, 6);
		gc2.insets = new Insets(0, 4, 0, 6);

		sdf.setTimeZone(UTC);
		hmsdf.setTimeZone(UTC);

		TCS_Status.mapCodes();

		statusCategoryMap = new HashMap();
		stateModelIds = new HashMap();
		stateModelStates = new Vector();
		stateModelStates2 = new Vector();
		stateColorMap = new HashMap();
		statusCodeMap = new HashMap();
		menuMap = new HashMap();

		requestors = new Vector();

		// We dont know yet
		processInControl = NOONE_IN_CONTROL;

		rcsVersion = new VersionInfo();

		logger = LogManager.getLogger("RCSGUI");
		logger.setLogLevel(3);

		if (System.getProperty("mysql.logging") != null) {
			try {
				LogHandler dbHandler = new MysqlLogHandler(scope, "GUI", "localhost", "logs", "root", "appletart99ax");
				dbHandler.setLogLevel(5);
				logger.addHandler(dbHandler);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		LogHandler console = new ConsoleLogHandler(new BasicLogFormatter(150));
		console.setLogLevel(5);

		logger.addHandler(console);

	}

	/** Create and display a RcsGUI. Configured from file specified as args[0]. */
	public static void main(String args[]) {

		if (args.length > 1 && args[1].equals("test"))
			MetalLookAndFeel.setCurrentTheme(new MyTestLNF());
		else
			MetalLookAndFeel.setCurrentTheme(new MyLNF());

		String scope = args[0];
		File configFile = new File(scope + ".properties");

		// set this so String.format uses UTC not local time
		TimeZone.setDefault(UTC);

		RcsGUI gui = new RcsGUI(scope);

		// get the login name

		String luname = gui.login();

		try {
			gui.configure(configFile);
			gui.display(luname);
		} catch (Exception ex) {
		    System.err.println("Error loading config: " + ex);
		    ex.printStackTrace();
		    return;
		}
		
	}
    
    /** Log the user in. */
	public String login() {
		String luname = null;
		while (luname == null || luname.equals(""))
			luname = JOptionPane.showInputDialog(null, "Please enter your User ID", "RCSGUI Login",
					JOptionPane.QUESTION_MESSAGE);
		return luname;
	}

	/**
	 * Load the configuration from a file.
	 * 
	 * @param file
	 *            The file to load configuration from.
	 */
	private void configure(File file) throws Exception {
		config = new ConfigurationProperties();
		FileInputStream fin = new FileInputStream(file);
		config.load(fin);
		configure(config);
	}

	/** Configure from supplied ConfigurationProperties. */
	private void configure(ConfigurationProperties config) throws Exception {

		WAIT_ICON = new ImageIcon("icons/wait.gif");
		STOP_ICON = new ImageIcon("icons/stop.gif");
		START_ICON = new ImageIcon("icons/start.gif");
		DEFAULT_ICON = new ImageIcon("icons/default.gif");

		rcsHost = config.getProperty("rcs.host", DEFAULT_HOST);
		rcsCmdPort = config.getIntValue("rcs.command.port", DEFAULT_COMMAND_PORT);

		agHost = config.getProperty("ag.host", DEFAULT_HOST);
		bssHost = config.getProperty("bss.host", DEFAULT_HOST);
		bssPort = config.getIntValue("bss.port", DEFAULT_BSS_PORT);
		schedHost = config.getProperty("sched.host", DEFAULT_HOST);

		iregUrl = config.getProperty("ireg.url");

		telId = config.getProperty("telescope.id");

		telName = config.getProperty("telescope.name");

		telIcon = new ImageIcon(config.getProperty("telescope.icon"));

		String myhost = null;
		try {
			myhost = InetAddress.getLocalHost().getHostName();
		} catch (Exception ee) {
		}
		localHost = myhost;

	}

	/** Create all components and display. */
    private void display(String luname) throws Exception {
		loginUserName = luname;

		gc.fill = GridBagConstraints.HORIZONTAL;
		gc1.fill = GridBagConstraints.HORIZONTAL;

		graphFrame = new GraphTabFrame(telId + ": Status Information Display");
		// graphFrame = new GraphTabPanel();

		frame = createFrame(luname);
		infoPanel = createInfoPanel();
		displayPanel = createDisplayPanel();
		JMenuBar menuBar = createMenuBar();
		JToolBar toolbar = createToolBar();

		JPanel lowerPanel = new JPanel(true);
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.add(displayPanel, BorderLayout.CENTER);
		lowerPanel.add(toolbar, BorderLayout.NORTH);

		// lowerPanel.add(displayPanel, BorderLayout.NORTH);
		// lowerPanel.add(graphFrame, BorderLayout.CENTER);

		frame.getContentPane().add(infoPanel, BorderLayout.NORTH);
		frame.getContentPane().add(lowerPanel, BorderLayout.CENTER);
		frame.setJMenuBar(menuBar);
		frame.pack();

		int ww = Toolkit.getDefaultToolkit().getScreenSize().width;
		int hh = Toolkit.getDefaultToolkit().getScreenSize().height;

		frame.setLocation(100, hh / 2);
		frame.setVisible(true);

		graphFrame.setBounds(100, 100, ww - 400, hh / 2 - 100);
		// graphFrame.setLocation(100, 100);
		graphFrame.setVisible(true);

		createStatusRequestors();

		createInstStatusRequestors();

		createPopups();

		createTelemetry();

		createSkyModelMonitor(graphFrame);

		startAll();

	}

	private JToolBar createToolBar() {
		JToolBar toolbar = new JToolBar(SwingConstants.HORIZONTAL);
		toolbar.setLayout(new FlowLayout(FlowLayout.LEFT));

		socaStatusLabel = new MonitorStateDisplay2("SOCA");
		socaStatusLabel.setMonitorEnabled(true);
		socaStatusLabel.setMonitorActivated(true);
		toolbar.add(socaStatusLabel);

		schedBtn = new JButton("Disable SOCA");
		schedEnabled = true;
		schedBtn.addActionListener(new SchedBtnListener());
		toolbar.add(schedBtn);

		toolbar.addSeparator();

		// Monitors

		agMonLabel = new MonitorStateDisplay2("AgMon");
		// try {
		// monitorStateDisplayHandler = new
		// MonitorStateDisplayHandler(agMonLabel);
		// } catch (Exception e) {}
		agMonLabel.setMonitorActivated(false);

		// toolbar.add(createToolbarLabel("Autoguider", SLATE));
		toolbar.add(agMonLabel);

		trkMonLabel = new MonitorStateDisplay2("TrkMon");
		trkMonLabel.setMonitorActivated(false);
		// wait till trklistner setup
		// monitorStateDisplayHandler = new
		// MonitorStateDisplayHandler(trkMonLabel);

		// toolbar.add(createToolbarLabel("Tracking", SLATE));
		toolbar.add(trkMonLabel);

		bssMonLabel = new MonitorStateDisplay2("Beam Steering");
		bssMonLabel.setMonitorActivated(true);
		// toolbar.add(createToolbarLabel("Beam Steering", Color.black));
		toolbar.add(bssMonLabel);

		issMonLabel = new MonitorStateDisplay2("Inst Support");
		issMonLabel.setMonitorActivated(false);
		// toolbar.add(createToolbarLabel("Inst Support", SLATE));
		toolbar.add(issMonLabel);

		return toolbar;
	}

	private JLabel createToolbarLabel(String name, Color color) {
		JLabel label = new JLabel(name);
		// label.setFont(BIG_FONT);
		label.setForeground(color);
		// label.setPreferredSize(new Dimension(80,15));
		label.setHorizontalTextPosition(SwingConstants.RIGHT);

		return label;
	}

	private JButton createToolBarButton(String text, String command, ActionListener al) {

		JButton btn = new JButton(text);
		btn.setActionCommand(command);
		btn.addActionListener(al);
		return btn;

	}

	/** Create popup frames - using config information. */
	private void createPopups() {

		fitsDialog = createFITSHeadersDialog();
		fitsDialog.setVisible(true);

	}

	/** Create the top level frame. */
	private JFrame createFrame(String luname) throws IOException, IllegalArgumentException {

		String type = (config.getProperty("restricted") == null ? "ADMIN:" : "RESTRICTED:");

		frame = new JFrame(telName + ": Robotic Control System: Engineering Interface: [" + type + "V" + VERSION
				+ " - Build " + BUILD + "] User: " + luname);

		// FTN: Robotic Control System: Engineering Interface: [ADMIN:V1.0.0 -
		// Build 6)]

		frame.addWindowListener(new WindowControl());

		return frame;

	}

	private JMenuBar createMenuBar() throws IOException, IllegalArgumentException {

		restricted = (config.getProperty("restricted") != null);

		JMenuBar bar = new JMenuBar();

		// Control.
		ControlMenuListener controlMenuListener = new ControlMenuListener(this);

		controlMenu = bar.add(new JMenu("Control"));
		if (restricted)
			controlMenu.setEnabled(false);

		controlMenu.addActionListener(controlMenuListener);

		controlMenu.add(createMenuItem("Engineering start)", controlMenuListener, "start-rcs-eng"));
		controlMenu.add(createMenuItem("Auto start", controlMenuListener, "start-rcs-auto"));
		controlMenu.add(createMenuItem("Restart (Engineering)", controlMenuListener, "restart-rcs-eng"));
		controlMenu.add(createMenuItem("Restart (Automated)", controlMenuListener, "restart-rcs-auto"));
		controlMenu.add(createMenuItem("Halt RCS", controlMenuListener, "halt-rcs"));
		controlMenu.add(createMenuItem("Reboot OCC", controlMenuListener, "reboot-occ"));
		controlMenu.add(createMenuItem("Shutdown OCC", controlMenuListener, "shutdown-occ"));

		// Signal Events.
		EventMenuListener el = new EventMenuListener(this);
		signalMenu = bar.add(new JMenu("Signal"));
		if (restricted)
			signalMenu.setEnabled(false);

		JMenu menu1 = null;
		JMenu menu2 = null;
		JMenu menu3 = null;

		String itext0 = "Signal";
		String itext1 = "";
		String itext2 = "";
		String itext3 = "";

		JMenuItem item = null;

		try {

			int n0 = config.getIntValue("signal.menu.count", 0);

			for (int i0 = 0; i0 < n0; i0++) {

				// level 0

				String iclass = config.getProperty("menu." + i0 + ".class");
				itext1 = config.getProperty("menu." + i0 + ".text");

				if (iclass.equals("item")) {

					String isig = config.getProperty("menu." + i0 + ".sig");
					System.err.println(itext0 + " -> " + itext1 + " == " + isig);
					item = createMenuItem(itext1, el, isig);
					signalMenu.add(item);

				} else if (iclass.equals("menu")) {

					// level 1
					logger.log(1, "GUI", "-", "-", "Submenu: " + itext0 + " -> " + itext1);
					menu1 = new JMenu(itext1);
					signalMenu.add(menu1);

					int n1 = config.getIntValue("menu.count." + i0, 0);

					for (int i1 = 0; i1 < n1; i1++) {

						iclass = config.getProperty("menu." + i0 + "." + i1 + ".class");
						itext2 = config.getProperty("menu." + i0 + "." + i1 + ".text");

						if (iclass.equals("item")) {

							String isig = config.getProperty("menu." + i0 + "." + i1 + ".sig");
							logger.log(1, "GUI", "-", "-", itext0 + " -> " + itext1 + " -> " + itext2 + " == " + isig);
							item = createMenuItem(itext2, el, isig);
							menu1.add(item);

						} else if (iclass.equals("menu")) {

							// level 2
							logger.log(1, "GUI", "-", "-", "Submenu: " + itext0 + " -> " + itext1 + " -> " + itext2);
							menu2 = new JMenu(itext2);
							menu1.add(menu2);

							int n2 = config.getIntValue("menu.count." + i0 + "." + i1, 0);

							for (int i2 = 0; i2 < n2; i2++) {

								iclass = config.getProperty("menu." + i0 + "." + i1 + "." + i2 + ".class");
								itext3 = config.getProperty("menu." + i0 + "." + i1 + "." + i2 + ".text");

								if (iclass.equals("item")) {

									String isig = config.getProperty("menu." + i0 + "." + i1 + "." + i2 + ".sig");
									logger.log(1, "GUI", "-", "-", itext0 + " -> " + itext1 + " -> " + itext2 + " -> "
											+ itext3 + " == " + isig);
									item = createMenuItem(itext3, el, isig);
									menu2.add(item);

								}
							}

						}

					}
				}

			}
		} catch (Exception e) {
			System.err.println("**** Exeption while configuring sig menu: " + e);

		}

		// Logging.

		// Agents.

		// Expert

		ExpertMenuListener xl = new ExpertMenuListener(this);

		expertMenu = bar.add(new JMenu("Tools"));
		if (restricted)
			expertMenu.setEnabled(false);

		// Get weather overrides.

		int cw = config.getIntValue("weather.override.count", 0);
		for (int iw = 0; iw < cw; iw++) {
			String ovr = config.getProperty("weather.override.code." + iw);
			if (ovr != null)
				xl.addWeatherOverride(ovr);
		}

		expertMenu.add(createMenuItem("Weather Override", xl, "weather-override"));
		// expertMenu.add(createMenuItem("RCS Command...", xl, "rci-command"));

		expertMenu.add(createMenuItem("TCS Command", xl, "tcs-command"));

		JMenu extMenu = new JMenu("Set Extinction");

		ButtonGroup group1 = new ButtonGroup();

		JRadioButtonMenuItem rb;

		// manual photom
		rb = createRadioMenuItem("PHOTOMETRIC", xl, "ext-photom");
		extMenu.add(rb);
		group1.add(rb);

		// manual spectr
		rb = createRadioMenuItem("SPECTROSCOPIC", xl, "ext-spec");
		extMenu.add(rb);
		group1.add(rb);

		// external
		rb = createRadioMenuItem("EXTERNAL", xl, "ext-ext");
		extMenu.add(rb);
		group1.add(rb);

		expertMenu.add(extMenu);

		JMenu seeMenu = new JMenu("Set Seeing");

		ButtonGroup group2 = new ButtonGroup();

		// X
		rb = createRadioMenuItem("GOOD", xl, "see-good");
		seeMenu.add(rb);
		group2.add(rb);

		// A
		rb = createRadioMenuItem("AVERAGE", xl, "see-aver");
		seeMenu.add(rb);
		group2.add(rb);

		// P
		rb = createRadioMenuItem("POOR", xl, "see-poor");
		seeMenu.add(rb);
		group2.add(rb);

		// U
		rb = createRadioMenuItem("USABLE", xl, "see-usab");
		seeMenu.add(rb);
		group2.add(rb);

		expertMenu.add(seeMenu);

		// Spy

		// Help.

		HelpMenuListener hl = new HelpMenuListener(this);

		helpMenu = bar.add(new JMenu("Help"));
		if (restricted)
			helpMenu.setEnabled(false);

		JMenuItem aboutMenu = helpMenu.add(new JMenuItem("about..."));
		aboutMenu.addActionListener(hl);

		return bar;

	}

	private JMenuItem createMenuItem(String label, ActionListener listener, String action) {
		JMenuItem item = new JMenuItem(label);
		item.addActionListener(listener);
		item.setActionCommand(action);

		menuMap.put(action, item);

		return item;
	}

	private JMenuItem createCheckMenuItem(String label, ActionListener listener, String action) {
		JCheckBoxMenuItem item = new JCheckBoxMenuItem(label);
		item.addActionListener(listener);
		item.setActionCommand(action);

		menuMap.put(action, item);

		return item;
	}

	private JRadioButtonMenuItem createRadioMenuItem(String label, ActionListener listener, String action) {
		JRadioButtonMenuItem item = new JRadioButtonMenuItem(label);
		item.addActionListener(listener);
		item.setActionCommand(action);

		menuMap.put(action, item);

		return item;
	}

	/** Create the information panel. */
	private JPanel createInfoPanel() throws IOException, IllegalArgumentException {

		JPanel outer = new JPanel(true);
		outer.setLayout(new BorderLayout());

		JPanel infoPanel = new JPanel();
		infoPanel.setBorder(loweredbevel);
		infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 10, 0));

		// Icon.
		JLabel iconLabel = new JLabel(telId, telIcon, SwingConstants.LEFT);
		// iconLabel.setBorder(raisedbevel);
		iconLabel.setHorizontalTextPosition(SwingConstants.LEFT);
		iconLabel.setHorizontalAlignment(SwingConstants.LEFT);
		iconLabel.setFont(HUGE_FONT);
		iconLabel.setForeground(Color.blue);
		infoPanel.add(iconLabel);

		// Time Label.
		JLabel label = new JLabel("Current time");
		label.setVerticalAlignment(SwingConstants.VERTICAL);
		label.setFont(new Font("serif", Font.ITALIC + Font.BOLD, 10));
		timeLabel = new TimeLabel();
		infoPanel.add(createPair(label, timeLabel));

		// PIC and PIC State indicator.
		label = new JLabel("Process In Control");
		label.setVerticalAlignment(SwingConstants.VERTICAL);
		label.setFont(new Font("serif", Font.ITALIC + Font.BOLD, 10));

		picIndicator = new JLabel("NO COMMS       ");
		picIndicator.setBackground(LIGHTSLATE);
		picIndicator.setBorder(loweredbevel);
		picIndicator.setFont(BIG_FONT);
		picIndicator.setForeground(Color.blue);
		picIndicator.setOpaque(true);
		infoPanel.add(createPair(label, picIndicator));

		// PIC Status (state-model state OR WD state)
		label = new JLabel("Status");
		label.setVerticalAlignment(SwingConstants.VERTICAL);
		label.setFont(new Font("serif", Font.ITALIC + Font.BOLD, 10));

		picStatusIndicator = new JLabel("NO COMMS       ");
		picStatusIndicator.setBackground(LIGHTSLATE);
		picStatusIndicator.setBorder(loweredbevel);
		picStatusIndicator.setFont(BIG_FONT);
		picStatusIndicator.setForeground(Color.blue);
		picStatusIndicator.setOpaque(true);
		infoPanel.add(createPair(label, picStatusIndicator));

		// Opsmgr state
		label = new JLabel("Operations Manager");
		label.setVerticalAlignment(SwingConstants.VERTICAL);
		label.setFont(new Font("serif", Font.ITALIC + Font.BOLD, 10));

		currentNewOpField = new JLabel("UNKNOWN        ");
		currentNewOpField.setBackground(LIGHTSLATE);
		currentNewOpField.setBorder(loweredbevel);
		currentNewOpField.setFont(BIG_FONT);
		currentNewOpField.setForeground(Color.red);
		currentNewOpField.setOpaque(true);
		infoPanel.add(createPair(label, currentNewOpField));

		// MCA
		label = new JLabel("Observing Mode");
		label.setVerticalAlignment(SwingConstants.VERTICAL);
		label.setFont(new Font("serif", Font.ITALIC + Font.BOLD, 10));

		rcsAicNameField = new JLabel("UNKNOWN        ");
		rcsAicNameField.setBackground(LIGHTSLATE);
		rcsAicNameField.setBorder(loweredbevel);
		rcsAicNameField.setFont(BIG_FONT);
		rcsAicNameField.setForeground(Color.blue);
		rcsAicNameField.setOpaque(true);
		infoPanel.add(createPair(label, rcsAicNameField));

		// Some buttons..

		toggleModeAction = new ToggleModeAction(this, "N/A", WAIT_ICON);
		toggleModeButton = new JButton(toggleModeAction);
		toggleModeButton.setBackground(YELLOWSLATE);
		toggleModeButton.setForeground(Color.gray);
		// toggleModeButton.setBorder(BorderFactory.createCompoundBorder(
		// BorderFactory.createLineBorder(Color.blue,2),
		// BorderFactory.createLineBorder(Color.green,2)));
		toggleModeButton.setEnabled(false);
		infoPanel.add(toggleModeButton);

		// some test buttons

		dustWarningField = new JTextField("UNKNOWN");
		dustWarningField.setBackground(YELLOWSLATE);
                dustWarningField.setForeground(Color.gray);
		dustWarningField.setOpaque(true);
		dustWarningField.setEnabled(false);
		infoPanel.add(dustWarningField);

		/*
		 * JPanel p = new JPanel(true); p.setLayout(new
		 * FlowLayout(FlowLayout.LEFT));
		 * 
		 * RoundButton b2 = new RoundButton(new
		 * ImageIcon("icons/Button-First-icon.png"));
		 * b2.setRolloverEnabled(true); p.add(b2); RoundButton b5 = new
		 * RoundButton(new ImageIcon("icons/Button-Rewind-icon.png"));
		 * p.add(b5);
		 * 
		 * RoundButton b1 = new RoundButton(new
		 * ImageIcon("icons/Button-Fast-Forward-icon.png")); p.add(b1);
		 * 
		 * RoundButton b3 = new RoundButton(new
		 * ImageIcon("icons/Button-Last-icon.png")); p.add(b3);
		 * 
		 * RoundButton b4 = new RoundButton(new
		 * ImageIcon("icons/Button-Refresh-icon.png")); p.add(b4);
		 * 
		 * RoundButton b6 = new RoundButton(new
		 * ImageIcon("icons/Zoom-In-icon.png")); p.add(b6); RoundButton b7 = new
		 * RoundButton(new ImageIcon("icons/Zoom-Out-icon.png")); p.add(b7);
		 * RoundButton b8 = new RoundButton(new
		 * ImageIcon("icons/info-icon.png")); p.add(b8); infoPanel.add(p);
		 */
		outer.add(infoPanel, BorderLayout.CENTER);

		colorStateIndicator = new ColorStatePanel();
		outer.add(colorStateIndicator, BorderLayout.SOUTH);

		return outer;

	}

	private Component createPair(Component c1, Component c2) {
		JPanel p = new JPanel(true);
		p.setLayout(new GridLayout(2, 1));
		p.add(c1);
		p.add(c2);
		return p;
	}

	/** Create and pack the display information panel. */
	private JPanel createDisplayPanel() throws Exception {

		JPanel displayPanel = new JPanel();
		displayPanel.setBorder(loweredbevel);
		displayPanel.setLayout(new BorderLayout());

		statePanel = createNewDisplayPanel();
		displayPanel.add(statePanel, BorderLayout.WEST);

		JTabbedPane tabPanel = createTabPanel();
		displayPanel.add(tabPanel, BorderLayout.EAST);

		return displayPanel;

	}

	private JPanel createNewDisplayPanel() throws Exception {

		JPanel displayPanel = new JPanel();
		displayPanel.setLayout(grid2);
		displayPanel.setBorder(loweredbevel);
		displayPanel.setBackground(LIGHTSLATE);
		displayPanel.setOpaque(true);

		// Variables
		JPanel variablePanel = new JPanel();
		variablePanel.setLayout(grid1);
		// variablePanel.setBorder(BorderFactory.createTitledBorder("System Variables"));
		variablePanel.setBorder(BorderFactory.createTitledBorder(loweredbevel, "System Variables", TitledBorder.LEFT,
				TitledBorder.BELOW_TOP, TITLE_FONT, Color.blue));

		variablePanel.setBackground(LIGHTSLATE);
		variablePanel.setOpaque(true);

		// variables
		systemVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "System", systemVariableField, 0);

		axesVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Axes", axesVariableField, 1);

		weatherVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Weather", weatherVariableField, 2);

		enclosureVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Enclosure", enclosureVariableField, 3);

		mirrcoverVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "PMC", mirrcoverVariableField, 4);

		controlVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Control", controlVariableField, 5);

		networkVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Network", networkVariableField, 6);

		intentVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Intent", intentVariableField, 7);

		periodVariableField = createStateField("", ORANGESLATE);
		addNewVariableField(variablePanel, grid1, gc1, "Period", periodVariableField, 8);

		makeComp(displayPanel, grid2, gc2, variablePanel, 0, 6, 4, 9);

		// Sky model

		skyPanel = new SkyPanel();
		makeComp(displayPanel, grid2, gc2, skyPanel, 0, 0, 4, 6);

		// obsFrame = new ObservationFrame();
		obsPanel = new ObservationPanel();
		seqPanel = new SequencePanel();

		opsPanel = new OperationsPanel();
		effPanel = new EfficiencyPanel();
		obsListener = new ObservationUpdateListener(obsPanel, seqPanel, opsPanel, effPanel, null, null);

		// scheduler
		sdp = new ScheduleDataPanel();
		scp = new ScheduleCandidatePanel();
		gmp = new GroupWatchMasterPanel();
		schedListener = new TestScheduleListener(sdp, scp, gmp);

		ftp = new FeasibilityTablePanel();

		feasListener = new TestFeasibilityUpdateListener(ftp, sdp);

		bssPanel = new BeamStatusPanel();


		// weather rules
		weatherRulesUpdatePanel = new WeatherRulesUpdatePanel();
		weatherRulesUpdateHandler = new WeatherRulesUpdateHandler(weatherRulesUpdatePanel);
		
		testReactiveSystem = new BasicReactiveSystem(new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0)));
		XmlConfigurator.use(new File("rules.xml")).configure(testReactiveSystem);
		
		testReactiveSystem.addReactiveSystemUpdateListener(weatherRulesUpdateHandler);
		testReactiveSystem.startCacheReader();
	
		// inst panel and health panel

		JPanel instPanel = new JPanel(true);
		int ninst = config.getIntValue("number.instruments", 0);
		instPanel.setLayout(new GridLayout(ninst + 2, 1));
		instPanel.setBorder(BorderFactory.createTitledBorder(loweredbevel, "Instruments", TitledBorder.LEFT,
				TitledBorder.BELOW_TOP, TITLE_FONT, Color.blue));
		instPanel.setBackground(LIGHTSLATE);
		instPanel.setOpaque(true);

		instrumentHealthPanel = new InstrumentHealthPanel();

		insthandler = new InstrumentStatusHandler(instrumentHealthPanel);

		String chartName = "InstTemp";
		instTempPane = new GraphPanel(chartName, "Inst Temps", -200.0, 0.0);
		graphFrame.addGraphPanel(chartName, instTempPane);

		String chartName2 = "InstADU";
		instTempPane2 = new GraphPanel(chartName2, "Inst ADU", 0.0, 3500.0);
		graphFrame.addGraphPanel(chartName2, instTempPane2);
		
		logger.log(1, "GUI", "-", "-", "Adding Graph named: [InstTemp]");
		logger.log(1, "GUI", "-", "-", "Adding Graph named: [InstADU]");
		
		for (int in = 0; in < ninst; in++) {
			// instrument.1 = RATCAM instrument.1.cat=RATCAM
			String instId = config.getProperty("instrument." + in);
			String instName = config.getProperty("instrument." + in + ".cat");
			String tkw = config.getProperty("instrument." + in + ".tkw", "Temperature");
			String akw = config.getProperty("instrument." + in + ".akw");

			TimeSeries ts = new TimeSeries(instName, Second.class);
			ts.setMaximumItemCount(1500);
			graphFrame.addCategorySeries(instName, tkw, chartName, ts);

			if (akw != null) {
				TimeSeries ts2 = new TimeSeries(instName, Second.class);
				ts2.setMaximumItemCount(1500);
				graphFrame.addCategorySeries(instName, akw, chartName2, ts2);
			}
			
			InstrumentStatusPanel instStatusPanel = new InstrumentStatusPanel(instName, tkw, ts);
			InstrumentDataPanel instDataPanel = null;
			if (instName.equalsIgnoreCase("FRODO_RED"))
				instDataPanel = new FrodoGeneralPanel();
			insthandler.addInstrument(instName, instStatusPanel, instDataPanel);

			instPanel.add(instStatusPanel);

			instrumentHealthPanel.addInstrument(instName);

		}
		// Autoguider
		TimeSeries agts = new TimeSeries("AUTOGUIDER", Second.class);
		agts.setMaximumItemCount(500);
		graphFrame.addCategorySeries("AUTOGUIDER", "temperature", chartName, agts);
		agStatusPanel = new InstrumentStatusPanel("AUTOGUIDER", "temperature", agts);
		instPanel.add(agStatusPanel);
		
		instrumentHealthPanel.addInstrument("AUTOGUIDER");

		insthandler.startLostDataHandler();

		instrumentHealthPanel.addTimeAxis();

		// makeComp(displayPanel, grid2, gc2, instPanel, 0,9,8,ninst+1);
		makeComp(displayPanel, grid2, gc2, instPanel, 0, 15, 3, ninst + 2);

		// OSS
		JPanel ossPanel = new JPanel(true);
		ossPanel.setLayout(new GridLayout(3, 2));
		ossPanel.setBorder(BorderFactory.createTitledBorder(loweredbevel, "Scheduler and OSS", TitledBorder.LEFT,
				TitledBorder.BELOW_TOP, TITLE_FONT, Color.blue));

		ossPanel.setBackground(LIGHTSLATE);
		ossPanel.setOpaque(true);

		JLabel olab1 = new JLabel("Scheduler");
		olab1.setFont(new Font("courier", Font.PLAIN, 8));
		ossPanel.add(olab1);

		ossSchedStatusField = new JTextField(10);
		ossSchedStatusField.setBorder(raisedbevel);
		ossSchedStatusField.setBackground(LIGHTSLATE);
		ossSchedStatusField.setOpaque(true);
		ossSchedStatusField.setFont(new Font("courier", Font.PLAIN, 8));
		ossPanel.add(ossSchedStatusField);

		JLabel olab2 = new JLabel("Base Models");
		olab2.setFont(new Font("courier", Font.PLAIN, 8));
		ossPanel.add(olab2);

		ossBaseStatusField = new JTextField(10);
		ossBaseStatusField.setBorder(raisedbevel);
		ossBaseStatusField.setBackground(LIGHTSLATE);
		ossBaseStatusField.setOpaque(true);
		ossBaseStatusField.setFont(new Font("courier", Font.PLAIN, 8));
		ossPanel.add(ossBaseStatusField);

		JLabel olab3 = new JLabel("Synoptics");
		olab3.setFont(new Font("courier", Font.PLAIN, 8));
		ossPanel.add(olab3);

		ossSynStatusField = new JTextField(10);
		ossSynStatusField.setBorder(raisedbevel);
		ossSynStatusField.setBackground(LIGHTSLATE);
		ossSynStatusField.setOpaque(true);
		ossSynStatusField.setFont(new Font("courier", Font.PLAIN, 8));
		ossPanel.add(ossSynStatusField);

		makeComp(displayPanel, grid2, gc2, ossPanel, 0, 15 + ninst + 2, 2, 3);

		return displayPanel;

	}

	private void addNewVariableField(JPanel panel, GridBagLayout grid1, GridBagConstraints gc1, String name,
			JTextField varField, int cc) {
		makeComp(panel, grid1, gc1, createStateLabel(name, LIGHTSLATE), 0, cc, 1, 1);
		makeComp(panel, grid1, gc1, varField, 1, cc, 1, 1);

	}

	private JPanel createStatePanel() throws IOException, IllegalArgumentException {

		JPanel statePanel = new JPanel();
		statePanel.setLayout(grid1);
		statePanel.setBorder(loweredbevel);
		statePanel.setBackground(LIGHTSLATE);
		statePanel.setOpaque(true);

		// Look for entries in config:- state.model.entry.count = N
		//
		// state.model.entry.0 = <label> e.g. C:CONTROL
		// <label>.class = {CLAMP | STATE | EVENT} e.g. C:CONTROL.class = STATE
		// <label>.name = <display name> e.g. C:CONTROL.name = Control
		// <label>.<valueN>.color = <color> e.g. C:CONTROL.CTRL_ENABLED.color =
		// 55FF23

		int nStates = config.getIntValue("state.model.entry.count", -1);
		if (nStates == -1)
			throw new IllegalArgumentException("No states defined");

		int c1 = 0; // count column 1
		int c2 = 0; // count column 2

		makeComp(statePanel, grid1, gc1, createStateLabel("Current (EXEC-LOOP)", LIGHTSLATE), 0, 0, 1, 1);
		currentStateField = createStateField("Primary", ORANGESLATE);
		makeComp(statePanel, grid1, gc1, currentStateField, 1, 0, 2, 1);
		c1++;

		makeComp(statePanel, grid1, gc1, createStateLabel("Current (ENC-LOOP)", LIGHTSLATE), 0, 1, 1, 1);
		secondaryStateField = createStateField("Secondary", ORANGESLATE);
		makeComp(statePanel, grid1, gc1, secondaryStateField, 1, 1, 2, 1);
		c1++;

		for (int is = 0; is < nStates; is++) {

			String sid = config.getProperty("state.model.entry." + is);
			if (sid == null)
				throw new IllegalArgumentException("No StateId for entry: " + is);

			String sclass = config.getProperty("state.model.entry." + is + ".class");
			if (sclass == null)
				throw new IllegalArgumentException("No class for: " + sid);

			String sname = config.getProperty("state.model.entry." + is + ".name");

			String scol = config.getProperty("state.model.entry." + is + ".color");

			if (sclass.equals("STATE")) {
				stateModelStates.add(sid);
				// logger.log(1,"GUI", "-","-", "Noting State: "+sid);
			} else if (sclass.equals("S2")) {
				stateModelStates2.add(sid);
			} else if (sclass.equals("EVENT")) {
				makeComp(statePanel, grid1, gc1, createStateLabel(sname, LIGHTSLATE), 0, c1, 1, 1);
				JTextField sf = createStateField(sname, YELLOWSLATE);
				stateModelIds.put(sid, sf);
				makeComp(statePanel, grid1, gc1, sf, 1, c1, 2, 1);
				// logger.log(1,"GUI", "-","-", "Adding E state entry: "+sid+"
				// aka
				// "+sname+" at 0,"+c1);
				c1++;
			} else if (sclass.equals("CLAMP")) {
				makeComp(statePanel, grid1, gc1, createStateLabel(sname, LIGHTSLATE), 3, c2, 1, 1);
				JTextField sf = createStateField(sname, LIGHTSLATE);
				stateModelIds.put(sid, sf);

				makeComp(statePanel, grid1, gc1, sf, 4, c2, 2, 1);
				// logger.log(1,"GUI", "-","-", "Adding C state entry: "+sid+"
				// aka
				// "+sname+" at 1,"+c2);
				c2++;

				// look for some entries like: state.entry.<is>.<value> =
				// <color>

				Enumeration e = config.propertyNames();
				while (e.hasMoreElements()) {
					String kv = (String) e.nextElement();
					// logger.log(1,"GUI", "-","-",
					// "Any Starting: "+sid+" //"+kv);

					if (kv.startsWith(sid)) {

						String value = kv.substring(sid.length() + 1);

						// logger.log(1,"GUI", "-","-",
						// "Looking for:["+value+"]");
						String colStr = config.getProperty(kv);

						Color col = LIGHTSLATE;
						if (colStr.equals("red"))
							col = Color.red;
						else if (colStr.equals("orange"))
							col = Color.orange;
						else if (colStr.equals("yellow"))
							col = Color.yellow;
						else if (colStr.equals("green"))
							col = Color.green;
						else if (colStr.equals("blue"))
							col = Color.cyan.darker();
						else
							col = LIGHTSLATE;

						stateColorMap.put(sid + "." + value, col);

					}
				}

			}

		}

		return statePanel;

	}

	private JTextField createStateField(String placeId, Color bgcolor) {

		JTextField field = new JTextField("UNKNOWN", 12);
		field.setBorder(raisedbevel);
		field.setBackground(bgcolor);
		field.setOpaque(true);
		field.setFont(STATE_FONT);
		field.setForeground(Color.black);
		return field;

	}

	private JLabel createStateLabel(String labeltext, Color bgcolor) {
		JLabel label = new JLabel(labeltext);
		label.setBackground(bgcolor);
		label.setOpaque(true);
		label.setFont(SMALL_FONT_2);
		return label;
	}

	// /** Displays RCS Activity. */
	// private JPanel createActivityPanel() throws IOException,
	// IllegalArgumentException {

	// JPanel activityPanel = new JPanel();
	// activityPanel.setLayout(grid1);
	// activityPanel.setBorder(loweredbevel);
	// activityPanel.setBackground(LIGHTSLATE);
	// activityPanel.setOpaque(true);

	// // AIC Name/Desc
	// makeComp(activityPanel, grid1, gc1, new JLabel("Agent in Charge"), 0,
	// 0, 1, 1);
	// rcsAicNameField = new JTextField(12);
	// rcsAicNameField.setText("N/A");
	// makeComp(activityPanel, grid1, gc1, rcsAicNameField, 1, 0, 1, 1);
	// // AIC Activity
	// makeComp(activityPanel, grid1, gc1, new JLabel("Activity"), 2, 0, 1, 1);
	// rcsAicActivityField = new JTextField(6);
	// rcsAicActivityField.setText("N/A");
	// makeComp(activityPanel, grid1, gc1, rcsAicActivityField, 3, 0, 1, 1);
	// // AIC ID
	// makeComp(activityPanel, grid1, gc1, new JLabel("Agent ID"), 0, 1, 1, 1);
	// rcsAicIdField = new JTextField(6);
	// rcsAicIdField.setText("N/A");
	// makeComp(activityPanel, grid1, gc1, rcsAicIdField, 1, 1, 1, 1);
	// // AIC Task
	// makeComp(activityPanel, grid1, gc1, new JLabel("Current Task"), 2, 1,
	// 1, 1);
	// rcsAicTaskField = new JTextField(6);
	// rcsAicTaskField.setText("N/A");
	// makeComp(activityPanel, grid1, gc1, rcsAicTaskField, 3, 1, 1, 1);

	// return activityPanel;

	// }

	private JTabbedPane createTabPanel() throws IOException, IllegalArgumentException {

		JTabbedPane tabPanel = new JTabbedPane();
		tabPanel.setBorder(loweredbevel);
		tabPanel.setBackground(LIGHTSLATE);
		tabPanel.setOpaque(true);

		String tabName = null;
		String tabCat = null;
		Map tabMap = null;

		String itemLabel = null;
		String itemUnits = null;
		String itemDef = null;
		String itemType = null;
		String itemKey = null;

		int itemx = 0;
		int itemy = 0;

		// create all the charts.
		String chartName = null;
		int chartCount = config.getIntValue("chart.count", -1);
		for (int g = 0; g < chartCount; g++) {
			chartName = config.getProperty("chart." + g + ".name");
			String label = config.getProperty("chart." + g + ".label");
			double min = 0.0;
			double max = 0.0;
			try {
				min = config.getDoubleValue("chart." + g + ".min");
				max = config.getDoubleValue("chart." + g + ".max");
			} catch (ParseException px) {
				px.printStackTrace();
				return null;
			}
			GraphPanel chartPane = new GraphPanel(chartName, label, min, max);
			graphFrame.addGraphPanel(chartName, chartPane);

			logger.log(1, "GUI", "-", "-", "Adding Graph named: [" + chartName + "]");

		}

		// get all the status cat/keys.
		int tabCount = config.getIntValue("tab.count", -1);

		if (tabCount == -1)
			throw new IllegalArgumentException("No tabs");

		for (int t = 0; t < tabCount; t++) {

			tabName = config.getProperty("tab." + t + ".name");
			tabCat = config.getProperty("tab." + t + ".cat");

			tabMap = getStatusCategoryMap(tabCat);

			TabPanel tab = new TabPanel(tabName);
			// Read the tab names.

			int itemCount = config.getIntValue(tabName + ".count", -1);
			if (itemCount == -1)
				throw new IllegalArgumentException("No items for tab: " + tabName);

			for (int i = 0; i < itemCount; i++) {

				itemLabel = config.getProperty(tabName + ".item." + i + ".label");
				itemUnits = config.getProperty(tabName + ".item." + i + ".units");
				itemDef = config.getProperty(tabName + ".item." + i + ".default");
				itemKey = config.getProperty(tabName + ".item." + i + ".key");
				itemType = config.getProperty(tabName + ".item." + i + ".type");

				try {
					itemx = config.getIntValue(tabName + ".item." + i + ".xpos");
					itemy = config.getIntValue(tabName + ".item." + i + ".ypos");
				} catch (ParseException px) {
					throw new IllegalArgumentException("While creating tab panel: " + itemLabel + ": " + px);
				}

				// makeComp(tab, grid1, gc1, new JLabel(itemLabel), itemx,
				// itemy, 1,1);
				StateField field = new StateField(itemLabel, 6, itemUnits);
				field.setText(itemDef);
				gc1.anchor = GridBagConstraints.EAST;
				makeComp(tab, grid1, gc1, field, itemx, itemy, 1, 1);
				// makeComp(tab, grid1, gc1, new JLabel(itemUnits), itemx+2,
				// itemy, 1,1);

				int it = 0;
				if (itemType.equals("D"))
					it = ITEM_TYPE_DOUBLE;
				else if (itemType.equals("I"))
					it = ITEM_TYPE_INTEGER;
				else if (itemType.equals("S"))
					it = ITEM_TYPE_STATE;
				else
					it = ITEM_TYPE_STRING;

				tabMap.put(itemKey, new FieldDescriptor(field, it));

				logger.log(1, "GUI", "-", "-", "Processing tabkey: [" + tabCat + "." + itemKey + "]");

				String graphName = config.getProperty(tabName + ".item." + i + ".graph");
				if (graphName != null) {
					logger.log(1, "GUI", "-", "-", "Will use chart named: [" + graphName + "]");
					TimeSeries ts = new TimeSeries(itemLabel, Second.class);
					ts.setMaximumItemCount(500);
					graphFrame.addCategorySeries(tabCat, itemKey, graphName, ts);

				}

			}

			tabPanel.addTab(tabName, tab);

		}

		// load: [state <-> code] mappings. #### Use TCS_Status.codeString() for
		// now ###

		// add curent obs
		tabPanel.addTab("Current Obs", obsPanel);

		tabPanel.addTab("Current Seq.", seqPanel);

		tabPanel.addTab("Operations", opsPanel);

		tabPanel.addTab("Efficiency", effPanel);

		tabPanel.add("Contention", sdp);

		tabPanel.add("Schedule", scp);

		tabPanel.add("Prescan", ftp);

		tabPanel.add("Watch", gmp);

		tabPanel.add("Beam", bssPanel);

		// add instrument health
		tabPanel.addTab("Instruments", instrumentHealthPanel);

		// frodo test panel etc
		int ninst = config.getIntValue("number.instruments", 0);
		for (int in = 0; in < ninst; in++) {
			String instId = config.getProperty("instrument." + in);
			String instName = config.getProperty("instrument." + in + ".cat");
			InstrumentDataPanel idp = insthandler.getDataPanel(instName);
			if (idp != null)
				tabPanel.addTab(instName, idp);
		}

		tabPanel.addTab("W Rules", weatherRulesUpdatePanel);

		gc1.anchor = GridBagConstraints.WEST;
		return tabPanel;

	}

	private void createStatusRequestors() {

		Enumeration e = config.propertyNames();
		while (e.hasMoreElements()) {

			String kv = (String) e.nextElement();

			// status.requestor.XX = METEO
			if (kv.startsWith("status.requestor")) {

				String srid = config.getProperty(kv);

				long sint = config.getLongValue("status." + srid + ".interval", DEFAULT_SR_INTERVAL);

				createStatusRequestor(srid, sint);

			}

		}

	}

    private void createReactiveSystem() {

	// 


    }


	/** Create a StatusRequestor QueryThread. */
	private void createStatusRequestor(String cat, long interval) {
		ngat.message.GUI_RCS.GET_STATUS getStat = new ngat.message.GUI_RCS.GET_STATUS("test:" + cat);
		getStat.setCategory(cat);
		QueryThread qryStat = new QueryThread(cat);
		qryStat.setPollingInterval(interval);
		qryStat.setTimeout((interval / 2));
		qryStat.setCommand(getStat);
		qryStat.setResponseHandler(new Handler(cat));
		qryStat.setConnectionFactory(cfy);
		qryStat.setConnectionId("RCS_CMD");

		requestors.add(qryStat);

	}

	/** Create a InstStatusRequestor QueryThread. */
	private void createInstStatusRequestors() {

		IregLookup ireglookup = new IregLookup(iregUrl, insthandler);
		ireglookup.start();

	}

	/** Creates the Telemetry classes. */
	private void createTelemetry() {

		String client = "rcsgui";
		try {
			client = "rcsgui@" + InetAddress.getLocalHost().getCanonicalHostName() + ":" + loginUserName + ":"
					+ hmsdf.format(new Date());

		} catch (Exception nx) {
			nx.printStackTrace();
		}

		final ObservationUpdateListener fobsListener = obsListener;
		Runnable r1 = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						GroupOperationsMonitor gom = (GroupOperationsMonitor) Naming.lookup("rmi://" + rcsHost
								+ "/GroupOperationsMonitor");
						gom.addGroupOperationsListener(fobsListener);
						System.err.println("Rebind to GOM");
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

		final TestScheduleListener fschedListener = schedListener;
		Runnable r2 = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						
						SchedulingStatusProvider sched = (SchedulingStatusProvider) Naming.lookup("rmi://" + schedHost + "/ScheduleDespatcher");
						sched.addSchedulingUpdateListener(fschedListener);
						System.err.println("Re-registered as sched listener on: " + sched);
					} catch (Exception cx) {
						System.err.println("Failed to re-register as sched listener");
						cx.printStackTrace();
					}
					try {
						Thread.sleep(120000L);
					} catch (InterruptedException ix) {
					}
				} // next rebind
			}
		};

		(new Thread(r2)).start();

		final TestFeasibilityUpdateListener ffeasListener = feasListener;
		Runnable r3 = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						FeasibilityPrescanMonitor prescan = (FeasibilityPrescanMonitor) Naming.lookup("rmi://"
								+ schedHost + "/FeasibilityPrescanner");
						prescan.addFeasibilityPrescanUpdateListener(ffeasListener);
						System.err.println("Re-registered as prescan listener on: " + prescan);
					} catch (Exception cx) {
						System.err.println("Failed to re-register as prescan listener");
						cx.printStackTrace();
					}
					try {
						Thread.sleep(120000L);
					} catch (InterruptedException ix) {
					}
				} // next rebind
			}
		};

		(new Thread(r3)).start();

		// check the curent status of SOCA
		final MonitorStateDisplay2 fsocaStatusLabel = socaStatusLabel;
		Runnable r4 = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						System.err.println("AAAAAAAAAContacting SOCA controller...");
						TaskModeControllerManagement tmc = (TaskModeControllerManagement) Naming.lookup("rmi://"
								+ rcsHost + "/SOCAModeController");
						fsocaStatusLabel.setMonitorActivated(true);
						if (tmc.isEnabled()) {
							fsocaStatusLabel.setMonitorEnabled(true);
						} else {
							fsocaStatusLabel.setMonitorEnabled(false);
						}
					} catch (Exception cx) {
						System.err.println("Failed to determine soca state");
						cx.printStackTrace();
						socaStatusLabel.setMonitorActivated(false);
						socaStatusLabel.setMonitorEnabled(false);
					}
					try {
						Thread.sleep(30000L);
					} catch (InterruptedException ix) {
					}
				} // next test
			}
		};

		(new Thread(r4)).start();


		// weathere rules
		final BasicReactiveSystem ftrs = testReactiveSystem;
                Runnable r5 = new Runnable() {
                        @Override
						public void run() {
			    while (true) {
				try {
				    
				    MeteorologyStatusProvider meteo = (MeteorologyStatusProvider) Naming
					.lookup("rmi://"+rcsHost+"/Meteorology");
				    System.err.println("Located Meteo provider: " + meteo);
				    
				    meteo.addMeteorologyStatusUpdateListener(ftrs);
				    System.err.println("Rebind TRS to meteo provider");
				    
				} catch (Exception cx) {
				    System.err.println("Failed to rebind TRS to Meteo");
				    cx.printStackTrace();
				}
				try {
				    Thread.sleep(120000L);
				} catch (InterruptedException ix) {
				}
			    } // next rebind
                        }
		    };

                (new Thread(r5)).start();

		
	}

	private void createSkyModelMonitor(GraphTabFrame graphFrame) {

		try {

			long cadence = config.getLongValue("experimental.skymodel.monitor.cadence", 10000L);

			tskymonitor = new TestSkyModelMonitor(telName, cadence);
			ChartPanel cp = tskymonitor.createChartPanel();

			graphFrame.addGeneralPanel("Seeing", cp);

			// tskymonitor.createGraphFrame();
			// logger.log(1,"GUI", "-","-", "Created SkyModel frame");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/** Creates the LCD. */
	private FITSHeadersDialog createFITSHeadersDialog() {

		fitsDialog = new FITSHeadersDialog(this);

		return fitsDialog;
	}

	/** Display the LCD. */
	public void displayFITSHeadersDialog(boolean show) {

		if (fitsDialog != null)
			fitsDialog.setVisible(show);

	}

	/** Start any updating threads. */
	private void startAll() {

		System.err.println("Startup::Starting monitoring...");

		(new Thread(timeLabel)).start();

		System.err.println("Startup::Starting status requestors...");
		Iterator it = requestors.iterator();
		while (it.hasNext()) {

			QueryThread qt = (QueryThread) it.next();
			qt.start();

		}

		// ID Stuff

		ID mid = new ID("Test");

		QueryThread qid = new QueryThread("ID");
		qid.setPollingInterval(10000L);
		qid.setTimeout(20000L);
		qid.setCommand(mid);
		qid.setResponseHandler(new IdHandler());
		qid.setConnectionFactory(cfy);
		qid.setConnectionId("RCS_CMD");
		qid.start();

		// STATE_MODEL

		GET_STATE_MODEL gstm = new GET_STATE_MODEL("test");

		QueryThread gstmt = new QueryThread("STATE_MODEL");

		gstmt.setPollingInterval(10000L);
		gstmt.setTimeout(20000L);
		gstmt.setCommand(gstm);
		gstmt.setResponseHandler(new SMHandler());
		gstmt.setConnectionFactory(cfy);
		gstmt.setConnectionId("RCS_CMD");
		gstmt.start();

		// Scheduler
		ngat.message.GUI_RCS.GET_STATUS getOss = new ngat.message.GUI_RCS.GET_STATUS("test:oss");
		getOss.setCategory("X_OSS_MONITOR");
		QueryThread qryOss = new QueryThread("OSS_MONITOR");
		qryOss.setPollingInterval(10000L);
		qryOss.setTimeout(20000L);
		qryOss.setCommand(getOss);
		qryOss.setResponseHandler(new OssHandler());
		qryOss.setConnectionFactory(cfy);
		qryOss.setConnectionId("RCS_CMD");
		qryOss.start();

		// BSS
		ngat.message.RCS_BSS.GET_STATUS getBss = new ngat.message.RCS_BSS.GET_STATUS("test:bss");
		QueryThread qryBss = new QueryThread("BSS_MONITOR");
		qryBss.setPollingInterval(30000L);
		qryBss.setTimeout(20000L);
		qryBss.setCommand(getBss);
		qryBss.setResponseHandler(new BssHandler());
		qryBss.setConnectionFactory(cfy);
		qryBss.setConnectionId("BSS");
		qryBss.start();

		// RCS Version
		ngat.message.GUI_RCS.GET_VERSION getv = new ngat.message.GUI_RCS.GET_VERSION("test-v");
		QueryThread gvt = new QueryThread("RCS_VERSION");
		gvt.setPollingInterval(60000L); // 1 minute
		gvt.setTimeout(20000L);
		gvt.setCommand(getv);
		gvt.setResponseHandler(new GVHandler());
		gvt.setConnectionFactory(cfy);
		gvt.setConnectionId("RCS_CMD");
		gvt.start();

		final TestSkyModelMonitor ftsm = tskymonitor;
		System.err.println("Startup::Starting skymodel prediction monitoring using host: " + rcsHost);

		Runnable r = new Runnable() {
			@Override
			public void run() {
				ftsm.run(rcsHost);
			}
		};
		(new Thread(r)).start();

		// Telemetry.
		System.err.println("Startup::Starting agmonitor");
		try {
			AutoguiderMonitor agMon = (AutoguiderMonitor) Naming.lookup("rmi://" + rcsHost + "/AutoguiderMonitor");
			agMon.addAutoguiderMonitorStateListener(monitorStateDisplayHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// SkyPanel
		System.err.println("Startup::Starting skymodel monitor");
		long cadence = config.getLongValue("skypanel.monitor.cadence", 60000L);
		String skyModelHost = config.getProperty("sky.model.host", "occ");
		skyPanel.asynchPredict(skyModelHost, cadence);

		// ag active monitor
		System.err.println("Startup::Starting ag active monitor");
		final AgChecker agChecker = new AgChecker(agHost, 6571);
		final InstrumentStatusPanel fAgStatusPanel = agStatusPanel;
		final InstrumentHealthPanel fihp = instrumentHealthPanel;		
		Runnable r2 = new Runnable() {
			HashMap map = new HashMap();

			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(10000L);
					} catch (InterruptedException ix) {
					}
					double agtemp = 999;
					boolean agtempgot = false;
					boolean agactive = false;
					boolean agfunc = false;
					String agval = "UNKNOWN";
					int agstat = InstrumentStatus.OPERATIONAL_STATUS_UNAVAILABLE;
					InstrumentStatus agstatus = new InstrumentStatus();
					int aghstat = InstrumentData.ONLINE_OKAY;
					try {
						// get the temperature status
						agtemp = agChecker.checkAgTemp();
						// this is kelvins 
						agtempgot = true;
					} catch (Exception e) {
						e.printStackTrace();
					}

					try {
						// get the active status
						agChecker.checkAgActive();
						agactive = true;
					} catch (Exception e) {
						agactive = false;
						e.printStackTrace();
					}

					if (!agtempgot) {
						// we have no temperature but we know its okish
						agstat = InstrumentStatus.OPERATIONAL_STATUS_OKAY;
					} else {
						if (agtemp < 223 || agtemp > 243) {
							agstat = InstrumentStatus.OPERATIONAL_STATUS_FAIL;
							aghstat = InstrumentData.ONLINE_FAIL;
							agval = GET_STATUS_DONE.VALUE_STATUS_FAIL;
						} else if (agtemp < 228 || agtemp > 238) {
							agstat = InstrumentStatus.OPERATIONAL_STATUS_WARN;
							aghstat = InstrumentData.ONLINE_WARN;
							agval = GET_STATUS_DONE.VALUE_STATUS_WARN;
							agfunc = true;
						} else {
							agstat = InstrumentStatus.OPERATIONAL_STATUS_OKAY;
							aghstat = InstrumentData.ONLINE_OKAY;
							agval = GET_STATUS_DONE.VALUE_STATUS_OK;
							agfunc = true;
						}
					}
					if (!agactive) {
						aghstat = InstrumentData.OFFLINE;
					}
					// we now have the active and temperature statuses
					fihp.updateInstrument("AUTOGUIDER", System.currentTimeMillis(), aghstat);

					map.put("temperature", agtemp);
					map.put(GET_STATUS_DONE.KEYWORD_INSTRUMENT_STATUS, agval);
					agstatus.setEnabled(true);
					agstatus.setStatusTimeStamp(System.currentTimeMillis());
					agstatus.setFunctional(agfunc);
					agstatus.setOnline(agactive);
					agstatus.setStatus(map);
					fAgStatusPanel.updateStatus(agstatus);

					try {
						Thread.sleep(50000L);
					} catch (InterruptedException ix) {
					}
				}
			}
		};

		(new Thread(r2)).start();
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

	/** Returns and/or creates a map for the category. */
	private Map getStatusCategoryMap(String cat) {

		Map map = (Map) statusCategoryMap.get(cat);

		if (map == null) {
			map = new HashMap();
			statusCategoryMap.put(cat, map);
		}
		return map;

	}

	/** Creates a connection to the RCS. */
	public IConnection createConnection(String connectionId) {
		return cfy.createConnection(connectionId);
	}

	/** Returns a ref to the ConnFactory. */
	public ConnectionFactory getConnectionFactory() {
		return cfy;
	}

	/** Returns this GUI's window frame. */
	public JFrame getFrame() {
		return frame;
	}

	public void quit() {
		logger.log(1, "GUI", "-", "-", "Stopping and closing all connections...");
		System.exit(0);
	}

	public String getLoginUserName() {
		return loginUserName;
	}

	public String getLocalHost() {
		return localHost;
	}

	/** Displays an updating time label. Wrap in a Thread to start updates. */
	private class TimeLabel extends JLabel implements Runnable {

		TimeLabel() {
			super(sdf.format(new Date(System.currentTimeMillis())));
			setHorizontalTextPosition(SwingConstants.CENTER);
			setBackground(YELLOWSLATE);
			setForeground(Color.blue);
			setOpaque(true);
			setBorder(loweredbevel);
			setFont(BIG_FONT);
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(1000L);
				} catch (InterruptedException ix) {
				}
				setText(sdf.format(new Date(System.currentTimeMillis())));
			}
		}

	} // [TimeLabel]

	private class TabPanel extends JPanel {

		String label;

		TabPanel(String label) {
			super(true);
			this.label = label;

			setLayout(grid1);

		}

	} // [TabPanel]

	private class FieldDescriptor {

		public StateField field;

		public int itemType;

		FieldDescriptor(StateField field, int itemType) {
			this.field = field;
			this.itemType = itemType;
		}

	} // [FieldDescriptor]

	private class StateField extends JPanel {

		JTextField field;

		// JCheckBox check;

		JLabel label;

		StateField(String labelText, int width, String unitText) {
			super(new GridLayout(), true);
			label = new JLabel(labelText);
			label.setHorizontalAlignment(SwingConstants.RIGHT);
			label.setBorder(emptyborder);
			label.setFont(new Font("courier", Font.BOLD, 9));
			field = new JTextField(width);
			field.setFont(new Font("courier", Font.PLAIN, 9));
			// check = new JCheckBox(unitText);
			// check.setHorizontalTextPosition(SwingConstants.LEFT);
			// check.setHorizontalAlignment(SwingConstants.RIGHT);
			// check.setFont(new Font("courier", Font.PLAIN, 9));
			setBorder(raisedbevel);
			setBackground(LIGHTSLATE);
			setOpaque(true);
			add(label);
			add(field);
			// add(check);
		}

		public void setText(String text) {
			field.setText(text);
		}

	}

	public void updateBssLabel(boolean enabled) {
		bssMonLabel.setMonitorEnabled(enabled);
	}

	public void updateIssLabel(boolean enabled) {
		issMonLabel.setMonitorEnabled(enabled);
	}

	private class Handler implements CAMPResponseHandler {

		String cat;

		Handler(String cat) {
			this.cat = cat;
		}

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			// logger.log(1,"GUI", "-","-", "Received update: "+update);
			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

			StatusCategory status = null;

			try {

				if (!(update instanceof ngat.message.GUI_RCS.GET_STATUS_DONE)) {
					logger.log(1, "GUI", "-", "-", "CAMP Error: Unexpected class: " + update);
				}

				// logger.log(1,"GUI", "-","-",
				// "Received: "+update.getClass().getName()+
				// "Success: "+update.getSuccessful()+
				// "ErrNO: "+update.getErrorNum()+
				// "Message: "+update.getErrorString());

				status = ((ngat.message.GUI_RCS.GET_STATUS_DONE) update).getStatus();

				// FUDGE special handling for mechanisms
				
				if (cat.equals("MECHANISM")) {

				    try {
					double azm = status.getStatusEntryDouble("azimuth.position");
					double alt = status.getStatusEntryDouble("altitude.position");
					
					skyPanel.updateSkyb(alt, azm);
				    } catch (Exception e) {
					System.err.println("Unable to update SKYB: "+e);
				    }
				}

				// FUDGE special handling for DUST alerts
				if (cat.equals("X_TNGDUST")) {

				    try {
					double dust = status.getStatusEntryDouble("dust");
					
					if (dust > 45.0) {
					    dustWarningField.setBackground(Color.red);
					    dustWarningField.setForeground(Color.blue);
					    dustWarningField.setEnabled(true);
					    dustWarningField.setText("HI_DUST");
					} else if
					      (dust > 30.0) {
					    dustWarningField.setBackground(Color.orange);
                                            dustWarningField.setForeground(Color.blue);
                                            dustWarningField.setEnabled(true);
					    dustWarningField.setText("DUST");
					} else {
					    dustWarningField.setBackground(Color.green);
                                            dustWarningField.setForeground(Color.blue);
                                            dustWarningField.setEnabled(true);
					    dustWarningField.setText("LO_DUST");
					}

				    } catch (Exception e) {
                                        System.err.println("Unable to update TNGDUST: "+e);
                                    }

				}

				// logger.log(1,"GUI", "-","-", "STATUS: ["+status+"]");

				Map map = (Map) statusCategoryMap.get(cat);

				if (map == null) {
					logger.log(1, "GUI", "-", "-", "No such cat: " + cat);
					return;
				}

				Iterator c = map.keySet().iterator();
				while (c.hasNext()) {
					String key = (String) c.next();
					FieldDescriptor fd = (FieldDescriptor) map.get(key);

					if (fd == null)
						continue;

					int type = fd.itemType;
					StateField tf = fd.field;

					switch (type) {
					case ITEM_TYPE_INTEGER:
						int ii = status.getStatusEntryInt(key);
						tf.setText("" + ii);

						graphFrame.updateGraph(cat, key, ii);

						break;
					case ITEM_TYPE_STATE:
						int is = status.getStatusEntryInt(key);
						tf.setText(TCS_Status.codeString(is));
						break;
					case ITEM_TYPE_DOUBLE:
						double d = status.getStatusEntryDouble(key);
						tf.setText(nf.format(d));

						// Update any graphs found....
						graphFrame.updateGraph(cat, key, d);

						//
						// if (cat.equals("METEO") &&
						// key.equals("wind.direction")) {
						// p.update(d);
						// }
						//

						break;
					case ITEM_TYPE_STRING:
						String s = status.getStatusEntryId(key);
						tf.setText(s);
						break;
					}
				}

			} catch (Exception e) {
				e.printStackTrace();

			}

			// try to relay the thing to LiveData receiver

			StatusInfo sinfo = new StatusInfo(System.currentTimeMillis(), "relay", status);

		}

		@Override
		public void failed(Exception e, IConnection connection) {
			logger.log(1, "GUI", "-", "-", "CAMP Error: " + e);
			e.printStackTrace();
			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

		}

	} // [Handler]

	private class BssHandler implements CAMPResponseHandler {

		BssHandler() {
		}

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			System.err.println("BSSH::Received status update: " + update);
			System.err.println("BSSH::Close connection.");
			if (connection != null)
				connection.close();
			try {

				if (update instanceof COMMAND_DONE) {
					COMMAND_DONE done = update;
					System.err.println("BSSH: Reply: " + done.getId() + " Success: " + done.getSuccessful() + " Err: "
							+ done.getErrorNum() + ", " + done.getErrorString());
				}

				if (!(update instanceof ngat.message.RCS_BSS.GET_STATUS_DONE)) {
					System.err.println("BSSH::CAMP Error: Unexpected class: " + update);
					// set empty values in fields
					return;
				}

				// update fields

				ngat.message.RCS_BSS.GET_STATUS_DONE bssdone = (ngat.message.RCS_BSS.GET_STATUS_DONE) update;

				Hashtable hash = bssdone.getStatusData();

				bssPanel.update(hash);
				bssMonLabel.setMonitorEnabled(true);

			} catch (Exception e) {
				e.printStackTrace();

			}

		}

		@Override
		public void failed(Exception e, IConnection connection) {
			System.err.println("BSSH::CAMP Error: " + e);
			e.printStackTrace();
			System.err.println("BSSH::Close connection.");
			if (connection != null)
				connection.close();

			// set empty values
			bssPanel.updateNoData();
			bssMonLabel.setMonitorEnabled(false);
		}

	}

	private class OssHandler implements CAMPResponseHandler {

		OssHandler() {
		}

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			System.err.println("OSSH::Received status update: " + update);
			System.err.println("OSSH::Close connection.");
			if (connection != null)
				connection.close();

			try {

				if (update instanceof COMMAND_DONE) {
					COMMAND_DONE done = update;
					System.err.println("OSSH: Reply: " + done.getId() + " Success: " + done.getSuccessful() + " Err: "
							+ done.getErrorNum() + ", " + done.getErrorString());
				}

				if (!(update instanceof ngat.message.GUI_RCS.GET_STATUS_DONE)) {
					System.err.println("OSSH::CAMP Error: Unexpected class: " + update);
					ossSchedStatusField.setText("U/X CLASS");
					ossSchedStatusField.setBackground(Color.yellow);
					ossSchedStatusField.setForeground(LIGHTSLATE);

					ossBaseStatusField.setText("U/X CLASS");
					ossBaseStatusField.setBackground(Color.yellow);
					ossBaseStatusField.setForeground(LIGHTSLATE);

					ossSynStatusField.setText("U/X CLASS");
					ossSynStatusField.setBackground(Color.yellow);
					ossSynStatusField.setForeground(LIGHTSLATE);

					return;
				}

				StatusCategory status = ((ngat.message.GUI_RCS.GET_STATUS_DONE) update).getStatus();

				System.err.println("OSSH::Received class: " + status.getClass().getName());

				String schon = status.getStatusEntryId("scheduler.online");
				String bason = status.getStatusEntryId("base.models.online");
				String synon = status.getStatusEntryId("synoptic.models.online");

				if ("ONLINE".equals(schon)) {
					ossSchedStatusField.setText("ONLINE");
					ossSchedStatusField.setBackground(Color.green);
					ossSchedStatusField.setForeground(Color.blue);
				} else if ("OFFLINE".equals(schon)) {
					ossSchedStatusField.setText("OFFLINE");
					ossSchedStatusField.setBackground(Color.red);
					ossSchedStatusField.setForeground(Color.blue);
				} else {
					ossSchedStatusField.setText("UNKNOWN");
					ossSchedStatusField.setBackground(Color.yellow);
					ossSchedStatusField.setForeground(Color.blue);
				}

				if ("ONLINE".equals(bason)) {
					ossBaseStatusField.setText("ONLINE");
					ossBaseStatusField.setBackground(Color.green);
					ossBaseStatusField.setForeground(Color.blue);
				} else if ("OFFLINE".equals(bason)) {
					ossBaseStatusField.setText("OFFLINE");
					ossBaseStatusField.setBackground(Color.red);
					ossBaseStatusField.setForeground(Color.blue);
				} else {
					ossBaseStatusField.setText("UNKNOWN");
					ossBaseStatusField.setBackground(Color.yellow);
					ossBaseStatusField.setForeground(Color.blue);
				}

				if ("ONLINE".equals(synon)) {
					ossSynStatusField.setText("ONLINE");
					ossSynStatusField.setBackground(Color.green);
					ossSynStatusField.setForeground(Color.blue);
				} else if ("OFFLINE".equals(synon)) {
					ossSynStatusField.setText("OFFLINE");
					ossSynStatusField.setBackground(Color.red);
					ossSynStatusField.setForeground(Color.blue);
				} else {
					ossSynStatusField.setText("UNKNOWN");
					ossSynStatusField.setBackground(Color.yellow);
					ossSynStatusField.setForeground(Color.blue);
				}

			} catch (Exception e) {
				e.printStackTrace();

			}
		}

		@Override
		public void failed(Exception e, IConnection connection) {
			System.err.println("OSSH::CAMP Error: " + e);
			e.printStackTrace();
			System.err.println("OSSH::Close connection.");
			if (connection != null)
				connection.close();

			ossSchedStatusField.setText("UNKNOWN");
			ossSchedStatusField.setBackground(Color.yellow);
			ossSchedStatusField.setForeground(LIGHTSLATE);

			ossBaseStatusField.setText("UNKNOWN");
			ossBaseStatusField.setBackground(Color.yellow);
			ossBaseStatusField.setForeground(LIGHTSLATE);

			ossSynStatusField.setText("UNKNOWN");
			ossSynStatusField.setBackground(Color.yellow);
			ossSynStatusField.setForeground(LIGHTSLATE);

		}

	} // [OssHandler]

	private class SMHandler implements CAMPResponseHandler {

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

			// logger.log(1,"GUI", "-","-", "Received update: "+update);

			if (!(update instanceof GET_STATE_MODEL_DONE)) {
				logger.log(1, "GUI", "-", "-", "CAMP Error: Unexpected class: " + update);
			}

			// logger.log(1,"GUI", "-","-",
			// "Received: "+update.getClass().getName()+
			// "Success: "+update.getSuccessful()+
			// "ErrNO: "+update.getErrorNum()+
			// "Message: "+update.getErrorString());

			HashMap map = ((GET_STATE_MODEL_DONE) update).getVariables();

			int cs = ((GET_STATE_MODEL_DONE) update).getCurrentState();
			int cop = ((GET_STATE_MODEL_DONE) update).getCurrentOperation();

			updateNewStateField(cs);
			updateNewOpField(cop);

			updateNewVariableFields(map);

			statePanel.validate();
			frame.repaint();

		}

		@Override
		public void failed(Exception e, IConnection connection) {
			logger.log(1, "GUI", "-", "-", "CAMP Error: " + e);
			e.printStackTrace();

			clearNewStateField();
			clearNewOpField();
			clearNewVariableFields();

			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

		}

	} // [SMHandler]

	private class GVHandler implements CAMPResponseHandler {

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			System.err.println("GV::Received RCS version update: " + update);
			System.err.println("GV::Close connection.");
			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

			if (!(update instanceof GET_VERSION_DONE)) {
				logger.log(1, "GUI", "-", "-", "*** GETV::CAMP Error: Unexpected class: " + update);
			}

			GET_VERSION_DONE gvd = (GET_VERSION_DONE) update;

			rcsVersion.majorVersion = gvd.getMajorVersion();
			rcsVersion.minorVersion = gvd.getMinorVersion();
			rcsVersion.patchVersion = gvd.getPatchVersion();
			rcsVersion.releaseName = gvd.getReleaseName();
			rcsVersion.buildNumber = gvd.getBuildNumber();
			rcsVersion.buildDate = gvd.getBuildDate();

			helpMenu.setForeground(Color.cyan);

		}

		@Override
		public void failed(Exception e, IConnection connection) {
			logger.log(1, "GUI", "-", "-", "*** GETV:: CAMP Error: " + e);
			e.printStackTrace();

			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

		}

	} // GV Handler

	private void clearNewStateField() {
		picStatusIndicator.setText("NO COMMS");
		picStatusIndicator.setBackground(LIGHTSLATE);
	}

	private void clearNewOpField() {
		currentNewOpField.setText("NO COMMS");
		currentNewOpField.setBackground(LIGHTSLATE);
	}

	private void clearNewVariableFields() {
		clearNewVariableField(systemVariableField);
		clearNewVariableField(controlVariableField);
		clearNewVariableField(networkVariableField);
		clearNewVariableField(weatherVariableField);
		clearNewVariableField(enclosureVariableField);
		clearNewVariableField(mirrcoverVariableField);
		clearNewVariableField(axesVariableField);
		clearNewVariableField(periodVariableField);
		clearNewVariableField(intentVariableField);
	}

	private void clearNewVariableField(JTextField field) {
		field.setBackground(LIGHTSLATE);
		field.setForeground(Color.blue);
		field.setText("UNKNOWN");
	}

	private void updateNewStateField(int cs) {

		String sl = "UNKNOWN";
		Color scol = Color.gray;

		switch (cs) {
		case 1:
			sl = "ENGINEERING";
			break;
		case 2:
			sl = "STANDBY";
			break;
		case 3:
			sl = "STARTING";
			break;
		case 4:
			sl = "OPENING";
			break;
		case 5:
			sl = "OPERATIONAL";
			break;
		case 6:
			sl = "CLOSING";
			break;
		case 7:
			sl = "STOPPING";
			break;
		case 8:
			sl = "SHUTDOWN";
			break;
		}

		picStatusIndicator.setText(sl);
		picStatusIndicator.setBackground(Color.orange);

		colorStateIndicator.updateState(cs);

	}

	private void updateNewOpField(int cs) {

		String sl = "UNKNOWN";
		Color scol = Color.gray;

		switch (cs) {
		case 1:
			sl = "IDLE";
			scol = Color.orange;
			break;
		case 2:
			sl = "INITIALIZING";
			scol = Color.yellow;
			break;
		case 3:
			sl = "FINALIZING";
			scol = Color.yellow;
			break;
		case 4:
			sl = "OBSERVING";
			scol = Color.green;
			break;
		case 5:
			sl = "MODE_SWITCH";
			scol = Color.cyan.darker();
			break;
		}

		currentNewOpField.setText(sl);
		currentNewOpField.setBackground(scol);

	}

	private void updateNewVariableFields(Map map) {

		String sl = "UNKNOWN";
		Color scol = Color.gray;
		Color fcol = Color.blue;

		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {

			String var = (String) it.next();
			JTextField field = null;
			Integer vv = (Integer) map.get(var);
			if (vv == null)
				continue;
			int cs = vv.intValue();
			switch (cs) {
			case 1:
				sl = "SUSPEND";
				scol = Color.red;
				field = systemVariableField;
				break;
			case 2:
				sl = "STANDBY";
				scol = Color.orange;
				field = systemVariableField;
				break;
			case 3:
				sl = "OKAY";
				scol = Color.green;
				field = systemVariableField;
				break;
			case 4:
				sl = "FAIL";
				scol = Color.red;
				field = systemVariableField;
				break;
			case 5:
				sl = "ENABLED";
				scol = Color.green;
				field = controlVariableField;
				break;
			case 6:
				sl = "DISABLED";
				scol = Color.red;
				field = controlVariableField;
				break;
			case 7:
				sl = "CONNECTED";
				scol = Color.green;
				field = networkVariableField;
				break;
			case 8:
				sl = "OFFLINE";
				scol = Color.red;
				field = networkVariableField;
				break;
			case 9:
				sl = "ALERT";
				scol = Color.red;
				field = weatherVariableField;
				break;
			case 10:
				sl = "CLEAR";
				scol = Color.green;
				field = weatherVariableField;
				break;
			case 11:
				sl = "OPEN";
				scol = Color.green;
				field = enclosureVariableField;
				break;
			case 12:
				sl = "CLOSED";
				scol = Color.red;
				field = enclosureVariableField;
				break;
			case 13:
				sl = "ERROR";
				scol = Color.red;
				field = enclosureVariableField;
				break;
			case 14:
				sl = "OKAY";
				scol = Color.green;
				field = axesVariableField;
				break;
			case 15:
				sl = "ERROR";
				scol = Color.red;
				field = axesVariableField;
				break;
			case 16:
				sl = "AUTOMATIC";
				scol = Color.green;
				field = intentVariableField;
				toggleModeButton.setText("ENG");
				toggleModeAction.setEngineering(true);
				toggleModeButton.setBackground(Color.red);
				toggleModeButton.setForeground(Color.yellow);
				// System.err.println("Set toggle to ENG as received curr AUTO");
				break;
			case 17:
				sl = "MANUAL";
				scol = Color.orange;
				field = intentVariableField;
				toggleModeButton.setText("OPER");
				toggleModeAction.setEngineering(false);
				toggleModeButton.setBackground(Color.orange);
				toggleModeButton.setForeground(Color.blue);
				// System.err.println("Set toggle to AUTO as received curr ENG");
				break;
			case 18:
				sl = "DAY_TIME";
				scol = Color.orange;
				fcol = Color.blue;
				field = periodVariableField;
				break;
			case 19:
				sl = "NIGHT_TIME";
				scol = Color.blue;
				fcol = Color.orange;
				field = periodVariableField;
				break;
			case 25:
				sl = "OPEN";
				scol = Color.green;
				field = mirrcoverVariableField;
				break;
			case 26:
				sl = "CLOSED";
				scol = Color.red;
				field = mirrcoverVariableField;
				break;
			case 27:
				sl = "ERROR";
				scol = Color.red;
				field = mirrcoverVariableField;
				break;
			}

			colorStateIndicator.updateSysvar(cs);

			if (field != null) {
				field.setText(sl);
				field.setBackground(scol);
				field.setForeground(fcol);
			}

		}

	}

	private class IdHandler implements CAMPResponseHandler {

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

			// logger.log(1,"GUI", "-","-", "Received update: "+update);

			if (!(update instanceof ID_DONE)) {
				logger.log(1, "GUI", "-", "-", "ID: CAMP Error: Unexpected class: " + update);
				return;
			}

			ID_DONE idd = (ID_DONE) update;
			// #### TEMP - needs agentDesc field in ID_DONE
			String aid = idd.getAgentInControl();
			String adesc = "";
			if (aid != null) {
				if (aid.equals("PCA"))
					adesc = "Planetarium/RTI";
				else if (aid.equals("BGCA"))
					adesc = "Background";
				else if (aid.equals("TOCA"))
					adesc = "Target of Opp";
				else if (aid.equals("XCA"))
					adesc = "Experimental";
				else if (aid.equals("CAL"))
					adesc = "Calibration";
				else if (aid.equals("SOCA"))
					adesc = "Scheduled";
				else
					adesc = "Unknown";
			}

			rcsAicNameField.setText(adesc);

			colorStateIndicator.updateMode(aid);

			// rcsAicIdField.setText(aid != null ? aid : "N/A");
			// // rcsAicActivityField.setText(idd.getAgentActivity());

			// String currTask = idd.getAgentActivity();
			// rcsAicTaskField.setText((currTask != null ? currTask : "N/A"));

			// rcsTransientField.setText(idd.getTransient());

			switch (idd.getControl()) {
			case ID.WATCHDOG_PROCESS:
				picIndicator.setText("WATCHDOG");

				if (idd.getLastStatus() > 605000) {
					picStatusIndicator.setText("ERROR:" + idd.getLastStatus());
				} else
					picStatusIndicator.setText("HOLDING");

				startEnabled = true;
				// stopStartButton.setText("START");

				// Control buttons.
				((JMenuItem) menuMap.get("start-rcs-eng")).setEnabled(true);
				((JMenuItem) menuMap.get("start-rcs-auto")).setEnabled(true);
				((JMenuItem) menuMap.get("restart-rcs-eng")).setEnabled(false);
				((JMenuItem) menuMap.get("restart-rcs-auto")).setEnabled(false);
				((JMenuItem) menuMap.get("halt-rcs")).setEnabled(false);
				((JMenuItem) menuMap.get("reboot-occ")).setEnabled(true);
				((JMenuItem) menuMap.get("shutdown-occ")).setEnabled(true);

				// Toggle state.
				toggleModeButton.setText("N/A");
				toggleModeAction.setEnabled(false);

				// Weather override.
				// weatherOverrideButton.setEnabled(false);

				break;
			case ID.RCS_PROCESS:
				picIndicator.setText("RCS");
				if (idd.getOperational()) {
					// picStatusIndicator.setText("OPERATIONAL");
					// toggleModeButton.setText("ENG");
					// toggleModeAction.setEngineering(true);
				} else if (idd.getEngineering()) {
					// picStatusIndicator.setText("ENGINEERING");
					// toggleModeButton.setText("OPER");
					// toggleModeAction.setEngineering(false);
					// logger.log(1,"GUI", "-","-", "******************Setting
					// toggle text
					// to OP");
				} else {
					// picStatusIndicator.setText("STANDBY");
					// toggleModeButton.setText("ENG");
					// toggleModeAction.setEngineering(true);
				}

				startEnabled = false;
				// stopStartButton.setText("STOP");

				// Control buttons.
				((JMenuItem) menuMap.get("start-rcs-eng")).setEnabled(false);
				((JMenuItem) menuMap.get("start-rcs-auto")).setEnabled(false);
				((JMenuItem) menuMap.get("restart-rcs-eng")).setEnabled(true);
				((JMenuItem) menuMap.get("restart-rcs-auto")).setEnabled(true);
				((JMenuItem) menuMap.get("halt-rcs")).setEnabled(true);
				((JMenuItem) menuMap.get("reboot-occ")).setEnabled(true);
				((JMenuItem) menuMap.get("shutdown-occ")).setEnabled(true);

				// Toggle state.
				toggleModeAction.setEnabled(true);
				toggleModeButton.setEnabled(true);

				// Weather override.
				// weatherOverrideButton.setEnabled(true);

				break;
			default:
				picIndicator.setText("NO COMMS");

				((JMenuItem) menuMap.get("start-rcs-eng")).setEnabled(false);
				((JMenuItem) menuMap.get("start-rcs-auto")).setEnabled(false);
				((JMenuItem) menuMap.get("restart-rcs-eng")).setEnabled(false);
				((JMenuItem) menuMap.get("restart-rcs-auto")).setEnabled(false);
				((JMenuItem) menuMap.get("halt-rcs")).setEnabled(false);
				((JMenuItem) menuMap.get("reboot-occ")).setEnabled(false);
				((JMenuItem) menuMap.get("shutdown-occ")).setEnabled(false);

			}

			if (restricted)
				toggleModeButton.setEnabled(false);

			frame.pack();
			frame.repaint();

		}

		@Override
		public void failed(Exception e, IConnection connection) {
			logger.log(1, "GUI", "-", "-", "ID: CAMP Error: " + e);
			e.printStackTrace();
			picIndicator.setText("NO COMMS");
			picStatusIndicator.setText("NO COMMS");
			rcsAicNameField.setText("NO COMMS");
			logger.log(1, "GUI", "-", "-", "Close connection.");
			connection.close();

		}

	} // [IdHandler]

	private class ConnFactory implements ConnectionFactory {

		@Override
		public IConnection createConnection(String connectionId) {
			if (connectionId.equals("BSS"))
				return new SocketConnection(bssHost, bssPort);
			else
				return new SocketConnection(rcsHost, rcsCmdPort);
		}

	} // [ConnFactory}

	private class WindowControl extends WindowAdapter {

		@Override
		public void windowClosing(WindowEvent wev) {

			quit();

		}

	}

	private class StopStartListener implements ActionListener, CAMPResponseHandler {

		@Override
		public void actionPerformed(ActionEvent ae) {

			if (startEnabled) {

				START start = new START("gui");
				start.setEngineering(true);

				IConnection con = cfy.createConnection("RCS_CMD");
				try {
					con.open();
				} catch (ConnectException cx) {
					failed(cx, con);
					return;
				}

				try {
					con.send(start);
				} catch (IOException iox) {
					failed(iox, con);
					return;
				}

				try {
					Object obj = con.receive(20000L);
					// logger.log(1,"GUI", "-","-", "Object recvd: "+obj);
					COMMAND_DONE update = (COMMAND_DONE) obj;
					handleUpdate(update, con);
				} catch (ClassCastException cx) {
					failed(cx, con);
					return;
				} catch (IOException iox) {
					failed(iox, con);
					return;
				}

			}

		}

		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			// logger.log(1,"GUI", "-","-", "START: Close connection.");
			if (connection != null)
				connection.close();

			// logger.log(1,"GUI", "-","-", "START: Received update: "+update);

			if (!(update instanceof START_DONE)) {
				logger.log(1, "GUI", "-", "-", "START: CAMP Error: Unexpected class: " + update);
				return;
			}

			START_DONE sd = (START_DONE) update;

			// JOptionPane.showMessageDialog(frame,
			// "<html>"+
			// "<p>Error: "+sd.getErrorNum()+
			// "<p>Message: "+sd.getErrorString(),
			// "Start RCS", JOptionPane.INFORMATION_MESSAGE);

		}

		@Override
		public void failed(Exception e, IConnection connection) {
			// connection.close();
			logger.log(1, "GUI", "-", "-", "START: CAMP Error: " + e);
			e.printStackTrace();
			logger.log(1, "GUI", "-", "-", "Close connection.");
			if (connection != null)
				connection.close();

		}

	} // [StopStartListener]

	private class ToolBarListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			String cmd = ae.getActionCommand();
			if (cmd.equals("fits")) {
				// if (fitsControlVisible)
				// return;
				// displayFitsControl();
				JOptionPane.showMessageDialog(frame, "FITS Header Control Panel is always available",
						"Future Enhancements", JOptionPane.INFORMATION_MESSAGE);
			} else if (cmd.equals("logs")) {
				// if (logViewerVisible)
				// return;
				// displayLogViewer();
				JOptionPane.showMessageDialog(frame, "Log Viewer Panel is always available", "Future Enhancements",
						JOptionPane.INFORMATION_MESSAGE);
			} else if (cmd.equals("tocs")) {

			} else if (cmd.equals("calib")) {

			} else if (cmd.equals("sky")) {

			} else if (cmd.equals("sched")) {

			}

		}

	}

	/**
	 * @author eng
	 * 
	 */
	public class SchedBtnListener implements ActionListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent
		 * )
		 */
		// @Override
		@Override
		public void actionPerformed(ActionEvent ae) {
			TaskModeControllerManagement tmc = null;
			try {
				tmc = (TaskModeControllerManagement) Naming.lookup("rmi://" + rcsHost + "/SOCAModeController");
			} catch (Exception e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Unable to contact SOCA controller",
						"SOCA Offline: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
				return;
			}
			if (schedEnabled) {
				int confirm = JOptionPane.showConfirmDialog(null, "Do you want to disable the scheduler",
						"Scheduler disable", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.NO_OPTION)
					return;
				try {
					tmc.disable();
					schedEnabled = false;
					schedBtn.setText("Enable SOCA");
					socaStatusLabel.setMonitorEnabled(false);
					JOptionPane.showMessageDialog(null, "SOCA controller is disabled", "SOCA Disabled",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to disable SOCA controller",
							"SOCA Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
				}
			} else {
				int confirm = JOptionPane.showConfirmDialog(null, "Do you want to re-enable the scheduler",
						"Scheduler re-enable", JOptionPane.YES_NO_OPTION);
				if (confirm == JOptionPane.NO_OPTION)
					return;
				try {
					tmc.enable();
					schedEnabled = true;
					schedBtn.setText("Disable SOCA");
					socaStatusLabel.setMonitorEnabled(true);
					JOptionPane.showMessageDialog(null, "SOCA controller is re-enabled", "SOCA re-enabled",
							JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, "Unable to re-enable SOCA controller",
							"SOCA Error: " + e.getMessage(), JOptionPane.ERROR_MESSAGE);
				}
			}

		}

	}

	private class IregLookup extends Thread {

		String iregUrl;
		InstrumentStatusHandler handler;

		IregLookup(String iregUrl, InstrumentStatusHandler handler) {
			this.iregUrl = iregUrl;
			this.handler = handler;
		}

		@Override
		public void run() {
			while (true) {
				try {
					System.err.println("IregLookup: searching for: " + iregUrl);
					InstrumentRegistry ireg = (InstrumentRegistry) Naming.lookup(iregUrl);
					System.err.println("Found instrument registry: " + ireg);
					List instlist = ireg.listInstruments();
					Iterator ii = instlist.iterator();
					while (ii.hasNext()) {
						InstrumentDescriptor id = (InstrumentDescriptor) ii.next();
						InstrumentStatusProvider isp = ireg.getStatusProvider(id);
						System.err.println("Found isp for: " + id.getInstrumentName() + " : " + isp);
						isp.addInstrumentStatusUpdateListener(insthandler);
						System.err.println("Re-registered with isp for: " + id);
					}
				} catch (Exception e) {
					System.err.println("IregLookup: " + e);
				}
				try {
					Thread.sleep(120000L);
				} catch (InterruptedException ix) {
				}
			}

		}
	}

} // [RcsGUI]

