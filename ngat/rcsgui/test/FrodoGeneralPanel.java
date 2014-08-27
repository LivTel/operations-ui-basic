/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ngat.rcsgui.stable.InstrumentDataPanel;

/**
 * Frodo display - top area, general system status.
 * 
 * @author eng
 * 
 */
public class FrodoGeneralPanel extends InstrumentDataPanel {

	
	FrodoArmPanel redPanel;
	FrodoArmPanel bluePanel;
	
	/*
	 * Instrument=FrodoSpec, Environment.Humidity=35.634632,
	 * Environment.Temperature.Panel=19.339756,
	 * Instrument.Status.Focus.Stage=OK, Air.Pressure=5.465587,
	 * network.status=ONLINE, Plc.Fault.Status=0,
	 * Environment.Temperature.Instrument=19.896534,
	 * Plc.Mechanism.Status.String= 00001010 10100000, Instrument.Status.Plc=OK,
	 * Lamp.Controller.Plc.Comms.Status=OK, Environment.Temperature.4=23.06635,
	 * Environment.Temperature.3=19.339756, Environment.Temperature.2=18.909119,
	 * Environment.Temperature.1=18.799637, Environment.Temperature.0=18.81108,
	 * Air.Flow=-1.2631445, Instrument.Status.Detector.Temperature=OK,
	 * Cooling.Time=0.0, Lamp.Controller.Status=In Use Lamps:null In Use
	 * Count:0, Plc.Mechanism.Status=2720, Plc.Comms.Status=OK,
	 * Plc.Fault.Status.String= 00000000 00000000, Instrument.Status=OK,
	 * Lamp.Controller.Status.Fault=false
	 */
	JTextField focusStageStatusField;
	JTextField plcStatusField;
	JTextField lampPlcCommsStatusField;
	JTextField detectorTemperatureStatusField;
	JTextField plcCommsStatusField;

	JTextField instrumentTempField;
	JTextField panelTempField;
	JTextField env0TempField;
	JTextField env1TempField;
	JTextField env2TempField;
	JTextField env3TempField;
	JTextField env4TempField;

	JTextField airflowField;
	JTextField airPessureField;
	JTextField humidityField;

	/**
	 * 
	 */
	public FrodoGeneralPanel() {
		super();

		createPanel();
	}

	private void createPanel() {

		setLayout(new BorderLayout());
		
		JPanel topPanel = createTopPanel();
		JPanel lowerPanel = createLowerPanel();
		
		add(lowerPanel, BorderLayout.CENTER);
		add(topPanel, BorderLayout.NORTH);

	}

	private JPanel createTopPanel() {
		
		JPanel topPanel = new JPanel(true);
		topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		JPanel statusPanel = new JPanel(true);
		statusPanel.setLayout(new GridLayout(5, 2));
		statusPanel.setBorder(BorderFactory.createTitledBorder("Status"));

		focusStageStatusField = addField(statusPanel, "Focus stage", 8);
		plcStatusField = addField(statusPanel, "PLC", 8);
		lampPlcCommsStatusField = addField(statusPanel, "Lamp PLC Comms", 8);
		detectorTemperatureStatusField = addField(statusPanel, "Detector Temp", 8);
		plcCommsStatusField = addField(statusPanel, "PLC Comms", 8);

		JPanel envPanel = new JPanel(true);
		envPanel.setLayout(new GridLayout(5, 4));
		envPanel.setBorder(BorderFactory.createTitledBorder("Environment"));

		instrumentTempField = addField(envPanel, "Instrument temp", 6);
		env0TempField = addField(envPanel, "Opt. table Temp", 6);//optical table

		panelTempField = addField(envPanel, "Panel", 6);
		env1TempField = addField(envPanel, "Red camera Temp", 6); // red camera

		airflowField = addField(envPanel, "Airflow", 6);
		env2TempField = addField(envPanel, "Box Temp", 6); // Box

		airPessureField = addField(envPanel, "Air pressure", 6);
		env3TempField = addField(envPanel, "Elec. panel Temp", 6); // Electrical panel

		humidityField = addField(envPanel, "Humidity", 6);
		env4TempField = addField(envPanel, "Stick Temp", 6); // stick

		topPanel.add(statusPanel);
		topPanel.add(envPanel);

		return topPanel;
	}
	
	private JPanel createLowerPanel() {
		
		JPanel lowerPanel = new JPanel(true);
		lowerPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		
		redPanel = new FrodoArmPanel("RED", Color.red);		
		bluePanel= new FrodoArmPanel("BLUE", Color.blue);
		
		lowerPanel.add(redPanel);
		lowerPanel.add(bluePanel);
		
		return lowerPanel;
		
	}
	
	

	@Override
	public void update(Map data) {
		System.err.println("FGP:Update received: "+data);
		humidityField.setText(getFloat(data, "Environment.Humidity", -99));
		panelTempField.setText(getFloat(data, "Environment.Temperature.Panel", -99));
		airPessureField.setText(getFloat(data, "Air.Pressure", -99));
		instrumentTempField.setText(getFloat(data, "Environment.Temperature.Instrument", -99));
		airflowField.setText(getFloat(data, "Air.Flow", -99));
		
		env4TempField.setText(getFloat(data, "Environment.Temperature.4", -99));
		env3TempField.setText(getFloat(data, "Environment.Temperature.3", -99));
		env2TempField.setText(getFloat(data, "Environment.Temperature.2", -99));
		env1TempField.setText(getFloat(data, "Environment.Temperature.1", -99));
		env0TempField.setText(getFloat(data, "Environment.Temperature.0", -99));
		
		focusStageStatusField.setText(getString(data, "Instrument.Status.Focus.Stage", "UNKNOWN"));
		plcStatusField.setText(getString(data, "Instrument.Status.Plc", "UNKNOWN"));
		lampPlcCommsStatusField.setText(getString(data, "Lamp.Controller.Plc.Comms.Status", "UNKNOWN"));
		detectorTemperatureStatusField.setText(getString(data, "Instrument.Status.Detector.Temperature", "UNKNOWN"));
		plcCommsStatusField.setText(getString(data, "Plc.Comms.Status", "UNKNOWN"));
			
		redPanel.update(data);
		bluePanel.update(data);
	}


}
