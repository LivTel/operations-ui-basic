/**
 * 
 */
package ngat.opsgui.services;

/** Interface for clients which wish to receive notifications of a service's availability,
 * @author eng
 *
 */
public interface ServiceAvailabilityListener {

	/** Notification that a service is or is not available.
	 * @param serviceName The name of the service.
	 * @param time Time the notification applies to.
	 * @param available True if the service is available.
	 */
	public void serviceAvailable(String serviceName, long time, boolean available);
	
	
	
}
