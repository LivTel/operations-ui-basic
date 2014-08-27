/**
 * 
 */
package ngat.opsgui.chart;

import java.awt.Color;
import java.awt.Font;

/**
 * @author eng
 *
 */
/**
 * @author eng
 *
 */
public interface ITimeAxisChart {

	/**
	 * @param title the title to set
	 */
	public void setTitle(String title);
	/**
	 * @param titleFont the titleFont to set
	 */
	public void setTitleFont(Font titleFont) ;

	/**
	 * @param timeLabel the timeLabel to set
	 */
	public void setTimeLabel(String timeLabel);

	/**
	 * @param timeLabelFont the timeLabelFont to set
	 */
	public void setTimeLabelFont(Font timeLabelFont) ;

	/**
	 * @param yLabel the yLabel to set
	 */
	public void setYLabel(String yLabel);
	/**
	 * @param yLabelFont the yLabelFont to set
	 */
	public void setYLabelFont(Font yLabelFont);

	/**
	 * @param showGridLines the showGridLines to set
	 */
	public void setShowGridLines(boolean showGridLines);

	/**
	 * @param chartBackgroundColor the chartBackgroundColor to set
	 */
	public void setChartBackgroundColor(Color chartBackgroundColor);

	/**
	 * @param gridLineColor the gridLineColor to set
	 */
	public void setGridLineColor(Color gridLineColor);

	/**
	 * @param timeAxisRange the timeAxisRange to set
	 */
	public void setTimeAxisRange(long timeAxisRange);
	/**
	 * @param yAxisStart the yAxisStart to set
	 */
	public void setYAxisStart(double yAxisStart);

	/**
	 * @param yAxisEnd the yAxisEnd to set
	 */
	public void setYAxisEnd(double yAxisEnd);

	
	/** Add a horizontal line with value and color specified.*/
	public void addLine(double value, String label, Color color);
	
	/** Add a colored background between start and end on range axis.*/
	public void addRangeBackground(double start, double end, Color color);		
	
	
	// view methods
	
	/** pan left or right by factor times time range. 
	 * @param factor -ve means go left, +ve means go right.
	 */
	public void pan(double factor);
	
	/** zoom in or out by factor times time range.
	 * @param factor >1 means reduce range, <1 means increase range.
	 */
	public void zoom(double factor);
	
	
	/** Add a data point.
	 * @param plotId The series to add to.
	 * @param time The timestamp.
	 * @param value The data value.
	 */
	public void addData(String plotId, double time, double value);
	

	/** Create a new series.
	 * @param plotId An identity.
	 * @param plotLabel The label to display in the key.
	 * @param symbolType The symbol type.
	 * @param symbolSize The symbol size.
	 * @param symbolColor The symbol color.
	 * @param join True if points should be joined.
	 * @throws Exception
	 */
	public void createPlot(String plotId, String plotLabel, int symbolType, int symbolSize, Color symbolColor, boolean join, boolean legend) throws Exception;
	
	
	
	
	
}
