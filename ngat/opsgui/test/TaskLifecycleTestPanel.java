/**
 * 
 */
package ngat.opsgui.test;

import java.rmi.RemoteException;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.rcs.tms.TaskLifecycleListener;
import ngat.rcs.tms.events.TaskLifecycleEvent;

/**
 * @author eng
 *
 */
public class TaskLifecycleTestPanel extends JPanel implements TaskLifecycleListener , ServiceAvailabilityListener {

	JTextArea area;
	
	public TaskLifecycleTestPanel() {
		super(true);
		area = new JTextArea(20, 80);
		//area.setAutoscrolls(true);
		//area.setLineWrap(true);
		JScrollPane pane = new JScrollPane(area,  ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		add(pane);
	}
	
	
	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskLifecycleEventNotification(ngat.rcs.tmm.events.TaskLifecycleEvent)
	 */
    //	@Override
	@Override
	public void taskLifecycleEventNotification(TaskLifecycleEvent event) throws RemoteException {
		area.append("EE"+event.toString()+"\n");
		System.err.println("TTT"+event.toString());
	}


	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub
		
	}

}
