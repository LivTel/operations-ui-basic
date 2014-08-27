/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.Font;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

/**
 * @author eng
 *
 */
public abstract class InstrumentDataPanel extends JPanel {

	public static final Font LABEL_FONT = new Font("serif", Font.ITALIC, 9);
	public static final Font FIELD_FONT = new Font("serif", Font.PLAIN, 9);
	public static final Border BEVEL_BORDER = BorderFactory.createBevelBorder(BevelBorder.LOWERED);
	
	public InstrumentDataPanel() {
		super(true);
	}

	protected JTextField addField(JPanel panel, String label, int size) {
		JLabel jlabel = new JLabel(label);
		jlabel.setFont(LABEL_FONT);
		panel.add(jlabel);
		JTextField field = new JTextField(size);
		field.setBorder(BEVEL_BORDER);
		field.setFont(FIELD_FONT);
		panel.add(field);
		return field;
	}

	protected String getString(Map data, String key, String vdefault) {		
			String value = (String) data.get(key);
			if (value == null)
				return vdefault;
			return value;	
	}

	protected String getFloat(Map data, String key, double vdefault) {
		Float value = null;
		try {
			value = (Float) data.get(key);
			double d = value.doubleValue();
			return String.format("%6.2f", d);
		} catch (Exception e) {
			System.err.println("Error parsing value for key: "+key+":"+e);
			return "/"+value+"/";
		}
	}
	
	protected String getDouble(Map data, String key, double vdefault) {
		Double value = null;
		try {
			value = (Double) data.get(key);
			double d = value.doubleValue();
			return String.format("%6.2f", d);
		} catch (Exception e) {
			System.err.println("Error parsing value for key: "+key+":"+e);
			return "/"+value+"/";
		}
	}
	protected String getInt(Map data, String key, int vdefault) {
		Integer value = null;
		try {
			value = (Integer) data.get(key);
			int d = value.intValue();
			return String.format("%4d", d);
		} catch (Exception e) {
			System.err.println("Error parsing value for key: "+key+":"+e);
			return "/"+value+"/";
		}
	}
	
	
	public abstract void update(Map data);
	
}
