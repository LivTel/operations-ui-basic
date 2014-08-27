/**
 * 
 */
package ngat.opsgui.chart;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import ngat.util.charting.TextTitle;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.IntervalMarker;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.chart.title.Title;
import org.jfree.data.time.Second;
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
public class TimeAxisChart implements ITimeAxisChart {

	public static final int SHAPE_NONE = 0;

	public static final int SHAPE_RECTANGLE = 1;

	public static final int SHAPE_TRIANGLE = 2;

	public static final int SHAPE_CIRCLE = 3;

	private String title;

	private Font titleFont;

	private String timeLabel;

	private Font timeLabelFont;

	private String yLabel;

	private Font yLabelFont;

	private boolean showGridLines;

	private Color chartBackgroundColor;

	private Color gridLineColor;

	private long timeAxisRange;

	private double yAxisStart;

	private double yAxisEnd;

	private boolean nolegend;

	private Map<String, PlotData> plots;

	private Map<String, TimeSeries> series;

	private JFreeChart chart;

	private ChartPanel cp;

	private XYPlot xyplot;
	
	private XYTitleAnnotation keyanot;

	private TimeSeriesCollection tsc;

	/**
	 * @param title
	 */
	public TimeAxisChart(String title) {
		super();
		this.title = title;

		plots = new HashMap<String, PlotData>();
		series = new HashMap<String, TimeSeries>();

		tsc = new TimeSeriesCollection();

		chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", title,
				tsc, false, true, false);

		xyplot = chart.getXYPlot();

		LegendTitle legendTitle = new LegendTitle(xyplot);
		//legendTitle.setPosition(RectangleEdge.TOP);
		
		legendTitle.setBackgroundPaint(new Color(200, 200, 255, 100));
		legendTitle.setFrame(new BlockBorder(Color.white));
		//legendTitle.setPosition(RectangleEdge.BOTTOM);
		keyanot = new XYTitleAnnotation(0.28, 0.98, legendTitle,RectangleAnchor.TOP_LEFT);
		keyanot.setMaxWidth(0.28);
		xyplot.addAnnotation(keyanot);
		
		
		//chart.addLegend(legendTitle);

		xyplot.setBackgroundPaint(Color.gray.brighter());
		xyplot.setDomainGridlinePaint(Color.BLUE);
		xyplot.setDomainGridlineStroke(new BasicStroke(0.3f));
		xyplot.setRangeGridlinePaint(Color.blue);
		xyplot.setRangeGridlineStroke(new BasicStroke(0.3f));

		
		org.jfree.chart.title.TextTitle tit = new org.jfree.chart.title.TextTitle(title, new Font("sans", Font.BOLD, 14));
		chart.setTitle(tit);
		
		Font labelFont = new Font("sans", Font.BOLD, 14);
		
		ValueAxis range = xyplot.getRangeAxis();
		range.setLabelFont(labelFont);
		//range.setLabelPaint(Color.cyan);
		range.setTickLabelFont(labelFont);
		//range.setTickLabelPaint(Color.green);
		
		ValueAxis domain = xyplot.getDomainAxis();
		domain.setLabelFont(labelFont);
		//domain.setLabelPaint(Color.magenta);
		domain.setTickLabelFont(labelFont);
		//domain.setTickLabelPaint(Color.blue);
		
		//xyplot.setDomainPannable(true);
		
		XYItemRenderer renderer = xyplot.getRenderer();
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		renderer.setSeriesStroke(1, new BasicStroke(1.0f));
		renderer.setSeriesStroke(2, new BasicStroke(1.0f));

