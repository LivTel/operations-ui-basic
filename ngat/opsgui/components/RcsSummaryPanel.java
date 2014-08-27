/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;

import ngat.message.GUI_RCS.ID_DONE;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;

/**
 * @author eng
 *
 */
public class RcsSummaryPanel extends SummaryPane {
	
	StateColorMap stateModelStateMap;
	StateColorMap opsManagerStateMap;
	StateColorMap aicModeMap;
	
	StateField stateModelStateField;
	StateField operationsManagerStateField;
	StateField modeStateField;
	DataField uptimeField;
	
	
	
	/**
	 * @param title
	 */
	public RcsSummaryPanel(String title) {
		super(title);
		
		stateModelStateMap = new StateColorMap(Color.gray, "UNKNOWN");	
		stateModelStateMap.addColorLabel(1, Color.blue, "ENGINEERING");
		stateModelStateMap.addColorLabel(2, Color.orange, "STANDBY");
		stateModelStateMap.addColorLabel(3, Color.red, "STARTING");
		stateModelStateMap.addColorLabel(4, Color.cyan, "OPENING");
		stateModelStateMap.addColorLabel(5, Color.green, "OPERATIONAL");
		stateModelStateMap.addColorLabel(6, Color.cyan, "CLOSING");
		stateModelStateMap.addColorLabel(7, Color.red, "STOPPING");
		stateModelStateMap.addColorLabel(8, Color.red, "SHUTDOWN");
		
		opsManagerStateMap = new StateColorMap(Color.gray, "UNKNOWN");
		opsManagerStateMap.addColorLabel(1, Color.orange, "IDLE");
		opsManagerStateMap.addColorLabel(2, Color.yellow,"INITIALIZING");	
		opsManagerStateMap.addColorLabel(3, Color.yellow,"FINALIZING");		
		opsManagerStateMap.addColorLabel(4, Color.green,"OBSERVING");
		opsManagerStateMap.addColorLabel(5, Color.cyan.darker(), "MODE_SWITCH");
		
		aicModeMap = new StateColorMap(Color.gray, "UNKNOWN");
		aicModeMap.addColorLabel(ID_DONE.IDLE, Color.orange, "NONE");
		aicModeMap.addColorLabel(ID_DONE.TOCA_MODE, Color.pink, "TOCA");
		aicModeMap.addColorLabel(ID_DONE.SOCA_MODE, Color.yellow.darker(), "SOCA");
		aicModeMap.addColorLabel(ID_DONE.BGCA_MODE, Color.cyan, "BGCA");
		aicModeMap.addColorLabel(ID_DONE.CAL_MODE, Color.pink.darker(), "CALIB");
		aicModeMap.addColorLabel(ID_DONE.X_MODE, Color.orange.darker(), "X");
						
						
	}
	
	/**
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
	
		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Status"));
		stateModelStateField = ComponentFactory.makeStateField(8, stateModelStateMap);
		l.add(stateModelStateField);
		add(l);
		
		 l = new LinePanel();
		 l.add(ComponentFactory.makeLabel("Ops Mgr"));
		 operationsManagerStateField = ComponentFactory.makeStateField(8, opsManagerStateMap);
		 l.add(operationsManagerStateField);
		add(l);
			
		 l = new LinePanel();
		 l.add(ComponentFactory.makeLabel("Mode"));
		 modeStateField = ComponentFactory.makeStateField(8, aicModeMap);		
		 l.add(modeStateField);
		 add(l);
		 
		 l = new LinePanel();
		 l.add(ComponentFactory.makeLabel("Uptime"));
		 uptimeField = ComponentFactory.makeDataField(8, "%tM %tS"); 
		 l.add(uptimeField);
		 add(l);
		 
	}
	
	
	public void updateNewStateField(int cs) {
		stateModelStateField.updateState(cs);
	}

	public void updateNewOpField(int cs) {
		operationsManagerStateField.updateState(cs);
	}
	
	public void updateObsMode(String modeStr, int aidId) {
		//modeStateField.setText(modeStr);
		modeStateField.updateState(aidId);
	}
	
	public void updateUptime(long uptime) {
		
		// uptime is in musec
		
		// colorize and format to: zH xM [yS] 
		Color color = Color.green;
		if (uptime < 10*60*1000)
			color = Color.red;
		else if
			(uptime < 30*60*1000)
			color = Color.orange;
		else if
		(uptime < 60*60*1000)
		color = Color.cyan;
		
		uptimeField.setBackground(color);
		if (uptime > 60*60*1000) {
			int uh = (int)Math.floor(uptime/3600000.0);
			int um = (int)Math.floor((uptime - 3600000.0*uh)/60000);
			uptimeField.setText(""+uh+"h "+um+"m");
		} else {
			int um = (int)Math.floor(uptime/60000.0);
			int us = (int)Math.floor((uptime - 60000.0*um)/1000);
			uptimeField.setText(""+um+"m "+us+"s");
		}
		//uptimeField.u
	}
	
	
}
