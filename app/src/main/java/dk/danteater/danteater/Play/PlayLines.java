package dk.danteater.danteater.Play;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by nirav
 */
public class PlayLines {





    public enum PlayLType {
        PlayLineTypeUnknown,
        PlayLineTypeAuthor,
        PlayLineTypeTitle,
        PlayLineTypeAct,
        PlayLineTypeRole,
        PlayLineTypeLine,
        PlayLineTypeNote,
        PlayLineTypeInfo,
        PlayLineTypeSong,
        PlayLineTypeSongLine,
        PlayLineTypeSongLineVerse,
        PlayLineTypeChooseSong,
        PlayLineTypePicutre
    }

    public boolean showRoleHighlight = false;
    public boolean allowComments = false;
    public boolean allowRecording = false;
    public boolean showIntoWords = false;
    public boolean showLineNumber = false;
    public boolean isLastSongLine = false;

    @SerializedName("LineCount")
    public int LineCount;

    public int lID;

    @SerializedName("LineID")
    public String LineID;

    @SerializedName("RoleName")
    public String RoleName;

    @SerializedName("AssignedUsers")
    public ArrayList<AssignedUsers> assignedUsersList;

    @SerializedName("RoleLinesCount")
    public String RoleLinesCount;

    @SerializedName("MainLineType")
    public String MainLineType;

    @SerializedName("TextLines")
    public ArrayList<TextLines> textLinesList;

    @SerializedName("CastMatches")
    public ArrayList<String> castMatchesList;

    public String castMatchesString;

    @SerializedName("Comments")
    public ArrayList<Comments> commentsList;

    @SerializedName("SongFiles")
    public ArrayList<SongFiles> songFilesList;

    public boolean isSoundAvailable=false;

    public boolean isSoundAvailable() {
        return isSoundAvailable;
    }

    public void setSoundAvailable(boolean isSoundAvailable) {
        this.isSoundAvailable = isSoundAvailable;
    }



    public PlayLines(){

    }

    public PlayLines(int lineCount, String lineID, String roleName, ArrayList<AssignedUsers> assignedUsersList, String roleLinesCount, String mainLineType, ArrayList<TextLines> textLinesList, ArrayList<String> castMatchesList, ArrayList<Comments> commentsList, ArrayList<SongFiles> songFilesList) {

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

    public int getLineCount() {
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

    public PlayLType playLineType(){

        if(this.MainLineType.equalsIgnoreCase("") || this.MainLineType == null){
            return PlayLType.PlayLineTypeUnknown;
        }
        else if(this.MainLineType.equalsIgnoreCase("Forfatter")){
            return PlayLType.PlayLineTypeAuthor;
        }
        else if(this.MainLineType.equalsIgnoreCase("Titel")){
            return PlayLType.PlayLineTypeTitle;
        }
        else if(this.MainLineType.equalsIgnoreCase("Akt")){
            return PlayLType.PlayLineTypeAct;
        }
        else if(this.MainLineType.equalsIgnoreCase("Rolle")){
            return PlayLType.PlayLineTypeRole;
        }
        else if(this.MainLineType.equalsIgnoreCase("Note")){
            return PlayLType.PlayLineTypeNote;
        }
        else if(this.MainLineType.equalsIgnoreCase("Info")){
            return PlayLType.PlayLineTypeInfo;
        }
        else if(this.MainLineType.equalsIgnoreCase("Replik")){
            return PlayLType.PlayLineTypeLine;
        }
        else if(this.MainLineType.equalsIgnoreCase("Song")){
            return PlayLType.PlayLineTypeSong;
        }
        else if(this.MainLineType.equalsIgnoreCase("Songline")){
            return PlayLType.PlayLineTypeSongLine;
        }
        else if(this.MainLineType.equalsIgnoreCase("SonglineVerse")){
            return PlayLType.PlayLineTypeSongLineVerse;
        }
        else if(this.MainLineType.equalsIgnoreCase("Billede")){
            return PlayLType.PlayLineTypePicutre;
        }else{

        }

        return PlayLType.PlayLineTypeUnknown;
    }

//    @Override
//    public int compareTo(PlayLines another) {
//        PlayLines p=(PlayLines)another;
//
//        return this.LineCount-another.LineCount;
//
//    }


}




