/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JPanel;

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

/**
 * @author eng
 *
 */
public class MeteoGraphPanel extends JPanel {

	public static final int FULL_SIZE = 1;
	public static final int THUMBNAIL = 2;
	
	
	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	
	public static final Color GRAPH_BGCOLOR = Color.black;

	public static final Color CHART_BGCOLOR = new Color(153, 203, 203);
	
	private TimeSeriesCollection tsc;
	private TimeSeries ts;
	
	private String name;
	private double lo;
	private double hi;
	/**
	 * @param name
	 * @param lo
	 * @param hi
	 */
	public MeteoGraphPanel(String name, double lo, double hi, int size) {
		super();
		this.name = name;
		this.lo = lo;
		this.hi = hi;
		
		tsc = new TimeSeriesCollection();
		ts = new TimeSeries(name, Second.class);
		//ts.setMaximumItemCount(25000);
		ts.setMaximumItemAge(7200000L);
		tsc.addSeries(ts);
		JFreeChart chart2 = makeChart(name+" Plot", name, lo, hi, tsc, Color.blue);
		ChartPanel cp2 = new ChartPanel(chart2);
		
		switch (size) {
		case FULL_SIZE:
			cp2.setPreferredSize(new Dimension(600, 300));
			break;
		case THUMBNAIL:
			cp2.setPreferredSize(new Dimension(200, 150));
			break;
		}
		add(cp2);
		
	}
	

	private JFreeChart makeChart(String title, String ylabel, double lolim, double hilim, TimeSeriesCollection tsc,
			Color color) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(GRAPH_BGCOLOR);
		plot.setDomainGridlinePaint(Color.green);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		
		XYItemRenderer r = plot.getRenderer();
		r.setPaint(Color.cyan);
		
		ValueAxis axis = plot.getDomainAxis();
		
		if (axis instanceof DateAxis) {
			((DateAxis) axis).setDateFormatOverride(odf);
			System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		axis.setFixedAutoRange(3 * 3600000.0); // 2 hour

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lolim, hilim);

		// LegendTitle legendTitle = new LegendTitle(plot);
		// legendTitle.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(legendTitle);

		return chart;

	}

	public void updateValue(long time, double value) {
		ts.add(new Second(new Date(time)), value);
	}
	
	
}
