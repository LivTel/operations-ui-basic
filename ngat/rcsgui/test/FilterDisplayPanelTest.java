/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import ngat.astrometry.BasicSite;
import ngat.ems.MeteorologyStatusProvider;
import ngat.rcs.ers.ReactiveSystemUpdateListener;
import ngat.rcs.ers.test.BasicReactiveSystem;
import ngat.util.XmlConfigurator;

/**
 * @author eng
 * 
 */
public class FilterDisplayPanelTest extends JPanel {

    public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

    public static Dimension PANEL_VIEWPORT_SIZE = new Dimension(640, 200);

    public static Dimension GRAPH_VIEWPORT_SIZE = new Dimension(640, 400);

    public static Dimension LINE_SIZE = new Dimension(600, 20);

    static final Font LFONT = new Font("palatino", Font.PLAIN, 10);

    static final Font TFONT = new Font("helvetica", Font.PLAIN, 8);

    static final Color GRAPH_BGCOLOR = Color.gray.brighter();

    JTextField s1;
    JTextField s2;
    JTextField s3; // cloud
    JTextField s4; // dust

    JTextField f1;
    JTextField f2;
    JTextField f3;
    JTextField f4;
    JTextField f5; // cloud
    JTextField f6; // dust

    BooleanField c1;
    BooleanField c2;
    BooleanField c3;
    BooleanField c4;
    BooleanField c5;
    BooleanField c6;  // cloud hi
    BooleanField c7;  // cloud lo1
    BooleanField c8;  // cloud lo2
    BooleanField c9;  // dust hi
    BooleanField c10; // dust lo



    BooleanField r1;
    BooleanField r2;
    BooleanField r3;
    BooleanField r4;
    BooleanField r5;

    BooleanField r6;
    BooleanField r7;

    BooleanField r8; 
    BooleanField r9; 
    BooleanField r10; 

    BooleanField r11; // dust lo
    BooleanField r12; // dust hi


    JTextField o1;

    TimeSeries ts1;
    TimeSeries ts2;
    TimeSeries ts3;
    TimeSeries ts4;

    TimeSeries tf1;
    TimeSeries tf2;
    TimeSeries tf3;
    TimeSeries tf4;
    TimeSeries tf5;
    TimeSeries tf6;

    TimeSeries tc1;
    TimeSeries tc2;
    TimeSeries tc3;
    TimeSeries tc4;
    TimeSeries tc5;
    TimeSeries tc6;
    TimeSeries tc7;
    TimeSeries tc8;
    TimeSeries tc9;
    TimeSeries tc10;

    TimeSeries tr1;
    TimeSeries tr2;
    TimeSeries tr3;
    TimeSeries tr4;
    TimeSeries tr5;
    TimeSeries tr6;
    TimeSeries tr7;
    TimeSeries tr8;
    TimeSeries tr9;
    TimeSeries tr10;
    TimeSeries tr11;
    TimeSeries tr12;

    TimeSeries to1;

    private String base;
    private ImageIcon expandIcon;

    /**
     * 
     */
    public FilterDisplayPanelTest(String base) {
	super(true);

	setLayout(new BorderLayout());

	JPanel rowPanel = createRowPanel();
	JPanel graphPanel = createGraphPanel();

	add(rowPanel, BorderLayout.CENTER);
	add(graphPanel, BorderLayout.SOUTH);

    }

    private JPanel createRowPanel() {
	// Row Panel
	JPanel rowPanel = new JPanel(true);
	rowPanel.setLayout(new BorderLayout());
	rowPanel.setPreferredSize(PANEL_VIEWPORT_SIZE);

	this.base = base;
	expandIcon = new ImageIcon(base + "/shortcut-icon.png");

	JPanel inner = new JPanel(true);
	inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

	addLinePanels(inner);

	JScrollPane jsp = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	jsp.add(inner);
	jsp.setViewportView(inner);

	rowPanel.add(jsp, BorderLayout.CENTER);
	return rowPanel;
    }

