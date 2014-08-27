/**
 * 
 */
package ngat.opsgui.base;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ngat.astrometry.ISite;
import ngat.ems.SkyModelUpdateListener;
import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusUpdateListener;
import ngat.opsgui.components.AxisSummaryPanel;
import ngat.opsgui.components.InstrumentSummaryPanel;
import ngat.opsgui.components.ObservationSummaryPanel;
import ngat.opsgui.components.RcsSummaryPanel;
import ngat.opsgui.components.SkySummaryPanel;
import ngat.opsgui.components.StateVariableSummaryPanel;
import ngat.opsgui.components.TrackingSummaryPanel;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.tcm.AutoguiderActiveStatus;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusUpdateListener;

/**
 * @author eng
 *
 */
public class GuiSideBar extends JPanel implements ServiceAvailabilityListener, 
	TelescopeStatusUpdateListener,
	InstrumentStatusUpdateListener, 
	SkyModelUpdateListener {
	
	//private String base;
	
	/** List of instruments available.*/
	private List<InstrumentDescriptor> instruments;
	
	AxisSummaryPanel axisSummaryPanel;

    InstrumentSummaryPanel instrumentSummaryPanel;
    
    SkySummaryPanel skySummaryPanel;
 
    
    RcsSummaryPanel rcsSummaryPanel;
  
    StateVariableSummaryPanel stateVariableSummaryPanel;
    
	/**
	 * 
	 */
	public GuiSideBar(List<InstrumentDescriptor> instruments) {
		super(true);
	
		this.instruments = instruments;
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setBorder(BorderFactory.createLoweredBevelBorder());
		
		///this.base = base;
		
		rcsSummaryPanel = (RcsSummaryPanel)add(createRcsPanel());
		//auxSystemsPanel = (AuxSystemsSummaryPanel)add(createAuxSystemsPanel());
		axisSummaryPanel = (AxisSummaryPanel)add(createAxisPanel());	
		instrumentSummaryPanel = (InstrumentSummaryPanel)add(createInstrPanel());
		//observationSummaryPanel = (ObservationSummaryPanel)add(createObsPanel());
		skySummaryPanel = (SkySummaryPanel)add(createSkyPanel());
		//add(createSchedPanel());		
		stateVariableSummaryPanel = (StateVariableSummaryPanel)add(createStatePanel());
		
	}
	
	
	
	private RcsSummaryPanel createRcsPanel() {
		rcsSummaryPanel = new RcsSummaryPanel("RCS");
		rcsSummaryPanel.createPanel();
		return rcsSummaryPanel;
	}
	
	private JPanel createAxisPanel() {
		axisSummaryPanel = new AxisSummaryPanel("Axes");
		axisSummaryPanel.createPanel();
		return axisSummaryPanel;
	}
	
	private JPanel createSkyPanel() {
		SkySummaryPanel ssp = new SkySummaryPanel("Sky");
		ssp.createPanel();
		return ssp;
	}
	
	private JPanel createInstrPanel() {
		instrumentSummaryPanel = new InstrumentSummaryPanel("Instruments", instruments);
		instrumentSummaryPanel.createPanel();
		return instrumentSummaryPanel;
	}
	
	private JPanel createStatePanel() {
		StateVariableSummaryPanel asp = new StateVariableSummaryPanel("State");
		asp.createPanel();
		return asp;
	}
	

	// or optics ?
	private JPanel createBeamPanel() {
	
		JPanel p = new JPanel();
	/*	
		LinePanel l = new LinePanel();
		l.add(makeLabel("Upper slide"));
		l.add(makeField(8, "AlMirror"));
		l.add(makeField(8, "MOVING"));
		p.add(l);
		
		l = new LinePanel();
		l.add(makeLabel("Lower slide"));
		l.add(makeField(8, "Clear"));
		l.add(makeField(8, "IN_POSN"));
		p.add(l);*/
		
		return p;
	}
	
	private JPanel createAgentPanel() {
		JPanel p = new JPanel();
		
	/*	LinePanel l = new LinePanel();
		l.add(makeLabel("Control"));
		l.add(makeField(6, "SOCA"));
		l.add(makeField(6, "Running"));
		l.add(makeb());
		p.add(l);
		*/
		return p;
	}
	
	private JPanel createObsPanel() {
		ObservationSummaryPanel osp = new ObservationSummaryPanel("Observation");
		osp.createPanel();
		return osp;
	}
	
	
	public StateVariableSummaryPanel getStateVariableSummaryPanel() {
			return stateVariableSummaryPanel;
	}
	
	public RcsSummaryPanel getRcsSummaryPanel() {
			return rcsSummaryPanel;
	}
	
	
	/**
	 * @author eng
	 *
	 */
	public class LinePanel extends JPanel {

		/**
		 * 
		 */
		public LinePanel() {
			super(true);
			setLayout(new FlowLayout(FlowLayout.LEADING));
			setPreferredSize(new Dimension(200, 20));
		}

		

	}

	@Override
	public void telescopeNetworkFailure(long time, String message) throws RemoteException {
		// TODO Auto-generated method stub
		//System.err.println("GSB:Tel network Fail: "+time+":"+message );
		
	}



	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {
		//System.err.println("GSB:Tel update: "+status);
		if (status instanceof PrimaryAxisStatus) {
			PrimaryAxisStatus axis = (PrimaryAxisStatus)status;
			if (axis.getMechanismName().equalsIgnoreCase("AZM"))
				axisSummaryPanel.updateAzimuth(axis);
			else if
			(axis.getMechanismName().equalsIgnoreCase("ALT"))
				axisSummaryPanel.updateAltitude(axis);
			else if
			(axis.getMechanismName().equalsIgnoreCase("ROT"))
				axisSummaryPanel.updateRotator(axis);
		} else if (status instanceof AutoguiderActiveStatus) {
			
			AutoguiderActiveStatus agstatus = (AutoguiderActiveStatus)status;
			if (agstatus.getAutoguiderName().equals("CASS"))
			instrumentSummaryPanel.updateAutoguider(agstatus);
			
		}
		
		
		
	}

    @Override
	public void instrumentStatusUpdated(InstrumentStatus status) throws RemoteException {
	//System.err.println("GSB:Inst update: "+status);
	instrumentSummaryPanel.updateInstrument(status);
    }



	@Override
	public void extinctionUpdated(long time, double extinction) throws RemoteException {
		skySummaryPanel.updateExtinction(time, extinction);
		
	}



	@Override
	public void seeingUpdated(long time, double rawSeeing, double correctedSeeing, double prediction, double alt, double azm, double wav, boolean standard, String source, String targetName) throws RemoteException {
		System.err.println("GSB:Sky update: "+rawSeeing+", "+correctedSeeing);
		skySummaryPanel.updateSeeing(time, prediction);
	}



	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		System.err.println("GSB:SVC Update: Service: "+serviceName+(available ? " AVAIL" : " NOT_AVAIL"));
		if (serviceName.equals("TCM"))
			axisSummaryPanel.updateServiceStatus(time, available, "");
	
		// TODO this is done by polling so different mechanism
		//rcsSummaryPanel.updateServiceStatus(time, available, "");
		
		if (serviceName.equals("INSTR"))
			instrumentSummaryPanel.updateServiceStatus(time, available, "");
		if (serviceName.equals("SKY"))
			skySummaryPanel.updateServiceStatus(time, available, "");
		
		// TODO this is done by polling so different mechanism
		//stateVariableSummaryPanel.updateServiceStatus(time, available, "");
	}



}
