package ngat.rcsgui.stable;

import ngat.net.datalogger.*;
import ngat.message.RCS_TCS.*;
import ngat.message.GUI_RCS.*;
import ngat.util.*;
import ngat.rcs.scm.collation.*;

import java.util.*;
import java.text.*;
import java.io.*;

public class DataFileWriter implements DataLoggerUpdateListener {

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHH:mm:ssz");

	SimpleTimeZone UTC = new SimpleTimeZone(0, "UTC");

	PrintStream meteoout;
	PrintStream stateout;
	PrintStream mechout;
	PrintStream rcsout;
	PrintStream cloudout;
	PrintStream skyout;
	PrintStream smout;
	PrintStream oasout;
	PrintStream diskout;
	PrintStream agtempout;
	PrintStream autoout;
	PrintStream ocrout;

	PrintStream ratcamout;
	PrintStream thorout;
	PrintStream riseout;
	PrintStream ringout;
	PrintStream frodoout;
	PrintStream iooout;
	PrintStream ioiout;
	PrintStream spratout;
	PrintStream seeout;

	InternalStatus latest_rcs;
	StateModelVariableStatus latest_sm;

	/** Create a DataFileWriter. */
	public DataFileWriter(File file) throws IOException {

		meteoout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/meteo_lt.dat", true));
		stateout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/status_lt.dat", true));
		mechout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/mech_lt.dat", true));
		rcsout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/rcs_lt.dat", true));
		cloudout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/cloud_lt.dat", true));
		skyout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/sky_lt.dat", true));
		smout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/sm_lt.dat", true));
		oasout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/oas_lt.dat", true));
		diskout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/disks_lt.dat", true));
		agtempout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/agtemp_lt.dat", true));
		autoout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/auto_lt.dat", true));
		ocrout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/ocr_lt.dat", true));
		seeout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/seeing_lt.dat", true));

		ratcamout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/ratcam.dat", true));
		thorout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/thor.dat", true));
		riseout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/rise.dat", true));
		ringout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/ringo3.dat", true));
		frodoout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/frodo.dat", true));
		iooout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/ioo.dat", true));
		ioiout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/ioi.dat", true));
		spratout = new PrintStream(new FileOutputStream(file.getPath()
				+ "/sprat.dat", true));

		sdf.setTimeZone(UTC);

		TCS_Status.mapCodes();

	}

