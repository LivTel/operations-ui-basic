package ngat.rcsgui.stable;

import java.util.Date;

import ngat.autoguider.command.StatusTemperatureGetCommand;
import ngat.net.*;

public class AgChecker {

	private String host;

	private int port;

	public AgChecker(String host, int port) {
		this.host = host;
		this.port = port;
	}

	public void checkAgActive() throws Exception {

		TelnetSocketConnection telnet = new TelnetSocketConnection(host, port);

		telnet.open();

		String mesg = "status guide active";

		try {
			telnet.send(mesg, 20000L);
			String reply = (String) telnet.receive(20000L);
		} catch (Exception e) {
			throw e;
		} finally {
			telnet.close();
		}

		telnet = null;

	}

	public double checkAgTemp() throws Exception {

		StatusTemperatureGetCommand command = new StatusTemperatureGetCommand(host, port);
		command.run();
		if (command.getRunException() != null) {
			throw command.getRunException();
		} else {

			command.getCommandFinished();
			command.getParsedReplyOK();
			Date timestamp = command.getTimestamp();
			double temp = command.getCCDTemperature();
			return temp+273.0;
		}

	}

	public static void main(String[] args) {

		// status guide active
		try {
			AgChecker ac = new AgChecker(args[0], Integer.parseInt(args[1]));
			ac.checkAgActive();
			System.err.println("AG: OKAY");
		} catch (Exception e) {
			// e.printStackTrace();
			System.err.println("AG: FAIL");
		}

	}
}