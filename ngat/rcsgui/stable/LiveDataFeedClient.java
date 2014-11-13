package ngat.rcsgui.stable;

import ngat.rcs.scm.collation.InternalStatus;
import ngat.rcs.scm.collation.SeeingHistoryStatus;
import ngat.rcs.scm.collation.SeeingStatus;
import ngat.rcs.scm.collation.StateModelVariableStatus;
import ngat.util.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.net.datalogger.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;
import ngat.message.RCS_TCS.*;

import java.io.*;
import java.util.*;

public class LiveDataFeedClient {

	private static String tempFileName;
	
	/** This is called each time the client runs, note it is usually run as a cron job so does not remember any 
	 * information from its last run.
	 * @param args are: --cat <cat> --host <rcs-host> --port <rcs-port> --fhost <fwd-host --fport <fwd-udp-port>
	 */
	public static void main(String args[]) {

		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			String cat = cfg.getProperty("cat", "MECHANISM");
			String host = cfg.getProperty("host", "occ");
			int port = cfg.getIntValue("port", 9110);

			String fhost = cfg.getProperty("fhost", "telescope");
			int fport = cfg.getIntValue("fport", 6567);

			tempFileName = cfg.getProperty("temp", "/usr/local/livedata/tmp");
			
			DataForwarder forwarder = new DataForwarder(new Vector(), fhost, fport);

			// (required for proper decoding of TCS replies).
			TCS_Status.mapCodes();

			IConnection connection = new SocketConnection(host, port);

			COMMAND command = null;

			if (cat.equals("ID")) {
				command = new ID("live_data_request");
			} else if (cat.equals("SM")) {
				command = new GET_STATE_MODEL("live_data_request");
			} else if (cat.equals("SEEING")) {
				// read last sample time from file...
				long lastSampleTime = readLastSampleTime();
				command = new GET_SEEING("live_data_request", 0, lastSampleTime);
			} else {
				GET_STATUS getStat = new GET_STATUS("live_data_request");
				getStat.setCategory(cat);
				command = getStat;
			}

			CAMPResponseHandler handler = new MyStatusHandler(forwarder, cat);

			try {
				connection.open();
			} catch (IOException cx) {
				handler.failed(cx, connection);
				return;
			}

			try {
				connection.send(command);
			} catch (IOException iox) {
				handler.failed(iox, connection);
				return;
			}

			try {
				Object obj = connection.receive(10000L);
				System.err.println("Object recvd: " + obj);
				COMMAND_DONE update = (COMMAND_DONE) obj;
				handler.handleUpdate(update, connection);
			} catch (ClassCastException cx) {
				handler.failed(cx, connection);
				return;
			} catch (IOException iox) {
				handler.failed(iox, connection);
				return;
			} finally {
				if (connection != null) {
					System.err.println("Closing connection");
					try {
						connection.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	
	public static long readLastSampleTime() throws Exception {
		
		BufferedReader bin = new BufferedReader(new FileReader(tempFileName));
		
		String line = bin.readLine();
		System.err.println("read line: "+line);
		long time = Long.parseLong(line);
		
		return time;
		
	}

	public static void saveLastTime(long time) throws Exception {
		
		// print stream - NO APPEND - ie re-create file each time
		PrintWriter pout = new PrintWriter(new FileWriter(tempFileName, false));
		// timestamp in msec since
		pout.print(time);
		pout.println();
		// something for the humans to read
		pout.println(new Date(time));
		pout.flush();
		pout.close();
		
	}
	
	// Here is an example of a handler which handles a specific status key, more
	// likely want to handle
	// all the keys in a given status category.

	public static class MyStatusHandler implements CAMPResponseHandler {

		// feed onto host,port via UDP
		DataForwarder forwarder;

		String cat;

		MyStatusHandler(DataForwarder forwarder, String cat) {
			this.forwarder = forwarder;
			this.cat = cat;
		}

		// This is called when a response is received.
		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			if (connection != null)
				connection.close();

			if (update instanceof GET_STATUS_DONE) {

				System.err.println("Received: " + update.getClass().getName() + ", Success: " + update.getSuccessful()
						+ ", ErrNO:   " + update.getErrorNum() + ", Message: " + update.getErrorString());

				
				
				StatusCategory status = ((GET_STATUS_DONE) update).getStatus();
				if(status == null)
				{
					System.err.println("Received a null Status.");
				}
				StatusInfo sinfo = new StatusInfo(System.currentTimeMillis(), cat, status);
				System.err.println("Received: Status: "+status.getClass().getName());
				System.err.println("Received: Status: "+status);
				
				try {
					forwarder.dataUpdate(sinfo);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Error forwarding: " + e);
				}

			} else if (update instanceof GET_STATE_MODEL_DONE) {

				GET_STATE_MODEL_DONE gsmdone = (GET_STATE_MODEL_DONE)update;
				
				Map smvars = gsmdone.getVariables();
				Object wvar = smvars.get("WEATHER");
				System.err.println("SMS: Weather = "+(wvar != null ? wvar.getClass().getName():"null")+" "+wvar);
			
				StateModelVariableStatus sms = new StateModelVariableStatus(smvars);			
				sms.setTimeStamp(System.currentTimeMillis());
				
				StatusInfo sinfo = new StatusInfo(System.currentTimeMillis(), "SM", sms);
			
				try {
					forwarder.dataUpdate(sinfo);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Error forwarding: " + e);					
				}
				
			} else if (update instanceof GET_SEEING_DONE) {
				
				GET_SEEING_DONE gsd = (GET_SEEING_DONE)update;
				
				Vector samples = gsd.getSeeingData();
				
				SeeingHistoryStatus shs = new SeeingHistoryStatus(samples);
				
				// dump the last sample time into temp file
				if (samples.size() > 0) {
					SeeingStatus last = (SeeingStatus)samples.get(samples.size()-1);
					try {
						saveLastTime(last.getTimeStamp());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				StatusInfo sinfo = new StatusInfo(System.currentTimeMillis(), "SEEING", shs);
				
				try {
					forwarder.dataUpdate(sinfo);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Error forwarding: " + e);					
				}
				
			} else if (update instanceof ID_DONE) {
				//ID_DONE iddone = (ID_DONE)update;
				//InternalStatus intt = new InternalStatus();
				//intt.setAgentActivity(iddone.getAgentActivity());
				//StatusInfo sinfo = new StatusInfo(System.currentTimeMillis(), "IDD", iddone.
				
			} else {
				System.err.println("CAMP Error: Unexpected class: " + update);
				return;

			}
		}

		// This is called if an exception occurs during protocol implementation.
		@Override
		public void failed(Exception e, IConnection connection) {
			e.printStackTrace();
			// System.err.println("Close connection.");
			if (connection != null)
				connection.close();
		}

	} // [MyStatusHandler]

}
