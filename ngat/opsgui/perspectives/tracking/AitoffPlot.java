package ngat.opsgui.perspectives.tracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;
import java.util.SimpleTimeZone;
import java.text.SimpleDateFormat;

import javax.swing.*;

import ngat.astrometry.AstroCatalog;
import ngat.astrometry.AstrometrySiteCalculator;
import ngat.astrometry.BasicAstrometrySiteCalculator;
import ngat.astrometry.BasicSite;
import ngat.astrometry.BasicTargetCalculator;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;
import ngat.astrometry.SkyBrightnessCalculator;
import ngat.astrometry.SolarCalculator;
import ngat.astrometry.TargetTrackCalculator;
import ngat.astrometry.AstrometrySimulatorListener;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;

public class AitoffPlot extends JPanel implements AstrometrySimulatorListener {

	// SkyB colors
	public static final Color DARK_COLOR = new Color(0, 0, 128);
	public static final Color MAG_0p75_COLOR = new Color(205, 92, 92);
	public static final Color MAG_1p5_COLOR = new Color(135, 206, 250);
	public static final Color MAG_2_COLOR = new Color(176, 224, 230);
	public static final Color MAG_4_COLOR = new Color(25, 38, 238);
	public static final Color MAG_6_COLOR = new Color(224, 55, 255);
	public static final Color MAG_10_COLOR = new Color(27, 255, 0);
	public static final Color DAY_COLOR = new Color(255, 215, 0);

	// History age colors
	public static final Color HISTORY_NEWEST_COLOR = Color.green;
	public static final Color HISTORY_OLDEST_COLOR = new Color(101, 176, 230);

	public static final Color HISTORY_COLOR_0 = new Color(10, 245, 14);
	public static final Color HISTORY_COLOR_1 = new Color(20, 252, 248);
	public static final Color HISTORY_COLOR_2 = new Color(247, 17, 247);
	public static final Color HISTORY_COLOR_3 = new Color(247, 3, 105);
	public static final Color HISTORY_COLOR_4 = new Color(172, 93, 252);

	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	//public static final Font DEFAULT_MAIN_FONT = new Font("helvetica", Font.BOLD, 12);
	//public static final Font DEFAULT_SIM_TIME_FONT = new Font("helvetica", Font.ITALIC, 10);

	public static Dimension SIZE = new Dimension(600, 424);

	private List<Coordinate> coordinates;

	private CatalogDisplayManager cdm;

	private AstrometrySiteCalculator astro;

	/** True if a simulation is running. */
	private boolean simulation;

	/** Simulation time. */
	private long simulationTime;

	/** True if an altaz grid should be displayed - default true. */
	private boolean showAltAzGrid = false;

	/** True if require Declination grid. */
	private boolean showDecGrid = false;

	/** True if require an RA grid. */
	private boolean showRaGrid = false;

	/** True if require an airmass/az grid. Should not be used with alt grid. */
	private boolean showAirmassGrid = false;

	/** True if a sky-brightness overlay should be displayed. */
	private boolean showSkyB = false;

	/** how far back we display history trails. */
	private long historyAgeLimit = 24 * 3600 * 1000L;

	/** True if history is to display age-color variation. */
	private boolean historyColorAging = false;

	private ISite site;

	private SkyBrightnessCalculator skycalc;

	private Font mainFont;
	
	private Font simTimeFont;
	
	private boolean showTime;
	
