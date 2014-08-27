/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.TimeSlicePanel;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * Create an operations time-slice plot.
 * 
 * @author eng
 * 
 */
public class OperationsTimeSlicer {

	public static final Color DEFAULT_COLOR = Color.gray;
	public static final Color DAYTIME_COLOR = Color.orange;
	public static final Color BAD_WEATHER_COLOR = Color.red;
	public static final Color SYSTEM_SUSPEND_COLOR = Color.red.darker();
	public static final Color SYSTEM_STANDBY_COLOR = Color.yellow.darker();
	public static final Color SYSTEM_FAIL_COLOR = Color.red;

	public static final Color OPS_SOCA_COLOR = Color.yellow.darker();
	public static final Color OPS_BGCA_COLOR = Color.cyan;
	public static final Color OPS_TOCA_COLOR = Color.pink;
	public static final Color OPS_CAL_COLOR = Color.pink.darker();
	public static final Color OPS_X_COLOR = Color.pink.darker();
	public static final Color ENG_COLOR = Color.blue;

	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	private static final SimpleDateFormat ddf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssZ");
	private static final SimpleDateFormat udf = new SimpleDateFormat("yyyy-MM-dd");
	private static final SimpleDateFormat mdf = new SimpleDateFormat("EE dd/MM");

	private static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	private ConfigurationProperties config;

	/**
	 * @param config
	 */
	public OperationsTimeSlicer(ConfigurationProperties config) throws Exception {
		this.config = config;

		sdf.setTimeZone(UTC);
		ddf.setTimeZone(UTC);
		udf.setTimeZone(UTC);
		mdf.setTimeZone(UTC);

		boolean display = (config.getProperty("display") != null);
		boolean write = (config.getProperty("write") != null);

		String title = config.getProperty("title", "Operations Summary");

		long start = sdf.parse(config.getProperty("start")).getTime();

		int hs = config.getIntValue("hs", 17);
		int he = config.getIntValue("he", 8);

		int ndays = config.getIntValue("nd", 7);
		long end = start + ndays * 86400 * 1000L;

		TimeSlicePanel tsp = new TimeSlicePanel(title, start, hs, he, ndays);

		if (ndays == 1)
			tsp.setPreferredSize(new Dimension(500, 100));
		else
			tsp.setPreferredSize(new Dimension(500, 800));

		StateColorMap map = new StateColorMap(Color.gray, "UNKNOWN");

		boolean gotColors = false;
		String colorMapFileName = config.getProperty("colors");
		if (colorMapFileName != null) {
			try {
				Properties colors = new Properties();
				colors.load(new FileReader(colorMapFileName));

				setColor(1, map, colors, "DAY");
				setColor(2, map, colors,"WEATHER");
				setColor(3, map, colors,"ENGINEER");
				setColor(4, map, colors,"SUSPEND");
				setColor(5, map, colors,"SOCA");
				setColor(6, map, colors,"BGCA");
				setColor(7, map, colors,"CAL");
				setColor(8, map, colors,"TOCA");
				setColor(9, map, colors,"TRANSIENT");
				
				System.err.println("Using map: "+colors);

				gotColors = true;
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}

		if (!gotColors) {
			map.addColorLabel(1, DAYTIME_COLOR, "DAY");
			map.addColorLabel(2, BAD_WEATHER_COLOR, "WEATHER");
			map.addColorLabel(3, ENG_COLOR, "ENGINEER");
			map.addColorLabel(4, SYSTEM_SUSPEND_COLOR, "SUSPEND");
			map.addColorLabel(5, OPS_SOCA_COLOR, "SOCA");
			map.addColorLabel(6, OPS_BGCA_COLOR, "BGCA");
			map.addColorLabel(7, OPS_CAL_COLOR, "CAL");
			map.addColorLabel(8, OPS_TOCA_COLOR, "TOCA");
			map.addColorLabel(9, Color.green.brighter(), "TRANSIENT");
		}

		tsp.setMap(map);

		File inputFile = new File(config.getProperty("file"));
		String outfileName = config.getProperty("output", "/home/eng/public_html/weekly_ops.png");

		BufferedReader in = new BufferedReader(new FileReader(inputFile));

		int ntl = 0;
		int nl = 0;
		String line = null;
		while ((line = in.readLine()) != null) {
			ntl++;
			// 2012-11-2107:36:03UTC 3 16 9 19 Idle 2 WEATHER
			StringTokenizer st = new StringTokenizer(line);
			String strtime = st.nextToken();
			long time = ddf.parse(strtime).getTime();

			if (time >= start && time <= end) {
				nl++;
				st.nextToken();//
				st.nextToken();//
				st.nextToken();//
				st.nextToken();//
				st.nextToken();// rcs state
				int state = 0;
				try {
					state = Integer.parseInt(st.nextToken());
				} catch (Exception ee) {
					ee.printStackTrace();
				}
				// System.err.println("Add item: "+nl+" : "+line);
				tsp.addHistory(time, state);
			}
		}
		System.err.println("Done: checked " + ntl + ", used " + nl);

		// display the slice panel

		if (display) {
			JFrame f = new JFrame("Operations State: (" + mdf.format(new Date(start)) + " : "
					+ mdf.format(new Date(end)) + ")");
			f.getContentPane().setLayout(new BorderLayout());
			f.getContentPane().add(tsp, BorderLayout.CENTER);
			f.pack();
			f.setVisible(true);
		}

		// dump to image file - need to set a real size for it as its not extant
		// on screen
		if (write) {

			Dimension d = tsp.getPreferredSize();
			tsp.setSize(d);

			BufferedImage image = new BufferedImage(tsp.getWidth(), tsp.getHeight(), BufferedImage.TYPE_INT_RGB);
			// call the Component's paint method, using
			// the Graphics object of the image.
			tsp.paint(image.getGraphics());

			// String datestr = udf.format(new Date(start));
			System.err.println("Writing to: " + outfileName);
			ImageIO.write(image, "png", new File(outfileName));
		}

	}

    private void setColor(int i, StateColorMap map, Properties props, String name) throws Exception {

	String value = props.getProperty((""+i).trim());

	StringTokenizer st = new StringTokenizer(value, "/");
	int r = Integer.parseInt(st.nextToken(), 16);
	int g = Integer.parseInt(st.nextToken(), 16);
	int b = Integer.parseInt(st.nextToken(), 16);

	Color color = new Color(r/255.0f, g/255.0f, b/255.0f);
	map.addColorLabel(i, color, name);
	System.err.println("Set color: "+i+" color " +color);
    }


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {

			ConfigurationProperties config = CommandTokenizer.use("--").parse(args);

			OperationsTimeSlicer slicer = new OperationsTimeSlicer(config);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
