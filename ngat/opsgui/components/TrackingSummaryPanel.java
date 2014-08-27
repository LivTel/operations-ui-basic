/**
 * 
 */
package ngat.opsgui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

import ngat.astrometry.ISite;
import ngat.opsgui.perspectives.tracking.AitoffPlot;

/**
 * @author eng
 *
 */
public class TrackingSummaryPanel extends JPanel {

	public static final Dimension TRACKING_PANEL_SIZE = new Dimension(150, 150);
	
	private ISite site;
	private AitoffPlot aitoff;
	
	/**
	 * @param title
	 */
	public TrackingSummaryPanel(ISite site) {
		super();
		this.site = site;
		createPanel();
	}

	/* (non-Javadoc)
	 * @see ngat.opsgui.components.SummaryPane#createPanel()
	 */
	public void createPanel() {
		setPreferredSize(TRACKING_PANEL_SIZE);
		setLayout(new BorderLayout());		
		setBorder(BorderFactory.createTitledBorder("Tracking"));
		aitoff = new AitoffPlot();
		aitoff.showTracks(site);
		aitoff.setShowAltAzGrid(true);
		aitoff.setShowSkyB(true);
		aitoff.setMainFont(new Font("helvetica", Font.BOLD, 8));
		aitoff.setSimTimeFont(new Font("helvetica", Font.ITALIC, 8));
		aitoff.setShowTime(false);
		add(aitoff, BorderLayout.CENTER);
	}

}
