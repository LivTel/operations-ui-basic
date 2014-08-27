package ngat.rcsgui.stable;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class OpsTableRenderer extends JLabel implements TableCellRenderer {
	    
	    
	static Color COMPLETED_COLOR = new Color(236,245,203);
	static Color FAILED_COLOR = Color.red;
	static Color RUN_COLOR = Color.orange;
	static Font TABLE_FONT = new Font("serif", Font.PLAIN, 9);

	static Color TABLE_BG_COLOR = new Color(236,245,203);


    //		@Override
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
				boolean hasFocus, int row, int column) {
			
		    System.err.println("Render: "+(value != null ? value.getClass().getName()+" "+value: "NULL"));
		    
		    if (column == 0) {
		    	int mode = ((Integer)value).intValue();
		    	switch (mode) {
		    	case 1:		    	
		    		return new OpsButton(Color.cyan); // BGCA
		    	case 2:
		    		return new OpsButton(Color.yellow.darker()); // SOCA
		    	case 3:
			    return new OpsButton(Color.pink.darker()); // CAL
		    	case 4:
			    return new OpsButton(Color.pink); // TOCA (no can do atm)
		    	default:
			    return new OpsButton(Color.cyan.darker()); // UNKNOWN & WEIRD
		    	}
		    }
		    
			String text = (String)value;
			  setFont(TABLE_FONT);
			  setText(text);
			if (column == 6) { 
				if (text.equals("Completed")) 			
						setBackground(COMPLETED_COLOR);
				else if (text.equals("Failed")) 	        	
						setBackground(FAILED_COLOR);
		    	 else 	        	
		    		setBackground(RUN_COLOR);
			} else
				setBackground(TABLE_BG_COLOR);
		     //setEnabled(list.isEnabled());
		    		
		     setOpaque(true);
		     return this;
		}
	 }

	class OpsButton extends JPanel {
	
	public OpsButton(Color color) {
		super(true);
		setBackground(color);			
	}
	
	
}
