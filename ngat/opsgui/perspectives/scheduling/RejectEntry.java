package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;

public class RejectEntry extends TableEntry {

    /** The rejected group.*/
    private GroupItem group;

    /** Reason for group's rejection.*/
    private String reason;

	/**
	 * @param group
	 * @param reason
	 */
	public RejectEntry(GroupItem group, String reason) {
		super();
		this.group = group;
		this.reason = reason;
	}

	/**
	 * @return the group
	 */
	public GroupItem getGroup() {
		return group;
	}

	/**
	 * @param group the group to set
	 */
	public void setGroup(GroupItem group) {
		this.group = group;
	}

	/**
	 * @return the reason
	 */
	public String getReason() {
		return reason;
	}

	/**
	 * @param reason the reason to set
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}

  
}