/**
 * 
 */
package ngat.rcsgui.stable;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ngat.icm.InstrumentStatus;
import ngat.icm.InstrumentStatusUpdateListener;
import ngat.message.ISS_INST.GET_STATUS_DONE;

/**
 * @author eng
 * 
 */
public class InstrumentStatusHandler extends UnicastRemoteObject implements InstrumentStatusUpdateListener {

	Map<String, InstrumentStatusPanel> instMap;

	InstrumentHealthPanel instHealthPanel;

	Map<String, InstrumentDataPanel> dataPanels;

	LostDataHandlerThread lostDataHandler;

	/**
	 * @throws RemoteException
	 */
	public InstrumentStatusHandler(InstrumentHealthPanel instHealthPanel) throws RemoteException {
		super();
		this.instHealthPanel = instHealthPanel;
		instMap = new HashMap<String, InstrumentStatusPanel>();
		dataPanels = new HashMap<String, InstrumentDataPanel>();
		lostDataHandler = new LostDataHandlerThread();
	}

	public void addInstrument(String name, InstrumentStatusPanel isp, InstrumentDataPanel idp) {
		instMap.put(name, isp);
		dataPanels.put(name, idp);
		System.err.println("InstHdlr:AddInstrument: " + name + " with idp: " + idp);
	}

	public InstrumentDataPanel getDataPanel(String name) {
		return dataPanels.get(name);
	}

	/**
	 * Handle a status update for an instrument.
	 * 
	 * @param inst
	 *            A descriptor for the instrument.
	 * @param status
	 *            The new status update.
	 */
	@Override
	public void instrumentStatusUpdated(InstrumentStatus status) throws RemoteException {
		String name = status.getInstrument().getInstrumentName();
		
		System.err.println("ISH::Recieved status update for: " + name + ":" + 
		(status == null ? "None-available" : status.getStatus()));
		
		InstrumentStatusPanel isp = instMap.get(name);
		InstrumentDataPanel idp = dataPanels.get(name);
		isp.updateStatus(status);
		if (idp != null) {
			try {
			idp.update(status.getStatus());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		long time = System.currentTimeMillis();
		int state = -1;
		if (!status.isEnabled()) {
			state = InstrumentData.DISABLED;
		} else {
			if (status.isOnline()) {
			String tempStatus = (String) status.getStatus().get(GET_STATUS_DONE.KEYWORD_INSTRUMENT_STATUS);
			if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_OK.equals(tempStatus))
				state = InstrumentData.ONLINE_OKAY;
			else if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_WARN.equals(tempStatus))
				state = InstrumentData.ONLINE_WARN;
			else if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_FAIL.equals(tempStatus))
				state = InstrumentData.ONLINE_FAIL;
			else if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_UNKNOWN.equals(tempStatus))
				state = InstrumentData.ONLINE_OKAY;
			else
				// special case where instrument does not supply
				// Instrument.Status info
				state = InstrumentData.ONLINE_OKAY;
			} else {
			state = InstrumentData.OFFLINE;
			}
		}
		try {
			instHealthPanel.updateInstrument(name, time, state);
		} catch (Exception e) {
			e.printStackTrace();			
		}
	}

	public void startLostDataHandler() {
		lostDataHandler.start();
	}
	
	/**
	 * @author eng
	 * 
	 */
	public class LostDataHandlerThread extends Thread {

		public LostDataHandlerThread() {
			super("LDH");
		}

		@Override
		public void run() {

			while (true) {
				try {
					Thread.sleep(60000L);
				} catch (InterruptedException ix) {
				}

				// go over each ISP
				Iterator<InstrumentStatusPanel> ii = instMap.values().iterator();
				while (ii.hasNext()) {
					InstrumentStatusPanel isp = ii.next();
					long tls = System.currentTimeMillis() - isp.getTimeLastDataSample();
					if (tls > 300000L) {
						InstrumentStatus status = new InstrumentStatus();
						status.setEnabled(true);
						status.setFunctional(false);
						status.setOnline(false);
						isp.updateStatus(status);
					}
				}
			}
		}
	}

}
