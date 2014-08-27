/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ngat.opsgui.services.ServiceManager;
import ngat.opsgui.services.ServiceProvider;
import ngat.opsgui.util.StateColorMap;

/** Display details of telemetry service providers.
 * @author eng
 *
 */
public class ServicesManagementPanel extends JPanel {

    public static final Color NO_DATA_COLOR = Color.gray.brighter();
    public static final Color SERVICE_AVAILABLE_COLOR = Color.green;
    public static final Color SERVICE_OFFLINE_COLOR = Color.red;


    public static StateColorMap colorMap;
	
	/** Mapping from service name to a history panel.*/
	private Map<String, ServiceDisplayPanel> displays;
	
	/** Create a ServicesManagementPanel.*/
	public ServicesManagementPanel(Map<String, ServiceProvider> services) {
		super();

		colorMap = new StateColorMap(NO_DATA_COLOR, "UNKNOWN");
		colorMap.addColorLabel(ServiceManager.SERVICE_AVAILABLE, SERVICE_AVAILABLE_COLOR, "AVAILABLE");
		colorMap.addColorLabel(ServiceManager.SERVICE_UNAVAILABLE, SERVICE_OFFLINE_COLOR, "OFFLINE");
		
		displays= new HashMap<String, ServiceDisplayPanel>();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		Iterator<String> is = services.keySet().iterator();
		while (is.hasNext()) {
			String svcName = is.next();
			ServiceProvider svc = services.get(svcName);		
			String serviceName = svc.getServiceProviderName();
			ServiceDisplayPanel sdp = addService(serviceName);
			add(sdp);
		}
	}
	
	/** Create a new ServiceDisplayPanel for the named service.
	 * @param serviceName The name of the service provider.
	 * @return A new ServiceDisplayPanel.
	 */
	private ServiceDisplayPanel addService(String serviceName) {		
		ServiceDisplayPanel sdp  = new ServiceDisplayPanel(serviceName, colorMap);		
		displays.put(serviceName, sdp);
		return sdp;
	}

	/** Update the services panel with information about service availability.
	 * @param serviceName The service to update.
	 * @param time When the informaiton is valid.
	 * @param available True if the service is available.
	 */
	public void serviceUpdate(String serviceName, long time, boolean available) {
		ServiceDisplayPanel sdp = displays.get(serviceName);
		if (sdp != null)
			sdp.serviceAvailabilityUpdate(time, available);
	}
}
