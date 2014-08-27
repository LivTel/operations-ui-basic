/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * @author eng
 *
 */
public class BasicServiceProvider extends UnicastRemoteObject {

	/** Cycle time between status distribution events.*/
	protected long cycleInterval;
	
	/** Interval between registration attempts.*/ 
	protected long pollingInterval;

	/**
	 * @throws RemoteException
	 */
	public BasicServiceProvider() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the cycleInterval
	 */
	public long getCycleInterval() {
		return cycleInterval;
	}

	/**
	 * @param cycleInterval the cycleInterval to set
	 */
	public void setCycleInterval(long cycleInterval) {
		this.cycleInterval = cycleInterval;
	}

	/**
	 * @return the pollingInterval
	 */
	public long getPollingInterval() {
		return pollingInterval;
	}

	/**
	 * @param pollingInterval the pollingInterval to set
	 */
	public void setPollingInterval(long pollingInterval) {
		this.pollingInterval = pollingInterval;
	}
	
	
	
}
