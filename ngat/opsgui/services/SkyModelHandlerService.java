/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import ngat.ems.SkyModelArchive;
import ngat.ems.SkyModelExtinctionUpdate;
import ngat.ems.SkyModelMonitor;
import ngat.ems.SkyModelSeeingUpdate;
import ngat.ems.SkyModelUpdate;
import ngat.ems.SkyModelUpdateListener;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 * 
 */
public class SkyModelHandlerService extends BasicServiceProvider implements
		SkyModelUpdateListener, ServiceProvider {

	private String smUrl;

	private String smaUrl;

	private long lookBackTime;

	private List<SkyModelUpdate> liveCache;
	private List<SkyModelUpdate> archiveCache;
	private List<SkyModelUpdateListener> listeners;

	/** Logging. */
	private LogGenerator logger;

	public SkyModelHandlerService() throws RemoteException {
		super();
		liveCache = new Vector<SkyModelUpdate>();
		archiveCache = new Vector<SkyModelUpdate>();
		listeners = new Vector<SkyModelUpdateListener>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate().system("GUI").subSystem("Services")
				.srcCompClass(this.getClass().getSimpleName()).srcCompId("SKY");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			SkyModelHandlerService tss = new SkyModelHandlerService();
			tss.smUrl = "rmi://ltsim1/SkyModel";
			tss.smaUrl = "rmi://ltsim1/SkyModelGateway";
			tss.lookBackTime = 1800000L;
			tss.loadServiceArchive();
			try {
				Thread.sleep(10000L);
			} catch (InterruptedException ix) {
			}
			tss.registerService();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void addListener(SkyModelUpdateListener l) {
		listeners.add(l);
	}

	/**
	 * @return the smUrl
	 */
	public String getSmUrl() {
		return smUrl;
	}

	/**
	 * @param smUrl
	 *            the smUrl to set
	 */
	public void setSmUrl(String smUrl) {
		this.smUrl = smUrl;
	}

	/**
	 * @return the smaUrl
	 */
	public String getSmaUrl() {
		return smaUrl;
	}

	/**
	 * @param smaUrl
	 *            the smaUrl to set
	 */
	public void setSmaUrl(String smaUrl) {
		this.smaUrl = smaUrl;
	}

	/**
	 * @return the lookBackTime
	 */
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

	@Override
	public String getServiceProviderName() {
		return "SKY";
	}

	@Override
	public void registerService() throws Exception {
		System.err.println("SKY: registering with: " + smUrl);
		SkyModelMonitor sky = (SkyModelMonitor) Naming.lookup(smUrl);
		sky.addSkyModelUpdateListener(this);
		logger.create().info().level(2).extractCallInfo()
				.msg("Registering with remote service provider: " + smUrl)
				.send();

	}

	@Override
	public int loadServiceArchive() throws Exception {
		SkyModelArchive tsa = (SkyModelArchive) Naming.lookup(smaUrl);

		long t2 = System.currentTimeMillis();
		long t1 = t2 - lookBackTime;
		List<SkyModelUpdate> list = tsa.getSkyModelHistory(t1, t2);

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
			SkyModelUpdateListener l = listeners.get(il);
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
			SkyModelUpdate status = archiveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				SkyModelUpdateListener l = listeners.get(il);
				try {
					if (status instanceof SkyModelSeeingUpdate) {
						SkyModelSeeingUpdate seeing = (SkyModelSeeingUpdate) status;
						l.seeingUpdated(seeing.getStatusTimeStamp(),
								seeing.getRawSeeing(),
								seeing.getCorrectedSeeing(),
								seeing.getPredictedSeeing(),
								seeing.getElevation(),
								seeing.getAzimuth(),
								seeing.getWavelength(), 
								seeing.isStandard(), 
								seeing.getSource(),
								seeing.getTargetName());
					}
					// TODO or ext or skyb
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
			SkyModelUpdate status = liveCache.get(ic);
			for (int il = 0; il < listeners.size(); il++) {
				SkyModelUpdateListener l = listeners.get(il);
				try {
					if (status instanceof SkyModelSeeingUpdate) {
						SkyModelSeeingUpdate seeing = (SkyModelSeeingUpdate) status;
						l.seeingUpdated(seeing.getStatusTimeStamp(),
								seeing.getRawSeeing(),
								seeing.getCorrectedSeeing(),
								seeing.getPredictedSeeing(),
								seeing.getElevation(),
								seeing.getAzimuth(),
								seeing.getWavelength(), 
								seeing.isStandard(),
								seeing.getSource(),
								seeing.getTargetName());
					} else if (status instanceof SkyModelExtinctionUpdate) {
						SkyModelExtinctionUpdate photom = (SkyModelExtinctionUpdate) status;
						l.extinctionUpdated(photom.getStatusTimeStamp(),
								photom.getExtinction());
					}

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

	@Override
	public void extinctionUpdated(long time, double ext) throws RemoteException {
		SkyModelExtinctionUpdate photom = new SkyModelExtinctionUpdate(time,
				ext);
		logger.create().info().level(3).extractCallInfo()
				.msg("Received " + ext).send();
		liveCache.add(photom);
	}

	@Override
	public void seeingUpdated(long time, double rawSeeing,
			double correctedSeeing, double prediction, double elevation,
			double azimuth, double wavelength, boolean standard, String source, String targetName)
			throws RemoteException {
		SkyModelSeeingUpdate seeing = new SkyModelSeeingUpdate(time, rawSeeing,
				correctedSeeing, prediction, standard, source);
		seeing.setElevation(elevation);
		seeing.setAzimuth(azimuth);
		seeing.setWavelength(wavelength);
		seeing.setTargetName(targetName);
		logger.create().info().level(3).extractCallInfo()
				.msg("Received " + seeing).send();
		liveCache.add(seeing);
	}

}
