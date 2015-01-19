package dk.danteater.danteater.login;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 16-09-2014.
 */
public class BeanGroupInfo {

    @SerializedName("result")
    BeanGroupResult beanGroupResult;

    public BeanGroupResult getBeanGroupResult() {
        return beanGroupResult;
    }

    public void setBeanGroupResult(BeanGroupResult beanGroupResult) {
        this.beanGroupResult = beanGroupResult;
    }
}
