/**
 * 
 */
package ngat.rcsgui.stable;

import ngat.phase2.IExecutionFailureContext;
import ngat.sms.GroupItem;

/**
 * @author eng
 *
 */
public class OpsTableRow {
	
	public GroupItem group;

	public boolean hasCompleted;
	
	public IExecutionFailureContext error;

	public long exec;
	
	public long startTime;
	
	public long completionTime;

	public long shutter;

    public String details;
	
}
