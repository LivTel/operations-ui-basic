/**
 * 
 */
package ngat.rcsgui.stable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Vector;

import ngat.opsgui.perspectives.scheduling.CandidateRow;
import ngat.rcsgui.test.GroupWatchMasterPanel;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetricsSet;

/**
 * @author eng
 *
 */
public class TestScheduleListener extends UnicastRemoteObject implements SchedulingStatusUpdateListener {

	private ScheduleDataPanel sdp;
	private ScheduleCandidatePanel scp;
	private GroupWatchMasterPanel gmp;

	private int contention;
	private List<CandidateRow> rows;
	
	/**
	 * @param sdp
	 * @throws RemoteException
	 */
	public TestScheduleListener(ScheduleDataPanel sdp,ScheduleCandidatePanel scp, GroupWatchMasterPanel gmp) throws RemoteException {
		super();
		this.sdp = sdp;
		this.scp = scp;
		this.gmp = gmp;
		rows = new Vector<CandidateRow>();
	}

    //	@Override
	@Override
	public void candidateAdded(String s, GroupItem group, ScoreMetricsSet metrics, double score, int rank)
			throws RemoteException {
		// add one
		contention++;
		System.err.println("SCHED: Add candidate: "+contention);
		scp.candidateAdded(group, score);
		gmp.candidateUpdate(group, false, score);
	}

    //@Override
	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {
		//sched.getstuff
		// update graph
		System.err.println("SCHED: Selected item: "+sched);
		try {
		    sdp.addDataActual(time, contention);
		} catch (Exception e) {
		    e.printStackTrace();
		}
		try {
		    scp.candidateSelected();
		} catch (Exception e) {
                    e.printStackTrace();
                }
		if (sched != null) {
		    try {
			gmp.candidateUpdate(sched.getGroup(), true, sched.getScore());
		    } catch (Exception e) {
			e.printStackTrace();
		    }
		}

	}


    //@Override
	@Override
	public void scheduleSweepStarted(long time, int sweep) throws RemoteException {
		// clear all indices
		System.err.println("SCHED: Starting sweep: "+sweep);
		contention = 0;
		scp.sweepStarted(sweep);	
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
