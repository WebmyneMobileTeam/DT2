package dk.danteater.danteater.tab_inspiration;

import com.google.gson.annotations.SerializedName;

/**
 * Created by dhruvil on 11-11-2014.
 */
public class Inspiration {

    @SerializedName("Id")
    public String Id;

    @SerializedName("PlayId")
    public String PlayId;

    @SerializedName("UserId")
    public String UserId;

    @SerializedName("UserName")
    public String UserName;

    @SerializedName("SchoolId")
    public String SchoolId;

    @SerializedName("SchoolName")
    public String SchoolName;

    @SerializedName("MessageText")
    public String MessageText;

    @SerializedName("ImageUrlOriginal")
    public String ImageUrlOriginal;

    @SerializedName("ImageUrlSmall")
    public String ImageUrlSmall;

    @SerializedName("ImageUrlMedium")
    public String ImageUrlMedium;

    @SerializedName("ImageUrlLarge")
    public String ImageUrlLarge;



}
