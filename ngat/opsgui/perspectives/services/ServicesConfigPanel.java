package ngat.opsgui.perspectives.services;

import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.components.LinePanel;

public class ServicesConfigPanel extends JPanel {
		
    /**
     * 
     */
    public ServicesConfigPanel() {
	super(true);
	
	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
	
	add(createLinePanel("TCM"));
	add(createLinePanel("RATCAM"));
	add(createLinePanel("RISE"));
	add(createLinePanel("FRODO"));
		add(createLinePanel("IO:O"));
		add(createLinePanel("IO:THOR"));
		add(createLinePanel("RINGO3"));
		add(createLinePanel("SCHED"));
		add(createLinePanel("RCS_OPS"));
		add(createLinePanel("RCS_TMM"));
		add(createLinePanel("RCS_SM"));
		add(createLinePanel("SKY"));
		add(createLinePanel("METEO"));	
		add(createLinePanel("SMP"));
		add(createLinePanel("OSS"));
		add(createLinePanel("CAMS"));	
	}

	private LinePanel createLinePanel(String serviceName) {
		
		LinePanel linePanel = ComponentFactory.makeLinePanel();
	
		linePanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		linePanel.add(ComponentFactory.makeLabel(serviceName));
		linePanel.add(ComponentFactory.makeEntryField(8));
		linePanel.add(ComponentFactory.makeUnsizedLabel(":"));	
		linePanel.add(ComponentFactory.makeEntryField(10));
		
		linePanel.add(ComponentFactory.makeSmallLabel("  "));
		
		linePanel.add(ComponentFactory.makeEntryField(8));
		linePanel.add(ComponentFactory.makeUnsizedLabel(":"));
		linePanel.add(ComponentFactory.makeEntryField(10));
		
		linePanel.add(ComponentFactory.makeSmallLabel(" ns "));
		linePanel.add(ComponentFactory.makeEntryField(5));
		linePanel.add(ComponentFactory.makeTextButton("Apply"));
		
		
		return linePanel;
		
	}
	
}
