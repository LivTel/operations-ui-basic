/**
 * 
 */
package ngat.opsgui.test;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ngat.opsgui.services.ServiceProvider;

/**
 * @author eng
 *
 */
public class SimulatedService extends UnicastRemoteObject implements ServiceProvider {

	String svcName;
		
	boolean available;
	
	/**
	 * @param svcName
	 * @throws RemoteException
	 */
	public SimulatedService(String svcName) throws RemoteException {
		super();
		this.svcName = svcName;
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.services.ServiceProvider#getServiceProviderName()
	 */
	@Override
	public String getServiceProviderName() {
	return svcName;
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.services.ServiceProvider#registerService()
	 */
	@Override
	public void registerService() throws Exception {
		if (Math.random() > 0.9) {
			available = !available;			
		}
		if (!available)
			throw new Exception("aaaarg");
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.services.ServiceProvider#loadServiceArchive()
	 */
	@Override
	public int loadServiceArchive() throws Exception {
		
		return 0;
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.services.ServiceProvider#broadcastServiceAvailability(boolean)
	 */
	@Override
	public void broadcastServiceAvailability(boolean available) {
		

	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.services.ServiceProvider#broadcastStatus()
	 */
	@Override
	public int broadcastStatus() throws Exception {
		return 0;

	}

	@Override
	public long getCycleInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getPollingInterval() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setCycleInterval(long cycleInterval) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setPollingInterval(long pollingInterval) {
		// TODO Auto-generated method stub
		
	}

}
