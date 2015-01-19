package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 12-09-2014.
 */
public class BeanUserResult {

    @SerializedName("result")
    private BeanUserInfo beanUserResult;

    public BeanUserInfo getBeanUserResult() {
        return beanUserResult;
    }

    public void setBeanUserResult(BeanUserInfo beanUserResult) {
        this.beanUserResult = beanUserResult;
    }
}
