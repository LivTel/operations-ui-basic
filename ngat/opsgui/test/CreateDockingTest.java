package ngat.opsgui.test;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import java.awt.Dimension;
import java.awt.GridLayout;

public class CreateDockingTest {

    public static final Dimension SIZE = new Dimension(600, 500);
    public static final Dimension PANEL = new Dimension(300, 500);

    public static void main(String args[]) {
	
	JFrame f = new JFrame("Docking Test");
	f.setPreferredSize(SIZE);
	f.getContentPane().setLayout(new GridLayout(1,2));
	
	JTabbedPane lhs = createLeftPane();
	JTabbedPane rhs = createRightPane();

	f.getContentPane().add(lhs);
	f.getContentPane().add(rhs);
	f.pack();
	f.setVisible(true);
	
    }

    private static JTabbedPane createLeftPane() {
	
	JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP, SwingConstants.VERTICAL);
	tabs.setPreferredSize(SIZE);

	DockingManager dm = new DockingManager(tabs);

	// create some tabs...

	dm.attach(createTab("Humidity"));
	dm.attach(createTab("Temperature"));
	dm.attach(createTab("Wind Speed"));
	dm.attach(createTab("Pressure"));
	dm.attach(createTab("Dewpoint"));
	dm.attach(createTab("Solar"));
	dm.attach(createTab("Dust"));

	return tabs;

    }

    private static JTabbedPane createRightPane() {

        JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP, SwingConstants.VERTICAL);
	tabs.setPreferredSize(SIZE);

        DockingManager dm = new DockingManager(tabs);

        // create some tabs...

        dm.attach(createTab("Azimuth"));
        dm.attach(createTab("Altitude"));
        dm.attach(createTab("Rotator"));
        dm.attach(createTab("Sciefold"));
        dm.attach(createTab("Agfilter"));
        dm.attach(createTab("Focus"));
        dm.attach(createTab("Power"));

        return tabs;

    }

    private static ComponentDescriptor createTab(String title) {

	JPanel panel = new JPanel(true);
	panel.setLayout(new GridLayout(8, 3));
	panel.setMinimumSize(PANEL);
	panel.setPreferredSize(PANEL);

	addRow(panel, "Actual");
	addRow(panel, "Demand");
	addRow(panel, "Previous");
	addRow(panel, "Rate");

	addRow(panel, "History");
	addRow(panel, "Mean");
	addRow(panel, "Std Dev");
	addRow(panel, "Trend");
	
	ComponentDescriptor cd = new ComponentDescriptor(panel, title);

	return cd;
    }

    private static void addRow(JPanel panel, String text) {
	
	panel.add(new JLabel(text));
	panel.add(new JTextField(10));
	panel.add(new JButton(text));

    }

}