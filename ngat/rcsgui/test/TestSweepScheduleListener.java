/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetricsSet;

/**
 * @author eng
 *
 */
public class TestSweepScheduleListener extends UnicastRemoteObject implements SchedulingStatusUpdateListener {

	private ScheduleSweepMasterPanel smp;
	
	/**
	 * @param smp
	 */
	public TestSweepScheduleListener(ScheduleSweepMasterPanel smp) throws Exception {
		super();
		this.smp = smp;
	}

	/* (non-Javadoc)
	 * @see ngat.sms.SchedulingUpdateListener#candidateAdded(java.lang.String, ngat.sms.GroupItem, ngat.sms.ScoreMetricsSet, double, int)
	 */
	@Override
	public void candidateAdded(String ident, GroupItem group, ScoreMetricsSet metrics, double score, int rank)
			throws RemoteException {
		smp.addCandidate(group, score);
	}

	/* (non-Javadoc)
	 * @see ngat.sms.SchedulingUpdateListener#candidateSelected(ngat.sms.ScheduleItem)
	 */
	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {
		smp.sweepCompleted(sched);
	}

	/* (non-Javadoc)
	 * @see ngat.sms.SchedulingUpdateListener#scheduleSweepStarted(long, int)
	 */
	@Override
	public void scheduleSweepStarted(long start, int sn) throws RemoteException {
		smp.startSweep(sn);
	}

	@Override
	public void candidateRejected(String arg0, GroupItem arg1, String arg2) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void schedulerMessage(int arg0, String arg1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
