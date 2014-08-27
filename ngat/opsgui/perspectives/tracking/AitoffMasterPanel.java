/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import ngat.astrometry.AstroCatalog;
import ngat.astrometry.ISite;
import ngat.opsgui.base.ColorIndex;
import ngat.opsgui.base.Resources;
import ngat.opsgui.util.StateColorMap;
import ngat.phase2.ITarget;
import ngat.phase2.XExtraSolarTarget;

/**
 * @author eng
 *
 */
public class AitoffMasterPanel extends JPanel {
	
	private ISite site;
	
	private AitoffPlot aitoff;
	
	private LegendPanel legend;
	
	private ColorIndex colorIndexPanel;

	/**
	 * 
	 */
	public AitoffMasterPanel(AitoffPlot aitoff) {
		super(true);
		this.aitoff = aitoff;
		
		setLayout(new BorderLayout());
		
		add(aitoff, BorderLayout.CENTER);
	
		StateColorMap colorMap = Resources.getColorMap("sky.brightness.colors");
	
		colorIndexPanel = new ColorIndex(colorMap, ColorIndex.VERTICAL);
		colorIndexPanel.setPreferredSize(new Dimension(80, 400));
		add(colorIndexPanel, BorderLayout.WEST);
		
		legend = new LegendPanel();
		legend.setPreferredSize(new Dimension(80, 400));
		add(legend, BorderLayout.EAST);	
		
	}
	
	public void addCatalogDisplay(CatalogDisplay display) throws Exception {
		aitoff.getCatalogDisplayManager().addCatalogDisplay(display);
		legend.addLegendEntry(display);
		aitoff.repaint();
		legend.repaint();
	}
	
	public void updateCatalogDisplay(CatalogDisplay display) throws Exception {
		aitoff.getCatalogDisplayManager().addCatalogDisplay(display);
	}
	
	public void addTargetToCatalog(String catName, ITarget target) throws Exception {
		CatalogDisplay display = aitoff.getCatalogDisplayManager().getCatalogDisplay(catName);
		AstroCatalog catalog = display.getCatalog();
		catalog.addTarget((XExtraSolarTarget)target);
		System.err.println("AMP::Added Target: "+target.getName()+" to catalog: "+catName+" now contains: "+catalog.size()+" targets");
	}
	
	
	
}
