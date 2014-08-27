/**
 * 
 */
package ngat.opsgui.perspectives.phase2;

import java.util.List;
import java.util.Vector;

import ngat.sms.GroupItem;

/**
 * @author eng
 *
 */
public class GroupSelection {

	private GroupItem selectedGroup;
	
	private List<GroupSelectionListener> listeners;

	/**
	 * 
	 */
	public GroupSelection() {
		super();
		listeners = new Vector<GroupSelectionListener>();
	}
	
	public void addGroupSelectionListener(GroupSelectionListener l) {
		if (listeners.contains(l))
			return;
		listeners.add(l);		
	}
	
	public void removeGroupSelectionListener(GroupSelectionListener l) {
		if (!listeners.contains(l))
			return;
		listeners.remove(l);		
	}
	
	public void selectGroup(GroupItem group) {
		selectedGroup = group;
		notifyListenersGroupSelectionChanged(group);
	}

	private void notifyListenersGroupSelectionChanged(GroupItem group) {
		for (int i = 0; i < listeners.size(); i++) {
			GroupSelectionListener l = listeners.get(i);
			try {
				l.groupSelectionChanged(group);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
}
