package ngat.rcsgui.test;

import javax.swing.*;
import java.awt.*;


public class WeatherRulesUpdatePanel extends JPanel {

    public static Dimension L_SIZE = new Dimension(100, 26);
    public static Dimension F_SIZE = new Dimension(60, 26);

    JTextField  humField;
    JTextField wmsField;
    JTextField rainField;
    JTextField moistField;
    JTextField wsField;
    JTextField tempField;
    JTextField cloudField;
    JTextField dustField;
    

    public WeatherRulesUpdatePanel() {
	super(true);

	setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

	humField = createField();
	wmsField  = createField();
	rainField = createField();
	moistField  = createField();
	wsField  = createField();
	tempField  = createField();
	cloudField  = createField();
	dustField  = createField();

	add(createLine("Humidity", humField));
	add(createLine("Wind", wsField));
	add(createLine("Rain", rainField));
	add(createLine("Moisture", moistField));
	add(createLine("Temp", tempField));
	add(createLine("WMS", wmsField));
	add(createLine("Cloud", cloudField));
	add(createLine("Dust", dustField));

    }

    public void updateHumidity(double humhi, double hlo) {
	updateField(humField, humhi, hlo);
    }
    public void updateWs(double wshi, double wslo) {
	updateField(wsField, wshi, wslo);
    }
    public void updateRain(double rhi, double rlo) {
	updateField(rainField, rhi, rlo);
    }
    public void updateMoist(double mhi, double mlo) {
	updateField(moistField, mhi, mlo);
    }
    public void updateWms(double wmshi, double wmslo) {
	updateField(wmsField, wmshi, wmslo);
}
    public void updateTemp(double tlo, double thi) {
	updateField(tempField, tlo, thi);
    }
    public void updateCloud(double chi, double clo) {
	updateField(cloudField, chi, clo);
    }
    public void updateDust(double dhi, double dlo) {
	updateField(dustField, dhi, dlo);
    }

    private JPanel createLine(String label, JTextField field) {
	JPanel p = new JPanel(true);
	p.setLayout(new FlowLayout(FlowLayout.LEADING));

	JLabel l = new JLabel(label);
	l.setPreferredSize(L_SIZE);
	p.add(l);
	p.add(field);
	return p;
    }
    
    private JTextField createField() {
	JTextField field = new JTextField(8);
	field.setPreferredSize(F_SIZE);
	field.setForeground(Color.blue);
	return field;
    }

    private void updateField(JTextField field, double alert, double clear) {
	
	
	if (alert > 0.9) {
	    // definite alert
	    field.setText("ALERT");
	    field.setBackground(Color.red);
	} else {
	    
	    if (clear > 0.9) {
		// definite clear
		field.setText("CLEAR");
		field.setBackground(Color.green);
	    } else {
		// warning
		field.setText("WARN");
		field.setBackground(Color.orange);
	    }
	}

    }


}
