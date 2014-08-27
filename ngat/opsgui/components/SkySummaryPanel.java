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
public class SkySummaryPanel extends SummaryPane {

	private StateColorMap extinctionStateMap;
	private StateColorMap seeingStateMap;

	private StateField seeingCategoryField;

	private StateField extinctionCategoryField;

	/**
	 * @param title
	 */
	public SkySummaryPanel(String title) {
		super(title);

		extinctionStateMap = new StateColorMap(Color.gray, "UNKNOWN");
		extinctionStateMap.addColorLabel(1, Color.green, "PHOTOMETRIC");
		extinctionStateMap.addColorLabel(2, Color.red, "SPECTROMETRIC");

		seeingStateMap = new StateColorMap(Color.gray, "UNKNOWN");

		seeingStateMap.addColorLabel(1, Color.green, "GOOD");
		seeingStateMap.addColorLabel(2, Color.orange, "AVERAGE");
		seeingStateMap.addColorLabel(3, Color.red, "POOR");
		seeingStateMap.addColorLabel(4, Color.cyan.darker(), "USABLE");

	}

	/**
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {

		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Seeing (Z/R)"));
		seeingCategoryField = ComponentFactory.makeStateField(12, seeingStateMap);
		l.add(seeingCategoryField);
		l.add(ComponentFactory.makeRedirectButton());
		add(l);

		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Extinction"));
		extinctionCategoryField = ComponentFactory.makeStateField(12, extinctionStateMap);
		l.add(extinctionCategoryField);
		add(l);

	}

	public void updateSeeing(long time, double prediction) {
		int seeingState = 0;
		if (prediction < 0.8)
			seeingState = 1;
		else if (prediction < 1.3)
			seeingState = 2;
		else if (prediction < 5.0)
			seeingState = 3;
		else
			seeingState = 4;

		seeingCategoryField.updateState(seeingState);
	}

	public void updateExtinction(long time, double extinction) {
		int extState = 0;
		if (extinction < 0.5)
			extState = 1;
		else
			extState = 2;
		extinctionCategoryField.updateState(extState);
	}

}