	@Override
	public void dataUpdate(Object data) {

		try {

			if (data instanceof StatusInfo) {

				StatusInfo info = (StatusInfo) data;

				StatusCategory status = info.getStatus();

				System.err.println("Status class: "
						+ status.getClass().getName());

				if (status instanceof TCS_Status.Segment) {

					TCS_Status.Segment seg = (TCS_Status.Segment) status;

					System.err.println("Segment:[" + seg + "]");

					if (seg instanceof TCS_Status.Meteorology) {

						TCS_Status.Meteorology meteo = (TCS_Status.Meteorology) seg;

						meteoout.println(sdf.format(new Date(meteo.timeStamp))
								+ " " + TCS_Status.codeString(meteo.wmsStatus)
								+ " " + TCS_Status.codeString(meteo.rainState)
								+ " " + meteo.moistureFraction + " "
								+ meteo.serrurierTrussTemperature + " "
								+ meteo.oilTemperature + " " + meteo.windSpeed
								+ " " + meteo.windDirn + " "
								+ meteo.extTemperature + " "
								+ meteo.dewPointTemperature + " "
								+ meteo.humidity + " " + meteo.pressure + " "
								+ meteo.lightLevel);
					} else if (seg instanceof TCS_Status.Autoguider) {

						TCS_Status.Autoguider auto = (TCS_Status.Autoguider) seg;

						autoout.println(sdf.format(new Date(auto.timeStamp))
								+ " " + TCS_Status.codeString(auto.agSwState)
								+ " " + TCS_Status.codeString(auto.agStatus));

					} else if (seg instanceof TCS_Status.State) {

						TCS_Status.State state = (TCS_Status.State) seg;

						stateout.println(sdf.format(new Date(state.timeStamp))
								+ " "
								+ TCS_Status.codeString(state.telescopeState)
								+ " "
								+ TCS_Status.codeString(state.tcsState)
								+ " "
								+ TCS_Status
										.codeString(state.networkControlState)
								+ " "
								+ TCS_Status
										.codeString(state.engineeringOverrideState)
								+ " " + state.systemRestartFlag + " "
								+ state.systemShutdownFlag);

					} else if (seg instanceof TCS_Status.Mechanisms) {

						TCS_Status.Mechanisms mech = (TCS_Status.Mechanisms) seg;

						mechout.println(sdf.format(new Date(mech.timeStamp))
								+ " "
								+ mech.azPos
								+ " "
								+ mech.azDemand
								+ " "
								+ TCS_Status.codeString(mech.azStatus)
								+ " "
								+ mech.altPos
								+ " "
								+ mech.altDemand
								+ " "
								+ TCS_Status.codeString(mech.altStatus)
								+ " "
								+ mech.rotPos
								+ " "
								+ mech.rotDemand
								+ " "
								+ TCS_Status.codeString(mech.rotStatus)
								+ " "
								+ mech.secMirrorPos
								+ " "
								+ mech.secMirrorDemand
								+ " "
								+ TCS_Status.codeString(mech.secMirrorStatus)
								+ " "
								+ mech.focusOffset
								+ " "
								+

								TCS_Status.codeString(mech.encShutter1Pos)
								+ " "
								+ TCS_Status.codeString(mech.encShutter1Status)
								+ " "
								+ TCS_Status.codeString(mech.encShutter2Pos)
								+ " "
								+ TCS_Status.codeString(mech.encShutter2Status)
								+ " "
								+

								TCS_Status.codeString(mech.primMirrorCoverPos)
								+ " "
								+ TCS_Status
										.codeString(mech.primMirrorCoverStatus)

						);

					} // other segment type.

				} else if (status instanceof ngat.rcs.scm.collation.SeeingHistoryStatus) {

					ngat.rcs.scm.collation.SeeingHistoryStatus seehist = (ngat.rcs.scm.collation.SeeingHistoryStatus) status;

					Vector<SeeingStatus> samples = seehist.getHistory();

					System.err.println("SeeingHistory contains: "
							+ (samples != null ? "" + samples.size() : "NULL"));

					// write each sample out as:
					// date raw corrected predicted elev_deg wavelength_nm std
					// src_inst tgt_name

					// e.g. 2014-06-25222:22:23 1.2 0.85 1.12 45.6 550.0 STD IO:O G17+25

					for (int is = 0; is < samples.size(); is++) {
						SeeingStatus seeing = samples.get(is);
						seeout.printf("%s %4.2f %4.2f %4.2f %4.2f %4.2f %s %s %s\n", 
								sdf.format(new Date(seeing.getTimeStamp())), 
								seeing.getRawSeeing(),
								seeing.getCorrectedSeeing(), 
								seeing.getPrediction(), 
								Math.toDegrees(seeing.getElevation()), 
								seeing.getWavelength(),
								(seeing.isStandard() ? "STD" : "SCI"), seeing
								.getSource(), 
								seeing.getTargetName());

					} // next historic sample

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

					rcsout.println(sdf.format(new Date(intrcs.getTimeStamp()))
							+ "," + sctrl + "," + state + "," + aic + "," + aid
							+ "," + aac);

					// record the latest value
					latest_rcs = intrcs;
					
				} else if (status instanceof AgActiveStatus) {
					
					AgActiveStatus aga = (AgActiveStatus)status;

					agtempout.println(sdf.format(new Date(aga.getTimeStamp()))
							+ " "
							+ (aga.getTemperature()-273.16));
					
				} else if (status instanceof ngat.rcs.scm.collation.MappedStatusCategory) {

					MappedStatusCategory msc = (MappedStatusCategory) status;
					String mcat = info.getCat();

					System.err.println("MSC: Cat=" + mcat + " : " + msc);

					if (mcat.equals("X_CLOUD")) {
						System.err.println("Processing CLOUD data");
						cloudout.println(sdf.format(new Date(msc.getTimeStamp()))
								+ " "
								+ msc.getStatusEntryDouble("t.amb")
								+ " "
								+ msc.getStatusEntryDouble("t.diff")
								+ " "
								+ msc.getStatusEntryDouble("t.sensor")
								+ " "
								+ msc.getStatusEntryDouble("heater")
								+ " "
								+ msc.getStatusEntryDouble("wet.flag")
								+ " "
								+ msc.getStatusEntryDouble("dt"));
					} else if (mcat.equals("DISKS")) {
						System.err.println("Processing DISK data");
						diskout.println(sdf.format(new Date(msc.getTimeStamp()))
								+ " "
								+ msc.getStatusEntryDouble("disk.usage.occ")
								+ " "
								+ (msc.getStatusEntryDouble("free.space.occ") / 1000000)
								+ " "

								+ msc.getStatusEntryDouble("disk.usage.nas2")
								+ " "
								+ (msc.getStatusEntryDouble("free.space.nas2") / 1000000)
								+ " "

								+ msc.getStatusEntryDouble("disk.usage.rise")
								+ " "
								+ (msc.getStatusEntryDouble("free.space.rise") / 1000000)
								+ " "

								+ msc.getStatusEntryDouble("disk.usage.ringo3-1")
								+ " "
								+ (msc.getStatusEntryDouble("free.space.ringo3-1") / 1000000)
								+ " "

								+ msc.getStatusEntryDouble("disk.usage.ringo3-2")
								+ " "
								+ (msc.getStatusEntryDouble("free.space.ringo3-2") / 1000000)
								+ " "

								+ msc.getStatusEntryDouble("disk.usage.autoguider")
								+ " "
								+ (msc.getStatusEntryDouble("free.space.autoguider") / 1000000));

						System.err
								.println("Dumping DISK status to: disks_lt.dat: occ: "
										+ msc.getStatusEntryDouble("disk.usage.occ"));

					} else if (mcat.equals("AGACTIVE")) {
						System.err.println("Processing AGTEMP data");

						agtempout.println(sdf.format(new Date(msc
								.getTimeStamp()))
								+ " "
								+ msc.getStatusEntryDouble("t.ag"));
						System.err
								.println("Dumping AGTEMP data to: agtemp_lt.dat");
					} else if (mcat.equals("X_OCR")) {
						System.err.println("Processing OCR data");
						int state = (int) (msc.getStatusEntryDouble("state"));
						String strState = "UNKNOWN";
						if (state == 2)
							strState = "OKAY";
						else
							strState = "ERROR";
						ocrout.println(sdf.format(new Date(msc.getTimeStamp()))
								+ " " + strState);

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
					double temp4 = -99.99;
					double temp5 = -99.99;

					int hadu = -999;
					int hadu1 = -999;
					int hadu2 = -999;
					int hadu3 = -999;
					double humidity2=-999.0,humidity3=-999.0;

					System.err.println(icat + ":NetStat: " + netstat);
					System.err.println(icat + ":OpStat:  " + opstat);

					// System.err.println(icat+":Temp:    "+temp);
					if (icat.startsWith("FRODO")) {
						temp1 = status.getStatusEntryDouble("red.Temperature") - 273.15;
						temp2 = status.getStatusEntryDouble("blue.Temperature") - 273.15;
						try {
							temp3 = Double
									.parseDouble(status
											.getStatusEntryRaw("Environment.Temperature.0"));
							temp4 = Double
									.parseDouble(status
											.getStatusEntryRaw("Environment.Temperature.2"));
							temp5 = Double
									.parseDouble(status
											.getStatusEntryRaw("Environment.Temperature.4"));
						} catch (Exception e) {
							System.err.println(icat
									+ ":Env Temp value(s) not available");
						}
						try {
							hadu1 = status.getStatusEntryInt("red.Heater ADU");
						} catch (Exception e) {
							System.err.println(icat
									+ ":ADU red value not available");
						}
						try {
							hadu2 = status.getStatusEntryInt("blue.Heater ADU");
						} catch (Exception e) {
							System.err.println(icat
									+ ":ADU blue value not available");
						}
					} else if (icat.startsWith("RINGO")) {
						temp1 = status.getStatusEntryDouble("Temperature.0.0") - 273.15;
						temp2 = status.getStatusEntryDouble("Temperature.1.0") - 273.15;
						temp3 = status.getStatusEntryDouble("Temperature.1.1") - 273.15;

					} else if (icat.startsWith("SPRAT")) {
						temp1 = status.getStatusEntryDouble("Temperature") - 273.15;
						temp2 = status.getStatusEntryDouble("Mechanism.Temperature.0");
						temp3 = status.getStatusEntryDouble("Mechanism.Temperature.1");
						humidity2 = status.getStatusEntryDouble("Mechanism.Humidity.0");
						humidity3 = status.getStatusEntryDouble("Mechanism.Humidity.1");
					} else {
						temp1 = status.getStatusEntryDouble("Temperature") - 273.15;
						try {
							hadu = status.getStatusEntryInt("Heater ADU");
						} catch (Exception e) {
							System.err.println(icat
									+ ":ADU value not available");
						}
					}

					if (icat.equals("RATCAM")) {
						ratcamout.println(sdf.format(new Date(inst
								.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat + " " + temp1 + " " + hadu);
					} else if (icat.startsWith("RINGO")) {
						ringout.println(sdf.format(new Date(inst.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1
								+ " " + temp2 + " " + temp3);
					} else if (icat.equals("IO:THOR")) {
						thorout.println(sdf.format(new Date(inst.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1
								+ " " + hadu);
					} else if (icat.equals("RISE")) {
						riseout.println(sdf.format(new Date(inst.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1
								+ " " + hadu);
					} else if (icat.startsWith("FRODO")) {
						frodoout.println(sdf.format(new Date(inst
								.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1
								+ " "
								+ temp2
								+ " "
								+ hadu1
								+ " "
								+ hadu2
								+ " "
								+ temp3
								+ " "
								+ temp4 + " " + temp5);
					} else if (icat.equals("IO:O")) {
						iooout.println(sdf.format(new Date(inst.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1
								+ " " + hadu);

					} else if (icat.equals("IO:I")) {
						ioiout.println(sdf.format(new Date(inst.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1);

					} else if (icat.startsWith("SPRAT")) {
						spratout.println(sdf.format(new Date(inst
								.getTimeStamp()))
								+ " "
								+ netstat
								+ " "
								+ opstat
								+ " "
								+ temp1
								+ " "
								+ temp2
								+ " "
								+ humidity2
								+ " "
								+ temp3
								+ " "
								+ humidity3);
					}

				/*} else if (status instanceof SkyModelStatus) {

					SkyModelStatus sky = (SkyModelStatus) status;
					// We dont really get a proper timestamp from these packets
					// due to the way SkyModel is updated
					long now = System.currentTimeMillis();
					skyout.println(sdf.format(new Date(now)) + " "
							+ sky.getExtinctionCat() + " "
							+ sky.getPrediction());*/

				} else if (status instanceof StateModelVariableStatus) {

					StateModelVariableStatus sms = (StateModelVariableStatus) status;
					int weather = sms.getWeatherState();
					String wms = "UNKNOWN";
					if (weather == 9)
						wms = "ALERT";
					else if (weather == 10)
						wms = "CLEAR";

					smout.println(sdf.format(new Date(status.getTimeStamp()))
							+ " " + wms);

					// record latest values
					latest_sm = sms;

					processOverallState(status.getTimeStamp(), latest_rcs,
							latest_sm);

				} // any others

			}

		} catch (Exception e) {
			System.err.println("Error: " + e);
		}
	}

	private void processOverallState(long timestamp, InternalStatus l_rcs,
			StateModelVariableStatus l_sm) {

		int system = -1;
		int intent = -1;
		int weather = -1;
		int period = -1;

		try {
			system = l_sm.getVariable("SYSTEM"); // suspend, okay, stby
		} catch (Exception e) {
		}

		try {
			intent = l_sm.getVariable("INTENT"); // eng, auto
		} catch (Exception e) {
		}

		try {
			weather = l_sm.getVariable("WEATHER"); // alert, clear
		} catch (Exception e) {
		}

		try {
			period = l_sm.getVariable("PERIOD"); // daytime, nightime
		} catch (Exception e) {
		}

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
		} else if (weather == 9) {
			oas = 2;
			oasname = "WEATHER";
		} else if (intent == 17) {
			oas = 3;
			oasname = "ENG";
		} else if (system != 3) {
			oas = 4;
			oasname = "SUSPEND";
		} else {
			if (aic.equals("SOCA")) {
				oas = 5;
				oasname = "SOCA";
			} else if (aic.equals("BGCA")) {
				oas = 6;
				oasname = "BGCA";
			} else if (aic.equals("CAL")) {
				oas = 7;
				oasname = "CAL";
			} else if (aic.equals("TOCA")) {
				oas = 8;
				oasname = "TOCA";
			} else {
				oas = 9;
				oasname = "TRANSIENT";
			}
		}

		oasout.println(sdf.format(new Date(timestamp)) + " " + system + " "
				+ intent + " " + weather + " " + period + " " + aic + " " + oas
				+ " " + oasname);

		// 2012-10-31 T 18:00:00 SUSPEND AUTO CLEAR NIGHT NONE

	}

}
