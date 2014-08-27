/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.rmi.Naming;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusArchive;

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
public class TcmArchiveRequestGraph {

	public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	
	public static final Color GRAPH_BGCOLOR = Color.black;

	public static final Color CHART_BGCOLOR = new Color(153, 203, 203);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			TimeSeriesCollection tsc = new TimeSeriesCollection();
			TimeSeries ts = new TimeSeries("AZM", Second.class);
			ts.setMaximumItemCount(12000);
			//ts.setMaximumItemAge()
			tsc.addSeries(ts);
			JFreeChart chart2 = makeChart("AzmPlot", "Azm", -180.0, 360.0, tsc, Color.blue);
			ChartPanel cp2 = new ChartPanel(chart2);
			
			cp2.setPreferredSize(new Dimension(600, 300));
		
			JFrame f = new JFrame("AZM Plot");
			f.getContentPane().add(cp2);
			f.pack();
			f.setVisible(true);
			
			TelescopeStatusArchive tar = (TelescopeStatusArchive)Naming.lookup("rmi://ltsim1/TelescopeGateway");
			
			//System.err.printf("Requesting archive data for %4d mins, from: %tT to %tT",(back/60000),t1,t2);
			long t2= System.currentTimeMillis();
			long t1 = t2 - 3*3600*1000L;
			List<TelescopeStatus> list = tar.getTelescopeStatusHistory(t1, t2);
			
			System.err.println("Request returned "+list.size()+" entries");
			
			for (int is = 0; is < list.size(); is++) {
				TelescopeStatus status = list.get(is);
				//System.err.printf("%6d : %tT : %s\n", is, status.getStatusTimeStamp(), status);
				System.err.println("Status: "+status);
				if (status instanceof PrimaryAxisStatus) {
					PrimaryAxisStatus axis = (PrimaryAxisStatus)status;
					if (axis.getMechanismName() == null)
						continue;
					if (axis.getMechanismName().equals("AZM")) {
						ts.add(new Second(new Date(status.getStatusTimeStamp())), axis.getCurrentPosition());
					}
				}
			}
			
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	protected static JFreeChart makeChart(String title, String ylabel, double lolim, double hilim, TimeSeriesCollection tsc,
			Color color) {
		JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
		XYPlot plot = chart.getXYPlot();
		plot.setBackgroundPaint(GRAPH_BGCOLOR);
		plot.setDomainGridlinePaint(Color.green);
		plot.setDomainGridlineStroke(new BasicStroke(0.3f));
		
		XYItemRenderer r = plot.getRenderer();

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
}
