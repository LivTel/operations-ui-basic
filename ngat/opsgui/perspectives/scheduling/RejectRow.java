/**
 * 
 */
package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;

/** Stores scheduler sweep rejection information for a group.
 * @author eng
 *
 */
public class RejectRow {

	public GroupItem group;
	
	public String error;

	public RejectRow(RejectEntry entry) {
		this.group = entry.getGroup();
		this.error = entry.getReason();
	}
}
