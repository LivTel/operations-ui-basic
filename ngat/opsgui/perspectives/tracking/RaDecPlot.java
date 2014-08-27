/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.SimpleTimeZone;
import java.util.Vector;

import javax.swing.JPanel;

import ngat.astrometry.AstroFormatter;
import ngat.astrometry.Coordinates;
import ngat.astrometry.ISite;
import ngat.astrometry.test.AltAz2RaDec;

/**
 * Plot for RA dec positions.
 * 
 * @author eng
 * 
 */
public class RaDecPlot extends JPanel {

	public static final Font SIM_TIME_FONT = new Font("helvetica", Font.ITALIC, 10);

	private static final Color DEC_GRID_COLOR = Color.orange.darker();
	private static final Color AZM_GRID_COLOR = Color.pink.darker();
	private static final Color ALT_GRID_COLOR = Color.green.darker();

	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public static SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

    private AltAz2RaDec calc;

	private boolean showRaGrid = true;

	private boolean showDecGrid = true;

	private boolean showAltazGrid = false;

	private double raGridSpacing = Math.toRadians(22.5);

	private double decGridSpacing = Math.toRadians(22.5);

	private ISite site;

	private List<Coordinate> coordinates;

	/** True if a simulation is running. */
	private boolean simulation = false;

	/** Simulation time. */
	private long simulationTime;

	/**
	 * 
	 */
	public RaDecPlot(ISite site) {
		super(true);
		this.site = site;

		coordinates = new Vector<RaDecPlot.Coordinate>();
		setBackground(Color.blue.darker().darker());

		calc = new AltAz2RaDec(site.getLatitude(), site.getLongitude());
		sdf.setTimeZone(UTC);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);

		long t = System.currentTimeMillis();

		if (simulation) {
			t = simulationTime;

			g.setColor(Color.orange);
			g.setFont(SIM_TIME_FONT);
			g.drawString(sdf.format(t) + "(S)", 10, 10);

		} else {
			g.setColor(Color.green);
			g.setFont(SIM_TIME_FONT);
			g.drawString(sdf.format(t) + "(N)", 10, 10);
		}

		if (showDecGrid) {
			g.setColor(DEC_GRID_COLOR);

			// plot dec circles start at dec -20 ish ???
			double dec0 = 0.0; // TODO start dec
			double dec = 0.0;
			while (dec < 0.5 * Math.PI) {
				int x0 = screenX(0.0, 0.0);
				int y0 = screenY(0.0, 0.0);
				double ra = 0.0;
				while (ra < 2.0 * Math.PI) {
					int x1 = screenX(ra, dec);
					int y1 = screenY(ra, dec);
					g.drawLine(x0, y0, x1, y1);
					x0 = x1;
					y0 = y1;
					ra += Math.toRadians(1.0);
				}
				dec += decGridSpacing;
			}

		}

		if (showRaGrid) {
			// plot ra lines
			double ra = 0.0;
			while (ra < 2.0 * Math.PI) {
				int x0 = screenX(0.0, 0.5 * Math.PI);
				int y0 = screenY(0.0, 0.5 * Math.PI);
				int x1 = screenX(ra, 0.0);
				int y1 = screenY(ra, 0.0);
				g.drawLine(x0, y0, x1, y1);
				ra += raGridSpacing;
			}
		}

