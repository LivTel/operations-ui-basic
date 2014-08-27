package ngat.rcsgui.stable;


import ngat.net.datalogger.*;
import ngat.message.GUI_RCS.*;
import ngat.util.*;
import ngat.net.*;

import java.io.*;
import java.net.*;
import java.util.*;

public class DataRelay {
    
    // ---------------------
    //  Arglist:-
    // ---------------------
    // 0 - source host 
    // 1 - source port
    // 2 - incoming host
    // 3 - incoming port
    // 4 - forward host
    // 5 - forward port
    // ---------------------
    
    public static final String DEFAULT_RCS_HOST     = "occ";
    public static final int    DEFAULT_RCS_PORT     = 9000;
    
    public static final String DEFAULT_RECV_HOST    = "tmcproxy";
    public static final int    DEFAULT_RECV_PORT    = 9100;
    
    public static final String DEFAULT_ONGOING_HOST = "archive";
    public static final int    DEFAULT_ONGOING_PORT = 6500;
    
    public static final long TIME = 5*60*1000L;

    /** Our ID.*/
    String cid;

    /** RCS Host.*/
    String shost;
    
    /** RCS Control port.*/
    int    sport;
    
    /** Relay host (as seen by RCS).*/
    String ihost;
    
    /** Relay port.*/
    int    iport;

    /** The list of clients who want telemtry data.*/
    protected List targets;

    /** A datalogger.*/
    DataLogger dataLogger;

    /** Receiver thread.*/
    DataReceiverThread drt;

    /** Keep RCS contact going.*/
    Contactor c;

    /** Create  a DR.*/
    public DataRelay(String cid,
		     String shost,
		     int    sport,
		     String ihost,
		     int    iport) throws Exception {

	
	targets = new Vector();
	
	dataLogger = new DataLogger();
	drt = new DataReceiverThread(dataLogger, iport);
	DataWriter dw = new DataWriter();
	dataLogger.addUpdateListener(dw);

	// Our own handler
	dataLogger.addUpdateListener(new RelayTelemetryHandler());
	
	c = new Contactor(cid, shost, sport, ihost, iport);

    }

    /** Start the relay.*/
    public void start() throws Exception {

	dataLogger.start();
	drt.start();

	(new Thread(c)).start();
	    
    }

    public static void main(String[] args) {
	
	CommandTokenizer ct = new CommandTokenizer("--");
	ct.parse(args);

	ConfigurationProperties config = ct.getMap();
	
	try {
	    
	    System.err.println("DataRelay: "+args.length+" args");
	    
	    String cid = config.getProperty("cid", "TMC_DATA_RELAY");

	    // Source.
	    String shost = config.getProperty("rcs-host", DEFAULT_RCS_HOST);
	    int    sport = config.getIntValue("rcs-port", DEFAULT_RCS_PORT);
	    
	    // Incoming.
	    String ihost = config.getProperty("recv-host", DEFAULT_RECV_HOST);
	    int    iport = config.getIntValue("recv-port", DEFAULT_RECV_PORT);
	  
	    // Primary Forwarding.	    
	    String fhost = config.getProperty("ongoing-host", DEFAULT_ONGOING_HOST);
	    int    fport = config.getIntValue("ongoing-port", DEFAULT_ONGOING_PORT);
	    
	  
	    DataRelay relay = new DataRelay(cid, shost, sport, ihost, iport);

	    // Add primary target
	    relay.addTarget(cid, 
			    new ConnectionSetupInfo(System.currentTimeMillis(), 
						    ConnectionSetupInfo.UDP,
						    fhost, fport),
			    new Vector());
	    
	    relay.start();
	 
	    
	} catch (Exception e) {
	    e.printStackTrace();
	    return;
	}

    }

    /** Add this client. If the ID is already known we dont add it.*/
    public void addTarget(String clientId, ConnectionSetupInfo conset, Vector wants) throws IOException {
	
	if (targets.contains(clientId)) 
	    return;

	System.err.println("RELAY_TELEMETRY HANDLER: Adding target: "+clientId+", "+conset+" wants:"+wants);
	
	switch (conset.type) {
	case ConnectionSetupInfo.UDP:
	    DataForwarder df = new DataForwarder(wants, conset.host, conset.port);	
	    dataLogger.addUpdateListener(df);	
	    targets.add(clientId);
	    break;
	case ConnectionSetupInfo.CAMP:
	    DataCampSender ds = new DataCampSender(wants, conset.host, conset.port, 10000L);
	    dataLogger.addUpdateListener(ds);	
	    targets.add(clientId);
	    break;
	default:
	    // We dont handle other protocols yet.
	}
    }
    
    
    public static class Contactor implements Runnable {

