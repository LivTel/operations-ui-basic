/**
 * 
 */
package ngat.opsgui.perspectives.meteorology;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author eng
 * 
 */
public class SeeingHistoryTableRenderer extends DefaultTableCellRenderer {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	/**
	 * 
	 */
	public SeeingHistoryTableRenderer() {
		super();
		sdf.setTimeZone(UTC);
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {

		System.err.println("SHTR::Column renderer: col=" + column
				+ " value isa " + value.getClass().getName());

		Component c = super.getTableCellRendererComponent(table, value,
				isSelected, hasFocus, row, column);

		if (column == 0) {
			JLabel l = (JLabel) c;
			Long lv = (Long) value;
			l.setText(sdf.format(new Date(lv)));
		} else if (column == 1) {
			JLabel l = (JLabel) c;
			Double d = (Double) value;
			l.setText(String.format("%4.2f", d));
		} else if (column == 2) {
			JLabel l = (JLabel) c;
			Double d = (Double) value;
			l.setText(String.format("%4.2f", d));
		} else if (column == 5) {
			JLabel l = (JLabel) c;
			String s = (String) value;
			l.setText(s);
		} else if (column == 3) {
			JLabel l = (JLabel) c;
			String s = (String) value;
			l.setText(s);
		} else if (column == 4) {
			JLabel l = (JLabel) c;
			Double d = (Double) value;
			l.setText(String.format("%4.2f", d));
		} else if (column == 6) {
			JLabel l = (JLabel) c;
			String s = (String) value;
			l.setText(s);
		} else if (column == 7) {
			JLabel l = (JLabel) c;
			String s = (String) value;
			l.setText(s);
		}

		return c;

	}

}
