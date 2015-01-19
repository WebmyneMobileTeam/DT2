package dk.danteater.danteater.Play;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 27-08-2014.
 */
public class PlayOrderDetails {

    @SerializedName("PlayId")
    public String PlayId;

    @SerializedName("UserId")
    public String UserId;

    @SerializedName("PlayOrderId")
    public String PlayOrderId;

    @SerializedName("NumberOfPerformances")
    public String NumberOfPerformances;

    @SerializedName("NumberOfAuditions")
    public String NumberOfAuditions;

    @SerializedName("PerformDateFirst")
    public String PerformDateFirst;

    @SerializedName("PerformDateLast")
    public String PerformDateLast;

    @SerializedName("Comments")
    public String Comments;

    @SerializedName("SchoolId")
    public String SchoolId;

    public PlayOrderDetails(String playId, String userId, String playOrderId, String numberOfPerformances, String numberOfAuditions, String performDateFirst, String performDateLast, String comments, String schoolId) {
        PlayId = playId;
        UserId = userId;
        PlayOrderId = playOrderId;
        NumberOfPerformances = numberOfPerformances;
        NumberOfAuditions = numberOfAuditions;
        PerformDateFirst = performDateFirst;
        PerformDateLast = performDateLast;
        Comments = comments;
        SchoolId = schoolId;
    }

    public String getPlayId() {
        return PlayId;
    }

    public String getUserId() {
        return UserId;
    }

    public String getPlayOrderId() {
        return PlayOrderId;
    }

    public String getNumberOfPerformances() {
        return NumberOfPerformances;
    }

    public String getNumberOfAuditions() {
        return NumberOfAuditions;
    }

    public String getPerformDateFirst() {
        return PerformDateFirst;
    }

    public String getPerformDateLast() {
        return PerformDateLast;
    }

    public String getComments() {
        return Comments;
    }

    public String getSchoolId() {
        return SchoolId;
    }
}
