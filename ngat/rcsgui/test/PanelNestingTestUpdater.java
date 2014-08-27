/**
 * 
 */
package ngat.rcsgui.test;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.List;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusUpdateListener;
//import ngat.tcm.PrimaryAxisState;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.RotatorAxisStatus;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusUpdateListener;

/**
 * @author eng
 * 
 */
public class PanelNestingTestUpdater extends UnicastRemoteObject implements TelescopeStatusUpdateListener, InstrumentStatusUpdateListener {

	PanelNestingTest pnt;

	public PanelNestingTestUpdater(PanelNestingTest pnt) throws RemoteException {
		super();
		this.pnt = pnt;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * ngat.tcm.TelescopeStatusUpdateListener#telescopeStatusUpdate(ngat.tcm
	 * .TelescopeStatus)
	 */
	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {
		System.err.println("Update graph with status: " + status.getClass().getName());

		if (status instanceof PrimaryAxisStatus) {
			System.err.println("Update graph using axis status");

			PrimaryAxisStatus axis = (PrimaryAxisStatus) status;
			System.err.println("Axis cat is: " + axis.getMechanismName());

			String axisName = axis.getMechanismName();
			if (axisName == null)
				return;
			if (axis.getMechanismName().equalsIgnoreCase("AZM")) {
				System.err.println("Update graph azm: " + axis.getCurrentPosition() + ", " + axis.getDemandPosition());

				// find any graphs associated with AZM
				List<TimeSeries> list = pnt.graphMap.get("AZM.current");
				System.err.println("Found: " + list.size() + " entries for AZM.current");
				Iterator<TimeSeries> il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key AZM.current: " + ts);
					ts.add(new Second(), axis.getCurrentPosition());
				}

				list = pnt.graphMap.get("AZM.demand");
				System.err.println("Found: " + list.size() + " entries for AZM.demand");
				il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key AZMdemand: " + ts);
					ts.add(new Second(), axis.getDemandPosition());
				}

			} else if (axis.getMechanismName().equalsIgnoreCase("ALT")) {

				// find any graphs associated with ALT
				List<TimeSeries> list = pnt.graphMap.get("ALT.current");
				Iterator<TimeSeries> il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key ALT.current: " + ts);
					ts.add(new Second(), axis.getCurrentPosition());
				}

				list = pnt.graphMap.get("ALT.demand");
				il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key ALT demand: " + ts);
					ts.add(new Second(), axis.getDemandPosition());
				}

			} else if (axis.getMechanismName().equalsIgnoreCase("ROT")) {

				// find any graphs associated with ROT
				List<TimeSeries> list = pnt.graphMap.get("ROT.current");
				Iterator<TimeSeries> il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key ROT.current: " + ts);
					ts.add(new Second(), axis.getCurrentPosition());
				}

				list = pnt.graphMap.get("ROT.demand");
				il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key ROT.demand: " + ts);
					ts.add(new Second(), axis.getDemandPosition());
				}

				list = pnt.graphMap.get("ROT.skypa");
				il = list.iterator();
				while (il.hasNext()) {
					TimeSeries ts = il.next();
					System.err.println("Found ts for key ROT.skypa: " + ts);
					ts.add(new Second(), ((RotatorAxisStatus)axis).getSkyAngle());
				}

			}
		}
	}

	@Override
	public void telescopeNetworkFailure(long time, String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void instrumentStatusUpdated(InstrumentStatus status) throws RemoteException {
		String instName = status.getInstrument().getInstrumentName();
		System.err.println("Recieved instrument update for: "+instName);
		Object temp = status.getStatus().get("Temperature");
		System.err.println(instName+" temperature: "+temp);
	
		
	}


}
