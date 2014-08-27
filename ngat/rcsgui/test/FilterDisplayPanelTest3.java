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
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.rcs.ers.ReactiveSystemUpdateListener;
import ngat.rcs.ers.test.BasicReactiveSystem;
import ngat.tcm.TelescopeStatusProvider;
import ngat.util.XmlConfigurator;


/**
 * @author eng
 *
 */
public class FilterDisplayPanelTest3 extends JPanel {

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

	public static Dimension PANEL_VIEWPORT_SIZE = new Dimension(640, 200);

	public static Dimension GRAPH_VIEWPORT_SIZE = new Dimension(640, 400);

	
	public static Dimension LINE_SIZE = new Dimension(600, 20);

	static final Font LFONT = new Font("palatino", Font.PLAIN, 10);

	static final Font TFONT = new Font("helvetica", Font.PLAIN, 8);

	static final Color GRAPH_BGCOLOR = Color.black;

	
	JTextField s1;
	
	JTextField f1;

	BooleanField c1;
	BooleanField c2;
	BooleanField c3;
	BooleanField c4;
	BooleanField c5;
	
	BooleanField r1;
	BooleanField r2;
	BooleanField r3;
	BooleanField r4;
	BooleanField r5;
	
	JTextField o1;

	TimeSeries ts1;
		
	TimeSeries tf1;
	
	TimeSeries tc1;
	TimeSeries tc2;
	TimeSeries tc3;
	TimeSeries tc4;
	TimeSeries tc5;
	
	TimeSeries tr1;
	TimeSeries tr2;
	TimeSeries tr3;
	TimeSeries tr4;
	TimeSeries tr5;
	
	TimeSeries to1;
	
	private String base;
	private ImageIcon expandIcon;

