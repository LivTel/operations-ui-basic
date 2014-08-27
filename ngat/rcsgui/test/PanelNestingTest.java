/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Shape;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import javax.swing.border.TitledBorder;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
//import ngat.tcm.PrimaryAxisState;
import ngat.tcm.Telescope;
import ngat.tcm.TelescopeStatusProvider;
import org.jfree.data.time.TimeSeries;

/**
 * @author eng
 * 
 */
public class PanelNestingTest extends JFrame {

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

	static Font TFONT = new Font("serif", Font.PLAIN, 12);

	static Font STATUS_FONT = new Font("tahoma", Font.PLAIN, 12);
	static Font STATUS_LABEL_FONT = new Font("palatino", Font.BOLD, 12);
	public static final Color SLATE = new Color(102, 153, 153);

	public static final Color LIGHTSLATE = new Color(153, 203, 203);

	public static final Color YELLOWSLATE = new Color(204, 204, 51);

	public static final Color PINKSLATE = new Color(204, 151, 151);

	public static final Color ORANGESLATE = new Color(255, 165, 0);

	public static final Color OLIVE = new Color(153, 204, 153);
	
	/** Map cat.key to field.*/
	public Map<String, StateField> fieldMap;
	
	/**Map cat/key to graph.*/
	public Map<String, List<TimeSeries>> graphMap;
	
	/**
	 * @author eng
	 * 
	 */
	public class APanel extends JPanel {

		public APanel(boolean db) {
			super(db);
			setPreferredSize(new Dimension(700, 300));
		}

	}

	
	
	public static void main(String args[]) {

		PanelNestingTest t = new PanelNestingTest();
		
		try {
			PanelNestingTestUpdater pnu = new PanelNestingTestUpdater(t);
		
			Telescope scope = (Telescope)Naming.lookup("rmi://ltsim1/Telescope");
			
			TelescopeStatusProvider tsp = scope.getTelescopeStatusProvider();
			
			tsp.addTelescopeStatusUpdateListener(pnu);
			
			InstrumentRegistry ireg = (InstrumentRegistry)Naming.lookup("rmi://ltsim1/InstrumentRegistry");
			List instruments = ireg.listInstruments();
			Iterator ii = instruments.iterator();
			while (ii.hasNext()) {
				InstrumentDescriptor id = (InstrumentDescriptor)ii.next();
				ireg.getStatusProvider(id).addInstrumentStatusUpdateListener(pnu);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PanelNestingTest() {
		super("Panel Nesting Test");
		
		graphMap = new HashMap<String, List<TimeSeries>>();
		fieldMap = new HashMap<String, StateField>();
		
		getContentPane().add(createContent());
		pack();
		setVisible(true);
		
	}

	private JPanel createContent() {
	
		JPanel infoPanel = new JPanel(true);
		infoPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));

		JTabbedPane infoTabPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

		// Mech
		JTabbedPane mechPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		mechPanel.setBorder(BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.LOWERED, Color.pink,
				Color.magenta), "Primary Mechanisms", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, TFONT,
				Color.blue));

		// summary and graphs
		JPanel sumPanel = new APanel(true);
		addlabels(sumPanel, "Mech");
		mechPanel.addTab("Summary", sumPanel);

