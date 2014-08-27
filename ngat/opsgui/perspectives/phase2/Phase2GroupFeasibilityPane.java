/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.JLabel;

import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.SkyBrightnessCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.base.Resources;
import ngat.opsgui.components.LinePanel;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeAxisPanel;
import ngat.opsgui.util.TimeDisplayController;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XAirmassConstraint;
import ngat.phase2.XEphemerisTimingConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.phase2.XSkyBrightnessConstraint;
import ngat.sms.ComponentSet;
import ngat.sms.GroupItem;
import ngat.sms.ObservingConstraintAdapter;

/**
 * @author eng
 * 
 */
public class Phase2GroupFeasibilityPane extends JPanel implements GroupSelectionListener {

	private static final Dimension LINE_SIZE = new Dimension(600, 35);
	private static final Dimension H_SIZE = new Dimension(400, 30);

	private static final int ELEVATION_BELOW_HORIZON = 1;
	private static final int ELEVATION_BELOW_DOME = 2;
	private static final int ELEVATION_BELOW_AIRMASS_LIMIT = 3;
	private static final int ELEVATION_ABOVE_AIRMASS_LIMIT = 4;

	private static final int SKYB_OKAY = 1;
	private static final int SKYB_TOO_BRIGHT = 2;

	private static final int INSIDE_WINDOW = 1;
	private static final int OUTSIDE_WINDOW = 2;

	private static final int FEASIBLE = 1;
	private static final int NON_FEASIBLE = 2;

	private static final Color ELEVATION_BELOW_HORIZON_COLOR = Color.gray;
	private static final Color ELEVATION_BELOW_DOME_COLOR = Color.cyan.darker();
	private static final Color ELEVATION_BELOW_AIRMASS_LIMIT_COLOR = Color.cyan.brighter();
	private static final Color ELEVATION_ABOVE_AIRMASS_LIMIT_COLOR = Color.pink;

	private static final Color SKYB_OKAY_COLOR = Color.pink;
	private static final Color SKYB_TOO_BRIGHT_COLOR = Color.cyan.darker();

	private static final Color INSIDE_WINDOW_COLOR = Color.pink;
	private static final Color OUTSIDE_WINDOW_COLOR = Color.gray;

	private static final Color FEASIBLE_COLOR = Color.pink;
	private static final Color NON_FEASIBLE_COLOR = Color.gray;

	private TimeAxisPanel timeAxisPanel;

	private StatusHistoryPanel airmassPanel;

	private StatusHistoryPanel skybPanel;

	private StatusHistoryPanel skybPanel2;

	private StatusHistoryPanel windowPanel;

	private StatusHistoryPanel rotatorPanel;

	private StatusHistoryPanel haPanel;

	private StatusHistoryPanel totalPanel;

	private JLabel groupNameLabel;

	private ISite site;

	private AstrometrySiteCalculator astro;

	private SkyBrightnessCalculator skyb;

	private TimeDisplayController tdc;

	private GroupItem displayGroup;

	private long displayStart;

	private long displayEnd;

	/**
     * 
     */
	public Phase2GroupFeasibilityPane(ISite site) {
		super(true);
		this.site = site;

		tdc = new TimeDisplayController(24 * 3600 * 1000L);

		astro = new BasicAstrometrySiteCalculator(site);
		skyb = new SkyBrightnessCalculator(site);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		createNavigationPanel();

		airmassPanel = createAirmassPanel();

		skybPanel = createSkybPanel();

		skybPanel2 = createSkybPanel2();

		windowPanel = createWindowPanel();

		totalPanel = createTotalPanel();

		timeAxisPanel = createTimeAxis();

	}

	private void createNavigationPanel() {
		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);

