/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;
import ngat.rcs.newstatemodel.EnvironmentChangeEvent;

/**
 * @author eng
 *
 */
public class StateVariableSummaryPanel extends SummaryPane {

	StateColorMap stateMap;
	
	StateField systemField;
	StateField axesField;
	StateField weatherField;
	StateField enclosureField;
	StateField controlField;
	StateField networkField;
	StateField intentField;
	StateField periodField;
	StateField pmcField;
	StateField powerStateField;
	
	private StateColorMap periodStateMap;

	private StateColorMap intentStateMap;

	private StateColorMap networkStateMap;

	private StateColorMap ctrlStateMap;

	private StateColorMap encStateMap;
	
	private StateColorMap pmcStateMap;

	private StateColorMap axesStateMap;

	private StateColorMap meteoStateMap;

	private StateColorMap systemStateMap;
	
	private StateColorMap powerStateMap;
	
	/**
	 * @param title
	 */
	public StateVariableSummaryPanel(String title) {
		super(title);	
	
		periodStateMap  = new StateColorMap(Color.gray, "UNKNOWN");
		periodStateMap.addColorLabel(EnvironmentChangeEvent.DAY_TIME, Color.orange, "DAYTIME");
		periodStateMap.addColorLabel(EnvironmentChangeEvent.NIGHT_TIME, Color.blue, "NIGHTTIME");
		
		networkStateMap = new StateColorMap(Color.gray, "UNKNOWN");
		networkStateMap.addColorLabel(EnvironmentChangeEvent.NETWORK_OKAY, Color.green, "CONNECTED");
		networkStateMap.addColorLabel(EnvironmentChangeEvent.NETWORK_ALERT, Color.red, "OFFLINE");
		
		intentStateMap  = new StateColorMap(Color.gray, "UNKNOWN");
		intentStateMap.addColorLabel(EnvironmentChangeEvent.INTENT_OPERATIONAL, Color.green, "AUTOMATIC");
		intentStateMap.addColorLabel(EnvironmentChangeEvent.INTENT_ENGINEERING, Color.red, "MANUAL");
		
		ctrlStateMap    = new StateColorMap(Color.gray, "UNKNOWN");
		ctrlStateMap.addColorLabel(EnvironmentChangeEvent.CONTROL_ENABLED, Color.green, "ENABLED");
		ctrlStateMap.addColorLabel(EnvironmentChangeEvent.CONTROL_DISABLED, Color.red, "DISABLED");
		
		encStateMap     = new StateColorMap(Color.gray, "UNKNOWN");
		encStateMap.addColorLabel(EnvironmentChangeEvent.ENCLOSURE_OPEN, Color.green, "OPEN");
		encStateMap.addColorLabel(EnvironmentChangeEvent.ENCLOSURE_CLOSED, Color.red, "CLOSED");		
		
		pmcStateMap     = new StateColorMap(Color.gray, "UNKNOWN");
		pmcStateMap.addColorLabel(EnvironmentChangeEvent.MIRR_COVER_OPEN, Color.green, "OPEN");
		pmcStateMap.addColorLabel(EnvironmentChangeEvent.MIRR_COVER_CLOSED, Color.red, "CLOSED");		
		
		
		axesStateMap    = new StateColorMap(Color.gray, "UNKNOWN");
		axesStateMap.addColorLabel(EnvironmentChangeEvent.AXES_OKAY, Color.green, "OKAY");
		axesStateMap.addColorLabel(EnvironmentChangeEvent.AXES_ERROR, Color.red, "ERROR");
		
		meteoStateMap   = new StateColorMap(Color.gray, "UNKNOWN");
		meteoStateMap.addColorLabel(EnvironmentChangeEvent.WEATHER_CLEAR, Color.green, "CLEAR");
		meteoStateMap.addColorLabel(EnvironmentChangeEvent.WEATHER_ALERT, Color.red, "ALERT");
		
		systemStateMap  = new StateColorMap(Color.gray, "UNKNOWN");
		systemStateMap.addColorLabel(EnvironmentChangeEvent.SYSTEM_OKAY, Color.green, "OKAY");
		systemStateMap.addColorLabel(EnvironmentChangeEvent.SYSTEM_STANDBY, Color.yellow.darker(), "STANDY");
		systemStateMap.addColorLabel(EnvironmentChangeEvent.SYSTEM_SUSPEND, Color.red, "SUSPEND");
		systemStateMap.addColorLabel(EnvironmentChangeEvent.SYSTEM_FAIL, Color.red.darker(), "FAIL");
		
		powerStateMap = new StateColorMap(Color.gray, "UNKNOWN");
		powerStateMap.addColorLabel(EnvironmentChangeEvent.OP_RUN, Color.green, "RUN");
		powerStateMap.addColorLabel(EnvironmentChangeEvent.OP_REBOOT, Color.red, "REBOOT");
		powerStateMap.addColorLabel(EnvironmentChangeEvent.OP_RESTART_AUTO, Color.red, "START AUTO");
		powerStateMap.addColorLabel(EnvironmentChangeEvent.OP_RESTART_ENG, Color.red, "START ENG");
		powerStateMap.addColorLabel(EnvironmentChangeEvent.OP_RESTART_INSTR, Color.magenta, "BOOT INSTR");
		                                                                              
		
		
		
	}
	
