/** Create a histogram plot with stats overlay
 * 
 */
package ngat.opsgui.test;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Rectangle2D;

import javax.swing.BorderFactory;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.LegendItem;
import org.jfree.chart.LegendItemCollection;
import org.jfree.chart.annotations.XYAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.event.RendererChangeListener;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.labels.XYSeriesLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.chart.plot.CrosshairState;
import org.jfree.chart.plot.Marker;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.urls.XYURLGenerator;
import org.jfree.data.DomainOrder;
import org.jfree.data.Range;
import org.jfree.data.function.Function2D;
import org.jfree.data.function.NormalDistributionFunction2D;
import org.jfree.data.general.DatasetChangeListener;
import org.jfree.data.general.DatasetGroup;
import org.jfree.data.general.DatasetUtilities;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;
import org.jfree.data.xy.DefaultXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.Layer;

/**
 * @author eng
 *
 */
public class CreateOverlaidHistogram {
	 
	

	private SimpleHistogramDataset dataset;
	private XYSeries series2;
	
	private double sum;
	private double av;
	private int n;
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		CreateOverlaidHistogram ch = new CreateOverlaidHistogram();
		ch.run();
	}
	
	public CreateOverlaidHistogram() {
	
		// Histrogram plot
		
		// Primary Histogram dataset
		
		dataset = new SimpleHistogramDataset("data-1");	
		dataset.setAdjustForBinSize(false);
		double lb = 0.0;
		while (lb < 2.0) {		
			double ub = lb + 0.1;
			SimpleHistogramBin bin = new SimpleHistogramBin(lb, ub, true, false);
			dataset.addBin(bin);
			System.err.println("Add bin: "+bin.getLowerBound()+","+bin.getUpperBound());
			lb = ub;
		}
		//dataset.setAdjustForBinSize(true);
		
		// Statistics overlay dataset
	
	
		String plotTitle = "Seeing Test";
		String xaxis = "Seeing [asec]";
		String yaxis = "Number";
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;
		JFreeChart chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips,
				urls);
		
		XYPlot plot = chart.getXYPlot();

		XYDataset data2 = new DefaultXYDataset();
		 Function2D normal = new NormalDistributionFunction2D(1.3, 0.4);
	        XYDataset dataset2 = DatasetUtilities.sampleFunction2D(normal, 0.0, 5.0, 200, "Normal");
	       

		plot.setDataset(1, dataset2);
		DefaultXYItemRenderer r2 = new DefaultXYItemRenderer();
		r2.setSeriesStroke(0, new BasicStroke(0.3f));
		r2.setSeriesShapesVisible(0, false);
		plot.setRenderer(1, r2);
	
	
	
		ChartPanel cp = new ChartPanel(chart);
		cp.getChart().getTitle().setFont(new Font("Serif", Font.PLAIN, 12));
		
		cp.setBorder(BorderFactory.createLineBorder(Color.red));
	
		JFrame f = new JFrame("Overlay Histrogram Test");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(cp);
		f.pack();
		f.setVisible(true);
	}
	
	private void run() {
		// now start the old updates
		for (int i = 0; i < 1000; i++){
			
			double sample = Math.random()*2.0;
			dataset.addObservation(sample);
			
			// stats
			sum += sample;
			n++;
			av = sum/n;

			// create new xy dataset
		
			try {Thread.sleep(1000L);} catch (Exception e) {}
			
		}
		
		      
	}
	

}
