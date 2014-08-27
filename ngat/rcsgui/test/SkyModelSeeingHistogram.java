/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;

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
public class SkyModelSeeingHistogram extends JPanel {

	public static Dimension SIZE = new Dimension(200, 200);
	private SimpleHistogramDataset dataset; 
	private JFreeChart chart;
	private int i =0;
	private String title;
	
	private ChartPanel cp;
	
	public SkyModelSeeingHistogram() {
		super();
	}

	/**
	 * 
	 */
	public SkyModelSeeingHistogram(String title) {
		this();
		this.title= title;
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
	
		String plotTitle = title;
		String xaxis = "Seeing [asec]";
		String yaxis = "Number";
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;
		chart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, dataset, orientation, show, toolTips,
				urls);
		cp = new ChartPanel(chart);
		cp.getChart().getTitle().setFont(new Font("Serif", Font.PLAIN, 12));
		cp.setPreferredSize(SIZE);
		cp.setBorder(BorderFactory.createLineBorder(Color.red));
		System.err.println("borsder: RED");
		add(cp);
	}
	
	public JPanel getChartPanel() {
		return cp;
	}

	public void updateSeeing(double seeing) {
		System.err.println("Add obs: "+(++i)+" "+seeing);
		dataset.addObservation(seeing);
	}
	
	public void asynchUpdate() {
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
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#getPreferredSize()
	 */
	@Override
	public Dimension getPreferredSize() {
		// TODO Auto-generated method stub
		return SIZE;
	}
	
	public static void main(String args[]) {
		SkyModelSeeingHistogram hist = new SkyModelSeeingHistogram("test");
		
		JFrame f = new JFrame("test");
		f.getContentPane().add(hist);
		f.pack();
		f.setVisible(true);
		
	}

}
