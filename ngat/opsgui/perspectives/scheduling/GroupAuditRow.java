/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

/** Data for a row in the group audit table.
 * @author eng
 *
 */
public class GroupAuditRow {
	
	
	
	public int sweepNumber;

    public long sweepTime;
	
    public int status;
    
    /** Additional context information.*/
    public String extra;

	/**
	 * 
	 */
	public GroupAuditRow(GroupAuditEntry entry) {
		this.sweepNumber = entry.sweepNumber;
		this.sweepTime = entry.sweepTime;
		this.status = entry.status;
		this.extra = entry.extra;
	}
    
    
}
