/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ngat.rcs.tms.TaskLifecycleListener;
import ngat.rcs.tms.TaskMonitor;
import ngat.rcs.tms.events.TaskLifecycleEvent;

/**
 * @author eng
 *
 */
public class TestTaskLifecycleHandler extends UnicastRemoteObject implements TaskLifecycleListener {

	/**
	 * @throws RemoteException
	 */
	public TestTaskLifecycleHandler() throws RemoteException {
		super();
	}

	
	
	
	public static void main(String args[]) {
		
		String host = args[0];
		
		try {
			System.err.println("TTLH::Looking up task monitor on: "+host);
			TaskMonitor tm = (TaskMonitor)Naming.lookup("rmi://"+host+"/TaskMonitor");
			System.err.println("TTLH::Located TaskMonitor: "+tm);
			
			TestTaskLifecycleHandler tth = new TestTaskLifecycleHandler();
			tm.addTaskEventListener(tth);
			
			System.err.println("TTLH::Bound lifecycle listener");
			
			while (true){
				try{Thread.sleep(60000);}catch (InterruptedException ix) {}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}




	@Override
	public void taskLifecycleEventNotification(TaskLifecycleEvent event) throws RemoteException {
		System.err.println("TaskLifecycleEvent: "+event);
		
	}

}
