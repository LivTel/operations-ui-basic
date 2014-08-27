/**
 * 
 */
package ngat.rcsgui.test;

import ngat.message.base.ACK;
import ngat.message.base.COMMAND;
import ngat.message.base.COMMAND_DONE;
import ngat.net.SocketConnection;

/**
 * @author eng
 *
 */
public class JMSCLient1 {

	 private String name;

	    private String host;

	    private int port;

	    private COMMAND command;
	    
	    private COMMAND_DONE data;

	    private long timeout;

	    public JMSCLient1(String name, String host, int port, COMMAND command, long timeout) {
	        this.name = name;
	        this.host = host;
	        this.port = port;
	        this.command = command;
	        this.timeout = timeout;
	    }

	    public COMMAND_DONE getData() {
	        return data;
	    }

	    public void run() throws Exception {

	        SocketConnection sock = new SocketConnection(host, port);
	        sock.open();

	        log("Opened socket: "+host+":"+port);
	        log("Sending command "+command.getClass().getName()+" with t/o: "+timeout);
	        sock.send(command);

	        boolean finished = false;
	        long endtime = System.currentTimeMillis()+timeout+5000L;

	        log("Waiting reply...");
	        while (! finished) {
	            
	            Object reply = sock.receive(timeout);
	            
	            log("Received: "+reply.getClass().getName());
	            if (reply instanceof ACK) {
	                ACK ack = (ACK)reply;
	                int ttc = ack.getTimeToComplete();
	                endtime += ttc + 5000L;
	                log("Ack timeout: "+ttc);
	            } else if
	                  (reply instanceof COMMAND_DONE) {
	                COMMAND_DONE done = (COMMAND_DONE)reply;
	                boolean success = done.getSuccessful();
	                log("Success: "+success);
	                if (success) {
	                    data = done;
	                    log("Completion data..."+done);
	                } else {
	                    log("Error: "+done.getErrorNum()+" : "+done.getErrorString());
	                }
	                finished = true;
	                log("Completed, exiting");
	                // breakout
	                continue; 
	            }
	        }


	    }
	    
	    public void log(String message) {
	        long time = System.currentTimeMillis();
	        System.err.printf("%tF %tT : JMS: %10s : %s \n", time, time, name, message);
	    }

	    public static void main(String args[]) {

	        try {
	            
	            String name = args[0];

	            String host = args[1];
	            int port = Integer.parseInt(args[2]);
	            long timeout = (Integer.parseInt(args[3]));
	            COMMAND command = new COMMAND("test");

	            JMSCLient1 jms = new JMSCLient1(name, host, port, command, timeout);
	            jms.run();

	        } catch (Exception e) {
	            e.printStackTrace();
	        }


	    }
	
}
