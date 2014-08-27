/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentStatus;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.services.ServiceManager;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;
import ngat.tcm.AutoguiderActiveStatus;

/**
 * @author eng
 * 
 */
public class InstrumentSummaryPanel extends SummaryPane {



	public static final int OKAY = 1;
	public static final int FAIL = 2;
	public static final int OFFLINE = 3;
	public static final int DISABLED = 4;

	private StateColorMap stateMap;
	
	private StateColorMap agStateMap;
	

	private List<InstrumentDescriptor> instruments;

	private Map<InstrumentDescriptor, InstrumentStatusHandler> instrumentHandlerMap;
	
	private StateField agField;

	private DataField agTempField;
	
	/**
	 * @param title
	 */
	public InstrumentSummaryPanel(String title, List<InstrumentDescriptor> instruments) {
		super(title);
		this.instruments = instruments;
		stateMap = new StateColorMap(Color.gray, "UNKNOWN");
		// TODO currently IStat does not return detailed status info to
		// distinguish warn/fail
		// stateMap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_FAIL,
		// Color.red, "FAIL");
		// stateMap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_WARN,
		// Color.orange, "WARN");
		// stateMap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_OKAY,
		// Color.green, "OKAY");
		// stateMap.addColorLabel(InstrumentStatus.OPERATIONAL_STATUS_UNAVAILABLE,
		// Color.green, "N/A");
		// stateMap.addColorLabel(InstrumentStatus.OFFLINE., Color.blue,
		// "OFFLINE");
		// stateMap.addColorLabel(InstrumentStatus.DISABLED, Color.pink,
		// "DISABLED");
		stateMap.addColorLabel(OKAY, Color.green, "OKAY");
		stateMap.addColorLabel(FAIL, Color.red, "FAIL");
		stateMap.addColorLabel(OFFLINE, Color.blue, "OFFLINE");
		stateMap.addColorLabel(DISABLED, Color.pink, "DISABLED");
	
		
		agStateMap = new StateColorMap(Color.gray, "UNKNOWN");
		agStateMap.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_FAIL_HIGH, Color.red, "FAIL_HIGH");
		agStateMap.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_WARN_HIGH, Color.orange, "WARN_HIGH");
		agStateMap.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_OKAY,      Color.green, "OKAY");
		agStateMap.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_WARN_LOW, Color.orange, "WARN_LOW");
		agStateMap.addColorLabel(AutoguiderActiveStatus.AUTOGUIDER_TEMPERATURE_FAIL_LOW, Color.red, "FAIL_LOW");
		
		instrumentHandlerMap = new HashMap<InstrumentDescriptor, InstrumentStatusHandler>();
	}

	/**
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
		
		if (instruments == null)
			return;
		
		for (int ii = 0; ii < instruments.size(); ii++) {
			InstrumentDescriptor id = instruments.get(ii);
			System.err.println("Create instpanel for: " + id);
			
			InstrumentMainPanel panel = new InstrumentMainPanel(id);
			InstrumentStatusHandler handler = new InstrumentStatusHandler(panel);
			add(panel);
			
			List<InstrumentDescriptor> sublist = id.listSubcomponents();
			if (sublist.size() != 0) {
				
				for (int is = 0; is < sublist.size(); is++) {
					InstrumentDescriptor sid = sublist.get(is);				
					InstrumentSubPanel subPanel = new InstrumentSubPanel(sid);
					panel.addPanel(id.getInstrumentName()+"_"+sid.getInstrumentName(), subPanel);
					add(subPanel);					
				}
			} 
			
			instrumentHandlerMap.put(id, handler);
		
			
		}
		// Autoguider
					agField = ComponentFactory.makeStateField(8, agStateMap);
					LinePanel agp = ComponentFactory.makeLinePanel();
					agp.add(ComponentFactory.makeLabel("Autoguider"));
					agTempField = ComponentFactory.makeDataField(5, "%4.2f");
					agp.add(agTempField);
					agp.add(agField);
				
					add(agp);
	}

	public void updateInstrument(InstrumentStatus status) {
	
		InstrumentDescriptor id = status.getInstrument();
		InstrumentStatusHandler handler = instrumentHandlerMap.get(id);
		handler.updateInstrument(status);
		
	}

	public void updateAutoguider(AutoguiderActiveStatus agstatus) {
		
		agTempField.updateData(agstatus.getTemperature());
		agField.updateState(agstatus.getTemperatureStatus());
		
	}
	
	
	private class InstrumentMainPanel extends LinePanel {
		
		private InstrumentDescriptor id;
		private StateField stateField;
		private DataField tempField;
		
		Map<String, InstrumentSubPanel> panels;
		
		public InstrumentMainPanel(InstrumentDescriptor id) {
			super();
			this.id = id;	
			panels = new HashMap<String, InstrumentSubPanel>();
			
			add(ComponentFactory.makeLabel(id.getInstrumentName()));
			
			
			// we only add a temp field if this instrument has NO sub-insts
			List<InstrumentDescriptor> sublist = id.listSubcomponents();
			if (sublist.size() == 0) {
				tempField = ComponentFactory.makeDataField(5, "%4.2f");
				add(tempField);
			} else {
				add(ComponentFactory.makeFixedWidthLabel("", 45));
			}
			
			stateField = ComponentFactory.makeStateField(8, stateMap);
			add(stateField);
			//add(ComponentFactory.makeRedirectButton());
			
		}
		
		// add panels for each sub inst if any or just one panel
		public void addPanel(String fullId, InstrumentSubPanel panel) {
			panels.put(fullId, panel);
		}
		
		public void updateInstrument(InstrumentStatus status) {		
			
			System.err.println("IMP: UPDATE INSTR: "+status.getInstrument().getInstrumentName());
			
			int state = (status.isEnabled() ? 
					(status.isOnline() ? 
					(status.isFunctional() ? 
						OKAY : FAIL) : OFFLINE) : DISABLED);
			
			stateField.updateState(state);
			
			// nothing else valid if its offline
			if (!status.isOnline()) {
				// we could update the temeprature as INVALID/UNKNOWN 
				return;
			}
			
			if (tempField != null) {	
				System.err.println("IMP: UPDATE INSTR: Update single temeprature");
				String tkw = "Temperature";
				double temperature = ((Double)status.getStatus().get(tkw)).doubleValue();
				tempField.updateData(temperature);	
			} else {
				System.err.println("IMP: UPDATE INSTR: Update subpanel temperatures");
				// we have sub-panels
				List<InstrumentDescriptor> sublist = id.listSubcomponents();
				for (int is = 0; is < sublist.size(); is++) {
					InstrumentDescriptor sid = sublist.get(is);
					String fullid = id.getInstrumentName()+"_"+sid.getInstrumentName();
					
					String basekey = "Temperature";
					String prefix = sid.getTemperatureKeywordPrefix();
					if (prefix == null)
						prefix = "";
					String suffix = sid.getTemperatureKeywordSuffix();
					if (suffix == null)
						suffix = "";
					String tkw = prefix + basekey + suffix; 
				
					double temperature = ((Double)status.getStatus().get(tkw)).doubleValue();
					System.err.println("IMP: UPDATE INSTR: Temp value was: "+temperature);
					InstrumentSubPanel panel = panels.get(fullid);
					panel.updateInstrument(temperature);
				}
			}
		}
		
	}
	
	
	private class InstrumentSubPanel extends LinePanel {
		
		private InstrumentDescriptor id;
		private DataField tempField;
		
		public InstrumentSubPanel(InstrumentDescriptor id) {
			super();
			this.id = id;
		
			add(ComponentFactory.makeLabel("   "+id.getInstrumentName()));
			tempField = ComponentFactory.makeDataField(5, "%4.2f");
			add(tempField);
		}
		
		public void updateInstrument(double temperature) {		
				tempField.updateData(temperature);	
		}
		
	}

	public class InstrumentStatusHandler {
	
		InstrumentMainPanel panel;
		
		InstrumentStatusHandler(InstrumentMainPanel panel) {
			this.panel = panel;
		}
		
		
		public void updateInstrument(InstrumentStatus status) {
			panel.updateInstrument(status);	
		}
		
	}
	
	
}
