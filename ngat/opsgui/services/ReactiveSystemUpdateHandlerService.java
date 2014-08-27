/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.rcs.ers.CriterionUpdateEvent;
import ngat.rcs.ers.FilterUpdateEvent;
import ngat.rcs.ers.ReactiveEvent;
import ngat.rcs.ers.ReactiveSystemArchive;
import ngat.rcs.ers.ReactiveSystemMonitor;
import ngat.rcs.ers.ReactiveSystemUpdateListener;
import ngat.rcs.ers.RuleUpdateEvent;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 * 
 */
public class ReactiveSystemUpdateHandlerService extends BasicServiceProvider
		implements ServiceProvider, ReactiveSystemUpdateListener {

	/** Reactive System Monitor. */
	private String rsmUrl;

	/** Reactive System Archive. */
	private String rsaUrl;

	private long lookBackTime;

	private List<ReactiveEvent> liveCache;
	private List<ReactiveEvent> archiveCache;
	private List<ReactiveSystemUpdateListener> listeners;

	/** Logging. */
	private LogGenerator logger;

	public ReactiveSystemUpdateHandlerService() throws RemoteException {
		super();

		liveCache = new Vector<ReactiveEvent>();
		archiveCache = new Vector<ReactiveEvent>();
		listeners = new Vector<ReactiveSystemUpdateListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate().system("GUI").subSystem("Services")
				.srcCompClass(this.getClass().getSimpleName()).srcCompId("ERS");
	}

	public String getRsmUrl() {
		return rsmUrl;
	}

	public void setRsmUrl(String rsmUrl) {
		this.rsmUrl = rsmUrl;
	}

	public String getRsaUrl() {
		return rsaUrl;
	}

	public void setRsaUrl(String rsaUrl) {
		this.rsaUrl = rsaUrl;
	}

	public long getLookBackTime() {
		return lookBackTime;
	}

	public void setLookBackTime(long lookBackTime) {
		this.lookBackTime = lookBackTime;
	}

	/**
	 * Add a local listener component.
	 * 
	 * @param l
	 *            Local listener component.
	 */
	public void addListener(ReactiveSystemUpdateListener l) {
		listeners.add(l);
	}

	/** Test startup hook. */
	public static void main(String[] args) {
		try {
			ReactiveSystemUpdateHandlerService res = new ReactiveSystemUpdateHandlerService();
			res.rsmUrl = "rmi://ltsim1/ReactiveSystem";
			res.rsaUrl = "rmi://ltsim1/ReactiveSystemGateway";
			res.lookBackTime = 1800000L;
			res.loadServiceArchive();
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException ix) {
			}
			res.registerService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void criterionUpdated(String criterionName, long statusTimeStamp,
			boolean criterionOutput) throws RemoteException {

		CriterionUpdateEvent cev = new CriterionUpdateEvent(statusTimeStamp,
				criterionName, criterionOutput);

		logger.create().info().level(5).extractCallInfo()
				.msg("Received " + cev).send();

		liveCache.add(cev);
	}

	@Override
	public void filterUpdated(String filterName, long statusTimeStamp,
			Number sensorInput, Number filterOutput) throws RemoteException {

		FilterUpdateEvent fev = new FilterUpdateEvent(statusTimeStamp,
				filterName, sensorInput, filterOutput);

		logger.create().info().level(5).extractCallInfo()
				.msg("Received " + fev).send();

		liveCache.add(fev);
	}

	@Override
	public void ruleUpdated(String ruleName, long statusTimeStamp,
			boolean ruleTriggered) throws RemoteException {

		RuleUpdateEvent rev = new RuleUpdateEvent(statusTimeStamp, ruleName,
				ruleTriggered);

		logger.create().info().level(5).extractCallInfo()
				.msg("Received " + rev).send();

		liveCache.add(rev);
	}

	@Override
	public String getServiceProviderName() {
		return "ERS";
	}

	@Override
	public void registerService() throws Exception {
		
		logger.create().info().level(3).extractCallInfo()
		.msg("Looking up service provider: "+rsmUrl).send();
		
		
		ReactiveSystemMonitor rsm = (ReactiveSystemMonitor) Naming
				.lookup(rsmUrl);
		rsm.addReactiveSystemUpdateListener(this);
		logger.create().info().level(2).extractCallInfo()
				.msg("Registering with remote service provider").send();
	}

	@Override
	public int loadServiceArchive() throws Exception {

		ReactiveSystemArchive rsa = (ReactiveSystemArchive) Naming
				.lookup(rsaUrl);

		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime;
		List<ReactiveEvent> list = rsa.getReactiveSystemHistory(t1, t2);

		// add these to the archive cache
		logger.create().info().level(3).extractCallInfo()
				.msg("Received " + list.size() + " status items").send();
		archiveCache.addAll(list);

		return list.size();
	}

	@Override
	public void broadcastServiceAvailability(boolean available) {
		logger.create()
				.info()
				.level(3)
				.extractCallInfo()
				.msg("Broadcasting service availability: "
						+ (available ? "ONLINE" : "OFFLINE")).send();
		long time = System.currentTimeMillis();
		for (int il = 0; il < listeners.size(); il++) {
			ReactiveSystemUpdateListener l = listeners.get(il);
			try {
				((ServiceAvailabilityListener) l).serviceAvailable(
						getServiceProviderName(), time, available);
			} catch (Exception e) {
				// log and maybe mark listener for deletion
				logger.create().info().level(3).extractCallInfo()
						.msg("Exception updating listener: " + l + ": " + e)
						.send();
			}
		}
	}

	@Override
	public int broadcastStatus() throws Exception {
		// cycle thro archive-cache and send out to each registered listener
		int as = archiveCache.size();
		for (int ic = 0; ic < as; ic++) {
			ReactiveEvent status = archiveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				ReactiveSystemUpdateListener l = listeners.get(il);
				try {
					//System.err.println("Flushing archive cache: " + ic + " "
						//	+ status);				
					sendStatusToListener(status, l);					
				} catch (Exception e) {
					// log and maybe mark listener for deletion
					logger.create()
							.info()
							.level(3)
							.extractCallInfo()
							.msg("Exception updating listener: " + l + ": " + e)
							.send();
				}
			}
		}
		archiveCache.subList(0, as).clear();
		// cycle thro live-cache and send out to each registered listener
		int ls = liveCache.size();
		for (int ic = 0; ic < ls; ic++) {
			ReactiveEvent status = liveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				ReactiveSystemUpdateListener l = listeners.get(il);
				try {
					sendStatusToListener(status, l);
				} catch (Exception e) {
					// log and maybe mark listener for deletion
					logger.create()
							.info()
							.level(3)
							.extractCallInfo()
							.msg("Exception updating listener: " + l + ": " + e)
							.send();
				}
			}
		}
		// clear the elements weve already processed
		liveCache.subList(0, ls).clear();

		return ls;
	}

	/** Send status to a specific listener
	 * @param status The status to send.
	 * @param l The listener to send to.
	 * @throws Exception
	 */
	private void sendStatusToListener(ReactiveEvent status, ReactiveSystemUpdateListener l) throws Exception {
		if (status instanceof FilterUpdateEvent) {
			
			FilterUpdateEvent fev = (FilterUpdateEvent) status;
			l.filterUpdated(fev.getFilterName(), fev.getStatusTimeStamp(), fev.getSensorInput(), fev.getFilterOutput());
			
		} else if
		(status instanceof CriterionUpdateEvent) {
			
			CriterionUpdateEvent cev = (CriterionUpdateEvent) status;
			l.criterionUpdated(cev.getCriterionName(), cev.getStatusTimeStamp(), cev.isCriterionOutput());
			
		} else if
		(status instanceof RuleUpdateEvent) {
			
			RuleUpdateEvent rev = (RuleUpdateEvent) status;
			l.ruleUpdated(rev.getRuleName(), rev.getStatusTimeStamp(), rev.isRuleTriggered());
			
		}
		
	}
	
	
}
