/**
 * 
 */
package ngat.rcsgui.stable;

/**
 * @author eng
 *
 */
public class CilRow {

	public static final int SENT      = 1;
	public static final int ACTIONED  = 2;
	public static final int COMPLETED = 3;
	public static final int FAILED    = 4;
	public static final int TIMED_OUT = 5;
	
	// message, sentAt, elapsed, status
	
	public String message;
	
	public long sendTime;
	
	public long elapsedTime;
	
	public int status;

	
}