		JTabbedPane graphPane = makeMechGraphPane();
		mechPanel.addTab("Graphs", graphPane);

		
		// Meteo
		JTabbedPane meteoPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		meteoPanel.setBorder(BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.LOWERED, Color.pink,
				Color.magenta), "Meteorological information", TitledBorder.RIGHT, TitledBorder.DEFAULT_POSITION, TFONT,
				Color.blue));

		// summary and graphs
		JPanel sum2Panel = new APanel(true);
		addlabels(sum2Panel, "Meteo");
		meteoPanel.addTab("Summary", sum2Panel);

		JTabbedPane graphPane2 = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);

		graphPane2.addTab("Humidity", new APanel(true));
		graphPane2.addTab("Temperature", new APanel(true));
		graphPane2.addTab("Wind speed", new APanel(true));
		meteoPanel.addTab("Graphs", graphPane2);

		// Secondos
		JTabbedPane secPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		secPanel.setBorder(BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.LOWERED, Color.pink,
				Color.magenta), "Secondary Mechanisms", TitledBorder.RIGHT, TitledBorder.DEFAULT_POSITION, TFONT,
				Color.blue));

		// summary and graphs
		JPanel sum3Panel = new APanel(true);
		addlabels(sum3Panel, "Sec");
		secPanel.addTab("Summary", sum3Panel);
			
		JTabbedPane graphPane3 = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
		graphPane3.addTab("Focus", new APanel(true));
		graphPane3.addTab("Ag focus", new APanel(true));
		graphPane3.addTab("Enclosure", new APanel(true));
		secPanel.addTab("Graphs", graphPane3);

		// Instruments
		JTabbedPane instPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
				
		// ratcam
		JTabbedPane ratPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		// summary and graphs
		JPanel sumratPanel = new APanel(true);
		addlabels(sumratPanel, "Ratcam");
		ratPanel.addTab("Summary", sumratPanel);
			
		JTabbedPane graphPanerat = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
		graphPanerat.addTab("Temperature", new APanel(true));
		graphPanerat.addTab("Heater ADU", new APanel(true));	
		ratPanel.addTab("Graphs", graphPanerat);
		
		// supircam
		JTabbedPane supPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		// summary and graphs
		JPanel sumsupPanel = new APanel(true);
		addlabels(sumsupPanel, "Supircam");
		supPanel.addTab("Summary", sumsupPanel);
			
		JTabbedPane graphPanesup = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
		graphPanesup.addTab("Temperature", new APanel(true));
		graphPanesup.addTab("Heater ADU", new APanel(true));	
		supPanel.addTab("Graphs", graphPanesup);
		
		// frodo
		JTabbedPane froPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		// summary and graphs
		JPanel sumfroPanel = new APanel(true);
		addlabels(sumfroPanel, "FRODO");
		froPanel.addTab("Summary", sumfroPanel);
			
		JTabbedPane graphPanefro = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
		graphPanefro.addTab("Temperature", new APanel(true));
		graphPanefro.addTab("Heater ADU", new APanel(true));	
		froPanel.addTab("Graphs", graphPanefro);
		
		instPanel.addTab("RATCAM", ratPanel);
		instPanel.addTab("SUPIRCAM", supPanel);
		instPanel.addTab("FRODO", froPanel);
						
		// Favorites
		JTabbedPane favPanel = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);
		favPanel.setBorder(BorderFactory.createTitledBorder(new BevelBorder(BevelBorder.LOWERED, Color.pink,
				Color.magenta), "Standard Telemetry", TitledBorder.RIGHT, TitledBorder.DEFAULT_POSITION, TFONT,
				Color.blue));
		
		// summary x2, graphs, thumbs
		JPanel sum4Panel = new APanel(true);
		addlabels(sum4Panel, "Telem-1");	
		JPanel sum4aPanel = new APanel(true);
		addlabels(sum4aPanel, "Telem-2");
		
		favPanel.addTab("Telem. 1", sum4Panel);
		favPanel.addTab("Telem. 2", sum4aPanel);
		favPanel.setBackground(YELLOWSLATE);

		JTabbedPane graphPane4 = makeFavGraphPane();
		favPanel.addTab("Graphs", graphPane4);
		
		// thumbs
		JPanel thumbPanel = makeFavThumbPane();				
		favPanel.addTab("Thumbs", thumbPanel);
		
		infoTabPanel.addTab("Mech", mechPanel);
		infoTabPanel.addTab("Meteo", meteoPanel);
		infoTabPanel.addTab("Secondary", secPanel);
		infoTabPanel.addTab("Instrument", instPanel);
		infoTabPanel.addTab("Favourites", favPanel);

		infoTabPanel.setBackgroundAt(4, Color.red);
		infoTabPanel.setForegroundAt(4, Color.yellow);

		infoPanel.add(infoTabPanel);
		return infoPanel;
	}
	
	private JTabbedPane makeMechGraphPane() {
		
		JTabbedPane graphPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
	
		GraphPanel g = makeGraph("Azimuth", -180.0, 360.0);
		createSeries(g, "AZM", "current", "Position", Color.cyan, true, null);
		createSeries(g, "AZM", "demand", "Demand", Color.yellow, true, null);
		graphPane.addTab("Azimuth",  g.getChartPanel());

		g = makeGraph("Altitude", 0.0, 90.0);
		createSeries(g, "ALT", "current", "Position", Color.cyan, true, null);
		createSeries(g, "ALT", "demand", "Demand", Color.red, true, null);
		graphPane.addTab("Altitude",  g.getChartPanel());

		g = makeGraph("Rotator", -90.0, 90.0);
		createSeries(g, "ROT", "current", "Position", Color.cyan, true, null);
		createSeries(g, "ROT", "demand", "Demand", Color.red, true, null);
		graphPane.addTab("Rotator",  g.getChartPanel());

		g = makeGraph("Sky PA", 0.0, 360.0);
		createSeries(g, "ROT", "skypa", "Sky PA", Color.cyan, true, null);
		graphPane.addTab("Sky PA",  g.getChartPanel());
		
		return graphPane;
	}	
	
	private JTabbedPane makeFavGraphPane() {
		
		JTabbedPane graphPane = new JTabbedPane(SwingConstants.LEFT, JTabbedPane.WRAP_TAB_LAYOUT);
	
		GraphPanel g = makeGraph("Azimuth", -180.0, 360.0);
		createSeries(g, "AZM", "current", "Position", Color.cyan, true, null);
		createSeries(g, "AZM", "demand", "Demand", Color.red, true, null);
		graphPane.addTab("Azimuth",  g.getChartPanel());
		
		g = makeGraph("Altitude", 0.0, 90.0);
		createSeries(g, "ALT", "current", "Position", Color.cyan, true, null);
		createSeries(g, "ALT", "demand", "Demand", Color.red, true, null);
		graphPane.addTab("Altitude",  g.getChartPanel());

		g = makeGraph("Rotator", -90.0, 90.0);
		createSeries(g, "ROT", "current", "Position", Color.cyan, true, null);
		createSeries(g, "ROT", "demand", "Demand", Color.red, true, null);
		graphPane.addTab("Rotator",  g.getChartPanel());

		g = makeGraph("Sky PA", 0.0, 360.0);
		createSeries(g, "ROT", "skypa", "Sky PA", Color.cyan, true, null);
		graphPane.addTab("Sky PA",  g.getChartPanel());
		
		g = makeGraph("Inst. Temp", -200.0, 40.0);
		createSeries(g, "RATCAM",      "temperature", "RATCAM",      Color.cyan, true, null);
		createSeries(g, "SUPIRCAM",    "temperature", "SUPIRCAM",    Color.cyan, true, null);
		createSeries(g, "RINGO2",      "temperature", "RINGO2",      Color.cyan, true, null);
		createSeries(g, "RISE",        "temperature", "RISE",        Color.cyan, true, null);
		createSeries(g, "FRODO_RED",   "temperature", "FRODO_RED",   Color.cyan, true, null);
		createSeries(g, "FRODO_BLUE",  "temperature", "FRODO_BLUE",  Color.cyan, true, null);
		createSeries(g, "IO_INFRARED", "temperature", "IO_INFRARED", Color.cyan, true, null);
		createSeries(g, "IO_OPTICAL",  "temperature", "IO_OPTICAL",  Color.cyan, true, null);
		graphPane.addTab("Inst. Temp",  g.getChartPanel());
		
		g = makeGraph("Humidity", 0.0, 100.0);
		createSeries(g, "METEO", "humidity", "Humidity", Color.cyan, true, null);
		graphPane.addTab("Humidity",  g.getChartPanel());

		g = makeGraph("Temperature", -50.0, 50.0);
		createSeries(g, "METEO", "temperature", "Temperature", Color.cyan, true, null);
		graphPane.addTab("Temperature",  g.getChartPanel());
		
		g = makeGraph("Cloud", -50.0, 50.0);
		createSeries(g, "METEO", "cloud.amb.other", "Cloud", Color.cyan, true, null);
		graphPane.addTab("Cloud",  g.getChartPanel());
		
		g = makeGraph("Wind Speed", 0.0, 50.0);
		createSeries(g, "METEO", "wind.speed", "Wind speed", Color.cyan, true, null);
		graphPane.addTab("Wind speed",  g.getChartPanel());
		
		g = makeGraph("Truss Temp", -20.0, 50.0);
		createSeries(g, "ENV", "truss.temp", "Truss temp", Color.cyan, true, null);
		graphPane.addTab("Truss temp",  g.getChartPanel());
				
		g = makeGraph("AG Focus", -20.0, 50.0);
		createSeries(g, "AGFOCUS", "current", "Position", Color.cyan, true, null);
		createSeries(g, "AGFOCUS", "demand", "Demand", Color.yellow, true, null);
		graphPane.addTab("AG Focus",  g.getChartPanel());
		
		g = makeGraph("Focus", -20.0, 50.0);
		createSeries(g, "FOCUS", "current", "Position", Color.cyan, true, null);
		createSeries(g, "FOCUS", "demand", "Demand", Color.yellow, true, null);
		graphPane.addTab("Focus",  g.getChartPanel());
		
		return graphPane;
	}	
	

	private JPanel makeFavThumbPane() {
		
		JPanel graphPane = new JPanel(true);
		graphPane.setLayout(new GridLayout(3,4, 5, 5));
		
		GraphPanel g = makeThumbGraph("Azimuth", -180.0, 360.0);
		createSeries(g, "AZM", "current", "Position", Color.cyan, true, null);
		createSeries(g, "AZM", "demand", "Demand", Color.yellow, true, null);
		graphPane.add(g.getChartPanel());
		
		g = makeThumbGraph("Altitude", 0.0, 90.0);
		createSeries(g, "ALT", "current", "Position", Color.cyan, true, null);
		createSeries(g, "ALT", "demand", "Demand", Color.red, true, null);
		graphPane.add(g.getChartPanel());

		g = makeThumbGraph("Rotator", -90.0, 90.0);
		createSeries(g, "ROT", "current", "Position", Color.cyan, true, null);
		createSeries(g, "ROT", "demand", "Demand", Color.red, true, null);
		graphPane.add(g.getChartPanel());

		g = makeThumbGraph("Sky PA", 0.0, 360.0);
		createSeries(g, "ROT", "skypa", "Sky PA", Color.cyan, true, null);
		graphPane.add(g.getChartPanel());
		
		g = makeThumbGraph("Inst. Temp", -200.0, 40.0);
		createSeries(g, "RATCAM",      "temperature", "RATCAM",      Color.cyan, true, null);
		createSeries(g, "SUPIRCAM",    "temperature", "SUPIRCAM",    Color.cyan, true, null);
		createSeries(g, "RINGO2",      "temperature", "RINGO2",      Color.cyan, true, null);
		createSeries(g, "RISE",        "temperature", "RISE",        Color.cyan, true, null);
		createSeries(g, "FRODO_RED",   "temperature", "FRODO_RED",   Color.cyan, true, null);
		createSeries(g, "FRODO_BLUE",  "temperature", "FRODO_BLUE",  Color.cyan, true, null);
		createSeries(g, "IO_INFRARED", "temperature", "IO_INFRARED", Color.cyan, true, null);
		createSeries(g, "IO_OPTICAL",  "temperature", "IO_OPTICAL",  Color.cyan, true, null);
		graphPane.add(g.getChartPanel());
		
		g = makeThumbGraph("Humidity", 0.0, 100.0);
		createSeries(g, "METEO", "humidity", "Humidity", Color.cyan, true, null);
		graphPane.add(g.getChartPanel());

		g = makeThumbGraph("Temperature", -50.0, 50.0);
		createSeries(g, "METEO", "temperature", "Temperature", Color.cyan, true, null);
		graphPane.add(g.getChartPanel());
		
		g = makeThumbGraph("Cloud", -50.0, 50.0);
		createSeries(g, "METEO", "cloud.amb.other", "Cloud", Color.cyan, true, null);
		graphPane.add(g.getChartPanel());
				
		return graphPane;
	}	
	

	
	private void addlabels(JPanel panel, String prefix) {
		panel.setLayout(new GridLayout(8, 8, 5, 8));
		for (int i = 0; i < 8; i++) {

			panel.add(makeStatusLabel(prefix + " Value"));
			panel.add(makeStatusField("NOMINAL"));
			panel.add(makeStatusLabel(prefix + " Value"));
			panel.add(makeStatusField("NOMINAL"));
			panel.add(makeStatusLabel(prefix + " Value"));
			panel.add(makeStatusField("NOMINAL"));
			panel.add(makeStatusLabel(prefix + " Value"));
                        panel.add(makeStatusField("NOMINAL"));

		}
	}

	private JLabel makeStatusLabel(String text) {
		JLabel label = new JLabel(text);
		label.setFont(STATUS_LABEL_FONT);
		label.setForeground(Color.blue);
		label.setHorizontalAlignment(SwingConstants.RIGHT);
		label.setBorder(BorderFactory.createEmptyBorder());
		return label;
	}

	private JTextField makeStatusField(String text) {
		JTextField field = new JTextField(text);
		field.setFont(STATUS_FONT);
		// field.setOpaque(true);
		field.setForeground(Color.blue);
		field.setBackground(LIGHTSLATE);
		field.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		return field;
	}

	
	/** Create a GraphPanel with associated params.
	 * @param title Title of panel.
	 * @param min Lo limit of plots.
	 * @param max Hi limit of plots.
	 * @return The newly created GraphPanel.
	 */
	private GraphPanel makeGraph(String title, double min, double max) {	
			GraphPanel graph = new GraphPanel(title, min, max, 700, 300);
			return graph;		
	}
	
	/** Create a GraphPanel with associated params.
	 * @param title Title of panel.
	 * @param min Lo limit of plots.
	 * @param max Hi limit of plots.
	 * @return The newly created GraphPanel.
	 */
	private GraphPanel makeThumbGraph(String title, double min, double max) {	
			GraphPanel graph = new GraphPanel(title, min, max, 100, 50);
			return graph;		
	}
	
	/** Add a TimeSeries to a previously built GraphPanel.
	 * @param graph The GraphPanel.
	 * @param cat Status category associated with this series.
	 * @param key Status key associated with this series.
	 * @param label Label for key.
	 * @param color Plot color.
	 * @param joined True if points are joined with a line.
	 * @param symbol Symbol to use if NOT joined.
	 */
	private GraphPanel createSeries(GraphPanel graph, String cat, String key, String label, Color color, boolean joined, Shape symbol) {

		String keywd = cat+"."+key;
		
		TimeSeries ts = graph.addTimeSeries(label, color, joined, symbol);
		
		List<TimeSeries> list = graphMap.get(keywd);
		if (list == null) {
			list = new Vector<TimeSeries>();
			graphMap.put(keywd, list);
		}
			
		list.add(ts);
		System.err.println("TimeSeries linked to incoming: ["+keywd+"]");
	
		return graph;
	}
	
	
}
