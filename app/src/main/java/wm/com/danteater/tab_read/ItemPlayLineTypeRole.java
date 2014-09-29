package wm.com.danteater.tab_read;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;

/**
 * Created by dhruvil on 25-09-2014.
 */
public class ItemPlayLineTypeRole extends LinearLayout {

    private PlayLines playLine;
    private int currentState;
    private Context ctx;

    public ItemPlayLineTypeRole(Context context) {
        super(context);
        this.ctx = context;
    }

    public void init(PlayLines playline,int currentState){
        this.playLine = playline;
        this.currentState = currentState;

            switch (currentState){
                case 0:

                    break;

                case 1:

                    break;

                case 2:

                    break;

                case 3:

                    TextLines textLines = playLine.textLinesList.get(0);
                    String roleDescription = textLines.LineText;
                    if(roleDescription.length() == 0){
                        LayoutInflater.from(ctx).inflate(R.layout.item_empty_role_cell, this, true);
                    }else{
                        LayoutInflater.from(ctx).inflate(R.layout.item_read_play_role_cell, this, true);
                    }

                    break;

            }




    }



}
