package ngat.rcsgui.stable;

//package ngat.rcs.gui;

import java.util.*;

import javax.swing.*;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import java.awt.event.*;
import java.awt.*;


/** Displays a Graph with some controls. */
public class GraphTabFrame extends JFrame {

	/** Map from chartID to chartpanel. */
	protected Map graphs;

	/** Map from cat.key to TimeSeries. */
	protected Map series;

	/** Tabbed pane. */
	JTabbedPane pane;

	/** Create a GraphTabFrame. */
	public GraphTabFrame(String title) {
		super(title);

		graphs = new HashMap();
		series =new HashMap();
		pane = new JTabbedPane(SwingConstants.TOP, JTabbedPane.WRAP_TAB_LAYOUT);

		getContentPane().add(pane);
		pack();

		setBounds(0, 0, 500, 150);
		setVisible(true);

	}

	/**
	 * Add the Supplied GraphPanel.
	 * 
	 * @param graphTitle
	 *            The title for the Graph's tab.
	 * @param graphPanel
	 *            The GraphPanel.
	 */
	public void addGraphPanel(String graphTitle, GraphPanel graphPanel) {

		graphs.put(graphTitle, graphPanel);
		pane.addTab(graphTitle, graphPanel);

	}

    public void addGeneralPanel(String title,JPanel panel) {

		pane.addTab(title, panel);

	}

	public void addCategorySeries(String cat, String key, String graphTitle,
			TimeSeries ts) {

		GraphPanel gpanel = (GraphPanel) graphs.get(graphTitle);
		if (gpanel == null)
			return;
		String gname = cat + ":" + key;

		if (series.containsKey(gname))
			return;

		TimeSeriesCollection tsc = gpanel.getTSC();
		tsc.addSeries(ts);
		series.put(gname, ts);
		
		XYPlot plot = gpanel.getPlot();
		
		plot.getRenderer().setStroke(new BasicStroke(2.0f));

	}


	public void updateGraph(String cat, String key, double y) {

		String gname = cat + ":" + key;

		if (! series.containsKey(gname))
			return;

		((TimeSeries) series.get(gname)).add(new Second(), y);

	}

	/** Listens for menu selections. */
	private class MenuListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			// This is the <cat.key> value.
			String gname = ae.getActionCommand();

			System.err.println("*** Selected: " + gname);

		}

	}

}
