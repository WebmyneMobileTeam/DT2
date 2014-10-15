package wm.com.danteater.tab_read;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import com.androidanimations.library.Techniques;
import com.androidanimations.library.Webmyne;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;

import wm.com.danteater.Messages.ReadActivityForChat;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.model.ComplexPreferences;

/**
 * Created by dhruvil on 30-09-2014.
 */
public class CellReadPlayNote implements View.OnClickListener{

    WMTextView tvNote;
    WMTextView lblLineNumber;
    ImageView btnEditLine;
    ImageView btnMenu;
    User user;
    Context ctx;
    private View viewMenu;
    private ImageView imgCloseMenu;
    private WMTextView btnEdit;
    private ImageView imgItemReadPLayNoteCompose;

    public int STATE_RECORD = 0;
    public int STATE_PREVIEW = 1;
    public int STATE_READ = 2;
    public int STATE_CHAT = 3;

    View convertView;

    public CellReadPlayNote(View view,Context context) {

        ctx = context;
        tvNote = (WMTextView)view.findViewById(R.id.readPlayNoteCellDescription);
        btnMenu = (ImageView)view.findViewById(R.id.readPlayNoteCellMoreImage);
        btnEditLine = (ImageView)view.findViewById(R.id.imgItemReadPLayNoteCompose);

        imgItemReadPLayNoteCompose = (ImageView)view.findViewById(R.id.imgItemReadPLayNoteCompose);
        imgItemReadPLayNoteCompose.setOnClickListener(this);

        viewMenu = (View)view.findViewById(R.id.cellMenuReadLineTeacherNote);
        imgCloseMenu = (ImageView)view.findViewById(R.id.readPlayNoteMenuMore);
        btnEdit = (WMTextView)view.findViewById(R.id.readPlayNoteMenuEdit);

        btnMenu.setOnClickListener(this);
        imgCloseMenu.setOnClickListener(this);
        btnEdit.setOnClickListener(this);

        this.convertView = view;
    }

    public void setupForPlayLine(PlayLines playline,int current_state){
        convertView.setBackgroundColor(Color.TRANSPARENT);
        if(current_state==STATE_CHAT){
            if(Integer.parseInt(playline.LineCount)== ReadActivityForChat.lineNumber) {
                convertView.setBackgroundColor(Color.parseColor("#f6f6d6"));
            }
        }
        viewMenu.setVisibility(View.GONE);
        tvNote.setText("");
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnMenu.setVisibility(View.VISIBLE);
        }else{
            btnMenu.setVisibility(View.INVISIBLE);
        }


        if(current_state == STATE_RECORD || current_state == STATE_PREVIEW){
            btnMenu.setVisibility(View.INVISIBLE);
        }

      /*  if(current_state == STATE_PREVIEW){
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.read_play_cell));
        }else{
            convertView.setBackgroundColor(Color.WHITE);
        }*/


        if(user.checkPupil(user.getRoles())){
            btnMenu.setVisibility(View.INVISIBLE);
        }

        tvNote.setTextColor(Color.parseColor(AppConstants.noteTextColor));

        ArrayList<TextLines> textLines = playline.textLinesList;

        if (textLines == null || textLines.size() == 0) {

            tvNote.setText("");

        } else if (textLines.size() == 1) {

            TextLines textLine = textLines.get(0);

            tvNote.setTextColor(Color.parseColor(AppConstants.noteTextColor));
            tvNote.setText(textLine.currentText());


        }else{
            {
                int lineCount = 0;

                for (TextLines textLine : textLines) {

                    String s = tvNote.getText().toString();
                    if (s.length() == 0) {
                        tvNote.setText(textLine.currentText());
                    } else {
                        tvNote.setText(s + "\n\n" + textLine.currentText());
                    }
                    tvNote.setTextColor(Color.parseColor(AppConstants.noteTextColor));
                }
            }
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.imgItemReadPLayNoteCompose:






                break;


            // edit called
            case R.id.readPlayNoteMenuEdit:

                if(imgItemReadPLayNoteCompose.getVisibility() == View.INVISIBLE){
                    imgItemReadPLayNoteCompose.setVisibility(View.VISIBLE);
                    hideMenu();
                }else{

                }

                break;



            // hide menu
            case R.id.readPlayNoteMenuMore:

                hideMenu();

                break;



            // show menu
            case R.id.readPlayNoteCellMoreImage:


                showMenu();
                break;

        }


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

}
