package wm.com.danteater.my_plays;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 06-10-2014.
 */
public class SharedUser {

    @SerializedName("UserId")
    public String userId;

    @SerializedName("UserName")
    public String userName;

    public SharedUser(){

    }

    public SharedUser(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }
}
