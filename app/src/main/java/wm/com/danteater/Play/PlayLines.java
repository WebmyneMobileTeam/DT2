package wm.com.danteater.Play;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav
 */
public class PlayLines {

    @SerializedName("LineCount")
    public String LineCount;

    @SerializedName("LineID")
    public String LineID;

    @SerializedName("RoleName")
    public String RoleName;

    @SerializedName("AssignedUsers")
    public ArrayList<AssignedUsers>  assignedUsersList;

    @SerializedName("RoleLinesCount")
    public String RoleLinesCount;

    @SerializedName("MainLineType")
    public String MainLineType;

    @SerializedName("TextLines")
    public ArrayList<TextLines> textLinesList;

    @SerializedName("CastMatches")
    public ArrayList<String> castMatchesList;

    @SerializedName("Comments")
    public ArrayList<Comments> commentsList;

    @SerializedName("SongFiles")
    public ArrayList<SongFiles> songFilesList;


    public PlayLines(String lineCount, String lineID, String roleName, ArrayList<AssignedUsers> assignedUsersList, String roleLinesCount, String mainLineType, ArrayList<TextLines> textLinesList, ArrayList<String> castMatchesList, ArrayList<Comments> commentsList, ArrayList<SongFiles> songFilesList) {
        LineCount = lineCount;
        LineID = lineID;
        RoleName = roleName;
        this.assignedUsersList = assignedUsersList;
        RoleLinesCount = roleLinesCount;
        MainLineType = mainLineType;
        this.textLinesList = textLinesList;
        this.castMatchesList = castMatchesList;
        this.commentsList = commentsList;
        this.songFilesList = songFilesList;
    }

    public String getLineCount() {
        return LineCount;
    }

    public String getLineID() {
        return LineID;
    }

    public String getRoleName() {
        return RoleName;
    }

    public ArrayList<AssignedUsers> getAssignedUsersList() {
        return assignedUsersList;
    }

    public String getRoleLinesCount() {
        return RoleLinesCount;
    }

    public String getMainLineType() {
        return MainLineType;
    }

    public ArrayList<TextLines> getTextLinesList() {
        return textLinesList;
    }

    public ArrayList<String> getCastMatchesList() {
        return castMatchesList;
    }

    public ArrayList<Comments> getCommentsList() {
        return commentsList;
    }

    public ArrayList<SongFiles> getSongFilesList() {
        return songFilesList;
    }
}
