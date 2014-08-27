/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import ngat.astrometry.AstroCatalog;
import ngat.astrometry.AstroFormatter;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.opsgui.base.Resources;
import ngat.opsgui.perspectives.tracking.CatalogDisplay;
import ngat.opsgui.perspectives.tracking.CatalogDisplayDescriptor;
import ngat.opsgui.perspectives.tracking.CatalogDisplayEditorPanel;
import ngat.opsgui.perspectives.tracking.TrackingPerspective;
import ngat.phase2.XExtraSolarTarget;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.TelescopeStatus;

/**
 * Display an Aitoff plot and test history-aging colors
 * 
 * @author eng
 * 
 */
public class TestAitoffLimitColors {

	JFrame f;
	
	TrackingPerspective tp;
	
	int ncat = 0;

	/**
	 * 
	 */
	public TestAitoffLimitColors() {
		super();

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Resources.setDefaults("/home/eng/rcsgui");
		TestAitoffLimitColors test = new TestAitoffLimitColors();
		test.exec();
	}

	public void exec() {
		
		ISite site = new BasicSite("test", Math.toRadians(28.0), Math.toRadians(-17.0));
		tp = new TrackingPerspective(f, site);
		
		
		BasicAstrometrySiteCalculator astro = new BasicAstrometrySiteCalculator(site);

		f = new JFrame("Aitoff Track Display Test");
		f.getContentPane().add(tp);

		f.setJMenuBar(createMenuBar());

	
		CatalogDisplayDescriptor cdd = new CatalogDisplayDescriptor("DEFAULT");
		cdd.setSymbol(CatalogDisplayDescriptor.OPEN_DIAMOND_SYMBOL);
		cdd.setColor(Color.magenta.brighter());
		cdd.setShowLabel(true);
		cdd.setShowSymbol(true);
		AstroCatalog cat = new AstroCatalog("DEFAULT");
		CatalogDisplay display = new CatalogDisplay(cat, cdd);
		try {
			tp.addCatalogDisplay(display);
		} catch (Exception e) {
			e.printStackTrace();
		}

		f.pack();
		f.setVisible(true);

		// create some tracks

		long start = System.currentTimeMillis();
		long time = start;

		int iTarget = 0;
		int nPointsTrack = 0;
		int nPointMax = (int) (Math.random() * 20.0 + 40.0);
		// first target
		double ra = Math.random() * Math.PI * 2.0;
		double dec = Math.random() * Math.PI * 0.5;
		XExtraSolarTarget target = new XExtraSolarTarget("T:" + 0);
		target.setRa(ra);
		target.setDec(dec);
		try {
			tp.addTargetToCatalog("DEFAULT", target);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.err.printf("New Target: RA: %12s Dec: %12s \n", AstroFormatter.formatHMS(ra, ":"),
				AstroFormatter.formatDMS(dec, ":"));
		BasicTargetCalculator track = new BasicTargetCalculator(target, site);
		while (time < start + 4 * 3600 * 1000L) {

			if (nPointsTrack == nPointMax) {
				// new target
				iTarget++;
				nPointsTrack = 0;
				nPointMax = (int) (Math.random() * 20.0 + 40.0);

				ra = Math.random() * Math.PI * 2.0;
				dec = Math.random() * Math.PI * 0.5;
				target = new XExtraSolarTarget("T:" + iTarget);
				target.setRa(ra);
				target.setDec(dec);
				try {
					tp.addTargetToCatalog("DEFAULT", target);
				} catch (Exception e) {
					e.printStackTrace();
				}
				System.err.printf("New Target: RA: %12s Dec: %12s \n", AstroFormatter.formatHMS(ra, ":"),
						AstroFormatter.formatDMS(dec, ":"));
				track = new BasicTargetCalculator(target, site);
			}

			// where is the target in alt/az ?
			try {
				Coordinates c = track.getCoordinates(time);
				double alt = astro.getAltitude(c, time);
				double azm = astro.getAzimuth(c, time);
				
				PrimaryAxisStatus azmstat = new PrimaryAxisStatus();
				azmstat.setMechanismName("AZM");
				azmstat.setCurrentPosition(azm);
				azmstat.setMechanismState(TelescopeStatus.MOTION_TRACKING);
				azmstat.setStatusTimeStamp(time);
				tp.telescopeStatusUpdate(azmstat);
				tp.telescopeStatusUpdate(azmstat);
				
				PrimaryAxisStatus altstat = new PrimaryAxisStatus();
				altstat.setMechanismName("ALT");
				altstat.setCurrentPosition(alt);
				altstat.setMechanismState(TelescopeStatus.MOTION_TRACKING);
				altstat.setStatusTimeStamp(time);
				tp.telescopeStatusUpdate(altstat);
				
				//aitoff.addCoordinate(time, alt, azm, Color.pink);// this color			
				nPointsTrack++;
				System.err.printf("Target at: Azm: %4.2f Alt: %4.2f \n", Math.toDegrees(azm), Math.toDegrees(alt));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				Thread.sleep(30 * 1000L);
			} catch (InterruptedException ix) {
			}
			time += 30 * 1000L;
		}

	}

	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();

		JMenu menu = new JMenu("Catalogs");

		ActionListener ml = new MenuListener();
		JMenuItem item = new JMenuItem("Load from file");
		item.addActionListener(ml);

		menu.add(item);
		bar.add(menu);
		return bar;
	}

	public class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			JFileChooser chooser = new JFileChooser();
			int returnVal = chooser.showDialog(null, "Load from local catalog file");
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				System.out.println("You chose this file: " + chooser.getSelectedFile().getName());

				try {
					// here we could either extract the catname from its
					// file or
					// specify via dialog
					ncat++;
					AstroCatalog cat = AstroCatalog.loadCatalog("TEST-"+ncat, chooser.getSelectedFile());

					CatalogDisplayDescriptor desc = new CatalogDisplayDescriptor(cat.getCatalogName());
					desc.setSymbol(CatalogDisplayDescriptor.OPEN_DIAMOND_SYMBOL);
					desc.setColor(Color.magenta.brighter());
					desc.setShowLabel(true);
					desc.setShowSymbol(true);
					
					JDialog dlg = new JDialog(f, true);
					dlg.setTitle("Catalog Display Editor");			
					
					CatalogDisplayEditorPanel cep = new CatalogDisplayEditorPanel(dlg, tp, cat);
					cep.createCatalogDisplay();
						
					dlg.getContentPane().add(cep);
					dlg.pack();
					dlg.setVisible(true);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}
}
