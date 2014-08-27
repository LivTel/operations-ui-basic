/**
 * 
 */
package ngat.opsgui.base;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import ngat.opsgui.login.Display;
import ngat.opsgui.test.Behaviour;
import ngat.opsgui.test.ComponentDescriptor;
import ngat.opsgui.test.DockingManager;
import ngat.util.logging.LogGenerator;

/** A GUI display frame, contains 1 or more perspectives.
 * @author eng
 *
 */
public class DisplayFrame extends JFrame {


	static Dimension TOP_AREA_SIZE         = new Dimension(894, 180);
	static Dimension PERSPECTIVE_AREA_SIZE = new Dimension(850, 650);
	
	private Gui gui;
	
	/** The display we are ... displaying.*/
	private Display display;
	
	private JTabbedPane tabs;
	
	/** Not used yet but will be...*/
	private DockingManager dm;
	
	/** Menu bar - contains basic menus and perspective submenus.*/
	private JMenuBar menuBar;

	/** Sidebar if required.*/
	private GuiSideBar sidebar;

	/** Top panel- only if sidebar.*/
	private JPanel topPanel;
	
	/** True if the sidebar is required.*/
	private boolean sidebarRequired;
	
	/** Logging. */
	private LogGenerator logger;
	
	/** The currently selected perspective.*/
	private Perspective currentPerspective;

