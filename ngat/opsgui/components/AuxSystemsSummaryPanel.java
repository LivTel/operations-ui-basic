/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;

/**
 * @author eng
 * 
 */
public class AuxSystemsSummaryPanel extends SummaryPane {

	private StateField ocrField;
	private StateField gcnField;
	private StateField teaField;
	private StateField baseField;
	private StateField smpField;
	private StateField schedField;

	private StateColorMap colors;

	/**
	 * @param title
	 */
	public AuxSystemsSummaryPanel(String title) {
		super(title);
		colors = new StateColorMap(Color.gray, "UNKNOWN");

		colors.addColorLabel(1, Color.blue, "OFFLINE");
		colors.addColorLabel(2, Color.green, "ONLINE");
		colors.addColorLabel(3, Color.red, "ERROR");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {

		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeFixedWidthLabel("NSO Interface", 80));
		ocrField = ComponentFactory.makeStateField(8, colors);
		l.add(ocrField);
		add(l);

		l = new LinePanel();
		l.add(ComponentFactory.makeFixedWidthLabel("RTML Interface", 80));
		teaField = ComponentFactory.makeStateField(8, colors);
		l.add(teaField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeFixedWidthLabel("GCN Interface", 80));
		gcnField = ComponentFactory.makeStateField(8, colors);
		l.add(gcnField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeFixedWidthLabel("Synoptics", 80));
		smpField = ComponentFactory.makeStateField(8, colors);
		l.add(smpField);
		add(l);

		l = new LinePanel();
		l.add(ComponentFactory.makeFixedWidthLabel("Base models", 80));
		baseField = ComponentFactory.makeStateField(8, colors);
		l.add(baseField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeFixedWidthLabel("Scheduler", 80));
		schedField = ComponentFactory.makeStateField(8, colors);
		l.add(schedField);
		add(l);
		
		

	}

	public void updateTeaStatus(int status) {
		//teaField.updateState(status);
	}

	public void updateOcrStatus(int status) {
		//System.err.println("OCR Update: "+status);
		ocrField.updateState(status);
	}

	public void updateBaseModelStatus(int status) {
		baseField.updateState(status);
	}

	public void updateSmpStatus(int status) {
		smpField.updateState(status);
	}
	
	public void updateSchedStatus(int status) {
		schedField.updateState(status);
	}

}