	String client;
	String shost;
	int    sport;
	String ihost;
	int    iport;

	ConnectionSetupInfo conset;

	Contactor(String client, String shost, int sport, String ihost, int iport) {
	    this.client = client;
	    this.shost = shost;
	    this.sport = sport;
	    this.ihost = ihost;
	    this.iport = iport;

	    conset = new ConnectionSetupInfo(System.currentTimeMillis(), ConnectionSetupInfo.UDP, ihost, iport);
	}

	@Override
	public void run() {

	    while (true) {
		TELEMETRY tel = new TELEMETRY("test");
		tel.setConnect(conset);
		tel.setClientId(client);

		// be more seelctive here
		Vector v = new Vector();
		v.add(ObservationInfo.class);
		v.add(ObservationStatusInfo.class);
		v.add(ExposureInfo.class);
		v.add(ReductionInfo.class);
		v.add(LogInfo.class);	       
		tel.setWants(v);

		System.err.println("Contacting OCC on: "+shost+":"+sport);
		final TelemetryClient client = new TelemetryClient(new SocketConnection(shost,sport), tel);
		(new Thread(client)).start();
		
		try {Thread.sleep(TIME);} catch (InterruptedException ix) {}
	    }
	}
    } // contactor



    public static class UDPContactor implements Runnable {

	String client;
	String shost;
	int    sport;
	String ihost;
	int    iport;
	
	ConnectionSetupInfo conset;
	DatagramSocket socket;
	InetAddress address;
	
	UDPContactor(String client, String shost, int sport, String ihost, int iport) throws Exception {
	    this.client = client;
	    this.shost = shost;
	    this.sport = sport;
	    this.ihost = ihost;
	    this.iport = iport;

	    conset = new ConnectionSetupInfo(System.currentTimeMillis(), ConnectionSetupInfo.UDP, ihost, iport);

	    socket = new DatagramSocket();
	    address = InetAddress.getByName(shost);

	}

	@Override
	public void run() {

	    while (true) {
		TELEMETRY tel = new TELEMETRY("test");
		tel.setConnect(conset);
		tel.setClientId(client);

		// be more seelctive here
		Vector v = new Vector();
		v.add(ObservationInfo.class);
		v.add(ObservationStatusInfo.class);
		v.add(ExposureInfo.class);
		v.add(ReductionInfo.class);
		v.add(LogInfo.class);	       
		tel.setWants(v);

		try {
		    ByteArrayOutputStream baos = new ByteArrayOutputStream(10000);	    
		    ObjectOutputStream    oos  = new ObjectOutputStream(baos);
		    //System.err.println(sdf.format(time)+ " Built OOS = "+oos);
		    oos.flush();
		    //System.err.println(sdf.format(time)+ " OOS flushed (1) - stream header OK");
		    oos.writeObject(tel);
		    
		    //System.err.println("DFwd::Wrote object: "+(data != null ? data.getClass().getName() : "NULL"));
		    
		    oos.flush();
		    oos.close();
		    //System.err.println(sdf.format(time)+ " OOS flushed (2) - record OK");
		    byte[] buffer = baos.toByteArray();
		    
		    //System.err.println("DFwd::Sending buffer: size: "+buffer.length);
		    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, sport);	    
		    socket.send(packet);
		
		    System.err.println("UDPContactor::Forwarded connection request packet to DR at: "+address+":"+sport);
		
		} catch (Exception e) {
		    e.printStackTrace();	
		}
		

		try {Thread.sleep(TIME);} catch (InterruptedException ix) {}
	    }
	}
    } // UDP contactor





    private class RelayTelemetryHandler implements DataLoggerUpdateListener {

	/** Handle a telemetry data update.*/
	@Override
	public void dataUpdate(Object data) {
	    
	    System.err.println("RELAY_TELEMETRY HANDLER: "+(data != null ? data.getClass().getName() : "NULL"));
	    if (data instanceof TELEMETRY) {

		TELEMETRY tel = (TELEMETRY)data;
		
		try {
		    DataRelay.this.addTarget(tel.getClientId(), tel.getConnect(), tel.getWants());
		} catch (Exception e) {
		    e.printStackTrace();
		}
	    }

	}
		

    }


}
