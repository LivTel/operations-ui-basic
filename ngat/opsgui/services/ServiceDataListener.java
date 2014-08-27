/**
 * 
 */
package ngat.opsgui.services;

/** Interface for clients which wish to receive notifications when a client recieves data.
 * @author eng
 *
 */
public interface ServiceDataListener {


	/** Notification that a service data update has occurred.
	 * @param serviceName The name of the service.
	 * @param time The time of the update.	
	 * @param size The size of the update (NOT USED CURRENTLY: probably bytes).
	 */
	public void serviceDataUpdate(String serviceName, long time, int size);
	
}
