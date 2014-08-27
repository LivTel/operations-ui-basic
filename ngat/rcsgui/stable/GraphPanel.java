package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.text.*;
import java.util.*;
import javax.swing.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.chart.title.LegendTitle;
import org.jfree.ui.RectangleEdge;


import java.awt.*;


/** Displays a Graph with some controls. */
public class GraphPanel extends JPanel {

	public static final Color GRAPH_BGCOLOR = new Color(204, 204, 153);

	public static final Color CHART_BGCOLOR = new Color(153, 203, 203);

	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	public static SimpleDateFormat sdf = new SimpleDateFormat(
			"yyyy MMM dd HH:mm:ss z");

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

	public static NumberFormat nf = NumberFormat.getInstance();

	private JFreeChart chart;

	private TimeSeriesCollection tsc;

	private XYPlot plot;
	
	/** Lo limit of Y axis. */
	private double lolim;

	/** Hi limit of Y axis. */
	private double hilim;

	private double cmin;

	private double cmax;

	/** Title. */
	private String title;

	/** Y axis label. */
	private String label;

	private ValueAxis rangeAxis;

	/**
	 * Create a GraphPanel.
	 */
	public GraphPanel(String title, String label, double lolim, double hilim) {
		super(true);

		this.title = title;
		this.lolim = lolim;
		this.hilim = hilim;
		this.label = label;
		cmin = lolim;
		cmax = hilim;
		setLayout(new BorderLayout());

		setMinimumSize(new Dimension(200, 200));

		sdf.setTimeZone(UTC);
		odf.setTimeZone(UTC);
		nf.setMaximumFractionDigits(3);
		nf.setMinimumFractionDigits(1);

		tsc = new TimeSeriesCollection();

		chart = makeChart(title, label, lolim, hilim);
		ChartPanel cp = new ChartPanel(chart);	

		add(cp, BorderLayout.CENTER);
	
	}

	/** Build the chart. */
	protected JFreeChart makeChart(String title, String label, double lolim,
			double hilim) {
		chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", label,
				tsc, false, true, false);
		plot = chart.getXYPlot();
		plot.setBackgroundPaint(GRAPH_BGCOLOR);
		plot.setDomainGridlinePaint(Color.BLUE);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.blue);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
	
		ValueAxis axis = plot.getDomainAxis(); 
		System.err.println("X axis is a "+axis.getClass().getName());
		if (axis instanceof DateAxis) {		   
		    ((DateAxis)axis).setDateFormatOverride(odf);
		    System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		axis.setFixedAutoRange(3600000.0); // 1 hour

		rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lolim, hilim);
			
		LegendTitle legendTitle = new LegendTitle(plot);
		legendTitle.setPosition(RectangleEdge.RIGHT);
		chart.addLegend(legendTitle);
		
		return chart;

	}

	/** Returns the imeSeriesCollection . */
	public TimeSeriesCollection getTSC() {
		return tsc;
	}

	public XYPlot getPlot() {
		return plot;
	}
	

} // [GraphFrame]
