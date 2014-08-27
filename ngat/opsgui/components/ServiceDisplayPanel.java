/**
 * 
 */
package ngat.opsgui.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;
import ngat.opsgui.services.ServiceManager;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateIndicator;
import ngat.opsgui.util.StatusHistoryPanel;
import ngat.opsgui.util.TimeDisplayController;

/**
 * @author eng
 *
 */
public class ServiceDisplayPanel extends JPanel {

	public static Dimension LSIZE = new Dimension(100, 25);
	
	public static Dimension HSIZE = new Dimension(250, 25);
	
	public static Dimension ISIZE = new Dimension(30, 25);
	
	
	/** Name of the service.*/
	private String serviceName;
	
	/** Display history.*/
	private StatusHistoryPanel history;
	
	/** Display current state.*/
	private StateIndicator current;
	
	/** Request service to re-register.*/
	private JButton regBtn;
	
	/** Color map for history.*/
	private StateColorMap colorMap;
	
	/** Controller for history display.*/
	private TimeDisplayController tdc;
	
	public ServiceDisplayPanel(String serviceName, StateColorMap colorMap) {
		super(true);
		this.serviceName = serviceName;
		this.colorMap = colorMap;
		
		tdc = new TimeDisplayController(3600*1000L);
		
		setLayout(new FlowLayout(FlowLayout.LEADING, 3, 2));
		
		// label
		JLabel label = new JLabel(serviceName);
		label.setPreferredSize(LSIZE);
		add(label);
		
		// history
		history = new StatusHistoryPanel(tdc);
		history.setMap(colorMap);	
		history.setBorder(BorderFactory.createLineBorder(Color.black));
		history.setPreferredSize(HSIZE);
		add(history);
		
		// current
		current = new StateIndicator();
		current.setMap(colorMap);
		current.setPreferredSize(ISIZE);
		add(current);		
		
		regBtn = new JButton("...");
		regBtn.setForeground(Color.blue);
		regBtn.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
		//regBtn.setBorderPainted(false);
		//regBtn.setMargin(new Insets(2,2,2,2));

		add(regBtn);
		
	}
	
	public void serviceAvailabilityUpdate(long time, boolean available) {		
		int state = (available ? ServiceManager.SERVICE_AVAILABLE : ServiceManager.SERVICE_UNAVAILABLE);
		history.addHistory(time, state);
		history.repaint();
		current.updateState(state, "");
		current.repaint();
	}
	
}
