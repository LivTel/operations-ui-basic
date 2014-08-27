/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Polygon;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
public class ScheduleDataPanel extends JPanel {
	
	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	TimeSeriesCollection tsc1;
	TimeSeries tsContention;
	TimeSeries tsPrediction;
	
	public ScheduleDataPanel() {
		super(true);
		odf.setTimeZone(UTC);
		tsc1 = new TimeSeriesCollection();
		tsContention = new TimeSeries("CONT");
		tsContention.setMaximumItemCount(500);
		tsc1.addSeries(tsContention);

		tsPrediction = new TimeSeries("PRED");
		tsPrediction.setMaximumItemCount(2000);
		tsc1.addSeries(tsPrediction);
		
		JFreeChart chart1 = makeChart("ContentionPlot", "Schedule Contention", 0.0, 100.0, tsc1, Color.cyan);
		ChartPanel cp1 = new ChartPanel(chart1);
		cp1.setPreferredSize(new Dimension(500, 300));
		XYPlot plot1 = chart1.getXYPlot();
		XYItemRenderer r1 = plot1.getRenderer();
		r1.setSeriesPaint(0, Color.orange);
		r1.setSeriesShape(0, new Polygon(new int[] { -2, 2, 0 }, new int[] { -2, -2, 2 }, 3));
		r1.setSeriesStroke(0, new BasicStroke(1.0f));

		XYLineAndShapeRenderer lsr1 = (XYLineAndShapeRenderer) r1;
		lsr1.setDrawOutlines(true);
		lsr1.setSeriesShapesVisible(0, true);
		lsr1.setSeriesLinesVisible(0, true);
		
		r1.setSeriesPaint(1, Color.cyan);
		r1.setSeriesShape(1, new Polygon(new int[] { -2, 2, 0 }, new int[] { -2, -2, 2 }, 3));
		r1.setSeriesStroke(1, new BasicStroke(1.0f));

		lsr1.setSeriesShapesVisible(1, true);
		lsr1.setSeriesLinesVisible(1, true);
		
		
		setLayout(new BorderLayout());
		
		add(cp1, BorderLayout.CENTER);
		
	}
	
	/** Build the chart. */
	protected JFreeChart makeChart(String title, String ylabel, double lolim, double hilim, TimeSeriesCollection tsc,
			Color color) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(new Color(0,64,0));
		plot.setDomainGridlinePaint(Color.cyan);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		plot.setRangeGridlinePaint(Color.cyan);
		plot.setRangeGridlineStroke(new BasicStroke(0.3f));
		XYItemRenderer r = plot.getRenderer();

		ValueAxis axis = plot.getDomainAxis();
		//System.err.println("X axis is a " + axis.getClass().getName());
		if (axis instanceof DateAxis) {
			((DateAxis) axis).setDateFormatOverride(odf);
			System.err.println("X axis reset date formatter...");
		}
		axis.setAutoRange(true);
		//axis.setFixedAutoRange(2 * 3600000.0); // 12 hour
		axis.setAutoRangeMinimumSize(1*3600*1000.0);
		
		ValueAxis rangeAxis = plot.getRangeAxis();
		rangeAxis.setRange(lolim, hilim);

		// LegendTitle legendTitle = new LegendTitle(plot);
		// legendTitle.setPosition(RectangleEdge.RIGHT);
		// chart.addLegend(legendTitle);

		return chart;

	}

	public void addDataActual(long time, int contention) {	
		final int fcont = contention;
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				// update the contention graph				
				tsContention.add(new Second(), fcont);
				System.err.println("Add data point to cont graph");
			}
		});
	}
	
	public void addDataPredict(long time, int contention) {	
		final int fcont = contention;
		final Second fsec = new Second(new Date(time));
		SwingUtilities.invokeLater(new Runnable() {	
			@Override
			public void run() {
				// update the contention graph				
				tsPrediction.addOrUpdate(fsec, fcont);
				System.err.println("Add data point to pred graph");
			}
		});
	}
	
}
