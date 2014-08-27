/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileFilter;
import javax.swing.JFileChooser;

import ngat.astrometry.AstroCatalog;
import ngat.astrometry.AstrometrySimulatorListener;
import ngat.astrometry.AstrometrySimulator;
import ngat.astrometry.ISite;
import ngat.opsgui.base.Perspective;
import ngat.opsgui.perspectives.astrometry.AitoffAstroSimulatorControlDialog;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.xcomp.CatalogDisplayEditorPanel2;
import ngat.opsgui.xcomp.CreateTargetPanel;
import ngat.phase2.ISequenceComponent;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;
import ngat.sms.ComponentSet;
import ngat.sms.GroupItem;
import ngat.sms.ScheduleItem;
import ngat.sms.SchedulingStatusUpdateListener;
import ngat.sms.ScoreMetricsSet;
import ngat.tcm.TelescopeStatusUpdateListener;
import ngat.tcm.PrimaryAxisStatus;
import ngat.tcm.TelescopeStatus;
import ngat.net.cil.tcs.TcsStatusPacket;

/**
 * @author eng
 * 
 */
// implements TelescopeStatusUpdateListenerComponent is better as has the extra
// paint flag
public class TrackingPerspective extends Perspective implements TelescopeStatusUpdateListener,
		SchedulingStatusUpdateListener, AstrometrySimulatorListener, ServiceAvailabilityListener {

	private ISite site;

	private AitoffMasterPanel aitoffMasterPanel;
	private AitoffPlot aitoff;

	private AitoffAstroSimulatorControlDialog astroSimDialog;

	private RaDecPlot radec;

	private JMenu catMenu;

	private int azmState;
	private int altState;
	private double azm;
	private double alt;

	/**
	 * 
	 */
	public TrackingPerspective(JFrame frame, ISite site) {
		super(frame);
		perspectiveName = "T";
		setLayout(new BorderLayout());

		this.site = site;

		JTabbedPane tabs = new JTabbedPane(SwingConstants.TOP, SwingConstants.HORIZONTAL);

		// aitoff

		aitoff = new AitoffPlot();
		aitoff.showTracks(site);
		aitoff.setShowTime(true);
		aitoff.setShowAltAzGrid(true);
		aitoffMasterPanel = new AitoffMasterPanel(aitoff);

		// MAYBE put in super constructor ????
		createMenus();

		// Link up sim controller
		AstrometrySimulator simulator = new AstrometrySimulator();
		astroSimDialog = new AitoffAstroSimulatorControlDialog(frame, this, simulator);
		simulator.addSimulationListener(astroSimDialog);
		simulator.addSimulationListener(this);

		// add some gratuitous catalogs

			// TODO this could be done within createMenus()....
		
		try {
			AstroCatalog c1 = new AstroCatalog("SWEEP");
			CatalogDisplayDescriptor cd1 = new CatalogDisplayDescriptor("SWEEP");
			cd1.setColor(Color.cyan);
			cd1.setShowLabel(true);
			cd1.setSymbol(CatalogDisplayDescriptor.OPEN_DIAMOND_SYMBOL);
			CatalogDisplay d1 = new CatalogDisplay(c1, cd1);
			aitoffMasterPanel.addCatalogDisplay(d1);
			
			AstroCatalog c2 = new AstroCatalog("OPS");
			CatalogDisplayDescriptor cd2 = new CatalogDisplayDescriptor("OPS");
			cd2.setColor(Color.magenta.brighter());
			cd2.setShowLabel(true);
			cd2.setSymbol(CatalogDisplayDescriptor.CROSS_SYMBOL);
			cd2.setShowCount(1);
			cd2.setShowAll(false);
			CatalogDisplay d2 = new CatalogDisplay(c2, cd2);
			aitoffMasterPanel.addCatalogDisplay(d2);
		
			AstroCatalog c3 = new AstroCatalog("DEFAULT");
			CatalogDisplayDescriptor cd3 = new CatalogDisplayDescriptor("DEFAULT");
			cd3.setColor(Color.gray);
			cd3.setShowLabel(true);
			cd3.setSymbol(CatalogDisplayDescriptor.PLUS_SYMBOL);
			cd3.setShowCount(5);
			cd3.setShowAll(false);
			CatalogDisplay d3 = new CatalogDisplay(c3, cd3);
			aitoffMasterPanel.addCatalogDisplay(d3);
			

		} catch (Exception e) {
			e.printStackTrace();
		}
		tabs.addTab("AltAz", aitoffMasterPanel);

		radec = new RaDecPlot(site);
		radec.setShowAltazGrid(true);

		tabs.addTab("RaDec", radec);

		add(tabs, BorderLayout.CENTER);

	}

	private void createMenus() {

		JMenu overlayMenu = new JMenu("Overlays");

		OverlayListener ol = new OverlayListener();

		JCheckBoxMenuItem item = new JCheckBoxMenuItem("RA grid");
		item.addActionListener(ol);
		item.setActionCommand("ra-grid");
		overlayMenu.add(item);

		item = new JCheckBoxMenuItem("Dec grid");
		item.addActionListener(ol);
		item.setActionCommand("dec-grid");
		overlayMenu.add(item);

		item = new JCheckBoxMenuItem("Altaz grid");
		item.addActionListener(ol);
		item.setActionCommand("altaz-grid");
		// switch altaz on by default
		item.setSelected(true);
		overlayMenu.add(item);

		item = new JCheckBoxMenuItem("Airmass grid");
		item.addActionListener(ol);
		item.setActionCommand("airmass-grid");
		overlayMenu.add(item);

		item = new JCheckBoxMenuItem("Sky");
		item.addActionListener(ol);
		item.setActionCommand("sky");
		overlayMenu.add(item);

		JMenu historyMenu = new JMenu("History");
		HistoryListener hl = new HistoryListener();

		ButtonGroup bg = new ButtonGroup();

		JRadioButtonMenuItem rbitem = new JRadioButtonMenuItem("Off");
		rbitem.addActionListener(hl);
		rbitem.setActionCommand("hist-off");
		historyMenu.add(rbitem);
		bg.add(rbitem);

		rbitem = new JRadioButtonMenuItem("30 Mins");
		rbitem.addActionListener(hl);
		rbitem.setActionCommand("hist-30m");
		historyMenu.add(rbitem);
		bg.add(rbitem);

		rbitem = new JRadioButtonMenuItem("1 Hour");
		rbitem.addActionListener(hl);
		rbitem.setActionCommand("hist-1h");
		historyMenu.add(rbitem);
		bg.add(rbitem);

		rbitem = new JRadioButtonMenuItem("2 Hours");
		rbitem.addActionListener(hl);
		rbitem.setActionCommand("hist-2h");
		historyMenu.add(rbitem);
		bg.add(rbitem);

		rbitem = new JRadioButtonMenuItem("4 Hours");
		rbitem.addActionListener(hl);
		rbitem.setActionCommand("hist-4h");
		historyMenu.add(rbitem);
		bg.add(rbitem);

		rbitem = new JRadioButtonMenuItem("All");
		rbitem.addActionListener(hl);
		rbitem.setActionCommand("hist-all");
		historyMenu.add(rbitem);
		bg.add(rbitem);

		overlayMenu.add(historyMenu);

		menus.add(overlayMenu);

		// Tracking menu
		catMenu = new JMenu("Catalog");

		TrackingListener tl = new TrackingListener();

		JMenuItem loadCatFileItem = new JMenuItem("Load cat (file)...");
		loadCatFileItem.addActionListener(tl);
		loadCatFileItem.setActionCommand("load-cat-file");
		catMenu.add(loadCatFileItem);

		JMenuItem loadCatProviderItem = new JMenuItem("Load cat (prov.)...");
		loadCatProviderItem.addActionListener(tl);
		loadCatProviderItem.setActionCommand("load-cat-provider");
		catMenu.add(loadCatProviderItem);

		JMenuItem newTargetItem = new JMenuItem("Single target...");
		newTargetItem.addActionListener(tl);
		newTargetItem.setActionCommand("new-target");
		catMenu.add(newTargetItem);

		JMenuItem newCatItem = new JMenuItem("New catalog...");
		newCatItem.addActionListener(tl);
		newCatItem.setActionCommand("new-catalog");
		catMenu.add(newCatItem);

		// TODO add the default catalogues to the cat menu here...
		
		menus.add(catMenu);

		// Add a simulation menu.
		JMenu simMenu = new JMenu("Simulation");
		JMenuItem simStartItem = new JMenuItem("Start");
		simStartItem.addActionListener(new MySimListener());
		simMenu.add(simStartItem);
		menus.add(simMenu);

	}

	// From AstroSimListener - some repetition here trash some of these

	@Override
	public void simulationTimeUpdated(long time) {
		aitoff.setSimulationTime(time);
		aitoff.repaint();
	}

	@Override
	public void simulationRunning(boolean run) {
		// aitoff.setSimulation(true);
	}

	// own methods for AstroSimDialog
	public void simulationActive(boolean active) {
		// switch Aitoff between sim and real modes.
		aitoff.setSimulation(active);
		aitoff.repaint();
	}

	@Override
	public void telescopeStatusUpdate(TelescopeStatus status) throws RemoteException {

		// System.err.println("TRACK_P:Recieved update class: " +
		// status.getClass().getName());
		// System.err.println("Recieved update: " + status);
		try {
			processTelescopePositionUpdate(status, true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// @Override
	@Override
	public void telescopeNetworkFailure(long time, String message) throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		// TODO Auto-generated method stub

	}

	@Override
	public void candidateAdded(String arg0, GroupItem arg1, ScoreMetricsSet arg2, double arg3, int arg4)
			throws RemoteException {
		// TODO Auto-generated method stub
		// nothing to do
	}

	@Override
	public void candidateRejected(String arg0, GroupItem arg1, String arg2) throws RemoteException {
		// TODO Auto-generated method stub
		// nothing to do
	}

	@Override
	public void candidateSelected(long time, ScheduleItem sched) throws RemoteException {

		GroupItem group = sched.getGroup();
		ISequenceComponent root = group.getSequence();

		// find any targets
		try {
			ComponentSet cs = new ComponentSet(root);
			Iterator<ITarget> targets = cs.listTargets();
			while (targets.hasNext()) {
				ITarget target = targets.next();
				addTargetToCatalog("OPS", target);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void scheduleSweepStarted(long arg0, int arg1) throws RemoteException {
		// TODO Auto-generated method stub
		// nothing to do
	}

	private void processTelescopePositionUpdate(TelescopeStatus status, boolean paint) {

		if (status instanceof PrimaryAxisStatus) {

			PrimaryAxisStatus axis = (PrimaryAxisStatus) status;
			double current = axis.getCurrentPosition();
			String axisName = axis.getMechanismName();

			if (axisName == null)
				return;

			if (axis.getMechanismName().equals("AZM")) {
				// System.err.println("processUpdate()::Received update for: " +
				// axisName);

				azm = Math.toRadians(current);
				azmState = axis.getMechanismState();
				while (azm > Math.PI)
					azm -= 2.0 * Math.PI;

			} else if (axis.getMechanismName().equals("ALT")) {
				// System.err.println("processUpdate()::Received update for: " +
				// axisName);

				alt = Math.toRadians(current);
				altState = axis.getMechanismState();

				Color color = Color.gray;
				// dont record if the scope isnt moving
				if (azmState == TcsStatusPacket.MOTION_TRACKING && altState == TcsStatusPacket.MOTION_TRACKING) {
					color = Color.green;
					aitoff.addCoordinate(axis.getStatusTimeStamp(), alt, azm, color, paint);
					radec.addCoordinate(azm, alt, axis.getStatusTimeStamp());

				}
			}

		}

	}

	/** Add a catalog display. */
	public void addCatalogDisplay(CatalogDisplay display) throws Exception {
		aitoffMasterPanel.addCatalogDisplay(display);
		// add entry to cat menu
		JMenuItem item = new JMenuItem(display.getDisplayDescriptor().getName());
		item.setAction(new CatalogEditAction(display));
		catMenu.add(item);
	}

	/** Modify an already-displayed catalog. */
	public void updateCatalogDisplay(CatalogDisplay display) throws Exception {
		aitoffMasterPanel.updateCatalogDisplay(display);
	}

	/** Add a target to a catalog. */
	public void addTargetToCatalog(String catName, ITarget target) throws Exception {
		aitoffMasterPanel.addTargetToCatalog(catName, target);
	}

	private class MySimListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			// if no simdialog, popitup else dont
			if (astroSimDialog.isVisible())
				return;
			astroSimDialog.setVisible(true);

		}

	}

	private class TrackingListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {

			String command = ae.getActionCommand();

			if (command.equals("load-cat-file")) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileFilter(new CatFilter());
				int returnVal = chooser.showDialog(TrackingPerspective.this, "Load from local catalog file");
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					System.out.println("You chose this file: " + chooser.getSelectedFile().getName());

					try {
						
						AstroCatalog cat = AstroCatalog.loadCatalog("DEFAULT", chooser.getSelectedFile());
						
						//JDialog dlg = new JDialog();
						//dlg.setTitle("Catalog Display Editor");

						CatalogDisplayEditorPanel2 cep = new CatalogDisplayEditorPanel2(TrackingPerspective.this, cat);
						cep.createCatalogDisplay();
						
						//CatalogDisplayEditorPanel cep = new CatalogDisplayEditorPanel(dlg, TrackingPerspective.this,
							//	cat);
						//cep.createCatalogDisplay();
						//dlg.getContentPane().add(cep);
						//dlg.pack();
						//dlg.setVisible(true);

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			} else if (command.equals("load-cat-provider")) {

			} else if (command.equals("new-catalog")) {

			} else if (command.equals("new-target")) {

				//String tstring = JOptionPane.showInputDialog(aitoff, "Enter target: <name> <hh:mm:ss> <dd:mm:ss>",
					//	"New target", JOptionPane.INFORMATION_MESSAGE);

				CreateTargetPanel ctp = new CreateTargetPanel();
				XExtraSolarTarget target = ctp.showDialog();
				ctp.setVisible(true);
				
				if (target != null) {
					try {
					aitoff.getCatalogDisplayManager().addTargetToCatalog("DEFAULT", target);
					aitoff.repaint();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				/*try {
					StringTokenizer st = new StringTokenizer(tstring);
					String name = st.nextToken();
					String strra = st.nextToken();
					double ra = AstroFormatter.parseHMS(strra, ":");
					String strdec = st.nextToken();
					double dec = AstroFormatter.parseDMS(strdec, ":");

					XExtraSolarTarget star = new XExtraSolarTarget(name);
					star.setRa(ra);
					star.setDec(dec);
					aitoff.getCatalogDisplayManager().addTargetToCatalog("DEFAULT", star);

				} catch (Exception e) {
					e.printStackTrace();
				}*/

			} else if (command.equals("new-catalog")) {

			}

		}

	}

	private class CatFilter extends FileFilter {
		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			}
			if (f.getName().endsWith(".cat"))
				return true;
			return false;
		}

		@Override
		public String getDescription() {
			return "Astrometry catalog files";
		}

	}

	/**
	 * @author eng
	 * 
	 */
	public class OverlayListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			String command = ae.getActionCommand();

			if (command.equals("ra-grid")) {
				aitoff.setShowRaGrid(!aitoff.isShowRaGrid());
			} else if (command.equals("dec-grid")) {
				aitoff.setShowDecGrid(!aitoff.isShowDecGrid());
			} else if (command.equals("altaz-grid")) {
				aitoff.setShowAltAzGrid(!aitoff.isShowAltAzGrid());
				aitoff.setShowAirmassGrid(!aitoff.isShowAltAzGrid());
			} else if (command.equals("airmass-grid")) {
				aitoff.setShowAirmassGrid(!aitoff.isShowAirmassGrid());
				aitoff.setShowAltAzGrid(!aitoff.isShowAirmassGrid());
			} else if (command.equals("sky")) {
				aitoff.setShowSkyB(!aitoff.isShowSkyB());
			}
			aitoff.repaint();
		}

	}

	/**
	 * @author eng
	 * 
	 */
	public class HistoryListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent ae) {
			String command = ae.getActionCommand();

			long age = 0L;
			if (command.equals("hist-off")) {
				age = 0L;
			} else if (command.equals("hist-30m")) {
				age = 30 * 60 * 1000L;
			} else if (command.equals("hist-1h")) {
				age = 60 * 60 * 1000L;
			} else if (command.equals("hist-2h")) {
				age = 2 * 60 * 60 * 1000L;
			} else if (command.equals("hist-4h")) {
				age = 4 * 60 * 60 * 1000L;
			} else if (command.equals("hist-all")) {
				// never actually have 24h worth but it covers the full night
				age = 24 * 60 * 60 * 1000L;

			}

			JOptionPane.showMessageDialog(aitoff, "History: Set age limit: " + age);			
			aitoff.setHistoryColorAging(true);
			aitoff.setHistoryDuration(age);
			aitoff.repaint();

		}
	}

	private class CatalogEditAction extends AbstractAction {

		private CatalogDisplay display;

		/**
		 * @param display
		 */
		public CatalogEditAction(CatalogDisplay display) {
			super(display.getDisplayDescriptor().getName());
			this.display = display;
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			//JDialog dlg = new JDialog();
			//dlg.setTitle("Catalog Display Editor");

			//CatalogDisplayEditorPanel cep = new CatalogDisplayEditorPanel(dlg, TrackingPerspective.this,
			//		display.getCatalog());
			//cep.editCatalogDisplay(display.getDisplayDescriptor());
			
			CatalogDisplayEditorPanel2 cep2 = new CatalogDisplayEditorPanel2(TrackingPerspective.this, display.getCatalog());
			cep2.editCatalogDisplay(display.getDisplayDescriptor());
			
			//dlg.getContentPane().add(cep);
			//dlg.pack();
			//dlg.setVisible(true);

		}

	}

	@Override
	public void schedulerMessage(int arg0, String arg1) throws RemoteException {
		// TODO Auto-generated method stub
		
	}

}
