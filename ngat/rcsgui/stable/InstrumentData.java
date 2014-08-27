/**
 * 
 */
package ngat.rcsgui.stable;

/**
 * @author eng
 *
 */
public class InstrumentData {

	public static final int ONLINE_OKAY = 1;
	public static final int ONLINE_WARN = 2;
	public static final int ONLINE_FAIL = 3;
	public static final int OFFLINE = 4;
	public static final int DISABLED = 5;
	
	public long time;
	
	public int state;

	/**
	 * @param time
	 * @param state
	 */
	public InstrumentData(long time, int state) {
		super();
		this.time = time;
		this.state = state;
	}
	
	
	
}
