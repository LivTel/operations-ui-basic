/**
 * 
 */
package ngat.opsgui.simulation;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import ngat.phase2.IRotatorConfig;
import ngat.phase2.XAirmassConstraint;
import ngat.phase2.XDetectorConfig;
import ngat.phase2.XExecutiveComponent;
import ngat.phase2.XExtraSolarTarget;
import ngat.phase2.XFilterDef;
import ngat.phase2.XFilterSpec;
import ngat.phase2.XGroup;
import ngat.phase2.XImagerInstrumentConfig;
import ngat.phase2.XInstrumentConfigSelector;
import ngat.phase2.XIteratorComponent;
import ngat.phase2.XIteratorRepeatCountCondition;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.phase2.XMultipleExposure;
import ngat.phase2.XProposal;
import ngat.phase2.XRotatorConfig;
import ngat.phase2.XSeeingConstraint;
import ngat.phase2.XSlew;
import ngat.phase2.XTag;
import ngat.phase2.XUser;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleDespatcher;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusProvider;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.bds.TestScheduleItem;

/**
 * @author eng
 * 
 */
public class SchedulingUpdateSimulator extends UnicastRemoteObject implements SchedulingStatusProvider, Runnable {

	static final String[] plist = new String[] { "JL12B01", "Pl12A15", "CL12B17", "PL12B02", "NSO_Prirority2" };

	static final String[] ulist = new String[] { "Jones.Arnold", "Smith.Cuthbert", "Nuisance.Andy",
			"Lapalmero.Alberto", "Mackenzie.Galileo" };

	static final String[] tlist = new String[] { "JMU", "PATT", "NSO", "CCI", "IAC" };

	static final String[] glist = new String[] { "BD+", "SDSS-", "NGC", "K12+23", "UJ-" };

	static final String[] elist = new String[] {"TARGET_LOW", "MONITOR_WINDOW", "SEEING", "SKY_BRIGHT", "EXPIRED"};
	
	private int n;

	List<GroupItem> groups;

	List<SchedulingStatusUpdateListener> listeners;

	private long interval;

	/**
	 * 
	 */
	public SchedulingUpdateSimulator(int n) throws Exception {
		super();
		this.n = n;

		long now = System.currentTimeMillis();

		listeners = new Vector<SchedulingStatusUpdateListener>();
		groups = new Vector<GroupItem>();

		List<XProposal> props = new Vector<XProposal>();
		List<XUser> users = new Vector<XUser>();
		List<XTag> tags = new Vector<XTag>();

		// make some props
		for (int ip = 0; ip < 5; ip++) {
			XProposal p = new XProposal(plist[ip]);
			p.setActivationDate(now - 30 * 3600 * 1000L);
			p.setExpiryDate(now + 30 * 3600 * 1000L);
			p.setEnabled(true);
			p.setID((long) (Math.random() * 1000));
			p.setPriority((int) (Math.random() * 5.0));
			props.add(p);
		}

		// make some tags
		for (int it = 0; it < 5; it++) {
			XTag t = new XTag();
			t.setName(tlist[it]);
			t.setID((long) (Math.random() * 10));
			tags.add(t);
		}

		// make some users
		for (int iu = 0; iu < 5; iu++) {
			XUser u = new XUser(ulist[iu]);
			u.setID((long) (Math.random() * 200));
			users.add(u);
		}

		for (int ig = 0; ig < n; ig++) {

			int nn = (int) (Math.random() * 5.0);
			XGroup g = new XGroup();
			g.setName(glist[nn] + "" + (int) (Math.random() * 60.) + "-" + (int) (Math.random() * 60.0));

			g.setActive(true);
			g.setID((long) (Math.random() * 20000));

			XMonitorTimingConstraint timing = new XMonitorTimingConstraint(now - 30 * 3600 * 1000L,
					now + 30 * 3600 * 1000L, (long) (Math.random() * 24 * 3600 * 1000),
					(long) (Math.random() * 3600 * 1000));
			g.setTimingConstraint(timing);

			List oc = new Vector();
			oc.add(new XSeeingConstraint(0.5 + Math.random() * 2.0));
			oc.add(new XAirmassConstraint(1.0 + Math.random()));
			g.setObservingConstraints(oc);

			XIteratorComponent root = new XIteratorComponent("root", new XIteratorRepeatCountCondition(1));

			XExtraSolarTarget target = new XExtraSolarTarget("star");
			target.setRa(Math.random() * Math.PI * 2.0);
			target.setDec((Math.random() - 0.5) * Math.PI);
			XRotatorConfig rotate = new XRotatorConfig(IRotatorConfig.SKY, 0.0, "RATCAM");
			root.addElement((new XExecutiveComponent("slew", new XSlew(target, rotate, false))));
			XImagerInstrumentConfig config = new XImagerInstrumentConfig("ratconf");

			XFilterSpec fs = new XFilterSpec();
			fs.addFilter(new XFilterDef("blue"));
			fs.addFilter(new XFilterDef("green"));
			config.setFilterSpec(fs);
			
			XDetectorConfig detector = new XDetectorConfig();
			detector.setXBin(2);
			detector.setYBin(2);
			config.setDetectorConfig(detector);
			config.setInstrumentName("RATCAM");
			root.addElement(new XExecutiveComponent("config", new XInstrumentConfigSelector(config)));
			root.addElement(new XExecutiveComponent("mult", new XMultipleExposure(Math.random() * 60000.0, (int) (Math
					.random() * 6))));

			int jp = (int) (Math.random() * 5.0);
			int jt = (int) (Math.random() * 5.0);
			int ju = (int) (Math.random() * 5.0);

			GroupItem group = new GroupItem(g, root);
			group.setProposal(props.get(jp));
			group.setTag(tags.get(jt));
			group.setUser(users.get(ju));

			System.err.println("Create group item: " + group);
			groups.add(group);

		}

	}

