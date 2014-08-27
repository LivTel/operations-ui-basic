/**
 * 
 */
package ngat.opsgui.services;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import ngat.message.GUI_RCS.GET_STATE_MODEL_DONE;
import ngat.message.base.COMMAND_DONE;
import ngat.net.IConnection;
import ngat.net.camp.CAMPResponseHandler;
import ngat.opsgui.components.RcsSummaryPanel;
import ngat.opsgui.components.StateVariableSummaryPanel;
import ngat.rcsgui.stable.ColorStatePanel2;
import ngat.rcsgui.stable.RcsStatePanel;
import ngat.rcsgui.stable.ToolMenu;

/**
 * @author eng
 *
 */
public class LegacyStateModelHandler implements CAMPResponseHandler {

	private ColorStatePanel2 csp;
	
	private StateVariableSummaryPanel svp;
	
	private RcsStatePanel rsp;
	
	private RcsSummaryPanel rsup;
	
	private ToolMenu toolMenu;
	
	/**
	 * @param csp
	 */
	public LegacyStateModelHandler(ColorStatePanel2 csp, StateVariableSummaryPanel svp, RcsStatePanel rsp, RcsSummaryPanel rsup, ToolMenu toolMenu) {
		super();
		this.csp = csp;
		this.svp = svp;
		this.rsp = rsp;
		this.rsup = rsup;
		this.toolMenu = toolMenu;
	}

	/* (non-Javadoc)
	 * @see ngat.net.camp.CAMPResponseHandler#failed(java.lang.Exception, ngat.net.IConnection)
	 */
	@Override
	public void failed(Exception e, IConnection connection) {		
		e.printStackTrace();	
		if (connection != null)
			connection.close();
		//rsp.updateNewOpField(cop);
		//rsp.updateNewStateField(cs);
		//rsup.updateNewOpField(cop);
		//rsup.updateNewStateField(cs);
		rsup.updateServiceStatus(System.currentTimeMillis(), false, "");
		svp.updateServiceStatus(System.currentTimeMillis(), false, "");
	}

	/* (non-Javadoc)
	 * @see ngat.net.camp.CAMPResponseHandler#handleUpdate(ngat.message.base.COMMAND_DONE, ngat.net.IConnection)
	 */
	@Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
		if (connection != null)
			connection.close();

		// logger.log(1,"GUI", "-","-", "Received update: "+update);

		if (!(update instanceof GET_STATE_MODEL_DONE)) {
			return;
		}

		HashMap map = ((GET_STATE_MODEL_DONE) update).getVariables();

		int cs = ((GET_STATE_MODEL_DONE) update).getCurrentState();
		int cop = ((GET_STATE_MODEL_DONE) update).getCurrentOperation();

		//updateNewStateField(cs);
		//updateNewOpField(cop);

		//updateNewVariableFields(map);

		//statePanel.validate();
		
		System.err.println("LSM: update: cs = "+cs+" cop = "+cop);
		csp.updateState(cs);
		
		updateHistory(map);	
		svp.updateStates(map);
		svp.updateServiceStatus(System.currentTimeMillis(), true, "");
		rsp.updateNewOpField(cop);
		rsp.updateNewStateField(cs);
		rsup.updateServiceStatus(System.currentTimeMillis(), true, "");
		rsup.updateNewOpField(cop);
		rsup.updateNewStateField(cs);
		toolMenu.updateOperationalState(cs);
		
		
	}

	private void updateHistory(Map map) {

		String sl = "UNKNOWN";
	

		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {

			String var = (String) it.next();
		
			Integer vv = (Integer) map.get(var);
			if (vv == null)
				continue;
			int cs = vv.intValue();
			

			csp.updateSysvar(cs);


		}

	}
	
	private void updateSysvars(Map map) {

	

	}
	
	
}
