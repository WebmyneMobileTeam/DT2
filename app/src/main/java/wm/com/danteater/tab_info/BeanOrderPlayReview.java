package wm.com.danteater.tab_info;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 19-09-2014.
 */
public class BeanOrderPlayReview {

    @SerializedName("PlayOrderId")
    public String PlayOrderId;

    @SerializedName("OrderError")
    public String OrderError;

    public BeanOrderPlayReview() {
    }

    public BeanOrderPlayReview(String playOrderId, String orderError) {
        PlayOrderId = playOrderId;
        OrderError = orderError;
    }
}
