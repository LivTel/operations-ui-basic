/**
 * 
 */
package ngat.smsgui;

import ngat.sms.GroupItem;

/**
 * @author eng
 *
 */
public class HistoryRow {

	public static final int RUNNING = 1;
	public static final int FAILED = 2;
	public static final int COMPLETED = 3;
	
	public int sweep;
	
	public long time;
	
	public GroupItem group;
	
	public double score;
	
	public int status = 0;
	
	public long completion;
	
	public boolean success;
	
	public String errmsg;
	
}
