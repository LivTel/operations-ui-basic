/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.rcs.ops.OperationsArchive;
import ngat.rcs.ops.OperationsEventListener;
import ngat.rcs.ops.OperationsMonitor;
import ngat.rcs.ops.OperationsEvent;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 * 
 */
public class OperationsEventHandlerService extends BasicServiceProvider
		implements OperationsEventListener, ServiceProvider {

	/** Operations status provider. */
	private String ospUrl;

	/** Opaerations status archive. */
	private String osaUrl;

	private long lookBackTime;

	private List<OperationsEvent> liveCache;
	private List<OperationsEvent> archiveCache;
	private List<OperationsEventListener> listeners;

	/** Logging. */
	private LogGenerator logger;

	public OperationsEventHandlerService() throws RemoteException {
		super();

		liveCache = new Vector<OperationsEvent>();
		archiveCache = new Vector<OperationsEvent>();
		listeners = new Vector<OperationsEventListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate().system("GUI").subSystem("Services")
				.srcCompClass(this.getClass().getSimpleName()).srcCompId("OPS");

	}

	public String getOspUrl() {
		return ospUrl;
	}

	public void setOspUrl(String ospUrl) {
		this.ospUrl = ospUrl;
	}

	public String getOsaUrl() {
		return osaUrl;
	}

	public void setOsaUrl(String osaUrl) {
		this.osaUrl = osaUrl;
	}

	public long getLookBackTime() {
		return lookBackTime;
	}

	/**
	 * @param lookBackTime
	 *            the lookBackTime to set
	 */
	public void setLookBackTime(long lookBackTime) {
		this.lookBackTime = lookBackTime;
	}

	public void addListener(OperationsEventListener l) {
		listeners.add(l);
	}

	public static void main(String[] args) {
		try {
			OperationsEventHandlerService oes = new OperationsEventHandlerService();
			oes.ospUrl = "rmi://ltsim1/Operations";
			oes.osaUrl = "rmi://ltsim1/OperationsGateway";
			oes.lookBackTime = 1800000L;
			oes.loadServiceArchive();
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException ix) {
			}
			oes.registerService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void operationsEventNotification(OperationsEvent status)
			throws RemoteException {
		logger.create().info().level(3).extractCallInfo()
				.msg("Received " + status).send();
		liveCache.add(status);

	}

	@Override
	public String getServiceProviderName() {
		return "OPS";
	}

	@Override
	public void registerService() throws Exception {
		OperationsMonitor osp = (OperationsMonitor) Naming.lookup(ospUrl);
		osp.addOperationsEventListener(this);
		logger.create().info().level(2).extractCallInfo()
				.msg("Registering with remote service provider").send();
	}

	@Override
	public int loadServiceArchive() throws Exception {
		OperationsArchive osa = (OperationsArchive) Naming.lookup(osaUrl);

		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime;
		List<OperationsEvent> list = osa.getOperationsHistory(t1, t2);

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
			OperationsEventListener l = listeners.get(il);
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
			OperationsEvent status = archiveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				OperationsEventListener l = listeners.get(il);
				try {
					System.err.println("Flushing archive cache: " + ic + " "
							+ status);
					l.operationsEventNotification(status);
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
			OperationsEvent status = liveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				OperationsEventListener l = listeners.get(il);
				try {
					l.operationsEventNotification(status);
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

}