	/**
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
		LinePanel l = new LinePanel();
		
		l.add(ComponentFactory.makeLabel("System"));
		systemField = ComponentFactory.makeStateField(8, systemStateMap);
		l.add(systemField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Axes"));
		axesField = ComponentFactory.makeStateField(8, axesStateMap);
		l.add(axesField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Weather"));
		weatherField = ComponentFactory.makeStateField(8, meteoStateMap);
		l.add(weatherField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Enclosure"));
		enclosureField = ComponentFactory.makeStateField(8, encStateMap);
		l.add(enclosureField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Mirr_Cover"));
		pmcField = ComponentFactory.makeStateField(8, pmcStateMap);
		l.add(pmcField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Control"));
		controlField = ComponentFactory.makeStateField(8, ctrlStateMap);
		l.add(controlField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Network"));
		networkField = ComponentFactory.makeStateField(8, networkStateMap);
		l.add(networkField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Intent"));
		intentField = ComponentFactory.makeStateField(8, intentStateMap);
		l.add(intentField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Period"));
		periodField = ComponentFactory.makeStateField(8, periodStateMap);
		l.add(periodField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Power"));
		powerStateField = ComponentFactory.makeStateField(8, powerStateMap);
		l.add(powerStateField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);
		
		
		
	}
	
	// TODO TEMP link up with legacy SM handler
	public void updateStates(Map map) {
	
		
		Iterator it = map.keySet().iterator();
		while (it.hasNext()) {

			String key = (String) it.next();	
			Integer vv = (Integer) map.get(key);
			
			if (vv == null)
				continue;
			int cs = vv.intValue();
			
			
			switch (cs) {
			case 1:			
			case 2:			
			case 3:			
			case 4:
				systemField.updateState(cs);
				break;
			case 5:				
			case 6:
				controlField.updateState(cs);
				break;
			case 7:				
			case 8:
				networkField.updateState(cs);
				break;
			case 9:			
			case 10:
				weatherField.updateState(cs);
				break;
			case 11:	
			case 12:				
			case 13:
				enclosureField.updateState(cs);
				break;
			case 14:				
			case 15:
				axesField.updateState(cs);
				break;
			case 16:				
			case 17:
				intentField.updateState(cs);
				break;
			case 18:			
			case 19:
				periodField.updateState(cs);
				break;
			case EnvironmentChangeEvent.OP_RUN:
			case EnvironmentChangeEvent.OP_REBOOT:
			case EnvironmentChangeEvent.OP_RESTART_AUTO:
			case EnvironmentChangeEvent.OP_RESTART_ENG:
			case EnvironmentChangeEvent.OP_RESTART_INSTR:
				powerStateField.updateState(cs);
				break;
			case 25:				
			case 26:				
			case 27:
				pmcField.updateState(cs);
				break;
			}		

		}
		
		
	}
	
	
	
	
}
