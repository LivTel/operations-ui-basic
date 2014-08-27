/**
 * 
 */
package ngat.opsgui.perspectives.meteorology;

import java.awt.Color;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTextField;
import ngat.ems.WmsStatus;
import ngat.net.cil.tcs.TcsStatusPacket;
import ngat.opsgui.base.ComponentFactory;
import ngat.opsgui.base.Resources;
import ngat.opsgui.components.LinePanel;
import ngat.opsgui.util.DataField;
import ngat.opsgui.util.StateColorMap;
import ngat.opsgui.util.StateField;

/**
 * @author eng
 *
 */
public class WmsDataDisplayPanel extends JPanel {

	private static Color RAIN_CLEAR_COLOR = Color.green;
	
	private static Color RAIN_ALERT_COLOR = Color.red;
	
	private static Color WMS_CLEAR_COLOR = Color.green;
	
	private static Color WMS_ALERT_COLOR = Color.red;
	
	private StateColorMap rainColorMap;
	
	private StateColorMap wmsColorMap;
	
	private DataField humidityField;
	
	private DataField temperatureField;
	
	private DataField windSpeedField;
	
	private DataField windDirectionField;
	
	private DataField solarField;
	
	private DataField pressureField;
	
	private DataField moistureField;
	
	private StateField rainField;
	
	private StateField wmsStateField;
	
	
	
	/**
	 * rainField = ComponentFactory.makeStateField(8, rainColorMap);
		add(createLinePanel("Rain", "%", rainField));
		
	 */
	public WmsDataDisplayPanel() {
		super();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		rainColorMap = new StateColorMap(Color.gray, "UNKNOWN");
		rainColorMap.addColorLabel(TcsStatusPacket.RAIN_CLEAR, RAIN_CLEAR_COLOR, "CLEAR");
		rainColorMap.addColorLabel(TcsStatusPacket.RAIN_ALERT, RAIN_ALERT_COLOR, "ALERT");
		
		wmsColorMap = new StateColorMap(Color.gray, "UNKNOWN");
		wmsColorMap.addColorLabel(TcsStatusPacket.STATE_SUSPENDED, WMS_ALERT_COLOR, "ALERT");
		wmsColorMap.addColorLabel(TcsStatusPacket.STATE_OKAY, WMS_CLEAR_COLOR, "CLEAR");
		
		rainField = ComponentFactory.makeStateField(8, rainColorMap);
		add(createLinePanel("Rain", "%", rainField));
		
		wmsStateField = ComponentFactory.makeStateField(8, wmsColorMap);
		add(createLinePanel("WMS", "%", wmsStateField));
		
		moistureField= ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Moisture", "%", moistureField));
		
		humidityField = ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Humidity", "%", humidityField));
		
		temperatureField = ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Temp", "C", temperatureField));
		
		solarField = ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Solar", "L", solarField));
		
		pressureField = ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Pressure", "kPa", pressureField));
		
		
		windSpeedField = ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Wind Speed", "ms", windSpeedField));
		
		
		windDirectionField = ComponentFactory.makeDataField(8, "%4.2f");
		add(createLinePanel("Wind Dirn", "d", windDirectionField));
		
		
	}

	private LinePanel createLinePanel(String title, String units, JTextField field) {
		
		LinePanel line = ComponentFactory.makeLinePanel();
		line.setBorder(BorderFactory.createLineBorder(Color.blue));
		
		line.add(ComponentFactory.makeLabel(title)); // Measurement name
		line.add(ComponentFactory.makeSmallLabel("("+units+")")); // units
		line.add(field); // value
		line.add(ComponentFactory.makeSmallButton(null)); // state indicator
		line.add(ComponentFactory.makeIconButton(Resources.getIcon("trend.graph.icon"))); // trend graphs
		line.add(ComponentFactory.makeIconButton(Resources.getIcon("statistics.graph.icon"))); // histograms
		
		return line;
		
	}


	public void updateData(WmsStatus wmsStatus) {
		System.err.println("Meteo: DP: update received: "+wmsStatus);
		
		humidityField.updateData(wmsStatus.getHumidity());
		temperatureField.updateData(wmsStatus.getExtTemperature());
		windSpeedField.updateData(wmsStatus.getWindSpeed());
		windDirectionField.updateData(wmsStatus.getWindDirn());
		moistureField.updateData(wmsStatus.getMoistureFraction());
		pressureField.updateData(wmsStatus.getPressure());
		rainField.updateState(wmsStatus.getRainState());
		wmsStateField.updateState(wmsStatus.getWmsStatus());
		solarField.updateData(wmsStatus.getLightLevel());
		
	}

}
