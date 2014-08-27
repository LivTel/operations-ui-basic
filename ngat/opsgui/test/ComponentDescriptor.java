package ngat.opsgui.test;

import javax.swing.JComponent;
import javax.swing.Icon;

public class ComponentDescriptor {

    private Icon labelIcon;

    private String labelText;

    private String tagText;
    
    private JComponent component;

    public ComponentDescriptor(JComponent component, String labelText, Icon labelIcon, String tagText) {
	this.component = component;
	this.labelText = labelText;
	this.labelIcon = labelIcon;
	this.tagText = tagText;
    }

    public ComponentDescriptor(JComponent component, String labelText, Icon labelIcon) {
	this(component, labelText, labelIcon, labelText);
    }
    
    public ComponentDescriptor(JComponent component, String labelText) {
	this(component, labelText, null, labelText);
    }

    public ComponentDescriptor(JComponent component, Icon labelIcon) {
	this(component, null, labelIcon, null);
    }

    public JComponent getComponent() {return component; }
    
    public String getLabelText() { return labelText; }

    public Icon getLabelIcon() { return labelIcon; }

    public String getTagText() { return tagText; }

}