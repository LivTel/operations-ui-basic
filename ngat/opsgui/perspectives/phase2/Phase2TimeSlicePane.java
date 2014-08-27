/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Iterator;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.SkyBrightnessCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.TimeSlicePanel;
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
 * Displays a group's feasibility as a time-slice display.
 * 
 * @author eng
 * 
 */
public class Phase2TimeSlicePane extends JPanel implements GroupSelectionListener {

	private static final int FEASIBLE = 1;
	private static final int NON_FEASIBLE = 2;

	private static final Color FEASIBLE_COLOR = Color.pink;
	private static final Color NON_FEASIBLE_COLOR = Color.gray;

	private GroupItem displayGroup;

	private ISite site;

	private AstrometrySiteCalculator astro;

	private SkyBrightnessCalculator skyb;

	private TimeSlicePanel tsp;

	/**
	 * 
	 */
	public Phase2TimeSlicePane(ISite site) {
		super(true);
		this.site = site;

		setLayout(new BorderLayout());

		tsp = new TimeSlicePanel("Group Feasibility", System.currentTimeMillis() - 7 * 24 * 3600 * 1000L, 18, 8, 14);
		tsp.setPreferredSize(new Dimension(600, 500));
		/*
		 * StateColorMap skybMap2 = new StateColorMap(Color.gray, "UNKNOWN");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.DARK,
		 * TrackingPerspective.DARK_COLOR, "DARK");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.MAG_0P75,
		 * TrackingPerspective.MAG_0p75_COLOR, "0.75");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.MAG_1P5,
		 * TrackingPerspective.MAG_1p5_COLOR, "1.5");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.MAG_2,
		 * TrackingPerspective.MAG_2_COLOR, "2.0");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.MAG_4,
		 * TrackingPerspective.MAG_4_COLOR, "4.0");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.MAG_6,
		 * TrackingPerspective.MAG_6_COLOR, "6.0");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.MAG_10,
		 * TrackingPerspective.MAG_10_COLOR, "10.0");
		 * skybMap2.addColorLabel(XSkyBrightnessConstraint.DAYTIME,
		 * TrackingPerspective.DAY_COLOR, "DAY");
		 * 
		 * 
		 * tsp.setMap(skybMap2);
		 */

		StateColorMap totalMap = new StateColorMap(Color.gray, "UNKNOWN");
		totalMap.addColorLabel(FEASIBLE, FEASIBLE_COLOR, "OKAY");
		totalMap.addColorLabel(NON_FEASIBLE, NON_FEASIBLE_COLOR, "INFEASIBLE");
		tsp.setMap(totalMap);
		add(tsp, BorderLayout.CENTER);

		astro = new BasicAstrometrySiteCalculator(site);
		skyb = new SkyBrightnessCalculator(site);
	}

	public void updateGroup(GroupItem group) {
		displayGroup = group;

		long start = System.currentTimeMillis() - 7 * 24 * 3600 * 1000L;
		long end = System.currentTimeMillis() + 7 * 24 * 3600 * 1000L;
		long delta = (long) ((end - start) / 7000.0);

		try {

			ISequenceComponent root = displayGroup.getSequence();
			ComponentSet cs = null;
			try {
				cs = new ComponentSet(root);
			} catch (Exception x) {
				x.printStackTrace();
				return;
			}
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
			while (time < end) {

				/*
				 * int isky = 0; try { isky =
				 * skyb.getSkyBrightnessCriterion(track, time);
				 * tsp.addHistory(time, isky); } catch (Exception e) {
				 * 
				 * } time += (end - start) / 7000;
				 */

				// AIRMASS
				boolean airmassOkay = false;

				if (target == null) {

					airmassOkay = true;
				} else {
					Coordinates c = track.getCoordinates(time);
					double alt = astro.getAltitude(c, time);
					double air = 1.0 / Math.cos(0.5 * Math.PI - alt);

					if (alt < 0.0) {

					} else if (alt < Math.toRadians(22.0)) {

					} else {
						if (xair == null) {
							airmassOkay = true;

						} else {
							double rair = xair.getMaximumAirmass();
							if (air > rair) {

							} else {

								airmassOkay = true;
							}
						}
					}
				}

				// SKYBRIGHTNESS
				boolean skybOkay = false;
				if (xsky == null) {
					skybOkay = true;

				} else {
					// TODO does that make sense - if no target then skyb is ok
					// ?
					// in reallity we observe whereever the scope happens to be
					// pointing but we cant know
					// this in advance so should we infact assume some standard
					// bit of sky and use that ?
					if (target == null) {

						skybOkay = true;
					} else {
						int isky = skyb.getSkyBrightnessCriterion(track, time);
						double sky = SkyBrightnessCalculator.getSkyBrightness(isky);
						double rsky = SkyBrightnessCalculator.getSkyBrightness(xsky.getSkyBrightnessCategory());
						if (sky <= rsky) {

							skybOkay = true;
						}

					}
				}

				// TIMING
				boolean winOkay = timingCheck(displayGroup, time);

				// TOTAL
				if (skybOkay && airmassOkay && winOkay)
					tsp.addHistory(time, FEASIBLE);
				else
					tsp.addHistory(time, NON_FEASIBLE);

				time += delta;

			}
			repaint();
		} catch (Exception x) {
			x.printStackTrace();
			return;
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
