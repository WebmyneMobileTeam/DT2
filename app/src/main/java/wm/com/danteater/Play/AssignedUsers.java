package wm.com.danteater.Play;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav
 */
public class AssignedUsers {

    public int uID;

    @SerializedName("AssignedUserId")
    public String AssignedUserId;

    @SerializedName("AssignedUserName")
    public String AssignedUserName;

    public AssignedUsers(){

    }

    public AssignedUsers(String assignedUserId, String assignedUserName) {
        AssignedUserId = assignedUserId;
        AssignedUserName = assignedUserName;
    }

    public String getAssignedUserId() {
        return AssignedUserId;
    }

    public String getAssignedUserName() {
        return AssignedUserName;
    }
}
