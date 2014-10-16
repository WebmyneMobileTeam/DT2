package wm.com.danteater.tab_music;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
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
import wm.com.danteater.model.AppConstants;

/**
 * Created by nirav on 01-10-2014.
 */
public class CellMusicTableView implements SeekBar.OnSeekBarChangeListener{

    private HUD dialog;
    LinearLayout musicCellLayout;
    WMTextView musicText,startTime,endTime;
    ImageView musicDownload,musicPlay;
    LinearLayout playerView;
    Context context;
    private SeekBar songProgressBar;
    Handler mHandler;
    MediaPlayer mediaPlayer=null;
    View convertView;
    private setOnReload setOnReloading;
    private SetStopMusic setStopMusicSong;
    private String currentSongPosition;
    public  DownLoadMusicTask downLoadMusicTask;
    public String musicUrl;

    public CellMusicTableView(View convertView,final Context context) {

        this.convertView=convertView;
        this.context=context;
        musicText=(WMTextView)convertView.findViewById(R.id.music_table_view_cell_title);
        musicDownload=(ImageView)convertView.findViewById(R.id.music_download);
        musicPlay=(ImageView)convertView.findViewById(R.id.music_play);
        musicCellLayout=(LinearLayout)convertView.findViewById(R.id.music_cell_layout);
        playerView=(LinearLayout)convertView.findViewById(R.id.playerView);
        startTime=(WMTextView)convertView.findViewById(R.id.start_label);
        endTime=(WMTextView)convertView.findViewById(R.id.end_label);
        songProgressBar=(SeekBar)convertView.findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(this);
    }

    public void setUpSongFile(final SongFiles songFile,final String sectionTitle,final Context context,final int section,final int position, final String playTitle) {
        try {
            if(position%2==0) {
                musicCellLayout.setBackgroundColor(Color.parseColor(AppConstants.songFileOddColor));
            } else {
                musicCellLayout.setBackgroundColor(Color.parseColor(AppConstants.songFileEvenColor));
            }
            FileDescriptor fd = null;
            File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");
            Log.e("fetched url path: ",fileDir.getAbsolutePath() +"/"+songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/")+1));
            String audioPath = fileDir.getAbsolutePath() +"/"+songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/")+1);


            if(new File(audioPath).exists()
                 ) {
                FileInputStream fis = new FileInputStream(audioPath);
                fd = fis.getFD();

                if(mediaPlayer==null) {
                    mHandler= new Handler();
                    mediaPlayer = new MediaPlayer();
                    mediaPlayer.setDataSource(fd);
                    mediaPlayer.prepare();
                    musicPlay.setImageResource(R.drawable.ic_play);
                    startTime.setText(milliSecondsToTimer(mediaPlayer.getCurrentPosition()));
                    endTime.setText(milliSecondsToTimer(mediaPlayer.getDuration()));

                } else {
                    if (mediaPlayer.isPlaying()) {
                        musicPlay.setImageResource(R.drawable.ic_pause);
                        updateProgressBar();
                    } else {
                        musicPlay.setImageResource(R.drawable.ic_play);
                    }
                }
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
                FileDescriptor fd = null;
                File fileDir = new File(Environment.getExternalStorageDirectory() + "/danteater");
                Log.e("fetched url path: ", fileDir.getAbsolutePath() + "/" + songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/") + 1));
                String audioPath = fileDir.getAbsolutePath() + "/" + songFile.SongMp3Url.substring(songFile.SongMp3Url.lastIndexOf("/") + 1);
                if (!(new File(audioPath).exists())) {
                    musicUrl=songFile.SongMp3Url.toString();
                    downloadMusic();
                }
            }
        });
        musicPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // check for already playing
                String sCheck = section+"*"+position;
                if(MusicFragment.playingMusic.size()>0 && !MusicFragment.playingMusic.get(0).equalsIgnoreCase(section+""+position)){

                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                    alert.setTitle("Afspiller");
                    alert.setMessage("Afspiller en anden sang. Venligst sæt den på pause, før du afspiller en ny. ");
                    alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alert.show();
                }else{
                    if(mediaPlayer.isPlaying()){
                        if(mediaPlayer!=null){
                            MusicFragment.isAnySongPlayed=false;
                            mediaPlayer.pause();
                            // Changing button image to play button
                            musicPlay.setImageResource(R.drawable.ic_play);
                            MusicFragment.playingMusic.remove(section+""+position);

                        }
                    }else{
                        // Resume song
                        if(mediaPlayer!=null){
                            mediaPlayer.start();
                            setStopMusicSong.onStopMusic(mediaPlayer);
                            // Changing button image to pause button
                            musicPlay.setImageResource(R.drawable.ic_pause);
                            MusicFragment.playingMusic.add(section+""+position);
                            updateProgressBar();
                        }
                    }
                }

            }

        });
    }

    private void downloadMusic() {

       downLoadMusicTask=new DownLoadMusicTask();
        StartAsyncTaskInParallel(downLoadMusicTask);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void StartAsyncTaskInParallel(DownLoadMusicTask task) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        else
            task.execute();
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
            mHandler.postDelayed(this, 1);
        }

    };

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 1);
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




    public void setMusicSongStopClicked(SetStopMusic m){
        setStopMusicSong=m;
    }

    public class DownLoadMusicTask extends  AsyncTask<Void,Integer,Void> {
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
                Log.e("file path",musicUrl.substring(musicUrl.lastIndexOf("/")+1));
                File file = new File(fileDir,musicUrl.substring(musicUrl.lastIndexOf("/")+1));
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
            Log.e("progress for single: ",values[0].toString()+"");
            if(values[0].toString().equalsIgnoreCase("100")) {
                musicDownload.setVisibility(View.GONE);
                musicPlay.setVisibility(View.VISIBLE);
                playerView.setVisibility(View.VISIBLE);
                dialog.dismiss();
                convertView.invalidate();
                setOnReloading.onReload();
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

}


interface setOnReload{

    public void onReload();

}

interface SetStopMusic {

    public void onStopMusic(MediaPlayer mediaPlayer);
}

