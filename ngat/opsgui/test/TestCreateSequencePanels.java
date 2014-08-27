/**
 * 
 */
package ngat.opsgui.test;

import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import ngat.opsgui.util.TimeAxisPanel;

/** Create and update some sequence lookahead panels and update them
 * @author eng
 *
 */
public class TestCreateSequencePanels {

	private long start;
	
	private long end;
	
	private final double[] execat = new double[] {0.21,  0.27, 0.36,  0.40,  0.46,  0.51,  0.54,  0.75};
	private final double[] doneat = new double[]       {0.265, 0.355, 0.395, 0.455, 0.505, 0.535, 0.745, 0.8};
	
	LookaheadSequencePanel[] las;

	int nstart = -1;
	int ndone = -1;
	
	/**
	 * @param start
	 * @param end
	 */
	public TestCreateSequencePanels(long start, long end) {
		super();
		this.start = start;
		this.end = end;
		
		las = new LookaheadSequencePanel[8];
		
		 las[0] = createLookaheadPanel("Grp1", 0.10, 0.35, 0.16, 0.31);
		 las[1] = createLookaheadPanel("Grp2", 0.16, 0.51, 0.22, 0.37);
		 las[2] = createLookaheadPanel("Grp3", 0.17, 0.43, 0.28, 0.41);
		 las[3] = createLookaheadPanel("Grp4", 0.29, 0.51, 0.34, 0.45);
		 las[4] = createLookaheadPanel("Grp5", 0.24, 0.61, 0.39, 0.52);
		 las[5] = createLookaheadPanel("Grp6", 0.38, 0.67, 0.47, 0.56);
		 las[6] = createLookaheadPanel("Grp7", 0.31, 0.76, 0.42, 0.64);
		 las[7] = createLookaheadPanel("Grp8", 0.63, 0.91, 0.66, 0.83);
		
		 JPanel p = new JPanel();
		 p.setLayout(new GridLayout(9, 1));
		 p.add(las[0]);
		 p.add(las[1]);
		 p.add(las[2]);
		 p.add(las[3]);
		 p.add(las[4]);
		 p.add(las[5]);
		 p.add(las[6]);
		 p.add(las[7]);
	 
		 
		 TimeAxisPanel tap = new TimeAxisPanel();
		 tap.displayTime(start, end);
		 p.add(tap);
		 
		 JFrame f = new JFrame("Look-ahead sequence display test");
		 f.getContentPane().add(p);
		 f.pack();
		 f.setVisible(true);
	}

	public void updateTime() {
		long time = System.currentTimeMillis();
		for (int i = 0; i < 8; i++) {
			las[i].updateTime(time);
			// see if we should start or end any groups
			double range = end - start;
			long exectime = (long)(execat[i]*range) + start;
			long donetime = (long)(doneat[i]*range) + start;
			
			// past the start but not started yet -> start now
			if (time >= exectime && nstart < i) {
				las[i].sequenceSelected(time);
				nstart = i; 
			}
			// past the end and not done yet -> done or failed now
			if (time > donetime && ndone < i) {
				if (Math.random() > 0.7) 
					las[i].sequenceFailed(time);
				else 
					las[i].sequenceCompleted(time);
				ndone = i;
			}
			
		}
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		long start = System.currentTimeMillis() - 10*60*1000L;
		long end = start + 1*3600*1000L;
		
		TestCreateSequencePanels test = new TestCreateSequencePanels(start, end);
		
		long time = System.currentTimeMillis();
		while (time < end) {
			
			test.updateTime();
			
			try {Thread.sleep(30000L); } catch (InterruptedException e){}
		}
		
		
	}

	// pass in the approx start point and fracs explicity eg s=0.2, w=0.4, s+w< 1.0
	private LookaheadSequencePanel createLookaheadPanel(String name, 
			double f1, double f2, double a1, double a2) { 
		
		double range = end - start;
		
		long fstart = (long)(f1*range) + start;
		long fend = (long)(f2*range) + start;
		long astart = (long)(a1*range) + start;
		long aend = (long)(a2*range) + start;
		
		LookaheadSequencePanel las = 
				new LookaheadSequencePanel(name, start, end, fstart, fend, astart, aend);
		return las;
	}

}
