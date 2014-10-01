package wm.com.danteater.tab_read;

import android.content.Context;
import android.view.View;

import java.util.ArrayList;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;

/**
 * Created by dhruvil on 01-10-2014.
 */
public class CellPreviewPlayRole {

    WMTextView previewPlayRoleTitle;
    WMTextView previewPlayRoleDescription;

    public CellPreviewPlayRole(View view, Context ctx) {

        previewPlayRoleTitle = (WMTextView)view.findViewById(R.id.previewPlayRoleTitle);
        previewPlayRoleTitle.setBold();

        previewPlayRoleDescription = (WMTextView)view.findViewById(R.id.previewPlayRoleDescription);




    }

    public void setupForPlayLine(PlayLines playLine,boolean isEmpty){


        previewPlayRoleDescription.setVisibility(View.VISIBLE);
        previewPlayRoleTitle.setVisibility(View.VISIBLE);
        previewPlayRoleTitle.setText(playLine.RoleName);

        if(isEmpty){

            previewPlayRoleDescription.setVisibility(View.GONE);


        }else{

            ArrayList<TextLines> textLines = playLine.textLinesList;

            if(textLines == null || textLines.size() == 0){

            }else{
                previewPlayRoleDescription.setText(textLines.get(0).LineText);
            }




        }

    }

}
