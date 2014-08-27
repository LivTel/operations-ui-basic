/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ngat.rcsgui.stable.InstrumentDataPanel;

/**
 * @author eng
 * 
 */
public class FrodoArmPanel extends InstrumentDataPanel {

	private String arm;
	private Color color;

	JTextField heaterVoltageADUField;
	JTextField lowVoltageADUField;
	JTextField minusLowVoltageADUField;
	JTextField highVoltageADUField;
	JTextField utilityBoardTempADUField;

	JTextField instrumentStatusField;
	JTextField sdsuCommsStatusField;
	JTextField plcStatusField;
	JTextField focusStageStatusField;
	JTextField detectorTempStatusField;

	JTextField focusStagePositionField;
	JTextField focusStageEncoderPositionField;

	/**
	 * @param ucarm
	 *            The arm name (Upper case).
	 */
	public FrodoArmPanel(String ucarm, Color color) {
		super();
		this.arm = ucarm.toLowerCase();
		this.color = color;
		createPanel();
	}

	private void createPanel() {

		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(color), arm+" arm"));

		JPanel aduPanel = new JPanel(true);
		aduPanel.setLayout(new GridLayout(5, 2));
		aduPanel.setBorder(BorderFactory.createTitledBorder("ADU"));

		heaterVoltageADUField = addField(aduPanel, "Heater voltage", 6);
		lowVoltageADUField = addField(aduPanel, "Low voltage(+)", 6);
		minusLowVoltageADUField = addField(aduPanel, "Low voltage(-)", 6);
		highVoltageADUField = addField(aduPanel, "High voltage", 6);
		utilityBoardTempADUField = addField(aduPanel, "Utility board", 6);

		JPanel statusPanel = new JPanel(true);
		statusPanel.setLayout(new GridLayout(5, 2));
		statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));

		instrumentStatusField = addField(statusPanel, "Instrument", 6);
		sdsuCommsStatusField = addField(statusPanel, "SDSU comms", 6);
		plcStatusField = addField(statusPanel, "PLC", 6);
		focusStageStatusField = addField(statusPanel, "Focus stage", 6);
		detectorTempStatusField = addField(statusPanel, "Detector temp", 6);

		JPanel oPanel = new JPanel(true);
		oPanel.setLayout(new GridLayout(5, 2));
		oPanel.setBorder(BorderFactory.createTitledBorder("Other"));
		focusStagePositionField = addField(oPanel, "Focus stage", 6);
		focusStageEncoderPositionField = addField(oPanel, "Focus stage Enc", 6);
		addField(oPanel, "spare", 5);
		addField(oPanel, "spare", 5);
		addField(oPanel, "spare", 5);
		
		add(aduPanel, BorderLayout.NORTH);
		add(statusPanel, BorderLayout.CENTER);
		add(oPanel, BorderLayout.EAST);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see ngat.rcsgui.stable.InstrumentDataPanel#update(java.util.Map)
	 */
	@Override
	public void update(Map data) {
		System.err.println("FGP:(" + arm + "):Update received");
		/*
		 * red.Elapsed Exposure Time=999, red.NRows=0,
		 * red.Instrument.Status.Detector.Temperature=OK, red.currentCommand=,
		 * red.Instrument.Status.Focus.Stage=OK, red.Window Flags=0, red.Low
		 * Voltage Supply ADU=3535, red.Focus Stage Position=0.0, red.Heater
		 * ADU=1646, red.Focus.Stage.Linear.Encoder.Position=0.0, red.High
		 * Voltage Supply ADU=3297, red.Current Mode=0,
		 * red.Instrument.Status.Plc=OK, red.DeInterlace Type=0, red.NCols=0,
		 * red.Temperature=173.70284881591795, red.Minus Low Voltage Supply
		 * ADU=589, red.Exposure Count=0, red.Grating Position String=low,
		 * red.Exposure Number=0, red.Exposure Start Time=0,
		 * red.Instrument.Status=OK, blue.NCols=0, red.NPBin=0, red.NSBin=0,
		 * red.Exposure Length=0, red.Setup Status=false, red.Utility Board
		 * Temperature ADU=2812, red.Instrument.Status.SDSU.Comms=OK,
		 */

		heaterVoltageADUField.setText(getInt(data, arm + ".Heater ADU", -99));
		lowVoltageADUField.setText(getInt(data, arm + ".Low Voltage Supply ADU", -99));
		minusLowVoltageADUField.setText(getInt(data, arm + ".Minus Low Voltage Supply ADU", -99));
		highVoltageADUField.setText(getInt(data, arm + ".High Voltage Supply ADU", -99));
		utilityBoardTempADUField.setText(getInt(data, arm + ".Utility Board Temperature ADU", -99));

		instrumentStatusField.setText(getString(data, arm + ".Instrument.Status", "UNKNOWN"));
		sdsuCommsStatusField.setText(getString(data, arm + ".Instrument.Status.SDSU.Comms", "UNKNOWN"));
		plcStatusField.setText(getString(data, arm + ".Instrument.Status.Plc", "UNKNOWN"));
		focusStageStatusField.setText(getString(data, arm + ".Instrument.Status.Focus.Stage", "UNKNOWN"));
		detectorTempStatusField.setText(getString(data, arm + ".Instrument.Status.Detector.Temperature", "UNKNOWN"));

		focusStagePositionField.setText(getDouble(data, arm + ".Focus Stage Position", -99));
		focusStageEncoderPositionField.setText(getFloat(data, arm + ".Focus.Stage.Linear.Encoder.Position", -99));

	}

}
