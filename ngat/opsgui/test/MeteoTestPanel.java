/**
 * 
 */
package ngat.opsgui.test;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.rmi.RemoteException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import ngat.ems.CloudStatus;
import ngat.ems.MeteorologyStatus;
import ngat.ems.MeteorologyStatusUpdateListener;
import ngat.ems.WmsStatus;
import ngat.opsgui.services.MeteorologyStatusHandlerService;
import ngat.opsgui.services.ServiceAvailabilityListener;
import ngat.opsgui.services.ServiceManager;

/**
 * @author eng
 * 
 */
public class MeteoTestPanel extends JPanel implements ServiceAvailabilityListener, MeteorologyStatusUpdateListener {

	private MeteoGraphPanel metHum;
	private MeteoGraphPanel metPress;
	private MeteoGraphPanel metWspeed;
	private MeteoGraphPanel metMoist;
	private MeteoGraphPanel metDew;
	private MeteoGraphPanel metTemp;
	
	private MeteoGraphPanel bcsCloud;
	
	private MeteoGraphPanel thmetHum;
	private MeteoGraphPanel thmetPress;
	private MeteoGraphPanel thmetWspeed;
	private MeteoGraphPanel thmetMoist;
	private MeteoGraphPanel thmetDew;
	private MeteoGraphPanel thmetTemp;

	private MeteoGraphPanel thbcsCloud;
	
	private JTextField humfield;
	private JTextField pressfield;
	private JTextField wspeedfield;
	private JTextField moistfield;
	private JTextField dewfield;
	private JTextField tempfield;
	private JTextField cloudField;
	
	/**
	 * 
	 */
	public MeteoTestPanel() {
		super(true);
		metHum = new MeteoGraphPanel("Humidity", 0.0, 1.0, MeteoGraphPanel.FULL_SIZE);
		metPress = new MeteoGraphPanel("Pressure", 700.0, 800.0, MeteoGraphPanel.FULL_SIZE);
		metWspeed = new MeteoGraphPanel("Wind speed", 0.0, 25.0, MeteoGraphPanel.FULL_SIZE);
		metMoist = new MeteoGraphPanel("Moisture", 0.0, 1.0, MeteoGraphPanel.FULL_SIZE);
		metDew = new MeteoGraphPanel("Dewpoint", -20.0, 20.0, MeteoGraphPanel.FULL_SIZE);
		metTemp = new MeteoGraphPanel("Temp", -20.0, 20.0, MeteoGraphPanel.FULL_SIZE);
		bcsCloud = new MeteoGraphPanel("Cloud", -40.0, -20.0, MeteoGraphPanel.FULL_SIZE);
		
		thmetHum = new MeteoGraphPanel("Humidity", 0.0, 1.0, MeteoGraphPanel.THUMBNAIL);
		thmetPress = new MeteoGraphPanel("Pressure", 700.0, 800.0, MeteoGraphPanel.THUMBNAIL);
		thmetWspeed = new MeteoGraphPanel("Wind speed", 0.0, 25.0, MeteoGraphPanel.THUMBNAIL);
		thmetMoist = new MeteoGraphPanel("Moisture", 0.0, 1.0, MeteoGraphPanel.THUMBNAIL);
		thmetDew = new MeteoGraphPanel("Dewpoint", -20.0, 20.0, MeteoGraphPanel.THUMBNAIL);
		thmetTemp = new MeteoGraphPanel("Temp", -20.0, 20.0, MeteoGraphPanel.THUMBNAIL);
		thbcsCloud = new MeteoGraphPanel("Cloud", -40.0, -20.0, MeteoGraphPanel.THUMBNAIL);
		
		JPanel thumbs = new JPanel(true);
		thumbs.setLayout(new GridLayout(2,4));
		thumbs.add(thmetHum);
		thumbs.add(thmetPress );
		thumbs.add(thmetWspeed);
		thumbs.add(thmetMoist);
		thumbs.add(thmetDew);
		thumbs.add(thmetTemp);
		thumbs.add(thbcsCloud);
		
		JPanel data = createDataPanel();
		
		JTabbedPane tabs = new JTabbedPane();
		tabs.addTab("Humidity", metHum);
		tabs.addTab("Pressure", metPress);
		tabs.addTab("Wind Speed", metWspeed);
		tabs.addTab("Moisture", metMoist);
		tabs.addTab("Temperature", metTemp);
		tabs.addTab("Dewpoint", metDew);
		tabs.addTab("Cloud", bcsCloud);
		
		tabs.addTab("Thumbs", thumbs);
		tabs.add("Information", data);
		
		add(tabs);
		
	}
	
