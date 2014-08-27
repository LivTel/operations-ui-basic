/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ngat.tcm.AutoguiderMonitorStateListener;
import ngat.tcm.TrackingStatusListener;

/**
 * @author eng
 *
 */
public class MonitorStateDisplayHandler extends UnicastRemoteObject implements AutoguiderMonitorStateListener, TrackingStatusListener {
	
	private static Color ENABLED_BG = Color.green;
	private static Color ENABLED_FG = Color.blue;
	private static Color DISABLED_BG = Color.red;
	private static Color DISABLED_FG = Color.cyan;
	
	/** The label which displays the state of the monitor.*/
	private MonitorStateDisplay display;
	
	
	
	/** Create MonitorStateDisplayHandler using the supplied display label.
	 * @param label The label to update
	 * @throws RemoteException
	 */
	public MonitorStateDisplayHandler(MonitorStateDisplay display) throws RemoteException {
		super();
		this.display = display;
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.scm.detection.AutoguiderMonitorStateListener#autoguiderMonitorEnabled(boolean)
	 */
	@Override
	public void autoguiderMonitorEnabled(boolean enabled) throws RemoteException {
		display.setEnabled(enabled);		
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.scm.detection.AutoguiderMonitorStateListener#autoguiderMonitorWasReset()
	 */
	@Override
	public void autoguiderMonitorWasReset() throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void trackingLost() {
		
		
	}

}
