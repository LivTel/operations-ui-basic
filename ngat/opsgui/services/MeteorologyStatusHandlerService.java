/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.ems.MeteorologyStatus;
import ngat.ems.MeteorologyStatusArchive;
import ngat.ems.MeteorologyStatusProvider;
import ngat.ems.MeteorologyStatusUpdateListener;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 *
 */
public class MeteorologyStatusHandlerService extends BasicServiceProvider implements ServiceProvider,
		MeteorologyStatusUpdateListener {

	private String mspUrl;
	
	private String msaUrl;
	
	private long lookBackTime;
	
	private List<MeteorologyStatus> liveCache;
	private List<MeteorologyStatus> archiveCache;
	private List<MeteorologyStatusUpdateListener> listeners;
	
	/** Logging. */
	private LogGenerator logger;
	
	public MeteorologyStatusHandlerService() throws RemoteException {
		super();
	
		liveCache = new Vector<MeteorologyStatus>();
		archiveCache = new Vector<MeteorologyStatus>();
		listeners = new Vector<MeteorologyStatusUpdateListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate()
					.system("GUI")
					.subSystem("Services")
					.srcCompClass(this.getClass().getSimpleName())
					.srcCompId("METEO");
	
	}

	/**
	 * @param mspUrl the mspUrl to set
	 */
	public void setMspUrl(String mspUrl) {
		this.mspUrl = mspUrl;
	}

	/**
	 * @param msaUrl the msaUrl to set
	 */
	public void setMsaUrl(String msaUrl) {
		this.msaUrl = msaUrl;
	}

	/**
	 * @param lookBackTime the lookBackTime to set
	 */
	public void setLookBackTime(long lookBackTime) {
		this.lookBackTime = lookBackTime;
	}

	public void addListener(MeteorologyStatusUpdateListener l) {
		listeners.add(l);
	}
	
	public static void main(String[] args) {
		try{		
			MeteorologyStatusHandlerService mss = new MeteorologyStatusHandlerService();
			mss.mspUrl = "rmi://ltsim1/Meteorology";
			mss.msaUrl = "rmi://ltsim1/MeteorologyGateway";
			mss.lookBackTime = 1800000L;		
			mss.loadServiceArchive();
			try {Thread.sleep(10000L);} catch (InterruptedException ix){}
				mss.registerService();
			}catch (Exception e) {
				e.printStackTrace();
			}
	}

	@Override
	public void meteorologyStatusUpdate(MeteorologyStatus status) throws RemoteException {
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+status).send();		
		liveCache.add(status);
	}

	@Override
	public String getServiceProviderName() {
		return "METEO";
	}

	@Override
	public void registerService() throws Exception {	
		MeteorologyStatusProvider msp = (MeteorologyStatusProvider)Naming.lookup(mspUrl);		
		msp.addMeteorologyStatusUpdateListener(this);
		logger.create().info().level(2).extractCallInfo()
		.msg("Registering with remote service provider").send();
	}

	@Override
	public int loadServiceArchive() throws Exception {
		MeteorologyStatusArchive msa = (MeteorologyStatusArchive)Naming.lookup(msaUrl);
		
		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime; 
		List<MeteorologyStatus> list = msa.getMeteorologyStatusHistory(t1, t2);
		
		// add these to the archive cache	
		logger.create().info().level(3).extractCallInfo()
		.msg("Received "+list.size()+" status items").send();
		archiveCache.addAll(list);
		
		return list.size();
	}

	@Override
	public void broadcastServiceAvailability(boolean available) {
		logger.create().info().level(3).extractCallInfo()
		.msg("Broadcasting service availability: "+(available ? "ONLINE" : "OFFLINE")).send();
		long time = System.currentTimeMillis();
		for (int il = 0; il < listeners.size(); il++) {
			MeteorologyStatusUpdateListener l = listeners.get(il);
			try {
				((ServiceAvailabilityListener)l).serviceAvailable(getServiceProviderName(), time, available);
			} catch (Exception e) {
				// log and maybe mark listener for deletion
				logger.create().info().level(3).extractCallInfo()
				.msg("Exception updating listener: "+l+": "+e).send();
			}
		}
	}

	@Override
	public int broadcastStatus() throws Exception {
		// cycle thro archive-cache and send out to each registered listener	
		int as = archiveCache.size();
		for (int ic = 0; ic < as; ic++) {
			MeteorologyStatus status = archiveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				MeteorologyStatusUpdateListener l = listeners.get(il);
				try {
					System.err.println("Flushing archive cache: "+ic+" "+status);
					l.meteorologyStatusUpdate(status);
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
		for (int ic = 0; ic < ls; ic++) {
			MeteorologyStatus status = liveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				MeteorologyStatusUpdateListener l = listeners.get(il);
				try {
					l.meteorologyStatusUpdate(status);
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
	
	
	
}
