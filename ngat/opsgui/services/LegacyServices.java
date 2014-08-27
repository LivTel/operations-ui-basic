/**
 * 
 */
package ngat.opsgui.services;

import java.rmi.Naming;

import ngat.message.GUI_RCS.GET_STATE_MODEL;
import ngat.message.GUI_RCS.GET_STATUS;
import ngat.message.GUI_RCS.ID;
import ngat.message.base.COMMAND;
import ngat.net.ConnectionFactory;
import ngat.net.IConnection;
import ngat.net.SocketConnection;
import ngat.net.UnknownResourceException;
import ngat.net.camp.CAMPResponseHandler;
import ngat.net.camp.QueryThread;
import ngat.opsgui.components.AuxSystemsSummaryPanel;
import ngat.oss.transport.RemotelyPingable;

/** Controls a host of legacy services until they are migrated.
 * @author eng
 *
 */
public class LegacyServices {

	public static final long PING_INTERVAL = 30000L;
	
	private CAMPResponseHandler stateModelHandler;
	
	private CAMPResponseHandler idHandler;
	
	private CAMPResponseHandler ocrHandler;
	
	private QueryThread stateModelThread;
	
	private QueryThread idThread;
	
	private QueryThread ocrThread;
	
	private String smpUrl;
	
	private String schedUrl;
	
	private String teaUrl;
	
	private String phase2Url;
	
	private PingThread pingThread;
	
	private AuxSystemsSummaryPanel auxPanel;
	
	/**
	 * @param host
	 * @param port
	 * @param stateModelHandler
	 * @param idHandler
	 * @param ocrHandler
	 */
	public LegacyServices(String host, int port, 
			CAMPResponseHandler stateModelHandler, 
			CAMPResponseHandler idHandler,  
			CAMPResponseHandler ocrHandler,
			String smpUrl, String schedUrl, String teaUrl, String phase2Url, AuxSystemsSummaryPanel auxPanel) {	
	
		ConnectionFactory cfy = new LegacyConnectionFactory(host, port);
		
		COMMAND getStateComd = new GET_STATE_MODEL("opsgui");
		COMMAND getIdCmd = new ID("opsgui");
		COMMAND getOcrCmd = new GET_STATUS("opsgui");
		getOcrCmd.setId("X_OCR");
		
		stateModelThread = new QueryThread("");
		stateModelThread.setCommand(getStateComd);
		stateModelThread.setConnectionFactory(cfy);
		stateModelThread.setConnectionId("GET_STATE_MODEL");
		stateModelThread.setPollingInterval(10000L);
		stateModelThread.setResponseHandler(stateModelHandler);
		
		idThread = new QueryThread("");
		idThread.setCommand(getIdCmd);
		idThread.setConnectionFactory(cfy);
		idThread.setConnectionId("ID");
		idThread.setPollingInterval(10000L);
		idThread.setResponseHandler(idHandler);
	
		/*ocrThread = new QueryThread("");
		ocrThread.setCommand(getOcrCmd);
		ocrThread.setConnectionFactory(cfy);
		ocrThread.setConnectionId("GET_STATUS");
		ocrThread.setPollingInterval(60000L);
		ocrThread.setResponseHandler(ocrHandler);*/
	
		pingThread = new PingThread(smpUrl, schedUrl, teaUrl, phase2Url, auxPanel);
		
	}

public void startServices() {
	
	stateModelThread.start();
	idThread.start();
	//ocrThread.start();
	pingThread.start();
	
}



	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		
		
		

	}

	/**
	 * @author eng
	 *
	 */
	public class LegacyConnectionFactory implements ConnectionFactory {

		
		private String host;
	
		
		private int port;
	
		
		
		/**
		 * @param stateModelHost
		 * @param idHost
		 * @param stateModelPort
		 * @param idPort
		 */
		public LegacyConnectionFactory(String host, int port) {
			super();
			this.host = host;
			this.port = port;
		}



		/* (non-Javadoc)
		 * @see ngat.net.ConnectionFactory#createConnection(java.lang.String)
		 */
		@Override
		public IConnection createConnection(String cid) throws UnknownResourceException {
				return new SocketConnection(host, port);
			//throw new UnknownResourceException("Unknown connection id: "+cid);
		}

	}

	public class PingThread extends Thread {
		
		private String smpUrl;
		
		private String schedUrl;
		
		private String teaUrl;
		
		private String phase2Url;
		
		private AuxSystemsSummaryPanel auxPanel;
	
		

		/**
		 * @param smpUrl
		 * @param schedUrl
		 * @param teaUrl
		 * @param auxPanel
		 */
		public PingThread(String smpUrl, String schedUrl, String teaUrl, String phase2Url, AuxSystemsSummaryPanel auxPanel) {
			super();
			this.smpUrl = smpUrl;
			this.schedUrl = schedUrl;
			this.teaUrl = teaUrl;
			this.phase2Url = phase2Url;
			this.auxPanel = auxPanel;
		}



		@Override
		public void run() {
			
			while (true) {
				
				// SMP
				try {System.err.println("LOOKUP: "+smpUrl);
					RemotelyPingable smp = (RemotelyPingable)Naming.lookup(smpUrl);
					System.err.println("PING PING PING PING PING PING PING PING PING PING PING PING SMP: "+smp);
					smp.ping();
					auxPanel.updateSmpStatus(2); // online
				} catch (Exception e) {
					auxPanel.updateSmpStatus(1); // offline
				}
				
				// SCHED
				try {System.err.println("LOOKUP: "+schedUrl);
					RemotelyPingable sched = (RemotelyPingable)Naming.lookup(schedUrl);
					System.err.println("PING PING PING PING PING PING PING PING PING PING PING PING  SCHED: "+sched);
					sched.ping();
					auxPanel.updateSchedStatus(2);
				} catch (Exception e) {
					auxPanel.updateSchedStatus(1); // offline
				}
				
				// TEA
				try {
					RemotelyPingable tea = (RemotelyPingable)Naming.lookup(teaUrl);
					tea.ping();
					auxPanel.updateTeaStatus(2);// online
				} catch (Exception e) {
					auxPanel.updateTeaStatus(1); // offline
				}
				
				// BASE
				try {
					RemotelyPingable phase2 = (RemotelyPingable)Naming.lookup(phase2Url);
					phase2.ping();
					auxPanel.updateBaseModelStatus(2); // online
				} catch (Exception e) {
					auxPanel.updateBaseModelStatus(1); // offline
				}
				
				// OCR
				
				
				try {Thread.sleep(PING_INTERVAL);} catch (Exception e) {}
			}
			
		}
		
	}
	
	
}
