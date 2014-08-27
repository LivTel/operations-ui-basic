/**
 * 
 */
package ngat.rcsgui.stable;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.Hashtable;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.BevelBorder;

/**
 * @author eng
 * 
 */
public class BeamStatusPanel extends JPanel {
	
	TipTiltPanel upperTiptiltPanel;
	
	SlidePanel upperSlidePanel;
	
	TipTiltPanel lowerTiptiltPanel; 
	
	SlidePanel lowerSlidePanel;
	
	public BeamStatusPanel() {
		super(true);
		createPanel();
	}

	private void createPanel() {

		// Upper (left panel)

		JPanel upperPanel = new JPanel(true);
		upperPanel.setLayout(new BorderLayout());
		upperPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		upperTiptiltPanel = new TipTiltPanel("Upper tiptilt");
		upperPanel.add(upperTiptiltPanel, BorderLayout.CENTER);
		
		upperSlidePanel = new SlidePanel("Upper slide");
		upperPanel.add(upperSlidePanel, BorderLayout.SOUTH);
		
		// Lower (right panel)
		
		JPanel lowerPanel = new JPanel(true);
		lowerPanel.setLayout(new BorderLayout());
		lowerPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		lowerTiptiltPanel = new TipTiltPanel("Lower tiptilt");
		lowerPanel.add(lowerTiptiltPanel, BorderLayout.CENTER);
		
		lowerSlidePanel = new SlidePanel("Lower slide");
		lowerPanel.add(lowerSlidePanel, BorderLayout.SOUTH);
		
		add(upperPanel, BorderLayout.WEST);
		add(lowerPanel, BorderLayout.EAST);
		
	}

	class TipTiltPanel extends JPanel {

		String name;

		JTextField iocField;
		JTextField xOffsetField;
		JTextField yOffsetField;

		TipTiltPanel(String name) {
			super(true);
			this.name = name;
			setLayout(new GridLayout(3, 2));
			setBorder(BorderFactory.createTitledBorder(name));
			
			add(new JLabel("Instr"));
			iocField = new JTextField(8);
			add(iocField);

			add(new JLabel("X offset"));
			xOffsetField = new JTextField(8);
			add(xOffsetField);

			add(new JLabel("Y offset"));
			yOffsetField = new JTextField(8);
			add(yOffsetField);
		}

	}
	
	class SlidePanel extends JPanel {

		String name;

		JTextField filterClassField;
		JTextField filterNameField;
		JTextField filterStatusField;
		
		public SlidePanel(String name) {
			this.name = name;
			setLayout(new GridLayout(4, 2));
			setBorder(BorderFactory.createTitledBorder(name));
			
			add(new JLabel("Class"));
			filterClassField = new JTextField(8);
			add(filterClassField);

			add(new JLabel("Name"));
			filterNameField = new JTextField(8);
			add(filterNameField);
			
			add(new JLabel("Status"));
			filterStatusField = new JTextField(8);
			add(filterStatusField);
			
		}
		
		public void setFilterClassName(String name){
			filterClassField.setText(name);
		}
		
		public void setFilterName(String name){
			filterNameField.setText(name);
		}
		
		public void setFilterStatus(String status) {
			filterStatusField.setText(status);
		}
		
	}
	/** Update the panel with supplied key mapping. Later we will supply proper status entities.*/
	public void updateNoData() {
		upperSlidePanel.setFilterClassName("UNKNOWN");
		upperSlidePanel.setFilterName("UNKNOWN");
		upperSlidePanel.setFilterStatus("UNKNOWN");
		lowerSlidePanel.setFilterClassName("UNKNOWN");
		lowerSlidePanel.setFilterName("UNKNOWN");
		lowerSlidePanel.setFilterStatus("UNKNOWN");
	}
	/** Update the panel with supplied key mapping. Later we will supply proper status entities.*/
	public void update(Hashtable hash) {
		
		if (hash == null) {
			return;
		}
		
		String s = (String)hash.get("upper.slide.filter.class.name");
		upperSlidePanel.setFilterClassName(s);
		s = (String)hash.get("upper.slide.filter.name");
		upperSlidePanel.setFilterName(s);
		s = (String)hash.get("upper.slide.status.label");
		upperSlidePanel.setFilterStatus(s);
		
		s = (String)hash.get("lower.slide.filter.class.name");
		lowerSlidePanel.setFilterClassName(s);
		s = (String)hash.get("lower.slide.filter.name");
		lowerSlidePanel.setFilterName(s);
		s = (String)hash.get("lower.slide.status.label");
		lowerSlidePanel.setFilterStatus(s);
	}
	
	
	public static void main(String args[]) {
		
		JFrame f = new JFrame("BSS");
		BeamStatusPanel bsp = new BeamStatusPanel();
		
		f.getContentPane().add(bsp);
		f.pack();
		f.setVisible(true);
		
	}
	
}
