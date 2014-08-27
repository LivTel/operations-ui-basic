/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import ngat.astrometry.AstroCatalog;
import ngat.astrometry.BasicAstrometryCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.SolarCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.phase2.XExtraSolarTarget;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.time.Second;
import org.jfree.data.Range;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.ui.Layer;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

/**
 * @author eng
 * 
 */
public class AstrometryPanelTest extends JPanel {

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	static final Color[] colors = new Color[] { Color.cyan, Color.red, Color.blue, Color.green, Color.magenta,
			Color.orange, Color.pink };

	TimeSeriesCollection tsc;

	Map<Integer, TimeSeries> tsmap;
	Map<Integer, TargetTrackCalculator> targets;

	long sod, eod;

    public XYPlot plot;
	private ISite site; 
	BasicAstrometryCalculator astro;

	public AstrometryPanelTest(ISite site, long sod, long eod) {
		super(true);
		this.site = site;
		this.sod = sod;
		this.eod = eod;
		odf.setTimeZone(UTC);
		tsc = new TimeSeriesCollection();
		tsmap = new HashMap<Integer, TimeSeries>();
		targets = new HashMap<Integer, TargetTrackCalculator>();
		
		astro = new BasicAstrometryCalculator();

		JFreeChart chart1 = makeChart("VisibilityPlot", "Elevation", -20.0, 90.0, tsc, Color.cyan);
		ChartPanel cp1 = new ChartPanel(chart1);
		cp1.setPreferredSize(new Dimension(600, 500));
		plot = chart1.getXYPlot();
		//XYItemRenderer r1 = plot.getRenderer();

		setLayout(new BorderLayout());

		add(cp1, BorderLayout.CENTER);

	}

