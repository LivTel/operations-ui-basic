/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;

import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.services.ServiceManager;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.TelescopeStatus;

/**
 * @author eng
 *
 */
public class AxisSummaryPanel extends SummaryPane  {

	StateColorMap stateMap;
	
	StateField azmField;
	StateField altField;
	StateField rotField;
	
	DataField azmPosField;
	DataField altPosField;
	DataField rotPosField;
	
	/**
	 * @param title
	 */
	public AxisSummaryPanel(String title) {
		super(title);	
		stateMap = new StateColorMap(Color.gray, "UNKNOWN");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_ERROR, Color.red, "FAIL");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_MOVING, Color.green, "MOVING");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_TRACKING, Color.cyan, "TRACKING");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_STOPPED, Color.green, "STOP");
		stateMap.addColorLabel(TcsStatusPacket.STATE_ERROR, Color.orange, "ERROR");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_INPOSITION, Color.green, "IN_POSN");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_OFF_LINE, Color.red, "OFFLINE");
		stateMap.addColorLabel(TcsStatusPacket.STATE_UNKNOWN, Color.blue, "UNKNOWN");
	}

	/**
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	@Override
	public void createPanel() {
	
		LinePanel l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Azimuth"));
		azmField = ComponentFactory.makeStateField(8, stateMap);
		l.add(azmField);
		azmPosField = ComponentFactory.makeDataField(5, "%4.2f");
		l.add(azmPosField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Altitude"));
		altField = ComponentFactory.makeStateField(8, stateMap);
		l.add(altField);
		altPosField = ComponentFactory.makeDataField(5, "%4.2f");
		l.add(altPosField);
		add(l);
		
		l = new LinePanel();
		l.add(ComponentFactory.makeLabel("Rotator"));
		rotField = ComponentFactory.makeStateField(8, stateMap);
		l.add(rotField);
		rotPosField = ComponentFactory.makeDataField(5, "%4.2f");
		l.add(rotPosField);		
		add(l);
	}

	
	public void updateAzimuth(PrimaryAxisStatus axis) {
		azmField.updateState(axis.getMechanismState());
		azmPosField.updateData(axis.getCurrentPosition());
	}
	
	public void updateAltitude(PrimaryAxisStatus axis) {
		altField.updateState(axis.getMechanismState());
		altPosField.updateData(axis.getCurrentPosition());
	}

	public void updateRotator(PrimaryAxisStatus axis) {
		rotField.updateState(axis.getMechanismState());
		rotPosField.updateData(axis.getCurrentPosition());
	}


}
