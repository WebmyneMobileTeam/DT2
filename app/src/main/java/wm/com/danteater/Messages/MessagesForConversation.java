package wm.com.danteater.Messages;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 08-10-2014.
 */
public class MessagesForConversation {

    @SerializedName("Id")
    public String Id;
    @SerializedName("OrderId")
    public String OrderId;
    @SerializedName("LineId")
    public String LineId;
    @SerializedName("FromUserId")
    public String FromUserId;
    @SerializedName("ToUserId")
    public String ToUserId;
    @SerializedName("MessageText")
    public String MessageText;
    @SerializedName("postedTimeStamp")
    public String postedTimeStamp;
    @SerializedName("isRead")
    public String isRead;

    public MessagesForConversation() {
    }

    public MessagesForConversation(String id, String orderId, String lineId, String fromUserId, String toUserId, String messageText, String postedTimeStamp, String isRead) {
        Id = id;
        OrderId = orderId;
        LineId = lineId;
        FromUserId = fromUserId;
        ToUserId = toUserId;
        MessageText = messageText;
        this.postedTimeStamp = postedTimeStamp;
        this.isRead = isRead;
    }
}