	public DisplayFrame(Gui gui, Display display, boolean sidebarRequired) {
		super(Gui.TITLE_BASE+" : "+
				gui.getCurrentUser().getDescriptor().getTitle()+" "+
				gui.getCurrentUser().getDescriptor().getFirstName()+" "+
				gui.getCurrentUser().getDescriptor().getLastName()+
				" : ["+display.getName()+"]");
		//super(Gui.TITLE_BASE+" : "+gui.getCurrentUser().getDescriptor()+" : ["+display.getName()+"]");
		
		//System.err.println("CREATE DISPLAYFRAME: "+display);
		
		this.gui = gui;
		this.display = display;
		this.sidebarRequired = sidebarRequired;		
		
		// Title:   LT Operations UI : Jo Bloggs : SchedInstOps
		
		try {
		createDisplay();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				super.componentResized(e);
				System.err.println("FRAME RESIZE: "+getSize().width+"x"+getSize().height);
			}
			
		});
	}
	
	private void createDisplay() throws Exception {

		// splash.splashText("Creating sidebar...");
		if (sidebarRequired) { 
			try {
				sidebar = gui.createSideBar();
				topPanel = gui.createTopPanel();
				topPanel.setPreferredSize(TOP_AREA_SIZE);
			
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (display.countPerspectives() > 1) {
			
			tabs = new JTabbedPane(SwingConstants.RIGHT);
			// tabs.setBorder(BorderFactory.createLoweredBevelBorder());
			tabs.setPreferredSize(PERSPECTIVE_AREA_SIZE);
			tabs.setFont(Resources.getFont("tabs.title.font"));
			tabs.setForeground(Resources.getColor("tabs.foreground.color"));
			tabs.addChangeListener(new TabListener());
			
			// not yet in use
			dm = new DockingManager(tabs);
			dm.setPostSelectionBehaviour(new InternalSelectionBehaviour());
			
			// add the perspectives to the PA			
			List<String> ps = display.getPerspectiveNames();
			for (int ip = 0; ip < ps.size(); ip++) {
				String pname = ps.get(ip);		
				// create the perspective
				Perspective p = gui.createPerspective(pname, this);
				// add the perspective to the display
				String plabel = p.getPerspectiveName().substring(0, 1);
				dm.attach(new ComponentDescriptor(p, plabel));	
				// LATER dm.attach(new ComponentDescriptor(p, plabel, p.getSidebarIcon()));	
				gui.addPerspective(plabel, p);
			}
			
			// Add the main components to the master frame.

			JPanel all = new JPanel(true);
			all.setLayout(new BorderLayout());

			JPanel right = new JPanel(true);
			right.setLayout(new BorderLayout());

			right.add(tabs, BorderLayout.CENTER);
			

			if (sidebar != null) {
				all.add(right, BorderLayout.CENTER);
				all.add(sidebar, BorderLayout.WEST);	
				right.add(topPanel, BorderLayout.NORTH);
			} else {
				all.add(right, BorderLayout.CENTER);
			}
			
			// Create the menus after we have all the perspectives in place.
		
				System.err.println("Creating menu bar");
				menuBar = createMenuBar();
				setJMenuBar(menuBar);
			
		
			getContentPane().add(all);
			pack();
			setVisible(true);

			//tabs.addChangeListener(new TabListener());
			
		
			
		} else {
			
			// Add the single component to the master frame.

			JPanel all = new JPanel(true);
			all.setLayout(new BorderLayout());

			// there is only ONE perspective here
			List<String> ps = display.getPerspectiveNames();
			for (int ip = 0; ip < ps.size(); ip++) {
				
				try {
				String pname = ps.get(ip);	
				// create the perspective
				Perspective p = gui.createPerspective(pname, this);
				// add the perspective to the display
				String plabel = p.getPerspectiveName().substring(0, 1);
				//dm.attach(new ComponentDescriptor(p, plabel));		
				gui.addPerspective(plabel, p);
				all.add(p, BorderLayout.CENTER);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("FATAL ERROR creating perspective !!!");
				}
			}
			
			// Create the menus after we have all the perspectives in place.
		
				System.err.println("Creating menu bar");
				menuBar = createMenuBar();
				setJMenuBar(menuBar);
			
		
			getContentPane().add(all);
			pack();
			setVisible(true);

			//tabs.addChangeListener(new TabListener());
			
		}
	}
	
	private JMenuBar createMenuBar() {
		JMenuBar bar = new JMenuBar();
		bar.add(gui.createFileMenu());
		//bar.add(createViewMenu());
		if (sidebarRequired)
			bar.add(gui.createControlMenu(this));

		bar.add(gui.createLogMenu());
		
		//bar.add(new JSeparator(JSeparator.VERTICAL));

		// bar.setHelpMenu(new JMenu("Help"));

		return bar;
	}
	
	/** Change to display the specified perspective. */
	public void changePerspective(String name) {
		System.err.println("DF::ChangePerspective: " + name);

		// Remove any menus supplied by the old perspective
		if (currentPerspective != null) {
			List<JMenu> oldMenus = currentPerspective.listMenus();
			for (int i = 0; i < oldMenus.size(); i++) {
				JMenu menu = oldMenus.get(i);
				System.err.println("Remove menu: " + i + " of " + oldMenus.size());
				menuBar.remove(menu);
			}
		}

		Perspective newPerspective = null;
		
			newPerspective = gui.getPerspective(name);
			System.err.println("New perspective is: "+newPerspective);
			if (newPerspective != null) {
				setTitle(Gui.TITLE_BASE+" : "+gui.getCurrentUser().getDescriptor().getFirstName()+" "+
						gui.getCurrentUser().getDescriptor().getLastName()+ ": " + 
						newPerspective.getClass().getSimpleName());
				//setTitle("Gui" + ": " + newPerspective.getClass().getSimpleName());

				// Add any menus supplied by the new perspective
				List<JMenu> newMenus = newPerspective.listMenus();

				for (int i = 0; i < newMenus.size(); i++) {
					JMenu menu = newMenus.get(i);
					System.err.println("Add menu: " + i + " of " + newMenus.size());
					menuBar.add(menu);
				}
			}

		
			if (menuBar != null)
				menuBar.revalidate();
			validate();
		repaint();
		currentPerspective = newPerspective;

	}
	
	

	private class TabListener implements ChangeListener {

		@Override
		public void stateChanged(ChangeEvent ce) {

			int itab = tabs.getSelectedIndex();
			Component comp = tabs.getComponentAt(itab);
			final String title = tabs.getTitleAt(itab);
			System.err.println("Selected tab: " + itab + " : " + title + " isa: " + comp.getClass().getName());

			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					changePerspective(title);
				}
			});

		}

	}

	
	
	
	/**
	 * @author eng
	 *
	 */
	public class InternalSelectionBehaviour implements Behaviour {

		/* (non-Javadoc)
		 * @see ngat.opsgui.test.Behaviour#perform()
		 */
		@Override
		public void perform() {
			
			// we need to determine which perspective was selected
			

		}

	}
}
