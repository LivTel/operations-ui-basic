package ngat.rcsgui.stable;

import java.io.IOException;
import java.net.ConnectException;

import javax.swing.JOptionPane;

import ngat.message.GUI_RCS.START_DONE;
import ngat.message.GUI_RCS.SYSTEM_DONE;
import ngat.message.base.COMMAND;
import ngat.message.base.COMMAND_DONE;
import ngat.net.ConnectionFactory;
import ngat.net.IConnection;
import ngat.net.SocketConnection;
import ngat.net.UnknownResourceException;
import ngat.net.camp.CAMPClient;

public class SimpleCAMPClient extends CAMPClient {

	String operationDescription;
	
	public SimpleCAMPClient(String operationDescription, String host, int port, COMMAND command) {
		super(new InternalConnectionFactory(host, port), "any", command);
		this.operationDescription = operationDescription;
	}

	@Override
	public void failed(Exception e, IConnection connection) {
		
		if (connection != null)
		    connection.close();
		
		e.printStackTrace();	
		displayMessage("Failed due to: "+e.getMessage(), operationDescription, JOptionPane.ERROR_MESSAGE);
		
	}

	@Override
	public void handleUpdate(COMMAND_DONE update, IConnection connection) {
		
		if (connection != null)
			connection.close();
		
		if (update == null) {
			displayMessage("unexpected NULL response", operationDescription, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		if (! (update instanceof START_DONE) &&
		    ! (update instanceof SYSTEM_DONE)) {
		   
			displayMessage("Unexpected response to message: "+update.getClass().getSimpleName(), operationDescription, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		if (!update.getSuccessful()) {
			displayMessage("Error response: "+update.getErrorNum()+":"+update.getErrorString(), operationDescription, JOptionPane.ERROR_MESSAGE);
		    return;
		}
		
		displayMessage("Command received and executed", operationDescription, JOptionPane.INFORMATION_MESSAGE);

	}
	
	private void displayMessage(String message, String title, int type) {
		JOptionPane.showMessageDialog(null, message, title, type);
	}

	private static class InternalConnectionFactory implements ConnectionFactory {

		private String host;

		private int port;

		public InternalConnectionFactory(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}

		@Override
		public IConnection createConnection(String id)
				throws UnknownResourceException {
			return new SocketConnection(host, port);
		}

	}

}
