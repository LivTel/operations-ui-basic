/**
 * 
 */
package ngat.opsgui.base;

import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JComponent;

import org.jdom.Element;

import ngat.util.XmlConfigurable;

/**
 * Base component for displaying data.
 * 
 * @author eng
 * 
 */
public class Perspective extends JComponent implements XmlConfigurable {

	/** This Perspective's menus. */
	protected List<JMenu> menus;
	
	/** The Services required by this perspective.*/
	protected List<String>servicesRequired;

	/** Icon to display in sidebar/tabpanel. */
	protected Icon sidebarIcon;

	/** The name of this perspective. */
	protected String perspectiveName;

	/** Description of the perspective, used in tooltips etc. */
	protected String perspectiveDescription;

	/**
	 * Create a Perspective to run within the supplied frame.
	 * 
	 * @param frame
	 *            The enclosing frame.
	 */
	public Perspective(JFrame frame) {
		menus = new Vector<JMenu>();
		servicesRequired = new Vector<String>();
	}
	
	/**
	 * @return A list of menus provided by this perspective.
	 */
	public List<JMenu> listMenus() {
		return menus;
	}

	/**
	 * @return A list of required services.
	 */
	public List<String> listServicesRequired() {
		return servicesRequired;
	}
	
	/**
	 * @return the sidebarIcon
	 */
	public Icon getSidebarIcon() {
		return sidebarIcon;
	}

	/**
	 * @param sidebarIcon
	 *            the sidebarIcon to set
	 */
	public void setSidebarIcon(Icon sidebarIcon) {
		this.sidebarIcon = sidebarIcon;
	}

	/**
	 * @return the perspectiveName
	 */
	public String getPerspectiveName() {
		return perspectiveName;
	}

	/**
	 * @param perspectiveName
	 *            the perspectiveName to set
	 */
	public void setPerspectiveName(String perspectiveName) {
		this.perspectiveName = perspectiveName;
	}

	/**
	 * @return the perspectiveDescription
	 */
	public String getPerspectiveDescription() {
		return perspectiveDescription;
	}

	/**
	 * @param perspectiveDescription
	 *            the perspectiveDescription to set
	 */
	public void setPerspectiveDescription(String perspectiveDescription) {
		this.perspectiveDescription = perspectiveDescription;
	}

	@Override
	public void configure(Element node) throws Exception {
		// name, desc, etc
		// Build up a list of service-provider names
		List lsvc = node.getChildren("service");
		for (int il = 0; il < lsvc.size(); il++) {
			Element snode = (Element)lsvc.get(il);
			String svcName = snode.getTextTrim().toUpperCase();
			servicesRequired.add(svcName);
		}
		
	}

}
