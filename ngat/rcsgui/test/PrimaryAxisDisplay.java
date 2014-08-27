/**
 * 
 */
package ngat.rcsgui.test;

import javax.swing.BoxLayout;
import java.awt.BorderLayout;
import javax.swing.border.BevelBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.*;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.List;
import java.util.Iterator;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JCheckBox;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.SimpleHistogramBin;
import org.jfree.data.statistics.SimpleHistogramDataset;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.astrometry.AstroCatalog;
import ngat.phase2.XExtraSolarTarget;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.RotatorAxisStatus;
import ngat.tcm.Telescope;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusProvider;
import ngat.tcm.TelescopeStatusUpdateListener;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.opsgui.components.*;
import ngat.opsgui.util.*;
import ngat.opsgui.base.*;
import ngat.util.CommandTokenizer;
import ngat.util.ConfigurationProperties;

/**
 * @author eng
 * 
 */
public class PrimaryAxisDisplay extends UnicastRemoteObject implements TelescopeStatusUpdateListener {

	public static Dimension CSIZE = new Dimension(200, 200);

	ISite site;

	ngat.opsgui.util.DataField azmCurrentField;
	ngat.opsgui.util.DataField azmDemandField;
	ngat.opsgui.util.StateField azmStatusField;

	ngat.opsgui.util.DataField altCurrentField;
	ngat.opsgui.util.DataField altDemandField;
	ngat.opsgui.util.StateField altStatusField;

	ngat.opsgui.util.DataField rotCurrentField;
	ngat.opsgui.util.DataField rotDemandField;
	ngat.opsgui.util.StateField rotStatusField;
	ngat.opsgui.util.StateField rotModeField;
	ngat.opsgui.util.DataField rotSkyField;

	ngat.opsgui.perspectives.tracking.AitoffPlot aitoff;

	private SimpleHistogramDataset azmDataset;
	private SimpleHistogramDataset altDataset;

	StateColorMap stateMap;
	StateColorMap modeMap;

	JPanel main;

	double azm;
	double alt;
	int azmState = TcsStatusPacket.MOTION_UNKNOWN;
	int altState = TcsStatusPacket.MOTION_UNKNOWN;

	JCheckBox raBox;
	JCheckBox decBox;
	JCheckBox skyBox;
	JCheckBox stdBox;

	/** Record latest AZM. */
	double latestAzm = 0.0;

	/** Record latest ALT. */
	double latestAlt = 0.0;

	private AstroCatalog stdcat;

