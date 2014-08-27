/**
 * 
 */
package ngat.opsgui.services;

import ngat.message.GUI_RCS.ID_DONE;
import ngat.message.base.COMMAND_DONE;
import ngat.net.IConnection;
import ngat.net.camp.CAMPResponseHandler;
import ngat.opsgui.components.RcsSummaryPanel;
import ngat.rcsgui.stable.ColorStatePanel2;
import ngat.rcsgui.stable.RcsStatePanel;

/**
 * @author eng
 *
 */
public class LegacyIdHandler implements CAMPResponseHandler {

	private ColorStatePanel2 csp;
	
	private RcsStatePanel rsp;
	
	RcsSummaryPanel rsup;
	
	/**
	 * @param csp
	 */
	public LegacyIdHandler(ColorStatePanel2 csp, RcsStatePanel rsp, RcsSummaryPanel rsup) {
		super();
		this.csp = csp;
		this.rsp = rsp;
		this.rsup = rsup;
	}

	/* (non-Javadoc)
	 * @see ngat.net.camp.CAMPResponseHandler#failed(java.lang.Exception, ngat.net.IConnection)
	 */
	@Override
	public void failed(Exception e, IConnection connection) {		
		e.printStackTrace();	
		connection.close();
	}

	/* (non-Javadoc)
	 * @see ngat.net.camp.CAMPResponseHandler#handleUpdate(ngat.message.base.COMMAND_DONE, ngat.net.IConnection)
	 */
	@Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {

		if (connection != null)
			connection.close();

		// logger.log(1,"GUI", "-","-", "Received update: "+update);

		if (!( update instanceof ID_DONE)) {			
			return;
		}

		ID_DONE idd = (ID_DONE) update;
		// #### TEMP - needs agentDesc field in ID_DONE
		String aid = idd.getAgentInControl();
		String adesc = "";
		int aidId = 0;
		
		if (aid != null) {
			 if (aid.equals("BGCA")) {
				adesc = "Background";
				aidId = ID_DONE.BGCA_MODE;
			 } else if (aid.equals("TOCA")) {
				adesc = "Target of Opp";
				aidId = ID_DONE.TOCA_MODE;
			} else if (aid.equals("XCA")) {
				adesc = "Experimental";
				aidId = ID_DONE.X_MODE;
			} else if (aid.equals("CAL")) {
				adesc = "Calibration";
				aidId = ID_DONE.CAL_MODE;
			} else if (aid.equals("SOCA")) {
				adesc = "Scheduled";
				aidId = ID_DONE.SOCA_MODE;
			} else {
				adesc = "Unknown";
				aidId = ID_DONE.IDLE;
			}

		}
		
		long uptime = idd.getUptime();
		System.err.println("LSM: update: Uptime: "+(uptime/1000)+"S, aid = "+aid+", adesc = "+adesc);
		
		csp.updateMode(aid);
		rsp.updateObsMode(adesc, aidId);
		rsp.updateUptime(uptime);
		rsup.updateObsMode(adesc, aidId);
		rsup.updateUptime(uptime);
	}

}
