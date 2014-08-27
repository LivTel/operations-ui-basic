/**
 * 
 */
package ngat.opsgui.test;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.BasicTargetTrackCalculatorFactory;
import ngat.astrometry.JAstroSlalib;
import ngat.astrometry.TargetTrackCalculator;
import ngat.astrometry.components.RotatorPositionSelectionListener;
import ngat.astrometry.components.RotatorSkyTimePanel;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class RotatorSkyCheck {

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	JTextField rafield;
	JTextField decfield;
	JTextField startfield;
	JTextField durationfield;
	JTextField forfield;
	
	JComboBox ibox;
	
	JButton btn;
	
	Map<String, Double> iomap;
	
	public RotatorSkyCheck() {
		
		iomap = new HashMap<String, Double>();
		iomap.put("IO:O", 0.0);
		iomap.put("RISE", Math.toRadians(-44.4));
		iomap.put("RINGO3", Math.toRadians(-87.8));
		iomap.put("FRODO", 0.0);
		
		
		JPanel p1 = new JPanel();
		p1.setLayout(new GridLayout(7, 2));
		
		p1.add(new JLabel("RA (hh:mm:ss)"));
		rafield = new JTextField(10);
		p1.add(rafield);
		
		p1.add(new JLabel("Dec (dd:mm:ss)"));
		decfield = new JTextField(10);
		p1.add(decfield);
		
		p1.add(new JLabel("Start (yyyy-MM-dd-HH:mm)"));
		startfield = new JTextField(20);
		p1.add(startfield);
		
		p1.add(new JLabel("For (hours)"));
		forfield = new JTextField(10);
		p1.add(forfield);
		
		p1.add(new JLabel("Duration (secs)"));
		durationfield = new JTextField(10);
		p1.add(durationfield);
		
		p1.add(new JLabel("Instrument"));
		ibox = new JComboBox(new String[] {"IO:O", "RISE", "RINGO3", "FRODO"});
		p1.add(ibox);
		
		btn = new JButton("Run");
		btn.addActionListener(new BListener());
		p1.add(btn);
		

		JFrame f1 = new JFrame("Sky angle checker");
		f1.getContentPane().add(p1);
		f1.pack();
		f1.setVisible(true);
		
		
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		sdf.setTimeZone(UTC);
		TimeZone.setDefault(UTC);
		
		RotatorSkyCheck check = new RotatorSkyCheck();


	}
	
	private class BListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			// gather data and setup a rot frame
			
			try {
			double ra = AstroFormatter.parseHMS(rafield.getText(), ":");
			double dec = AstroFormatter.parseDMS(decfield.getText(), ":");
			
			XExtraSolarTarget target = new XExtraSolarTarget("test");
			target.setRa(ra);
			target.setDec(dec);
			
			long start = sdf.parse(startfield.getText()).getTime();
			long ftime = (long)(3600*1000*Double.parseDouble(forfield.getText()));
			long end = start +ftime;
			
			long duration = (long)(1000*Double.parseDouble(durationfield.getText()));
			
			String iname = (String)(ibox.getSelectedItem());
			if (iname == null || iname.equals("")) {
				JOptionPane.showMessageDialog(null,	"No instrument","Rotator sky angle Plot", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// TODO extract offset from iomap....
			if (!iomap.containsKey(iname)) {
				JOptionPane.showMessageDialog(null,	"Unknown instrument: "+iname,"Rotator sky angle Plot", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			double instoffset = 0.0;
			try {
				instoffset = iomap.get(iname);
			} catch (Exception ex) {
				JOptionPane.showMessageDialog(null,	"Unable to determine offsets for: "+iname ,"Rotator sky angle Plot", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			BasicSite site = new BasicSite("", Math.toRadians(28), Math.toRadians(-17));
			TargetTrackCalculator track = new BasicTargetCalculator(target, site);
			RotatorSkyTimePanel rtp = new RotatorSkyTimePanel(site, new JAstroSlalib(), new BasicTargetTrackCalculatorFactory(), instoffset);
			
			rtp.setPreferredSize(new Dimension(500, 300));
			
			JFrame f2 = new JFrame("Rotator Sky Angles: "+iname);
			f2.getContentPane().setLayout(new BorderLayout());
			
			JPanel p2 = new JPanel();
			p2.setLayout(new GridLayout(1, 2));
			JTextField tfield = new JTextField(20);
			JTextField rfield = new JTextField(20);
			p2.add(tfield);
			p2.add(rfield);
			
			f2.getContentPane().add(rtp, BorderLayout.CENTER);
			f2.getContentPane().add(p2, BorderLayout.SOUTH);
			
			PanelListener pl = new PanelListener(tfield, rfield);
			rtp.addRotatorPositionSelectionListener(pl);
			
			f2.pack();
			f2.setVisible(true);
			
			rtp.update(track, start, end, duration);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
	}
	
	private class PanelListener implements RotatorPositionSelectionListener {

		private JTextField timeField;
		
		private JTextField skyField;
		

		public PanelListener(JTextField timeField, JTextField skyField) {		
			this.timeField = timeField;
			this.skyField = skyField;
		}



		@Override
		public void rotatorSelection(long time, double sky) {
			System.err.println("PL called with "+time+" "+sky);
			timeField.setText(String.format("%tF %tT", time, time));
			skyField.setText(String.format("%4.2f",sky));
		}
		
		
	}

}
