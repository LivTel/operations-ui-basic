/**
 * 
 */
package ngat.opsgui.util;

/** A class to represent a historic state value
 * @author eng
 *
 */
public class StateHistory {

	private long time;
	
	private int state;

	/**
	 * @param time
	 * @param state
	 */
	public StateHistory(long time, int state) {
		super();
		this.time = time;
		this.state = state;
	}

	/**
	 * @return the time
	 */
	public long getTime() {
		return time;
	}

	/**
	 * @return the state
	 */
	public int getState() {
		return state;
	}
	
	@Override
	public String toString(){
		return ""+time+";"+state;
	}
	
}
