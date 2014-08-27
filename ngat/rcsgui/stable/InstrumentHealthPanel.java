/**
 * 
 */
package ngat.rcsgui.stable;

import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import ngat.rcsgui.test.TimeScale;

/** Display instrument health log as a line with varying colors:-
 * GREEN  = ONLINE and OKAY
 * ORANGE = ONLINE and WARN
 * RED    = ONLINE and FAILED
 * BLUE   = OFFLINE
 * 
 * @author eng
 *
 */
public class InstrumentHealthPanel extends JPanel {

	public static final SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");
	
	public static final long PERIOD = 12*3600*1000L;
	
	long start;
	long end;
	
	private Map<String, InstrumentLogRenderer> instruments;
	
	private TimeAxisRenderer timeAxis;
	
	private TimeScale scale;
	
	public InstrumentHealthPanel() {
		super(true);
		
		// reset start to a fixed hour before now.
		//start = System.currentTimeMillis();
	
		
		instruments = new HashMap<String, InstrumentLogRenderer>();
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		long now = System.currentTimeMillis();
		scale = new TimeScale(now, now+PERIOD, PERIOD);
	}
	
	public void addInstrument(String name){
		// CHAGED TO 2 from 6 on 21april2011 TESTETSTESTT
		InstrumentLogRenderer renderer = new InstrumentLogRenderer(name, scale);
		instruments.put(name, renderer);
		add(renderer);
		validate();
		System.err.println("IHP:: add Instrument: "+name);
	}

	public void addTimeAxis() {
		
		timeAxis = new TimeAxisRenderer(scale);
		add(timeAxis);
		
	}
	
	public void updateInstrument(String name, long time, int state) {
		System.err.println("IHP::updateInstrument("+name+", "+state);
		InstrumentLogRenderer log = instruments.get(name);
		log.addItem(new InstrumentData(time, state));
		log.repaint();
		timeAxis.repaint();
	}
	
}
