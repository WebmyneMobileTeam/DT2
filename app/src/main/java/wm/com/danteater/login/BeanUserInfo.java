package wm.com.danteater.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 12-09-2014.
 */
public class BeanUserInfo {

    @SerializedName("user_info")
    BeanUser beanUser;

    public BeanUser getBeanUser() {
        return beanUser;
    }

    public void setBeanUser(BeanUser beanUser) {
        this.beanUser = beanUser;
    }
}
