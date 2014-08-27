package ngat.opsgui.perspectives.services;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.components.LinePanel;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.services.ServiceDataListener;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeAxisPanel;
import ngat.opsgui.util.TimeDisplayController;

public class ServicesViewPanel extends JPanel implements ServiceAvailabilityListener, ServiceDataListener {

	
	private String serviceName;

	private StateColorMap serviceStateColorMap;

	private StatusHistoryPanel statusHistoryPanel;
	
	private TimeAxisPanel timeAxisPanel;
	private StateField serviceStateField;
	private DataField packetCountField;
	private DataField bcastCountField;
	private boolean serviceAvailable;
	private int packetCount;
	private int bcastCount;
	private int avc;
	
	/**
     * 
     */
	public ServicesViewPanel(String serviceName, StateColorMap serviceStateColorMap) {
		super(true);
		this.serviceName = serviceName;
		this.serviceStateColorMap = serviceStateColorMap;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));


		add(createConfigPanel());
		add(createMonitorPanel());
		add(createHistoryPanel());
		add(createControlPanel());

	}

	private LinePanel createConfigPanel() {

		LinePanel linePanel = ComponentFactory.makeLinePanel();

		linePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue),
				"Configuration"));

		linePanel.add(ComponentFactory.makeEntryField(8)); // p host
		linePanel.add(ComponentFactory.makeUnsizedLabel(":")); // p svc name
		linePanel.add(ComponentFactory.makeEntryField(10));

		linePanel.add(ComponentFactory.makeSmallLabel("  "));

		linePanel.add(ComponentFactory.makeEntryField(8)); // a host
		linePanel.add(ComponentFactory.makeUnsizedLabel(":"));
		linePanel.add(ComponentFactory.makeEntryField(10)); // a svc name

		linePanel.add(ComponentFactory.makeEntryField(5)); // samples recvd
		linePanel.add(ComponentFactory.makeSmallLabel(" ns "));

		linePanel.add(ComponentFactory.makeTextButton("Apply"));

		return linePanel;

	}

	private LinePanel createMonitorPanel() {

		LinePanel linePanel = ComponentFactory.makeLinePanel();

		linePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Monitoring"));

		serviceStateField = ComponentFactory.makeStateField(8, serviceStateColorMap);
		linePanel.add(serviceStateField); // service status indicator

		linePanel.add(ComponentFactory.makeEntryField(8)); // last data
		linePanel.add(ComponentFactory.makeUnsizedLabel("sec"));

		linePanel.add(ComponentFactory.makeSmallButton(null)); // data received
																// flasher
		packetCountField = ComponentFactory.makeIntegerDataField(6, "%6d"); 
		linePanel.add(packetCountField); // data received
															// count
		linePanel.add(ComponentFactory.makeUnsizedLabel(" pkt"));

		linePanel.add(ComponentFactory.makeSmallButton(null)); // bcast flasher
		bcastCountField = ComponentFactory.makeIntegerDataField(6, "%6d"); 
		linePanel.add(bcastCountField); // bcast count
		linePanel.add(ComponentFactory.makeUnsizedLabel(" bcast"));

		linePanel.add(ComponentFactory.makeTextButton("Mute"));

		return linePanel;

	}

	private JPanel createHistoryPanel() {

		JPanel panel = new JPanel(true);
		panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "History"));

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		TimeDisplayController tdc = new TimeDisplayController(4*3600 * 1000L);
		
		statusHistoryPanel = new StatusHistoryPanel(tdc);
		statusHistoryPanel.setMap(serviceStateColorMap);
		tdc.addTimeDisplay(statusHistoryPanel);
		
		panel.add(statusHistoryPanel);

		timeAxisPanel = new TimeAxisPanel();
		tdc.addTimeDisplay(timeAxisPanel);
		panel.add(timeAxisPanel);

		panel.add(Box.createVerticalGlue());
		
		return panel;
	}

	private LinePanel createControlPanel() {

		LinePanel linePanel = ComponentFactory.makeLinePanel();

		linePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.blue), "Control"));

		linePanel.add(new JButton("Stop B/c"));
		linePanel.add(new JButton("Stop Reg"));
		linePanel.add(new JButton("Exec B/c"));
		linePanel.add(new JButton("Exec Reg"));
		linePanel.add(new JButton("Discon"));

		return linePanel;
	}

	@Override
	public void serviceDataUpdate(String serviceName, final long time, int size) {
		System.err.println(this.toString()+": received data update: bc: "+bcastCount+", npkt: "+size);
		packetCount += size;
		bcastCount++;
		
		//final boolean sva = serviceAvailable;
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (!serviceAvailable) {
					statusHistoryPanel.addHistory(time, ServicesPerspective.TELEMETRY_OFFLINE);
					statusHistoryPanel.repaint();
					timeAxisPanel.repaint();	
					serviceStateField.updateState(ServicesPerspective.TELEMETRY_OFFLINE);
				} else {
					statusHistoryPanel.addHistory(time, ServicesPerspective.TELEMETRY_OKAY);
					statusHistoryPanel.repaint();
					timeAxisPanel.repaint();	
					serviceStateField.updateState(ServicesPerspective.TELEMETRY_OKAY);
				}
				bcastCountField.updateData(bcastCount);
				packetCountField.updateData(packetCount);
				// update datarecvd field count
				// flasher once
			}
		});

	}

	@Override
	public void serviceAvailable(String serviceName, final long time, final boolean available) {
		System.err.println(this.toString()+": received avail update: "+available+": "+(++avc));
		this.serviceAvailable = available;
		
		//final boolean sva = serviceAvailable;		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!serviceAvailable) {
					statusHistoryPanel.addHistory(time, ServicesPerspective.TELEMETRY_OFFLINE);
					statusHistoryPanel.repaint();
					timeAxisPanel.repaint();	
					serviceStateField.updateState(ServicesPerspective.TELEMETRY_OFFLINE);
				} else {
					statusHistoryPanel.addHistory(time, ServicesPerspective.TELEMETRY_OKAY);
					statusHistoryPanel.repaint();
					timeAxisPanel.repaint();	
					serviceStateField.updateState(ServicesPerspective.TELEMETRY_OKAY);
				}
				// update current state indicator
				// flasher once
				// latest data time
			}
		});
	}
	
	@Override
	public String toString() { return "SVP:"+serviceName;}

}
