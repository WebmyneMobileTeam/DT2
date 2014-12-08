package wm.com.danteater.tab_read;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;

import wm.com.danteater.Messages.MessagesForConversation;
import wm.com.danteater.Play.Comments;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.SegmentedGroup;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.my_plays.SharedUser;
//import wm.com.danteater.tab_music.MusicFragment;

/**
 * Created by dhruvil on 29-09-2014.
 */
public class CellRecordPlayPlayLine implements SeekBar.OnSeekBarChangeListener{

    private ImageView imgPLay;
    private WMTextView btnOpTag;
    private LinearLayout listReadPlayPlaylinecell;
    private WMTextView lblRoleName;
    private WMTextView tvPlayLines;
    private WMTextView lblLineNumber;
    private Context ctx;
    private boolean showLineNumber;
    private boolean showComments;

    public int STATE_RECORD = 0;
    public int STATE_PREVIEW = 1;
    public int STATE_READ = 2;
    public int STATE_CHAT = 3;

    Play selectedPlay;

    User user;
    String currentUserName = "";
    String currentUserId = "";
    PlayLines playLine;

    //interfaces
    RecordDelegates delegate;
    RecordingAudio recordingAudio;
    PlayRecordingAudio playRecordingAudio;
    boolean mStartPlaying = true;
    UploadAudioFile uploadAudio;

    // music views
    SeekBar songProgressBar;
    WMTextView startTime;
    WMTextView endTime;
    Handler mHandler;
    Handler mHandlerModifyIcon;

    //    MediaPlayer   mPlayer = null;
//    String mFileName = null;
    TextLines textLine;
    boolean isStopPlayclicked=false;

    //dialog views
    LinearLayout recordAndPlayView;
    EditText recordPopupTextArea;
    WMTextView recordPopupSave;
    ImageView countDownNumber;
    WMTextView txtStopRecording;
    WMTextView recordPopupCancel;
    ImageView btnPlay;
    ImageView btnRecord;
    LinearLayout seekbarView;
    User cUser;
    SegmentedGroup recordSegmentGroup;
    RadioButton recordShareWithteacher;
    RadioButton recordShareWithAll;

    ArrayList<SharedUser> marrTeachers;
    String soundId;

    public CellRecordPlayPlayLine(View view, Context context) {

        this.ctx = context;
        ComplexPreferences complexPreferencesForUser = ComplexPreferences.getComplexPreferences(context, "user_pref", 0);
        cUser = complexPreferencesForUser.getObject("current_user", User.class);

        ComplexPreferences complexPreference = ComplexPreferences.getComplexPreferences(ctx, "mypref", 0);
        selectedPlay = complexPreference.getObject("selected_play",Play.class);

        mHandler = new Handler();
        mHandlerModifyIcon= new Handler();
        lblLineNumber = (WMTextView) view.findViewById(R.id.recordPlayLineCellLineNumber);
        lblRoleName = (WMTextView) view.findViewById(R.id.recordPlayLineCellRollName);
        tvPlayLines = (WMTextView) view.findViewById(R.id.recordPlayLineCellDescription);
        btnOpTag = (WMTextView) view.findViewById(R.id.btnOpTag);
        imgPLay = (ImageView) view.findViewById(R.id.recordPlayLineCellPlayButton);

        listReadPlayPlaylinecell = (LinearLayout)view.findViewById(R.id.listReadPlayPlaylinecell);
        lblRoleName.setBold();



    }

