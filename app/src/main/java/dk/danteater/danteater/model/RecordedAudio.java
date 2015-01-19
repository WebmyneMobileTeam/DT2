package dk.danteater.danteater.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Android on 09-12-2014.
 */
public class RecordedAudio {

    @SerializedName("TimeStamp")
    private String TimeStamp;
    @SerializedName("LineID")
    private String LineID;
    @SerializedName("UserId")
    private String UserId;
    @SerializedName("shareType")
    private String shareType;
    @SerializedName("OrderID")
    private String OrderID;

    public RecordedAudio() {
    }

    public RecordedAudio(String timeStamp, String lineID, String userId, String shareType, String orderID) {
        TimeStamp = timeStamp;
        LineID = lineID;
        UserId = userId;
        this.shareType = shareType;
        OrderID = orderID;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getLineID() {
        return LineID;
    }

    public void setLineID(String lineID) {
        LineID = lineID;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getShareType() {
        return shareType;
    }

    public void setShareType(String shareType) {
        this.shareType = shareType;
    }

    public String getOrderID() {
        return OrderID;
    }

    public void setOrderID(String orderID) {
        OrderID = orderID;
    }
}
