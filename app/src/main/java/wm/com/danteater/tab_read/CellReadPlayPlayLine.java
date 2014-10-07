package wm.com.danteater.tab_read;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.androidanimations.library.Techniques;
import com.androidanimations.library.Webmyne;
import com.google.gson.GsonBuilder;
import com.nineoldandroids.animation.Animator;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wm.com.danteater.Play.Comments;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.SegmentedGroup;
import wm.com.danteater.customviews.WMEdittext;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.BeanGroupMemberInfo;
import wm.com.danteater.login.BeanGroupMemberResult;
import wm.com.danteater.login.GroupMembers;
import wm.com.danteater.login.User;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.my_plays.DrawerActivity;

/**
 * Created by dhruvil on 29-09-2014.
 */
public class CellReadPlayPlayLine implements View.OnClickListener{

    private LinearLayout listReadPlayPlaylinecell;
    private WMTextView lblRoleName;
    private WMTextView tvPlayLines;
    private ImageView btnMenu;
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

    private PlayLines pl;
    private int currentState = 0;

    private View viewMenu;
    private ImageView imgCloseMenu;
    private WMTextView btnEdit;
    private WMTextView btnNote;
    private WMTextView btnMessage;

    private OnTextLineUpdated onTextLineUpdated;



    public CellReadPlayPlayLine(View view, Context context) {

        this.ctx = context;
        lblLineNumber = (WMTextView) view.findViewById(R.id.readPlayLineCellLineNumber);
        lblRoleName = (WMTextView) view.findViewById(R.id.readPlayLineCellRollName);
        tvPlayLines = (WMTextView) view.findViewById(R.id.readPlayLineCellDescription);
        btnMenu = (ImageView) view.findViewById(R.id.readPlayLineCellMoreImage);
        listReadPlayPlaylinecell = (LinearLayout)view.findViewById(R.id.listReadPlayPlaylinecell);
        lblRoleName.setBold();

        viewMenu = (View)view.findViewById(R.id.cellMenuReadLineTeacher);
        imgCloseMenu = (ImageView)view.findViewById(R.id.readPlayLineMenuMoreClose);
        btnEdit = (WMTextView)view.findViewById(R.id.readPlayLineMenuEdit);
        btnMessage = (WMTextView)view.findViewById(R.id.readPlayLineMenuNote);
        btnNote = (WMTextView)view.findViewById(R.id.readPlayLineMenuChat);

        btnMenu.setOnClickListener(this);
        imgCloseMenu.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnNote.setOnClickListener(this);



    }



    public void setupForPlayLine(PlayLines playLine, int current_state,boolean mark) {

       lblRoleName.setBackgroundColor(Color.TRANSPARENT);


        Log.e("Mark is ",""+mark);
        if(mark == true){
            lblRoleName.setBackgroundColor(Color.YELLOW);
        }

        viewMenu.setVisibility(View.GONE);
        this.pl = playLine;
        this.currentState = current_state;

        listReadPlayPlaylinecell.removeAllViews();
        listReadPlayPlaylinecell.invalidate();
        if(current_state == STATE_PREVIEW){
            btnMenu.setVisibility(View.INVISIBLE);
        }

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        currentUserId = user.getUserId();
        currentUserName = user.getFirstName()+" "+user.getLastName();

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnMenu.setVisibility(View.VISIBLE);
        }else{
            btnMenu.setVisibility(View.INVISIBLE);
        }

        tvPlayLines.setText("");

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


    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.readPlayLineCellMoreImage:

                showMenu();

                break;

            case R.id.readPlayLineMenuMoreClose:

                hideMenu();


                break;

            case R.id.readPlayLineMenuEdit:

                hideMenu();
                showEditOptionsWithFunctionality();


                break;

            case R.id.readPlayLineMenuNote:
                hideMenu();
                showCommentOptionsWithFunctionality();

                break;

            case R.id.readPlayLineMenuChat:

                break;

        }

    }

    private void showCommentOptionsWithFunctionality() {

        final Dialog dialog = new Dialog(ctx,android.R.style.Theme_Translucent_NoTitleBar);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount=0.6f;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.write_comment_view_popup_menu,null);
        dialog.setContentView(view);
        dialog.show();

        final SegmentedGroup segmentedGroup = (SegmentedGroup)view.findViewById(R.id.writeCommentPopupSegmentedGroup);
        if(user.checkPupil(user.getRoles())){
            segmentedGroup.setVisibility(View.INVISIBLE);
        }else{
            segmentedGroup.setVisibility(View.VISIBLE);
        }

        WMTextView writeCommentPopupCancel = (WMTextView)view.findViewById(R.id.writeCommentPopupCancel);
        writeCommentPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

       final EditText writeCommentPopupTextArea = (EditText)view.findViewById(R.id.writeCommentPopupTextArea);

        WMTextView writeCommentPopupSave = (WMTextView)view.findViewById(R.id.writeCommentPopupSave);
        writeCommentPopupSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(writeCommentPopupTextArea.getText().toString() == null || writeCommentPopupTextArea.getText().toString().equalsIgnoreCase("")){
                    dialog.dismiss();
                }else{


                    boolean isprivate = false;
                    if(segmentedGroup.getCheckedRadioButtonId() == R.id.writeCommentPopupShareWithEveryOne){
                        isprivate = false;
                    }else if(segmentedGroup.getCheckedRadioButtonId() == R.id.writeCommentPopupShareWithMe){
                        isprivate = true;
                    }else{
                        isprivate = false;
                    }
                    dialog.dismiss();
                    onTextLineUpdated.onCommentAdded(writeCommentPopupTextArea.getText().toString(),isprivate);
                }

            }
        });



    }

    private void hideMenu() {

        Webmyne.get(Techniques.SlideOutUp).duration(700).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                viewMenu.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startOn(viewMenu);

    }

    private void showMenu() {
        viewMenu.setVisibility(View.VISIBLE);
        Webmyne.get(Techniques.SlideInDown).duration(700).withListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startOn(viewMenu);

    }


    private void showEditOptionsWithFunctionality() {

        final Dialog dialog = new Dialog(ctx,android.R.style.Theme_Translucent_NoTitleBar);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount=0.6f;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.edit_line_view_popup_menu,null);
        dialog.setContentView(view);
        dialog.show();

        final EditText editLineViewTextArea = (EditText)view.findViewById(R.id.editLineViewTextArea);
        editLineViewTextArea.setText(pl.textLinesList.get(0).currentText());

        WMTextView saveBtn = (WMTextView)view.findViewById(R.id.editLineViewPopupSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                onTextLineUpdated.onTextLineUpdated(editLineViewTextArea.getText().toString());

            }
        });

    }

    public void setOnTextLineUpdated(OnTextLineUpdated textLineUpdated){

       this.onTextLineUpdated = textLineUpdated;


    }


    interface OnTextLineUpdated{

        public void onTextLineUpdated(String newText);
        public void onCommentAdded(String comment,boolean isPrivate);
    }
}
