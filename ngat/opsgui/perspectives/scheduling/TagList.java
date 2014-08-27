package ngat.opsgui.perspectives.scheduling;

import java.util.List;
import java.util.Vector;

import ngat.sms.GroupItem;

/**
 *  Holds details of a List of groups which are tagged.
 */
public class TagList {

    /** The list of groups.*/
    private List<GroupItem> groupList;

    /** A list of listeners for TagList update events.*/
    private List<TagListUpdateListener> listeners;

    /** Create a TagList.*/
    public TagList() {
	groupList = new Vector<GroupItem>();
	listeners = new Vector<TagListUpdateListener>();
    }

    /** Add a listener to the list of listeners.*/
    public void addTagListUpdateListener(TagListUpdateListener l) {
	if (listeners.contains(l))
	    return;
	listeners.add(l);
    }

    /** Remove a listener from the list of listeners.*/
    public void removeTagListUpdateListener(TagListUpdateListener l) {
	if (! listeners.contains(l))
            return;
	listeners.remove(l);
    }

    public void tagGroup(GroupItem group) {
	if (groupList.contains(group))
            return;
        
	groupList.add(group);

        for (int i = 0; i < listeners.size(); i++) {
            TagListUpdateListener l = listeners.get(i);
            try {
                l.groupTagged(group);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    public void untagGroup(GroupItem group) {
	if (! groupList.contains(group))
	    return;
	
	groupList.remove(group);

	for (int i = 0; i < listeners.size(); i++) {
            TagListUpdateListener l = listeners.get(i);
            try {
                l.groupUntagged(group);
            } catch (Exception e) {
                e.printStackTrace();
            }
	}
    }

    public void clearTags() {
	if (groupList.size() == 0)
	    return;

	groupList.clear();

	for (int i = 0; i < listeners.size(); i++) {
	    TagListUpdateListener l = listeners.get(i);
	    try {
		l.tagsCleared();
	    } catch (Exception e) {
		e.printStackTrace();
	    }
	}
    }

    /** @return a reference to the List of tagged groups.*/
    public List<GroupItem> getTaggedGroups() {
	return groupList;
    }

    public boolean isTagged(GroupItem group) {
	return groupList.contains(group);
    }

    
}