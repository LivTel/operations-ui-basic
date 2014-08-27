/**
 * 
 */
package ngat.opsgui.perspectives.services;

import java.awt.Color;

import javax.swing.BorderFactory;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.components.LinePanel;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.services.ServiceDataListener;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;

/**
 * Displays the service monitoring data.
 * 
 * @author eng
 * 
 */
public class ServicesMonitorDisplayPanel extends LinePanel implements ServiceAvailabilityListener, ServiceDataListener {

	private StateColorMap serviceStateColorMap;

	/** Service state. */
	private StateField serviceStateField;

	/** Time since last data received. */
	private DataField lastDataField;

	/** Count of received data. */
	private DataField dataReceivedCountField;

	/** Count of data broadcasts. */
	private DataField bcastCountField;

	private int packetCount;
	private int bcastCount;
	
	public ServicesMonitorDisplayPanel(String serviceName, StateColorMap serviceStateColorMap) {
		super();
		this.serviceStateColorMap = serviceStateColorMap;

		setBorder(BorderFactory.createLineBorder(Color.blue));
		add(ComponentFactory.makeLabel(serviceName)); // service name label
		serviceStateField = ComponentFactory.makeStateField(8, serviceStateColorMap);
		add(serviceStateField); // service status indicator

		lastDataField = ComponentFactory.makeIntegerDataField(8, "%8d");
		add(lastDataField); // last data

		add(ComponentFactory.makeUnsizedLabel("sec"));

		add(ComponentFactory.makeSmallButton(null)); // data received flasher

		dataReceivedCountField = ComponentFactory.makeIntegerDataField(8, "%8d");
		add(dataReceivedCountField); // data received count
		add(ComponentFactory.makeUnsizedLabel(" pkt"));

		add(ComponentFactory.makeSmallButton(null)); // bcast flasher

		bcastCountField = ComponentFactory.makeIntegerDataField(8, "%8d");

		add(bcastCountField); // bcast count
		add(ComponentFactory.makeUnsizedLabel(" bcast"));

		add(ComponentFactory.makeRedirectButton());
		// this button takes us to the individual service view panel for this service..
		// sp.showViewPanel(serviceName);
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		if (available)
			serviceStateField.updateState(ServicesPerspective.TELEMETRY_OKAY);
		else
			serviceStateField.updateState(ServicesPerspective.TELEMETRY_OFFLINE);
		
	}

	@Override
	public void serviceDataUpdate(String serviceName, long time, int size) {
		bcastCount++;
		packetCount += size;
		dataReceivedCountField.updateData(packetCount);
		bcastCountField.updateData(bcastCount);
	}
	
}