	public void run(long interval) {
		this.interval = interval;
		(new Thread(this)).start();
	}

	@Override
	public void run() {

		int is = 0;

		while (true) {
			is++;
			try {
				Thread.sleep(interval);
			} catch (InterruptedException ix) {
			}

			System.err.println("Start sweep: " + is);

			double frac = 0.25 + Math.random() * 0.25;

			notifyListenersSweepStart(is);

			if (Math.random() > 0.2) {
				GroupItem winner = null;
				double winningScore = -99.9;
				for (int i = 0; i < n; i++) {

					GroupItem g = groups.get(i);

					if (Math.random() < frac) {
						// candidate
						try {
							Thread.sleep(100);
						} catch (InterruptedException ix) {
						}
						double score = Math.random() * 2.0;
						notifyListenersCandidate(g, score);
						if (score > winningScore) {
							winningScore = score;
							winner = g;
						}
					} else {
						int ie = (int)(Math.random()*elist.length);
						notifyListenersReject(g, elist[ie]);
					}

				}

				TestScheduleItem sched = new TestScheduleItem(winner, null);
				sched.setScore(winningScore);
				notifyListenersSweepEnd(sched);
			} else {
				notifyListenersSweepEnd(null);
			}

		}

	}

	private void notifyListenersSweepStart(int nsweep) {
		System.err.println("Notify - SweepStart");

		for (int il = 0; il < listeners.size(); il++) {
			SchedulingStatusUpdateListener l = listeners.get(il);
			try {
				l.scheduleSweepStarted(System.currentTimeMillis(), nsweep);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void notifyListenersCandidate(GroupItem g, double score) {
		System.err.println("Notify - Candidate: " + g);
		for (int il = 0; il < listeners.size(); il++) {
			SchedulingStatusUpdateListener l = listeners.get(il);
			try {

				l.candidateAdded("", g, null, score, 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	
	
	private void notifyListenersReject(GroupItem g, String reason) {
		System.err.println("Notify - Reject: " + g);
		for (int il = 0; il < listeners.size(); il++) {
			SchedulingStatusUpdateListener l = listeners.get(il);
			try {

				l.candidateRejected("", g, reason);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	private void notifyListenersSweepEnd(ScheduleItem sched) {
		System.err.println("Notify - SweepEnd: " + sched);
		for (int il = 0; il < listeners.size(); il++) {
			SchedulingStatusUpdateListener l = listeners.get(il);
			try {
				l.candidateSelected(System.currentTimeMillis(), sched);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void addSchedulingUpdateListener(SchedulingStatusUpdateListener l) throws RemoteException {
		if (listeners.contains(l))
			return;
		listeners.add(l);
	}

	@Override
	public ScheduleDespatcher getDespatcher() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> listCandidateQueues() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeSchedulingUpdateListener(SchedulingStatusUpdateListener l) throws RemoteException {
		if (!listeners.contains(l))
			return;
		listeners.remove(l);

	}

	public static void main(String[] args) {

		try {
			SchedulingUpdateSimulator sim = new SchedulingUpdateSimulator(100);

			sim.run(10000L);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