	public PrimaryAxisDisplay(ISite site, AstroCatalog stdcat) throws RemoteException {
		super();
		this.site = site;
		this.stdcat = stdcat;

		stateMap = new StateColorMap(Color.gray, "UNKNOWN");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_OFF_LINE, Color.red, "OFFLINE");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_ERROR, Color.red.darker(), "ERROR");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_TRACKING, Color.green, "TRACKING");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_MOVING, Color.green, "MOVING");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_INPOSITION, Color.green, "IN-POSN");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_STOPPED, Color.green, "STOPPED");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_OVERRIDE, Color.yellow, "OVEERIDE");
		stateMap.addColorLabel(TcsStatusPacket.MOTION_LIMIT, Color.yellow, "LIMIT");

		modeMap = new StateColorMap(Color.gray, "UNKNOWN");
		modeMap.addColorLabel(TcsStatusPacket.ROT_SKY, Color.pink, "SKY");
		modeMap.addColorLabel(TcsStatusPacket.ROT_MOUNT, Color.pink, "MOUNT");
		modeMap.addColorLabel(TcsStatusPacket.ROT_FLOAT, Color.pink, "FLOAT");

		createDisplay();

	}

	private void createDisplay() {

		main = new JPanel(true);
		main.setLayout(new BorderLayout());

		// Left: azm, alt, rot statii
		JPanel left = createStatusPanel();

		// Right: sky and graphs
		JPanel right = new JPanel(true);
		right.setLayout(new BorderLayout());

		// Right Top: sky
		aitoff = new ngat.opsgui.perspectives.tracking.AitoffPlot();
		aitoff.showTracks(site);
		aitoff.setShowAltAzGrid(true);
		right.add(aitoff, BorderLayout.CENTER);

		// Right bottom: controls
		JPanel mpanel = new JPanel(true);
		mpanel.setLayout(new FlowLayout(FlowLayout.LEADING));

		ItemListener il = new MyItemListener();
		raBox = new JCheckBox("RA");
		raBox.addItemListener(il);
		mpanel.add(raBox);
		decBox = new JCheckBox("Dec");
		decBox.addItemListener(il);
		mpanel.add(decBox);
		skyBox = new JCheckBox("SkyB");
		skyBox.addItemListener(il);
		mpanel.add(skyBox);
		stdBox = new JCheckBox("Std");
		stdBox.addItemListener(il);
		mpanel.add(stdBox);
		right.add(mpanel, BorderLayout.SOUTH);

		// Left bottom: histograms
		JPanel gpanel = createHistograms();
		left.add(gpanel);

		main.add(right, BorderLayout.CENTER);
		main.add(left, BorderLayout.WEST);

	}

	private JPanel createStatusPanel() {
		JPanel panel = new JPanel(true);

		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		// setPreferredSize(new Dimension(200, 200));

		// azm
		JPanel azmPanel = new JPanel(true);
		azmPanel.setLayout(new BoxLayout(azmPanel, BoxLayout.Y_AXIS));
		azmPanel.setBorder(BorderFactory.createTitledBorder("Azm"));

		LinePanel line = ComponentFactory.makeLinePanel();
		azmCurrentField = ComponentFactory.makeDataField(5, "%f5.2");
		azmDemandField = ComponentFactory.makeDataField(5, "%f5.2");
		azmStatusField = ComponentFactory.makeStateField(10, stateMap);

		line.add(ComponentFactory.makeLabel("Current"));
		line.add(azmCurrentField);
		azmPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Demand"));
		line.add(azmDemandField);
		azmPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Status"));
		line.add(azmStatusField);
		azmPanel.add(line);
		panel.add(azmPanel);

		// alt
		JPanel altPanel = new JPanel(true);
		altPanel.setLayout(new BoxLayout(altPanel, BoxLayout.Y_AXIS));
		altPanel.setBorder(BorderFactory.createTitledBorder("Alt"));

		altCurrentField = ComponentFactory.makeDataField(5, "%f5.2");
		altDemandField = ComponentFactory.makeDataField(5, "%f5.2");
		altStatusField = ComponentFactory.makeStateField(10, stateMap);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Current"));
		line.add(altCurrentField);
		altPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Demand"));
		line.add(altDemandField);
		altPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Status"));
		line.add(altStatusField);
		altPanel.add(line);
		panel.add(altPanel);

		// rot
		JPanel rotPanel = new JPanel(true);
		rotPanel.setLayout(new BoxLayout(rotPanel, BoxLayout.Y_AXIS));
		rotPanel.setBorder(BorderFactory.createTitledBorder("Rot"));

		rotCurrentField = ComponentFactory.makeDataField(5, "%f5.2");
		rotDemandField = ComponentFactory.makeDataField(5, "%f5.2");
		rotStatusField = ComponentFactory.makeStateField(10, stateMap);
		rotModeField = ComponentFactory.makeStateField(10, modeMap);
		rotSkyField = ComponentFactory.makeDataField(5, "%f5.2");

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Current"));
		line.add(rotCurrentField);
		rotPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Demand"));
		line.add(rotDemandField);
		rotPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Status"));
		line.add(rotStatusField);
		rotPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Mode"));
		line.add(rotModeField);
		rotPanel.add(line);

		line = ComponentFactory.makeLinePanel();
		line.add(ComponentFactory.makeLabel("Sky"));
		line.add(rotSkyField);
		rotPanel.add(line);
		panel.add(rotPanel);

		return panel;
	}

	private JPanel createHistograms() {
		JPanel panel = new JPanel(true);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

		azmDataset = new SimpleHistogramDataset("Azm");
		azmDataset.setAdjustForBinSize(false);
		double lb = 0.0;
		while (lb < 360.0) {
			double ub = lb + 10.0;
			SimpleHistogramBin bin = new SimpleHistogramBin(lb, ub, true, false);
			azmDataset.addBin(bin);
			System.err.println("Azm: Add bin: " + bin.getLowerBound() + "," + bin.getUpperBound());
			lb = ub;
		}

		String plotTitle = "Azm";
		String xaxis = "azm [deg]";
		String yaxis = "Number";
		PlotOrientation orientation = PlotOrientation.VERTICAL;
		boolean show = false;
		boolean toolTips = false;
		boolean urls = false;
		JFreeChart azmchart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, azmDataset, orientation, show,
				toolTips, urls);
		ChartPanel azmcp = new ChartPanel(azmchart);
		azmcp.getChart().getTitle().setFont(new Font("Serif", Font.ITALIC, 10));
		azmcp.setPreferredSize(CSIZE);

		panel.add(azmcp);

		// alt
		altDataset = new SimpleHistogramDataset("Alt");
		altDataset.setAdjustForBinSize(false);
		lb = 0.0;
		while (lb < 90.0) {
			double ub = lb + 5.0;
			SimpleHistogramBin bin = new SimpleHistogramBin(lb, ub, true, false);
			altDataset.addBin(bin);
			System.err.println("Alt: Add bin: " + bin.getLowerBound() + "," + bin.getUpperBound());
			lb = ub;
		}

		plotTitle = "Altitude";
		xaxis = "alt [deg]";
		yaxis = "Number";

		JFreeChart altchart = ChartFactory.createHistogram(plotTitle, xaxis, yaxis, altDataset, orientation, show,
				toolTips, urls);
		ChartPanel altcp = new ChartPanel(altchart);
		altcp.getChart().getTitle().setFont(new Font("Serif", Font.ITALIC, 10));
		altcp.setPreferredSize(CSIZE);

		panel.add(altcp);

		return panel;
	}

	public static void main(String args[]) {

		try {

			System.setProperty("astrometry.impl", "ngat.astrometry.TestCalculator");

			ConfigurationProperties config = CommandTokenizer.use("--").parse(args);

			// v. important and bizarre !
			TcsStatusPacket.mapCodes();

			Resources.setDefaults(config.getProperty("base", "/home/eng/rcsgui/"));

			double lat = Math.toRadians(config.getDoubleValue("lat", 28.0));
			double lon = Math.toRadians(config.getDoubleValue("lon", -17.0));
			ISite site = new BasicSite("test", lat, lon);

			String catfile = config.getProperty("cat", "/occ/rcs/config/bg_std.cat");
			
			AstroCatalog stdcat = AstroCatalog.loadCatalog("STD", new File(catfile));
			
			PrimaryAxisDisplay p = new PrimaryAxisDisplay(site, stdcat);
			JPanel main = p.getMain();

			JFrame f = new JFrame("Primary Axes");
			f.getContentPane().add(main);
			f.pack();
			f.setVisible(true);

			String host = config.getProperty("host", "localhost");

			Telescope scope = (Telescope) Naming.lookup("rmi://" + host + "/Telescope");
			System.err.println("Found scope: " + scope);

			// time minutes
			long back = 180 * 60000L;

			long t2 = System.currentTimeMillis();
			long t1 = t2 - back;

			// TelescopeStatusArchive tar = (TelescopeStatusArchive)
			// Naming.lookup("rmi://"+host+"/TelescopeGateway");
			// System.err.printf("Requesting archive data for %4d mins, from: %tT to %tT",
			// (back / 60000), t1, t2);

			// long st0 = System.currentTimeMillis();
			// List<TelescopeStatus> list = tar.getTelescopeStatusHistory(t1,
			// t2);
			// long st1 = System.currentTimeMillis();

			// System.err.println("Request returned " + list.size() +
			// " entries in " + (st1 - st0) + "ms");

			// for (int is = 0; is < list.size(); is++) {
			// TelescopeStatus status = list.get(is);
			// System.err.printf("%6d : %tT : %s\n", is,
			// status.getStatusTimeStamp(), status);
			// p.processUpdate(status, false);
			// }

			TelescopeStatusProvider tsp = scope.getTelescopeStatusProvider();
			System.err.println("Found TSP: " + tsp);

			tsp.addTelescopeStatusUpdateListener(p);
			System.err.println("Added self as listener");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void processUpdate(TelescopeStatus status, boolean paint) {
		if (status instanceof PrimaryAxisStatus) {
			PrimaryAxisStatus axis = (PrimaryAxisStatus) status;

			String axisName = axis.getMechanismName();
			System.err.println("processUpdate()::Recieved update for: " + axisName);
			if (axisName == null)
				return;
			double current = axis.getCurrentPosition();
			double demand = axis.getDemandPosition();
			int state = axis.getMechanismState();

			if (axis.getMechanismName().equals("AZM")) {
				azm = Math.toRadians(current);
				while (azm > Math.PI)
					azm -= 2.0 * Math.PI;
				azmState = axis.getMechanismState();

				azmCurrentField.updateData(current);
				azmDemandField.updateData(demand);
				azmStatusField.updateState(state);

				while (current < 0.0)
					current += 360.0;

				latestAzm = current;

			} else if (axis.getMechanismName().equals("ALT")) {
				alt = Math.toRadians(current);
				altState = axis.getMechanismState();

				altCurrentField.updateData(current);
				altDemandField.updateData(demand);
				altStatusField.updateState(state);

				Color color = Color.gray;
				// dont record if the scope isnt moving
				if (azmState == TcsStatusPacket.MOTION_TRACKING && altState == TcsStatusPacket.MOTION_TRACKING) {
					color = Color.green;
					aitoff.addCoordinate(axis.getStatusTimeStamp(), alt, azm, color, paint);
				}

				latestAlt = current;
				// dont add to histogram if its 0ish
				if (Math.abs(latestAzm) >= Math.toRadians(0.05) && Math.abs(latestAlt) >= Math.toRadians(0.05)) {
					altDataset.addObservation(latestAlt);
					azmDataset.addObservation(latestAzm);
				}

			} else if (axis.getMechanismName().equals("ROT")) {

				RotatorAxisStatus rot = (RotatorAxisStatus) status;

				rotCurrentField.updateData(current);
				rotDemandField.updateData(demand);
				rotStatusField.updateState(state);
				rotModeField.updateState(rot.getRotatorMode());
				rotSkyField.updateData(rot.getSkyAngle());

			}
		}

	}

	/**
	 * @return the main
	 */
	public JPanel getMain() {
		return main;
	}

	private class MyItemListener implements ItemListener {
		@Override
		public void itemStateChanged(ItemEvent e) {

			Object source = e.getItemSelectable();

			boolean selected = (e.getStateChange() == ItemEvent.SELECTED);

			if (source == raBox) {
				aitoff.setShowRaGrid(selected);
				System.err.println("RA Grid: " + selected);
			} else if (source == decBox) {
				aitoff.setShowDecGrid(selected);
				System.err.println("Dec Grid: " + selected);
			} else if (source == skyBox) {
				aitoff.setShowSkyB(selected);
				System.err.println("SkyB Overlay: " + selected);
			} else if (source == stdBox) {
				if (selected) {
					//aitoff.clearMarkedTargets();
					// TODO - bummer - now need to re-add stds to marked
					// targets.

					List<XExtraSolarTarget> tlist = stdcat.listTargets();
					Iterator<XExtraSolarTarget> it = tlist.iterator();
					while (it.hasNext()) {
						// OLD STYLE TARGETS !!!					
						XExtraSolarTarget tgt = it.next();					
						//aitoff.addMarkedTarget(tgt);
					}
					System.err.println("Std overlay: selected");
				} else {
					//aitoff.clearMarkedTargets();
					System.err.println("Std overlay: de-selected");
				}

			}
		}
	}

	// @Override
	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {

		System.err.println("Recieved update class: " + status.getClass().getName());
		System.err.println("Recieved update: " + status);
		try {
			processUpdate(status, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// @Override
	@Override
	public void telescopeNetworkFailure(long time, String arg0) throws RemoteException {
		// TODO Auto-generated method stub

	}

}