	/**
	 * 
	 */
	public FilterDisplayPanelTest3(String base) {
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

		expandIcon = new ImageIcon(base + "/shortcut-icon.png");

		JPanel inner = new JPanel(true);
		inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));

		addLinePanels(inner);

		JScrollPane jsp = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
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

		ts1 = addTimeSeriesGraph(graphPanel, "S_SYS",  "System",    460.0, 466.0, Color.blue);
		
		tf1 = addTimeSeriesGraph(graphPanel, "F_SYS",  "F_SYS",  460.0, 466.0, Color.red.brighter());
	
		tc1 = addTimeSeriesGraph(graphPanel, "C1_SYS_OKAY", "SYS_OKAY",  0.0, 1.0, Color.cyan.darker());
		tc2 = addTimeSeriesGraph(graphPanel, "C2_SYS_STBY", "SYS_STBY",  0.0, 1.0, Color.gray.darker());
		tc3 = addTimeSeriesGraph(graphPanel, "C3_SYS_SUSP", "SYS_SUSP",  0.0, 1.0, Color.pink.darker());
		tc4 = addTimeSeriesGraph(graphPanel, "C4_SYS_FAIL", "SYS_FAIL",  0.0, 1.0, Color.orange.darker());
		tc5 = addTimeSeriesGraph(graphPanel, "C5_SYS_INIT", "SYS_INIT",  0.0, 1.0, Color.red.darker());
		
		
		tr1 = addTimeSeriesGraph(graphPanel, "R1_C1(2m)", "C1 for 2m", 0.0, 1.0, Color.orange.darker());
		tr2 = addTimeSeriesGraph(graphPanel, "R2_C2(2m)", "C2 for 2m", 0.0, 1.0, Color.magenta.darker());
		tr3 = addTimeSeriesGraph(graphPanel, "R3_C3(30s)","C3 for 30s", 0.0, 1.0, Color.blue.brighter());
		tr4 = addTimeSeriesGraph(graphPanel, "R4_C4(30s)","C4 for 30s", 0.0, 1.0, Color.magenta.darker());
		tr5 = addTimeSeriesGraph(graphPanel, "R5_C5(30s)","C5 for 30s", 0.0, 1.0, Color.blue.brighter());
		
		
		to1 = addTimeSeriesGraph(graphPanel, "SYS", "System", 0.0, 1.0, Color.cyan.darker());
		
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
		JFreeChart cts = makeChart(null, name, lo, hi, tsc, color);
		
		ChartPanel cp = new ChartPanel(cts);
		cp.setPreferredSize(new Dimension(540, 60));
		
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
		plot.setDomainGridlinePaint(Color.green);	
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.green);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
		
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
		l.add(makeSmallLabel("S_SYS"));
		s1 = (JTextField) l.add(makeField(4, ""));
		
		l.add(makeSmallLabel("S_SLOW"));
		f1 = (JTextField) l.add(makeField(4, ""));
		l.add(makeb());

		l.add(makeSmallLabel("OKAY"));
		c1 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		l.add(makeSmallLabel("+2m"));
		r1 =  (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());
				
		inner.add(l);

		// Line 2
		l = new LinePanel();
		l.add(makePadding(170));
	
		l.add(makeSmallLabel("STBY"));
		c2 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		l.add(makeSmallLabel("+2m"));
		r2 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		inner.add(l);
		
		// Line 3
		l = new LinePanel();
		l.add(makePadding(170));
					
		l.add(makeSmallLabel("SUSP"));
		c3 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		l.add(makeSmallLabel("+30s"));
		r3 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());
		
		inner.add(l);
	
		// Line 4
		l = new LinePanel();
	
		l.add(makePadding(170));
	
		l.add(makeSmallLabel("FAIL"));
		c4 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		l.add(makeSmallLabel("+30s"));
		r4 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		inner.add(l);
		
		// Line 5
		l = new LinePanel();
		l.add(makePadding(170));
		
		l.add(makeSmallLabel("INIT"));
		c5 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());

		l.add(makeSmallLabel("+30s"));
		r5 = (BooleanField)l.add(makeBoolField(4, ""));
		l.add(makeb());
		
		inner.add(l);
		
		// Line 7
		l = new LinePanel();
		l.add(makeLabel("System"));
		o1 = (JTextField)l.add(makeField(8, "UNKNOWN"));
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
			
			TcsStatusPacket.mapCodes();
			
			FilterDisplayPanelTest3 test = new FilterDisplayPanelTest3(args[0]);

			JFrame f = new JFrame("GUI: Scrolling Filter Table Test: [System]");
			f.getContentPane().add(test);
			f.pack();
			f.setVisible(true);

			MyListener ml = test.createListener();
			System.err.println("Created RSSU");

			//TestSystem ts = new TestSystem();
			//System.err.println("Created TS");
			
			BasicReactiveSystem ts = new BasicReactiveSystem(new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0)));
			XmlConfigurator.use(new File(args[1])).configure(ts);

			System.err.println("Created TRS");
			
			ts.addReactiveSystemUpdateListener(ml);
			System.err.println("Created Added fdp as rssl");

			//MeteorologyStatusProvider meteo = (MeteorologyStatusProvider) Naming.lookup("rmi://ltsim1/Meteorology");
			//meteo.addMeteorologyStatusUpdateListener(ts);
			
			TelescopeStatusProvider tel = (TelescopeStatusProvider)Naming.lookup("rmi://ltsim1/Telescope");
			tel.addTelescopeStatusUpdateListener(ts);
			System.err.println("Linked TS to telescope");
			
			System.err.println("TS::Starting cache reader...");
			ts.startCacheReader();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public class MyListener implements ReactiveSystemUpdateListener {

		FilterDisplayPanelTest3 fdp;

		/**
		 * @param fdp
		 */
		public MyListener(FilterDisplayPanelTest3 fdp) {
			super();
			this.fdp = fdp;
		}

	    //@Override
		@Override
		public void criterionUpdated(String cname, long t, boolean cout) throws RemoteException {
			System.err.printf("Critrn updated: %tF %tT %6s %4b \n", t, t, cname, cout);
			if (cname.equals("C_SYS_OKAY")) {
				c1.updateBoolean(cout);
				tc1.add(new Second(), (cout ? 0.9 : 0.1));
			} else if (cname.equals("C_SYS_STBY")) {
				c2.updateBoolean(cout);
				tc2.add(new Second(), (cout ? 0.9 : 0.1));
			} else if (cname.equals("C_SYS_SUSP")) {
				c3.updateBoolean(cout);
				tc3.add(new Second(), (cout ? 0.9 : 0.1));
			} else if (cname.equals("C_SYS_FAIL")) {
				c4.updateBoolean(cout);
				tc4.add(new Second(), (cout ? 0.9 : 0.1));
			} else if (cname.equals("C_SYS_INIT")) {
				c5.updateBoolean(cout);
				tc5.add(new Second(), (cout ? 0.9 : 0.1));							
			} 
		}

	    //@Override
		@Override
		public void filterUpdated(String fname, long t, Number fin, Number fout) throws RemoteException {
			System.err.printf("Filter updated: %tF %tT %6s %6s : %6s \n", t, t, fname, fin, fout);

			if (fname.equals("A_SYS_STATE")) {
				s1.setText(TcsStatusPacket.codeString(fin.intValue()));
				ts1.add(new Second(new Date(t)), fin.doubleValue());
				f1.setText(TcsStatusPacket.codeString(fout.intValue()));
				tf1.add(new Second(new Date(t)), fout.doubleValue());
			} 
		}

	    //@Override
		@Override
		public void ruleUpdated(String rname, long t, boolean rout) throws RemoteException {
			System.err.printf("Rule   updated: %tF %tT %6s %4b \n", t, t, rname, rout);
			String sysValue = "UNKNOWN";
			double sysInt = 0.0;
			Color sysCol = Color.gray;
			boolean update = false;
			if (rname.equals("SYS_OKAY")) {
				r1.updateBoolean(rout);
				tr1.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
				sysValue = "OKAY";
				sysInt = 0.1;
				sysCol = Color.green;
				update = true;
			} else if (rname.equals("SYS_STBY")) {
				r2.updateBoolean(rout);
				tr2.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
				sysValue = "STBY";
				sysInt = 0.3;
				sysCol = Color.yellow;
				update = true;
			} else if (rname.equals("SYS_SUSP")) {
				r3.updateBoolean(rout);
				tr3.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
				sysValue = "SUSP";
				sysInt = 0.5;
				sysCol = Color.orange;
				update = true;
			} else if (rname.equals("SYS_FAIL")) {
				r4.updateBoolean(rout);
				tr4.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
				sysValue = "FAIL";
				sysInt = 0.7;
				sysCol = Color.red;
				update = true;
			} else if (rname.equals("SYS_INIT")) {
				r5.updateBoolean(rout);
				tr5.addOrUpdate(new Second(), (rout ? 0.9 : 0.1));
				sysValue = "INIT";
				sysInt = 0.9;
				sysCol = Color.red;
				update = true;
			} 

				if (rout && update) {
					to1.addOrUpdate(new Second(), sysInt);
					o1.setText(sysValue);
					o1.setBackground(sysCol);
					o1.setForeground(Color.blue);
					/*if (outputField != null) {
						 outputField.setText(sysValue);
						 outputField.setBackground(sysCol);
						 outputField.setForeground(Color.blue);
					}*/
				}
			
			
		}

	}

}
