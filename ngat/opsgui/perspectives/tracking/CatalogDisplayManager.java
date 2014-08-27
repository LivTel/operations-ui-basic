/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import ngat.astrometry.AstroCatalog;
import ngat.phase2.XExtraSolarTarget;

/** Manages catalog display information and provides access to the catalogs.
 * @author eng
 *
 */
public class CatalogDisplayManager {

	/** Maps a catlog ID to its display.*/
	private Map<String, CatalogDisplay> catalogs;

	/** List of catalogs.*/
	private List<CatalogDisplay> catalogList;
	
	/**
	 * 
	 */
	public CatalogDisplayManager() {
		catalogs = new HashMap<String, CatalogDisplay>();
		catalogList = new Vector<CatalogDisplay>();
	}
	
	public void addCatalogDisplay(CatalogDisplay display) throws Exception {
		if (display == null)
			throw new IllegalArgumentException("No display set");
		
		AstroCatalog catalog = display.getCatalog();
		if (catalog == null)
			throw new IllegalArgumentException("Display does not contain a catalog");
		
		String displayId = catalog.getCatalogName();
		if (displayId == null || displayId.equals(""))
			throw new IllegalArgumentException("Catalog ID not set");
	
		if (catalogs.containsKey(displayId)) {
			// updating an existing catalog
			
			// somehow we need to remove a display with that ID
			CatalogDisplay old = getCatalogDisplay(displayId);
			catalogList.remove(old);
			catalogList.add(display);
			catalogs.put(displayId, display);
		} else {
			// add to map and list
			catalogs.put(displayId, display);
			catalogList.add(display);
		}
	}
	
	public CatalogDisplay getCatalogDisplay(String displayId) {
		
		if (!catalogs.containsKey(displayId))
			return null;
		
		return catalogs.get(displayId);
					
	}
	
	public List<CatalogDisplay> listCatalogDisplays() {
		return catalogList;
	}
	
	public void addTargetToCatalog(String catologId, XExtraSolarTarget target) throws Exception {		
		AstroCatalog catalog = getCatalog(catologId);		
		catalog.addTarget(target);		
	}
	
	public void removeTargetFromCatalog(String catologId, String targetId) throws Exception {
		AstroCatalog catalog = getCatalog(catologId);
		catalog.removeTarget(targetId);
	}
	
	private AstroCatalog getCatalog(String catalogId) throws Exception {		
		CatalogDisplay display = catalogs.get(catalogId);
		if (display == null)
			throw new IllegalArgumentException("Unknown catalog: "+catalogId);
		return display.getCatalog();
	}
	
}
