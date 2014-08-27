/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JFrame;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.tree.DefaultMutableTreeNode;

import ngat.rcs.tms.ErrorIndicator;
import ngat.rcs.tms.TaskDescriptor;
import ngat.rcs.tms.TaskLifecycleListener;
import ngat.rcs.tms.TaskMonitor;
import ngat.rcs.tms.events.TaskLifecycleEvent;
import ngat.rcsgui.stable.MyLNF;

/**
 * @author eng
 *
 */
public class TaskDisplayHandler extends UnicastRemoteObject implements TaskLifecycleListener {

	/** Displays the task activity tree.*/
	private TaskDisplayPanel taskPanel;
	
	
	
	/**
	 * @param taskPanel
	 * @throws RemoteException
	 */
	public TaskDisplayHandler(TaskDisplayPanel taskPanel) throws RemoteException {
		super();
		this.taskPanel = taskPanel;
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskAborted(ngat.rcs.tmm.TaskDescriptor, ngat.rcs.tmm.ErrorIndicator)
	 */
	public void taskAborted(TaskDescriptor task, ErrorIndicator err) throws RemoteException {	
		System.err.println("TTLH::taskAborted:"+task.getName()+", "+err);
		taskPanel.updateNode(task, "FAIL: "+err.getErrorCode());
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskCompleted(ngat.rcs.tmm.TaskDescriptor)
	 */
	public void taskCompleted(TaskDescriptor task) throws RemoteException {
		System.err.println("TTLH::taskCompleted:"+task.getName());
		taskPanel.updateNode(task, "DONE");
		
		// if this task's manager is one of SOCA/BGCA/TOCA/CAL we should wipe its subnodes.	
		DefaultMutableTreeNode mgrNode = taskPanel.findManagerNode(task.getName());
		if (mgrNode != null) {
		TaskNodeDescriptor mgrNodeDesc = (TaskNodeDescriptor)mgrNode.getUserObject();
		String mgrName = mgrNodeDesc.task.getName();
		if (mgrName.equals("SOCA") ||
			mgrName.equals("TOCA") ||
			mgrName.equals("BGCA") ||
			mgrName.equals("CAL")) {
				
			taskPanel.clear(mgrNodeDesc.task);
		}
		}
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskCreated(ngat.rcs.tmm.TaskDescriptor, ngat.rcs.tmm.TaskDescriptor)
	 */
	public void taskCreated(TaskDescriptor mgr, TaskDescriptor task) throws RemoteException {
		System.err.println("TTLH::taskCreated: "+mgr.getName()+" >> "+task.getName());
			taskPanel.addTaskNode(mgr, task);	
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskFailed(ngat.rcs.tmm.TaskDescriptor, ngat.rcs.tmm.ErrorIndicator)
	 */
	public void taskFailed(TaskDescriptor task, ErrorIndicator err) throws RemoteException {
		System.err.println("TTLH::taskFailed:"+task.getName()+", "+err);
		taskPanel.updateNode(task, "FAIL: "+err.getErrorCode());
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskInitialized(ngat.rcs.tmm.TaskDescriptor)
	 */
	public void taskInitialized(TaskDescriptor task) throws RemoteException {
		System.err.println("TTLH::taskInitialized:"+task.getName());
		taskPanel.updateNode(task, "INIT");
	}

	/* (non-Javadoc)
	 * @see ngat.rcs.tmm.TaskLifecycleListener#taskStarted(ngat.rcs.tmm.TaskDescriptor)
	 */
	public void taskStarted(TaskDescriptor task) throws RemoteException {
		System.err.println("TTLH::taskStarted:"+task.getName());
		taskPanel.updateNode(task, "RUNNING");
	}
	
public static void main(String args[]) {
		
		String host = args[0];
		MetalLookAndFeel.setCurrentTheme(new MyLNF());
		try {
			System.err.println("TTLH::Looking up task monitor on: "+host);
			TaskMonitor tm = (TaskMonitor)Naming.lookup("rmi://"+host+"/TaskMonitor");
			System.err.println("TTLH::Located TaskMonitor: "+tm);
			
			TaskDisplayPanel tp = new TaskDisplayPanel();
			
			JFrame f = new JFrame("Task Activity");
			f.getContentPane().add(tp);
			f.pack();
			f.setBounds(100,100,1000,800);
			f.setVisible(true);
			
			TaskDisplayHandler tdh = new TaskDisplayHandler(tp);			
			tm.addTaskEventListener(tdh);
			
			System.err.println("TTLH::Bound lifecycle listener display panel");
			
			while (true){
				try{Thread.sleep(60000);}catch (InterruptedException ix) {}
			}
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

@Override
public void taskLifecycleEventNotification(TaskLifecycleEvent arg0) throws RemoteException {
	// TODO Auto-generated method stub
	
}
}
