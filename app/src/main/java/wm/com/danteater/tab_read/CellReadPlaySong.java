package wm.com.danteater.tab_read;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.tab_music.CellMusicTableView;
import wm.com.danteater.tab_music.MusicFragment;


/**
 * Created by dhruvil on 01-10-2014.
 */
public class CellReadPlaySong {

    private setOnReload setOnReloading;
    WMTextView txtSongTitle;
    LinearLayout linearLayout;
    Context ctx;
    public int mCurrentPlayingPosition;
    public int mCurrentPlayingSection;

    public CellReadPlaySong(View view,Context context) {

        txtSongTitle = (WMTextView)view.findViewById(R.id.readPlaySongCelltitle);
        linearLayout = (LinearLayout)view.findViewById(R.id.linearSongCell);
        this.ctx = context;
        txtSongTitle.setBold();

    }

    public void setUpForPlayLine(PlayLines playLine, final int pos, final int sec){

        linearLayout.removeAllViews();
        txtSongTitle.setText("");
        txtSongTitle.setText(playLine.textLinesList.get(0).LineText);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        for(SongFiles songFile : playLine.songFilesList){

            if(songFile.FileType.equalsIgnoreCase("mp3")){

                View v = mInflater.inflate(R.layout.item_music_table_view_cell,null);
               /* WMTextView readPlaySongFileViewTitle = (WMTextView)vSongFile.findViewById(R.id.readPlaySongFileViewTitle);

                readPlaySongFileViewTitle.setText(songFile.FileDescription);*/
                CellMusicTableView vSongFile = new CellMusicTableView(v,ctx);

/*
                if(linearLayout.getChildCount()%2 == 0){
                    vSongFile.setBackgroundColor(Color.parseColor(AppConstants.songFileEvenColor));
                }else{
                    vSongFile.setBackgroundColor(Color.parseColor(AppConstants.songFileOddColor));
                }*/

                //-----------------------------------------------------------------------------------


                File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");
                final  String audioPath = fileDir.getAbsolutePath() +"/"+songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/")+1);
                int fCount = 0;
                int cCount = 0;
                if(new File(audioPath).exists()){
                    try {
                        FileInputStream fis = new FileInputStream(audioPath);
                        FileDescriptor fd = fis.getFD();
                        MediaPlayer mp = new MediaPlayer();
                        mp.setDataSource(fd);
                        mp.prepare();
                        fCount = mp.getDuration();
                        cCount = mp.getCurrentPosition();
                    }catch(FileNotFoundException e){}
                    catch (IOException e){}
                }else{
                }

                vSongFile.setCurrentPlayingPosition(MusicFragment.CURRENT_PLAYING_POSITION,MusicFragment.CURRENT_PLAYING_SECTION);
                vSongFile.setCounts(cCount,fCount);
                vSongFile.setUpSongFile(songFile,ctx,sec,pos,"HI");

                vSongFile.setReloadClicked(new CellMusicTableView.setOnReload() {
                    @Override
                    public void onReload() {
                       // musicSectionedAdapter.notifyDataSetChanged();
                        setOnReloading.onReload();

                    }
                });

                vSongFile.setOnClick(new CellMusicTableView.setOnPlayClick() {
                    @Override
                    public void onPlayClicked() {

                        if(MusicFragment.mediaPlayer != null && MusicFragment.mediaPlayer.isPlaying()){
                            //mediaPlayer.stop();
                            MusicFragment.mediaPlayer.pause();
                        }else{


                            MusicFragment.CURRENT_PLAYING_POSITION = pos;
                            MusicFragment.CURRENT_PLAYING_SECTION = sec;
                            try {
                                FileInputStream fis = new FileInputStream(audioPath);
                                FileDescriptor fd = fis.getFD();
                                MusicFragment.mediaPlayer.reset();
                                MusicFragment.mediaPlayer.setDataSource(fd);
                                MusicFragment.mediaPlayer.prepare();
                                MusicFragment.mediaPlayer.start();


                            }catch(FileNotFoundException e){}
                            catch (IOException e){}

                        }
                        //todo
                    }



                });

                //-------------------------------------------------------------------------------------
                linearLayout.addView(v,params);
                linearLayout.invalidate();



            }

        }



    }

    public void setCurrentPlayingPosition(int pos,int sec){
        this.mCurrentPlayingPosition = pos;
        this.mCurrentPlayingSection = sec;
    }

    public void setReloadClicked(setOnReload s){
        setOnReloading = s;
    }


    public interface setOnReload{

        public void onReload();

    }




}
