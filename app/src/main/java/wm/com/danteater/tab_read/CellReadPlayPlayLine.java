package wm.com.danteater.tab_read;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

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

import wm.com.danteater.Messages.ReadActivityForChat;
import wm.com.danteater.Play.Comments;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.FullLengthListView;
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
 //   private WMTextView tvPlayLines;
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
    private View convertView;
    private FullLengthListView listView;
    ArrayList<TextLines> textLines;
    public boolean showSideOptions = false;
    public TextListAdapter adapterTextList;

    public CellReadPlayPlayLine(View view, Context context) {
        this.convertView=view;
        this.ctx = context;
        lblLineNumber = (WMTextView) view.findViewById(R.id.readPlayLineCellLineNumber);
        lblRoleName = (WMTextView) view.findViewById(R.id.readPlayLineCellRollName);
    //    tvPlayLines = (WMTextView) view.findViewById(R.id.readPlayLineCellDescription);
        listView = (FullLengthListView)view.findViewById(R.id.listViewReadPlayLineText);
        btnMenu = (ImageView) view.findViewById(R.id.readPlayLineCellMoreImage);
        listReadPlayPlaylinecell = (LinearLayout)view.findViewById(R.id.listReadPlayPlaylinecell);
        lblRoleName.setBold();

        viewMenu = (View)view.findViewById(R.id.cellMenuReadLineTeacher);
        imgCloseMenu = (ImageView)view.findViewById(R.id.readPlayLineMenuMoreClose);
        btnEdit = (WMTextView)view.findViewById(R.id.readPlayLineMenuEdit);
        btnNote = (WMTextView)view.findViewById(R.id.readPlayLineMenuNote);
        btnMessage   = (WMTextView)view.findViewById(R.id.readPlayLineMenuChat);

        btnMenu.setOnClickListener(this);
        imgCloseMenu.setOnClickListener(this);
        btnEdit.setOnClickListener(this);
        btnMessage.setOnClickListener(this);
        btnNote.setOnClickListener(this);



    }



    public void setupForPlayLine(int firstIndex,int section,int position,PlayLines playLine, int current_state,boolean mark) {

       showSideOptions = false;

       lblRoleName.setBackgroundColor(Color.TRANSPARENT);
       btnEdit.setVisibility(View.VISIBLE);
        convertView.setBackgroundColor(Color.TRANSPARENT);
        if(current_state==STATE_CHAT){
            if(playLine.LineCount== ReadActivityForChat.lineNumber) {
                convertView.setBackgroundColor(Color.parseColor("#f6f6d6"));
            }
        }
        Log.e("new final line number",position+"");

        String currentKey = playLine.textLinesList.get(0).LineText;

//        if(section == 0 && current_state != STATE_RECORD){
//
//            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.read_play_cell));
//        }

//        if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeAct && current_state != STATE_RECORD){
//
//            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.read_play_cell));
//        }
        if((section<firstIndex-1 || section==0) && !(current_state == STATE_RECORD)){
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.read_play_cell));
        }

//        Log.e("Mark is ",""+mark);
        if(mark == true){
            lblRoleName.setBackgroundColor(Color.YELLOW);
        }

        if(mark == true){
            btnMenu.setVisibility(View.VISIBLE);
            btnEdit.setVisibility(View.GONE);
        } else {

            btnMenu.setVisibility(View.GONE);
            btnEdit.setVisibility(View.VISIBLE);
        }

        viewMenu.setVisibility(View.GONE);
        this.pl = playLine;
        this.currentState = current_state;

        listReadPlayPlaylinecell.removeAllViews();
        listReadPlayPlaylinecell.invalidate();



        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        currentUserId = user.getUserId();
        currentUserName = user.getFirstName()+" "+user.getLastName();

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnMenu.setVisibility(View.VISIBLE);
        }

        if(current_state == STATE_PREVIEW){
            btnMenu.setVisibility(View.INVISIBLE);
        }

       // tvPlayLines.setText("");

        SharedPreferences preferences = ctx.getSharedPreferences("settings", ctx.MODE_PRIVATE);
        showLineNumber = preferences.getBoolean("showLineNumber", false);
        showComments = preferences.getBoolean("showComments", false);

        if (showLineNumber == true) {
            lblLineNumber.setText(String.valueOf(playLine.LineCount));
        } else {
            lblLineNumber.setText("");
        }

        if(playLine.RoleName == null){
            lblRoleName.setText("");
        }else{
            lblRoleName.setText(playLine.RoleName + ":");
        }


         textLines = playLine.textLinesList;

        if(textLines != null && textLines.size()>0){
            adapterTextList = new TextListAdapter(textLines);
            listView.setAdapter(adapterTextList);
        }


