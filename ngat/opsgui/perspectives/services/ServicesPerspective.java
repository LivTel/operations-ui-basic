/**
 * 
 */
package ngat.opsgui.perspectives.services;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.services.ServiceDataListener;
import ngat.opsgui.services.ServiceProvider;
import ngat.opsgui.util.StateColorMap;

/**
 * @author eng
 *
 */
public class ServicesPerspective extends Perspective implements ServiceAvailabilityListener, ServiceDataListener {
	
	public static final int TELEMETRY_OKAY = 1;
	public static final int TELEMETRY_OFFLINE = 2;
	public  static final int TELEMETRY_DISABLED = 3;
	public  static final int TELEMETRY_UNKNOWN = 0;

	public  static final Color TELEMETRY_OKAY_COLOR = Color.green;
	public  static final Color TELEMETRY_OFFLINE_COLOR = Color.blue;
	public  static final Color TELEMETRY_DISABLED_COLOR = Color.pink;
	public  static final Color TELEMETRY_UNKNOWN_COLOR = Color.gray.brighter();
    
	/** Tab pane.*/
	private JTabbedPane servicesPane;
	
	private ServicesMonitorPanel servicesMonitorPanel;

    private ServicesConfigPanel servicesConfigPanel;
    
    private List<ServiceProvider> services;

    private Map<String, ServicesViewPanel> viewMap;
    
    private StateColorMap serviceStateColorMap;
    
	public ServicesPerspective(JFrame frame, List<ServiceProvider>  services) {
		super(frame);
		this.services = services;
		
		viewMap = new HashMap<String, ServicesViewPanel>();
		
		perspectiveName = "Y";
		setLayout(new BorderLayout());
		
		servicesPane = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);
		
		serviceStateColorMap = new StateColorMap(TELEMETRY_UNKNOWN_COLOR, "UNKNOWN");
		serviceStateColorMap.addColorLabel(TELEMETRY_OKAY, TELEMETRY_OKAY_COLOR, "RUNNING");
		serviceStateColorMap.addColorLabel(TELEMETRY_DISABLED, TELEMETRY_DISABLED_COLOR, "DISABLED");
		serviceStateColorMap.addColorLabel(TELEMETRY_OFFLINE, TELEMETRY_OFFLINE_COLOR, "OFFLINE");

				
		servicesMonitorPanel = new ServicesMonitorPanel(services, serviceStateColorMap);
		servicesPane.addTab("Monitor", servicesMonitorPanel);
		
		servicesConfigPanel = new ServicesConfigPanel();;		
		servicesPane.addTab("Config", servicesConfigPanel);
		
         for (int is = 0; is < services.size(); is++) {
        	 ServiceProvider svc = services.get(is);
        	 String serviceName = svc.getServiceProviderName();
        	 // create a view panel for this service and add as a tab
        	 ServicesViewPanel serviceViewPanel = new ServicesViewPanel(serviceName, serviceStateColorMap);
             servicesPane.addTab(serviceName, serviceViewPanel);
             // make a mapping so we can find it when updates come in
        	 viewMap.put(svc.getServiceProviderName(),serviceViewPanel);
        	 System.err.println("SP: add view panel: "+is+" : "+svc.getServiceProviderName()+": "+serviceViewPanel);
         }
		
		// MAYBE put in super constructor ????
		createMenus();
				
		add(servicesPane, BorderLayout.CENTER);
	}

	/** Create menus.*/	

	private void createMenus() {
	
		
	
	}

	@Override
	public void serviceDataUpdate(String serviceName, long time, int size) {
		System.err.println("SP: "+serviceName+": received data update: "+size);
		ServicesViewPanel serviceViewPanel = viewMap.get(serviceName);
		
		// pass the info to the view panel unmodified
		if (serviceViewPanel != null)
			serviceViewPanel.serviceDataUpdate(serviceName, time, size);
	
		servicesMonitorPanel.serviceDataUpdate(serviceName, time, size);
		
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		System.err.println("SP: "+serviceName+": received avail update");
		ServicesViewPanel serviceViewPanel = viewMap.get(serviceName);
		
		// pass the info to the view panel unmodified
		if (serviceViewPanel != null)
			serviceViewPanel.serviceAvailable(serviceName, time, available);
		
		
		servicesMonitorPanel.serviceAvailable(serviceName, time, available);
		
	}
	
	
}
