package wm.com.danteater.tab_read;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;

/**
 * Created by dhruvil on 29-09-2014.
 */
public class CellReadPlayPlayLine {

    private WMTextView lblRoleName;
    private WMTextView tvPlayLines;
    private ImageView btnMenu;
    private WMTextView lblLineNumber;
    private Context ctx;
    private boolean showLineNumber;



    public CellReadPlayPlayLine(View view,Context context) {

        this.ctx = context;
        lblLineNumber = (WMTextView)view.findViewById(R.id.readPlayLineCellLineNumber);
        lblRoleName = (WMTextView)view.findViewById(R.id.readPlayLineCellRollName);
        tvPlayLines = (WMTextView)view.findViewById(R.id.readPlayLineCellDescription);
        btnMenu = (ImageView)view.findViewById(R.id.readPlayLineCellMoreImage);

    }

    public void setupForPlayLine(PlayLines playLine,int current_state){

        SharedPreferences preferences = ctx.getSharedPreferences("settings", ctx.MODE_PRIVATE);
        showLineNumber = preferences.getBoolean("showLineNumber", false);

        if(showLineNumber == true){
            lblLineNumber.setText(playLine.LineCount);
        }else{
            lblLineNumber.setText("");
        }

        lblRoleName.setText(playLine.RoleName+":");

        ArrayList<TextLines> textLines = playLine.textLinesList;

        if(textLines == null || textLines.size() == 0){

            tvPlayLines.setText("");

        }else if(textLines.size() == 1){

            TextLines textLine = textLines.get(0);
            tvPlayLines.setText(textLine.currentText());

        }else{

            int lineCount = 0;
            for(TextLines textLine : textLines){

                if(lineCount == 0){
                    tvPlayLines.setText(textLine.currentText());
                }else{

                }

            }

        }




    }


}
