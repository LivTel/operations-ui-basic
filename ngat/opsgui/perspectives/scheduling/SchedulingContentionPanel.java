package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.SimpleTimeZone;

import javax.swing.JPanel;

import ngat.opsgui.base.ComponentFactory;
import ngat.phase2.IProposal;
import ngat.sms.GroupItem;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class SchedulingContentionPanel extends JPanel {

	// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
	// -------------------------------------------------------
	// We need a better way to handle the various filters
	// ie some sort of proposal filter class or other
	// sorts of classes we can do filtering with than Proposals.
	//
	// 
	//
	//
	//
	// -------------------------------------------------------
	// TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO TODO
	
	
	
	private static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	private static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	/** Sweep data. */
	private List<SweepEntry> sweeps;

	/** Collection of time series. */
	private TimeSeriesCollection tsc;

	/** Time series to display winning score per sweep. */
	private TimeSeries tsCont;

	/** Time series to display winning score per sweep for NSO groups. */
	private TimeSeries tsNSO;
	
	/** Time series to display winning score per sweep for Cat A groups. */
	private TimeSeries tsCatA;
	
	/** Time series to display winning score per sweep for Cat B groups. */
	private TimeSeries tsCatB;
	
	/** Time series to display winning score per sweep for Cat C groups. */
	private TimeSeries tsCatC;
	
	/** Time series to display winning score per sweep for Cat Z groups. */
	private TimeSeries tsCatZ;
	
	
	/** The chart.*/
	private JFreeChart chart;
	
	public SchedulingContentionPanel(List<SweepEntry> sweeps) {
		super(true);
		this.sweeps = sweeps;
		
		odf.setTimeZone(UTC);
		
		setLayout(new BorderLayout());

		tsc = new TimeSeriesCollection();
		tsCont = new TimeSeries("Total");
		tsNSO = new TimeSeries("NSO");
		tsCatA = new TimeSeries("Cat A");
		tsCatB = new TimeSeries("Cat B");
		tsCatC = new TimeSeries("Cat C");
		tsCatZ = new TimeSeries("Cat Z");
		
		tsc.addSeries(tsCont);
		tsc.addSeries(tsNSO);
		tsc.addSeries(tsCatA);
		tsc.addSeries(tsCatB);
		tsc.addSeries(tsCatC);
		tsc.addSeries(tsCatZ);
		
		chart = ComponentFactory.makeTimeChart(tsc, "Contention Plot","Contention", 0.0, 200.0, odf);
		ChartPanel cp = new ChartPanel(chart);
		cp.setPreferredSize(ComponentFactory.CHART_FULL);
		
		// TODO create filter menu
		
		
		add(cp, BorderLayout.CENTER);
	
	}

	
	
	/**
	 * A generic call used by all schedule panels to recieve new data. It is
	 * called when a new sweep has been processed by the SchedulingPerspective.
	 * FROM SweepDisplay
	 */
	public void updateData() {

		System.err.println("SCP: Update recieved");
		
		SweepEntry sweep = sweeps.get(sweeps.size()-1);
		int nc = sweep.getCandidates().size();
		tsCont.add(new Second(new Date(sweep.getTime())), nc);
		
		// being clever here lets try and filter out stuff
		List<CandidateEntry> candidates = sweep.getCandidates();
		int nNSO = 0;
		int nCatA = 0;
		int nCatB = 0;
		int nCatC = 0;
		int nCatZ = 0;
		for (int ic = 0; ic < candidates.size(); ic++ ){
			CandidateEntry c = candidates.get(ic);
			GroupItem g = c.getGroup();
			IProposal p = g.getProposal();
			int p0 = p.getPriority();
			double p1 = p.getPriorityOffset();
			switch (p0) {
			case IProposal.PRIORITY_A:
				nCatA++;
				break;
			case IProposal.PRIORITY_B:
				nCatB++;
				break;
			case IProposal.PRIORITY_C:
				nCatC++;
				break;
			case IProposal.PRIORITY_Z:
				nCatZ++;
				break;
			}
			
			if (g.getProgram().getName().equals("XNSO"))
				nNSO++;
		}
		tsNSO.add(new Second(new Date(sweep.getTime())), nNSO);
		tsCatA.add(new Second(new Date(sweep.getTime())), nCatA);
		tsCatB.add(new Second(new Date(sweep.getTime())), nCatB);
		tsCatC.add(new Second(new Date(sweep.getTime())), nCatC);
		tsCatZ.add(new Second(new Date(sweep.getTime())), nCatZ);	
	}

       
}