package wm.com.danteater.tab_read;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.AppConstants;

/**
 * Created by dhruvil on 01-10-2014.
 */
public class CellReadPlaySong {

    WMTextView txtSongTitle;
    LinearLayout linearLayout;
    Context ctx;

    public CellReadPlaySong(View view,Context context) {

        txtSongTitle = (WMTextView)view.findViewById(R.id.readPlaySongCelltitle);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearSongCell);
        this.ctx = context;
        txtSongTitle.setBold();

    }

    public void setUpForPlayLine(PlayLines playLine){

        linearLayout.removeAllViews();
        txtSongTitle.setText("");
        txtSongTitle.setText(playLine.textLinesList.get(0).LineText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        for(SongFiles songFile : playLine.songFilesList){

            if(songFile.FileType.equalsIgnoreCase("mp3")){



                View vSongFile = mInflater.inflate(R.layout.item_read_play_song_file_view,null);
                WMTextView readPlaySongFileViewTitle = (WMTextView)vSongFile.findViewById(R.id.readPlaySongFileViewTitle);
                readPlaySongFileViewTitle.setText(songFile.FileDescription);

                if(linearLayout.getChildCount()%2 == 0){
                    vSongFile.setBackgroundColor(Color.parseColor(AppConstants.songFileEvenColor));
                }else{
                    vSongFile.setBackgroundColor(Color.parseColor(AppConstants.songFileOddColor));
                }

                linearLayout.addView(vSongFile,params);
                linearLayout.invalidate();



            }

        }



    }



}
