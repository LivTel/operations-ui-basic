package ngat.rcsgui.stable;

import ngat.net.datalogger.*;
import ngat.message.RCS_TCS.*;
import ngat.message.GUI_RCS.*;
import ngat.util.*;
import ngat.rcs.scm.collation.*;

import java.util.*;
import java.text.*;
import java.io.*;

public class DataFileSingleStreamWriter implements DataLoggerUpdateListener {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssz");

    SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

    PrintStream out;

    InternalStatus latest_rcs; 
    StateModelVariableStatus latest_sm;

    /** Create a DataFileWriter. */
    public DataFileSingleStreamWriter(File file) throws IOException {

	out = new PrintStream(new FileOutputStream(file, true));

	sdf.setTimeZone(UTC);

	TCS_Status.mapCodes();

    }

    @Override
	public void dataUpdate(Object data) {

	try {

	    if (data instanceof StatusInfo) {

		StatusInfo info = (StatusInfo) data;

		StatusCategory status = info.getStatus();

		System.err.println("Status class: " + status.getClass().getName());

		if (status instanceof TCS_Status.Segment) {

		    TCS_Status.Segment seg = (TCS_Status.Segment) status;

		    System.err.println("Segment:[" + seg + "]");

		    if (seg instanceof TCS_Status.Meteorology) {

			TCS_Status.Meteorology meteo = (TCS_Status.Meteorology) seg;

			out.println(sdf.format(new Date(meteo.timeStamp)) + " "
					 + TCS_Status.codeString(meteo.wmsStatus) + " " + TCS_Status.codeString(meteo.rainState)
					 + " " + meteo.moistureFraction + " " + meteo.serrurierTrussTemperature + " "
					 + meteo.oilTemperature + " " + meteo.windSpeed + " " + meteo.windDirn + " "
					 + meteo.extTemperature + " " + meteo.dewPointTemperature + " " + meteo.humidity + " "
					 + meteo.pressure + " " + meteo.lightLevel);

		    } else if (seg instanceof TCS_Status.State) {

			TCS_Status.State state = (TCS_Status.State) seg;

			out.println(sdf.format(new Date(state.timeStamp)) + " "
					 + TCS_Status.codeString(state.telescopeState) + " "
					 + TCS_Status.codeString(state.tcsState) + " "
					 + TCS_Status.codeString(state.networkControlState) + " "
					 + TCS_Status.codeString(state.engineeringOverrideState) + " " + state.systemRestartFlag
					 + " " + state.systemShutdownFlag);

		    } else if (seg instanceof TCS_Status.Mechanisms) {

			TCS_Status.Mechanisms mech = (TCS_Status.Mechanisms) seg;

			out.println(sdf.format(new Date(mech.timeStamp)) + " " + mech.azPos + " " + mech.azDemand
					+ " " + TCS_Status.codeString(mech.azStatus) + " " + mech.altPos + " " + mech.altDemand
					+ " " + TCS_Status.codeString(mech.altStatus) + " " + mech.rotPos + " "
					+ mech.rotDemand + " " + TCS_Status.codeString(mech.rotStatus) + " "
					+ mech.secMirrorPos + " " + mech.secMirrorDemand + " "
					+ TCS_Status.codeString(mech.secMirrorStatus) + " " + mech.focusOffset + " " +

					TCS_Status.codeString(mech.encShutter1Pos) + " "
					+ TCS_Status.codeString(mech.encShutter1Status) + " "
					+ TCS_Status.codeString(mech.encShutter2Pos) + " "
					+ TCS_Status.codeString(mech.encShutter2Status) + " " +

					TCS_Status.codeString(mech.primMirrorCoverPos) + " "
					+ TCS_Status.codeString(mech.primMirrorCoverStatus)

					);

		    } // other segment type.

		} else if (status instanceof ngat.rcs.scm.collation.InternalStatus) {

		    InternalStatus intrcs = (InternalStatus) status;

		    int control = intrcs.getControl();
		    String sctrl = "UNKNOWN";
		    if (control == ID.RCS_PROCESS)
			sctrl = "RCS";
		    else if (control == ID.WATCHDOG_PROCESS)
			sctrl = "WATCHDOG";

		    String state = "UNKNOWN";
		    if (intrcs.getEngineering())
			state = "ENGINEERING";
		    else if (intrcs.getOperational())
			state = "OPERATIONAL";
		    else
			state = "STANDBY";

		    String aic = intrcs.getAgentInControl();
		    if (aic != null)
			aic = aic.replace(',', ' ');
		    else
			aic = "N/A";

		    String aid = intrcs.getAgentName();
		    if (aid != null)
			aid = aid.replace(',', ' ');
		    else
			aid = "N/A";

		    String aac = intrcs.getAgentActivity();
		    if (aac != null)
			aac = aac.replace(',', ' ');
		    else
			aac = "N/A";

		    out.println(sdf.format(new Date(intrcs.getTimeStamp())) + "," + sctrl + "," + state + "," + aic
				   + "," + aid + "," + aac);


		    // record the latest value
		    latest_rcs = intrcs;

		} else if (status instanceof ngat.rcs.scm.collation.MappedStatusCategory) {

		    MappedStatusCategory msc = (MappedStatusCategory) status;
		    String mcat = info.getCat();

		    System.err.println("MSC: Cat=" + mcat + " : " + msc);

		    if (mcat.equals("X_CLOUD")) {
			out.println(sdf.format(new Date(msc.getTimeStamp())) + " "
					 + msc.getStatusEntryDouble("t.amb") + " " + msc.getStatusEntryDouble("t.diff") + " "
					 + msc.getStatusEntryDouble("t.sensor") + " " + msc.getStatusEntryDouble("heater") + " "
					 + msc.getStatusEntryDouble("wet.flag") + " " + msc.getStatusEntryDouble("dt"));
		    }

		} else if (status instanceof ngat.rcs.scm.collation.InstrumentStatus) {

		    InstrumentStatus inst = (InstrumentStatus) status;
		    String icat = info.getCat();

		    // now pull the relevant stuff out
		    String netstat = status.getStatusEntryId("network.status");
		    String opstat = status
			.getStatusEntryId(ngat.message.ISS_INST.GET_STATUS_DONE.KEYWORD_INSTRUMENT_STATUS);
		    double temp1 = -99.99;
		    double temp2 = -99.99;
		    double temp3 = -99.99;

		    int hadu = -999;
		    int hadu1 = -999;
		    int hadu2 = -999;
		    int hadu3 = -999;

		    System.err.println(icat + ":NetStat: " + netstat);
		    System.err.println(icat + ":OpStat:  " + opstat);

		    // System.err.println(icat+":Temp:    "+temp);
		    if (icat.startsWith("FRODO")) {
			temp1 = status.getStatusEntryDouble("red.Temperature") - 273.15;
			temp2 = status.getStatusEntryDouble("blue.Temperature") - 273.15;	
			try{
			    hadu1 = status.getStatusEntryInt("red.Heater ADU");
			} catch (Exception e) {
			    System.err.println(icat + ":ADU red value not available");
			}
			try{
			    hadu2 = status.getStatusEntryInt("blue.Heater ADU");
			} catch (Exception e) {
			    System.err.println(icat + ":ADU blue value not available");
			}
		    } else if
			  (icat.startsWith("RINGO")) {
			temp1 = status.getStatusEntryDouble("Temperature.0.0") - 273.15;
                        temp2 = status.getStatusEntryDouble("Temperature.1.0") - 273.15;
			temp3 = status.getStatusEntryDouble("Temperature.1.1") - 273.15;

		    } else {						
			temp1 = status.getStatusEntryDouble("Temperature") - 273.15;
			try{
			    hadu = status.getStatusEntryInt("Heater ADU");
			} catch (Exception e) {
			    System.err.println(icat + ":ADU value not available");
			}
		    }

		    if (icat.equals("RATCAM")) {
			out.println(sdf.format(new Date(inst.getTimeStamp())) + " " + netstat + " " + opstat
					  + " " + temp1 + " " + hadu);
		    } else if (icat.startsWith("RINGO")) {
			out.println(sdf.format(new Date(inst.getTimeStamp())) + " " + netstat + " " + opstat + " "
					+ temp1+" "+temp2+" "+temp3);
		    } else if (icat.equals("IO:THOR")) {
			out.println(sdf.format(new Date(inst.getTimeStamp())) + " " + netstat + " " + opstat + " "
					+ temp1+" "+hadu);
		    } else if (icat.equals("RISE")) {
			out.println(sdf.format(new Date(inst.getTimeStamp())) + " " + netstat + " " + opstat + " "
					+ temp1+" "+hadu);
		    } else if (icat.startsWith("FRODO")) {
			out.println(sdf.format(new Date(inst.getTimeStamp())) + " " + netstat + " " + opstat + " "
					 + temp1 + " " + temp2+" "+hadu1+" "+hadu2);
		    } else if (icat.equals("IO:O")) {
			out.println(sdf.format(new Date(inst.getTimeStamp())) + " " + netstat + " " + opstat + " "
				       + temp1+" "+hadu);

		    }

		} else if (status instanceof SkyModelStatus) {

		    SkyModelStatus sky = (SkyModelStatus) status;
		    // We dont really get a proper timestamp from these packets due to the way SkyModel is updated
		    long now = System.currentTimeMillis();
		    out.println(sdf.format(new Date(now)) + " "+sky.getExtinctionCat()+" "+sky.getPrediction());

		} else if (status instanceof StateModelVariableStatus) {

		    StateModelVariableStatus sms = (StateModelVariableStatus) status;
		    int weather = sms.getWeatherState();
		    String wms = "UNKNOWN";
		    if (weather == 9)
			wms = "ALERT";
		    else if
			(weather == 10)
			wms = "CLEAR";
					
		    out.println(sdf.format(new Date(status.getTimeStamp())) + " " +wms);

		    // record latest values
		    latest_sm = sms;

		    processOverallState(status.getTimeStamp(), latest_rcs, latest_sm);

		} // any others

	    }

	} catch (Exception e) {
	    System.err.println("Error: " + e);
	}
    }



