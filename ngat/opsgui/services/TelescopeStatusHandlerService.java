/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusArchive;
import ngat.tcm.TelescopeStatusProvider;
import ngat.tcm.TelescopeStatusUpdateListener;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 *
 */
public class TelescopeStatusHandlerService extends BasicServiceProvider implements ServiceProvider, TelescopeStatusUpdateListener {

	private String tcmUrl;
	
	private String tsaUrl;
	
	private long lookBackTime;
	
	private List<TelescopeStatus> liveCache;
	private List<TelescopeStatus> archiveCache;
	private List<TelescopeStatusUpdateListener> listeners;
	
	/** Logging. */
	private LogGenerator logger;
	
	public TelescopeStatusHandlerService() throws RemoteException {
		super();
		liveCache = new Vector<TelescopeStatus>();
		archiveCache = new Vector<TelescopeStatus>();
		listeners = new Vector<TelescopeStatusUpdateListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate()
					.system("GUI")
					.subSystem("Services")
					.srcCompClass(this.getClass().getSimpleName())
					.srcCompId("TCM");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try{
			TelescopeStatusHandlerService tss = new TelescopeStatusHandlerService();
			tss.tcmUrl = "rmi://ltsim1/Telescope";
			tss.tsaUrl = "rmi://ltsim1/TelescopeGateway";
			tss.lookBackTime = 1800000L;
			//tss.registerService();
			tss.loadServiceArchive();
			try {Thread.sleep(10000L);} catch (InterruptedException ix){}
			tss.registerService();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addListener(TelescopeStatusUpdateListener l) {
		listeners.add(l);
	}
	
	/**
	 * @return the tcmUrl
	 */
	public String getTcmUrl() {
		return tcmUrl;
	}

	/**
	 * @param tcmUrl the tcmUrl to set
	 */
	public void setTcmUrl(String tcmUrl) {
		this.tcmUrl = tcmUrl;
	}

	/**
	 * @return the tsaUrl
	 */
	public String getTsaUrl() {
		return tsaUrl;
	}

	/**
	 * @param tsaUrl the tsaUrl to set
	 */
	public void setTsaUrl(String tsaUrl) {
		this.tsaUrl = tsaUrl;
	}

	/**
	 * @return the lookBackTime
	 */
	public long getLookBackTime() {
		return lookBackTime;
	}

	/**
	 * @param lookBackTime the lookBackTime to set
	 */
	public void setLookBackTime(long lookBackTime) {
		this.lookBackTime = lookBackTime;
	}

	@Override
	public String getServiceProviderName() {
		return "TCM";
	}

	@Override
	public void registerService() throws Exception {
		TelescopeStatusProvider tsp = (TelescopeStatusProvider)Naming.lookup(tcmUrl);
		tsp.addTelescopeStatusUpdateListener(this);	
		logger.create().info().level(2).extractCallInfo()
		.msg("Registering with remote service provider: "+tsaUrl).send();
		//System.err.println("TSS registered with: "+tcmUrl);
	}

	@Override
	public int loadServiceArchive() throws Exception {
		TelescopeStatusArchive tsa = (TelescopeStatusArchive)Naming.lookup(tsaUrl);
		
		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime; 
		List<TelescopeStatus> list = tsa.getTelescopeStatusHistory(t1, t2);
		
		// add these to the archive cache	
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+list.size()+" status items").send();
		archiveCache.addAll(list);
		
		return list.size();
	}

	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {	
		logger.create().info().level(4).extractCallInfo()
		.msg("Received "+status).send();		
		//System.err.println("TSS received status: "+status);
		liveCache.add(status);
	}
	

	@Override
	public void telescopeNetworkFailure(long time, String message) throws RemoteException {
		logger.create().info().level(3).extractCallInfo()
		.msg("Received TCS network failure alert:").send();		
		//System.err.println("TSS recieved tel net fail");
		for (int il = 0; il < listeners.size(); il++) {
			TelescopeStatusUpdateListener l = listeners.get(il);
			try {
				l.telescopeNetworkFailure(time, message);
			} catch (Exception e) {
				// log and maybe mark listener for deletion
				logger.create().info().level(3).extractCallInfo()
				.msg("Exception updating listener: "+l+": "+e).send();
			}
		}
		
	}

	
	//@Override
	@Override
	public int broadcastStatus() throws RemoteException {
		// cycle thro archive-cache and send out to each registered listener	
		int as = archiveCache.size();
		for (int ic = 0; ic < as; ic++) {
			TelescopeStatus status = archiveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				TelescopeStatusUpdateListener l = listeners.get(il);
				try {
					l.telescopeStatusUpdate(status);
				} catch (Exception e) {
					// log and maybe mark listener for deletion
					logger.create().info().level(3).extractCallInfo()
					.msg("Exception updating listener: "+l+": "+e).send();
				}
			}
		}
		archiveCache.subList(0, as).clear();
		// cycle thro live-cache and send out to each registered listener	
		int ls = liveCache.size();
		//System.err.println("TSS broadcasting upto: "+liveCache.size()+" items");
		for (int ic = 0; ic < ls; ic++) {
			TelescopeStatus status = liveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				TelescopeStatusUpdateListener l = listeners.get(il);
				try {
					l.telescopeStatusUpdate(status);
				} catch (Exception e) {
					// log and maybe mark listener for deletion
					logger.create().info().level(3).extractCallInfo()
					.msg("Exception updating listener: "+l+": "+e).send();
				}
			}
		}
		// clear the elements weve already processed
		liveCache.subList(0, ls).clear();
		return ls;
	}
	
	@Override
	public void broadcastServiceAvailability(boolean available) {
		logger.create().info().level(3).extractCallInfo()
		.msg("Broadcasting service availability: "+(available ? "ONLINE" : "OFFLINE")).send();
		long time = System.currentTimeMillis();
		for (int il = 0; il < listeners.size(); il++) {
			TelescopeStatusUpdateListener l = listeners.get(il);
			try {
				((ServiceAvailabilityListener)l).serviceAvailable(getServiceProviderName(), time, available);
			} catch (Exception e) {
				// log and maybe mark listener for deletion
				logger.create().info().level(3).extractCallInfo()
				.msg("Exception updating listener: "+l+": "+e).send();
			}
		}
	}

	

}