	public static void main(String args[]) {

		AitoffPlot aitoff = new AitoffPlot();
		ISite site = new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0));
		aitoff.showTracks(site);

		JFrame f = new JFrame("Aitoff");
		f.getContentPane().setLayout(new BorderLayout());
		f.getContentPane().add(aitoff, BorderLayout.CENTER);
		// set the search location

		f.pack();
		f.setVisible(true);

	}

	public AitoffPlot() {
		super(true);

		coordinates = new Vector<AitoffPlot.Coordinate>();
		setBackground(Color.blue.darker().darker());

		cdm = new CatalogDisplayManager();

		Action stg = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				showDecGrid = true;
				System.err.println("Track on");
				repaint();
			}
		};
		Action nstg = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				showDecGrid = false;
				System.err.println("Track off");
				repaint();
			}
		};

		Action srg = new AbstractAction() {
			// set the search location
			
			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				showRaGrid = true;
				System.err.println("RA on");
				repaint();
			}
		};
		Action nrtg = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				showRaGrid = false;
				System.err.println("RA off");
				repaint();
			}
		};

		Action kon = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				showSkyB = true;
				System.err.println("SKY on");
				repaint();
			}
		};
		Action koff = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				showSkyB = false;
				System.err.println("SKY off");
				repaint();
			}
		};

		Action ah30 = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				historyAgeLimit = 30 * 60 * 1000L;
				System.err.println("H 30m");
				repaint();
			}
		};
		Action ah1 = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {

				historyAgeLimit = 3600 * 1000L;
				System.err.println("H 1h");
				repaint();
			}
		};
		Action ah2 = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				historyAgeLimit = 2 * 3600 * 1000L;
				System.err.println("H 2h");
				repaint();
			}
		};
		Action ah4 = new AbstractAction() {

			// @Override
			@Override
			public void actionPerformed(ActionEvent e) {
				historyAgeLimit = 4 * 3600 * 1000L;
				System.err.println("H 4h");
				repaint();
			}
		};

		getInputMap().put(KeyStroke.getKeyStroke("T"), "showtrack");
		getInputMap().put(KeyStroke.getKeyStroke("N"), "normal");
		getInputMap().put(KeyStroke.getKeyStroke("R"), "ragrid");
		getInputMap().put(KeyStroke.getKeyStroke("L"), "nora");
		getInputMap().put(KeyStroke.getKeyStroke("S"), "sky");
		getInputMap().put(KeyStroke.getKeyStroke("D"), "nosky");

		getInputMap().put(KeyStroke.getKeyStroke("1"), "h1");
		getInputMap().put(KeyStroke.getKeyStroke("2"), "h2");
		getInputMap().put(KeyStroke.getKeyStroke("3"), "h30");
		getInputMap().put(KeyStroke.getKeyStroke("4"), "h4");

		getActionMap().put("showtrack", stg);
		getActionMap().put("ragrid", srg);
		getActionMap().put("normal", nstg);
		getActionMap().put("nora", nrtg);
		getActionMap().put("sky", kon);
		getActionMap().put("nosky", koff);

		getActionMap().put("h1", ah1);
		getActionMap().put("h2", ah2);
		getActionMap().put("h30", ah30);
		getActionMap().put("h4", ah4);

		addMouseListener(new MouseAdapter() {
			
			/* (non-Javadoc)
			 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
			 */
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				super.mousePressed(e);
			
				int h = getSize().height;
				int w = getSize().width - 180;
				
				int sx = e.getX();
				int sy = e.getY();
				
				double x = xoff(sx);
				double y = yoff(sy);
				
				double phi = hammerphi(x, y);
				double ll = hammerl(x, y);
				
				System.err.printf("Mouse at: Lat: %4.2f  Long: %4.2f \n", 
						Math.toDegrees(phi), 
						Math.toDegrees(ll));

				searchTarget(phi,ll);
				
				// notify any registered listeners template code dont use rotator listner
				/*Iterator<RotatorPositionSelectionListener> il = listeners.iterator();
				while (il.hasNext()) {
					try {
						RotatorPositionSelectionListener l = il.next();
						l.rotatorSelection(t, mount);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}*/
				
			}
			
		});
		
		sdf.setTimeZone(UTC);
	}

	public void showTracks(ISite site) {
		// showDecGrid = true;
		this.site = site;
		astro = new BasicAstrometrySiteCalculator(site);
		skycalc = new SkyBrightnessCalculator(site);

	}

	@Override
	public Dimension getPreferredSize() {
		return SIZE;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		long t = System.currentTimeMillis();

		if (simulation) {
			t = simulationTime;

			g.setColor(Color.orange);
			g.setFont(simTimeFont);
			g.drawString(sdf.format(t) + "(S)", 10, 10);

		} else {
			g.setColor(Color.green);
			g.setFont(simTimeFont);
			g.drawString(sdf.format(t) + "(N)", 10, 10);
		}

		g.setFont(mainFont);

		// PLOT SKYB OVERLAY
		if (showSkyB) {

			// latitude scan
			double balt = Math.toRadians(0.0);
			while (balt < Math.toRadians(90.0)) {
				// longitude scan
				double bazm = Math.toRadians(-180.0);
				while (bazm < Math.toRadians(180.0)) {

					// centre point
					double cazm = bazm + Math.toRadians(2.5);
					double calt = balt + Math.toRadians(2.5);

					try {
						int is = skycalc.getSkyBrightnessCriterion(calt, cazm, t);
						double s = SkyBrightnessCalculator.getSkyBrightness(is);

						// System.err.println("S " + s);
						if (s > 10)
							s = 11;

						Color ss = getSkyBrightnessColor(s);

						// fill 5 squares in alt and azm from start

						for (int i = 0; i < 5; i++) {
							for (int j = 0; j < 5; j++) {

								double sazm = bazm + i * Math.toRadians(1.0);
								double salt = balt + j * Math.toRadians(1.0);

								double xs = aitoffx(salt, sazm);
								double ys = aitoffy(salt, sazm);

								int sx = screenx(xs);
								int sy = screeny(ys);

								g.setColor(ss);
								g.fillRect(sx, sy, 1, 1);
							}
						}

					} catch (Exception e) {
						System.err.println("E: " + e);
					}
					bazm += Math.toRadians(5.0);
				}
				balt += Math.toRadians(5.0);

			}
		}

		// PLOT ALTAZ GRID OVERLAY
		if (showAltAzGrid) {
			// plot the lat lines
			double phi = Math.toRadians(-90.0);
			while (phi < Math.toRadians(90.0)) {
				// run along the long line
				double l = Math.toRadians(-180.0);
				double x0 = aitoffx(phi, l);
				double y0 = aitoffy(phi, l);
				while (l < Math.toRadians(180.0)) {
					double x = aitoffx(phi, l);
					double y = aitoffy(phi, l);
					if (!Double.isNaN(x) && !Double.isNaN(y)) {
						// draw from old to new position
						plot(g, x0, y0, x, y, Color.gray);
						x0 = x;
						y0 = y;
					}
					l += Math.toRadians(1.0);
				}

				phi += Math.toRadians(22.5);
			}

			// plot the longitude lines
			double l = Math.toRadians(-180.0);
			while (l < Math.toRadians(180.0)) {

				// run along the lat line
				phi = Math.toRadians(-90.0);
				double x0 = aitoffx(phi, l);
				double y0 = aitoffy(phi, l);
				while (phi < Math.toRadians(90.0)) {
					double x = aitoffx(phi, l);
					double y = aitoffy(phi, l);
					if (!Double.isNaN(x) && !Double.isNaN(y)) {
						// draw from old to new position
						plot(g, x0, y0, x, y, Color.gray);
						x0 = x;
						y0 = y;
					}
					phi += Math.toRadians(1.0);
				}

				l += Math.toRadians(30.0);
			}
		}

		// PLOT AIRMASS GRID OVERLAY
		if (showAirmassGrid) {
			// plot the lat lines
			double air = 2.5;
			double phi = 0.0;
			while (air >= 1.0) {
				phi = 0.5 * Math.PI - Math.acos(1 / air);
				// run along the long line
				double l = Math.toRadians(-180.0);
				double x0 = aitoffx(phi, l);
				double y0 = aitoffy(phi, l);
				while (l < Math.toRadians(180.0)) {
					double x = aitoffx(phi, l);
					double y = aitoffy(phi, l);
					if (!Double.isNaN(x) && !Double.isNaN(y)) {
						// draw from old to new position
						plot(g, x0, y0, x, y, Color.gray);
						x0 = x;
						y0 = y;
					}
					l += Math.toRadians(1.0);
				}

				air -= 0.25; // dec airmass by 0.25
			}

			// plot the longitude lines
			double l = Math.toRadians(-180.0);
			while (l < Math.toRadians(180.0)) {

				// run along the lat line
				phi = Math.toRadians(-90.0);
				double x0 = aitoffx(phi, l);
				double y0 = aitoffy(phi, l);
				while (phi < Math.toRadians(90.0)) {
					double x = aitoffx(phi, l);
					double y = aitoffy(phi, l);
					if (!Double.isNaN(x) && !Double.isNaN(y)) {
						// draw from old to new position
						plot(g, x0, y0, x, y, Color.gray);
						x0 = x;
						y0 = y;
					}
					phi += Math.toRadians(1.0);
				}

				l += Math.toRadians(30.0);
			}
		}

		// PLOT DEC GRID OVERLAY
		if (showDecGrid) {
			double ra = 0.0;
			double dec = -Math.toRadians(90.0) + site.getLatitude();
			while (dec < Math.toRadians(90.0)) {

				// this is not a real target its used to generate grid lines
				XExtraSolarTarget target = new XExtraSolarTarget("dec-grid");
				target.setRa(ra);
				target.setDec(dec);
				TargetTrackCalculator ttc = new BasicTargetCalculator(target, site);

				long now = t;
				long time = now;
				while (time < now + 24 * 3600 * 1000L) {
					try {
						Coordinates c = ttc.getCoordinates(time);

						double alt = astro.getAltitude(c, time);
						double azm = astro.getAzimuth(c, time);

						if (alt >= 0.0) {
							Coordinate ac = new Coordinate(0L, alt, azm, Color.blue);
							plotCoordinate(g, ac, Color.orange, 1);
						}
					} catch (Exception e) {
					}
					time += 2 * 60 * 1000L; // 5 mins
				}
				dec += Math.toRadians(15.0);
			}
		}

		// PLOT RA GRID OVERLAY
		if (showRaGrid) {

			double ra = 0.0;
			while (ra < Math.toRadians(360.0)) {

				long now = t;
				long time = now;
				double dec = -Math.toRadians(90.0) + site.getLatitude();
				while (dec < Math.toRadians(90.0)) {
					try {
						// this is not a real target its used to generate grid
						// lines
						XExtraSolarTarget target = new XExtraSolarTarget("ra-grid");
						target.setRa(ra);
						target.setDec(dec);
						TargetTrackCalculator ttc = new BasicTargetCalculator(target, site);

						Coordinates c = ttc.getCoordinates(time);
						// System.err.println("ragridat:" + ra + "," + dec);
						double alt = astro.getAltitude(c, time);
						double azm = astro.getAzimuth(c, time);

						if (alt >= 0.0) {
							Coordinate ac = new Coordinate(0L, alt, azm, Color.blue);
							plotCoordinate(g, ac, Color.pink, 1);
						}
					} catch (Exception e) {
						System.err.println("err" + e);
					}
					dec += Math.toRadians(1.0);
				}
				ra += Math.toRadians(20.0);
			}
		}

		// plot the coordinates of any points added
		Coordinate c0 = null;
		for (int ic = 0; ic < coordinates.size(); ic++) {
			Coordinate c = coordinates.get(ic);

			long age = System.currentTimeMillis() - c.getTime();
			// only plot points which are less old than max history age
			if (age <= historyAgeLimit) {
				Color color = Color.green;
				if (historyColorAging) {
					// Colors run from green (latest) to blue (oldest) at limit
					color = getGradientColor(age, historyAgeLimit);
				}
				plotCoordinate(g, c, color, 5);
			}
		}

		if (coordinates.size() > 0) {
			// replot the last coordinate
			Coordinate c = coordinates.get(coordinates.size() - 1);
			plotCoordinate(g, c, Color.red, 6);
			// TODO use the last point symbol and last point color
			// plotSymbol(g,c,CROSS_SYMBOL, LAST_POINT_COLOR);

			g.setColor(Color.magenta);
			g.drawString(String.format("Azm: %4.2f\n", Math.toDegrees(c.longitude)), 50, getSize().height - 50);
			g.drawString(String.format("Alt: %4.2f\n", Math.toDegrees(c.latitude)), 50, getSize().height - 20);

		}

		// Always plot the moon position
		try {
			LunarCalculator lc = new LunarCalculator(site);
			Coordinates moon = lc.getCoordinates(t);

			double malt = astro.getAltitude(moon, t);
			double mazm = astro.getAzimuth(moon, t);

			double xs = aitoffx(malt, mazm);
			double ys = aitoffy(malt, mazm);
			int sx = screenx(xs);
			int sy = screeny(ys);
			g.setColor(Color.ORANGE);
			if (malt < 0.0)
				g.setColor(Color.ORANGE.darker());
			g.fillOval(sx - 2, sy - 2, 4, 4);
			g.drawString("Moon", sx + 5, sy - 5);
			System.err.printf("Moon at: AZM: %4.2f, ALT: %4.2f \n", Math.toDegrees(mazm), Math.toDegrees(malt));
		} catch (Exception e) {
		}

		// Always plot the sun position
		try {
			SolarCalculator sc = new SolarCalculator();
			Coordinates sun = sc.getCoordinates(t);

			double salt = astro.getAltitude(sun, t);
			double sazm = astro.getAzimuth(sun, t);

			double xs = aitoffx(salt, sazm);
			double ys = aitoffy(salt, sazm);
			int sx = screenx(xs);
			int sy = screeny(ys);
			g.setColor(Color.ORANGE);
			if (salt < 0.0)
				g.setColor(Color.ORANGE.darker());
			g.fillOval(sx - 2, sy - 2, 4, 4);
			g.drawString("Sun", sx + 5, sy - 5);

		} catch (Exception e) {
		}

		// NEW PLOT TARGET_SETS
		List<CatalogDisplay> catList = cdm.listCatalogDisplays();
		for (int ic = 0; ic < catList.size(); ic++) {

			CatalogDisplay display = catList.get(ic);
			AstroCatalog cat = display.getCatalog();
			CatalogDisplayDescriptor descriptor = display.getDisplayDescriptor();

			// obtain plotting information
			int symbol = descriptor.getSymbol();
			Color scolor = descriptor.getColor();
			boolean showLabel = descriptor.isShowLabel();
			boolean showSymbol = descriptor.isShowSymbol();

			// for each target in cat
			int istart = 0;
			List<XExtraSolarTarget> targets = cat.listTargets();
			System.err.println("AP::ShowCatalog: "+ cat.getCatalogName()+" contains "+cat.size()+" targets");
			// are we displaying all targets in cat ?
			if (!descriptor.isShowAll())
				istart = Math.max(0, targets.size() - descriptor.getShowCount());
			System.err.println("AP::ShowCatalog: Show last: "+descriptor.getShowCount()+" Start at: "+istart+" upto: "+(targets.size()-1));
			
			for (int it = istart; it < targets.size(); it++) {
				XExtraSolarTarget target = targets.get(it);
				System.err.println("AP::ShowCatalog: Show Target: "+it+" -> "+target.getName());
				try {
					displayTarget(g, descriptor, target, t);
				} catch (Exception e) {
					System.err.println("AP::ShowCatalog: Error: "+e);
					e.printStackTrace();
				}
			}

		}

	}

	private Color getSkyBrightnessColor(double skyb) {

		if (skyb <= 0.0)
			return DARK_COLOR;
		else if (skyb <= 0.75)
			return MAG_0p75_COLOR;
		else if (skyb <= 1.5)
			return MAG_1p5_COLOR;
		else if (skyb <= 2.0)
			return MAG_2_COLOR;
		else if (skyb <= 4.0)
			return MAG_4_COLOR;
		else if (skyb <= 6.0)
			return MAG_6_COLOR;
		else if (skyb <= 10.0)
			return MAG_10_COLOR;
		return DAY_COLOR;

	}

	/**
	 * Display a target on the plot.
	 * 
	 * @param target
	 *            The target to display
	 */
	private void displayTarget(Graphics g, CatalogDisplayDescriptor descriptor, ITarget target, long t)
			throws Exception {
		long time = t;
		TargetTrackCalculator ttc = new BasicTargetCalculator(target, site);

		Coordinates c = ttc.getCoordinates(time);
		double alt = astro.getAltitude(c, time);
		double azm = astro.getAzimuth(c, time);

		double xs = aitoffx(alt, azm);
		double ys = aitoffy(alt, azm);
		int sx = screenx(xs);
		int sy = screeny(ys);

		boolean set = (alt < 0.0);

		// Paint the symbol and label
		descriptor.paintComponent(g, target.getName(), set, sx, sy);
		// g.drawString(target.getName(), sx + 5, sy - 5);

	}

	private void plotString(Graphics g, String string, Coordinate c, Color magenta) {
		double xs = aitoffx(c.latitude, c.longitude);
		double ys = aitoffy(c.latitude, c.longitude);
		int sx = screenx(xs);
		int sy = screeny(ys);
		g.setColor(Color.MAGENTA);

	}

	/**
	 * @return the simulationTime
	 */
	public long getSimulationTime() {
		return simulationTime;
	}

	/**
	 * @param simulationTime
	 *            the simulationTime to set
	 */
	public void setSimulationTime(long simulationTime) {
		this.simulationTime = simulationTime;
	}

	@Override
	public void simulationTimeUpdated(long simulationTime) {
		this.simulationTime = simulationTime;
	}

	@Override
	public void simulationRunning(boolean run) {
		this.simulation = run;
	}

	public void setMainFont(Font mainFont) {
		this.mainFont = mainFont;
	}

	public void setSimTimeFont(Font simTimeFont) {
		this.simTimeFont = simTimeFont;
	}

	/**
	 * @return the historyDuration
	 */
	public long getHistoryDuration() {
		return historyAgeLimit;
	}

	/**
	 * @param historyDuration
	 *            the historyDuration to set
	 */
	public void setHistoryDuration(long historyDuration) {
		this.historyAgeLimit = historyDuration;
	}

	/**
	 * @return the historyColorAging
	 */
	public boolean isHistoryColorAging() {
		return historyColorAging;
	}

	/**
	 * @param historyColorAging
	 *            the historyColorAging to set
	 */
	public void setHistoryColorAging(boolean historyColorAging) {
		this.historyColorAging = historyColorAging;
	}

	/**
	 * @return the showAltAzGrid
	 */
	public boolean isShowAltAzGrid() {
		return showAltAzGrid;
	}

	/**
	 * @param showAltAzGrid
	 *            the showAltAzGrid to set
	 */
	public void setShowAltAzGrid(boolean showAltAzGrid) {
		this.showAltAzGrid = showAltAzGrid;
	}

	/**
	 * @return the showGrid
	 */
	public boolean isShowDecGrid() {
		return showDecGrid;
	}

	/**
	 * @param showGrid
	 *            the showGrid to set
	 */
	public void setShowDecGrid(boolean showDecGrid) {
		this.showDecGrid = showDecGrid;
		repaint();
	}

	/**
	 * @return the showAirmassGrid
	 */
	public boolean isShowAirmassGrid() {
		return showAirmassGrid;
	}

	/**
	 * @param showAirmassGrid
	 *            the showAirmassGrid to set
	 */
	public void setShowAirmassGrid(boolean showAirmassGrid) {
		this.showAirmassGrid = showAirmassGrid;
	}

	
	
	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
		repaint();
	}

	/**
	 * @return the simulation
	 */
	public boolean isSimulation() {
		return simulation;
	}

	/**
	 * @param simulation
	 *            the simulation to set
	 */
	public void setSimulation(boolean simulation) {
		this.simulation = simulation;
		System.err.println("Aitoff.setSim:" + simulation);
	}

	/**
	 * @return the showRaGrid
	 */
	public boolean isShowRaGrid() {
		return showRaGrid;
	}

	/**
	 * @param showRaGrid
	 *            the showRaGrid to set
	 */
	public void setShowRaGrid(boolean showRaGrid) {
		this.showRaGrid = showRaGrid;
		System.err.println("Set show ra grid (" + showRaGrid + " -> " + this.showRaGrid);
		repaint();
	}

	/**
	 * @return the showSkyB
	 */
	public boolean isShowSkyB() {
		return showSkyB;
	}

	/**
	 * @param showSkyB
	 *            the showSkyB to set
	 */
	public void setShowSkyB(boolean showSkyB) {
		this.showSkyB = showSkyB;
		repaint();
	}

	/**
	 * @return the cdm
	 */
	public CatalogDisplayManager getCatalogDisplayManager() {
		return cdm;
	}

	/**
	 * Add a coordinate
	 * 
	 * @param c
	 */
	public void addCoordinate(long time, double latitude, double longitude, Color color, boolean paint) {
		System.err.println("Add coord: total list size " + coordinates.size());
		coordinates.add(new Coordinate(time, latitude, longitude, color));
		if (paint)
			repaint();
	}

	/**
	 * Add a coordinate
	 * 
	 * @param c
	 */
	public void addCoordinate(Coordinate c) {
		addCoordinate(c.getTime(), c.getLatitude(), c.getLongitude(), c.getColor(), true);
	}

	/**
	 * Convenience method to add a coordinate.
	 * 
	 * @param latitude
	 * @param longitude
	 */
	public void addCoordinate(long time, double latitude, double longitude, Color color) {
		addCoordinate(new Coordinate(time, latitude, longitude, color));
	}

	/** Screen X coordinate of projection coordinate X. */
	private int screenx(double x) {
		double w = (getSize().width);
		double sx = 0.5 * w * (1.0 + x / Math.PI);
		return (int) sx;
	}

	/** Screen Y coordinate of projection coordinate Y. */
	private int screeny(double y) {
		double h = (getSize().height);
		double sy = 0.5 * h * (1.0 - 2.0 * y / Math.PI);
		return (int) sy;
	}

	/** X coordinate of screen X. */
	private double xoff(int sx) {
		double w = (getSize().width);
		return (2.0 * sx / w - 1.0) * Math.PI;
	}

	/** Y coordinate of screen Y. */
	private double yoff(int sy) {
		double h = (getSize().height);
		return 0.5 * (1.0 - 2.0 * sy / h) * Math.PI;
	}

	private void plotCoordinate(Graphics g, Coordinate c, Color color, int size) {
		g.setColor(color);
		double x = aitoffx(c.latitude, c.longitude);
		double y = aitoffy(c.latitude, c.longitude);
		int sx = screenx(x);
		int sy = screeny(y);
		g.fillOval(sx - size / 2, sy - size / 2, size, size);
	}

	private void plot(Graphics g, double x0, double y0, double x, double y, Color color) {

		g.setColor(color);
		int sx0 = screenx(x0);
		int sy0 = screeny(y0);
		int sx = screenx(x);
		int sy = screeny(y);
		g.drawLine(sx0, sy0, sx, sy);

	}

	/** Calculate hammer-aitoff X projection for lat/long position (phi, l). */
	double aitoffx(double phi, double l) {
		double ll = l;
		while (ll > Math.PI)
			ll -= 2.0 * Math.PI;
		while (ll < -Math.PI)
			ll += 2.0 * Math.PI;
		double cp = Math.cos(phi);
		double w = Math.sqrt(1.0 + cp * Math.cos(ll / 2.0));
		return 2.0 * Math.sqrt(2.0) * cp * Math.sin(ll / 2.0) / w;
	}

	/** Calculate hammer-aitoff Y projection for lat/long position (phi, l). */
	double aitoffy(double phi, double l) {
		double ll = l;
		while (ll > Math.PI)
			ll -= 2.0 * Math.PI;
		while (ll < -Math.PI)
			ll += 2.0 * Math.PI;
		double cp = Math.cos(phi);
		double w = Math.sqrt(1.0 + cp * Math.cos(ll / 2.0));
		return Math.sqrt(2.0) * Math.sin(phi) / w;
	}

	/** Reverse hammer-aitoff latitude given screen position (x,y). */
	double hammerl(double x, double y) {
		double z = Math.sqrt(1.0 - x * x / 16.0 - y * y / 4.0);
		return 2.0 * Math.atan2(z * x, 2.0 * (2.0 * z * z - 1.0));
	}

	/** Reverse hammer-aitoff longitude given screen position (x,y). */
	double hammerphi(double x, double y) {
		double z = Math.sqrt(1.0 - x * x / 16.0 - y * y / 4.0);
		return Math.asin(y * z);
	}

	/*
	 * double aitoffx(double phi, double l) { double ll = l; while (ll >
	 * Math.PI) ll -= 2.0 * Math.PI; while (ll < -Math.PI) ll += 2.0 * Math.PI;
	 * double a = Math.acos(Math.cos(phi) * Math.cos(ll / 2.0)); double sa =
	 * Math.sin(a) / a; double x = (2.0 * Math.cos(phi) * Math.sin(ll / 2.0)) /
	 * sa; return x; }
	 * 
	 * double aitoffy(double phi, double l) { double ll = l; while (ll >
	 * Math.PI) ll -= 2.0 * Math.PI; while (ll < -Math.PI) ll += 2.0 * Math.PI;
	 * double a = Math.acos(Math.cos(phi) * Math.cos(ll / 2.0)); double sa =
	 * Math.sin(a) / a; double y = Math.sin(phi) / sa; return y; }
	 */

	private Color getGradientColor(long age, long limit) {

		// which quadrant are we in (0-3)
		int iquad = (int) Math.floor(4.0 * age / limit);
		Color c1 = null;
		Color c2 = null;
		switch (iquad) {
		case 0:
			c1 = HISTORY_COLOR_0;
			c2 = HISTORY_COLOR_1;
			break;
		case 1:
			c1 = HISTORY_COLOR_1;
			c2 = HISTORY_COLOR_2;
			break;
		case 2:
			c1 = HISTORY_COLOR_2;
			c2 = HISTORY_COLOR_3;
			break;
		case 3:
			c1 = HISTORY_COLOR_3;
			c2 = HISTORY_COLOR_4;
			break;
		default:
			c1 = HISTORY_COLOR_3;
			c2 = HISTORY_COLOR_4;
		}

		int ro = c1.getRed();
		// set the search location

		int go = c1.getGreen();
		int bo = c1.getBlue();

		int rn = c2.getRed();
		int gn = c2.getGreen();
		int bn = c2.getBlue();

		double mr = (double) (ro - rn) / (double) historyAgeLimit;
		double mg = (double) (go - gn) / (double) historyAgeLimit;
		double mb = (double) (bo - bn) / (double) historyAgeLimit;

		double r = mr * age + rn;
		double g = mg * age + gn;
		double b = mb * age + bn;

		return new Color((int) r, (int) g, (int) b);

	}

	private void searchTarget(double alt, double azm) {
		// find a target near (alt,azm)
		long time = System.currentTimeMillis();

		double mindist = 999.9;
		XExtraSolarTarget closestTarget = null;
		
		List<CatalogDisplay> catList = cdm.listCatalogDisplays();
		for (int ic = 0; ic < catList.size(); ic++) {

			CatalogDisplay display = catList.get(ic);
			AstroCatalog cat = display.getCatalog();

			// for each target in cat
			int istart = 0;
			List<XExtraSolarTarget> targets = cat.listTargets();
			for (int it = istart; it < targets.size(); it++) {
				XExtraSolarTarget target = targets.get(it);

				TargetTrackCalculator ttc = new BasicTargetCalculator(target, site);

				try {
					Coordinates c = ttc.getCoordinates(time);
					double talt = astro.getAltitude(c, time);
					double tazm = astro.getAzimuth(c, time);
					
					Coordinates skyPos = new Coordinates(azm, alt);
					Coordinates tgtPos = new Coordinates(tazm, talt);
					
					double distance = astro.getAngularSeperation(skyPos, tgtPos);
					if (distance < mindist) {
						mindist = distance;
						closestTarget = target;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		System.err.printf("Closest target %20.20s at %4.2f degs \n", 
				(closestTarget != null ? closestTarget.getName():"none"),
				Math.toDegrees(mindist));
	}

	private class Coordinate {

		private long time;

		private double latitude;

		private double longitude;

		private Color color;

		/**
		 * @param latitude
		 * @param longitude
		 */
		public Coordinate(long time, double latitude, double longitude, Color color) {
			this.time = time;
			this.latitude = latitude;
			this.longitude = longitude;
			this.color = color;
		}

		/**
		 * @return the time
		 */
		public long getTime() {
			return time;
		}

		/**
		 * @param time
		 *            the time to set
		 */
		public void setTime(long time) {
			this.time = time;
		}

		/**
		 * @return the latitude
		 */
		public double getLatitude() {
			return latitude;
		}

		/**
		 * @param latitude
		 *            the latitude to set
		 */
		public void setLatitude(double latitude) {
			this.latitude = latitude;
		}

		/**
		 * @return the longitude
		 */
		public double getLongitude() {
			return longitude;
		}

		/**
		 * @param longitude
		 *            the longitude to set
		 */
		public void setLongitude(double longitude) {
			this.longitude = longitude;
		}

		/**
		 * @return the color
		 */
		public Color getColor() {
			return color;
		}

		/**
		 * @param color
		 *            the color to set
		 */
		public void setColor(Color color) {
			this.color = color;
		}

		@Override
		public String toString() {
			return "C[" + time + " " + color.getRGB() + ";" + Math.toDegrees(latitude) + ","
					+ Math.toDegrees(longitude) + "]";
		}

	}

}
