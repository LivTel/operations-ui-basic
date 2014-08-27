/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeAxisPanel;
import ngat.opsgui.util.TimeDisplayController;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.Telescope;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusProvider;
import ngat.tcm.TelescopeStatusUpdateListener;

/**
 * @author eng
 * 
 */
public class TcmAxisDisplayTest extends UnicastRemoteObject implements TelescopeStatusUpdateListener {

	TimeDisplayController tdc;
	
	StatusHistoryPanel pazm; 
	StatusHistoryPanel palt;
	StatusHistoryPanel prot;
	TimeAxisPanel tap;
	
	protected TcmAxisDisplayTest(StatusHistoryPanel pazm, StatusHistoryPanel palt, StatusHistoryPanel prot, TimeAxisPanel tap) throws RemoteException {
		super();
		this.pazm = pazm;
		this.palt = palt;
		this.prot = prot;
		this.tap = tap;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try { 
			TimeDisplayController tdc = new TimeDisplayController(7200*1000L);
			StatusHistoryPanel pazm = new StatusHistoryPanel(tdc);
			tdc.addTimeDisplay(pazm);
			StatusHistoryPanel palt = new StatusHistoryPanel(tdc);
			tdc.addTimeDisplay(palt);
			StatusHistoryPanel prot = new StatusHistoryPanel(tdc);
			tdc.addTimeDisplay(prot);
						
			StateColorMap map = new StateColorMap(Color.gray, "UNKNOWN");
			map.addColorLabel(TcsStatusPacket.MOTION_OFF_LINE, Color.red, "OFFLINE");
			map.addColorLabel(TcsStatusPacket.MOTION_ERROR, Color.red.darker(), "ERROR");
			map.addColorLabel(TcsStatusPacket.MOTION_TRACKING, Color.green, "TRACKING");
			map.addColorLabel(TcsStatusPacket.MOTION_MOVING, Color.orange, "MOVING");
			map.addColorLabel(TcsStatusPacket.MOTION_INPOSITION, Color.blue.brighter(), "IN-POSN");
			map.addColorLabel(TcsStatusPacket.MOTION_STOPPED, Color.blue, "STOPPED");
			
			pazm.setMap(map);
			pazm.setPreferredSize(new Dimension(400, 20));
			palt.setMap(map);	
			palt.setPreferredSize(new Dimension(400, 20));
			prot.setMap(map);
			prot.setPreferredSize(new Dimension(400, 20));
			
			TimeAxisPanel tap = new TimeAxisPanel();
			tap.setPreferredSize(new Dimension(400, 20));
			tdc.addTimeDisplay(tap);
			
			TcmAxisDisplayTest t = new TcmAxisDisplayTest(pazm, palt, prot,tap);

			JFrame f = new JFrame("Tcm Axis State test");
			f.getContentPane().setLayout(new GridLayout(4,1,5,5));
		
			f.getContentPane().add(t.addPanel("Azm", pazm));
			f.getContentPane().add(t.addPanel("Alt", palt));
			f.getContentPane().add(t.addPanel("Rot", prot));
			f.getContentPane().add(t.addPanel("", tap));
			
			f.pack();
			f.setVisible(true);
			
			long back = 7200*1000L;
			long t2 = System.currentTimeMillis();
			long t1 = t2- back;
			
			/*TelescopeStatusArchive tar = (TelescopeStatusArchive) Naming.lookup("rmi://ltsim1/TelescopeGateway");
			System.err.printf("Requesting archive data for %4d mins, from: %tT to %tT", (back / 60000), t1, t2);

			long st0 = System.currentTimeMillis();
			List<TelescopeStatus> list = tar.getTelescopeStatusHistory(t1, t2);
			long st1 = System.currentTimeMillis();

			System.err.println("Request returned " + list.size() + " entries in " + (st1 - st0) + "ms");

			for (int is = 0; is < list.size(); is++) {
				TelescopeStatus status = list.get(is);
				System.err.printf("%6d : %tT : %s\n", is, status.getStatusTimeStamp(), status);
				t.processUpdate(status, false);				
			}&*/
			Telescope scope = (Telescope) Naming.lookup("rmi://ltsim1/Telescope");
			System.err.println("Found scope: " + scope);
			TelescopeStatusProvider tsp = scope.getTelescopeStatusProvider();
			System.err.println("Found TSP: " + tsp);

			tsp.addTelescopeStatusUpdateListener(t);
			System.err.println("Added self as listener for telescope updates");

			while (true) {try {Thread.sleep(60000);}catch (InterruptedException ix) {}}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public JPanel addPanel(String text, JPanel p) {
		
		JPanel panel = new JPanel(true);
		panel.setLayout(new BorderLayout(5, 6));
		panel.setBorder(BorderFactory.createLineBorder(Color.black));
		panel.add(p, BorderLayout.CENTER);
		JLabel l = new JLabel(text);
		l.setPreferredSize(new Dimension(50,12));
		panel.add(l, BorderLayout.WEST);
		return panel;				
		
	}

	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {
		processUpdate(status, true);		
	}
	
	@Override
	public void telescopeNetworkFailure(long time, String arg0) throws RemoteException {
		// TODO Auto-generated method stub
		
	}
	
	private void processUpdate(TelescopeStatus status, boolean paint) {
		if (status instanceof PrimaryAxisStatus) {
			PrimaryAxisStatus axis = (PrimaryAxisStatus) status;

			String axisName = axis.getMechanismName();
			System.err.println("Recieved update for: " + axisName);
			if (axisName == null)
				return;
			if (axis.getMechanismName().equals("AZM")) {
				System.err.println("Recieved AZM status: "+status);
				long time = axis.getStatusTimeStamp();
				int azmState = axis.getMechanismState();
				pazm.addHistory(time, azmState);
				if (paint)
				pazm.repaint();
			} else if
			(axis.getMechanismName().equals("ALT")) {
				System.err.println("Recieved ALT status: "+status);
				long time = axis.getStatusTimeStamp();
				int altState = axis.getMechanismState();
				palt.addHistory(time, altState);
				if (paint)
				palt.repaint();
			} else if
			(axis.getMechanismName().equals("ROT")) {
				System.err.println("Recieved ROT status: "+status);
				long time = axis.getStatusTimeStamp();
				int rotState = axis.getMechanismState();
				prot.addHistory(time, rotState);
				if (paint)
				prot.repaint();
			}	
			if (paint)
			tap.repaint();
		}
	}

	
}
