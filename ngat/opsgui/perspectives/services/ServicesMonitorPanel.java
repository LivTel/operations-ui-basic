package ngat.opsgui.perspectives.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JPanel;
import ngat.opsgui.components.LinePanel;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.services.ServiceDataListener;
import ngat.opsgui.services.ServiceProvider;
import ngat.opsgui.util.StateColorMap;

public class ServicesMonitorPanel extends JPanel implements ServiceAvailabilityListener, ServiceDataListener {
	
	private StateColorMap serviceStateColorMap;
	
	private List<ServiceProvider>  services;
	
	private Map<String, ServicesMonitorDisplayPanel> serviceMap;
	
	/**
	 * 
	 */
	public ServicesMonitorPanel(List<ServiceProvider>  services, StateColorMap serviceStateColorMap) {
		super(true);
		this.services = services;
		this.serviceStateColorMap = serviceStateColorMap;
		
		serviceMap = new HashMap<String, ServicesMonitorDisplayPanel> ();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		

	    for (int is = 0; is < services.size(); is++) {
	    	ServiceProvider svc = services.get(is);
       	 	String serviceName = svc.getServiceProviderName();
       	 	add(createDisplayPanel(serviceName));
	    }
	    
	}

	private LinePanel createDisplayPanel(String serviceName) {
		
		ServicesMonitorDisplayPanel sdp = new ServicesMonitorDisplayPanel(serviceName, serviceStateColorMap);
		serviceMap.put(serviceName, sdp);
		return sdp;
		
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		
		ServicesMonitorDisplayPanel sdp = serviceMap.get(serviceName);
		if (sdp != null)
			sdp.serviceAvailable(serviceName, time, available);
		
	}

	@Override
	public void serviceDataUpdate(String serviceName, long time, int size) {
		
		ServicesMonitorDisplayPanel sdp = serviceMap.get(serviceName);
		if (sdp != null)
			sdp.serviceDataUpdate(serviceName, time, size);
		
	}
	
}
