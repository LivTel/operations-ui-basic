/**
 * 
 */
package ngat.opsgui.services;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentStatus;
import ngat.opsgui.perspectives.instruments.InstrumentCombinedHealthDisplayPanel;
import ngat.rcsgui.stable.AgCheckHandler;

/**
 * @author eng
 *
 */
public class LegacyAutoguiderHandler implements AgCheckHandler {

	private InstrumentDescriptor agId = new InstrumentDescriptor("AUTOGUIDER");
	
	private InstrumentCombinedHealthDisplayPanel ihp;
	
	/**
	 * @param ihp
	 */
	public LegacyAutoguiderHandler(InstrumentCombinedHealthDisplayPanel ihp) {
		this.ihp = ihp;
	}


	@Override
	public void agStatusUpdated(InstrumentStatus status) {
		
		ihp.updateStatus(status);
		
	}

}
