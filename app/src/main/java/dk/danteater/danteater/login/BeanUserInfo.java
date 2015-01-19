package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 12-09-2014.
 */
public class BeanUserInfo {

    @SerializedName("user_info")
    User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
