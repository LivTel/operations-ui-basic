/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateIndicator;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeDisplayController;
import ngat.rcs.newstatemodel.ControlAction;
import ngat.rcs.newstatemodel.ControlActionImplementor;
import ngat.rcs.newstatemodel.ControlActionListener;
import ngat.rcs.newstatemodel.ControlActionResponseHandler;
import ngat.rcs.newstatemodel.IState;
import ngat.rcs.newstatemodel.StandardStateModel;
import ngat.rcs.newstatemodel.StateChangeListener;
import ngat.rcs.newstatemodel.StateModel;
import ngat.rcs.newstatemodel.TestState;
import ngat.rcs.ops.OperationsEvent;
import ngat.rcs.ops.OperationsEventListener;
import ngat.rcs.ops.OperationsMonitor;
import ngat.smsgui.TimeCategoryPanel;


/**
 * @author eng
 *
 */
public class OpsStateChangeHandlerTest extends UnicastRemoteObject implements StateChangeListener,ControlActionListener, ControlActionImplementor
									      ,OperationsEventListener {

    public static final Dimension LSIZE = new Dimension(80,20);
    public static final Dimension HSIZE = new Dimension(400,20);
    public static final Dimension SSIZE = new Dimension(80,20);
	
    private StateIndicator si;
	
    private StatusHistoryPanel shp;
	
    protected OpsStateChangeHandlerTest() throws RemoteException {
	super();
		
	StateColorMap map = new StateColorMap(Color.gray.brighter(), "UNKNOWN");
	map.addColorLabel(StandardStateModel.INIT_STATE, Color.yellow, "INIT");
	map.addColorLabel(StandardStateModel.STANDBY_STATE, Color.orange, "STANDBY");
	map.addColorLabel(StandardStateModel.OPERATIONAL_STATE, Color.green, "OPER");
	map.addColorLabel(StandardStateModel.OPENING_STATE, Color.pink.brighter(), "OPENING");		
	map.addColorLabel(StandardStateModel.CLOSING_STATE, Color.cyan.darker(), "CLOSING");
	map.addColorLabel(StandardStateModel.STARTING_STATE, Color.pink.darker(), "STARTING");
	map.addColorLabel(StandardStateModel.STOPPING_STATE, Color.magenta.darker(), "STOPPING");
	map.addColorLabel(StandardStateModel.SHUTDOWN_STATE, Color.red.darker(), "SHUTDOWN");
		
	TimeDisplayController tdc = new TimeDisplayController(4*3600*1000L);
		
	shp = new StatusHistoryPanel(tdc);
		
	shp.setMap(map);
	shp.setPreferredSize(HSIZE);
		
	si = new StateIndicator();
	si.setMap(map);
	si.setPreferredSize(SSIZE);
		
	JLabel l = new JLabel("StateModel");
	l.setPreferredSize(LSIZE);
		
	JPanel p = new JPanel(true);
	p.setLayout(new FlowLayout(FlowLayout.LEADING,1,5));
		
	p.add(l);
	p.add(shp);
	p.add(si);
	JFrame f = new JFrame("State Model");
	f.getContentPane().add(p);
	f.pack();
	f.setVisible(true);
				
    }

    /**
     * @param argsduration
     */
    public static void main(String[] args) {
	try {

	    String host = args[0];
		    
	    OpsStateChangeHandlerTest osm = new OpsStateChangeHandlerTest();
			
	    StateModel sm = (StateModel)Naming.lookup("rmi://"+host+"/StateModel");
	    System.err.println("Located remote statemodel");
	    sm.addStateChangeListener(osm);
	    sm.addControlActionImplementor(osm);
	    sm.addControlActionListener(osm);			
	    System.err.println("Bound to state model...");
	    OperationsMonitor ops = (OperationsMonitor)Naming.lookup("rmi://"+host+"/OperationsManager");
	    ops.addOperationsEventListener(osm);
	    System.err.println("Bound to OpsMgr...");
	} catch (Exception e) {
	    e.printStackTrace();
	}
    }

    //@Override
    //public void modeChanged(String oldMode, String newMode) throws RemoteException {
    //	System.err.println("Mode changed: From: "+oldMode+" to "+newMode);		
    //}

    @Override
	public void stateChanged(IState oldState, IState newState) throws RemoteException {
	System.err.println("State changed from: "+oldState+" to "+newState);
	TestState s = (TestState)newState;
	long time = System.currentTimeMillis();
	shp.addHistory(time, s.getState());
	shp.repaint();
	si.updateState(s.getState(), s.getStateName());
	si.repaint();
    }

    @Override
	public void performAction(ControlAction action, ControlActionResponseHandler carh) throws RemoteException {
	System.err.println("Perform action:  "+action+" rh: "+carh);		
    }

    @Override
	public void performAction(ControlAction action) throws RemoteException {
	System.err.println("Perform action:  "+action);		
    }

    @Override
	public void operationsEventNotification(OperationsEvent event) throws RemoteException {
    	System.err.println("Ops event: "+event);
    }

}
