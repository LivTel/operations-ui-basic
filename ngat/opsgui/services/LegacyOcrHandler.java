/**
 * 
 */
package ngat.opsgui.services;

import ngat.message.GUI_RCS.GET_STATUS_DONE;
import ngat.message.base.COMMAND_DONE;
import ngat.net.IConnection;
import ngat.net.camp.CAMPResponseHandler;
import ngat.opsgui.base.TopPanel;
import ngat.util.StatusCategory;

/**
 * @author eng
 *
 */
public class LegacyOcrHandler implements CAMPResponseHandler {

	private TopPanel topPanel;

	/**
	 * @param topPanel
	 */
	public LegacyOcrHandler(TopPanel topPanel) {
		super();
		this.topPanel = topPanel;
	}

	/* (non-Javadoc)
	 * @see ngat.net.camp.CAMPResponseHandler#failed(java.lang.Exception, ngat.net.IConnection)
	 */
	@Override
	public void failed(Exception e, IConnection connection) {
		// TODO Auto-generated method stub
		e.printStackTrace();	
		if (connection != null)
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

		System.err.println("GET_STATUS OCR received: "+update);
		
		if (!( update instanceof GET_STATUS_DONE)) {			
			return;
		}
		
		GET_STATUS_DONE gsd = (GET_STATUS_DONE)update;
		
		StatusCategory status = gsd.getStatus();
		
		int ocrStatus = status.getStatusEntryInt("state");
		double age = status.getStatusEntryDouble("age");
		
		topPanel.getAuxSystemsSummaryPanel().updateOcrStatus(ocrStatus);
		
	}

}
