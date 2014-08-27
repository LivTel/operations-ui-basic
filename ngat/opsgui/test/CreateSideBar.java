/**
 * 
 */
package ngat.opsgui.test;

import java.rmi.Naming;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.opsgui.base.GuiSideBar;
import ngat.opsgui.base.Resources;

/**
 * @author eng
 * 
 */
public class CreateSideBar {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Resources.setDefaults(args[0]);

			String iregHost = args[1];
			InstrumentRegistry ireg = (InstrumentRegistry) Naming.lookup("rmi://" + iregHost + "/InstrumentRegistry");
			List ilist = ireg.listInstruments();

			List<InstrumentDescriptor> instruments = new Vector<InstrumentDescriptor>();
			Iterator ii = ilist.iterator();
			while (ii.hasNext()) {
				instruments.add((InstrumentDescriptor)ii.next());
			}
			
			GuiSideBar test = new GuiSideBar(instruments);
			JFrame f = new JFrame("GUI Sidebar Layout Test");

			f.getContentPane().add(test);
			f.pack();
			f.setVisible(true);

			System.err.println("Panel=" + test.getSize().width + "," + test.getSize().height);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
