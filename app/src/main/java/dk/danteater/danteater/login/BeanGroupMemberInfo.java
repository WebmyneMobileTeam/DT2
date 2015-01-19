package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 16-09-2014.
 */
public class BeanGroupMemberInfo {

    @SerializedName("result")
    BeanGroupMemberResult beanGroupMemberResult;

    public BeanGroupMemberResult getBeanGroupMemberResult() {
        return beanGroupMemberResult;
    }

    public void setBeanGroupMemberResult(BeanGroupMemberResult beanGroupMemberResult) {
        this.beanGroupMemberResult = beanGroupMemberResult;
    }
}
