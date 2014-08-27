/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;

import ngat.icm.InstrumentStatus;
import ngat.message.ISS_INST.GET_STATUS_DONE;

/**
 * @author eng
 * 
 */
public class InstrumentStatusPanel extends JPanel {

	JLabel nameLabel;

	//JTextField networkField;

	JTextField operationalField;

	JTextField tempField;

	String name;
	String tkw;
	TimeSeries ts;
	
	private long timeLastDataSample;
	
	/**
	 * 
	 */
	public InstrumentStatusPanel(String name, String tkw, TimeSeries ts) {
		super(true);
		this.name = name;
		this.tkw = tkw;
		this.ts = ts;
		setLayout(new GridLayout(1, 3));

		nameLabel = new JLabel(name);
		nameLabel.setFont(new Font("courier", Font.PLAIN, 8));
		nameLabel.setBackground(RcsGUI.LIGHTSLATE);
		nameLabel.setOpaque(true);
		add(nameLabel);

		//tworkField = createField();
		//d(networkField);
		
		operationalField = createField();
		add(operationalField);
		
		tempField = createField();
		add(tempField);
	}

	private JTextField createField() {
		JTextField field = new JTextField(10);
		field.setBorder(RcsGUI.raisedbevel);
		field.setBackground(RcsGUI.LIGHTSLATE);
		field.setOpaque(true);
		field.setFont(new Font("courier", Font.PLAIN, 8));
		return field;
	}

	public void updateStatus(InstrumentStatus status) {

		if (status == null)
			return;
		
		// record last data item
		timeLastDataSample = System.currentTimeMillis();
		System.err.printf("Last sample: %s : %tF %tT: \n",name, timeLastDataSample, timeLastDataSample);
		
		try {
			if (!status.isEnabled()) {
				operationalField.setText("DISABLED");
				operationalField.setBackground(Color.pink);
				operationalField.setForeground(Color.blue);
			} else if (status.isOnline()) {

				//networkField.setText("ONLINE");
				//networkField.setBackground(Color.green);
				//networkField.setForeground(Color.blue);
				System.err.println("Check keyword: " + GET_STATUS_DONE.KEYWORD_INSTRUMENT_STATUS);
				String tempStatus = (String) status.getStatus().get(GET_STATUS_DONE.KEYWORD_INSTRUMENT_STATUS);

				if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_OK.equals(tempStatus)) {
					operationalField.setText("OKAY");
					operationalField.setBackground(Color.green);
					operationalField.setForeground(Color.blue);
				} else if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_WARN.equals(tempStatus)) {
					operationalField.setText("WARN");
					operationalField.setBackground(Color.orange);
					operationalField.setForeground(Color.blue);
				} else if (ngat.message.ISS_INST.GET_STATUS_DONE.VALUE_STATUS_FAIL.equals(tempStatus)) {
					operationalField.setText("FAIL");
					operationalField.setBackground(Color.red);
					operationalField.setForeground(Color.blue);
					// TODO wait till motty has set this next key up for us....
					// else if
					// (GET_STATUS_DONE.VALUE_STATUS_UNAVAILABLE.equals(opstat))
					// instrument.setOperationalStatus(Instrument.OPERATIONAL_STATUS_UNAVAILABLE);
				} else {
					operationalField.setText("ONLINE");
					operationalField.setBackground(Color.green);
					operationalField.setForeground(Color.blue);
				}

				double temp = (Double) status.getStatus().get(tkw);
				tempField.setText(String.format("%4.2f C", (temp - 273.15)));				
				ts.add(new Second(), temp-273.15);
			} else {
				operationalField.setText("OFFLINE");
				operationalField.setBackground(Color.blue);
				operationalField.setForeground(Color.yellow);

				//operationalField.setText("UNKNOWN");
				//operationalField.setBackground(Color.orange);
				//operationalField.setForeground(Color.blue);
			}

		} catch (Exception e) {
			System.err.println("InstStatusPanel(" + name + ")" + e.getMessage());
		}
	}

	/**
	 * @return the timeLastDataSample
	 */
	public long getTimeLastDataSample() {
		return timeLastDataSample;
	}

}
