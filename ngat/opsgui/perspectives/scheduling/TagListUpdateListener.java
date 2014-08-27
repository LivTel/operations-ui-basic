package ngat.opsgui.perspectives.scheduling;

import ngat.sms.GroupItem;

public interface TagListUpdateListener {

    public void groupTagged(GroupItem group) throws Exception;

    public void groupUntagged(GroupItem group) throws Exception;

    public void tagsCleared()  throws Exception;

}