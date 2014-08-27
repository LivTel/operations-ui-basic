/**
 * 
 */
package ngat.opsgui.services;

import ngat.util.ControlThread;
import ngat.util.logging.LogGenerator;
import ngat.util.logging.LogManager;
import ngat.util.logging.Logger;

/**
 * @author eng
 * 
 */
public class ServiceRegistrationThread extends ControlThread {


	/** The service to manage. */
	private ServiceProvider svc;

	/** the service manager.*/
	private ServiceManager svcMgr;
	
	/** Logging. */
	private LogGenerator logger;

	/**
	 * @param dvc
	 *            The service to manage.
	 */
	public ServiceRegistrationThread(ServiceProvider svc, ServiceManager svcMgr) {
		super(svc.getServiceProviderName(), true);
		this.svc = svc;
		this.svcMgr = svcMgr;
		Logger alogger = LogManager.getLogger("GUI");
		logger = alogger.generate().system("GUI").subSystem("Services").srcCompClass(this.getClass().getSimpleName())
				.srcCompId(svc.getServiceProviderName());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.util.ControlThread#initialise()
	 */
	@Override
	protected void initialise() {

		logger.create().info().level(1).extractCallInfo()
				.msg("Attempting to register service handler: " + svc.getServiceProviderName()).send();
		
		long time = System.currentTimeMillis();
		
		try {		
			svc.registerService();
			logger.create().info().level(1).extractCallInfo().msg("Registration hasCompleted").send();
			svc.broadcastServiceAvailability(true);
			svcMgr.serviceAvailable(svc.getServiceProviderName(), time, true);
		} catch (Exception e) {
			logger.create().info().level(1).extractCallInfo().msg("Registration failed, stack trace follows: " + e).send();
			e.printStackTrace();
			svc.broadcastServiceAvailability(false);
			svcMgr.serviceAvailable(svc.getServiceProviderName(), time, false);
		}

		try {
			int na = svc.loadServiceArchive();
			logger.create().info().level(1).extractCallInfo().msg("Loaded archived data: " + na + " items").send();
		} catch (Exception e) {
			e.printStackTrace();
			logger.create().info().level(1).extractCallInfo().msg("Load archive failed:"+e).send();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.util.ControlThread#mainTask()
	 */
	@Override
	protected void mainTask() {
		try {
			Thread.sleep(svc.getPollingInterval());
		} catch (InterruptedException ix) {
		}

		// Re-register for service
		logger.create().info().level(1).extractCallInfo()
				.msg("Attempting to re-register service handler: " + svc.getServiceProviderName()).send();
		
		long time = System.currentTimeMillis();
		
		try {
			svc.registerService();
			// At this point we should update the services display panel with an online
			svc.broadcastServiceAvailability(true);
			svcMgr.serviceAvailable(svc.getServiceProviderName(), time, true);
		} catch (Exception e) {
			// At this point we should update the services display panel with an offline
			svc.broadcastServiceAvailability(false);
			svcMgr.serviceAvailable(svc.getServiceProviderName(), time, false);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.util.ControlThread#shutdown()
	 */
	@Override
	protected void shutdown() {
		

	}

}
