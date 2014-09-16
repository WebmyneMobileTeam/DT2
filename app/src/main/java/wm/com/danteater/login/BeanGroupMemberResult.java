package wm.com.danteater.login;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav on 16-09-2014.
 */
public class BeanGroupMemberResult {

    @SerializedName("members")
    private ArrayList<GroupMembers> groupMembersArrayList;

    public ArrayList<GroupMembers> getGroupMembersArrayList() {
        return groupMembersArrayList;
    }

    public void setGroupMembersArrayList(ArrayList<GroupMembers> groupMembersArrayList) {
        this.groupMembersArrayList = groupMembersArrayList;
    }
}
