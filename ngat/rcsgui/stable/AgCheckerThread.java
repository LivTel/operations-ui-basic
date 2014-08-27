/**
 * 
 */
package ngat.rcsgui.stable;

import java.util.HashMap;
import java.util.Map;

import ngat.icm.InstrumentDescriptor;
import ngat.icm.InstrumentStatus;
import ngat.message.ISS_INST.GET_STATUS_DONE;

/**
 * @author eng
 * 
 */
public class AgCheckerThread extends Thread {

	private AgChecker agChecker;

	private AgCheckHandler agHandler;

	/**
	 * @param agHandler
	 */
	public AgCheckerThread(AgChecker agChecker, AgCheckHandler agHandler) {
		super("AG_CHK_HANDLER");
		this.agChecker = agChecker;
		this.agHandler = agHandler;
	}

	@Override
	public void run() {

		while (true) {

			try {
				Thread.sleep(10000L);
			} catch (InterruptedException ix) {
			}

			double agtemp = 999;
			boolean agtempgot = false;
			boolean agactive = false;
			boolean agfunc = false;
			String agval = "UNKNOWN";
			int agstat = InstrumentStatus.OPERATIONAL_STATUS_UNAVAILABLE;
			InstrumentStatus agstatus = new InstrumentStatus();
			int aghstat = InstrumentData.ONLINE_OKAY;
			try {
				// get the temperature status
				agtemp = agChecker.checkAgTemp();
				// this is kelvins
				agtempgot = true;
			} catch (Exception e) {
				e.printStackTrace();
			}

			try {
				// get the active status
				agChecker.checkAgActive();
				agactive = true;
			} catch (Exception e) {
				agactive = false;
				e.printStackTrace();
			}

			if (!agtempgot) {
				// we have no temperature but we know its okish
				agstat = InstrumentStatus.OPERATIONAL_STATUS_OKAY;
			} else {

				Map map = new HashMap();

				if (agtemp < 223 || agtemp > 243) {

					agstat = InstrumentStatus.OPERATIONAL_STATUS_FAIL;
					aghstat = InstrumentData.ONLINE_FAIL;
					agval = GET_STATUS_DONE.VALUE_STATUS_FAIL;
				} else if (agtemp < 228 || agtemp > 238) {
					agstat = InstrumentStatus.OPERATIONAL_STATUS_WARN;
					aghstat = InstrumentData.ONLINE_WARN;
					agval = GET_STATUS_DONE.VALUE_STATUS_WARN;
					agfunc = true;
				} else {
					agstat = InstrumentStatus.OPERATIONAL_STATUS_OKAY;
					aghstat = InstrumentData.ONLINE_OKAY;
					agval = GET_STATUS_DONE.VALUE_STATUS_OK;
					agfunc = true;
				}

				if (!agactive) {
					aghstat = InstrumentData.OFFLINE;
				}

				// we now have the active and temperature statuses

				map.put("temperature", agtemp);
				map.put(GET_STATUS_DONE.KEYWORD_INSTRUMENT_STATUS, agval);
				agstatus.setEnabled(true);
				agstatus.setInstrument(new InstrumentDescriptor("AUTOGUIDER"));
				agstatus.setStatusTimeStamp(System.currentTimeMillis());
				agstatus.setFunctional(agfunc);
				agstatus.setOnline(agactive);
				agstatus.setStatus(map);

			}
			
			agHandler.agStatusUpdated(agstatus);

		}
	}

}
