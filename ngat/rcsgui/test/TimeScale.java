/**
 * 
 */
package ngat.rcsgui.test;

/** Represents a TimeScale used for plotting against axes.
 * @author eng
 *
 */
public class TimeScale {

	private long start;
	
	private long end;
	
	/** The period is set to one of a number of set values.*/
	private long period;
	
	/**
	 * @param start
	 * @param end
	 * @param period
	 */
	public TimeScale(long start, long end, long period) {
		this.start = start;
		this.end = end;
		this.period = period;
	}

	/** Move the end point to the specified value and recalculate period.*/
	public void advance(long time) {
		end = time;
		start = end-period;		
		System.err.println("Advance to: "+this.toString());
	}

	/**
	 * @return the start
	 */
	public long getStart() {
		return start;
	}

	/**
	 * @return the end
	 */
	public long getEnd() {
		return end;
	}

	/**
	 * @return the period
	 */
	public long getPeriod() {
		return period;
	}
	
	@Override
	public String toString() {
			return String.format("TS: %tF %tT , %tF %tT \n", start, start, end, end);
	}
	
}
