package ngat.opsgui.perspectives.phase2;

import java.util.List;
import java.util.Vector;
import java.util.Map;
import java.util.HashMap;

import ngat.phase2.IProposal;
import ngat.phase2.ITag;
import ngat.phase2.IUser;

import ngat.sms.GroupItem;


public class Phase2 {

    private Map<Long, GroupItem> groups;

    private Map<Long, IProposal> proposals;

    private Map<Long, IUser> users;

    private Map<Long, ITag> tags;

    private List<Phase2CacheUpdateListener> listeners;

    private static Phase2 instance;

    public Phase2() {

	groups    = new HashMap<Long, GroupItem>();
	proposals = new HashMap<Long, IProposal>();
	users     = new HashMap<Long, IUser>();
	tags      = new HashMap<Long, ITag>();	

	listeners = new Vector<Phase2CacheUpdateListener>();

    }
    
    public static Phase2 getPhase2() {
	if (instance == null)
	    instance = new Phase2();
	return instance;
    }

    public void addPhase2CacheUpdateListener(Phase2CacheUpdateListener l) {
	if (listeners.contains(l))
	    return;
	listeners.add(l);	
    }

    public void removePhase2CacheUpdateListener(Phase2CacheUpdateListener l) {
        if (! listeners.contains(l))
            return;
        listeners.remove(l);
    }


    public GroupItem addGroup(GroupItem group) {

	boolean added = false;

	long gid = group.getID();

	// group already in table, return ref.
	if (groups.containsKey(gid))
	    return groups.get(gid);

	// new group, add
	added = true;
	groups.put(gid, group);

	// Link new group to an existing proposal in table or add
	IProposal proposal = group.getProposal();
	if (proposal != null) {
	    long pid = proposal.getID();
	    if (proposals.containsKey(pid))
		group.setProposal(proposals.get(pid));
	    else
		proposals.put(pid, proposal);
	}

	// Link new group to an existing user in table or add
	IUser user = group.getUser();
        if (user != null) {
            long uid = user.getID();
            if (users.containsKey(uid))
                group.setUser(users.get(uid));
            else
                users.put(uid, user);
        }

	// Link new group to an existing TAG in table or add
	ITag tag = group.getTag();
        if (tag != null) {
            long tid = tag.getID();
            if (tags.containsKey(tid))
                group.setTag(tags.get(tid));
            else
                tags.put(tid, tag);
        }

	if (added)
	    notifyListenersGroupAdded(group);

	return group;

    }

    public GroupItem getGroup(long gid) {
	if (groups.containsKey(gid))
	    return groups.get(gid);
	return null;
    }

    public IProposal getProposal(long pid) {
	if (proposals.containsKey(pid))
            return proposals.get(pid);
        return null;
    }

    public IUser getUser(long uid) {
	if (users.containsKey(uid))
	    return users.get(uid);
	return null;
    }

    public ITag getTag(long tid) {
	if (tags.containsKey(tid))
	    return tags.get(tid);
	return null;
    }

    private void notifyListenersGroupAdded(GroupItem group) {

	try {
	    for (int i = 0; i < listeners.size(); i++) {
		Phase2CacheUpdateListener l = listeners.get(i);
		l.phase2GroupAdded(group);
	    }
	} catch (Exception e) {
	    e.printStackTrace();
	}

    }

    
}