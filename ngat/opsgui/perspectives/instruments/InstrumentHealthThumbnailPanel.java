/**
 * 
 */
package ngat.opsgui.perspectives.instruments;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentRegistry;
import ngat.icm.InstrumentStatus;
import ngat.opsgui.chart.TimeAxisChart;
import ngat.tcm.AutoguiderActiveStatus;

/** Panel to display various per-instrument health graphs
 * @author eng
 *
 */
public class InstrumentHealthThumbnailPanel extends JPanel {
	
	private Map<String,TimeAxisChart> charts;
	
	private InstrumentRegistry ireg;
	
	/**
	 * 
	 */
	public InstrumentHealthThumbnailPanel(InstrumentRegistry ireg) throws Exception {
		super();
		this.ireg = ireg;
		
		charts = new HashMap<String, TimeAxisChart>();
		
		setLayout(new GridLayout(2, 4));
		
		try {
			List instList = ireg.listInstruments();
			for (int ii = 0; ii < instList.size(); ii++) {
				InstrumentDescriptor id = (InstrumentDescriptor)instList.get(ii);
				String instName = id.getInstrumentName();
				if (instName.equalsIgnoreCase("FRODO"))
				{					
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "RED", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
					createSubChart(tac, "BLUE", -120.0, 50.0, Color.blue);
				}
				else if (instName.equals("FRODO_RED"))
				{
					continue;
				}
				else if (instName.equals("FRODO_BLUE"))
				{
					continue;
				}
				else if(instName.equals("IO:O"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "IO:O", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if (instName.equals("IO:I"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "IO:I", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if (instName.equals("IO:THOR"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "IO:THOR", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if (instName.equals("LIRIC"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "LIRIC", -30.0, 30.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if (instName.equals("LOCI"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "LOCI", -110.0, -10.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if (instName.equalsIgnoreCase("MOPTOP"))
				{					
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "1", -50.0, 30.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
					createSubChart(tac, "2", -50.0, 30.0, Color.blue);
				}
				else if (instName.equals("MOPTOP_1"))
				{
					continue;
				}
				else if (instName.equals("MOPTOP_2"))
				{
					continue;
				}
				else if (instName.equals("RATCAM"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "RATCAM", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if(instName.equals("RINGO3"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "D", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
					createSubChart(tac, "E", -120.0, 50.0, Color.blue);
					createSubChart(tac, "F", -120.0, 50.0, Color.green);
				}
				else if (instName.equals("RISE"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "RISE", -100.0, -35.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
				else if (instName.equals("SPRAT"))
				{
					TimeAxisChart tac = addThumbPanel(id.getInstrumentName(), "SPRAT", -120.0, -50.0, Color.red);
					charts.put(id.getInstrumentName(), tac);
				}
			}
			
			// autoguider
			TimeAxisChart tac = addThumbPanel("AUTOGUIDER", "AUTOGUIDER", -5.0, 5.0, Color.black);
			charts.put("AUTOGUIDER", tac);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TimeAxisChart addThumbPanel(String title, String subtitle, double lo, double hi, Color color) {

		TimeAxisChart tac = new TimeAxisChart(title);
		tac.setTimeAxisRange(1*3600 * 1000L);
		tac.setYAxisStart(lo);
		tac.setYAxisEnd(hi);

		try {
			//tac.createPlot(subtitle, title, 0, 0, color, true, false);
			tac.createPlot(subtitle, "BUTT", 0, 0, color, true, false);
			// createPlot( red, frodo, color.red
		} catch (Exception e) {
			e.printStackTrace();
		}

		JPanel panel = tac.createChartPanel();
		add(panel);
		
		return tac;
	}
	
	private void createSubChart(TimeAxisChart tac, String title, double lo, double hi, Color color) {
		try {
			//tac.createPlot(title, title, 0, 0, color, true, false);
			tac.createPlot(title, "ARSE", 0, 0, color, true, false);
			// createPlot(blue, blue, color.blue
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updateStatus(InstrumentStatus status) {
	
		
		String instName = status.getInstrument().getInstrumentName();
		
		if (! status.isOnline())
			return;
		
		Map statusBlock = status.getStatus();
		
		double temperature = -1.0;
		try {	
			
			TimeAxisChart tac = charts.get(instName);
			
			if (instName.equalsIgnoreCase("RINGO3")) {
				temperature = getTemperature(instName+":D", statusBlock);							
				tac.addData("D", status.getStatusTimeStamp(), temperature-273.15);
				
				temperature = getTemperature(instName+":E", statusBlock);							
				tac.addData("E", status.getStatusTimeStamp(), temperature-273.15);
				
				temperature = getTemperature(instName+":F", statusBlock);
				tac.addData("F", status.getStatusTimeStamp(), temperature-273.15);
		
			} else if				
			(instName.equalsIgnoreCase("FRODO")) {	
				tac = charts.get("FRODO");
				temperature = getTemperature(instName+"_RED", statusBlock);							
				tac.addData("RED", status.getStatusTimeStamp(), temperature-273.15);	
				temperature = getTemperature(instName+"_BLUE", statusBlock);							
				tac.addData("BLUE", status.getStatusTimeStamp(), temperature-273.15);	
			} else {
				temperature = getTemperature(instName, statusBlock);							
				tac.addData(instName, status.getStatusTimeStamp(), temperature-273.15);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void updateAutoguiderStatus(AutoguiderActiveStatus agActiveStatus) {

		// we are only interested in CASS. this may change and we have multiple
		// AGs
		// identified by instName as there agName
		// TODO String instName = agActiveStatus.getAutoguiderName();

		System.err.println("ACMP: Received: " + agActiveStatus);

		if (!agActiveStatus.getAutoguiderName().equalsIgnoreCase("CASS")) {
			System.err.println("AHTP: Ignore status from autoguider: " + agActiveStatus.getAutoguiderName());
			return;
		}
		double temperature = -1.0;
		try {	
			
			TimeAxisChart tac = charts.get("AUTOGUIDER");
			tac.addData("AUTOGUIDER", agActiveStatus.getStatusTimeStamp(), agActiveStatus.getTemperature());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private double getTemperature(String instName, Map statusBlock) throws Exception {
		System.err.println("gettemp: "+instName);
						
		if (instName.equalsIgnoreCase("RATCAM"))
			return ((Double)statusBlock.get("Temperature"));
		else if
		(instName.equalsIgnoreCase("RISE"))
				return ((Double)statusBlock.get("Temperature"));
		else if
		(instName.equalsIgnoreCase("RINGO3:D"))
				return ((Double)statusBlock.get("Temperature.0.0"));
		else if
		(instName.equalsIgnoreCase("RINGO3:E"))
				return ((Double)statusBlock.get("Temperature.1.0"));
		else if
		(instName.equalsIgnoreCase("RINGO3:F"))
				return ((Double)statusBlock.get("Temperature.1.1"));
		else if
		(instName.equalsIgnoreCase("FRODO_RED"))
				return ((Double)statusBlock.get("red.Temperature"));
		else if
		(instName.equalsIgnoreCase("FRODO_BLUE"))
				return ((Double)statusBlock.get("blue.Temperature"));
		else if
		(instName.equalsIgnoreCase("IO:O"))
				return ((Double)statusBlock.get("Temperature"));
		else if
		(instName.equalsIgnoreCase("IO:I"))
				return ((Double)statusBlock.get("Temperature"));
		else if
		(instName.equalsIgnoreCase("LOCI"))
				return ((Double)statusBlock.get("Temperature"));
		else if
		(instName.equalsIgnoreCase("SPRAT"))
				return ((Double)statusBlock.get("Temperature"));
		else if
		(instName.equalsIgnoreCase("IO:THOR"))
				return ((Double)statusBlock.get("Temperature"));
		return -1.0;
	}
	
}
