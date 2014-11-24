package wm.com.danteater.tab_read;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.R;
import wm.com.danteater.customviews.FullLengthListView;
import wm.com.danteater.customviews.PinnedHeaderListView;
import wm.com.danteater.customviews.SectionedBaseAdapter;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.tab_music.CellMusicTableView;
import wm.com.danteater.tab_music.CellScriptTableView;
import wm.com.danteater.tab_music.MusicFragment;



/**
 * Created by dhruvil on 01-10-2014.
 */
public class CellReadPlaySong {

    private setOnReload setOnReloading;
    WMTextView txtSongTitle;
  //  LinearLayout linearLayout;
    Context ctx;
    public int mCurrentPlayingPosition;
    public int mCurrentPlayingSection;
    private PlayLines pl;
    public FullLengthListView listMusic;
    SongAdapter adapter;

    public CellReadPlaySong(View view,Context context) {

        listMusic = (FullLengthListView)view.findViewById(R.id.listViewReadMusic);
        txtSongTitle = (WMTextView)view.findViewById(R.id.readPlaySongCelltitle);
     //   linearLayout = (LinearLayout)view.findViewById(R.id.linearSongCell);
        this.ctx = context;
        txtSongTitle.setBold();

    }

    public void setUpForPlayLine(PlayLines playLine, final int pos, final int sec){

        this.pl = playLine;
    //    linearLayout.removeAllViews();
        txtSongTitle.setText("");
        txtSongTitle.setText(playLine.textLinesList.get(0).LineText);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        ArrayList<SongFiles> arr = new ArrayList<SongFiles>();
        for(SongFiles songFile : playLine.songFilesList){
            if(songFile.FileType.equalsIgnoreCase("mp3")){
                arr.add(songFile);
            }
        }



         adapter = new SongAdapter(arr);
        listMusic.setAdapter(adapter);


        if(playLine.songFilesList != null && playLine.songFilesList.size()>0){


        }

/*
        for(SongFiles songFile : playLine.songFilesList){

            if(songFile.FileType.equalsIgnoreCase("mp3")){

                View v = mInflater.inflate(R.layout.item_music_table_view_cell,null);
                //-------------------------------------------------------------------------------------
                linearLayout.addView(v,params);
                linearLayout.invalidate();
               */
/* WMTextView readPlaySongFileViewTitle = (WMTextView)vSongFile.findViewById(R.id.readPlaySongFileViewTitle);

                readPlaySongFileViewTitle.setText(songFile.FileDescription);*//*

                CellMusicTableView vSongFile = new CellMusicTableView(v,ctx);

*/
/*
                if(linearLayout.getChildCount()%2 == 0){
                    vSongFile.setBackgroundColor(Color.parseColor(AppConstants.songFileEvenColor));
                }else{
                    vSongFile.setBackgroundColor(Color.parseColor(AppConstants.songFileOddColor));
                }*//*


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
                vSongFile.setReadView();

                vSongFile.setReloadClicked(new CellMusicTableView.setOnReload() {
                    @Override
                    public void onReload() {
                       // musicSectionedAdapter.notifyDataSetChanged();
                        setOnReloading.onReload();

                    }
                });

                vSongFile.setOnClick(new CellMusicTableView.setOnPlayClick() {
                    @Override
                    public void onPlayClicked(String hackn) {


                        if(hackn.equalsIgnoreCase(MusicFragment.HACKNUMBER)){

                            if(MusicFragment.mediaPlayer != null && MusicFragment.mediaPlayer.isPlaying()){
                                //mediaPlayer.stop();
                                MusicFragment.mediaPlayer.pause();

                            }else {

                                MusicFragment.mediaPlayer.start();
                            }


                        }else{

                            if(MusicFragment.mediaPlayer != null && MusicFragment.mediaPlayer.isPlaying()){

                                Toast.makeText(ctx, "Close first", Toast.LENGTH_SHORT).show();


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

                                MusicFragment.HACKNUMBER = hackn;

                            }



                        }

                    }



                });

          */
/*      //-------------------------------------------------------------------------------------
                linearLayout.addView(v,params);
                linearLayout.invalidate();*//*




            }

        }
*/



    }

    public class SongAdapter extends  BaseAdapter{

        private ArrayList<SongFiles> arrSong;

        public SongAdapter(ArrayList<SongFiles> arrSong) {
            this.arrSong = arrSong;
        }

