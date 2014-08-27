/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.plot.XYPlot;

import ngat.opsgui.chart.TimeAxisChart;
import ngat.util.charting.Chart;

/**
 * @author eng
 *
 */
public class ChartBgTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TimeAxisChart tac = new TimeAxisChart("Test Range Markers");
		
		 tac.setTimeLabel("Time [UTC]");
	        tac.setYLabel("Seeing [asec]");
	        tac.setShowGridLines(true);
	        tac.setTimeAxisRange(3600 * 1000L);
	        tac.setYAxisStart(0.0);
	        tac.setYAxisEnd(3.0);
	        //tac.setChartBackgroundColor(Color.magenta.brighter().brighter());
	        
	        try {
	            tac.createPlot("Corrected Standards", "Raw", TimeAxisChart.SHAPE_TRIANGLE, 0, Color.magenta, false, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	            tac.createPlot("Raw Standards", "Raw", TimeAxisChart.SHAPE_TRIANGLE, 0, Color.blue, false, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        try {
	            tac.createPlot("Corrected Science", "Raw", TimeAxisChart.SHAPE_TRIANGLE, 0, Color.yellow, false, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	            tac.createPlot("Raw Science", "Raw", TimeAxisChart.SHAPE_TRIANGLE, 0, Color.pink, false, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        try {
	            tac.createPlot("Predicted", "Raw", TimeAxisChart.SHAPE_NONE, 0, Color.black, false, true);
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
	        tac.addRangeBackground(0.0, 0.8, new Color(192,248,193));
	        tac.addRangeBackground(0.8, 1.3, new Color(248,233,192));
	        tac.addRangeBackground(1.3, 2.0, new Color(248,198,192));
	        tac.addRangeBackground(2.0, 3.0, new Color(192,236,248));

	        JPanel cp = tac.createChartPanel();
	        cp.setLayout(new BorderLayout());
	        
	       	
	        
	        JFrame f = new JFrame("Test");
	        f.getContentPane().add(cp);
	        f.pack();
	        f.setVisible(true);
	        
	        double last = Math.random()*3.0;
	        for (int i = 0; i < 1000; i++) {
	        	double see = last +(Math.random()-0.5)*0.1;
	        	if (see > 3.0)
	        		see = 3.0;
	        	if (see < 0.1)
	        		see = 0.1;
	        	tac.addData("Corrected Standards", (double)(System.currentTimeMillis()), see);
	        	tac.addData("Raw Standards", (double)(System.currentTimeMillis()), see+Math.random()*0.2);
	        	tac.addData("Corrected Science", (double)(System.currentTimeMillis()), see);
	        	tac.addData("Raw Science", (double)(System.currentTimeMillis()), see+Math.random()*0.2);
	        	tac.addData("Predicted", (double)(System.currentTimeMillis()), see+Math.random()*0.2);
		        
	        	try {Thread.sleep(1000l);} catch (Exception e) {}
	        }
	        
	}

}
