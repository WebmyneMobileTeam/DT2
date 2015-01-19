package dk.danteater.danteater.Play;


import com.google.gson.annotations.SerializedName;

public class Comments {


    public String comment_id;
    @SerializedName("UserName")
    public String userName;
    @SerializedName("CommentText")
    public String commentText;
    @SerializedName("Private")
    public boolean isPrivate;

    public Comments() {
    }

    public Comments(String comment_id, String userName, String commentText, boolean isPrivate) {
        this.comment_id = comment_id;
        this.userName = userName;
        this.commentText = commentText;
        this.isPrivate = isPrivate;
    }



}