    public void setupForPlayLine(final PlayLines playLine, int current_state,boolean mark,ArrayList<SharedUser> marrTeachers) {
        this.marrTeachers=marrTeachers;
        this.playLine = playLine;
        listReadPlayPlaylinecell.removeAllViews();
        listReadPlayPlaylinecell.invalidate();
        lblRoleName.setBackgroundColor(Color.TRANSPARENT);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ctx, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        currentUserId = user.getUserId();
        currentUserName = user.getFirstName()+" "+user.getLastName();
        tvPlayLines.setText("");

        if(user.checkTeacherOrAdmin(user.getRoles())){
            btnOpTag.setVisibility(View.VISIBLE);
        }else{
            // btnOpTag.setVisibility(View.INVISIBLE);
        }

        SharedPreferences preferences = ctx.getSharedPreferences("settings", ctx.MODE_PRIVATE);
        showLineNumber = preferences.getBoolean("showLineNumber", false);
        showComments = preferences.getBoolean("showComments", false);

        if (showLineNumber == true) {
            lblLineNumber.setText(playLine.LineCount);
        } else {
            lblLineNumber.setText("");
        }

        lblRoleName.setText(playLine.RoleName + ":");

        if(mark == true){
            lblRoleName.setBackgroundColor(Color.YELLOW);
        }

        if(!cUser.checkTeacherOrAdmin(cUser.getRoles())){
            if(mark != true){
                btnOpTag.setVisibility(View.GONE);
                imgPLay.setVisibility(View.GONE);
            } else {
                btnOpTag.setVisibility(View.VISIBLE);
                imgPLay.setVisibility(View.VISIBLE);
            }
        }




        ArrayList<TextLines> textLines = playLine.textLinesList;

        if (textLines == null || textLines.size() == 0) {

            tvPlayLines.setText("");

        } else if (textLines.size() == 1) {

            textLine = textLines.get(0);
            tvPlayLines.setText(textLine.currentText());
            tvPlayLines.setTextColor(Color.parseColor(colorForLineType(textLine.textLineType())));

        } else {

            int lineCount = 0;

            for (TextLines textLine : textLines) {

                String s = tvPlayLines.getText().toString();
                if (s.length() == 0) {
                    tvPlayLines.setText(textLine.currentText());
                } else {
                    tvPlayLines.setText(s + "\n\n" + textLine.currentText());
                }
                tvPlayLines.setTextColor(Color.parseColor(colorForLineType(textLine.textLineType())));
            }

        }

        if (showComments == true) {

            ArrayList<Comments> comments = playLine.commentsList;
//            Log.e("SSSIIIEEEZZZEEE ",""+comments.size());
            ArrayList<Comments> finalComments = new ArrayList<Comments>();

            for(Comments comment : comments){

                if(comment.isPrivate && !comment.userName.equalsIgnoreCase(currentUserId)){

                }else{
                    finalComments.add(comment);
                }
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            for(Comments com : finalComments){
                View vComment = mInflater.inflate(R.layout.item_comment_list,null);
                WMTextView txt = (WMTextView)vComment.findViewById(R.id.txtItemCommentText);
                txt.setText(com.commentText);
                listReadPlayPlaylinecell.addView(vComment,params);
                listReadPlayPlaylinecell.invalidate();

            }
        }

        imgPLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgPLay.setBackgroundResource(R.drawable.ic_pause);

                delegate.onPlayClicked(playLine,imgPLay);
            }
        });


        btnOpTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRecordDialog();
            }
        });


    }

    private String colorForLineType(TextLines.TextLineType textLineType) {

        if(textLineType == TextLines.TextLineType.TextLineNormalLine){
            return AppConstants.lineTextColor;
        }else if(textLineType == TextLines.TextLineType.TextLineTypeInfo){
            return AppConstants.infoTextColor;
        }else if(textLineType == TextLines.TextLineType.TextLineTypeNote){
            return AppConstants.noteTextColor;
        }else{
            return "#000000";
        }

    }

    public void setRecordDelegates(RecordDelegates delegates){
        this.delegate = delegates;
    }


    public interface RecordDelegates{

        public void onPlayClicked(PlayLines playLine,ImageView imgPlay);

    }



    private void modifyPlayIcon() {


    }
    private void showRecordDialog(){

        final Dialog dialog = new Dialog(ctx,android.R.style.Theme_Translucent_NoTitleBar);
        WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
        lp.dimAmount=0.6f;
        dialog.getWindow().setAttributes(lp);
        dialog.setCancelable(true);
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LayoutInflater mInflater = (LayoutInflater) ctx.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.record_view_popup_menu,null);
        dialog.setContentView(view);
        dialog.show();
        dialogView(view,dialog);

    }

    private void dialogView(View view,final Dialog dialog) {

        final int[] backGroundtimer={R.drawable.ic_one,R.drawable.ic_two,R.drawable.ic_three};
        recordSegmentGroup=(SegmentedGroup)view.findViewById(R.id.recordSegmentGroup);
        recordShareWithteacher=(RadioButton)view.findViewById(R.id.recordShareWithteacher);
        recordShareWithAll=(RadioButton)view.findViewById(R.id.recordShareWithAll);
        recordAndPlayView=(LinearLayout)view.findViewById(R.id.recordAndPlayView);
        recordPopupTextArea=(EditText)view.findViewById(R.id.recordPopupTextArea);
        recordPopupSave=(WMTextView)view.findViewById(R.id.recordPopupSave);
        countDownNumber=(ImageView)view.findViewById(R.id.countDownNumber);
        txtStopRecording=(WMTextView)view.findViewById(R.id.txtStopRecording);
        songProgressBar=(SeekBar)view.findViewById(R.id.seekBar);
        songProgressBar.setOnSeekBarChangeListener(this);
        startTime=(WMTextView)view.findViewById(R.id.start_label);
        endTime=(WMTextView)view.findViewById(R.id.end_label);
        recordPopupCancel=(WMTextView)view.findViewById(R.id.recordPopupCancel);
        btnPlay=(ImageView)view.findViewById(R.id.recordPlay);
        btnRecord=(ImageView)view.findViewById(R.id.record);
        seekbarView=(LinearLayout)view.findViewById(R.id.seekbarView);

        recordPopupTextArea.setText(textLine.currentText());
        seekbarView.setVisibility(View.GONE);
        recordPopupSave.setEnabled(false);
        btnPlay.setEnabled(false);
        recordSegmentGroup.setVisibility(View.GONE);

        recordPopupCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ReadFragment.mPlayer !=null){
                    ReadFragment.mPlayer.release();
                    ReadFragment.mPlayer = null;
                }
                dialog.dismiss();
            }
        });

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                onPlay(mStartPlaying);
                if (mStartPlaying) {
                    isStopPlayclicked=false;
                    updateProgressBar();
                    btnPlay.setBackgroundResource(R.drawable.ic_pause);
                } else {
                    isStopPlayclicked=true;
                    updateProgressBar();
//                    updateProgressBarOneTime();
                    btnPlay.setBackgroundResource(R.drawable.ic_play);
                }
                mStartPlaying = !mStartPlaying;
            }
        });

        btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordAndPlayView.setVisibility(View.GONE);
                recordPopupSave.setVisibility(View.GONE);
                countDownNumber.setVisibility(View.VISIBLE);

                new CountDownTimer(4000, 1000) {

                    @Override
                    public void onFinish() {
                        Log.e("done","done");
                        countDownNumber.setVisibility(View.GONE);
                        txtStopRecording.setVisibility(View.VISIBLE);
                        recordingAudio.startRecording();


                    }

                    @Override
                    public void onTick(long millisUntilFinished) {
                        int i=(int)millisUntilFinished/1000;
                        countDownNumber.setBackgroundResource(backGroundtimer[i-1]);
                    }
                }.start();
            }

        });

        txtStopRecording.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recordingAudio.stopRecording();

                new CountDownTimer(1500, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }
                    public void onFinish() {
                        recordAndPlayView.setVisibility(View.VISIBLE);
                        recordPopupSave.setVisibility(View.VISIBLE);
                        recordPopupSave.setEnabled(true);
                        recordPopupSave.setTextColor(Color.parseColor("#006fb1"));
                        btnPlay.setEnabled(true);
                        txtStopRecording.setVisibility(View.GONE);
                        btnPlay.setBackgroundResource(R.drawable.ic_play);
                        seekbarView.setVisibility(View.VISIBLE);

                        isStopPlayclicked=false;
                        playRecordingAudio.preparePlay();
                        updateProgressBar();
                        if(cUser.checkTeacherOrAdmin(cUser.getRoles())) {
                            recordSegmentGroup.setVisibility(View.GONE);
                        } else {
                            recordSegmentGroup.setVisibility(View.VISIBLE);
                        }
                    }
                }.start();

            }
        });

        recordPopupSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("clicked:","click on save");
                //stop plaing
