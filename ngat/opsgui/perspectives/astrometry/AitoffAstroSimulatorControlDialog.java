/**
 * 
 */
package ngat.opsgui.perspectives.astrometry;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;

import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import ngat.astrometry.AstrometrySimulator;
import ngat.astrometry.AstrometrySimulatorListener;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.base.Resources;
import ngat.opsgui.components.LinePanel;
import ngat.opsgui.perspectives.tracking.TrackingPerspective;

/**
 * @author eng
 * 
 */
public class AitoffAstroSimulatorControlDialog extends JDialog implements AstrometrySimulatorListener {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	TrackingPerspective trackingPerspective;

	AstrometrySimulator simulator;

	private long simulationTime;

	private long startTime;
	private long endTime;

	/**
	 * Displays Real or Simulation label to indicate which mode is in operation.
	 */
	JLabel modeLabel;

	/** Displays current simulation (or real time). */
	JTextField simulationTimeField;

	JComboBox runStepField;

	long runStepLength;

	JComboBox delayField;

	long delay;

	JComboBox startShiftField;
	JComboBox endShiftField;

	JTextField startField;
	JTextField endField;

	JButton runForwardButton;
	JButton stepForwardButton;
	JButton fastForwardButton;
	JButton gotoEndButton;

	JButton runBackwardButton;
	JButton stepBackwardButton;
	JButton fastBackwardButton;
	JButton gotoStartButton;

	JButton pauseButton;
	JButton infoButton;

	JButton clearButton;
	JButton applyButton;

	/**
	 * @param simulator
	 * @param frame
	 */
	public AitoffAstroSimulatorControlDialog(JFrame frame, TrackingPerspective trackingPerspective,
			AstrometrySimulator simulator) {
		super(frame, "Astrometry Simulator Control", true);
		this.trackingPerspective = trackingPerspective;
		this.simulator = simulator;

		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		addWindowListener(new MyWindowListener());

		JPanel panel = createPanel();
		getContentPane().add(panel);
		pack();
		sdf.setTimeZone(UTC);
	}

	private JPanel createPanel() {

		JPanel p = new JPanel(true);
		p.setLayout(new BorderLayout());

		// Top Panel - sim time, sim mode
		LinePanel topPanel = ComponentFactory.makeLinePanel();
		topPanel.add(ComponentFactory.makeLabel("Simulation time"));

		simulationTimeField = (JTextField) topPanel.add(ComponentFactory.makeEntryField(20));

		modeLabel = (JLabel) topPanel.add(ComponentFactory.makeLabel("Idle"));

		// Config Panel - (runstep, delay), (start time, end time, clear and
		// apply buttons
		JPanel configPanel = new JPanel(true);
		configPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		JPanel configLeftPanel = new JPanel(true);
		configLeftPanel.setLayout(new BoxLayout(configLeftPanel, BoxLayout.Y_AXIS));

		LinePanel line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Run Step (m)"));
		runStepField = (JComboBox) line.add(ComponentFactory.makeCombo());
		runStepField.setModel(createStepModel());
		configLeftPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Delay (s)"));
		delayField = (JComboBox) line.add(ComponentFactory.makeCombo());
		delayField.setModel(createDelayModel());
		configLeftPanel.add(line);

		JPanel configRightPanel = new JPanel(true);
		configRightPanel.setLayout(new BoxLayout(configRightPanel, BoxLayout.Y_AXIS));

		line = ComponentFactory.makeLinePanel(350);
		line.add(ComponentFactory.makeSmallLabel("Start"));
		startField = (JTextField) line.add(ComponentFactory.makeEntryField(20));
		startShiftField = (JComboBox) line.add(ComponentFactory.makeCombo());
		startShiftField.setModel(createTimeModel());

		clearButton = (JButton) line.add(ComponentFactory.makeSmallButton(Resources.getIcon("clear.icon")));
		clearButton.setEnabled(false);
		configRightPanel.add(line);

		line = ComponentFactory.makeLinePanel(350);
		line.add(ComponentFactory.makeSmallLabel("End"));
		endField = (JTextField) line.add(ComponentFactory.makeEntryField(20));
		endShiftField = (JComboBox) line.add(ComponentFactory.makeCombo());
		endShiftField.setModel(createTimeModel());

		applyButton = (JButton) line.add(ComponentFactory.makeSmallButton(Resources.getIcon("apply.changes.icon"),
				"Apply changes"));
		configRightPanel.add(line);

		configPanel.add(configLeftPanel);
		configPanel.add(configRightPanel);

		// Sim Controls
		JPanel simPanel = new JPanel(true);
		simPanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		// button order

		// go start, fast back, step back, run back, run, step fwd, fast fwd,
		// goto end, pause, info

		infoButton = ComponentFactory.makeSmallButton(Resources.getIcon("info.icon"), "Information");
		simPanel.add(infoButton);

		gotoStartButton = ComponentFactory.makeSmallButton(Resources.getIcon("goto.start.icon"), "Goto Start");
		simPanel.add(gotoStartButton);

		fastBackwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("fast.backward.icon"), "Fast Back");
		simPanel.add(fastBackwardButton);

		stepBackwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("step.backward.icon"), "Step Back");
		simPanel.add(stepBackwardButton);

		runBackwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("run.backward.icon"), "Run Back");
		simPanel.add(runBackwardButton);

		runForwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("run.forward.icon"), "Run Forward");
		simPanel.add(runForwardButton);

		stepForwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("step.forward.icon"), "Step Forward");
		simPanel.add(stepForwardButton);

		fastForwardButton = ComponentFactory.makeSmallButton(Resources.getIcon("fast.forward.icon"), "Fast Forward");
		simPanel.add(fastForwardButton);

		gotoEndButton = ComponentFactory.makeSmallButton(Resources.getIcon("goto.end.icon"), "Goto End");
		simPanel.add(gotoEndButton);

		pauseButton = ComponentFactory.makeSmallButton(Resources.getIcon("pause.icon"), "Pause");
		simPanel.add(pauseButton);

		p.add(configPanel, BorderLayout.CENTER);
		p.add(topPanel, BorderLayout.NORTH);
		p.add(simPanel, BorderLayout.SOUTH);

		addActionListeners();

		return p;

	}

	private ComboBoxModel createStepModel() {

		Integer[] items = new Integer[] { 1, 2, 5, 10, 15, 30, 60, 120 };

		DefaultComboBoxModel model = new DefaultComboBoxModel(items);
		return model;
	}

	private ComboBoxModel createDelayModel() {

		Integer[] items = new Integer[] { 1, 2, 4, 10 };

		DefaultComboBoxModel model = new DefaultComboBoxModel(items);
		return model;
	}

	private ComboBoxModel createTimeModel() {

		Integer[] items = new Integer[] { -120, -60, -30, -10, -1, 0, 1, 10, 30, 60, 120 };

		DefaultComboBoxModel model = new DefaultComboBoxModel(items);
		return model;
	}

	private void addActionListeners() {

		infoButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(AitoffAstroSimulatorControlDialog.this,
						"Use the play controls to set the direction and speed of the simulation",
						"Astrometry Simulator Control", JOptionPane.INFORMATION_MESSAGE,
						Resources.getIcon("simulate.icon"));

			}
		});

		runForwardButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				runStepLength = 60000 * ((Integer) runStepField.getSelectedItem()).longValue();
				delay = 1000 * ((Integer) delayField.getSelectedItem()).longValue();
				simulator.runSimulation(runStepLength, delay);
			}
		});

		fastForwardButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				runStepLength = 600000 * ((Integer) runStepField.getSelectedItem()).longValue();
				delay = 1000 * ((Integer) delayField.getSelectedItem()).longValue();
				simulator.runSimulation(runStepLength, delay);
			}
		});

		stepForwardButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				runStepLength = 60000 * ((Integer) runStepField.getSelectedItem()).longValue();
				simulator.stepSimulation(runStepLength);
			}
		});

		gotoEndButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				simulator.gotoEnd();
			}
		});

		runBackwardButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				runStepLength = 60000 * ((Integer) runStepField.getSelectedItem()).longValue();
				delay = 1000 * ((Integer) delayField.getSelectedItem()).longValue();
				simulator.runSimulation(-runStepLength, delay);
			}
		});

		fastBackwardButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				runStepLength = 600000 * ((Integer) runStepField.getSelectedItem()).longValue();
				delay = 1000 * ((Integer) delayField.getSelectedItem()).longValue();
				simulator.runSimulation(-runStepLength, delay);
			}
		});

		stepBackwardButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				runStepLength = 60000 * ((Integer) runStepField.getSelectedItem()).longValue();
				simulator.stepSimulation(-runStepLength);
			}
		});

		gotoStartButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				simulator.gotoStart();
			}
		});

		pauseButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				simulator.pauseSimulation();
			}
		});

		applyButton.addActionListener(new ActionListener() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {

				try {
					startTime = (sdf.parse(startField.getText())).getTime();
					endTime = (sdf.parse(endField.getText())).getTime();
					System.err.println("Start: " + (new Date(startTime)) + ", End: " + (new Date(endTime)));
					simulator.configure(startTime, endTime);

					// local settings
					simulationTime = startTime;

					// disable backward buttons
					stepBackwardButton.setEnabled(false);
					gotoStartButton.setEnabled(false);
					runBackwardButton.setEnabled(false);
					fastBackwardButton.setEnabled(false);

					stepForwardButton.setEnabled(true);
					gotoEndButton.setEnabled(true);
					runForwardButton.setEnabled(true);
					fastForwardButton.setEnabled(true);

				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

	}

	@Override
	public void simulationTimeUpdated(long time) {
		simulationTime = time;
		simulationTimeField.setText(sdf.format(new Date(time)));

		boolean cangofwd = (simulationTime < endTime);
		boolean cangoback = (simulationTime > startTime);

		// these rules need tweaking eg cant change speed without pausing first
		// when a sim starts ALL the move buttons are disabled until the pause
		// is hit

		stepBackwardButton.setEnabled(cangoback);
		gotoStartButton.setEnabled(cangoback);
		runBackwardButton.setEnabled(cangoback);
		fastBackwardButton.setEnabled(cangoback);

		stepForwardButton.setEnabled(cangofwd);
		gotoEndButton.setEnabled(cangofwd);
		runForwardButton.setEnabled(cangofwd);
		fastForwardButton.setEnabled(cangofwd);

	}

	@Override
	public void simulationRunning(boolean run) {
		if (run) {
			modeLabel.setText("Running...");
			modeLabel.setForeground(Color.green);

			// disable all move buttons, enable pause button

		} else {
			modeLabel.setText("Idle");
			modeLabel.setForeground(Color.orange);

			// disable pause button enable all valid move buttons depends on
			// time

		}

	}

	private class MyWindowListener implements WindowListener {

		@Override
		public void windowOpened(WindowEvent we) {
			System.err.println("W-opened");
		}

		@Override
		public void windowClosing(WindowEvent we) {
			System.err.println("W-closing");
			setVisible(false);
		}

		@Override
		public void windowClosed(WindowEvent we) {
			System.err.println("W-closed");
		}

		@Override
		public void windowIconified(WindowEvent we) {
			System.err.println("W-iconified");
		}

		@Override
		public void windowDeiconified(WindowEvent we) {
			System.err.println("W-deiconified");
		}

		@Override
		public void windowActivated(WindowEvent we) {
			System.err.println("W-activated");
			trackingPerspective.simulationActive(true);			
		}

		@Override
		public void windowDeactivated(WindowEvent we) {
			System.err.println("W-deactivated");
			trackingPerspective.simulationActive(false);
		}

	}

}
