package ngat.rcsgui.stable;

import java.io.*;
import java.net.*;
import ngat.util.*;

public class DataReader {
  
    public static void main(String[] args) {
	
	CommandTokenizer parser = new CommandTokenizer("--");
	parser.parse(args);
	ConfigurationProperties config = parser.getMap();


	try {
	
	    int    port = config.getIntValue("port");
	    
	    int    size =  config.getIntValue("size");

	    DatagramSocket inSocket = new DatagramSocket(port);

	    while (true) {
		
		byte[] buffer = new byte[size];
		DatagramPacket packet = new DatagramPacket(buffer, size);
		
		System.err.println("0. Waiting for packet.");
		inSocket.receive(packet);
		System.err.println("1. Got packet");
		buffer = packet.getData();	
		System.err.println("2. Got data: buffer size: "+buffer.length);
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		System.err.println("3. Opened BAIS");
		ObjectInputStream ois  = new ObjectInputStream(bais);	
		System.err.println("4. Opened OIS");
		
		int nn = ois.readInt();
		System.err.println("["+nn+"]");
		for (int i = 0; i < nn; i++) {
		    int ii = ois.readInt();	
		    System.err.print("<"+ii+">");
		}
		System.err.println("[***]");
	    }
	} catch (Exception ex) {		
	    System.err.println("Exception: "+ex);
	}    	    
    }
    
}
