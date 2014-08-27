/**
 * 
 */
package ngat.rcsgui.test;

import java.util.Random;

import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;

/**
 * @author eng
 * 
 */
public class CreateHistogramTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		SimpleHistogramDataset dataset = new SimpleHistogramDataset("data-1");	
		double lb = 0.0;
		while (lb < 1.0) {		
			double ub = lb + 0.1;
			SimpleHistogramBin bin = new SimpleHistogramBin(lb, ub, true, false);
			dataset.addBin(bin);
			lb = ub;
		}
		dataset.setAdjustForBinSize(true);
		double[] value = new double[100];
		Random generator = new Random();
		for (int i = 1; i < 100; i++) {
			value[i] = generator.nextDouble();
			dataset.addObservation(value[i]);
		}
		
		String plotTitle = "Seeing samples";
		String xaxis = "Seeing [asec]";
		String yaxis = "Number";
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips,
				urls);
		
		JFrame f = new JFrame("histogram");
		f.getContentPane().add(new ChartPanel(chart));
		f.pack();
		f.setBounds(20, 20, 500, 500);
		f.setVisible(true);


		
		final SimpleHistogramDataset fh = dataset;
		Runnable r = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						Thread.sleep(300);
					} catch (InterruptedException ix) {
					}
					
					double x = Math.random();
					fh.addObservation(x);
					System.err.println("Field updated: "+x);
					
				}
			}
		};

		Thread t = new Thread(r);
		t.start();

	}
}
