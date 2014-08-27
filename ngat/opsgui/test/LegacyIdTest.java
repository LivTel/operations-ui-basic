package ngat.opsgui.test;

import ngat.message.GUI_RCS.*;
import ngat.message.base.*;
import ngat.net.*;
import ngat.net.camp.*;


public class LegacyIdTest {

    public static void main(String args[]) {

	try {

	    CAMPResponseHandler idHandler =  new LegacyIdHandler();

	    QueryThread idThread;

	    ConnectionFactory cfy = new LegacyConnectionFactory("ltsim1", 9110);

	    COMMAND getIdCmd = new ID("opsgui");

	    idThread = new QueryThread("");
	    idThread.setCommand(getIdCmd);
	    idThread.setConnectionFactory(cfy);
	    idThread.setConnectionId("ID");
	    idThread.setPollingInterval(10000L);
	    idThread.setResponseHandler(idHandler);
	    
	    idThread.start();




	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    /**
     * @author eng
     *
     */
    public static class LegacyConnectionFactory implements ConnectionFactory {

	private String idHost;

	private int idPort;


	/**
	 * @param idHost
	 * @param idPort
	 */
	public LegacyConnectionFactory(String idHost, int idPort) {
	    super();
	    this.idHost = idHost;
	    this.idPort = idPort;
	}



	/* (non-Javadoc)
	 * @see ngat.net.ConnectionFactory#createConnection(java.lang.String)
	 */
                @Override
				public IConnection createConnection(String cid) throws UnknownResourceException {
		    return new SocketConnection(idHost, idPort);
		    
                }

    }

    public static class LegacyIdHandler implements CAMPResponseHandler {


        /**
         * @param csp
         */
        public LegacyIdHandler() {
	    super();
	}

        /* (non-Javadoc)
         * @see ngat.net.camp.CAMPResponseHandler#failed(java.lang.Exception, ngat.net.IConnection)
         */
        @Override
		public void failed(Exception e, IConnection connection) {
	    e.printStackTrace();
	    connection.close();
        }

        /* (non-Javadoc)
         * @see ngat.net.camp.CAMPResponseHandler#handleUpdate(ngat.message.base.COMMAND_DONE, ngat.net.IConnection)
         */
        @Override
		public void handleUpdate(COMMAND_DONE update, IConnection connection) {

	    if (connection != null)
		connection.close();

	    // logger.log(1,"GUI", "-","-", "Received update: "+update);

	    if (!( update instanceof ID_DONE)) {
		return;
	    }

	    ID_DONE idd = (ID_DONE) update;
	    System.err.println("Received update: "+idd);
	    
	    long uptime = idd.getUptime();
	    //	    System.err.println("LSM: update: Uptime: "+(uptime/1000)+"S, aid = "+aid+", adesc = "+adesc);

        }
    }

}