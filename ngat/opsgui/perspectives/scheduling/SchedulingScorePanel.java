package ngat.opsgui.perspectives.scheduling;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SimpleTimeZone;

import javax.swing.JPanel;

import ngat.opsgui.base.ComponentFactory;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

public class SchedulingScorePanel extends JPanel implements TagListUpdateListener {

	private static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");
	private static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	/** Associates a group ID with a graph TimeSeries. */
	private Map<Long, TimeSeries> tagMap;

	/** Sweep data. */
	private List<SweepEntry> sweeps;

	/** The list of tagged groups. */
	private TagList tagList;

	/** Collection of time series. */
	private TimeSeriesCollection tsc;

	/** Time series to display winning score per sweep. */
	private TimeSeries tsWin;

	/** The chart.*/
	private JFreeChart chart;
	
	public SchedulingScorePanel(List<SweepEntry> sweeps, TagList tagList) {
		super(true);
		this.sweeps = sweeps;
		this.tagList = tagList;

		odf.setTimeZone(UTC);
		
		setLayout(new BorderLayout());

		tagMap = new HashMap<Long, TimeSeries>();

		tsc = new TimeSeriesCollection();
		tsWin = new TimeSeries("Winner");
		tsc.addSeries(tsWin);
		
		chart = ComponentFactory.makeTimeChart(tsc, "Scores","Score", 0.0, 5.0, odf);
		ChartPanel cp = new ChartPanel(chart);
		cp.setPreferredSize(ComponentFactory.CHART_FULL);
		
		add(cp, BorderLayout.CENTER);
		
	}

	/**
	 * A generic call used by all schedule panels to recieve new data. It is
	 * called when a new sweep has been processed by the SchedulingPerspective.
	 * FROM SweepDisplay
	 */
	public void updateData(ScheduleItem sched) {

		// TODO we really need to return a scheduleitem with a null group rather than null SI.
		if (sched == null)
			return;
		
		System.err.println("SSP: Update recieved: "+sched.getGroup()+" : "+sched.getScore());
		
		// add a data point to the winning score series. (time = sweeptime,
		// value = rank-1 score)
		SweepEntry sweep = sweeps.get(sweeps.size() - 1);
		tsWin.add(new Second(new Date(sweep.getTime())), sched.getScore());

		// for each group in the TagList, try to associate a series to it
		List<GroupItem> tags = tagList.getTaggedGroups();
		for (int i = 0; i < tags.size(); i++) {
			GroupItem group = tags.get(i);
			TimeSeries ts = tagMap.get(group.getID());
			if (ts != null) {
				// check if the group appears in this sweep (it may be a reject
				// or a candidate)
				CandidateEntry entry = sweep.findCandidate(group.getID());

				// if its a candidate find its score and update the series (t =
				// sweeptime, value = groups score or 0)
				if (entry != null) {
					ts.add(new Second(new Date(sweep.getTime())), entry.getScore());
				} else {
					ts.add(new Second(new Date(sweep.getTime())), 0.0);
				}
			}
		}

	}

	@Override
	public void groupTagged(GroupItem group) throws Exception {
		// a new group was added to the taglist
		System.err.println("SSP: Group was tagged: "+group.getName());
		
		// create and map a series for it
		if (! tagMap.containsKey(group.getID())) {

			TimeSeries ts = new TimeSeries(group.getName());
			
			tsc.addSeries(ts);
			tagMap.put(group.getID(), ts);
			
			// go thro all sweeps upto latest and add a data point for it
			for (int is = 0; is < sweeps.size(); is++) {
				SweepEntry sweep = sweeps.get(is);

				// check if the group appears in this sweep (it may be a reject
				// or a candidate)
				CandidateEntry entry = sweep.findCandidate(group.getID());

				// if its a candidate find its score and update the series (t =
				// sweeptime, value = groups score or 0)
				if (entry != null) {
					ts.add(new Second(new Date(sweep.getTime())), entry.getScore());
				} else {
					ts.add(new Second(new Date(sweep.getTime())), 0.0);
				}
			}
		}
	}

	@Override
	public void groupUntagged(GroupItem group) throws Exception {

		// find the associated series, remove from chart
		if (tagMap.containsKey(group.getID())) {
			TimeSeries ts = tagMap.get(group.getID());
			tsc.removeSeries(ts);
			// TODO hopefully the legend entry will disappear also ?

		}
		// remove map entry
		tagMap.remove(group.getID());

	}

	@Override
	public void tagsCleared() throws Exception {

		// remove all tag series from chart.
		Iterator<Long> series = tagMap.keySet().iterator();
		while (series.hasNext()) {
			long gid = series.next();
			TimeSeries ts = tagMap.get(gid);
			tsc.removeSeries(ts);
			// TODO hopefully the legend entry will disappear also
		}

		// remove all map entries
		tagMap.clear();

	}

}