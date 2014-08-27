/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Date;

import javax.swing.JPanel;

import ngat.sms.ExecutionResource;
import ngat.sms.ExecutionResourceBundle;
import ngat.sms.ExecutionResourceUsageEstimationModel;
import ngat.sms.GroupItem;
import ngat.sms.bds.TestResourceUsageEstimator;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;

/**
 * @author eng
 *
 */
public class EfficiencyPanel extends JPanel {

	TimeSeries series1;
	long start;
	long shutter;
	private ExecutionResourceUsageEstimationModel xrm;
	
	public EfficiencyPanel() {
		super(true);
		setLayout(new BorderLayout());
		
		xrm = new TestResourceUsageEstimator();
		
		series1 = new TimeSeries("Open Shutter Efficiency");
	  
		
	        TimeSeriesCollection dataset = new TimeSeriesCollection(series1);

	        JFreeChart chart = createChart(dataset);

	         ChartPanel chartPanel = new ChartPanel(chart);
		add(chartPanel, BorderLayout.CENTER);
	}
	

    /**
     * Creates a chart.
     * 
     * @param dataset  the dataset.
     * 
     * @return The chart.
     */
    private JFreeChart createChart(XYDataset dataset) {
        JFreeChart chart = ChartFactory.createXYAreaChart(
            "Open Shutter efficiency",
            "Time", "Efficiency",
            dataset,
            PlotOrientation.VERTICAL,
            false,  // legend
            false,  // tool tips
            false  // URLs
        );
        XYPlot plot = chart.getXYPlot();
    	
        ValueAxis domainAxis = new DateAxis("time");
        //domainAxis.setLowerMargin(0.0);
        //domainAxis.setUpperMargin(0.0);
        domainAxis.setAutoRange(true);
		domainAxis.setFixedAutoRange(10 * 3600000.0); 

        plot.setDomainAxis(domainAxis);
        plot.setForegroundAlpha(0.5f);  
        
        XYItemRenderer r = plot.getRenderer();
        //r.setStroke(new BasicStroke(1.5f));
		r.setSeriesPaint(0, Color.green.darker());
		
        return chart;      
    }
	
    public void startingGroup(GroupItem group) {
    	start = System.currentTimeMillis();
    	ExecutionResourceBundle xrb = xrm.getEstimatedResourceUsage(group);
		ExecutionResource xt = xrb.getResource("EXPOSURE");
		shutter = (long)xt.getResourceUsage(); // this is millis
    }
    
 
    public void completedGroup() {
    	long end = System.currentTimeMillis();
    	
    	double e = (double)shutter/(double)(end-start);
    	
    	long t = start;  
    	while (t < end) {    	
    		series1.add(new Second(new Date(t)), e);
    		t += 60*1000L;
    	}
    		   	
    }
    
    public void failedGroup() {
    	long end = System.currentTimeMillis();
    	double e = 0.0;
    	long t = start;  
    	while (t < end) {    	
    		series1.add(new Second(new Date(t)), e);
    		t += 60*1000L;
    	}
    }
    
   
}