	/** Build the chart. */
	protected JFreeChart makeChart(String title, String ylabel, double lolim, double hilim, TimeSeriesCollection tsc,
			Color color) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		plot = chart.getXYPlot();
		Color bg = new Color(0, 64, 0);
		plot.setBackgroundPaint(bg);
		plot.setDomainGridlinePaint(Color.cyan);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.cyan);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
		// XYItemRenderer r = plot.getRenderer();

		chart.getTitle().setFont(new Font("helvetica", Font.PLAIN, 9));

		ValueAxis axis = plot.getDomainAxis();
		// System.err.println("X axis is a " + axis.getClass().getName());
		if (axis instanceof DateAxis) {
			((DateAxis) axis).setDateFormatOverride(odf);
			System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		// axis.setFixedAutoRange(2 * 3600000.0); // 12 hour
		axis.setAutoRangeMinimumSize(24 * 3600 * 1000.0);

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lolim, hilim);

		// LegendTitle legendTitle = new LegendTitle(plot);
		// legendTitle.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(legendTitle);

		// TODO sort out a daytime marker area or areas.....
		IntervalMarker im = new IntervalMarker(sod, eod, bg.brighter().brighter(), new BasicStroke(1.0f), null, null,
				0.7f);
		plot.addDomainMarker(im, Layer.BACKGROUND);

		// dome elevation
		BasicStroke ds = new BasicStroke(2.0f, // Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				new float[] { 16.0f, 20.0f }, // Dash pattern
				0.0f); // Dash phase
		ValueMarker dome = new ValueMarker(22.5, Color.orange, ds);

		dome.setLabel("Dome limit");
		dome.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		plot.addRangeMarker(dome, Layer.BACKGROUND);

		// Airmass
		double[] airmass = new double[] { 1.0, 1.5, 2.0, 2.5, 4.0, 10.0 };

		// Legend
		// LegendTitle lt = chart.getLegend();
		LegendTitle lt = new LegendTitle(plot);
		lt.setItemFont(new Font("Dialog", Font.PLAIN, 12));
		lt.setBackgroundPaint(new Color(200, 200, 255, 50));
		lt.setFrame(new BlockBorder(Color.white));

		lt.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(lt);
		XYTitleAnnotation xyta = new XYTitleAnnotation(0.001, 0.999, lt, RectangleAnchor.TOP_LEFT);
		plot.addAnnotation(xyta);

		// XYTitleAnnotation ta = new XYTitleAnnotation(0.98, 0.02,
		// lt,RectangleAnchor.TOP_RIGHT);
		// ta.setMaxWidth(0.25);
		// plot.addAnnotation(ta);

		return chart;

	}

	public int addSeries(String name, TargetTrackCalculator track) {		
		int n = tsmap.size();
		TimeSeries ts = new TimeSeries(name);
		tsc.addSeries(ts);
		tsmap.put(n, ts);
		targets.put(n, track);

		XYItemRenderer r1 = plot.getRenderer();

		r1.setSeriesPaint(n, colors[n % 6]);
		// r1.setSeriesShape(0, new Polygon(new int[] { -2, 2, 0 }, new int[] {
		// -2, -2, 2 }, 3));
		r1.setSeriesStroke(n, new BasicStroke(1.0f));

		XYLineAndShapeRenderer lsr1 = (XYLineAndShapeRenderer) r1;
		// lsr1.setDrawOutlines(true);
		// lsr1.setSeriesShapesVisible(0, true);
		lsr1.setSeriesLinesVisible(n, true);

		System.err.println("Create series: " + ts);
		return n;
	}

	public void buildSeries(int n) {

		TargetTrackCalculator track = targets.get(n);

		// start now
		long start = System.currentTimeMillis();
		long t = start;
		double elev = 0.0;

		while (t < start + 24 * 3600 * 1000L) {

			try {
				Coordinates c = track.getCoordinates(t);

				elev = astro.getAltitude(c, site, t);
				addDataActual(n, t, Math.toDegrees(elev));

				t += 60 * 60 * 1000L; // 15mins
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	public void addDataActual(int n, long time, double elev) {
		final int fn = n;
		final int felev = (int) elev;
		final Second fsec = new Second(new Date(time));
		final TimeSeries ts = tsmap.get(n);
		System.err.println("Series: " + n + " is: " + ts);
		if (ts == null)
			return;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				// update 1
				ts.addOrUpdate(fsec, felev);
				System.err.println("Add data point to graph: [" + fn + "]");
			}
		});
	}

	public static void main(String args[]) {

		try {

			BasicSite site = new BasicSite("LT", Math.toRadians(28.0), Math.toRadians(-17.0));

			BasicAstrometryCalculator astro = new BasicAstrometryCalculator();

			// when is sod ?

			long now = System.currentTimeMillis();

			SolarCalculator sun = new SolarCalculator();
			Coordinates csun = sun.getCoordinates(now);

			double sunlev = astro.getAltitude(csun, site, now);

			long eod = 0L;
			long sod = 0L;
			if (sunlev > 0.0) {
				// daytime so want eod ahead (and the next sod/eod)
				sod = now - astro.getTimeSinceLastRise(csun, site, 0.0, now);
				eod = now + astro.getTimeUntilNextSet(csun, site, 0.0, now);
			} else {
				// nighttime, find sunrise and sunset ahead
				sod = now + astro.getTimeUntilNextRise(csun, site, 0.0, now);
				eod = sod + astro.getTimeUntilNextSet(csun, site, 0.0, sod + 3600 * 1000L) + 3600 * 1000L;
				// correction to ensure we work out the next set correctly
			}

			System.err.printf("Next: SOD %tF %tT , EOD %tF %tT\n", sod, sod, eod, eod);
			
			AstrometryPanelTest g = new AstrometryPanelTest(site, sod, eod);

			JFrame f = new JFrame("Astrometry Perspective: Elevation Panel Test");

			JMenuBar bar = new JMenuBar();
			JMenu amenu = new JMenu("Target");

			JMenuItem litem = new JMenuItem("Load...");
			litem.addActionListener(new TListener(f, g, site));
			amenu.add(litem);

			JMenuItem leftItem = new JMenuItem("Scroll <<");
                        leftItem.addActionListener(new ScrollListener(g, -1));
                        amenu.add(leftItem);

			JMenuItem rightItem = new JMenuItem("Scroll >>");
                        rightItem.addActionListener(new ScrollListener(g, 1));
                        amenu.add(rightItem);

			bar.add(amenu);

			f.setJMenuBar(bar);

			f.getContentPane().add(g);

			f.pack();
			f.setVisible(true);

			/*
			 * Vector<XExtraSolarTarget> v = new Vector<XExtraSolarTarget>();
			 * for (int i = 0; i < 6; i++) {
			 * 
			 * XExtraSolarTarget st = new XExtraSolarTarget("st-" + i);
			 * st.setRa(Math.random() * Math.PI * 2.0); st.setDec((Math.random()
			 * - 0.5) * Math.PI); v.add(st); g.addSeries(st);
			 * 
			 * }
			 * 
			 * // now do some astro stuff. for (int i = 0; i < 6; i++) {
			 * 
			 * XExtraSolarTarget st = v.get(i);
			 * 
			 * TargetTrackCalculator track = new BasicTargetCalculator(st,
			 * site);
			 * 
			 * // start now long start = System.currentTimeMillis(); long t =
			 * start; double elev = 0.0;
			 * 
			 * while (t < start + 24 * 3600 * 1000L) {
			 * 
			 * Coordinates c = track.getCoordinates(t);
			 * 
			 * elev = astro.getAltitude(c, site, t); g.addDataActual(i, t,
			 * Math.toDegrees(elev));
			 * 
			 * t += 60 * 60 * 1000L; // 15mins
			 * 
			 * }
			 * 
			 * }
			 */

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    private static class ScrollListener implements ActionListener {

	AstrometryPanelTest g;
	int d;
	public ScrollListener(AstrometryPanelTest g, int d) {
	    this.g = g;
	    this.d = d;
	}
	
	
	//	@Override
	@Override
	public void actionPerformed(ActionEvent ae) {
	    XYPlot plot = g.plot;
	    ValueAxis axis = plot.getDomainAxis();
	    Range range = axis.getRange();
	    double low = range.getLowerBound();
	    double hi  = range.getUpperBound();

	    System.err.println("Range.."+low+" -> "+hi);
	    
	    if (axis instanceof DateAxis) {
		if (d < 0) {
		    low -= 6*3600*1000.0;
		    hi  -= 6*3600*1000.0;
		    axis.setRange(low, hi);
		} else {
		    low += 6*3600*1000.0;
                    hi  += 6*3600*1000.0;
                    axis.setRange(low, hi);
		}
	    }
	    
	}	
    }
    
    
    private static class TListener implements ActionListener {

		JFrame f;
		AstrometryPanelTest p;
		ISite site;

		/**
		 * @param f
		 */
		public TListener(JFrame f, AstrometryPanelTest p, ISite site) {
			super();
			this.f = f;
			this.p = p;
			this.site = site;
		}

	//		@Override
		    @Override
			public void actionPerformed(ActionEvent ae) {
		    
			// request list of targets
			JFileChooser chooser = new JFileChooser();
			chooser.setFileFilter(new CatFilter());
			int returnVal = chooser.showDialog(f, "Load");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose this file: " + chooser.getSelectedFile().getName());

				try {
					// here we could either extract the catname from its file or
					// specify via dialog
					AstroCatalog cat = AstroCatalog.loadCatalog("CAT", chooser.getSelectedFile());

					List<XExtraSolarTarget> tlist = cat.listTargets();
					Iterator<XExtraSolarTarget> it = tlist.iterator();
					while (it.hasNext()) {
						XExtraSolarTarget tgt = it.next();	
						TargetTrackCalculator track = new BasicTargetCalculator(tgt, site);					
						int n = p.addSeries(tgt.getName(), track);
						p.buildSeries(n);
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

	}

	private static class CatFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			if (f.getName().endsWith(".cat"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "Astrometry catalog files";
		}

	}

}
