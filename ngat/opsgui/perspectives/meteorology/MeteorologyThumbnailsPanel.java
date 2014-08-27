/**
 * 
 */
package ngat.opsgui.perspectives.meteorology;

import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.JPanel;

import ngat.ems.MeteorologyStatus;
import ngat.ems.WmsStatus;
import ngat.ems.DustStatus;
import ngat.ems.CloudStatus;
import ngat.opsgui.chart.TimeAxisChart;

/**
 * @author eng
 * 
 */
public class MeteorologyThumbnailsPanel extends JPanel {

	private TimeAxisChart humidityChart;
	private TimeAxisChart moistureChart;
	private TimeAxisChart temperatureChart;
	private TimeAxisChart windSpeedChart;
	private TimeAxisChart dewpointChart;
	private TimeAxisChart solarChart;
	private TimeAxisChart pressureChart;
	private TimeAxisChart cloudChart;
	private TimeAxisChart dustChart;

	/**
	 * 
	 */
	public MeteorologyThumbnailsPanel() {
		super();
		setLayout(new GridLayout(3, 5));
		humidityChart = addThumbPanel("Humidity", 0.0, 1.0);
		moistureChart = addThumbPanel("Moisture", 0.0, 1.0);
		temperatureChart = addThumbPanel("Temperature", -20.0, 35.0);
		windSpeedChart = addThumbPanel("Wind Speed", 0.0, 35.0);
		dewpointChart = addThumbPanel("Dewpoint", 0.0, 1.0);
		solarChart = addThumbPanel("Solar", 0.0, 1000.0);
		pressureChart = addThumbPanel("Pressure", 700.0, 850.0);
		cloudChart = addThumbPanel("Cloud", -50.0, 0.0);
		dustChart = addThumbPanel("Dust", 0.0, 100.0);
	}

	private TimeAxisChart addThumbPanel(String title, double lo, double hi) {

		TimeAxisChart tac = new TimeAxisChart(title);
		tac.setTimeAxisRange(8*3600 * 1000L);
		tac.setYAxisStart(lo);
		tac.setYAxisEnd(hi);

		try {
			tac.createPlot("DATA", title, 0, 0, Color.green, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JPanel panel = tac.createChartPanel();
		add(panel);
		return tac;
	}
	
	public void updateData(MeteorologyStatus status) {
		System.err.println("Meteo: CP: update received: "+status);
	
		if (status instanceof WmsStatus) {
		    WmsStatus wmsStatus = (WmsStatus)status;
		    humidityChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getHumidity());
		    moistureChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getMoistureFraction());
		    temperatureChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getExtTemperature());
		    windSpeedChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getWindSpeed());
		    dewpointChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getDewPointTemperature());
		    solarChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getLightLevel());
		    pressureChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getPressure());
		} else if
		      (status instanceof CloudStatus) {
		    CloudStatus cloudStatus = (CloudStatus)status;
		    cloudChart.addData("DATA", cloudStatus.getStatusTimeStamp(), cloudStatus.getSkyMinusAmb());
		} else if
		      (status instanceof DustStatus) {
		    DustStatus dustStatus = (DustStatus)status;
		    dustChart.addData("DATA", dustStatus.getStatusTimeStamp(), dustStatus.getDust());
		}
	}
	
}
