/**
 * 
 */
package ngat.opsgui.services;

import java.util.List;
import java.util.Vector;

import java.rmi.Naming;
import java.rmi.RemoteException;

import ngat.sms.GroupItem;
import ngat.sms.SynopticModelProvider;
import ngat.sms.Phase2CompositeModel;
import ngat.sms.ExecutionHistorySynopsisModel;
import ngat.sms.ExecutionHistorySynopsis;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;

import ngat.opsgui.perspectives.phase2.Phase2CacheUpdateListener;
import ngat.opsgui.perspectives.phase2.Phase2;

/**
 * Implementors are objects which act as handlers for updates from some service
 * provider. The object is one which must regularly register with its provider.
 * 
 * @author eng
 * 
 */
public class Phase2CacheService extends BasicServiceProvider implements ServiceProvider {

	private String smpUrl;

	private String smaUrl;

	private List<GroupItem> liveCache;

	private List<Phase2CacheUpdateListener> listeners;

	public Phase2CacheService() throws RemoteException {
		super();
		
		liveCache = new Vector<GroupItem>();

		listeners = new Vector<Phase2CacheUpdateListener>();

	}

	public void setSmaUrl(String smaUrl) {
		this.smaUrl = smaUrl;
	}

	public void addPhase2CacheUpdateListener(Phase2CacheUpdateListener l) {
		if (listeners.contains(l))
			return;
		listeners.add(l);
	}

	public void removePhase2CacheUpdateListener(Phase2CacheUpdateListener l) {
		if (!listeners.contains(l))
			return;
		listeners.remove(l);
	}

	/**
	 * @return The name of the service this object registers with.
	 */
	@Override
	public String getServiceProviderName() {
		return "PHASE2";
	}

	/**
	 * Request to register with service provider.
	 * 
	 * @throws Exception
	 *             If registration fails.
	 */
	@Override
	public void registerService() throws Exception {
	}

	// May want to be able to tell the service to unbind from provider - why?
	// public void deregisterService() throws Exception;

	/**
	 * Request object to load archived data from the service provider. The
	 * amount of data is decided by the implementor.
	 * 
	 * @return The number of items received.
	 * @throws Exception
	 *             If the service provider fails to return archived data.
	 */
	@Override
	public int loadServiceArchive() throws Exception {
		System.err.println("P2S: load service archive");

		// lookup phase2cache and or SMP
		//System.err.println("P2S: Lookup: " + smaUrl);
		SynopticModelProvider smp = (SynopticModelProvider) Naming.lookup(smaUrl);
		//System.err.println("P2S: Located SMP: " + smp);

		// grab the phase2 info (listGroups()) and stick in arch cache
		Phase2CompositeModel p2 = smp.getPhase2CompositeModel();

		ExecutionHistorySynopsisModel hist = smp.getHistorySynopsisModel();

		List<GroupItem> groups = p2.listGroups();
		//System.err.println("P2S: checking upto: " + groups.size() + " groups");

		int nr = 0;
		int ng = 0;
		long now = System.currentTimeMillis();
		for (int i = 0; i < groups.size(); i++) {
			GroupItem group = groups.get(i);
			//System.err.println("P2S: Group: " + group.getName());
			ExecutionHistorySynopsis h = hist.getExecutionHistorySynopsis(group.getID(), now);

			int nn = h.getCountExecutions();

			// check if this group is done: Flex and Done
			ITimingConstraint timing = group.getTimingConstraint();
			if ((timing instanceof XFlexibleTimingConstraint) && nn == 0) {
				liveCache.add(group);
				//System.err.println("P2S: Flexible Accepted");
				ng++;
			} else if
				(timing instanceof XMinimumIntervalTimingConstraint) {
				XMinimumIntervalTimingConstraint xmin = (XMinimumIntervalTimingConstraint)timing;
				if (nn < xmin.getMaximumRepeats()) {
					liveCache.add(group);
					//System.err.println("P2S: Interval Accepted");
					ng++;
				} else {
					//System.err.println("P2S: Interval Rejected");
					nr++;
				}
			} else {
				//System.err.println("P2S: Rejected");
				nr++;
			}

		}
		//System.err.println("P2S: Processed: " + groups.size() + " accepted " + ng + " rejected " + nr);
		return ng;
	}

	/**
	 * Request object to broadcast availability information about the managed
	 * service.
	 * 
	 * @param available
	 *            True if the service is available.
	 * @throws Exception
	 */
	@Override
	public void broadcastServiceAvailability(boolean available) {
	}

	/**
	 * Request object to broadcast its next status information.
	 * 
	 * @throws Exception
	 */
	@Override
	public int broadcastStatus() throws Exception {

		// send out the phase2 events via the Phase2CacheUpdateListener i/face
		// e.g. phase2GroupAdded(g)
		//System.err.println("P2S: broadcasting status:");

		for (int i = 0; i < listeners.size(); i++) {
			Phase2CacheUpdateListener l = listeners.get(i);
			for (int j = 0; j < liveCache.size(); j++) {
				GroupItem g = liveCache.get(j);
				try {
					l.phase2GroupAdded(g);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}

		// and Phase2 itself for now
		Phase2 phase2 = Phase2.getPhase2();
		int ls = liveCache.size();
		for (int j = 0; j < liveCache.size(); j++) {
			GroupItem g = liveCache.remove(j);
			try {
				phase2.addGroup(g);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return ls;
	}

}
