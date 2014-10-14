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
public class TestPanel1 extends SummaryPane {

	private StateColorMap colorMap;
	
	/**
	 * @param title
	 */
	public TestPanel1(String title) {
		super(title);
		
		colorMap = new StateColorMap(Color.gray, "N-Y-K");
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
		// TODO Auto-generated method stub
		addLine("Spare 1");
		addLine("Spare 2");
		addLine("Spare 3");
		addLine("Spare 4");
		addLine("Spare 5");
		addLine("Spare 6");
	}
	
	private void addLine(String name) {
		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel(name));
		StateField azm3Field = ComponentFactory.makeStateField(8, colorMap);
		l.add(azm3Field);
		DataField azmPos3Field = ComponentFactory.makeDataField(5, "%4.2f");
		l.add(azmPos3Field);
		add(l);
	}
	

}
