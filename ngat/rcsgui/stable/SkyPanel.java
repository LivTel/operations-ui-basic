package ngat.rcsgui.stable;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.rmi.*;

import ngat.ems.*;
import ngat.astrometry.*;
import ngat.phase2.*;

public class SkyPanel extends JPanel {

	private JTextField seeingValueField;

	private JTextField seeingCatField;

	private JTextField extinctionField;

    private JTextField skybField;

    private SkyBrightnessCalculator skycalc;

	public SkyPanel() {
		super(true);

		setLayout(new GridLayout(4, 2));
		setBorder(BorderFactory.createTitledBorder(RcsGUI.loweredbevel, "Sky Model", TitledBorder.LEFT,
				TitledBorder.BELOW_TOP, RcsGUI.TITLE_FONT, Color.blue));
		setBackground(RcsGUI.LIGHTSLATE);
		setOpaque(true);

		JLabel label = new JLabel("Pred. Seeing at zen. (as)");
		// label.setOpaque(true);
		label.setFont(RcsGUI.SMALL_FONT_2);
		add(label);

		seeingValueField = new JTextField(4);
		seeingValueField.setBorder(RcsGUI.raisedbevel);
		seeingValueField.setBackground(RcsGUI.LIGHTSLATE);
		seeingValueField.setOpaque(true);
		seeingValueField.setFont(new Font("courier", Font.PLAIN, 9));
		add(seeingValueField);

		label = new JLabel("Seeing category");
		// label.setOpaque(true);
		label.setFont(RcsGUI.SMALL_FONT_2);
		add(label);

		seeingCatField = new JTextField(8);
		seeingCatField.setBorder(RcsGUI.raisedbevel);
		seeingCatField.setBackground(RcsGUI.LIGHTSLATE);
		seeingCatField.setOpaque(true);
		seeingCatField.setFont(new Font("courier", Font.PLAIN, 9));
		add(seeingCatField);

		label = new JLabel("Extinction category");
		// label.setOpaque(true);
		label.setFont(RcsGUI.SMALL_FONT_2);
		add(label);

		extinctionField = new JTextField(8);
		extinctionField.setBorder(RcsGUI.raisedbevel);
		extinctionField.setBackground(RcsGUI.LIGHTSLATE);
		extinctionField.setOpaque(true);
		extinctionField.setFont(new Font("courier", Font.PLAIN, 9));
		add(extinctionField);

		label = new JLabel("Sky brightness");
		label.setFont(RcsGUI.SMALL_FONT_2);
                add(label);

		skybField = new JTextField(8);
		skybField.setBorder(RcsGUI.raisedbevel); 
		skybField.setBackground(RcsGUI.LIGHTSLATE);
		skybField.setOpaque(true);
		skybField.setFont(new Font("courier", Font.PLAIN, 9));
		add(skybField); 

		try {
		    BasicSite site = new BasicSite("LT", Math.toRadians(28.7624), Math.toRadians(-17.8792));
		    skycalc = new SkyBrightnessCalculator(site);
		} catch (Exception e) {
		    e.printStackTrace();
		}
	}

	/** Update seeing to supplied value (CZR). */
	public void updateSeeing(double seeing) {

		if (Double.isNaN(seeing)) {
			updateSeeingField(seeing, "UNKNOWN", Color.YELLOW, Color.BLUE);
			return;
		}

		if (seeing < 0.8) {
			updateSeeingField(seeing, "GOOD", Color.GREEN, Color.BLUE);
		} else if (seeing < 1.3) {
			updateSeeingField(seeing, "AVERAGE", Color.ORANGE, Color.BLUE);
		} else if (seeing < 3.0) {
			updateSeeingField(seeing, "POOR", Color.RED, Color.BLUE);
		} else {
			updateSeeingField(seeing, "USABLE", Color.PINK, Color.BLUE);
		}

	}

	public void updatePhotom(double photom) {
		if (Double.isNaN(photom)) {
			updatePhotomField("UNKNOWN", Color.YELLOW, Color.BLUE);
			return;
		}

		if (photom < 0.5) {
			updatePhotomField("PHOTOMETRIC", Color.GREEN, Color.BLUE);
		} else {
			updatePhotomField("SPECTROSCOPIC", Color.ORANGE, Color.BLUE);
		}

	}

    public void updateSkyb(double alt, double azm) throws Exception {

	int skybcat = skycalc.getSkyBrightnessCriterion(alt, azm, System.currentTimeMillis());

	switch (skybcat) {
	case IObservingConstraint.DAYTIME:
	    updateSkybField("DAYTIME", Color.ORANGE, Color.BLUE);
	    break;
	case IObservingConstraint.MAG_10:
	    updateSkybField("10.0", RcsGUI.LIGHTSLATE, Color.BLUE);
	    break;
        case IObservingConstraint.MAG_6:
	    updateSkybField("6.0", RcsGUI.LIGHTSLATE, Color.BLUE);
	    break;
        case IObservingConstraint.MAG_4:
	    updateSkybField("4.0", RcsGUI.LIGHTSLATE, Color.BLUE);
	    break;
        case IObservingConstraint.MAG_2:
	    updateSkybField("2.0", RcsGUI.LIGHTSLATE, Color.BLUE);
            break;
        case IObservingConstraint.MAG_1P5:
	    updateSkybField("1.5", RcsGUI.LIGHTSLATE, Color.BLUE);
            break;
        case IObservingConstraint.MAG_0P75:
	    updateSkybField("0.75", RcsGUI.LIGHTSLATE, Color.BLUE);
            break;
        case IObservingConstraint.DARK:
	    updateSkybField("DARK", Color.BLACK, Color.CYAN);
	    break;
	}
	

    }

	private void updateSeeingField(double seeing, String catName, Color bg, Color fg) {
		seeingValueField.setText(String.format("%3.2f", seeing));
		seeingCatField.setForeground(fg);
		seeingCatField.setBackground(bg);
		seeingCatField.setText(catName);
	}

	private void updatePhotomField(String pcat, Color bg, Color fg) {
		extinctionField.setText(pcat);
		extinctionField.setForeground(fg);
		extinctionField.setBackground(bg);
	}

    private void updateSkybField(String scat, Color bg, Color fg) {
	skybField.setText(scat);
	skybField.setForeground(fg);
	skybField.setBackground(bg);
    }


	public void asynchPredict(String skyModelHost, long cadence) {
		final String fSkyModelHost = skyModelHost;
		final long fcadence = cadence;

		Runnable r = new Runnable() {

			@Override
			public void run() {
				predict(fSkyModelHost, fcadence);
			}
		};

		(new Thread(r)).start();
	}

	private void predict(String skyModelHost, long cadence) {

		while (true) {

			try {
				SkyModel sky = (SkyModel) Naming.lookup("rmi://" + skyModelHost + "/SkyModel");
				System.err.println("Located skymodel: " + sky);
				double s = sky.getSeeing(700.0, Math.toRadians(90.0), 0.0, System.currentTimeMillis());
				System.err.println("SKYPanel: Predict seeing " + s);
				updateSeeing(s);

				double p = sky.getExtinction(700.0, Math.toRadians(90.0), 0.0, System.currentTimeMillis());
				System.err.println("SKYPanel: Predict photom: " + p);
				updatePhotom(p);

			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(60 * 1000L);
			} catch (InterruptedException e) {
			}

		}

	}

}
