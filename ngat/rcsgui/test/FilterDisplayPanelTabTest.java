/**
 * 
 */
package ngat.rcsgui.test;

import java.awt.BorderLayout;
import java.io.File;
import java.rmi.Naming;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import ngat.astrometry.BasicSite;
import ngat.ems.MeteorologyStatusProvider;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.rcs.ers.test.BasicReactiveSystem;
import ngat.tcm.TelescopeStatusProvider;
import ngat.util.XmlConfigurator;

/**
 * @author eng
 *
 */
public class FilterDisplayPanelTabTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			
			TcsStatusPacket.mapCodes();
			
			FilterDisplayPanelTest  test1 = new FilterDisplayPanelTest(args[0]);
			FilterDisplayPanelTest2 test2 = new FilterDisplayPanelTest2(args[0]);

			JFrame f = new JFrame("GUI: Scrolling Filter Table Test: [Axes] [Weather]");
			JTabbedPane tabs = new JTabbedPane();
			tabs.addTab("Axes", test2);
			tabs.addTab("Weather", test1);
			
			f.setLayout(new BorderLayout());
			f.getContentPane().add(tabs);
			f.pack();
			f.setVisible(true);
			
			BasicReactiveSystem ts = new BasicReactiveSystem(new BasicSite("", Math.toRadians(28.0), Math.toRadians(-17.0)));
			XmlConfigurator.use(new File(args[1])).configure(ts);
			
			
			FilterDisplayPanelTest.MyListener ml1 = test1.createListener(); // weather
			FilterDisplayPanelTest2.MyListener ml2 = test2.createListener(); // axes
			System.err.println("Created RSSU");

			
			ts.addReactiveSystemUpdateListener(ml1);
			ts.addReactiveSystemUpdateListener(ml2);
			System.err.println("Created Added fdps as rssls");

			MeteorologyStatusProvider meteo = (MeteorologyStatusProvider) Naming.lookup("rmi://ltsim1/Meteorology");
			meteo.addMeteorologyStatusUpdateListener(ts);
			System.err.println("Linked TS to meteorology");
			
			TelescopeStatusProvider tel = (TelescopeStatusProvider)Naming.lookup("rmi://ltsim1/Telescope");
			tel.addTelescopeStatusUpdateListener(ts);
			System.err.println("Linked TS to telescope");
			
			System.err.println("TS::Starting cache reader...");
			ts.startCacheReader();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		

	}

}
