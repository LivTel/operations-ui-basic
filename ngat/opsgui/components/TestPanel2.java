/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;

/**
 * @author eng
 *
 */
public class TestPanel2 extends SummaryPane {
	
	private StateColorMap colorMap;
	
	/**
	 * @param title
	 */
	public TestPanel2(String title) {
		super(title);
		colorMap = new StateColorMap(Color.gray, "N-Y-K");
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel("test1"));
		StateField azmField = ComponentFactory.makeStateField(8, colorMap);
		l.add(azmField);
		DataField azmPosField = ComponentFactory.makeDataField(5, "%4.2f");
		l.add(azmPosField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("test2"));
		StateField azm2Field = ComponentFactory.makeStateField(8, colorMap);
		l.add(azm2Field);
		DataField azmPos2Field = ComponentFactory.makeDataField(5, "%4.2f");
		l.add(azmPos2Field);
		add(l);

	}

}
