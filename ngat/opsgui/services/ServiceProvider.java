/**
 * 
 */
package ngat.opsgui.services;

/** Implementors are objects which act as handlers for updates from some service provider.
 * The object is one which must regularly register with its provider.
 * @author eng
 *
 */
public interface ServiceProvider {

	/**
	 * @return the cycleInterval
	 */
	public long getCycleInterval();
	
	/**
	 * @return the pollingInterval
	 */
	public long getPollingInterval();
	
	/**
	 * @param cycleInterval the cycleInterval to set
	 */
	public void setCycleInterval(long cycleInterval);

	/**
	 * @param pollingInterval the pollingInterval to set
	 */
	public void setPollingInterval(long pollingInterval);
	
	/**
	 * @return The name of the service this object registers with.
	 */
	public String getServiceProviderName();
	
	
	/** Request to register with service provider. 
	 * @throws Exception If registration fails.
	 */
	public void registerService() throws Exception;
	
	// May want to be able to tell the service to unbind from provider - why?
	// public void deregisterService() throws Exception;
	
	/** Request object to load archived data from the service provider. The amount of data
	 * is decided by the implementor.
	 * @return The number of items received.
	 * @throws Exception If the service provider fails to return archived data.
	 */
	public int loadServiceArchive() throws Exception;
	
	/** Request object to broadcast availability information about the managed service.
	 * @param available True if the service is available.
	 * @throws Exception
	 */
	public void broadcastServiceAvailability(boolean available);

    /** Request object to broadcast its next status information.
     * @throws Exception
     */
    public int broadcastStatus() throws Exception;
	
}
