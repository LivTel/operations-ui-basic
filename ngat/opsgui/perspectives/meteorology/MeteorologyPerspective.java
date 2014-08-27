/**
 * 
 */
package ngat.opsgui.perspectives.meteorology;

import java.awt.BorderLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.rmi.RemoteException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;

import ngat.ems.MeteorologyStatus;
import ngat.ems.MeteorologyStatusUpdateListener;
import ngat.ems.SkyModelExtinctionUpdate;
import ngat.ems.SkyModelSeeingUpdate;
import ngat.ems.SkyModelUpdateListener;
import ngat.ems.WmsStatus;
import ngat.opsgui.base.Perspective;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.test.SeeingPanelTest;
import ngat.opsgui.xcomp.SeeingPanel2;
import ngat.tcm.TelescopeEnvironmentStatus;
import ngat.tcm.TelescopeStatus;
import ngat.tcm.TelescopeStatusUpdateListener;

/**
 * @author eng
 * 
 */
public class MeteorologyPerspective extends Perspective implements
		MeteorologyStatusUpdateListener, TelescopeStatusUpdateListener,
		SkyModelUpdateListener, ServiceAvailabilityListener {

	/** Scheduling master tab panel. */
	private JTabbedPane meteorologyPane;

	private WmsDataDisplayPanel wmsDataDisplayPanel;

	private MeteorologyTrendsPanel trendsPanel;

	private MeteorologyThumbnailsPanel thumbsPanel;

	//private ChartPanel skypanel;

	//private SeeingPanelTest seePanel;
	
	private SeeingPanel2 seePanel2;

	public MeteorologyPerspective(JFrame frame) {
		super(frame);
		
		perspectiveName = "M";

		setLayout(new BorderLayout());

		meteorologyPane = new JTabbedPane(SwingConstants.TOP,
				SwingConstants.HORIZONTAL);

		// WMS
		wmsDataDisplayPanel = new WmsDataDisplayPanel();
		meteorologyPane.addTab("Data", wmsDataDisplayPanel);

		// Trends
		trendsPanel = new MeteorologyTrendsPanel();
		meteorologyPane.addTab("Trends", trendsPanel);

		// Thumbs
		thumbsPanel = new MeteorologyThumbnailsPanel();
		meteorologyPane.addTab("Thumbs", thumbsPanel);

		// Stats
		meteorologyPane.addTab("Stats", createFillerPanel());

		// Sky
		//meteorologyPane.addTab("Sky", skypanel);

		// Seeing stats
		//seePanel = new SeeingPanelTest();
		//meteorologyPane.addTab("Seeing", seePanel);

		seePanel2 = new SeeingPanel2();
		meteorologyPane.addTab("Seeing", seePanel2);
		
		// MAYBE put in super constructor ????
		createMenus();

		add(meteorologyPane, BorderLayout.CENTER);

	}

	/** Create menus. */

	private void createMenus() {

	}

	private JPanel createFillerPanel() {
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());

		try {
			BufferedImage myPicture = ImageIO.read(new File(
					"/home/eng/rcsgui/icons/filler.jpg"));
			JLabel picLabel = new JLabel(new ImageIcon(myPicture));
			p.add(picLabel);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return p;
	}

	@Override
	public void meteorologyStatusUpdate(MeteorologyStatus status)
			throws RemoteException {

		System.err
				.println("MeteoPerspective: update received, updating DP and CP... ");

		if (status instanceof WmsStatus) {
			final WmsStatus wmsStatus = (WmsStatus) status;
			final WmsDataDisplayPanel fwmsDataDisplayPanel = wmsDataDisplayPanel;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					fwmsDataDisplayPanel.updateData(wmsStatus);
				}
			});

		}

		final MeteorologyStatus fstatus = status;
		final MeteorologyThumbnailsPanel fthumbsPanel = thumbsPanel;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				fthumbsPanel.updateData(fstatus);
			}
		});

		final MeteorologyTrendsPanel ftrendsPanel = trendsPanel;
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ftrendsPanel.updateData(fstatus);
			}
		});
	}

	@Override
	public void telescopeNetworkFailure(long arg0, String arg1)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	@Override
	public void telescopeStatusUpdate(TelescopeStatus status)
			throws RemoteException {

		// TODO WE ARE ONLY INTERESTED IN TEL_ENV
		System.err.println("MP: RECEIVED STATUS: "
				+ status.getClass().getName().toUpperCase());
		if (status instanceof TelescopeEnvironmentStatus) {
			TelescopeEnvironmentStatus envStatus = (TelescopeEnvironmentStatus) status;
			System.err.println("MP: Received status: " + envStatus);
			// update a graph or something
			final TelescopeEnvironmentStatus fstatus = (TelescopeEnvironmentStatus) status;
			final MeteorologyTrendsPanel ftrendsPanel = trendsPanel;
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					ftrendsPanel.updateEnvironmentData(fstatus);
				}
			});
		}

	}

	@Override
	public void serviceAvailable(String serviceName, long time,
			boolean available) {
		

	}

	@Override
	public void extinctionUpdated(long time, double extinction)
			throws RemoteException {
		
		final long ftime = time;
		final double fext = extinction;
		final SeeingPanel2 fseePanel2 = seePanel2;
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					SkyModelExtinctionUpdate sme = new SkyModelExtinctionUpdate(ftime, fext);					
					fseePanel2.photomUpdate(sme);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	@Override
	public void seeingUpdated(long time, double raw, double corrected,
			double prediction, double elevation, double azimuth, double wavelength, boolean std, String src, String tgt) throws RemoteException {

		//final SeeingPanelTest fseePanel = seePanel;
		final SeeingPanel2 fseePanel2 = seePanel2;
		final long ftime = time;
		final double fraw = raw;
		final double fcorr = corrected;
		final double fpred = prediction;
		final double felev = elevation;
		final double fazm = azimuth;
		final double fwav = wavelength;
		final boolean fstd = std;
		final String fsrc = src;
		final String ftgt = tgt;
		/*SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					// TODO
					fseePanel.seeingUpdated(ftime, fraw, fcorr, fpred, felev, fazm, fwav, fstd, fsrc, ftgt); // ADD NEW PARAMS
				} catch (RemoteException e) {					
					e.printStackTrace();
				}
			}
		});*/
		
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				try {
					// TODO
					SkyModelSeeingUpdate smu = new SkyModelSeeingUpdate(ftime, fraw, fcorr, fpred, fstd, fsrc);// ADD NEW PARAMS
					smu.setElevation(felev);
					smu.setAzimuth(fazm);
					smu.setWavelength(fwav);
					smu.setTargetName(ftgt);
					fseePanel2.seeingUpdate(smu);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		
		
	}

}
