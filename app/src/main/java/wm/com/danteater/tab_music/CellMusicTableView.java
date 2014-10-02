package wm.com.danteater.tab_music;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import wm.com.danteater.Play.SongFiles;
import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;

/**
 * Created by nirav on 01-10-2014.
 */
public class CellMusicTableView implements SeekBar.OnSeekBarChangeListener{

    private HUD dialog;

         WMTextView musicText,startTime,endTime;
         ImageView musicDownload,musicPlay;
         LinearLayout playerView;
        Context context;
     SeekBar songProgressBar;
    Handler mHandler;
    MediaPlayer mediaPlayer=null;
    View convertView;
    private setOnReload setOnReloading;

    public CellMusicTableView(View convertView,final Context context) {
        this.convertView=convertView;
                    this.context=context;
                    musicText=(WMTextView)convertView.findViewById(R.id.music_table_view_cell_title);
                    musicDownload=(ImageView)convertView.findViewById(R.id.music_download);
                    musicPlay=(ImageView)convertView.findViewById(R.id.music_play);

                    playerView=(LinearLayout)convertView.findViewById(R.id.playerView);
                    startTime=(WMTextView)convertView.findViewById(R.id.start_label);
                    endTime=(WMTextView)convertView.findViewById(R.id.end_label);
        songProgressBar=(SeekBar)convertView.findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(this);
    }

    public void setUpSongFile(final SongFiles songFile,final String sectionTitle,final Context context) {
        try {
            mHandler= new Handler();
            FileDescriptor fd = null;
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");
            String audioPath = fileDir.getAbsolutePath() +"/"+ sectionTitle + ".mp3";

            if(new File(audioPath).exists()) {
                FileInputStream fis = new FileInputStream(audioPath);
                fd = fis.getFD();
                if(mediaPlayer==null) {
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(fd);
                    mediaPlayer.prepare();
                } else {
                    updateProgressBar();
                }

                startTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                endTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));
                if(mediaPlayer.isPlaying()){

                    musicPlay.setImageResource(R.drawable.ic_pause);
                } else {
                    musicPlay.setImageResource(R.drawable.ic_play);
                }
                songProgressBar.setProgress(0);
                songProgressBar.setMax(100);
                musicDownload.setVisibility(View.GONE);
                musicPlay.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.VISIBLE);
            } else {
                musicDownload.setVisibility(View.VISIBLE);

                musicPlay.setVisibility(View.GONE);
                playerView.setVisibility(View.GONE);
            }




        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }

        musicText.setText("Instrumental");
        musicDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                downloadMusic(songFile.SongMp3Url,songFile.SongTitle);

            }
        });


        musicPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // check for already playing
                if(mediaPlayer.isPlaying()){
                    if(mediaPlayer!=null){
                        mediaPlayer.pause();
                        // Changing button image to play button
                        musicPlay.setImageResource(R.drawable.ic_play);
                    }
                }else{
                    // Resume song
                    if(mediaPlayer!=null){
                        mediaPlayer.start();
                        // Changing button image to pause button
                        musicPlay.setImageResource(R.drawable.ic_pause);
                    }
                }
                updateProgressBar();
            }

        });
    }

    public void downloadMusic(final String musicUrl,final String songFileName) {

        new AsyncTask<Void,Integer,Void>(){
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new HUD(context, android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("Loading...");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                int count;
                try {
                    URL url = new URL(musicUrl.replace(" ","%20"));
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.connect();
                    int lenghtOfFile = conexion.getContentLength();
                    Log.e("lenghtOfFile: ", lenghtOfFile + "");
                    File fileDir=new File(Environment.getExternalStorageDirectory()+"/danteater");
                    if(!fileDir.exists()) {
                        fileDir.mkdir();
                        Log.e("directory:","created");
                    } else {
                        Log.e("directory:","already exist");
                    }
                    File file = new File(fileDir,songFileName+".mp3");
                    FileOutputStream output = new FileOutputStream(file);
                    InputStream input = conexion.getInputStream();
                    byte data[] = new byte[1024];
                    long total = 0;
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress((int)(total*100/lenghtOfFile));
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();
//                    updateViewAfterDownLoad(position);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("error:",e+"");
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                Log.e("progress: ",values[0].toString()+"");
                if(values[0].toString().equalsIgnoreCase("100")) {
                    musicDownload.setVisibility(View.GONE);
                    musicPlay.setVisibility(View.VISIBLE);
                    playerView.setVisibility(View.VISIBLE);
                    convertView.invalidate();
                    setOnReloading.onReload();
                }
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();
            }
        }.execute();

    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long totalDuration = mediaPlayer.getDuration();
            long currentDuration = mediaPlayer.getCurrentPosition();

            // Displaying Total Duration time
            endTime.setText(""+milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            startTime.setText(""+milliSecondsToTimer(currentDuration));

            // Updating progress bar
            int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 100);
        }
    };

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }


    /**
     * Function to get Progress percentage
     * @param currentDuration
     * @param totalDuration
     * */
    public int getProgressPercentage(long currentDuration, long totalDuration){
        Double percentage = (double) 0;

        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);

        // calculating percentage
        percentage =(((double)currentSeconds)/totalSeconds)*100;

        // return percentage
        return percentage.intValue();
    }

    /**
     * Function to change progress to timer
     * @param progress -
     * @param totalDuration
     * returns current duration in milliseconds
     * */
    public int progressToTimer(int progress, int totalDuration) {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);

        // return current duration in milliseconds
        return currentDuration * 1000;
    }








    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromTouch) {

    }

    /**
     * When user starts moving the progress handler
     * */
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // remove message Handler from updating progress bar
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(mUpdateTimeTask);
        int totalDuration = mediaPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
        Log.e("current duration: ",currentPosition+"");
        // forward or backward to certain seconds
        mediaPlayer.seekTo(currentPosition);

        // update timer progress again
        updateProgressBar();
    }

    public String milliSecondsToTimer(long milliseconds){
        String finalTimerString = "";
        String secondsString = "";

        // Convert total duration into time
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        // Add hours if there
        if(hours > 0){
            finalTimerString = hours + ":";
        }
        if(seconds < 10){
            secondsString = "0" + seconds;
        }else{
            secondsString = "" + seconds;}

        finalTimerString = finalTimerString + minutes + ":" + secondsString;

        // return timer string
        return finalTimerString;
    }

    public void setReloadClicked(setOnReload s){
        setOnReloading = s;
    }

}



interface setOnReload{

    public void onReload();

}