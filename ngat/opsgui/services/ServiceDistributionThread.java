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
public class ServiceDistributionThread extends ControlThread {

	/** The service to manage. */
	private ServiceProvider svc;
	
	/** the service manager.*/
	private ServiceManager svcMgr;
	
	/** Logging. */
	private LogGenerator logger;

	/**
	 * @param svc
	 *            The service to manage.
	 */
	public ServiceDistributionThread(ServiceProvider svc, ServiceManager svcMgr) {
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

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.util.ControlThread#mainTask()
	 */
	@Override
	protected void mainTask() {
		try {
			Thread.sleep(svc.getCycleInterval());
		} catch (InterruptedException ix) {
		}

		// Re-register for service
		logger.create().info().level(3).extractCallInfo()
				.msg("Request status distribution for service handler: " + svc.getServiceProviderName()).send();
		
		long time = System.currentTimeMillis();
		
		try {
		    int ns = svc.broadcastStatus();
		    svcMgr.serviceDataUpdate(svc.getServiceProviderName(), time, ns); 
		    // TODO maybe: we dont know the bytes or packets size for last parameter.
		} catch (Exception e) {
		    logger.create().info().level(1).extractCallInfo().msg("Distribution failed, stack trace follows: " + e).send();
		    e.printStackTrace();
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
