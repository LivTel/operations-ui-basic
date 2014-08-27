/**
 * 
 */
package ngat.opsgui.perspectives.meteorology;

import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import ngat.ems.CloudStatus;
import ngat.ems.DustStatus;
import ngat.ems.MeteorologyStatus;
import ngat.ems.WmsStatus;
import ngat.opsgui.chart.TimeAxisChart;
import ngat.tcm.TelescopeEnvironmentStatus;

/**
 * @author eng
 * 
 */
public class MeteorologyTrendsPanel extends JPanel {

	private JTabbedPane tabs;

	private TimeAxisChart humidityChart;
	private TimeAxisChart moistureChart;
	private TimeAxisChart temperatureChart;
	private TimeAxisChart windSpeedChart;
	private TimeAxisChart dewpointChart;
	private TimeAxisChart solarChart;
	private TimeAxisChart pressureChart;
	private TimeAxisChart cloudChart;
	private TimeAxisChart dustChart;

	// TEMP should be in telscope perspectie ??
	private TimeAxisChart oilChart;
	
	/**
	 * 
	 */
	public MeteorologyTrendsPanel() {
		super();

		setLayout(new BorderLayout());

		tabs = new JTabbedPane(SwingConstants.LEFT, SwingConstants.VERTICAL);

		humidityChart = addTrendPanel("Humidity", 0.0, 1.0, 0.8, 0.7);
		moistureChart = addTrendPanel("Moisture", 0.0, 1.0, 0.1, 0.09);
		temperatureChart = addTrendPanel("Temperature", -20.0, 35.0, -0.05, 0.05);
		windSpeedChart = addTrendPanel("Wind Speed", 0.0, 35.0, 15.0, 12.0);
		dewpointChart = addTrendPanel("Dewpoint", 0.0, 1.0);
		solarChart = addTrendPanel("Solar", 0.0, 1000.0);
		pressureChart = addTrendPanel("Pressure", 700.0, 850.0);
		cloudChart = addTrendPanel("Cloud", -50.0, 0.0, -15.0, -10.0);
		dustChart = addTrendPanel("Dust", 0.0, 100.0, 60.0, 20.0);

		oilChart = addTrendPanel("Oil", -1.0, 40.0);
		add(tabs, BorderLayout.CENTER);

	}
	
	private TimeAxisChart addTrendPanel(String title, double lo, double hi, double bad, double good) {
		TimeAxisChart tac = addTrendPanel(title, lo, hi);
		tac.addLine(bad, "alert", Color.red);
		tac.addLine(good, "clear", Color.green);
		return tac;
	}

	private TimeAxisChart addTrendPanel(String title, double lo, double hi) {

		TimeAxisChart tac = new TimeAxisChart(title);
		tac.setTimeAxisRange(6 * 3600 * 1000L);
		tac.setYAxisStart(lo);
		tac.setYAxisEnd(hi);

		try {
			tac.createPlot("DATA", title, 0, 0, Color.green, true, false);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JPanel panel = tac.createChartPanel();
		tabs.addTab(title, panel);
		return tac;
	}

	public void updateData(MeteorologyStatus status) {
		if (status instanceof WmsStatus) {
			WmsStatus wmsStatus = (WmsStatus) status;
			System.err.println("Meteo: CP: update received: " + wmsStatus);
			humidityChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getHumidity());
			moistureChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getMoistureFraction());
			temperatureChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getExtTemperature());
			windSpeedChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getWindSpeed());
			dewpointChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getDewPointTemperature());
			solarChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getLightLevel());
			pressureChart.addData("DATA", wmsStatus.getStatusTimeStamp(), wmsStatus.getPressure());
		} else if (status instanceof CloudStatus) {
			CloudStatus cloudStatus = (CloudStatus) status;
			cloudChart.addData("DATA", cloudStatus.getStatusTimeStamp(), cloudStatus.getSkyMinusAmb());
		} else if (status instanceof DustStatus) {
			DustStatus dustStatus = (DustStatus) status;
			dustChart.addData("DATA", dustStatus.getStatusTimeStamp(), dustStatus.getDust());
		}
	}

	public void updateEnvironmentData(TelescopeEnvironmentStatus status) {
		oilChart.addData("DATA", status.getStatusTimeStamp(), status.getOilTemperature());
	}
	
}
