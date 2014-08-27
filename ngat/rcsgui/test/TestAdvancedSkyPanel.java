/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jfree.chart.ChartPanel;

import ngat.ems.SkyModelMonitor;
import ngat.ems.SkyModelUpdateListener;
import ngat.ems.test.TestSkyModelMonitor;

/**
 * @author eng
 * 
 */
public class TestAdvancedSkyPanel extends JPanel {

	/** External sky model.*/
	SkyModelMonitor sky;
	
	/** Handles sky model updates.*/
	SkyModelUpdateHandler smh;
	
	/** Displays seeing graphs. */
	TestSkyModelMonitor sm;

	/** Displays current sky model values. */
	JPanel currentDataPanel;
	JTextField seeingPredField;
	JTextField seeingCatField;
	JTextField seeingLatestField;
	JTextField extField;
	JTextField extCatField;

	/** Displays seeing histogram. */
	SkyModelSeeingHistogram histogramPanel;

	/** Controls. */
	JPanel controlPanel;
	JCheckBox ckStdSelect;
	JCheckBox ckStdCorrSelect;
	JCheckBox ckSciSelect;
	JCheckBox ckSciCorrSelect;
	JCheckBox ckPredictSelect;

	JRadioButton rb1HrBtn;
	JRadioButton rb2HrBtn;
	JRadioButton rb4HrBtn;
	JRadioButton rb12HrBtn;
	JRadioButton rb24HrBtn;
	JRadioButton rb48HrBtn;

	/**
	 * 
	 */
	public TestAdvancedSkyPanel(SkyModelMonitor sky) throws Exception {
		super(true);
		this.sky = sky;
		createPanel();
		smh = new SkyModelUpdateHandler();
	}

	public void bind() throws Exception {
		sky.addSkyModelUpdateListener(smh);
	}
	
	private void createPanel() throws Exception {

		setLayout(new BorderLayout());

		currentDataPanel = createCurrentDataPanel();

		histogramPanel = createHistogramPanel();

		controlPanel = createControlPanel();

		sm = new TestSkyModelMonitor("LT", 10000);
		ChartPanel sup = sm.createChartPanel();
		sup.getChart().getTitle().setFont(new Font("Serif", Font.ITALIC, 10));

		JPanel rightPanel = new JPanel(true);
		rightPanel.setLayout(new BorderLayout());
		rightPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		
		rightPanel.add(sup, BorderLayout.CENTER);
		rightPanel.add(controlPanel, BorderLayout.WEST);
		
		add(rightPanel, BorderLayout.CENTER);

		JPanel leftPanel = new JPanel(true);
		leftPanel.setLayout(new BorderLayout());
		leftPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
		
		leftPanel.add(currentDataPanel, BorderLayout.CENTER);
		leftPanel.add(histogramPanel, BorderLayout.SOUTH);

		add(leftPanel, BorderLayout.WEST);

	}

	private JPanel createControlPanel() {
		JPanel controlPanel = new JPanel(true);
		controlPanel.setLayout(new BorderLayout());

		// PLOT SETTINGS
		JPanel setPanel = new JPanel(true);
		setPanel.setLayout(new GridLayout(5, 2));
		setPanel.setBorder(BorderFactory.createLineBorder(Color.black));

		ckStdSelect = new JCheckBox("Standards");
		setPanel.add(ckStdSelect);

		ckStdCorrSelect = new JCheckBox("(corrected)");
		setPanel.add(ckStdCorrSelect);

		ckSciSelect = new JCheckBox("Science");
		setPanel.add(ckSciSelect);

		ckSciCorrSelect = new JCheckBox("(corrected)");
		setPanel.add(ckSciCorrSelect);

		ckPredictSelect = new JCheckBox("Prediction");
		setPanel.add(ckPredictSelect);

		// TIME SETTINGS
		JPanel timePanel = new JPanel(true);
		timePanel.setLayout(new GridLayout(6, 1));
		timePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		ActionListener tsl = new TimePanelActionListener();

		ButtonGroup bg = new ButtonGroup();
		rb1HrBtn = new JRadioButton("1 h");
		timePanel.add(rb1HrBtn);
		bg.add(rb1HrBtn);

		rb2HrBtn = new JRadioButton("2 h");
		timePanel.add(rb2HrBtn);
		bg.add(rb2HrBtn);

		rb4HrBtn = new JRadioButton("4 h");
		timePanel.add(rb4HrBtn);
		bg.add(rb4HrBtn);

		rb12HrBtn = new JRadioButton("12 h");
		timePanel.add(rb12HrBtn);
		bg.add(rb12HrBtn);

		rb24HrBtn = new JRadioButton("24 h");
		timePanel.add(rb24HrBtn);
		bg.add(rb24HrBtn);

		rb48HrBtn = new JRadioButton("48 h");
		timePanel.add(rb48HrBtn);
		bg.add(rb48HrBtn);

		controlPanel.add(setPanel, BorderLayout.CENTER);
		controlPanel.add(timePanel, BorderLayout.SOUTH);

		return controlPanel;

	}

	private SkyModelSeeingHistogram createHistogramPanel() {
		SkyModelSeeingHistogram histogram = new SkyModelSeeingHistogram("sss");
		return histogram;
	}

	private JPanel createCurrentDataPanel() {
		JPanel currentPanel = new JPanel(true);
		currentPanel.setLayout(new GridLayout(5, 2));

		currentPanel.add(new JLabel("Predicted (CZR)"));
		seeingPredField = new JTextField(6);
		currentPanel.add(seeingPredField);

		currentPanel.add(new JLabel("Seeing Category"));
		seeingCatField = new JTextField(6);
		currentPanel.add(seeingCatField);

		currentPanel.add(new JLabel("Latest seeing"));
		seeingLatestField = new JTextField(6);
		currentPanel.add(seeingLatestField);

		currentPanel.add(new JLabel("Extinction"));
		extField = new JTextField(6);
		currentPanel.add(extField);

		currentPanel.add(new JLabel("Ext. Category"));
		extCatField = new JTextField(6);
		currentPanel.add(extCatField);

		return currentPanel;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			
			SkyModelMonitor sky = (SkyModelMonitor)Naming.lookup("rmi://ltsim1/SkyModel");
			
			TestAdvancedSkyPanel tap = new TestAdvancedSkyPanel(sky);
			tap.bind();

			JFrame f = new JFrame("Test");
			f.getContentPane().add(tap);
			f.pack();
			f.setVisible(true);

			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * @author eng
	 *
	 */
	public class SkyModelUpdateHandler extends UnicastRemoteObject implements SkyModelUpdateListener {

		protected SkyModelUpdateHandler() throws RemoteException {
			super();			
		}

		@Override
		public void seeingUpdated(long time, double raw, double corrected, double prediction, double alt, double azm, double wav, boolean standard, String source, String tgt) throws RemoteException {
			System.err.println("Sky update: "+raw);
			histogramPanel.updateSeeing(raw);
			sm.seeingUpdated(time, raw, corrected, prediction, alt, azm, wav, standard, source, tgt);
			seeingCatField.setText(""+corrected);
			seeingLatestField.setText(""+raw);
		}

		@Override
		public void extinctionUpdated(long arg0, double arg1) throws RemoteException {
			// TODO Auto-generated method stub
			
		}

		
		
	}

	public class TimePanelActionListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			System.err.println("Selected: " + e.getActionCommand());

		}

	}

}
