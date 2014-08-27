/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.Color;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Iterator;
import java.util.List;

import javax.swing.JFrame;

import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;
import ngat.opsgui.perspectives.tracking.AitoffPlot;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;
import ngat.sms.ComponentSet;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusProvider;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetricsSet;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.Telescope;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusArchive;
import ngat.tcm.TelescopeStatusProvider;
import ngat.tcm.TelescopeStatusUpdateListener;
import ngat.net.cil.tcs.TcsStatusPacket;

/**
 * @author eng
 * 
 */
public class MechSkyPlotHandler extends UnicastRemoteObject implements TelescopeStatusUpdateListener, SchedulingStatusUpdateListener {

	AitoffPlot aitoff;

	ISite site;
	
	double azm;
	double alt;
	int azmState = TcsStatusPacket.MOTION_UNKNOWN;
	int altState = TcsStatusPacket.MOTION_UNKNOWN;

	protected MechSkyPlotHandler(AitoffPlot aitoff, ISite site) throws RemoteException {
		super();
		this.aitoff = aitoff;
		this.site = site;
	}

	public static void main(String args[]) {

		try {
		
			ISite site = new BasicSite("test", Math.toRadians(28.7624), Math.toRadians(170.8792));
			AitoffPlot aitoff = new AitoffPlot();
			MechSkyPlotHandler h = new MechSkyPlotHandler(aitoff, site);
			JFrame f = new JFrame("Sky plot");
			f.getContentPane().add(aitoff);
			f.setBounds(100, 100, 500, 500);
			f.setVisible(true);

			aitoff.showTracks(site);
			
		/*	addTarget(aitoff, "PG+1234");
			addTarget(aitoff, "SA0-115");
			addTarget(aitoff, "Mark-A");
			addTarget(aitoff, "V2645_Sgr");
			addTarget(aitoff, "11BAS");
			addTarget(aitoff, "BTMon");
			addTarget(aitoff, "V1500_Cyg");
			addTarget(aitoff, "ngc4527");
			addTarget(aitoff, "HZPup");
			addTarget(aitoff, "0234+285");
			addTarget(aitoff, "DN_Gem");
			addTarget(aitoff, "HD251204");
			addTarget(aitoff, "feige34");
			addTarget(aitoff, "GRB100814A");*/
			
			SchedulingStatusProvider sched = (SchedulingStatusProvider)Naming.lookup("rmi://localhost/Scheduler");
			System.err.println("Found scheduler: " + sched);
			
			sched.addSchedulingUpdateListener(h);
			System.err.println("Added self as listener for scheduler updates");
			
			try {Thread.sleep(5000);} catch (InterruptedException e) {}
			
			Telescope scope = (Telescope) Naming.lookup("rmi://ltsim1/Telescope");
			System.err.println("Found scope: " + scope);

			// time minutes 300 = 5 hours
			long back = 300 * 60000L;

			long t2 = System.currentTimeMillis();
			long t1 = t2 - back;

			TelescopeStatusArchive tar = (TelescopeStatusArchive) Naming.lookup("rmi://ltsim1/TelescopeGateway");
			System.err.printf("Requesting archive data for %4d mins, from: %tT to %tT", (back / 60000), t1, t2);

			long st0 = System.currentTimeMillis();
			List<TelescopeStatus> list = tar.getTelescopeStatusHistory(t1, t2);
			long st1 = System.currentTimeMillis();

			System.err.println("Request returned " + list.size() + " entries in " + (st1 - st0) + "ms");

			for (int is = 0; is < list.size(); is++) {
				TelescopeStatus status = list.get(is);
				System.err.printf("%6d : %tT : %s\n", is, status.getStatusTimeStamp(), status);
				h.processUpdate(status, false);
			}

			TelescopeStatusProvider tsp = scope.getTelescopeStatusProvider();
			System.err.println("Found TSP: " + tsp);

			tsp.addTelescopeStatusUpdateListener(h);
			System.err.println("Added self as listener for telescope updates");

			
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	private static void addTarget(AitoffPlot aitoff,String name) {
		XExtraSolarTarget star = new XExtraSolarTarget(name);
		star.setRa(Math.random()*Math.PI*2.0);
		star.setDec((Math.random()-0.5)*Math.PI);
		//aitoff.addMarkedTarget(star);
	}
	
	public void processUpdate(TelescopeStatus status, boolean paint) throws RemoteException {

		if (status instanceof PrimaryAxisStatus) {
			PrimaryAxisStatus axis = (PrimaryAxisStatus) status;
			
			String axisName = axis.getMechanismName();
			System.err.println("Recieved update for: " + axisName);
			if (axisName == null)
				return;
			double current = axis.getCurrentPosition();
			if (axis.getMechanismName().equals("AZM")) {
				azm = Math.toRadians(current);
				while (azm > Math.PI)
					azm -= 2.0 * Math.PI;
				azmState = axis.getMechanismState();
			} else if (axis.getMechanismName().equals("ALT")) {
				alt = Math.toRadians(current);
				altState = axis.getMechanismState();
				Color color = Color.gray;
				if (azmState == TcsStatusPacket.MOTION_TRACKING && altState == TcsStatusPacket.MOTION_TRACKING)
					color = Color.green;
				else
					color = Color.blue;

				aitoff.addCoordinate(axis.getStatusTimeStamp(), alt, azm, color, paint);
			}
		}
	}
	
	//@Override
	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {
		System.err.println("Recieved update class: " + status.getClass().getName());
		System.err.println("Recieved update: " + status);
		processUpdate(status, true);	
		
		// work out where the moon is
		try {
			AstrometrySiteCalculator astro = new BasicAstrometrySiteCalculator(site);
			LunarCalculator lc = new LunarCalculator(site);
			long time = status.getStatusTimeStamp();
			Coordinates moon = lc.getCoordinates(time);
			double alt = astro.getAltitude(moon, time);
			double azm = astro.getAzimuth(moon, time);
			System.err.printf("Moon at: Azm: %4.2f, Alt: %4.2f \n", Math.toDegrees(azm), Math.toDegrees(alt));
		
			
		} catch (Exception e) {}
	}

	//@Override
	@Override
	public void telescopeNetworkFailure(long time, String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

	//@Override
	@Override
	public void candidateAdded(String arg0, GroupItem group, ScoreMetricsSet arg2, double arg3, int arg4)
			throws RemoteException {
		if (group == null)
			return;
		
		try {
			
			ISequenceComponent root = group.getSequence();		
			ComponentSet cs = new ComponentSet(root);
		
			Iterator<ITarget> it = cs.listTargets();
			while (it.hasNext()) {
				ITarget target = it.next();
				//aitoff.addMarkedTarget(target);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

	//@Override
	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {
		/*if (sched == null)
			return;
		
		GroupItem group = sched.getGroup();
		
		if (group == null)
			return;
		
		try {
			
			ISequenceComponent root = group.getSequence();		
			ComponentSet cs = new ComponentSet(root);
		
			Iterator<ITarget> it = cs.listTargets();
			while (it.hasNext()) {
				ITarget target = it.next();
				aitoff.addMarkedTarget(target);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	//@Override
	@Override
	public void scheduleSweepStarted(long arg0, int arg1) throws RemoteException {
		// TODO Auto-generated method stub
		//aitoff.clearMarkedTargets();
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
