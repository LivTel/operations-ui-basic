/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.util.Date;

import javax.swing.JFrame;

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
public class AreaTimeSeriesTest  extends JFrame {

	TimeSeries series1;
	
		 public AreaTimeSeriesTest(String title) {

		        super(title);

		        series1 = new TimeSeries("Random 1", Second.class);
		      

		        TimeSeriesCollection dataset = new TimeSeriesCollection(series1);

		        JFreeChart chart = createChart(dataset);

		         ChartPanel chartPanel = new ChartPanel(chart);
		        chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		        setContentPane(chartPanel);

		    }

		 


		    /**
		     * Creates a chart.
		     * 
		     * @param dataset  the dataset.
		     * 
		     * @return The chart.
		     */
		    private JFreeChart createChart(final XYDataset dataset) {
		        JFreeChart chart = ChartFactory.createXYAreaChart(
		            "XY Area Chart Demo 2",
		            "Time", "Value",
		            dataset,
		            PlotOrientation.VERTICAL,
		            true,  // legend
		            true,  // tool tips
		            false  // URLs
		        );
		        XYPlot plot = chart.getXYPlot();
		    	
		        ValueAxis domainAxis = new DateAxis("time");
		        //domainAxis.setLowerMargin(0.0);
		        //domainAxis.setUpperMargin(0.0);
		        domainAxis.setAutoRange(true);
				domainAxis.setFixedAutoRange(12 * 3600000.0); 

		        plot.setDomainAxis(domainAxis);
		        plot.setForegroundAlpha(0.5f);  
		        
		        XYItemRenderer r = plot.getRenderer();
		        //r.setStroke(new BasicStroke(1.5f));
				r.setPaint(Color.green.darker());
		        return chart;      
		    }
		    
		    public void updateData() {
		    
		    	long start = System.currentTimeMillis();
		    	long t = start;
		    	double d = Math.random()*10.0;
		    	while (t < start + 6*24*3600*1000L) {
		    	
		    		d += Math.random()-0.5; 
		    		long dt = (long)(300000.0 + Math.random()*800000.0);
		    		long tt = t;
		    		while (tt < t + dt) {
		    			series1.add(new Second(new Date(tt)), d);
		    			tt += 60*1000L;
		    		}
		    		try {Thread.sleep(1000L); } catch (Exception e) {}
		    		t += dt;
		    	}
		    	
		    }
		    
		    /**
		     * Starting point for the demonstration application.
		     *
		     * @param args  ignored.
		     */
		    public static void main(final String[] args) {

		    	AreaTimeSeriesTest test = new AreaTimeSeriesTest("Test");
		    	test.pack();
		    	test.setVisible(true);
		    	
		    	test.updateData();
		    		
		     
		    }
	}
