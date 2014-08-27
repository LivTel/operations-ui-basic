/**
 * 
 */
package ngat.opsgui.perspectives.instruments;

import java.awt.BorderLayout;
import java.rmi.RemoteException;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.icm.InstrumentRegistry;
import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusUpdateListener;
import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.rcsgui.test.FrodoGeneralPanel;
import ngat.tcm.AutoguiderActiveStatus;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusUpdateListener;

/**
 * @author eng
 *
 */
public class InstrumentsPerspective extends Perspective implements InstrumentStatusUpdateListener, TelescopeStatusUpdateListener, ServiceAvailabilityListener {

	//List<InstrumentDescriptor> instruments;
	
	InstrumentRegistry ireg;
	
	/** Phase2 tab panel.*/
	private JTabbedPane instrumentsPane;
	
	private InstrumentHealthThumbnailPanel healthThumbnailPanel;
	
	private InstrumentCombinedHealthDisplayPanel combinedHealthPanel;
	
	private FrodoGeneralPanel frodoPanel;

	public InstrumentsPerspective(JFrame frame, InstrumentRegistry ireg) throws Exception {
		super(frame);
		this.ireg = ireg;
		
		perspectiveName = "I";
		setLayout(new BorderLayout());

		instrumentsPane = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);
	
		// shows status history plots in rows
		combinedHealthPanel = new InstrumentCombinedHealthDisplayPanel(ireg);
		instrumentsPane.addTab("History", combinedHealthPanel);
		
		frodoPanel = new FrodoGeneralPanel();
		instrumentsPane.addTab("Frodo", frodoPanel);
		
		// instruments common status panel
		// TODO shows general statii for all instruments
		
		// instruments graphs panel
		// TODO has common an individual instrument subpanels ?
		
		// individual instruments panels
	/*	try {
		List instLst = ireg.listInstruments();
		for (int ii = 0; ii < instLst.size(); ii++) {
			InstrumentDescriptor id = (InstrumentDescriptor)instLst.get(ii);
			JPanel p = new JPanel(true);
			instrumentsPane.addTab(id.getInstrumentName(), p);
			// TODO data subpanel and graphs subpanel
		}
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		// Common Health Panel
		healthThumbnailPanel = createHealthThumbnailPanel(ireg);
		instrumentsPane.addTab("Health", healthThumbnailPanel);
		
		// MAYBE put in super constructor ????
		createMenus();

		add(instrumentsPane, BorderLayout.CENTER);

	}
	
	protected void createMenus() {}

	public InstrumentHealthThumbnailPanel createHealthThumbnailPanel(InstrumentRegistry ireg) throws Exception {
		InstrumentHealthThumbnailPanel panel = new InstrumentHealthThumbnailPanel(ireg);
		return panel;
	}
	
	@Override
	public void instrumentStatusUpdated(InstrumentStatus status) throws RemoteException {
		// TODO Auto-generated method stub
		//System.err.println("IP: RECEIVED: STATUS FROM : "+instId+" with: "+instId.listSubcomponents().size()+" SUBS");
		// update individual data panels
		
		// update health thumb panel
		healthThumbnailPanel.updateStatus(status);
		
		// update hist panel
		combinedHealthPanel.updateStatus(status);
		
		if (status.getInstrument().getInstrumentName().equals("FRODO"))
			frodoPanel.update(status.getStatus());
		
	}

	@Override
	public void telescopeNetworkFailure(long arg0, String arg1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {
		// TODO Auto-generated method stub
		// TODO WE ARE ONLY INTERESTED IN AG ACTIVE STATUS here
		System.err.println("IP: RECEIVED STATUS: "+status.getClass().getName().toUpperCase());
		if (status instanceof AutoguiderActiveStatus) {
			AutoguiderActiveStatus agActiveStatus = (AutoguiderActiveStatus)status;
			System.err.println("IP: Received status: "+agActiveStatus);
			combinedHealthPanel.updateAutoguiderStatus(agActiveStatus);
			healthThumbnailPanel.updateAutoguiderStatus(agActiveStatus);
		}
		
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
}
