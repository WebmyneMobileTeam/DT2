package dk.danteater.danteater.tab_read;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import java.util.ArrayList;

import dk.danteater.danteater.Play.PlayLines;
import dk.danteater.danteater.Play.TextLines;
import dk.danteater.danteater.R;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.model.AppConstants;


/**
 * Created by dhruvil on 30-09-2014.
 */
public class CellReadPlayInfo {

    WMTextView tvInfo;
    public int STATE_RECORD = 0;
    public int STATE_PREVIEW = 1;
    public int STATE_READ = 2;
    public int STATE_CHAT = 3;
    View convertView;
    Context ctx;

    public CellReadPlayInfo(View view,Context context) {
        tvInfo = (WMTextView)view.findViewById(R.id.readPlayInfoCellDescription);
        this.convertView = view;
        this.ctx = context;
    }

    public void setupForPlayLine(int indexForFirstScene,int section,PlayLines playline,int current_state){

        tvInfo.setText("");

        if(current_state == STATE_PREVIEW){
          //  convertView.setBackgroundColor(ctx.getResources().getColor(R.color.read_play_cell));
        }

        if((section<indexForFirstScene-1 || section==0) && !(current_state == STATE_RECORD)){
            convertView.setBackgroundColor(ctx.getResources().getColor(R.color.read_play_cell));
        }




        ArrayList<TextLines> textLines = playline.textLinesList;

        if (textLines == null || textLines.size() == 0) {

            tvInfo.setText("");
            return;


        } else if (textLines.size() == 1) {

            TextLines textLine = textLines.get(0);
            tvInfo.setTextColor(Color.parseColor(AppConstants.infoTextColor));
            tvInfo.setText(textLine.LineText);
            return;
        }

        for (TextLines textLine : textLines) {

            String s = tvInfo.getText().toString();
            if (s.length() == 0) {
                tvInfo.setText(textLine.currentText());
            } else {
                tvInfo.setText(s + "\n\n" + textLine.currentText());
            }
            tvInfo.setTextColor(Color.parseColor(AppConstants.infoTextColor));
        }


    }
}
