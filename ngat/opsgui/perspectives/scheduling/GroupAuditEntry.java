/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

/**
 * @author eng
 *
 */
public class GroupAuditEntry {
	
	public static final int CANDIDATE = 1;
	
	public static final int REJECT = 2;
	
	public static final int SELECTED = 3;
	
	public int sweepNumber;

    public long sweepTime;
	
    public int status;
    
    /** Additional context information.*/
    public String extra;

	/**
	 * @param sweepNumber
	 * @param sweepTime
	 * @param status
	 * @param extra
	 */
	public GroupAuditEntry(int sweepNumber, long sweepTime, int status, String extra) {
		super();
		this.sweepNumber = sweepNumber;
		this.sweepTime = sweepTime;
		this.status = status;
		this.extra = extra;
	}
    
}
