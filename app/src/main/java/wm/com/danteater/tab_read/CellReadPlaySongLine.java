package wm.com.danteater.tab_read;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.AppConstants;

/**
 * Created by dhruvil on 01-10-2014.
 */
public class CellReadPlaySongLine {

    WMTextView txtRoleName;
    WMTextView txtDescription;
    View viewSongLineBottom;

    public CellReadPlaySongLine(View view,Context ctx) {

        txtDescription = (WMTextView)view.findViewById(R.id.readPlaySongLineCellDescription);
        txtRoleName = (WMTextView)view.findViewById(R.id.readPlaySongLineCellRollName);
        viewSongLineBottom = (View)view.findViewById(R.id.viewSongLineBottom);
        txtRoleName.setBold();
        txtDescription.setItalic();

    }

    public void setUpForPlayLine(PlayLines playLine){

        txtDescription.setText("");
        if(playLine.isLastSongLine){
            viewSongLineBottom.setVisibility(View.VISIBLE);
        }else{
            viewSongLineBottom.setVisibility(View.GONE);
        }

        txtRoleName.setText(playLine.RoleName+":");
        ArrayList<TextLines> textLines = playLine.textLinesList;

        if (textLines == null || textLines.size() == 0) {

            txtDescription.setText("");

        } else if (textLines.size() == 1) {

            TextLines textLine = textLines.get(0);
            txtDescription.setText(textLine.LineText);
            txtDescription.setTextColor(Color.parseColor(AppConstants.songLineTextColor));

        } else {

            int lineCount = 0;

            for (TextLines textLine : textLines) {

                String s = txtDescription.getText().toString();
                if (s.length() == 0) {
                    txtDescription.setText(textLine.LineText);
                } else {
                    txtDescription.setText(s + "\n" + textLine.LineText);
                }
                txtDescription.setTextColor(Color.parseColor(AppConstants.songLineTextColor));
            }

        }
    }
}
