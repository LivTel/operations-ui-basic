/**
 * This panel in a sub-panel that is put in TopPanel. It's displayed along the top of the opsgui,
 * and is actually meant to be showing the free space of the various instrument disks/main Nas.
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
		// Only add instrument disks when the instrument writes to it's own local disks
		// Some instruments write images to the NAS and don't need to be included here. 
		addLine("OCC");
		addLine("NAS-2");
		addLine("Rise");
		addLine("Moptop-1");
		addLine("Moptop-2");
		addLine("Autoguider");
	}
	
	private void addLine(String name) {
		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel(name));
		// usage percent
		DataField dfield1 = ComponentFactory.makeDataField(5, "%4.2f"); 		
		l.add(dfield1);
		// freespace Mb or Gb ??
		DataField dfield2 = ComponentFactory.makeDataField(5, "%6.2f");
		l.add(dfield2);
		add(l);
	}
	

}