//                if(ReadFragment.mPlayer.isPlaying() || ReadFragment.mPlayer !=null){
//                    ReadFragment.mPlayer.release();
//                    ReadFragment.mPlayer = null;
//                }
                if(cUser.checkTeacherOrAdmin(cUser.getRoles())){
                    soundId=playLine.LineID+".aac";
                } else {
                    if(recordSegmentGroup.isShown() && recordSegmentGroup.getCheckedRadioButtonId() == R.id.recordShareWithteacher){
                        soundId=playLine.LineID+"-teacher.aac";
                    } else {
                        soundId=playLine.LineID+".aac";
                    }
                }

                didFinishSavingUserAudio();
//


            }
        });
    }

    private void didFinishSavingUserAudio() {
        if(recordSegmentGroup.isShown() && recordSegmentGroup.getCheckedRadioButtonId() == R.id.recordShareWithteacher) {
            String strMessageTextWithLinkTitleRoleLinenumber="sound-"+playLine.LineID+"\n"+selectedPlay.Title.replace(" ","_")+"-"+playLine.RoleName+"-"+playLine.LineID.substring(playLine.LineID.lastIndexOf("-")+1)+"\n"+"Lydoptagelse";

//            ReadFragment.staticdialog = new HUD(ctx,android.R.style.Theme_Translucent_NoTitleBar);
//            ReadFragment.staticdialog .title("");
//            ReadFragment.staticdialog .show();
            // send a message
            for(SharedUser assignedUser : marrTeachers) {
                createNewMessageAndSendToUser(assignedUser.userId,strMessageTextWithLinkTitleRoleLinenumber,playLine.LineID,true);

            }

        }
        didFinishUploadingUserAudioToMV();
    }

    private void didFinishUploadingUserAudioToMV() {

        boolean shareWithTeacherOnly=true;
        if(recordSegmentGroup.isShown() && recordSegmentGroup.getCheckedRadioButtonId() == R.id.recordShareWithteacher) {
            shareWithTeacherOnly=true;
        } else {
            shareWithTeacherOnly=false;
        }

        String timeStamp=((int)(System.currentTimeMillis() / 1000))+"";

        saveUserAudioForPlayLineIdString(playLine.getLineID(),timeStamp,shareWithTeacherOnly);


    }

    private void saveUserAudioForPlayLineIdString(String playLineId, String timestamp, boolean forTeachersOnly) {

        String shareType;
        if (forTeachersOnly) {
            shareType = "teacher";
        } else {
            shareType = "all";
        }
        final JSONObject requestParams=new JSONObject();
        try {


            requestParams.put("OrderID", playLineId.substring(0,playLineId.lastIndexOf("-"))+"");
            requestParams.put("LineID", playLineId+"");
            requestParams.put("TimeStamp", timestamp+"");
            requestParams.put("UserId", cUser.getUserId()+"");
            requestParams.put("shareType", shareType);


        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/Api/Audio", requestParams.toString());
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
                    Log.e("response", response + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                uploadAudio.uploadingAudio(soundId);

            }
        }.execute();


    }
    private void createNewMessageAndSendToUser(String username, String messageText, String orderAndLineId, boolean isSound ){

        final JSONObject requestParamss=new JSONObject();
        try {


            requestParamss.put("OrderId", orderAndLineId + "");
            requestParamss.put("LineId", orderAndLineId + "");
            requestParamss.put("FromUserId", cUser.getUserId() + "");
            requestParamss.put("ToUserId", username + "");
            requestParamss.put("MessageText", messageText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/Message", requestParamss.toString());
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
                    Log.e("response message", response + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);


            }
        }.execute();

    }
    private void onPlay(boolean start) {
        if (start) {
            playRecordingAudio.startPlaying();
        } else {
            playRecordingAudio.stopPlaying();
        }
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

    private Runnable mUpdateTimeTask = new Runnable() {

        public void run() {

            long totalDuration = 00;
            long currentDuration = 00;
            try {

                totalDuration = ReadFragment.mPlayer.getDuration();
                currentDuration = ReadFragment.mPlayer.getCurrentPosition();

            }catch (Exception e){};
            // Displaying Total Duration time
            endTime.setText(""+milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            startTime.setText(""+milliSecondsToTimer(currentDuration));
            // Updating progress bar
            int progress = (int)(getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            songProgressBar.setProgress(progress);

            if(songProgressBar.getProgress() == songProgressBar.getMax() || isStopPlayclicked==true){
                // setOnReloading.onReload();
                songProgressBar.setProgress(0);
                startTime.setText(""+milliSecondsToTimer(0));
                btnPlay.setBackgroundResource(R.drawable.ic_play);

            }

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 1);
        }
    };

    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 1);
    }

    private void updateProgressBarOneTime(){


//        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/danteater/recording";
//
//        mFileName += "/"+playLine.LineID+".aac";
//
//        mPlayer=new MediaPlayer();
//        try {
//            mPlayer.setDataSource(mFileName);
//            mPlayer.prepare();
//            long totalDuration = 00;
//            long currentDuration = 00;
//
//            try {
//
//                totalDuration = mPlayer.getDuration();
//                currentDuration = mPlayer.getCurrentPosition();
//
//            }catch (Exception e){
//                e.printStackTrace();
//            };
//
//            endTime.setText(""+milliSecondsToTimer(totalDuration));
//            startTime.setText(""+milliSecondsToTimer(currentDuration));
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

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
//         quickSeekProcess();
        int totalDuration = ReadFragment.mPlayer.getDuration();
        int currentPosition = progressToTimer(seekBar.getProgress(), totalDuration);
//        Log.e("current duration: ",currentPosition+"");
        // forward or backward to certain seconds
        ReadFragment.mPlayer.seekTo(currentPosition);
        // update timer progress again
        updateProgressBar();
    }

    private void quickSeekProcess() {
        ReadFragment.mPlayer.seekTo(2);
        // update timer progress again
        updateProgressBar();

    }

    public interface RecordingAudio {
        public void startRecording();
        public void stopRecording();
    }

    public void setStartRecording(RecordingAudio RecordingAudio){
        this.recordingAudio = RecordingAudio;
    }

    public interface PlayRecordingAudio {
        public void startPlaying();
        public void stopPlaying();
        public void preparePlay();

    }

    public void setPlayRecording(PlayRecordingAudio playRecordingAudio) {
        this.playRecordingAudio=playRecordingAudio;
    }

    public interface UploadAudioFile {
        public void uploadingAudio(String soundId);
    }

    public void setUploadingAudio(UploadAudioFile uploadAudio) {
        this.uploadAudio=uploadAudio;
    }





}
