package wm.com.danteater.tab_read;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

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
public class CellReadPlayNote {

    WMTextView tvNote;
    WMTextView lblLineNumber;
    ImageView btnEditLine;
    ImageView btnMenu;
    User user;
    Context ctx;

    public int STATE_RECORD = 0;
    public int STATE_PREVIEW = 1;
    public int STATE_READ = 2;
    public int STATE_CHAT = 3;

    public CellReadPlayNote(View view,Context context) {

        ctx = context;
        tvNote = (WMTextView)view.findViewById(R.id.readPlayNoteCellDescription);
        btnMenu = (ImageView)view.findViewById(R.id.readPlayNoteCellMoreImage);
        btnEditLine = (ImageView)view.findViewById(R.id.imgItemReadPLayNoteCompose);

    }

    public void setupForPlayLine(PlayLines playline,int current_state){

        tvNote.setText("");
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnMenu.setVisibility(View.VISIBLE);
        }else{
            btnMenu.setVisibility(View.INVISIBLE);
        }


        if(current_state == STATE_RECORD){
            btnMenu.setVisibility(View.INVISIBLE);
        }

        if(user.checkPupil(user.getRoles())){
            btnMenu.setVisibility(View.INVISIBLE);
        }


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

}
