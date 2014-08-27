/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.rmi.RemoteException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ngat.ems.SkyModelUpdateListener;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateIndicator;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeDisplayController;
import ngat.rcsgui.test.SkyModelSeeingHistogram;

/**
 * @author eng
 * 
 */
public class SeeingPanelTest extends JPanel implements SkyModelUpdateListener, ServiceAvailabilityListener {

	public static final Dimension LSIZE = new Dimension(70, 20);
	public static final Dimension GSIZE = new Dimension(100, 100);
	public static final Dimension HSIZE = new Dimension(200, 20);

	public static final int NODATA = 0;
	public static final int POOR = 2;
	public static final int AVER = 3;
	public static final int GOOD = 4;
	public static final int USAB = 1;

	private SkyModelSeeingHistogram shraw;
	private SkyModelSeeingHistogram shcor;
	private StatusHistoryPanel shp;

	private StateIndicator si;
	private JTextField cf;
	private JTextField rf;
	
	static StateColorMap cmap = new StateColorMap(Color.gray, "UNKNOWN");
	static {
		cmap.addColorLabel(POOR, Color.red, "POOR");
		cmap.addColorLabel(AVER, Color.orange, "AVER");
		cmap.addColorLabel(GOOD, Color.green, "GOOD");
		cmap.addColorLabel(USAB, Color.blue, "USAB");
		cmap.addColorLabel(NODATA, Color.gray, "NODATA");
	}

	/**
	 * @param sh
	 * @param shp
	 * @param si
	 * @throws RemoteException
	 */
	public SeeingPanelTest() {
		super(true);
		setLayout(new BorderLayout());

		TimeDisplayController tdc = new TimeDisplayController(7200*1000L);
		
		shp = new StatusHistoryPanel(tdc);
		tdc.addTimeDisplay(shp);
		shp.setPreferredSize(HSIZE);	
		shp.setMap(cmap);

		si = new StateIndicator();
		si.setPreferredSize(LSIZE);
		si.setMap(cmap);

		JLabel l = new JLabel("Seeing");
		l.setPreferredSize(LSIZE);

		JPanel top = new JPanel(true);
		top.setLayout(new FlowLayout(FlowLayout.LEADING));

		shraw = new SkyModelSeeingHistogram("Raw seeing");
		shraw.setPreferredSize(GSIZE);
		shcor = new SkyModelSeeingHistogram("Corrected seeing");
		shcor.setPreferredSize(GSIZE);
		top.add(l);
		top.add(shp);
		top.add(si);

		JPanel mid = new JPanel(true);
		mid.setLayout(new GridLayout(2,2));
		
		JLabel rl = new JLabel("Raw");
		rl.setPreferredSize(LSIZE);
		mid.add(rl);
		rf = new JTextField();
		rf.setPreferredSize(LSIZE);
		mid.add(rf);
		
		JLabel cl = new JLabel("Corrected");
		cl.setPreferredSize(LSIZE);
		mid.add(cl);
		cf = new JTextField();
		cf.setPreferredSize(LSIZE);
		mid.add(cf);
		
		JPanel bot = new JPanel(true);
		bot.setLayout(new FlowLayout(FlowLayout.LEADING));
		bot.add(shraw);
		bot.add(shcor);

		add(top, BorderLayout.CENTER);
		add(mid, BorderLayout.NORTH);
		add(bot,BorderLayout.SOUTH);

	}

	@Override
	public void extinctionUpdated(long arg0, double arg1) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void seeingUpdated(long time, double raw, double corrected, double prediction, double alt, double azm, double wav, boolean standard, String source, String targetName) throws RemoteException {
		System.err.printf("SPT received %3.3s update from %8.8s: %tF %tT %4.2f %4.2f %4.2f \n", (standard ? "STD":"SCI"), source, time, time, raw, corrected, prediction);
		
		int rsee = USAB;
		if (raw < 0.8)
			rsee = GOOD;
		else if (raw < 1.3)
			rsee = AVER;
		else if (raw < 5.0)
			rsee = POOR;

		int csee = USAB;
		String css = "USAB";
		if (corrected < 0.8) {
			csee = GOOD;
			css = "GOOD";
		} else if (corrected < 1.3) {
			csee = AVER;
			css = "AVER";
		} else if (corrected < 5.0) {
			csee = POOR;
			css = "POOR";
		}
		shp.addHistory(time, csee);
		shp.repaint();
		si.updateState(csee, css);
		si.repaint();

		shraw.updateSeeing(raw);
		shcor.updateSeeing(corrected);

		rf.setText(String.format("%4.2f",raw));
		cf.setText(String.format("%4.2f",corrected));
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		if (!available) {
			shp.addHistory(time, NODATA);
			shp.repaint();
			si.updateState(NODATA, "unknown");
			si.repaint();
		}
	}
}