		XYLineAndShapeRenderer lsr = (XYLineAndShapeRenderer) renderer;
		lsr.setDrawOutlines(true);

	}

	public JPanel createChartPanel() {

		ValueAxis axis = xyplot.getDomainAxis();
		axis.setAutoRange(true);
		axis.setFixedAutoRange(timeAxisRange);
		

		ValueAxis rangeAxis = xyplot.getRangeAxis();
		rangeAxis.setRange(yAxisStart, yAxisEnd);

		cp = new ChartPanel(chart);
		return cp;

	}

	/**
	 * @param title
	 *            the title to set
	 */
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * @param titleFont
	 *            the titleFont to set
	 */
	@Override
	public void setTitleFont(Font titleFont) {
		this.titleFont = titleFont;
	}

	/**
	 * @param timeLabel
	 *            the timeLabel to set
	 */
	@Override
	public void setTimeLabel(String timeLabel) {
		this.timeLabel = timeLabel;
	}

	/**
	 * @param timeLabelFont
	 *            the timeLabelFont to set
	 */
	@Override
	public void setTimeLabelFont(Font timeLabelFont) {
		this.timeLabelFont = timeLabelFont;
	}

	/**
	 * @param yLabel
	 *            the yLabel to set
	 */
	@Override
	public void setYLabel(String yLabel) {
		this.yLabel = yLabel;
	}

	/**
	 * @param yLabelFont
	 *            the yLabelFont to set
	 */
	@Override
	public void setYLabelFont(Font yLabelFont) {
		this.yLabelFont = yLabelFont;
	}

	/**
	 * @param showGridLines
	 *            the showGridLines to set
	 */
	@Override
	public void setShowGridLines(boolean showGridLines) {
		this.showGridLines = showGridLines;
	}

	/**
	 * @param chartBackgroundColor
	 *            the chartBackgroundColor to set
	 */
	@Override
	public void setChartBackgroundColor(Color chartBackgroundColor) {
		this.chartBackgroundColor = chartBackgroundColor;
		chart.setBackgroundPaint(chartBackgroundColor);
	}

	/**
	 * @param gridLineColor
	 *            the gridLineColor to set
	 */
	@Override
	public void setGridLineColor(Color gridLineColor) {
		this.gridLineColor = gridLineColor;
	}

	/**
	 * phase2CacheService.
	 * 
	 * @param timeAxisRange
	 *            the timeAxisRange to set
	 */
	@Override
	public void setTimeAxisRange(long timeAxisRange) {
		this.timeAxisRange = timeAxisRange;
	}

	/**
	 * @param yAxisStart
	 *            the yAxisStart to set
	 */
	@Override
	public void setYAxisStart(double yAxisStart) {
		this.yAxisStart = yAxisStart;
	}

	/**
	 * @param yAxisEnd
	 *            the yAxisEnd to set
	 */
	@Override
	public void setYAxisEnd(double yAxisEnd) {
		this.yAxisEnd = yAxisEnd;
	}

	@Override
	public void pan(double factor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoom(double factor) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.opsgui.chart.ITimeAxisChart#addLine(double, java.awt.Color)
	 */
	@Override
	public void addLine(double value, String label, Color color) {

		// horizontal marker
		BasicStroke ds = new BasicStroke(1.0f, // Width
				BasicStroke.CAP_SQUARE, // End cap
				BasicStroke.JOIN_MITER, // Join style
				10.0f, // Miter limit
				new float[] { 8.0f, 10.0f }, // Dash pattern
				0.0f); // Dash phase
		ValueMarker dome = new ValueMarker(value, color, ds);

		dome.setLabel(label);
		dome.setLabelTextAnchor(TextAnchor.BOTTOM_LEFT);
		xyplot.addRangeMarker(dome, Layer.BACKGROUND);

	}
	
	// add overlay background color
	public void addRangeBackground(double start, double end, Color color) {
		
		IntervalMarker im = new IntervalMarker(start, end);
		im.setPaint(color);
		xyplot.addRangeMarker(im, Layer.BACKGROUND);

	}
	

	@Override
	public void addData(String seriesId, double time, double value) {

		System.err.println("Plot:" + title + " add data to series: " + seriesId
				+ " (" + time + "," + value);
		TimeSeries ts = series.get(seriesId);

		if (ts == null)
			return;
		Date dd = new Date((long) time);

		// locate the correct plot render for the relevant series number....

		ts.addOrUpdate(new Second(dd), value);

	}

	@Override
	public void createPlot(String plotId, String plotLabel, int symbolType,
			int symbolSize, Color symbolColor, boolean join, boolean legend)
			throws Exception {


		PlotData pdata = new PlotData();
		pdata.plotLabel = plotLabel;
		pdata.symbolType = symbolType;
		pdata.symbolColor = symbolColor;
		pdata.symbolSize = symbolSize;
		pdata.join = join;

		plots.put(plotId, pdata);

		int pno = plots.size() - 1;
		
		// select the shape to draw or none
		Shape shape = null;
		switch (symbolType) {
		case SHAPE_NONE:
			break;
		case SHAPE_RECTANGLE:
			shape = new Rectangle(3, 3);
			break;
		case SHAPE_TRIANGLE:
			shape = new Polygon(new int[] { -2, 2, 0 },
					new int[] { -2, -2, 2 }, 3);
			break;
		case SHAPE_CIRCLE:
			shape = new Ellipse2D.Double(-2, -2, 4, 4);
			break;
		}

		xyplot.getRenderer().setSeriesPaint(pno, symbolColor);

		XYLineAndShapeRenderer lsr = (XYLineAndShapeRenderer) xyplot
				.getRenderer();
		lsr.setDrawOutlines(true);
		lsr.setSeriesLinesVisible(pno, join);

		if (symbolType == SHAPE_NONE) {
			lsr.setSeriesShapesVisible(pno, false);

		} else {
			xyplot.getRenderer().setSeriesShape(pno, shape);
			lsr.setSeriesShapesVisible(pno, true);

		}

		TimeSeries ts = new TimeSeries(plotId);
		tsc.addSeries(ts);
		series.put(plotId, ts);

		if (!legend)
			xyplot.removeAnnotation(keyanot);//chart.removeLegend();
	}
}
