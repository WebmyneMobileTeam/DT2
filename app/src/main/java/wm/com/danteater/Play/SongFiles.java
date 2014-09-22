package wm.com.danteater.Play;

import com.google.gson.annotations.SerializedName;

/**
 * Created by nirav on 01-09-2014.
 */
public class SongFiles {


    @SerializedName("Id")
    public String Id;

    @SerializedName("SongTitle")
    public String SongTitle;

    @SerializedName("SongMp3Url")
    public String SongMp3Url;

    @SerializedName("FileType")
    public String FileType;

    @SerializedName("FileDescription")
    public String FileDescription;

    @SerializedName("FileAvailableForStudents")
    public boolean FileAvailableForStudents;

    public SongFiles(String id, String songTitle, String songMp3Url, String fileType, String fileDescription, boolean fileAvailableForStudents) {
        Id = id;
        SongTitle = songTitle;
        SongMp3Url = songMp3Url;
        FileType = fileType;
        FileDescription = fileDescription;
        FileAvailableForStudents = fileAvailableForStudents;
    }

    public String getId() {
        return Id;
    }

    public String getSongTitle() {
        return SongTitle;
    }

    public String getSongMp3Url() {
        return SongMp3Url;
    }

    public String getFileType() {
        return FileType;
    }

    public String getFileDescription() {
        return FileDescription;
    }

    public boolean getFileAvailableForStudents() {
        return FileAvailableForStudents;
    }
}
