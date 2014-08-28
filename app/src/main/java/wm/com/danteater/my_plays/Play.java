package wm.com.danteater.my_plays;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 27-08-2014.
 */
public class Play {

    @SerializedName("PlayId")
    public String PlayId;

    @SerializedName("OrderId")
    public String OrderId;

    @SerializedName("OrderType")
    public String OrderType;

    @SerializedName("OrderUserId")
    public String OrderUserId;

    @SerializedName("Title")
    public String Title;

    @SerializedName("SubtitleShort")
    public String SubtitleShort;

    @SerializedName("SubtitleLong")
    public String SubtitleLong;

    @SerializedName("Author")
    public String Author;

    @SerializedName("Actors")
    public String Actors;

    @SerializedName("Age")
    public String Age;

    @SerializedName("Music")
    public String Music;

    @SerializedName("MusicCount")
    public String MusicCount;

    @SerializedName("Duration")
    public String Duration;

    @SerializedName("Synopsis")
    public String Synopsis;

    @SerializedName("PlayVersion")
    public String PlayVersion;

    @SerializedName("PlayOrderDetails")
    public PlayOrderDetails playOrderDetails;

    public Play(String playId, String orderId, String orderType, String orderUserId, String title, String subtitleShort, String subtitleLong, String author, String actors, String age, String music, String musicCount, String duration, String synopsis, String playVersion, PlayOrderDetails playOrderDetails) {
        PlayId = playId;
        OrderId = orderId;
        OrderType = orderType;
        OrderUserId = orderUserId;
        Title = title;
        SubtitleShort = subtitleShort;
        SubtitleLong = subtitleLong;
        Author = author;
        Actors = actors;
        Age = age;
        Music = music;
        MusicCount = musicCount;
        Duration = duration;
        Synopsis = synopsis;
        PlayVersion = playVersion;
        this.playOrderDetails = playOrderDetails;
    }

    public String getPlayId() {
        return PlayId;
    }

    public String getOrderId() {
        return OrderId;
    }

    public String getOrderType() {
        return OrderType;
    }

    public String getOrderUserId() {
        return OrderUserId;
    }

    public String getTitle() {
        return Title;
    }

    public String getSubtitleShort() {
        return SubtitleShort;
    }

    public String getSubtitleLong() {
        return SubtitleLong;
    }

    public String getAuthor() {
        return Author;
    }

    public String getActors() {
        return Actors;
    }

    public String getAge() {
        return Age;
    }

    public String getMusic() {
        return Music;
    }

    public String getMusicCount() {
        return MusicCount;
    }

    public String getDuration() {
        return Duration;
    }

    public String getSynopsis() {
        return Synopsis;
    }

    public String getPlayVersion() {
        return PlayVersion;
    }

    public PlayOrderDetails getPlayOrderDetails() {
        return playOrderDetails;
    }
}