	private JPanel createDataPanel() {
		JPanel data = new JPanel(true);
		data.setLayout(new GridLayout(3,4));
		humfield = new JTextField(8);
		humfield.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Humidity"));
		data.add(humfield);
		pressfield = new JTextField(8);
		pressfield.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Pressure"));
		data.add(pressfield);
		wspeedfield = new JTextField(8);
		wspeedfield.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Wind Speed"));
		data.add(wspeedfield);
		moistfield = new JTextField(8);
		moistfield.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Moisture"));
		data.add(moistfield);
		dewfield = new JTextField(8);
		dewfield.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Dewpoint"));
		data.add(dewfield);
		
		tempfield = new JTextField(8);
		tempfield.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Temperature"));
		data.add(tempfield);
		
		cloudField = new JTextField(8);
		cloudField.setPreferredSize(new Dimension(100, 20));
		data.add(new JLabel("Cloud"));
		data.add(cloudField);
		
		return data;
	
	}

	@Override
	public void meteorologyStatusUpdate(MeteorologyStatus status) throws RemoteException {
		
		if (status instanceof WmsStatus) {
			WmsStatus wms = (WmsStatus) status;
			long time = wms.getStatusTimeStamp();
			metHum.updateValue(time, wms.getHumidity());
			metPress.updateValue(time, wms.getPressure());
			metWspeed.updateValue(time, wms.getWindSpeed());
			metMoist.updateValue(time, wms.getMoistureFraction());
			metDew.updateValue(time, wms.getDewPointTemperature());
			metTemp.updateValue(time, wms.getExtTemperature());
			

			thmetHum.updateValue(time, wms.getHumidity());
			thmetPress.updateValue(time, wms.getPressure());
			thmetWspeed.updateValue(time, wms.getWindSpeed());
			thmetMoist.updateValue(time, wms.getMoistureFraction());
			thmetDew.updateValue(time, wms.getDewPointTemperature());
			thmetTemp.updateValue(time, wms.getExtTemperature());
		
			
			humfield.setText(String.format("%4.2f", wms.getHumidity()));
			pressfield.setText(String.format("%4.2f", wms.getPressure()));
			wspeedfield.setText(String.format("%4.2f", wms.getWindSpeed()));
			moistfield .setText(String.format("%4.2f", wms.getMoistureFraction()));
			dewfield .setText(String.format("%4.2f", wms.getDewPointTemperature()));
			tempfield .setText(String.format("%4.2f", wms.getExtTemperature()));
			
		} else if (status instanceof CloudStatus) {
			CloudStatus cloud = (CloudStatus) status;
			long time = cloud.getStatusTimeStamp();
			System.err.println("MTP::Received status: "+status);
			bcsCloud.updateValue(time, cloud.getSkyMinusAmb());
			thbcsCloud.updateValue(time, cloud.getSkyMinusAmb());
			cloudField.setText(String.format("%4.2f",cloud.getSkyMinusAmb()));
		}
			
			
	}

	@Override
	public void serviceAvailable(String serviceName, long time, boolean available) {
		if (available)
			return;
		humfield.setText("N/A");
		pressfield.setText("N/A");
		wspeedfield.setText("N/A");
		moistfield .setText("N/A");
		dewfield .setText("N/A");
		tempfield .setText("N/A");
		
	}
	
	public static void main(String args[]) {
		
		MeteoTestPanel test = new MeteoTestPanel();
		
		JFrame f = new JFrame("Meteo Test");
		f.getContentPane().add(test);
		f.pack();
		f.setVisible(true);
		
		try {
		ServiceManager svcMgr = new ServiceManager();
		MeteorologyStatusHandlerService meteo = new MeteorologyStatusHandlerService();
		meteo.setMspUrl("rmi://ltsim1/Meteorology");
		meteo.setMsaUrl("rmi://ltsim1/MeteorologyGateway");
		meteo.setLookBackTime(7200 * 1000L);
		//svcMgr.addService(meteo);
		
		meteo.addListener(test);
		
		try {Thread.sleep(10000);} catch (Exception e) {}
		//svcMgr.startService(meteo, 20000L, 2000L);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
