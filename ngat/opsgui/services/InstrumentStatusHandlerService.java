/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusArchive;
import ngat.icm.InstrumentStatusProvider;
import ngat.icm.InstrumentStatusUpdateListener;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 *
 */
public class InstrumentStatusHandlerService extends BasicServiceProvider implements ServiceProvider,
		InstrumentStatusUpdateListener {
	
	//private String instrumentName;
	
	//private InstrumentDescriptor instId;
	
	private String iregUrl;
	
	private String isaUrl;
	
	
	private long lookBackTime;
	
	private List<InstrumentStatus> liveCache;
	private List<InstrumentStatus> archiveCache;
	private List<InstrumentStatusUpdateListener> listeners;
	
	// TODO Better: Make the attached listeners be:
	// InstrumentStatusUpdateListenerComponent = ISUL + SAL
	
	/** Logging. */
	private LogGenerator logger;
	
	
	public InstrumentStatusHandlerService() throws RemoteException {
		super();
		//this.instrumentName = instrumentName;
		//instId = new InstrumentDescriptor(instrumentName);
		liveCache = new Vector<InstrumentStatus>();
		archiveCache = new Vector<InstrumentStatus>();
		listeners = new Vector<InstrumentStatusUpdateListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate()
					.system("GUI")
					.subSystem("Services")
					.srcCompClass(this.getClass().getSimpleName())
					.srcCompId("Instruments");
	
	}

	
	
	/**
	 * @param iregUrl the iregUrl to set
	 */
	public void setIregUrl(String iregUrl) {
		this.iregUrl = iregUrl;
	}



	/**
	 * @param isaUrl the isaUrl to set
	 */
	public void setIsaUrl(String isaUrl) {
		this.isaUrl = isaUrl;
	}



	/**
	 * @param lookBackTime the lookBackTime to set
	 */
	public void setLookBackTime(long lookBackTime) {
		this.lookBackTime = lookBackTime;
	}

	public void addListener(InstrumentStatusUpdateListener l) {
		listeners.add(l);
	}

	public static void main(String[] args) {
		try{
			String iname = args[0];
			InstrumentStatusHandlerService iss = new InstrumentStatusHandlerService();
			iss.iregUrl = "rmi://ltsim1/InstrumentRegistry";
			iss.isaUrl = "rmi://ltsim1/InstrumentGateway";
			iss.lookBackTime = 1800000L;		
			iss.loadServiceArchive();
			try {Thread.sleep(10000L);} catch (InterruptedException ix){}
			iss.registerService();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void instrumentStatusUpdated(InstrumentStatus status) throws RemoteException {	
		//logger.create().info().level(3).extractCallInfo()
		//.msg("Received "+instId.getInstrumentName()+" "+
		//(status.isOnline() ? "ONLINE" : "OFFLINE")+ " "+
		//(status.isFunctional() ? "OKAY" : "ERROR")).send();	
		// Just incase it was not set ...
		//status.setInstrumentName(instId.getInstrumentName());
		liveCache.add(status);
	}

	
	@Override
	public String getServiceProviderName() {
			return "INSTR";
	}

	@Override
	public void registerService() throws Exception {		
		InstrumentRegistry ireg = (InstrumentRegistry)Naming.lookup(iregUrl);
		// NOTE we connect to the IAG not the ISP which is the raw feed
		List instList = ireg.listInstruments();
		for (int ii =0; ii < instList.size(); ii++) {
			InstrumentDescriptor instId = (InstrumentDescriptor)instList.get(ii);		
			InstrumentStatusProvider isp = ireg.getStatusProvider(instId);
			//InstrumentStatusProvider isp = (InstrumentStatusProvider)Naming.lookup(isaUrl);
			isp.addInstrumentStatusUpdateListener(this);	
			logger.create().info().level(2).extractCallInfo()
			.msg("Registering with remote service provider for: "+instId.getInstrumentName()).send();
		}

		// OR 
		//InstrumentStatusArchive isa = (InstrumentStatusArchive)Naming.lookup(isaUrl);
		// isa.addInstrumentStatusUpdateListener(this);

	}

	
	@Override
	public int loadServiceArchive() throws Exception {
		InstrumentStatusArchive isa = (InstrumentStatusArchive)Naming.lookup(isaUrl);
		
		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime; 
		List<InstrumentStatus> list = isa.getInstrumentStatusHistory(t1, t2);
		
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
			InstrumentStatusUpdateListener l = listeners.get(il);
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
					InstrumentStatus status = archiveCache.get(ic);
					// TODO maybe we store InstId in status rather than name ?????
					InstrumentDescriptor instId = new InstrumentDescriptor(status.getInstrument().getInstrumentName());
					for (int il = 0; il < listeners.size(); il++) {
						InstrumentStatusUpdateListener l = listeners.get(il);
						try {
							l.instrumentStatusUpdated(status);
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
				//System.err.println("ISS_"+instrumentName+"broadcasting upto: "+liveCache.size()+" items");
				for (int ic = 0; ic < ls; ic++) {
					InstrumentStatus status = liveCache.get(ic);
					InstrumentDescriptor instId = new InstrumentDescriptor(status.getInstrument().getInstrumentName());
					for (int il = 0; il < listeners.size(); il++) {
						InstrumentStatusUpdateListener l = listeners.get(il);
						try {
							l.instrumentStatusUpdated(status);
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
