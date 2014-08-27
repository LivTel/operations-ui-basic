/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.rmi.RemoteException;

import javax.swing.JLabel;
import javax.swing.JPanel;

import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusUpdateListener;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateIndicator;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeDisplayController;

/**
 * @author eng
 * 
 */
public class InstrumentTestPanel extends JPanel implements InstrumentStatusUpdateListener, ServiceAvailabilityListener {

	public static final Dimension LSIZE = new Dimension(70, 20);

	public static final Dimension HSIZE = new Dimension(200, 20);

	StatusHistoryPanel shp;
	StateIndicator si;

	public static final int OFFLINE = -1;
	public static final int DISABLED = -2;

	private TimeDisplayController tdc;
	
	static StateColorMap cmap = new StateColorMap(Color.gray, "UNKNOWN");
	static {
		cmap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_OKAY, Color.green, "OKAY");
		cmap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_WARN, Color.orange, "WARN");
		cmap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_FAIL, Color.red, "FAIL");
		cmap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_UNAVAILABLE, Color.gray, "UNAV");
		cmap.addColorLabel(DISABLED, Color.pink, "DISABLED");
		cmap.addColorLabel(OFFLINE, Color.blue, "OFFLINE");
	}

	/**
	 * Create an Inst status panel
	 * 
	 */
	public InstrumentTestPanel(String instrumentName) {
		super(true);
		tdc = new TimeDisplayController(3600*1000L);
		
		shp = new StatusHistoryPanel(tdc);
		shp.setPreferredSize(HSIZE);	
		shp.setMap(cmap);
		tdc.addTimeDisplay(shp);
		si = new StateIndicator();
		si.setPreferredSize(LSIZE);
		si.setMap(cmap);
		setLayout(new FlowLayout(FlowLayout.LEADING));
		JLabel l = new JLabel(instrumentName);
		l.setPreferredSize(LSIZE);
		add(l);
		add(shp);
		add(si);
	}

	@Override
	public void instrumentStatusUpdated(InstrumentStatus status) throws RemoteException {
		int state = 0;
		String stext = "Unknown";
		if (!status.isEnabled()) {
			state = DISABLED;
			stext = "disabled";
		} else {
			if (status.isOnline()) {
				state = (status.isFunctional() ? InstrumentStatus.OPERATIONAL_STATUS_OKAY
						: InstrumentStatus.OPERATIONAL_STATUS_FAIL);
				stext = (status.isFunctional() ? "okay" : "fail");

			} else {
				state = OFFLINE;
				stext = "offline";
				System.err.printf("ICS: %20s at %tF %tT \n", status.getInstrument().getInstrumentName(), status.getStatusTimeStamp(),
						status.getStatusTimeStamp());
			}
		}
		shp.addHistory(status.getStatusTimeStamp(), state);
		shp.repaint();
		si.updateState(state, stext);
		si.repaint();
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		if (!available) {
			shp.addHistory(time, InstrumentStatus.OPERATIONAL_STATUS_UNAVAILABLE);
			shp.repaint();
			si.updateState(InstrumentStatus.OPERATIONAL_STATUS_UNAVAILABLE, "unknown");
			si.repaint();
		}
	}

}
