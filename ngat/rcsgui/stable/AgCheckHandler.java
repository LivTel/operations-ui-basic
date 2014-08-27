/**
 * 
 */
package ngat.rcsgui.stable;

import ngat.icm.InstrumentStatus;

/**
 * @author eng
 *
 */
public interface AgCheckHandler {

	public void agStatusUpdated(InstrumentStatus status);
	
}
