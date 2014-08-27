/**
 * 
 */
package ngat.opsgui.perspectives.tracking;

import ngat.astrometry.AstroCatalog;

/** Contains information about the display of and access to a catalog.
 * @author eng
 *
 */
public class CatalogDisplay {

	private AstroCatalog catalog;
	
	private CatalogDisplayDescriptor displayDescriptor;

	/**
	 * @param catalog
	 * @param displayDescriptor
	 */
	public CatalogDisplay(AstroCatalog catalog, CatalogDisplayDescriptor displayDescriptor) {
		super();
		this.catalog = catalog;
		this.displayDescriptor = displayDescriptor;
	}

	/**
	 * @return the catalog
	 */
	public AstroCatalog getCatalog() {
		return catalog;
	}

	/**
	 * @param catalog the catalog to set
	 */
	public void setCatalog(AstroCatalog catalog) {
		this.catalog = catalog;
	}

	/**
	 * @return the displayDescriptor
	 */
	public CatalogDisplayDescriptor getDisplayDescriptor() {
		return displayDescriptor;
	}

	/**
	 * @param displayDescriptor the displayDescriptor to set
	 */
	public void setDisplayDescriptor(CatalogDisplayDescriptor displayDescriptor) {
		this.displayDescriptor = displayDescriptor;
	}


}