    private void processOverallState(long timestamp, InternalStatus l_rcs, StateModelVariableStatus l_sm) {
	
	int system = -1;
	int intent = -1;
	int weather = -1;
	int period = -1;

	try {
	    system = l_sm.getVariable("SYSTEM"); // suspend, okay, stby
	} catch (Exception e) {}
	
	try {
	    intent = l_sm.getVariable("INTENT"); // eng, auto
	} catch (Exception e) {}
	
	try {
	    weather = l_sm.getVariable("WEATHER"); // alert, clear
	} catch (Exception e) {}
	
	try {
	    period = l_sm.getVariable("PERIOD"); // daytime, nightime
	} catch (Exception e) {}

	String aic = l_rcs.getAgentInControl();
	if (aic != null)
	    aic = aic.replace(',', ' ');
	else
	    aic = "NONE";

	int oas = -1;
	String oasname = "UNKNOWN";

	if (period == 18) {
	    oas = 1;
	    oasname = "DAY";
	} else if
	      (weather == 9) {
	    oas = 2;
	    oasname = "WEATHER";
	} else if
	      (intent == 17) {
	    oas = 3;
	    oasname = "ENG";
	} else if
	      (system != 3) {
	    oas = 4;
	    oasname = "SUSPEND";
	} else {
	    if (aic.equals("SOCA")) {
		oas = 5;
		oasname = "SOCA";
	    } else if
		  (aic.equals("BGCA")) {
		oas = 6;
		oasname = "BGCA";
	    } else if
		  (aic.equals("CAL")) {
		oas = 7;
		oasname = "CAL";
	    } else if
		  (aic.equals("TOCA")) {
		oas = 8;
		oasname = "TOCA";
	    } else {
		oas = 9;
		oasname = "TRANSIENT";
	    }
	}

	out.println(sdf.format(new Date(timestamp)) + " "+
		       system+" "+
		       intent+" "+
		       weather+" "+
		       period+" "+
		       aic+" " +
		       oas+" "+
		       oasname);

	// 2012-10-31 T 18:00:00 SUSPEND AUTO CLEAR NIGHT NONE

    }



}
