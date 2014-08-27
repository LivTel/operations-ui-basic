/**
 * 
 */
package ngat.opsgui.test;

import java.io.File;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import ngat.opsgui.login.UiConfig;
import ngat.util.XmlConfigurator;

/**
 * @author eng
 * 
 */
public class ReadUserInfoFromXmlFile {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		UiConfig uic = new UiConfig();

		try {
			File xmlfile = new File(args[0]);

			SAXBuilder builder = new SAXBuilder();
			Document doc = builder.build(xmlfile);
			Element gui = doc.getRootElement();
			
		//	Element gui = root.getChild("gui");
			Element cfg = gui.getChild("config");
			
			uic.configure(cfg);

			
			
			// list the users
			

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
