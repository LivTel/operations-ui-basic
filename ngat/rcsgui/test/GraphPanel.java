/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

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

/**
 * @author eng
 *
 */
public class GraphPanel {
	
	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	private JFreeChart chart;
	private ChartPanel chartPanel;
	private TimeSeriesCollection tsc;
	
	/**
	 * @return the chartPanel
	 */
	public ChartPanel getChartPanel() {
		return chartPanel;
	}

	/**
	 * Create a new GraphPane.
	 */
	public GraphPanel(String title, double lolim, double hilim, int w, int h) {	
		 tsc = new TimeSeriesCollection();
		 chart = makeChart(title, title, lolim, hilim, tsc);
		 chartPanel = new ChartPanel(chart);
		 chartPanel.setPreferredSize(new Dimension(w, h));
		
	}

	protected JFreeChart makeChart(String title, String ylabel, double lolim, double hilim, TimeSeriesCollection tsc) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(Color.gray);
		plot.setDomainGridlinePaint(Color.BLUE);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.blue);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
		XYItemRenderer r = plot.getRenderer();

		ValueAxis axis = plot.getDomainAxis();

		if (axis instanceof DateAxis) {
			((DateAxis) axis).setDateFormatOverride(odf);
			//System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		axis.setFixedAutoRange(600000.0); // 12 hour

		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lolim, hilim);

		// LegendTitle legendTitle = new LegendTitle(plot);
		// legendTitle.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(legendTitle);

		return chart;

	}
	
	
	/** Create a new time series for this graph.
	 * @param label The label for the key.
	 * @param color Plot color.
	 * @param joined True if a line joining points.
	 * @param symbol Shape of drawn symbol if NOT joined else null.
	 */
	public TimeSeries addTimeSeries(String label, Color color, boolean joined, Shape symbol) {
		TimeSeries ts = new TimeSeries(label, Second.class);
		ts.setMaximumItemCount(500);
		tsc.addSeries(ts);

		// find out the number of this series
		int nts = tsc.getSeriesCount()-1;
		System.err.println("GraphPanel.AddTimeSeries: "+label+" as series: "+nts);
		
		XYPlot plot = chart.getXYPlot();
		XYItemRenderer r = plot.getRenderer();
		
		r.setSeriesPaint(nts, color);
		r.setSeriesStroke(nts, new BasicStroke(1.0f));

		if (! joined) {
			r.setSeriesShape(nts, symbol);
			XYLineAndShapeRenderer lsr = (XYLineAndShapeRenderer) r;
			lsr.setDrawOutlines(true);
			lsr.setSeriesShapesVisible(nts, true);
			lsr.setSeriesLinesVisible(nts, false);
		}

		return ts;
	}
	
	
}
