package wm.com.danteater.tab_read;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import wm.com.danteater.Play.Comments;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.model.ComplexPreferences;

/**
 * Created by dhruvil on 29-09-2014.
 */
public class CellRecordPlayPlayLine {


    private ImageView imgPLay;
    private WMTextView btnOpTag;
    private LinearLayout listReadPlayPlaylinecell;
    private WMTextView lblRoleName;
    private WMTextView tvPlayLines;

    private WMTextView lblLineNumber;
    private Context ctx;
    private boolean showLineNumber;
    private boolean showComments;

    public int STATE_RECORD = 0;
    public int STATE_PREVIEW = 1;
    public int STATE_READ = 2;
    public int STATE_CHAT = 3;

    User user;
    String currentUserName = "";
    String currentUserId = "";


    public CellRecordPlayPlayLine(View view, Context context) {

        this.ctx = context;
        lblLineNumber = (WMTextView) view.findViewById(R.id.recordPlayLineCellLineNumber);
        lblRoleName = (WMTextView) view.findViewById(R.id.recordPlayLineCellRollName);
        tvPlayLines = (WMTextView) view.findViewById(R.id.recordPlayLineCellDescription);
        btnOpTag = (WMTextView)view.findViewById(R.id.btnOpTag);
        imgPLay = (ImageView)view.findViewById(R.id.recordPlayLineCellPlayButton);

        listReadPlayPlaylinecell = (LinearLayout)view.findViewById(R.id.listReadPlayPlaylinecell);
        lblRoleName.setBold();

    }

    public void setupForPlayLine(PlayLines playLine, int current_state) {

        listReadPlayPlaylinecell.removeAllViews();
        listReadPlayPlaylinecell.invalidate();

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        currentUserId = user.getUserId();
        currentUserName = user.getFirstName()+" "+user.getLastName();
        tvPlayLines.setText("");

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnOpTag.setVisibility(View.VISIBLE);
        }else{
            btnOpTag.setVisibility(View.INVISIBLE);
        }

        SharedPreferences preferences = ctx.getSharedPreferences("settings", ctx.MODE_PRIVATE);
        showLineNumber = preferences.getBoolean("showLineNumber", false);
        showComments = preferences.getBoolean("showComments", false);

        if (showLineNumber == true) {
            lblLineNumber.setText(playLine.LineCount);
        } else {
            lblLineNumber.setText("");
        }

        lblRoleName.setText(playLine.RoleName + ":");

        ArrayList<TextLines> textLines = playLine.textLinesList;

        if (textLines == null || textLines.size() == 0) {

            tvPlayLines.setText("");

        } else if (textLines.size() == 1) {

            TextLines textLine = textLines.get(0);
            tvPlayLines.setText(textLine.currentText());
            tvPlayLines.setTextColor(Color.parseColor(colorForLineType(textLine.textLineType())));

        } else {

            int lineCount = 0;

            for (TextLines textLine : textLines) {

                String s = tvPlayLines.getText().toString();
                if (s.length() == 0) {
                    tvPlayLines.setText(textLine.currentText());
                } else {
                    tvPlayLines.setText(s + "\n\n" + textLine.currentText());
                }
                tvPlayLines.setTextColor(Color.parseColor(colorForLineType(textLine.textLineType())));
            }

        }

        if (showComments == true) {

            ArrayList<Comments> comments = playLine.commentsList;
            Log.e("SSSIIIEEEZZZEEE ",""+comments.size());
            ArrayList<Comments> finalComments = new ArrayList<Comments>();

            for(Comments comment : comments){

                if(comment.isPrivate && !comment.userName.equalsIgnoreCase(currentUserId)){

                }else{
                    finalComments.add(comment);
                }
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            for(Comments com : finalComments){
                View vComment = mInflater.inflate(R.layout.item_comment_list,null);
                WMTextView txt = (WMTextView)vComment.findViewById(R.id.txtItemCommentText);
                txt.setText(com.commentText);
                listReadPlayPlaylinecell.addView(vComment,params);
                listReadPlayPlaylinecell.invalidate();

            }

        }

    }

    private String colorForLineType(TextLines.TextLineType textLineType) {

        if(textLineType == TextLines.TextLineType.TextLineNormalLine){
            return AppConstants.lineTextColor;
        }else if(textLineType == TextLines.TextLineType.TextLineTypeInfo){
            return AppConstants.infoTextColor;
        }else if(textLineType == TextLines.TextLineType.TextLineTypeNote){
            return AppConstants.noteTextColor;
        }else{
            return "#000000";
        }
    }





}