/*        if (textLines == null || textLines.size() == 0) {

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

        }*/

        if (showComments == true) {

            ArrayList<Comments> comments = playLine.commentsList;
//            Log.e("SSSIIIEEEZZZEEE ",""+comments.size());
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
                txt.setTypeface(null,Typeface.ITALIC);
                ImageView img = (ImageView)vComment.findViewById(R.id.imgCommentCompose);

                if(com.isPrivate){
                    img.setImageResource(R.drawable.compose_green);
                }else{
                    img.setImageResource(R.drawable.compose_yellow);
                }

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

    public class TextListAdapter extends BaseAdapter{

        private ArrayList<TextLines> arrTextLines;


        public TextListAdapter(ArrayList<TextLines> arrTextLines) {
            this.arrTextLines = arrTextLines;
        }

        @Override
        public int getCount() {

            return arrTextLines.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = View.inflate(ctx,R.layout.item_readplayplayline_textline,null);
            }

            WMTextView txt = (WMTextView)convertView.findViewById(R.id.readPlayLineCellDescription);
            ImageView img = (ImageView)convertView.findViewById(R.id.imgReadPlayChangeLine);
            txt.setText(arrTextLines.get(position).currentText());
            txt.setTextColor(Color.parseColor(colorForLineType(arrTextLines.get(position).textLineType())));

            if(showSideOptions == false){
                img.setVisibility(View.INVISIBLE);
            }else{
                img.setVisibility(View.VISIBLE);
            }

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    showEditOptionsWithFunctionality(position);

                }
            });


            return convertView;
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

                if(textLines!=null && textLines.size()>0){

                    if(textLines.size() == 1){
                        showEditOptionsWithFunctionality(0);
                    }else{

                        showSideOptions = true;
                        adapterTextList.notifyDataSetChanged();

                    }

                }




                break;

            case R.id.readPlayLineMenuNote:
                hideMenu();
                showCommentOptionsWithFunctionality();

                break;

            case R.id.readPlayLineMenuChat:
                hideMenu();
                onTextLineUpdated.onChatClicked();
                //TODO

               Log.e("message","click on message");
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

       Webmyne.get(Techniques.SlideInDown).duration(100).withListener(new Animator.AnimatorListener() {
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

        viewMenu.setVisibility(View.VISIBLE);

    }


    private void showEditOptionsWithFunctionality(final int pos) {

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
        editLineViewTextArea.setText(pl.textLinesList.get(pos).currentText());


        WMTextView editLineViewPopupTitle = (WMTextView)view.findViewById(R.id.editLineViewPopupTitle);
        editLineViewPopupTitle.setText("Ret replik");


        WMTextView editLineViewPopupCancel = (WMTextView)view.findViewById(R.id.editLineViewPopupCancel);
        editLineViewPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        WMTextView saveBtn = (WMTextView)view.findViewById(R.id.editLineViewPopupSave);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();

                onTextLineUpdated.onTextLineUpdated(editLineViewTextArea.getText().toString(),pos);

            }
        });

    }

    public void setOnTextLineUpdated(OnTextLineUpdated textLineUpdated){
       this.onTextLineUpdated = textLineUpdated;

    }


    interface OnTextLineUpdated{
        public void onTextLineUpdated(String newText,int pos);
        public void onCommentAdded(String comment,boolean isPrivate);
        public void onChatClicked();
    }
}