    private JPanel createGraphPanel() {

	JPanel outer = new JPanel(true);
	outer.setLayout(new BorderLayout());
	outer.setPreferredSize(GRAPH_VIEWPORT_SIZE);

	JPanel graphPanel = new JPanel(true);
	graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));

	ts1 = addTimeSeriesGraph(graphPanel, "S_HUM", "Humidity", 0.0,   1.0, Color.blue);
	ts2 = addTimeSeriesGraph(graphPanel, "S_WS", "WindSpeed", 0.0,  20.0, Color.blue);
	ts3 = addTimeSeriesGraph(graphPanel, "S_CLO", "Cloud", -100.0, -40.0, Color.blue);
	ts4 = addTimeSeriesGraph(graphPanel, "S_TNG", "Dust",     0.0, 100.0, Color.blue);

	tf1 = addTimeSeriesGraph(graphPanel, "F_H_SLOW", "H_Slow", 0.0, 1.0, Color.red.brighter());
	tf2 = addTimeSeriesGraph(graphPanel, "F_H_FAST", "H_Fast", 0.0, 1.0, Color.pink);
	tf3 = addTimeSeriesGraph(graphPanel, "F_WS_SLOW", "WS_Slow", 0.0, 20.0, Color.red.brighter());
	tf4 = addTimeSeriesGraph(graphPanel, "F_WS_FAST", "WS_Fast", 0.0, 20.0, Color.pink);
	tf5 = addTimeSeriesGraph(graphPanel, "F_CLOUD", "Cloud", -40.0, -10.0, Color.pink.darker());
	tf6 = addTimeSeriesGraph(graphPanel, "F_DUST", "Dust",  0.0, 100.0, Color.red.darker());

	tc1 = addTimeSeriesGraph(graphPanel, "C1_H_LO1 > 80", "C1 < 80", 0.0, 1.0, Color.cyan.darker());
	tc2 = addTimeSeriesGraph(graphPanel, "C2_H_LO2 < 80", "C2 < 75", 0.0, 1.0, Color.gray.darker());
	tc3 = addTimeSeriesGraph(graphPanel, "C3_H_HI  < 75", "C3 > 80", 0.0, 1.0, Color.pink.darker());
	tc4 = addTimeSeriesGraph(graphPanel, "C4_WS_LO < 10", "C4 < 10", 0.0, 1.0, Color.orange.darker());
	tc5 = addTimeSeriesGraph(graphPanel, "C5_WS_HI > 15", "C5 > 15", 0.0, 1.0, Color.magenta.darker());
	tc6 = addTimeSeriesGraph(graphPanel, "C6_CLO_HI > -18", "C6 > -18", 0.0, 1.0, Color.magenta.darker());
	tc7 = addTimeSeriesGraph(graphPanel, "C7_CLO_LO1 < -19", "C7 < -19", 0.0, 1.0, Color.magenta.darker());
	tc8 = addTimeSeriesGraph(graphPanel, "C8_CLO_LO2 < -21", "C8 < -21", 0.0, 1.0, Color.magenta.darker());
	tc9 = addTimeSeriesGraph(graphPanel, "C9_TNG_LO < 45", "C9 < 45", 0.0, 1.0, Color.magenta.darker());
	tc10 = addTimeSeriesGraph(graphPanel, "C10_TNG_HI > 45", "C10 > 45", 0.0, 1.0, Color.magenta.darker());


	tr1 = addTimeSeriesGraph(graphPanel, "R1_C1 30m", "C1 for 30m", 0.0, 1.0, Color.orange.darker());
	tr2 = addTimeSeriesGraph(graphPanel, "R2_C2 20m", "C2 for 30m", 0.0, 1.0, Color.magenta.darker());
	tr3 = addTimeSeriesGraph(graphPanel, "R3_C3 20s", "C3 for 20s", 0.0, 1.0, Color.blue.brighter());
	tr4 = addTimeSeriesGraph(graphPanel, "R4_C4 20m", "C4 for 20m", 0.0, 1.0, Color.magenta.darker());
	tr5 = addTimeSeriesGraph(graphPanel, "R5_C5 30s", "C5 for 30s", 0.0, 1.0, Color.blue.brighter());
	tr6 = addTimeSeriesGraph(graphPanel, "R6_C6 10m", "C6 for 10m", 0.0, 1.0, Color.blue.brighter());
	tr7 = addTimeSeriesGraph(graphPanel, "R7_C7 15m", "C7 for 15m", 0.0, 1.0, Color.blue.brighter());
	tr8 = addTimeSeriesGraph(graphPanel, "R8_C8 10m", "C8 for 10m", 0.0, 1.0, Color.blue.brighter());

	tr9  = addTimeSeriesGraph(graphPanel, "R9_C9 2h", "C9 for 2h", 0.0, 1.0, Color.blue.brighter());
        tr10 = addTimeSeriesGraph(graphPanel, "R10_C10 2h", "C10 for 2h", 0.0, 1.0, Color.blue.brighter());

	tr11 = addTimeSeriesGraph(graphPanel, "CON_CLR", "CLR", 0.0, 1.0, Color.orange.darker());
        tr12 = addTimeSeriesGraph(graphPanel, "DIS_ALR", "ALR", 0.0, 1.0, Color.pink.darker());

	to1 = addTimeSeriesGraph(graphPanel, "WEATHER", "Weather", 0.0, 1.0, Color.cyan.darker());

	JScrollPane jsp = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
					  ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
	jsp.add(graphPanel);
	jsp.setViewportView(graphPanel);

	outer.add(jsp, BorderLayout.CENTER);
	return outer;

    }

    private TimeSeries addTimeSeriesGraph(Container base, String label, String name, double lo, double hi, Color color) {
	TimeSeriesCollection tsc = new TimeSeriesCollection();
	TimeSeries ts = new TimeSeries(name, Second.class);
	ts.setMaximumItemCount(25000);
	tsc.addSeries(ts);
	//JFreeChart cts = makeChart(null, name, lo, hi, tsc, color);
	JFreeChart cts = makeChart(null, name, lo, hi, tsc, Color.green);
	ChartPanel cp = new ChartPanel(cts);
	cp.setPreferredSize(new Dimension(540, 120));

	JPanel line = new JPanel(true);
	line.setLayout(new FlowLayout(FlowLayout.LEFT));

	JLabel jl = makeLabel(label);

	line.add(jl);
	line.add(cp);

	base.add(line);
	return ts;
    }

    protected static JFreeChart makeChart(String title, String ylabel, double lolim, double hilim,
					  TimeSeriesCollection tsc, Color color) {
	JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
	XYPlot plot = chart.getXYPlot();

	plot.setBackgroundPaint(GRAPH_BGCOLOR);
	plot.setDomainGridlinePaint(Color.blue);
	plot.setDomainGridlineStroke(new BasicStroke(0.1f));
	plot.setRangeGridlinePaint(Color.blue);
	plot.setRangeGridlineStroke(new BasicStroke(0.1f));

	XYItemRenderer r = plot.getRenderer();
	r.setStroke(new BasicStroke(1.5f));
	r.setPaint(color);

	ValueAxis axis = plot.getDomainAxis();

	if (axis instanceof DateAxis) {
	    ((DateAxis) axis).setDateFormatOverride(odf);
	    // System.err.println("X axis reset date formatter...");
	}
	axis.setAutoRange(true);
	axis.setFixedAutoRange(4 * 3600000.0); // 1 hour

	ValueAxis rangeAxis = plot.getRangeAxis();
	rangeAxis.setRange(lolim, hilim);

	// LegendTitle legendTitle = new LegendTitle(plot);
	// legendTitle.setPosition(RectangleEdge.RIGHT);
	// chart.addLegend(legendTitle);

	return chart;

    }

    /** Add some line panels. */
    private void addLinePanels(JPanel inner) {

	// Line 1
	LinePanel l = new LinePanel();
	l.add(makeSmallLabel("S_H"));
	s1 = (JTextField) l.add(makeField(4, ""));

	l.add(makeSmallLabel("H_F"));
	f1 = (JTextField) l.add(makeField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("< 80"));
	c1 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("+30m"));
	r1 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("CLR"));
	r11 = (BooleanField) l.add(makeBoolField(4, ""));

	inner.add(l);

	// Line 2
	l = new LinePanel();
	l.add(makePadding(72));

	l.add(makeSmallLabel("H_S"));
	f2 = (JTextField) l.add(makeField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("< 75"));
	c2 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("+20m"));
	r2 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	inner.add(l);

	// Line 3
	l = new LinePanel();
	l.add(makePadding(170));

	l.add(makeSmallLabel("> 80"));
	c3 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("+30s"));
	r3 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	inner.add(l);

	// Line 4
	l = new LinePanel();
	l.add(makeSmallLabel("S_W"));
	s2 = (JTextField) l.add(makeField(4, ""));

	l.add(makeSmallLabel("WS_S"));
	f3 = (JTextField) l.add(makeField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("< 10"));
	c4 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("+20m"));
	r4 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("ALR"));
	r12 = (BooleanField) l.add(makeBoolField(4, ""));
	inner.add(l);

	// Line 5
	l = new LinePanel();
	l.add(makePadding(72));

	l.add(makeSmallLabel("WS_F"));
	f4 = (JTextField) l.add(makeField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("> 15"));
	c5 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	l.add(makeSmallLabel("+30s"));
	r5 = (BooleanField) l.add(makeBoolField(4, ""));
	l.add(makeb());

	inner.add(l);

	// new lines for cloud 1
	l = new LinePanel();
	l.add(makeSmallLabel("S_C"));
	s3 = (JTextField) l.add(makeField(4, ""));

	l.add(makeSmallLabel("C_F"));
	f5 = (JTextField) l.add(makeField(4, "?"));
	l.add(makeb());

	l.add(makeSmallLabel("> -18"));
	c6 = (BooleanField) l.add(makeBoolField(4, "?"));
	l.add(makeb());

	l.add(makeSmallLabel("+10m"));
	r6 = (BooleanField) l.add(makeBoolField(4, "?"));
	l.add(makeb());
	inner.add(l);

	// new cloud line 2
	l = new LinePanel();
	l.add(makePadding(170));

	l.add(makeSmallLabel("< -21"));
	c7 = (BooleanField) l.add(makeBoolField(4, "?"));
	l.add(makeb());

	l.add(makeSmallLabel("+15m"));
	r7 = (BooleanField) l.add(makeBoolField(4, "?"));
	l.add(makeb());
	inner.add(l);

	// new cloud line 3
	l = new LinePanel();
	l.add(makePadding(170));

	l.add(makeSmallLabel("< -19"));
	c8 = (BooleanField) l.add(makeBoolField(4, "?"));
	l.add(makeb());

	l.add(makeSmallLabel("+10m"));
	r8 = (BooleanField) l.add(makeBoolField(4, "?"));
	l.add(makeb());
	inner.add(l);

	// dust line 1
	l = new LinePanel();
        l.add(makeSmallLabel("S_D"));
        s4 = (JTextField) l.add(makeField(4, ""));

        l.add(makeSmallLabel("D_F"));
        f6 = (JTextField) l.add(makeField(4, "?"));
        l.add(makeb());

        l.add(makeSmallLabel("< 45"));
        c9 = (BooleanField) l.add(makeBoolField(4, "?"));
        l.add(makeb());

        l.add(makeSmallLabel("+2h"));
        r9 = (BooleanField) l.add(makeBoolField(4, "?"));
        l.add(makeb());
        inner.add(l);

	// dust line 2
	l = new LinePanel();
        l.add(makePadding(170));

        l.add(makeSmallLabel("> 45"));
        c10 = (BooleanField) l.add(makeBoolField(4, "?"));
        l.add(makeb());

        l.add(makeSmallLabel("+2h"));
        r10 = (BooleanField) l.add(makeBoolField(4, "?"));
        l.add(makeb());
        inner.add(l);

	// Line 6
	l = new LinePanel();
	l.add(makeLabel("Weather"));
	o1 = (JTextField) l.add(makeField(8, "UNKNOWN"));
	inner.add(l);
    }

    private JLabel makeLabel(String text) {
	JLabel label = new JLabel(text, SwingConstants.LEFT);
	label.setFont(LFONT);
	label.setPreferredSize(new Dimension(60, 16));
	return label;
    }

    private JLabel makePadding(int w) {
	JLabel label = new JLabel("", SwingConstants.LEFT);
	label.setFont(LFONT);
	label.setPreferredSize(new Dimension(w, 16));
	return label;
    }

    private JLabel makeSmallLabel(String text) {
	JLabel label = new JLabel(text, SwingConstants.LEFT);
	label.setFont(LFONT);
	label.setPreferredSize(new Dimension(36, 16));
	return label;
    }

    private JTextField makeField(int w, String text) {
	JTextField field = new JTextField(w);
	field.setText(text);
	field.setFont(TFONT);
	return field;
    }

    private BooleanField makeBoolField(int w, String text) {
	BooleanField field = new BooleanField(w);
	field.setText(text);
	field.setFont(TFONT);
	return field;
    }

    private JButton makeb() {

	JButton b = new JButton(expandIcon);
	b.setPreferredSize(new Dimension(16, 16));
	b.setBorderPainted(false);
	// b.setBackground(Color.cyan);
	return b;
    }

    public MyListener createListener() {
	return new MyListener(this);
    }

    class BooleanField extends JTextField {

	/**
	 * 
	 */
	public BooleanField(int n) {
	    super(n);
	    setBackground(Color.gray);
	    setForeground(Color.blue);
	    setText("Unk");
	}

	public void updateBoolean(boolean b) {
	    if (b) {
		setBackground(Color.green);
		setText("True");
	    } else {
		setBackground(Color.red);
		setText("False");
	    }
	}

    }

    /**
     * @author eng
     * 
     */
    public class LinePanel extends JPanel {

	/**
	 * 
	 */
	public LinePanel() {
	    super(true);
	    setLayout(new FlowLayout(FlowLayout.LEADING));
	    setPreferredSize(new Dimension(600, 20));
	    setBorder(BorderFactory.createLoweredBevelBorder());
	}

    }

    public static void main(String args[]) {

	try {
	    FilterDisplayPanelTest test = new FilterDisplayPanelTest(args[0]);

	    // FilterDisplayPanelTest <baseDir> <xmlfile> <methost>

	    JFrame f = new JFrame("GUI: Scrolling Filter Table Test: [Weather]");
	    f.getContentPane().add(test);
	    f.pack();
	    f.setVisible(true);

	    MyListener ml = test.createListener();
	    System.err.println("Created RSSU");

	    // TestSystem ts = new TestSystem();
	    // System.err.println("Created TS");

	    BasicReactiveSystem ts = new BasicReactiveSystem(new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0)));
	    XmlConfigurator.use(new File(args[1])).configure(ts);

	    System.err.println("Created TRS");

	    ts.addReactiveSystemUpdateListener(ml);
	    System.err.println("Created Added fdp as rssl");

	    String meteohost = args[2];
	    MeteorologyStatusProvider meteo = (MeteorologyStatusProvider) Naming.lookup("rmi://"+meteohost+"/Meteorology");
	    meteo.addMeteorologyStatusUpdateListener(ts);
	    System.err.println("Linked TS to meteo");
	    
	    System.err.println("TS::Starting cache reader...");
	    ts.startCacheReader();

	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    public class MyListener implements ReactiveSystemUpdateListener {

	FilterDisplayPanelTest fdp;

	/**
	 * @param fdp
	 */
	public MyListener(FilterDisplayPanelTest fdp) {
	    super();
	    this.fdp = fdp;
	}

	// @Override
	@Override
	public void criterionUpdated(String cname, long t, boolean cout) throws RemoteException {
	    System.err.printf("Critrn updated: %tF %tT %6s %4b \n", t, t, cname, cout);
	    if (cname.equals("C_LO_1")) {
		c1.updateBoolean(cout);
		tc1.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_LO_2")) {
		c2.updateBoolean(cout);
		tc2.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_HI_1")) {
		c3.updateBoolean(cout);
		tc3.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_LO_3")) {
		c4.updateBoolean(cout);
		tc4.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_HI_2")) {
		c5.updateBoolean(cout);
		tc5.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_CLOUD_LO_1")) {
		c7.updateBoolean(cout);
		tc7.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_CLOUD_LO_2")) {
		c8.updateBoolean(cout);
		tc8.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_CLOUD_HI")) {
		c6.updateBoolean(cout);
		tc6.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    } else if (cname.equals("C_DUST_LO")) {
                c9.updateBoolean(cout);
                tc9.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    }  else if (cname.equals("C_DUST_HI")) {
		c10.updateBoolean(cout);
		tc10.addOrUpdate(new Second(), (cout ? 0.9 : 0.1));
	    }
	}

	// @Override
	@Override
	public void filterUpdated(String fname, long t, Number fin, Number fout) throws RemoteException {
	    System.err.printf("Filter updated: %tF %tT %6s %6s : %6s \n", t, t, fname, fin, fout);

	    if (fname.equals("M_HUM_SLOW")) {
		s1.setText("" + fin.doubleValue());
		ts1.addOrUpdate(new Second(new Date(t)), fin.doubleValue());
		f1.setText("" + fout.doubleValue());
		tf1.addOrUpdate(new Second(new Date(t)), fout.doubleValue());
	    } else if (fname.equals("M_HUM_FAST")) {
		f2.setText("" + fout.doubleValue());
		tf2.addOrUpdate(new Second(new Date(t)), fout.doubleValue());
	    } else if (fname.equals("M_WS_SLOW")) {
		s2.setText("" + fin.doubleValue());
		ts2.addOrUpdate(new Second(new Date(t)), fin.doubleValue());
		f3.setText("" + fout.doubleValue());
		tf3.addOrUpdate(new Second(new Date(t)), fout.doubleValue());
	    } else if (fname.equals("M_WS_FAST")) {
		f4.setText("" + fout.doubleValue());
		tf4.addOrUpdate(new Second(new Date(t)), fout.doubleValue());
	    } else if (fname.equals("B_SKYAMB")) {
		s3.setText("" + fin.doubleValue());
		ts3.addOrUpdate(new Second(new Date(t)), fin.doubleValue());
		f5.setText("" + fout.doubleValue());
		tf5.addOrUpdate(new Second(new Date(t)), fout.doubleValue());
	    } else if (fname.equals("T_DUST")) {
		s4.setText("" + fin.doubleValue());
                ts4.addOrUpdate(new Second(new Date(t)), fin.doubleValue());
		f6.setText("" + fout.doubleValue());
                tf6.addOrUpdate(new Second(new Date(t)), fout.doubleValue());				
	    }
	}

	// erride
	@Override
	public void ruleUpdated(String rname, long t, boolean rout) throws RemoteException {
	    System.err.printf("Rule   updated: %tF %tT %6s %4b \n", t, t, rname, rout);
	    if (rname.equals("HUM_LO_1")) {
		r1.setText("" + rout);
		r1.updateBoolean(rout);
		tr1.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("HUM_LO_2")) {
		r2.setText("" + rout);
		r2.updateBoolean(rout);
		tr2.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("HUM_HI")) {
		r3.setText("" + rout);
		r3.updateBoolean(rout);
		tr3.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("WS_LO")) {
		r4.setText("" + rout);
		r4.updateBoolean(rout);
		tr4.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("WS_HI")) {
		r5.setText("" + rout);
		r5.updateBoolean(rout);
		tr5.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("CLOUD_HI")) {
		r6.setText("" + rout);
		r6.updateBoolean(rout);
		tr6.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("CLOUD_LO_1")) {
		r7.setText("" + rout);
		r7.updateBoolean(rout);
		tr7.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("CLOUD_LO_2")) {
		r8.setText("" + rout);
		r8.updateBoolean(rout);
		tr8.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("DUST_LO")) {
		r9.setText("" + rout);
                r9.updateBoolean(rout);
                tr9.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("DUST_HI")) {
                r10.setText("" + rout);
                r10.updateBoolean(rout);
                tr10.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
	    } else if (rname.equals("METEO_CLEAR")) {
		r11.updateBoolean(rout);
		tr11.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
		if (rout) {
		    to1.addOrUpdate(new Second(), 0.1);
		    o1.setText("CLEAR");
		    o1.setBackground(Color.GREEN);
		    o1.setForeground(Color.blue);
		}
	    } else if (rname.equals("METEO_ALERT")) {
		r12.updateBoolean(rout);
		tr12.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));

		if (rout) {
		    to1.addOrUpdate(new Second(), 0.9);
		    o1.setText("ALERT");
		    o1.setBackground(Color.RED);
		    o1.setForeground(Color.blue);
		}
	    }

	}

    }

}
