package dk.danteater.danteater.Messages;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 08-10-2014.
 */
public class MessageUnread {

    @SerializedName("FromUserId")
    public String FromUserId;

    @SerializedName("unreadMessageCount")
    public String unreadMessageCount;

    public MessageUnread() {

    }

    public MessageUnread(String fromUserId, String unreadMessageCount) {
        FromUserId = fromUserId;
        this.unreadMessageCount = unreadMessageCount;
    }
}
