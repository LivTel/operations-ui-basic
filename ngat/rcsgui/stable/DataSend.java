package ngat.rcsgui.stable;

import java.io.*;
import java.net.*;
import ngat.util.*;

public class DataSend {

    public static void main(String[] args) {

	try {
	    
	    CommandTokenizer parser = new CommandTokenizer("--");
	    parser.parse(args);
	    ConfigurationProperties config = parser.getMap();


	    int    port = config.getIntValue("port");

	    String host = config.getProperty("host");

	    int    size = config.getIntValue("size");

	    long delay = config.getLongValue("delay");

	    DatagramSocket socket = new DatagramSocket();
	    
	    InetAddress address = InetAddress.getByName(host);
	    
	    System.err.println("UDP Source Bound");
	    
	    while (true) {
	       
		ByteArrayOutputStream baos = new ByteArrayOutputStream((size+1)*4);	    
		ObjectOutputStream    oos  = new ObjectOutputStream(baos);
		
		oos.writeInt(size);
		oos.flush();
		for (int i = 0; i < size; i++) {		 
		    oos.writeInt(i);	
		    oos.flush();
		}
	
		oos.close();
	
		byte[] buffer = baos.toByteArray();

		System.err.println("Sending buffer: size: "+buffer.length);
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);	    
		socket.send(packet);

		try {Thread.sleep(delay);} catch (InterruptedException x) {}
		
	    }

	} catch (Exception e) {
	    System.err.println("Exception: "+e);
	    return;
	}

    }

}