		JButton lbutton = ComponentFactory.makeSmallButton(Resources.getIcon("pan.left.icon"));
		lbutton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {

				long windowSize = displayEnd - displayStart;
				long panSize = (long) (windowSize * 0.75);
				long start = displayStart - panSize;
				long end = displayEnd - panSize;

				displayGroup(start, end);
				tdc.requestPan(-0.75);
				repaint();
			}
		});
		line.add(lbutton);

		JButton rbutton = ComponentFactory.makeSmallButton(Resources.getIcon("pan.right.icon"));
		rbutton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				long windowSize = displayEnd - displayStart;
				long panSize = (long) (windowSize * 0.75);
				long start = displayStart + panSize;
				long end = displayEnd + panSize;

				displayGroup(start, end);
				tdc.requestPan(0.75);
				repaint();
			}
		});
		line.add(rbutton);

		JButton zmbutton = ComponentFactory.makeSmallButton(Resources.getIcon("zoom.out.icon"));
		zmbutton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				long windowSize = displayEnd - displayStart;
				long newWindowSize = (long) (windowSize * 2.0);
				long windowMid = (long) (0.5 * (displayStart + displayEnd));
				long start = windowMid - (long) (0.5 * newWindowSize);
				long end = start + newWindowSize;

				displayGroup(start, end);
				tdc.requestZoom(2.0);
				repaint();
			}
		});
		line.add(zmbutton);

		JButton zpbutton = ComponentFactory.makeSmallButton(Resources.getIcon("zoom.in.icon"));
		zpbutton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {

				long windowSize = displayEnd - displayStart;
				long newWindowSize = (long) (windowSize * 0.5);
				long windowMid = (long) (0.5 * (displayStart + displayEnd));
				long start = windowMid - (long) (0.5 * newWindowSize);
				long end = start + newWindowSize;

				displayGroup(start, end);
				tdc.requestZoom(0.5);
				repaint();
			}
		});
		line.add(zpbutton);

		JButton rtbutton = ComponentFactory.makeSmallButton(Resources.getIcon("reset.time.icon"));
		rtbutton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {

				long start = System.currentTimeMillis();
				long end = start + 24 * 3600 * 1000L;

				displayGroup(start, end);
				repaint();

			}
		});
		line.add(rtbutton);

		groupNameLabel = ComponentFactory.makeUnsizedDisplayLabel("");
		line.add(groupNameLabel);

		add(line);
	}

	private TimeAxisPanel createTimeAxis() {

		TimeAxisPanel panel = new TimeAxisPanel();
		panel.setPreferredSize(H_SIZE);
		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);
		line.add(ComponentFactory.makeHugeLabel("Time"));
		line.add(panel);
		add(line);
		tdc.addTimeDisplay(panel);

		return panel;

	}

	private StatusHistoryPanel createAirmassPanel() {

		StateColorMap airmassMap = new StateColorMap(Color.gray, "UNKNOWN");
		airmassMap.addColorLabel(ELEVATION_BELOW_HORIZON, ELEVATION_BELOW_HORIZON_COLOR, "SET");
		airmassMap.addColorLabel(ELEVATION_BELOW_DOME, ELEVATION_BELOW_DOME_COLOR, "DOME");
		airmassMap.addColorLabel(ELEVATION_BELOW_AIRMASS_LIMIT, ELEVATION_BELOW_AIRMASS_LIMIT_COLOR, "AIRMASS");
		airmassMap.addColorLabel(ELEVATION_ABOVE_AIRMASS_LIMIT, ELEVATION_ABOVE_AIRMASS_LIMIT_COLOR, "OKAY");
		StatusHistoryPanel panel = new StatusHistoryPanel(tdc);
		panel.setPreferredSize(H_SIZE);
		panel.setMap(airmassMap);

		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);
		line.add(ComponentFactory.makeHugeLabel("Airmass constraint"));
		line.add(panel);
		add(line);
		tdc.addTimeDisplay(panel);

		return panel;
	}

	private StatusHistoryPanel createWindowPanel() {
		StateColorMap windowMap = new StateColorMap(Color.gray, "UNKNOWN");
		windowMap.addColorLabel(INSIDE_WINDOW, INSIDE_WINDOW_COLOR, "OKAY");
		windowMap.addColorLabel(OUTSIDE_WINDOW, OUTSIDE_WINDOW_COLOR, "WINDOW");
		StatusHistoryPanel panel = new StatusHistoryPanel(tdc);
		panel.setPreferredSize(H_SIZE);
		panel.setMap(windowMap);

		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);
		line.add(ComponentFactory.makeHugeLabel("Feasible window"));
		line.add(panel);
		add(line);
		tdc.addTimeDisplay(panel);

		return panel;
	}

	private StatusHistoryPanel createTotalPanel() {
		StateColorMap totalMap = new StateColorMap(Color.gray, "UNKNOWN");
		totalMap.addColorLabel(FEASIBLE, FEASIBLE_COLOR, "OKAY");
		totalMap.addColorLabel(NON_FEASIBLE, NON_FEASIBLE_COLOR, "INFEASIBLE");
		StatusHistoryPanel panel = new StatusHistoryPanel(tdc);
		panel.setPreferredSize(H_SIZE);
		panel.setMap(totalMap);

		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);
		line.add(ComponentFactory.makeHugeLabel("Total"));
		line.add(panel);
		add(line);
		tdc.addTimeDisplay(panel);

		return panel;
	}

	private StatusHistoryPanel createSkybPanel() {
		StateColorMap skybMap = new StateColorMap(Color.gray, "UNKNOWN");
		skybMap.addColorLabel(SKYB_OKAY, SKYB_OKAY_COLOR, "OKAY");
		skybMap.addColorLabel(SKYB_TOO_BRIGHT, SKYB_TOO_BRIGHT_COLOR, "SKYB");
		StatusHistoryPanel panel = new StatusHistoryPanel(tdc);
		panel.setPreferredSize(H_SIZE);
		panel.setMap(skybMap);

		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);
		line.add(ComponentFactory.makeHugeLabel("Sky constraint"));
		line.add(panel);
		add(line);
		tdc.addTimeDisplay(panel);

		return panel;
	}

	private StatusHistoryPanel createSkybPanel2() {
		StateColorMap skybMap2 = Resources.getColorMap("sky.brightness.colors");
	
		StatusHistoryPanel panel = new StatusHistoryPanel(tdc);
		panel.setPreferredSize(H_SIZE);
		panel.setMap(skybMap2);

		LinePanel line = ComponentFactory.makeLinePanel();
		line.setPreferredSize(LINE_SIZE);
		line.add(ComponentFactory.makeHugeLabel("Sky brightness"));
		line.add(panel);
		add(line);
		tdc.addTimeDisplay(panel);

		return panel;
	}

	/**
	 * Display the group between the specified times.
	 * 
	 * @param group
	 *            The group whose feasibility is to be displayed.
	 * @param start
	 *            Start time.
	 * @param end
	 *            End time.
	 */
	public void displayGroup(long start, long end) {

		if (displayGroup == null)
			return;

		displayStart = start;
		displayEnd = end;

		// TODO clear all the panels...
		airmassPanel.clearHistory();
		skybPanel.clearHistory();
		skybPanel2.clearHistory();
		windowPanel.clearHistory();
		totalPanel.clearHistory();

		try {

			groupNameLabel.setText(displayGroup.getName());

			ISequenceComponent root = displayGroup.getSequence();
			ComponentSet cs = new ComponentSet(root);
			// quick fudge lets just look at the first target and ignore others
			Iterator<ITarget> targets = cs.listTargets();
			ITarget target = null;
			TargetTrackCalculator track = null;

			if (targets != null) {
				if (targets.hasNext()) {
					target = targets.next();
					track = new BasicTargetCalculator(target, site);
				}
			}

			ObservingConstraintAdapter oca = new ObservingConstraintAdapter(displayGroup);
			XAirmassConstraint xair = oca.getAirmassConstraint();
			XSkyBrightnessConstraint xsky = oca.getSkyBrightnessConstraint();

			long time = start;
			long delta = (long) ((end - start) / 800.0);
			while (time < end) {

				// AIRMASS
				boolean airmassOkay = false;

				if (target == null) {
					airmassPanel.addHistory(time, ELEVATION_ABOVE_AIRMASS_LIMIT);
					airmassOkay = true;
				} else {
					Coordinates c = track.getCoordinates(time);
					double alt = astro.getAltitude(c, time);
					double air = 1.0 / Math.cos(0.5 * Math.PI - alt);

					if (alt < 0.0) {
						airmassPanel.addHistory(time, ELEVATION_BELOW_HORIZON);
					} else if (alt < Math.toRadians(22.0)) {
						airmassPanel.addHistory(time, ELEVATION_BELOW_DOME);
					} else {
						if (xair == null) {
							airmassOkay = true;
							airmassPanel.addHistory(time, ELEVATION_ABOVE_AIRMASS_LIMIT);
						} else {
							double rair = xair.getMaximumAirmass();
							if (air > rair) {
								airmassPanel.addHistory(time, ELEVATION_BELOW_AIRMASS_LIMIT);
							} else {
								airmassPanel.addHistory(time, ELEVATION_ABOVE_AIRMASS_LIMIT);
								airmassOkay = true;
							}
						}
					}
				}

				// SKYBRIGHTNESS
				boolean skybOkay = false;
				if (xsky == null) {
					skybOkay = true;
					skybPanel.addHistory(time, SKYB_OKAY);
				} else {
					// TODO does that make sense - if no target then skyb is ok
					// ?
					// in reallity we observe whereever the scope happens to be
					// pointing but we cant know
					// this in advance so should we infact assume some standard
					// bit of sky and use that ?
					if (target == null) {
						skybPanel.addHistory(time, SKYB_OKAY);
						skybOkay = true;
					} else {
						int isky = skyb.getSkyBrightnessCriterion(track, time);
						double sky = SkyBrightnessCalculator.getSkyBrightness(isky);
						double rsky = SkyBrightnessCalculator.getSkyBrightness(xsky.getSkyBrightnessCategory());
						if (sky <= rsky) {
							skybPanel.addHistory(time, SKYB_OKAY);
							skybOkay = true;
						} else
							skybPanel.addHistory(time, SKYB_TOO_BRIGHT);

						skybPanel2.addHistory(time, isky);
					}
				}

				// TIMING
				boolean winOkay = timingCheck(displayGroup, time);
				if (winOkay)
					windowPanel.addHistory(time, INSIDE_WINDOW);
				else
					windowPanel.addHistory(time, OUTSIDE_WINDOW);

				// TOTAL
				if (skybOkay && airmassOkay && winOkay)
					totalPanel.addHistory(time, FEASIBLE);
				else
					totalPanel.addHistory(time, NON_FEASIBLE);

				time += delta;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private boolean timingCheck(GroupItem group, long time) {

		ITimingConstraint tc = group.getTimingConstraint();

		if (tc == null)
			return false;

		if (tc instanceof XFlexibleTimingConstraint) {
			XFlexibleTimingConstraint xflex = (XFlexibleTimingConstraint) tc;

			if (xflex.getActivationDate() > time)
				return false;

			if (xflex.getExpiryDate() < time)
				return false;

			return true;
		}

		if (tc instanceof XMonitorTimingConstraint) {

			XMonitorTimingConstraint xmon = (XMonitorTimingConstraint) tc;

			long startDate = xmon.getStartDate();
			long endDate = xmon.getEndDate();
			long period = xmon.getPeriod();
			long window = xmon.getWindow();
			double floatFraction = (double) window / (double) period;

			double fPeriod = (double) (time - startDate) / (double) period;
			double iPeriod = Math.rint(fPeriod);

			if (startDate > time)
				return false;

			if (endDate < time)
				return false;

			long startFloat = startDate + (long) ((iPeriod - floatFraction / 2.0) * period);
			long endFloat = startDate + (long) ((iPeriod + floatFraction / 2.0) * period);

			if ((startFloat > time) || (endFloat < time))
				return false;

			return true;
		}

		if (tc instanceof XMinimumIntervalTimingConstraint) {

			XMinimumIntervalTimingConstraint xmin = (XMinimumIntervalTimingConstraint) tc;

			if (xmin.getStart() > time)
				return false;
			if (xmin.getEnd() < time)
				return false;

			return true;

		}

		if (tc instanceof XEphemerisTimingConstraint) {

			XEphemerisTimingConstraint xephem = (XEphemerisTimingConstraint) tc;
			// TODO SOON we will allow this but need to just check in-window
			// if (countExec >= 1)
			// return fail(group, "EPHEM_ALREADY_DONE");

			if (xephem.getStart() > time)
				return false;

			if (xephem.getEnd() < time)
				return false;

			// work out the window periods

			long startDate = xephem.getStart();
			long endDate = xephem.getEnd();
			long period = xephem.getCyclePeriod();
			double phase = xephem.getPhase();
			double window = xephem.getWindow();

			// double fperiod = Math.floor((time - startDate) / period);

			double fperiod = Math.floor(((double) time - (double) startDate - phase * period + window / 2.0)
					/ period);

			long startWindow = startDate + (long) ((fperiod + phase) * period - window / 2.0);
			long endWindow = startDate + (long) ((fperiod + phase) * period + window / 2.0);

			if (startWindow > time || endWindow < time)
				return false;

			return true;

		}

		if (tc instanceof XFixedTimingConstraint) {

			XFixedTimingConstraint fixed = (XFixedTimingConstraint) tc;

			long start = fixed.getStartTime();
			long end = fixed.getEndTime();
			// long slack = fixed.getSlack() / 2;

			// if we are in its start window, its doable
			long startWindow = start;// - slack;
			long endWindow = end;// + slack;
			if ((startWindow > time) || (endWindow < time))
				return false;

			return true;
		}

		// all tests passed
		return true;

	}

	/**
	 * Select a group to display. Use the period (now, now+24h).
	 * 
	 * @param group
	 *            The group to display.
	 */
	public void updateGroup(GroupItem group) {
		displayGroup = group;
		displayGroup(System.currentTimeMillis(), System.currentTimeMillis() + 24 * 3600 * 1000L);
		repaint();
	}

	@Override
	public void groupSelectionChanged(final GroupItem group) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateGroup(group);
			}
		});

	}

}
