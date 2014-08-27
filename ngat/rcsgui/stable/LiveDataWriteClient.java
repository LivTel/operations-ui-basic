package ngat.rcsgui.stable;

import ngat.rcs.scm.collation.StateModelVariableStatus;
import ngat.util.*;
import ngat.net.*;
import ngat.net.camp.*;
import ngat.message.base.*;
import ngat.message.GUI_RCS.*;
import ngat.message.RCS_TCS.*;

import java.io.*;
import java.util.*;

public class LiveDataWriteClient {

	public static void main(String args[]) {

		try {

			ConfigurationProperties cfg = CommandTokenizer.use("--").parse(args);

			String cat = cfg.getProperty("cat", "MECHANISM");
			String host = cfg.getProperty("host", "occ");
			int port = cfg.getIntValue("port", 9110);

			String fhost = cfg.getProperty("fhost", "telescope");
			int fport = cfg.getIntValue("fport", 6567);

			//DataForwarder forwarder = new DataForwarder(new Vector(), fhost, fport);

			String base = cfg.getProperty("base", System.getProperty("user.home"));

			DataFileSingleStreamWriter writer = new DataFileSingleStreamWriter(new File(base+"/"+cat.toLowerCase()+".d1"));

			// (required for proper decoding of TCS replies).
			TCS_Status.mapCodes();

			IConnection connection = new SocketConnection(host, port);
			
			COMMAND command = null;

			if (cat.equals("ID")) {
				command = new ID("test");
			} else if (cat.equals("SM")) {
				command = new GET_STATE_MODEL("test");
			} else {
				GET_STATUS getStat = new GET_STATUS("test");
				getStat.setCategory(cat);
				command = getStat;
			}

			CAMPResponseHandler handler = new MyStatusHandler(writer, cat);

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
			}

		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}

	// Here is an example of a handler which handles a specific status key, more
	// likely want to handle
	// all the keys in a given status category.

	public static class MyStatusHandler implements CAMPResponseHandler {

		// feed onto host,port via UDP
		DataFileSingleStreamWriter writer;

		String cat;

		MyStatusHandler(DataFileSingleStreamWriter writer, String cat) {
			this.writer = writer;
			this.cat = cat;
		}

		// This is called when a response is received.
		@Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

			if (connection != null)
				connection.close();

			if (update instanceof GET_STATUS_DONE) {

				System.err.println("Received: " + update.getClass().getName() + "Success: " + update.getSuccessful()
						+ "ErrNO:   " + update.getErrorNum() + "Message: " + update.getErrorString());

				StatusCategory status = ((GET_STATUS_DONE) update).getStatus();
				StatusInfo sinfo = new StatusInfo(System.currentTimeMillis(), cat, status);

				try {
					writer.dataUpdate(sinfo);
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
					writer.dataUpdate(sinfo);
				} catch (Exception e) {
					e.printStackTrace();
					System.err.println("Error forwarding: " + e);					
				}
				
			} else if (update instanceof ID_DONE) {

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
