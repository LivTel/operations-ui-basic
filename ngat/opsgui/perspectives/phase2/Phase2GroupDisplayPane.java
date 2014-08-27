/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.awt.Color;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.components.LinePanel;
import ngat.phase2.IObservingConstraint;
import ngat.phase2.ITimingConstraint;
import ngat.phase2.XAirmassConstraint;
import ngat.phase2.XFixedTimingConstraint;
import ngat.phase2.XFlexibleTimingConstraint;
import ngat.phase2.XMinimumIntervalTimingConstraint;
import ngat.phase2.XMonitorTimingConstraint;
import ngat.phase2.XSeeingConstraint;
import ngat.phase2.XPhotometricityConstraint;
import ngat.phase2.XSkyBrightnessConstraint;
import ngat.sms.GroupItem;
import ngat.sms.ObservingConstraintAdapter;

/**
 * @author eng
 * 
 */
public class Phase2GroupDisplayPane extends JPanel implements GroupSelectionListener {

	private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	private SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	private IdentitySection identitySection;

	private TimingSection timingSection;

	private ConstraintsSection constraintsSection;

	private JPanel left;

	private JPanel right;

	/**
	 * 
	 */
	public Phase2GroupDisplayPane() {
		super(true);

		// better get this from gui?
		sdf.setTimeZone(UTC);
		
		setLayout(new BorderLayout());

		left = new JPanel(true);
		left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));

		// Identity section
		identitySection = new IdentitySection();
		left.add(identitySection);

		// Timing section
		timingSection = new TimingSection();
		left.add(timingSection);

		// Observing-constraints section
		constraintsSection = new ConstraintsSection();
		left.add(constraintsSection);

		left.add(Box.createVerticalGlue());

		// TODO later we shall add the sequence panel.
		right = new JPanel(true);
		right.setPreferredSize(new Dimension(450, 500));

		add(right, BorderLayout.CENTER);
		add(left, BorderLayout.WEST);

	}

	private class IdentitySection extends JPanel {

		private JTextField idField;

		private JTextField nameField;

		private JTextField proposalField;

		private JTextField userField;

		private JTextField tagField;

		/**
		 * 
		 */
		public IdentitySection() {
			super(true);
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createTitledBorder("Identity"));

			LinePanel line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("Ident"));
			idField = ComponentFactory.makeEntryField(8);
			line.add(idField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("Group"));
			nameField = ComponentFactory.makeEntryField(14);
			nameField.setText("NAME OF GROUP");
			line.add(nameField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("Proposal"));
			proposalField = ComponentFactory.makeEntryField(8);
			line.add(proposalField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("User"));
			userField = ComponentFactory.makeEntryField(14);
			line.add(userField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("TAG"));
			tagField = ComponentFactory.makeEntryField(8);
			line.add(tagField);
			add(line);
		}

		public void updateIdentity(GroupItem group) {
			
			idField.setText("" + group.getID());
			nameField.setText(group.getName());
			proposalField.setText(group.getProposal().getName());
			userField.setText(group.getUser().getName());
			tagField.setText(group.getTag().getName());
		}

	}

	private class TimingSection extends JPanel {

		private JTextField typeField;

		private JTextField startField;

		private JTextField endField;

		private JTextField repeatField;

		private JTextField intervalField;

		private JTextField windowField;

		/**
		 * new JPanel());
		 */
		public TimingSection() {
			super(true);
			
			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createTitledBorder("Timing"));

			LinePanel line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("Class"));
			typeField = ComponentFactory.makeEntryField(8);
			line.add(typeField);
			add(line);
			
			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("Start"));
			startField = ComponentFactory.makeEntryField(14);
			line.add(startField);
			add(line);
			
			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("End"));
			endField = ComponentFactory.makeEntryField(14);
			line.add(endField);
			add(line);
			
			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("Interval"));
			intervalField = ComponentFactory.makeEntryField(8);
			line.add(intervalField);
			add(line);
			
			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("Window"));
			windowField = ComponentFactory.makeEntryField(8);
			line.add(windowField);
			add(line);
			
			line = ComponentFactory.makeLinePanel(200);
			line.add(ComponentFactory.makeLabel("Repeat"));
			repeatField = ComponentFactory.makeEntryField(6);
			line.add(repeatField);
			add(line);
			
		}
		
		public void updateTiming(GroupItem group) {
			ITimingConstraint timing = group.getTimingConstraint();
			
			if (timing == null)
				return;
			
			if (timing instanceof XMonitorTimingConstraint) {
				typeField.setText("MONITOR");
				intervalField.setText(""+((XMonitorTimingConstraint)timing).getPeriod()/60000);
				windowField.setText(""+((XMonitorTimingConstraint)timing).getWindow()/60000);
				repeatField.setText("N/A");
			} else if
				(timing instanceof XMinimumIntervalTimingConstraint) {
				typeField.setText("INTERVAL");
				intervalField.setText(""+((XMinimumIntervalTimingConstraint)timing).getMinimumInterval()/60000);
				windowField.setText(""+((XMinimumIntervalTimingConstraint)timing).getMinimumInterval()/60000);
				repeatField.setText(""+((XMinimumIntervalTimingConstraint)timing).getMaximumRepeats());
			} else if
				(timing instanceof XFlexibleTimingConstraint) {
				typeField.setText("FLEXIBLE");
				intervalField.setText("N/A");
				windowField.setText("N/A");
				repeatField.setText("N/A");
			} else if
				(timing instanceof 	XFixedTimingConstraint) {
				typeField.setText("FIXED");	
				intervalField.setText("N/A");
				windowField.setText("N/A");
				repeatField.setText("N/A");
			}
			
			long start = timing.getStartTime();
			startField.setText(sdf.format(start));
			
			long end = timing.getEndTime();
			endField.setText(sdf.format(end));
		}
		
	}

	private class ConstraintsSection extends JPanel {

		JTextField seeingField;
		JTextField photomField;
		JTextField airmassField;
		JTextField haField;
		JTextField skybField;

		public ConstraintsSection() {

			setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			setBorder(BorderFactory.createTitledBorder("Constraints"));

			LinePanel line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("Airmass"));
			airmassField = ComponentFactory.makeEntryField(8);
			line.add(airmassField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("Seeing"));
			seeingField = ComponentFactory.makeEntryField(8);
			line.add(seeingField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("Photom"));
			photomField = ComponentFactory.makeEntryField(8);
			line.add(photomField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("Skybr"));
			skybField = ComponentFactory.makeEntryField(8);
			line.add(skybField);
			add(line);

			line = ComponentFactory.makeLinePanel(200);

			line.add(ComponentFactory.makeLabel("HA"));
			haField = ComponentFactory.makeEntryField(8);
			line.add(haField);
			add(line);

		}

		public void updateConstraints(GroupItem group) {

			ObservingConstraintAdapter cs = new ObservingConstraintAdapter(group);

			XAirmassConstraint air = cs.getAirmassConstraint();
			if (air == null) {
				airmassField.setText("dome-limit");
				airmassField.setForeground(Color.pink);
			} else {
				airmassField.setText("" + air.getMaximumAirmass());
				airmassField.setForeground(Color.blue);
			}

			XSeeingConstraint seeing = cs.getSeeingConstraint();
			if (seeing == null) {
				seeingField.setText("unrestricted");
				seeingField.setForeground(Color.pink);
			} else {
				seeingField.setText("" + seeing.getSeeingValue());
				seeingField.setForeground(Color.blue);
			}

			XPhotometricityConstraint photom = cs.getPhotometricityConstraint();
			if (photom == null) {
				photomField.setText("unrestricted");
				photomField.setForeground(Color.pink);
			} else {
				int pcat = photom.getPhotometricityCategory();
				String strpcat = null;
				if (pcat == IObservingConstraint.PHOTOMETRIC)
					strpcat = "PHOTOM";
				else
					strpcat = "SPECTRO";
				photomField.setText(strpcat);
				photomField.setForeground(Color.blue);
			}

			XSkyBrightnessConstraint skyb = cs.getSkyBrightnessConstraint();
			if (skyb == null) {
				skybField.setText("unrestricted");
				skybField.setForeground(Color.pink);
			} else {
				skybField.setText("" + skyb.getSkyBrightnessCategory());
				skybField.setForeground(Color.blue);
			}

		}

	}

	public void updateGroup(GroupItem group) {
		identitySection.updateIdentity(group);
		timingSection.updateTiming(group);
		constraintsSection.updateConstraints(group);
	}

	@Override
	public void groupSelectionChanged(final GroupItem group) throws Exception {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				updateGroup(group);
			}
		});
	}

}
