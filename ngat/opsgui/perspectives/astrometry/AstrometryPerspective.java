/**
 * 
 */
package ngat.opsgui.perspectives.astrometry;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

import ngat.astrometry.BasicSite;
import ngat.astrometry.ISite;
import ngat.astrometry.LunarCalculator;
import ngat.opsgui.base.Perspective;
import ngat.rcsgui.test.AstrometryPanelTest;

/**
 * @author eng
 *
 */
public class AstrometryPerspective extends Perspective {

	private ISite site;
	
	private AstrometryPanelTest atp;
	
	private JMenu targetMenu;
	
	/**
	 * @param frame
	 */
	public AstrometryPerspective(JFrame frame, ISite site) {
		super(frame);
		this.site = site;
		perspectiveName = "A";
		setLayout(new BorderLayout());

		// top panel
		JPanel topPanel = new JPanel(true);
		topPanel.setLayout(new BorderLayout());

		// MAYBE put in super constructor ????
		createMenus();
		
		long sod = System.currentTimeMillis() - 12*3600*1000L;
		long eod = System.currentTimeMillis() + 12*3600*1000L;
		atp = new AstrometryPanelTest(site, sod, eod);
	
		topPanel.add(atp, BorderLayout.CENTER);
		
		add(topPanel, BorderLayout.CENTER);
	
	}
	
	
	private void createMenus() {
	
		// TARGETS
		targetMenu = new JMenu("Targets");
	
		JMenuItem loadItem = new JMenuItem("Load catalog...");
		targetMenu.add(loadItem);
		
		JMenuItem userDefinedItem = new JMenuItem("Input RA_Dec...");
		targetMenu.add(userDefinedItem);

		JCheckBoxMenuItem moonItem = new JCheckBoxMenuItem("Moon");
		moonItem.addActionListener(new MoonTargetListener());
		targetMenu.add(moonItem);
		
		menus.add(targetMenu);
	
	
	
	
	}
	
	private class MoonTargetListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
			BasicSite site = new BasicSite("here", Math.toRadians(28.0), Math.toRadians(-17.9));
			LunarCalculator moon = new LunarCalculator(site);
					
			int n = atp.addSeries("Moon", moon);
			atp.buildSeries(n);
			} catch (Exception me) {
				me.printStackTrace();			
			}
		}
		
	}
	
}
