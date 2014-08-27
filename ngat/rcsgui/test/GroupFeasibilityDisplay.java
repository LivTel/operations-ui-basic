package ngat.rcsgui.test;

import ngat.phase2.*;
import ngat.astrometry.*;

import java.awt.*;
import java.util.*;
import java.text.*;

import javax.swing.*;

import org.jfree.chart.*;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.time.*;

public class GroupFeasibilityDisplay extends JPanel {

    public static final Color GRAPH_BGCOLOR = Color.black;
    
    public static SimpleDateFormat odf = new SimpleDateFormat("HH:mm");

    static final Font LFONT = new Font("palatino", Font.PLAIN, 10);

    static final Font TFONT = new Font("helvetica", Font.PLAIN, 8);

    private ISite site;

    private BasicAstrometryCalculator astro;

    private SkyBrightnessCalculator skycalc;

    TimeSeries ts1;
    TimeSeries ts2;

    public GroupFeasibilityDisplay(ISite site) {
	super();
	this.site = site;

	astro = new BasicAstrometryCalculator();
	skycalc = new SkyBrightnessCalculator(site);

    }

    public void displayGroup(ITarget target, long start) {

	JPanel graphPanel = new JPanel(true);
        graphPanel.setLayout(new BoxLayout(graphPanel, BoxLayout.Y_AXIS));

	// create the elevation display
	ts1 = addTimeSeriesGraph(graphPanel, "Elevation",  "Elevation", 0.0,   90.0, Color.blue);

	// create the skyb display if one is set
        ts2 = addTimeSeriesGraph(graphPanel, "Sky bright", "Brightness", 0.0,  10.0, Color.blue);
       
	// create the timing display

	// create overall display

	add(graphPanel);

	// run forward 12 hours
	try {
	    addData(target, start);
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    private void addData(ITarget target, long start) throws Exception {

	TargetTrackCalculator track = new BasicTargetCalculator(target, site);

	long time = start;
	while (time < start + 12*3600*1000L) {

	    Coordinates c = track.getCoordinates(time);

	    double elev = astro.getAltitude(c, site, time); 
	    ts1.add(new Second(new Date(time)), Math.toDegrees(elev));
	    
	    int skybcat = skycalc.getSkyBrightnessCriterion(track, time);
	    double sky = SkyBrightnessCalculator.getSkyBrightness(skybcat);
	    
	    if (sky > 10.0)
		sky = 15.0;
	    ts2.add(new Second(new Date(time)), sky);
	    
	    time += 15*60*1000L; // 15m
	}

    }


    private TimeSeries addTimeSeriesGraph(Container base, String label, String name, double lo, double hi, Color color) {
        TimeSeriesCollection tsc = new TimeSeriesCollection();
        TimeSeries ts = new TimeSeries(name, Second.class);
        ts.setMaximumItemCount(25000);
        tsc.addSeries(ts);
        JFreeChart cts = makeChart(null, name, lo, hi, tsc, color);

        ChartPanel cp = new ChartPanel(cts);
        cp.setPreferredSize(new Dimension(540, 140));

        JPanel line = new JPanel(true);
        line.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel jl = makeLabel(label);

        line.add(jl);
        line.add(cp);

        base.add(line);
        return ts;
    }

    private JLabel makeLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(LFONT);
        label.setPreferredSize(new Dimension(100, 16));
        return label;
    }


    protected static JFreeChart makeChart(String title, String ylabel, double lolim, double hilim,
                                          TimeSeriesCollection tsc, Color color) {
        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, "Time [UT]", ylabel, tsc, false, true, false);
        XYPlot plot = chart.getXYPlot();

        plot.setBackgroundPaint(GRAPH_BGCOLOR);
        plot.setDomainGridlinePaint(Color.green);
        plot.setDomainGridlineStroke(new BasicStroke(0.3f));
        plot.setRangeGridlinePaint(Color.green);
        plot.setRangeGridlineStroke(new BasicStroke(0.3f));

        XYItemRenderer r = plot.getRenderer();
        r.setStroke(new BasicStroke(1.5f));
        r.setPaint(color);

        ValueAxis axis = plot.getDomainAxis();

        if (axis instanceof DateAxis) {
            ((DateAxis) axis).setDateFormatOverride(odf);
            // System.err.println("X axis reset date formatter...");
        }
        axis.setAutoRange(true);
        axis.setFixedAutoRange(12 * 3600000.0); // 12 hour

        ValueAxis rangeAxis = plot.getRangeAxis();
        rangeAxis.setRange(lolim, hilim);

        // LegendTitle legendTitle = new LegendTitle(plot);
        // legendTitle.setPosition(RectangleEdge.RIGHT);
        // chart.addLegend(legendTitle);

        return chart;

    }




}