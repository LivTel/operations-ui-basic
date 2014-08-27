/**
 * 
 */
package ngat.opsgui.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;

import ngat.opsgui.login.ServiceDescriptor;
import ngat.opsgui.perspectives.services.ServicesPerspective;
import ngat.util.logging.BasicLogFormatter;
import ngat.util.logging.ConsoleLogHandler;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;


/**
 * Provides registration for service services.
 * 
 * @author eng
 * 
 */
public class ServiceManager implements ServiceAvailabilityListener, ServiceDataListener {

	public static final int SERVICE_AVAILABLE = 777;

	public static final int SERVICE_UNAVAILABLE = 666;

	// private ServicesManagementPanel smp;

	/** List of services which are setup ready to run. */
	private Map<String, ServiceProvider> runnableServices;

	/** List of available services - they may not be used. */
	private Map<String, ServiceDescriptor> availableServices;

	/**
	 * the service-manager needs to be able to send service information to the
	 * services perspective
	 */
	private ServicesPerspective servicesPerspective;

	/** Logging. */
	private LogGenerator logger;

	/**
	 * @param name
	 *            The name of this thread.
	 */
	public ServiceManager() {
		availableServices = new HashMap<String, ServiceDescriptor>();
		runnableServices = new HashMap<String, ServiceProvider>();
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate().system("GUI").subSystem("Services").srcCompClass(this.getClass().getSimpleName())
				.srcCompId("SvcMgr");
	}

	/**
	 * Configure the set of available services.
	 * 
	 * @param services
	 */
	public void configureServices(List<ServiceDescriptor> services) {

		for (int is = 0; is < services.size(); is++) {
			ServiceDescriptor sd = services.get(is);
			availableServices.put(sd.getName(), sd);
			logger.create().info().level(1).extractCallInfo()
				.msg("Add service: "+sd.getName()+" to available services list")
				.send();
		}

	}

	public ServiceDescriptor lookupService(String svcName) {
		if (availableServices.containsKey(svcName))
			return availableServices.get(svcName);
		return null;
	}

	public void setServicesPerspective(ServicesPerspective servicesPerspective) {
		this.servicesPerspective = servicesPerspective;
	}

	public ServiceProvider createService(String svcName) throws Exception {

		logger.create().info().level(1).extractCallInfo().msg("Create service: " + svcName).send();

		ServiceDescriptor sd = lookupService(svcName);

		if (sd == null) {
			logger.create().info().level(1).extractCallInfo().msg("Service not known: " + svcName).send();
			return null;
		}

		if (runnableServices.containsKey(svcName)) {
			ServiceProvider sp = runnableServices.get(svcName);
			logger.create().info().level(1).extractCallInfo().msg("Service was already created").send();
			return sp;
		}

		// ServiceProvider svc = null;
		if (svcName.equalsIgnoreCase("TCM")) {
			TelescopeStatusHandlerService svc = new TelescopeStatusHandlerService();
			svc.setTcmUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setTsaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("METEO")) {
			MeteorologyStatusHandlerService svc = new MeteorologyStatusHandlerService();
			svc.setMspUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setMsaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("PHASE2")) {
			Phase2CacheService svc = new Phase2CacheService();
			svc.setSmaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			// svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("SCHED")) {
			SchedulingHandlerService svc = new SchedulingHandlerService();
			svc.setSchedUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setSchedArchiveUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("SKY")) {
			SkyModelHandlerService svc = new SkyModelHandlerService();
			svc.setSmUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setSmaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("TASK")) {
			TaskLifecycleEventHandlerService svc = new TaskLifecycleEventHandlerService();
			svc.setTmonUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setTmaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("INSTR")) {
			InstrumentStatusHandlerService svc = new InstrumentStatusHandlerService();
			svc.setIregUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setIsaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;
		} else if (svcName.equalsIgnoreCase("OPS")) {
			OperationsEventHandlerService svc = new OperationsEventHandlerService();
			svc.setOspUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setOsaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			
			JOptionPane.showMessageDialog(null, "Attaching OPS service provider at: "+svc.getOspUrl());
			return svc;
			
		} else if (svcName.equalsIgnoreCase("ERS")) {
			/*ReactiveSystemUpdateHandlerService svc = new ReactiveSystemUpdateHandlerService();
			svc.setRsmUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setRsaUrl("rmi://" + sd.getGatewayHost() + "/" + sd.getGatewayName());
			svc.setLookBackTime(sd.getLookbackTime());
			svc.setCycleInterval(sd.getBroadcastInterval());
			svc.setPollingInterval(sd.getPollingInterval());
			runnableServices.put(svcName, svc);
			return svc;	*/
			return null;
		}
		
		// else if ICM, TASK, SKY, METEO, ERS, etc, etc, etc...
		return null; // svc
	}

	/**
	 * Request manager to start a service. Returns silently if svc is not
	 * registered.
	 * 
	 * @param svc
	 */
	public void startServices() {

		Iterator<ServiceProvider> isvc = runnableServices.values().iterator();
		while (isvc.hasNext()) {

			ServiceProvider svc = isvc.next();

			if (svc != null) {
				logger.create().info().level(1).extractCallInfo()
						.msg("Start registration for: " + svc.getServiceProviderName()).send();

				// start the registration process
				ServiceRegistrationThread srt = new ServiceRegistrationThread(svc, this);
				srt.start();

				logger.create().info().level(1).extractCallInfo()
						.msg("Start distribution for: " + svc.getServiceProviderName()).send();

				// start the broadcast process
				ServiceDistributionThread sdt = new ServiceDistributionThread(svc, this);
				sdt.start();
			}
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		Logger alogger = LogManager.getLogger("GUI");
		alogger.setLogLevel(1);
		ConsoleLogHandler console = new ConsoleLogHandler(new BasicLogFormatter());
		console.setLogLevel(1);
		alogger.addExtendedHandler(console);

		try {
			
			ServiceManager svcmgr = new ServiceManager();
			ServiceProvider tss = svcmgr.createService("TCM");
			svcmgr.startServices();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * @return A list of services.
	 */
	public List<ServiceProvider> getServices() {
		List<ServiceProvider> servicesList = new Vector<ServiceProvider>();
		Iterator<ServiceProvider> is = runnableServices.values().iterator();
		while (is.hasNext()) {
			servicesList.add(is.next());
		}
		return servicesList;
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub
		System.err.println("SVM: received update for svc: " + serviceName + " available: " + available);

		if (servicesPerspective != null)
			servicesPerspective.serviceAvailable(serviceName, time, available);
	}

	@Override
	public void serviceDataUpdate(String serviceName, long time, int size) {
		// TODO Auto-generated method stub
		System.err.println("SVM: received data update for svc: " + serviceName);

		if (servicesPerspective != null)
			servicesPerspective.serviceDataUpdate(serviceName, time, size);

	}

}
