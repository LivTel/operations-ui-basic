/**
 * 
 */
package ngat.rcsgui.stable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import ngat.sms.FeasibilityPrescanUpdateListener;
import ngat.sms.util.PrescanEntry;

/**
 * @author eng
 *
 */
public class TestFeasibilityUpdateListener extends UnicastRemoteObject implements FeasibilityPrescanUpdateListener {
	
	private ScheduleDataPanel sdp;
	private FeasibilityTablePanel ftp;
	
	private List<PrescanEntry> pses;
	
	/** Expected group count.*/
	private int nge;
	
	/** Scanned groups count.*/
	private int ngs;
	
	private long start;
	private long end;
	private int nt;
	
	protected TestFeasibilityUpdateListener(FeasibilityTablePanel ftp, ScheduleDataPanel sdp) throws RemoteException {
		super();
		this.ftp = ftp;
		this.sdp = sdp;
		pses = new Vector<PrescanEntry>();
	}

	@Override
	public void prescanStarting(int ngc) throws RemoteException {
		System.err.println("Prescan clear, checking: "+ngc+" groups");
		ftp.clearTable(ngc);
		nge = ngc;
		ngs = 0;
		pses.clear();
		start = System.currentTimeMillis();
		end = System.currentTimeMillis();
	}

	@Override
	public void prescanUpdate(PrescanEntry pse) throws RemoteException {
		ngs++;
		System.err.println("Prescan update: "+ngs+"/"+nge+" -> "+pse.gname);
		ftp.addEntry(pse);
		pses.add(pse);
		if (pse.start < start)
			start = pse.start;
		if (pse.end > end)
			end = pse.end;
		if (pse.feasible.length > nt)
			nt = pse.feasible.length;
	}

	@Override
	public void prescanCompleted() throws RemoteException {
		
		System.err.printf("Prescan completed: %4d groups, between: %tF %tT and %tF %tT \n", ngs, start,start,end,end);	
	
		//now go over the pses and work out time v predicted contention
		
		int[] count = new int[nt];
		
		long interval = (long)((double)(end-start)/(double)nt);
		
		Iterator<PrescanEntry> ip = pses.iterator();
		while (ip.hasNext()) {
			PrescanEntry pse = ip.next();
			for (int it = 0; it < nt; it++) {
				if (pse.feasible[it])
					count[it]++;
			}
		}
		
		for (int it = 0; it < nt; it++) {
			long time = start + (long)((double)interval*(double)it+0.5);
			sdp.addDataPredict(time, count[it]);
		}
		
		
	}

}