		if (showAltazGrid) {

			try {
				AltAz2RaDec calc = new AltAz2RaDec(site.getLatitude(), site.getLongitude());
				g.setColor(ALT_GRID_COLOR);
				// plot alt grid overlay
				double alt = 0.0;
				while (alt < 0.5 * Math.PI) {

					double azm = 0.0;
					Coordinates c0;
					c0 = calc.compute(t, alt, azm);

					double ra0 = c0.getRa();
					double dec0 = c0.getDec();

					int x0 = screenX(ra0, dec0);
					int y0 = screenY(ra0, dec0);
					// if (dec0 > 0.0)
					g.drawString(String.format("%6.2f", Math.toDegrees(alt)), x0, y0);
					System.err.println("Plot alt: " + Math.toDegrees(alt) + " at " + x0 + " " + y0);
					while (azm < 2.0 * Math.PI) {

						Coordinates c = calc.compute(t, alt, azm);
						double ra = c.getRa();
						double dec = c.getDec();

						int x1 = screenX(ra, dec);
						int y1 = screenY(ra, dec);

						// g.drawLine(x0, y0, x1, y1);
						if (dec > 0.0)
							g.fillOval(x1, y1, 2, 2);
						azm += Math.toRadians(1.0);
					}
					alt += Math.toRadians(10.0);// altGridSpacing;
				}
				// plot az grid overlay
				g.setColor(AZM_GRID_COLOR);
				double azma = 0.0;
				while (azma < 2.0*Math.PI) {
					
					double alta = 0.0;
					Coordinates c0;
					c0 = calc.compute(t, alta, azma);

					double ra0 = c0.getRa();
					double dec0 = c0.getDec();

					int x0 = screenX(ra0, dec0);
					int y0 = screenY(ra0, dec0);
					if (dec0 > 0.0)
					g.drawString(String.format("%6.2f", Math.toDegrees(azma)), x0, y0);
			
					while (alta < 0.5*Math.PI) {
						Coordinates c = calc.compute(t, alta, azma);
						double ra = c.getRa();
						double dec = c.getDec();

						int x1 = screenX(ra, dec);
						int y1 = screenY(ra, dec);

						// g.drawLine(x0, y0, x1, y1);
						if (dec > 0.0)
							g.fillOval(x1, y1, 2, 2);
						alta += Math.toRadians(2.0);
					}				
					azma += Math.toRadians(22.5);// azmGridSpacing;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// show coords.
		// plot the coordinates of any points added
                for (int ic = 0; ic < coordinates.size(); ic++) {
		    Coordinate c = coordinates.get(ic);
		    plotCoordinate(g, c, Color.green);
		    if (ic == 0) {
			//c0 = c;
		    } else {
			//c0 = c;
			// just the actual points
			plotCoordinate(g, c, Color.orange);
		    }
                }

		if (coordinates.size() > 0) {
		    // replot the last coordinate
		    Coordinate c = coordinates.get(coordinates.size() - 1);
		    plotCoordinate(g, c, Color.red);

		    g.setColor(Color.magenta);
		    g.drawString(String.format("RA: %12s", 
					       AstroFormatter.formatHMS(c.longitude, ":")), 50, getSize().height - 50);
		    g.drawString(String.format("Dec: %12s\n", 
					       AstroFormatter.formatDMS(c.latitude, ":")), 50, getSize().height - 20);

                }



	}

    private void plotCoordinate(Graphics g, Coordinate c, Color color) {
	g.setColor(color);
	int sx = screenX(c.longitude, c.latitude);
	int sy = screenY(c.longitude, c.latitude);
	g.fillOval(sx - 4 / 2, sy - 4 / 2, 4, 4);
    }

   

    /**
     * Add a coordinate
     *
     * @param c
     */
    public void addCoordinate(double azm, double alt, long time) {
	// convert to radec
	double ra  = skyRa(azm, alt, time);
	double dec = skyDec(azm, alt, time);
	// store a radec (lat,long = dec,ra)
	coordinates.add(new Coordinate(dec, ra, Color.green));
	repaint();
    }



	/**
	 * Calculate screen X coordinate of a given ra,dec location.
	 * 
	 * @param ra
	 *            The ra of location
	 * @param dec
	 *            The dec of location.
	 * @return Screen X for that location.
	 */
	private int screenX(double ra, double dec) {
		int ww = getSize().width;
		int hh = getSize().height;
		//double rr = (0.5 * Math.PI - dec) * Math.min(ww, hh) / Math.PI;
		double rr = 0.5*Math.min(ww, hh)*Math.cos(dec);
		double xp = rr * Math.sin(ra);
		return ww / 2 + (int) xp;

	}

	/**
	 * Calculate screen Y coordinate of a given ra,dec location.
	 * 
	 * @param ra
	 *            The ra of location
	 * @param dec
	 *            The dec of location.
	 * @return Screen Y for that location.
	 */
	private int screenY(double ra, double dec) {
		int ww = getSize().width;
		int hh = getSize().height;
		//double rr = (0.5 * Math.PI - dec) * Math.min(ww, hh) / Math.PI;
		double rr = 0.5*Math.min(ww, hh)*Math.cos(dec);
		double yp = rr * Math.cos(ra);
		return hh / 2 - (int) yp;
	}

	/**
	 * Calculate RA for a given alt/az at time.
	 * 
	 * @param az
	 *            The azimuth of the location.
	 * @param alt
	 *            The altitude of the location.
	 * @param time
	 *            The required time.
	 * @return RA for the given location at specified time.
	 */
	private double skyRa(double azm, double alt, long time) {
	    try {
		Coordinates c = calc.compute(time, alt, azm);
		double ra = c.getRa();
		return ra;
	    } catch (Exception e) {
		return 0.0;
	    }
	}

	/**
	 * Calculate Dec for a given alt/az at time.
	 * 
	 * @param az
	 *            The azimuth of the location.
	 * @param alt
	 *            The altitude of the location.
	 * @param time
	 *            The required time.
	 * @return Dec for the given location at specified time.
	 */
	private double skyDec(double azm, double alt, long time) {
	    try {
	    Coordinates c = calc.compute(time, alt, azm);
	    double dec = c.getDec();
	    return dec;
	    } catch (Exception e) {
		return 0.0;
	    }
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
	}

	/**
	 * @return the showDecGrid
	 */
	public boolean isShowDecGrid() {
		return showDecGrid;
	}

	/**
	 * @param showDecGrid
	 *            the showDecGrid to set
	 */
	public void setShowDecGrid(boolean showDecGrid) {
		this.showDecGrid = showDecGrid;
	}

	/**
	 * @return the showAltazGrid
	 */
	public boolean isShowAltazGrid() {
		return showAltazGrid;
	}

	/**
	 * @param showAltazGrid
	 *            the showAltazGrid to set
	 */
	public void setShowAltazGrid(boolean showAltazGrid) {
		this.showAltazGrid = showAltazGrid;
	}

	/**
	 * @return the raGridSpacing
	 */
	public double getRaGridSpacing() {
		return raGridSpacing;
	}

	/**
	 * @param raGridSpacing
	 *            the raGridSpacing to set
	 */
	public void setRaGridSpacing(double raGridSpacing) {
		this.raGridSpacing = raGridSpacing;
	}

	/**
	 * @return the decGridSpacing
	 */
	public double getDecGridSpacing() {
		return decGridSpacing;
	}

	/**
	 * @param decGridSpacing
	 *            the decGridSpacing to set
	 */
	public void setDecGridSpacing(double decGridSpacing) {
		this.decGridSpacing = decGridSpacing;
	}

	/**
	 * @return the site
	 */
	public ISite getSite() {
		return site;
	}

	/**
	 * @param site
	 *            the site to set
	 */
	public void setSite(ISite site) {
		this.site = site;
	}

	private class Coordinate {

		public double latitude;

		public double longitude;

		public Color color;

		/**
		 * @param latitude
		 * @param longitude
		 * @param color
		 */
		public Coordinate(double latitude, double longitude, Color color) {
			super();
			this.latitude = latitude;
			this.longitude = longitude;
			this.color = color;
		}

	}

}