        @Override
        public int getCount() {
            return arrSong.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder.ViewHolderForMusic viewHolderForMusic=null;
            ViewHolder.ViewHolderForPDF viewHolderForPDF=null;

            final SongFiles songFile = arrSong.get(position);

                if(convertView == null){
                    LayoutInflater li = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    viewHolderForMusic = new ViewHolder().new ViewHolderForMusic();
                    convertView = li.inflate(R.layout.item_music_table_view_cell, parent,false);
                    viewHolderForMusic.cellMusicTableView=new CellMusicTableView(convertView,ctx);
                    convertView.setTag(viewHolderForMusic);

                }else{
                    viewHolderForMusic = (ViewHolder.ViewHolderForMusic)convertView.getTag();
                }



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

                viewHolderForMusic.cellMusicTableView.setCurrentPlayingPosition(MusicFragment.CURRENT_PLAYING_POSITION,MusicFragment.CURRENT_PLAYING_SECTION);
                viewHolderForMusic.cellMusicTableView.setCounts(cCount,fCount);
                viewHolderForMusic.cellMusicTableView.setUpSongFile(songFile,ctx,0,position,"");
                viewHolderForMusic.cellMusicTableView.setReadView();

            viewHolderForMusic.cellMusicTableView.setReloadClicked(new CellMusicTableView.setOnReload() {
                @Override
                public void onReload() {
                    adapter.notifyDataSetChanged();
                }
            });


                viewHolderForMusic.cellMusicTableView.setOnClick(new CellMusicTableView.setOnPlayClick() {

                    @Override
                    public void onPlayClicked(String hackn) {

                        if(hackn.equalsIgnoreCase(MusicFragment.HACKNUMBER)){


                            if(MusicFragment.mediaPlayer != null && MusicFragment.mediaPlayer.isPlaying()){
                                //mediaPlayer.stop();
                                MusicFragment.mediaPlayer.pause();
                                MusicFragment.STATE_HOLDER.put(MusicFragment.HACKNUMBER,""+MusicFragment.mediaPlayer.getCurrentPosition()+"#"+MusicFragment.mediaPlayer.getDuration());

                            }else {
                                MusicFragment.mediaPlayer.start();

                            }

                        }else{
                            //..................................................................................

                                MusicFragment.CURRENT_PLAYING_POSITION = position;
                                MusicFragment.CURRENT_PLAYING_SECTION = 0;
                                try {

                                    FileInputStream fis = new FileInputStream(audioPath);
                                    FileDescriptor fd = fis.getFD();
                                    MusicFragment.mediaPlayer.reset();
                                    MusicFragment.mediaPlayer.setDataSource(fd);
                                    MusicFragment.mediaPlayer.prepare();
                                    MusicFragment.mediaPlayer.start();
                                    if(MusicFragment.STATE_HOLDER.containsKey(hackn)){
                                        int s = Integer.parseInt(MusicFragment.STATE_HOLDER.get(hackn).toString().split("#")[0]);
                                        MusicFragment.mediaPlayer.seekTo(s);
                                    }

                                }catch(FileNotFoundException e){}
                                catch (IOException e){}

                                MusicFragment.HACKNUMBER = hackn;



                          /*  if(MusicFragment.mediaPlayer != null && MusicFragment.mediaPlayer.isPlaying()){

                                Toast.makeText(ctx, "Close first", Toast.LENGTH_SHORT).show();

                            }else{

                                MusicFragment.CURRENT_PLAYING_POSITION = position;
                                MusicFragment.CURRENT_PLAYING_SECTION = 0;
                                try {

                                    FileInputStream fis = new FileInputStream(audioPath);
                                    FileDescriptor fd = fis.getFD();
                                    MusicFragment.mediaPlayer.reset();
                                    MusicFragment.mediaPlayer.setDataSource(fd);
                                    MusicFragment.mediaPlayer.prepare();
                                    MusicFragment.mediaPlayer.start();
                                    if(MusicFragment.STATE_HOLDER.containsKey(hackn)){
                                        int s = Integer.parseInt(MusicFragment.STATE_HOLDER.get(hackn).toString().split("#")[0]);
                                        MusicFragment.mediaPlayer.seekTo(s);
                                    }

                                }catch(FileNotFoundException e){}
                                catch (IOException e){}

                                MusicFragment.HACKNUMBER = hackn;

                            }*/


                            //..................................................................................

                        }

                       adapter.notifyDataSetChanged();
                    }
                });



            return convertView;
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
