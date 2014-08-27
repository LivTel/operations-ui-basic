/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.base.Resources;
import ngat.opsgui.util.TimeField;



/**
 * @author eng
 *
 */
public class SchedulingSweepNavigationPanel extends JPanel implements SweepDisplay {
	
	private JTextField displaySweepNumberField;

	private JTextField currentSweepNumberField;
	
	private JTextField gotoSweepField;
	private JButton navBackwardButton;
	private JButton navForwardButton;
	private JButton navFirstButton;
	private JButton navLatestButton;
	private JCheckBox synchronizeCheck;
	
	private TimeField sweepTimeField;
	
	private SchedulingSweepNavigationController controller;
	
	/**
	 * 
	 */
	public SchedulingSweepNavigationPanel(SchedulingSweepNavigationController controller) {
		super(true);
			
		this.controller = controller;
		setLayout(new FlowLayout(FlowLayout.LEADING));

		add(ComponentFactory.makeUnsizedLabel("Sweep"));
		displaySweepNumberField = ComponentFactory.makeEntryField(4);
		add(displaySweepNumberField);

		add(ComponentFactory.makeUnsizedLabel("Current"));
		currentSweepNumberField = ComponentFactory.makeEntryField(4);
		add(currentSweepNumberField);
		
		add(new JSeparator(SwingConstants.VERTICAL));
		
		add(ComponentFactory.makeUnsizedLabel("Goto Sweep"));
		gotoSweepField = ComponentFactory.makeEntryField(3);
		add(gotoSweepField);		
		navFirstButton = ComponentFactory.makeSmallButton(Resources.getIcon("sweep-first"), "First sweep");
		add(navFirstButton);
		navBackwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("sweep-back"), "Previous sweep");
		add(navBackwardButton);
		navForwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("sweep-next"), "Next sweep");
		add(navForwardButton);
		navLatestButton = ComponentFactory.makeSmallButton(Resources.getIcon("sweep-last"), "Latest sweep");
		add(navLatestButton);

		synchronizeCheck = new JCheckBox("Synchronize");
		add(synchronizeCheck);

		sweepTimeField = ComponentFactory.makeTimeField(16, "%tT");
		add(sweepTimeField);
		
		addListeners();
		
		
	}

    private void addListeners() {
	
	navBackwardButton.addActionListener(new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent ae) { 
		    controller.requestSweepRelative(-1);
		}});
        navForwardButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
		    controller.requestSweepRelative(1);
                }});

        navFirstButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
		    controller.requestSweepAbsolute(0);
                }});

        navLatestButton.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
		    controller.requestSweepLatest();
                }});

        synchronizeCheck.addActionListener(new ActionListener() {
                @Override
				public void actionPerformed(ActionEvent ae) {
		    boolean synchToSweep = synchronizeCheck.isSelected();
		    controller.requestSynchronize(synchToSweep);
                }});



    }


	@Override
	public void displaySweep(int displaySweepCount, int latestSweepCount, boolean synchToSweep) {
		long sweepTime = -1L;
		
		if (synchToSweep) {
			sweepTime = controller.getSweepTime(latestSweepCount);
			displaySweepNumberField.setText(""+(latestSweepCount+1));
			currentSweepNumberField.setText(""+(latestSweepCount+1));
			synchronizeCheck.setSelected(true);
			navFirstButton.setEnabled(false);
			navBackwardButton.setEnabled(false);
			navForwardButton.setEnabled(false);
			navLatestButton.setEnabled(false);
		} else {
			sweepTime = controller.getSweepTime(displaySweepCount);
			displaySweepNumberField.setText(""+(displaySweepCount+1));
			currentSweepNumberField.setText(""+(latestSweepCount+1));
			synchronizeCheck.setSelected(false);
			if (displaySweepCount == 0) {
				navFirstButton.setEnabled(false);
				navBackwardButton.setEnabled(false);
			} else {
				navFirstButton.setEnabled(true);
				navBackwardButton.setEnabled(true);
			}
			
			if (displaySweepCount == latestSweepCount) {
				navForwardButton.setEnabled(false);
				navLatestButton.setEnabled(false);
			} else {
				navForwardButton.setEnabled(true);
				navLatestButton.setEnabled(true);
			}
		}
		
		sweepTimeField.updateData(sweepTime);
		
	}
	
}
