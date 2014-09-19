package wm.com.danteater.search;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 18-09-2014.
 */
public class BeanSearch {

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
    public String Synopsis ;

    @SerializedName("PlayVersion")
    public String PlayVersion;

    public BeanSearch() {
    }

    public BeanSearch(String playId, String orderId, String orderType, String orderUserId, String title, String subtitleShort, String author, String actors, String age, String music, String musicCount, String duration, String synopsis, String playVersion) {
        PlayId = playId;
        OrderId = orderId;
        OrderType = orderType;
        OrderUserId = orderUserId;
        Title = title;
        SubtitleShort = subtitleShort;
        Author = author;
        Actors = actors;
        Age = age;
        Music = music;
        MusicCount = musicCount;
        Duration = duration;
        Synopsis = synopsis;
        PlayVersion = playVersion;
    }



}
