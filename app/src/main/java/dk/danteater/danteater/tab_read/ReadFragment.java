package dk.danteater.danteater.tab_read;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;

import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;


import dk.danteater.danteater.Play.AssignedUsers;
import dk.danteater.danteater.Play.Comments;
import dk.danteater.danteater.Play.Play;
import dk.danteater.danteater.Play.PlayLines;
import dk.danteater.danteater.Play.TextLines;
import dk.danteater.danteater.R;
import dk.danteater.danteater.customviews.HUD;
import dk.danteater.danteater.customviews.PinnedHeaderListView;
import dk.danteater.danteater.customviews.SectionedBaseAdapter;
import dk.danteater.danteater.customviews.WMEdittext;
import dk.danteater.danteater.customviews.WMTextView;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.model.API;
import dk.danteater.danteater.model.CallWebService;
import dk.danteater.danteater.model.ComplexPreferences;
import dk.danteater.danteater.model.DatabaseWrapper;
import dk.danteater.danteater.model.LineNumberComparator;
import dk.danteater.danteater.model.RecordedAudio;
import dk.danteater.danteater.model.SharedPreferenceRecordedAudio;
import dk.danteater.danteater.model.StateManager;
import dk.danteater.danteater.my_plays.ChatViewFromRead;
import dk.danteater.danteater.my_plays.FragmentMyPlay;
import dk.danteater.danteater.my_plays.SelectTeacherForChat;
import dk.danteater.danteater.my_plays.SharedUser;
import dk.danteater.danteater.tab_music.MusicFragment;



public class ReadFragment extends Fragment {
    int linenumber;
    public static int staticLineNumber;
    int newLinePostion=0;
    boolean isPupil;
    public static SharedPreferenceRecordedAudio sharedPreferenceRecordedAudio;
    public boolean isUserAudioAvailable;
    public static MediaRecorder mRecorder = null;
    private static String mFileName = null;
    int mSubtractionCount = 0;
    public ArrayList<String> marrMyCastMatches;
    public ReadSectionedAdapter readSectionedAdapter;
    Reader readerForNone;
    public int goToLineNumberFromChatLink = 0;
    public PinnedHeaderListView listRead;
    private Play selectedPlay;
//    private ArrayList<PlayLines> playLinesesList;
    private ArrayList<AssignedUsers> assignedUsersesList = new ArrayList<AssignedUsers>();
    public static HUD dialog;
    private View layout_gotoLine;
    private boolean isGoToLineVisible = false;
    private Menu menu;
    private User currentUser;
    private  boolean isHeaderChecked=false;
    private boolean isplayPauseAudioclicked=false;
    public static MediaPlayer   mPlayer = null;
    File txtToSpeechfileDir;
    File recordedAudiofileDir;

    public static int STATE_RECORD = 0;
    public static int STATE_PREVIEW = 1;
    public static int STATE_READ = 2;
    public static int STATE_CHAT = 3;

    public int currentState = 0;
    private int lineNumber=0;

    private StateManager stateManager = StateManager.getInstance();

    public ArrayList<SharedUser> _marrSharedWithUsers;
    public String _marrSharedWithUsersString;
    public ArrayList<String> marrPlayLines;
    public ArrayList<String> marrPlaySections;
    public HashMap<String,ArrayList<PlayLines>> dicPlayLines;
    public HashMap<String,String> mdictSongIndexPaths;

    public boolean foundIndexOfFirstScene = false;
    int indexForFirstScene = 0;

    static final int RecordPlayRoleCell = 0;
    static final int EmptyPreviewPlayRoleCell = 1;
    static final int PreviewPlayRoleCell = 2;
    static final int EmptyPlayRoleCell = 3;
    static final int ReadPlayRoleCell = 4;
    static final int RecordPlayPlayLineCell = 5;
    static final int PreviewPlayPlayLineCell = 6;
    static final int ReadPlayPlayLineCell = 7;
    static final int PreviewReadPlayNoteCell = 8;
    static final int ReadPlayNoteCell = 9;
    static final int ReadPlayInfoCell = 10;
    static final int ReadPlayPictureCell = 11;
    static final int ReadPlaySongCell = 12;
    static final int ReadPlaySongLineCell = 13;

    private WMEdittext edGotoLine;
    private WMTextView txtGotoLine;
    File fileDir;
    public static MediaPlayer mTextToSpeechPlayer;
    public MediaPlayer downloadedSoundPlayer;
    public TextView txtSectionTile;

    public int nextLine=0;
    public int indexPostion=0;

    ArrayList<PlayLines> audioPlayLineList;
    ArrayList<RecordedAudio> recordedList;
    View include_fakeHeader;
    WMTextView hackPlayPauseAll;
    WMTextView hackSectionTitle;
    CheckBox hackCbShowMyData;
    private int hackCount=0;



    public static ReadFragment newInstance(String param1, String param2) {
        ReadFragment fragment = new ReadFragment();

        return fragment;
    }

    public ReadFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferenceRecordedAudio=new SharedPreferenceRecordedAudio();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        ComplexPreferences complexPreferencesPupil = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser = complexPreferencesPupil.getObject("current_user", User.class);
        isPupil= currentUser.checkPupil(currentUser.getRoles());
        if(isPupil){
            FragmentMyPlay.isPreview=false;
        }
        if(!FragmentMyPlay.isPreview ) {
        new CallWebService("http://api.danteater.dk/Api/Audio?UserId=&OrderId="+selectedPlay.OrderId+"&LineId=&isTeacher=false",CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(final String response) {

//                Log.e("Response recorded audio  : ", "" + response);
                Type listType = new TypeToken<ArrayList<RecordedAudio>>(){}.getType();
              recordedList = new GsonBuilder().create().fromJson(response, listType);

                if(recordedList !=null) {
                    sharedPreferenceRecordedAudio.clearAudio(getActivity());
                    for (int i = 0; i < recordedList.size(); i++) {
                        sharedPreferenceRecordedAudio.saveAudio(getActivity(), recordedList.get(i));
                    }
                }


                    try {
//                        Log.e("isPreview: ", FragmentMyPlay.isPreview + "");
                        sharedPreferenceRecordedAudio = new SharedPreferenceRecordedAudio();
                        recordedList = sharedPreferenceRecordedAudio.loadAudio(getActivity());

                        audioPlayLineList = new ArrayList<PlayLines>();
                        for (PlayLines playLine : selectedPlay.playLinesList) {
                            if (playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeLine) {
                                isUserAudioAvailable=true;
                                for (int i = 0; i < recordedList.size(); i++) {
                                    if (recordedList.get(i).getLineID().toString().contains(playLine.getLineID().toString())) {
                                        playLine.setSoundAvailable(true);
                                    }
                                }
                                audioPlayLineList.add(playLine);

                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }



            @Override
            public void error(VolleyError error) {

                Log.e("error  : ",""+error);


            }
        }.start();
        }


        fileDir = new File(Environment.getExternalStorageDirectory()+ "/danteater/recording");
        if(!fileDir.exists()) {
            fileDir.mkdirs();
//                        Log.e("directory:","created");
        } else {
//                        Log.e("directory:","already exist");
        }

//        mFileName += "/audiorecordtest.mp3";
        MusicFragment.mediaPlayer = new MediaPlayer();


        // setup actionbar methods
        ((WMTextView) getActivity().getActionBar().getCustomView()).setText(selectedPlay.Title);
        currentState = getArguments().getInt("currentState");
        lineNumber=   getArguments().getInt("line_number");

//         Log.e("lineNumber:",lineNumber+"");

        if(currentState == STATE_READ || currentState == STATE_RECORD) {
            setHasOptionsMenu(true);
        }

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        ((WMTextView)getActivity().getActionBar().getCustomView()).setGravity(Gravity.LEFT);

   /*     // setup init for read
        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        marrMyCastMatches = dbh.getMyCastMatchesForUserId(currentUser.getUserId(),Integer.parseInt(selectedPlay.PlayId));
        dbh.close();*/

//        Log.e("marrMyCastMatches   -- ",""+marrMyCastMatches);

        if(!FragmentMyPlay.isPreview){

            //TODO fetch sharing details
/*
            new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

                @Override
                public void response(String response) {

                    Type listType = new TypeToken<List<SharedUser>>() {
                    }.getType();
                    _marrSharedWithUsersString = response;
                    _marrSharedWithUsers = new ArrayList<SharedUser>();
                    _marrSharedWithUsers = new GsonBuilder().create().fromJson(response,listType);
                    Log.e("_maarSharedWithUsers ",""+_marrSharedWithUsers);
                }

                @Override
                public void error(VolleyError error) {
                Log.e("error: ",error+"");

                }
            }.start();*/
        }
        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        dbh.openDataBase();
        marrMyCastMatches = dbh.getMyCastMatchesForUserId(currentUser.getUserId(),selectedPlay.pID);
        dbh.close();
    }

    public void updatePlaySpecificData() {


        new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {

                Type listType = new TypeToken<List<SharedUser>>() {
                }.getType();
                _marrSharedWithUsersString = response;
                _marrSharedWithUsers = new ArrayList<SharedUser>();
                _marrSharedWithUsers = new GsonBuilder().create().fromJson(response,listType);
//                Log.e("_maarSharedWithUsers ",""+_marrSharedWithUsers);

            }

            @Override
            public void error(VolleyError error) {
                Log.e("error: ",error+"");
            }
        }.start();



        if(marrPlayLines == null){
            marrPlayLines = new ArrayList<String>();
        }else{
            marrPlayLines.clear();
        }

        if(marrPlaySections == null){
            marrPlaySections = new ArrayList<String>();
        }else{
            marrPlaySections.clear();
        }

        if(mdictSongIndexPaths == null){
            mdictSongIndexPaths = new HashMap<String, String>();
        }else{
            mdictSongIndexPaths.clear();
        }

        if(dicPlayLines == null){
            dicPlayLines = new HashMap<String,ArrayList<PlayLines>>();

        }else{
            dicPlayLines.clear();
        }

        if(_marrSharedWithUsers == null){
            _marrSharedWithUsers = new ArrayList<SharedUser>();
        }else{
            _marrSharedWithUsers.clear();
        }
        //
        goToLineNumberFromChatLink = 0;

        //
        foundIndexOfFirstScene = false;

        String currentKey = null;
        for(PlayLines playLine : selectedPlay.playLinesList){

            if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeAct){

                currentKey = playLine.textLinesList.get(0).LineText;
                marrPlaySections.add(currentKey); // add section
                dicPlayLines.put(currentKey,new ArrayList()); // add the section and blank arry to the section

                if(foundIndexOfFirstScene == false){

                    if(currentKey.contains("første") ||
                            currentKey.contains("1") ||
                            currentKey.contains("scene") ||
                            currentKey.contains("akt") ){

                        indexForFirstScene = marrPlaySections.size();
                        foundIndexOfFirstScene = true;
                    }
                }

            }else{

                if(isHeaderChecked==true){

                    for(int i=0;i<marrMyCastMatches.size();i++){
                        String sCheck = marrMyCastMatches.get(i).toString();
                        if(sCheck.equalsIgnoreCase(playLine.RoleName)){


                               dicPlayLines.get(currentKey).add(playLine);



                        }
                    }
                } else {
                    dicPlayLines.get(currentKey).add(playLine);
                }

                if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeSong) {
                    if (!isHeaderChecked) {
                        int section = marrPlaySections.indexOf(currentKey);
                        int row = dicPlayLines.get(currentKey).size();

                        String songTitle = playLine.textLinesList.get(0).LineText;
                        mdictSongIndexPaths.put(songTitle, section + "," + row); // hack for similar to indexPath in iOS
                        // seperated by ","
                        // First object is section and second is row
                    }
                }

            }

        }

        if(currentState == STATE_RECORD){

            ArrayList<String> toDelete = new ArrayList<String>();
            for(String section : marrPlaySections){

                if(section.contains("Personerne") || section.contains("Personer")){
                    toDelete.add(section);

                    mSubtractionCount = dicPlayLines.get(section).size() + 1;
                    dicPlayLines.remove(section);
                    indexForFirstScene--;
                    for(String title : mdictSongIndexPaths.keySet()){

                        String indexPath = mdictSongIndexPaths.get(title);
                        int s = Integer.parseInt(indexPath.split(",")[0]);
                        int r = Integer.parseInt(indexPath.split(",")[1]);
                        mdictSongIndexPaths.put(title,s-1+","+r);

                    }
                }
            }

            for(String sec : toDelete){
                marrPlaySections.remove(sec);
            }
        }

/*        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        marrMyCastMatches = dbh.getMyCastMatchesForUserId(currentUser.getUserId(),selectedPlay.pID);
        dbh.close();*/

//        Log.e("------------------ mycast read:",""+marrMyCastMatches);

//       System.out.println("-------------   Sections : "+marrPlaySections);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_read, container, false);
        layout_gotoLine = (View)convertView.findViewById(R.id.layout_item_goto_line);
        include_fakeHeader = (View)convertView.findViewById(R.id.include_fakeHeader);



//        include_fakeHeader.setVisibility(View.GONE);
        hackSectionTitle= (WMTextView)include_fakeHeader.findViewById(R.id.readPlaySectionName);

        hackPlayPauseAll= (WMTextView)include_fakeHeader.findViewById(R.id.playPauseAll);
        hackCbShowMyData= (CheckBox)include_fakeHeader.findViewById(R.id.cbShowMyData);
        listRead = (PinnedHeaderListView)convertView.findViewById(R.id.listViewRead);
        hackCbShowMyData.setVisibility(View.GONE);

        if(currentState == STATE_RECORD){

            hackPlayPauseAll.setVisibility(View.VISIBLE);
        } else {
            hackPlayPauseAll.setVisibility(View.GONE);


        }

        if(isPupil && !(currentState==STATE_RECORD)) {
            hackCbShowMyData.setVisibility(View.VISIBLE);
        }
        if(isplayPauseAudioclicked==true){
            hackPlayPauseAll.setText("Pause");
            hackPlayPauseAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);

        } else {
            hackPlayPauseAll.setText("Afspil alle");
            hackPlayPauseAll.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_play, 0, 0, 0);

        }

        hackCbShowMyData.setChecked(isHeaderChecked);

        hackPlayPauseAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isplayPauseAudioclicked==false){
                    hackPlayPauseAll.setText("Pause");
                    hackPlayPauseAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
                    isplayPauseAudioclicked=true;

                    for(int i=0;i<audioPlayLineList.size();i++){
//                            Log.e("isSoundAvailable",audioPlayLineList.get(i).isSoundAvailable()+"");
//                            Log.e("line number",(Integer.parseInt(audioPlayLineList.get(i).LineID.substring(audioPlayLineList.get(i).LineID.lastIndexOf("-") + 1))+""));
                    }
                    if(nextLine<audioPlayLineList.size()) {
                        nextLine = (Integer.parseInt(audioPlayLineList.get(indexPostion).LineID.substring(audioPlayLineList.get(indexPostion).LineID.lastIndexOf("-") + 1)));

                        listRead.setSelection(nextLine - mSubtractionCount);

                        if (audioPlayLineList.get(indexPostion).isSoundAvailable() == true) {
                            playUserAudio(audioPlayLineList.get(indexPostion), null, false, null);
                        } else {
                            downloadAndPlayRecordTextToSpeech(audioPlayLineList.get(indexPostion), null);
                        }
                    }







//                        }
                } else {
                    hackPlayPauseAll.setText("Afspil alle");
                    hackPlayPauseAll.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_play, 0, 0, 0);
                    isplayPauseAudioclicked=false;

                    if(mTextToSpeechPlayer !=null && mTextToSpeechPlayer.isPlaying()){
                        mTextToSpeechPlayer.stop();
                    }
                    if(downloadedSoundPlayer !=null && downloadedSoundPlayer.isPlaying()) {
                        downloadedSoundPlayer.stop();
                    }


                }
            }
        });

        hackCbShowMyData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checkboxValue) {
                if(checkboxValue==true){

                    isHeaderChecked=true;
                    if(currentState == STATE_RECORD) {

                    } else {

                        updatePlaySpecificData();
                        readSectionedAdapter.notifyDataSetChanged();
                    }
                } else {
                    isHeaderChecked=false;
                    if(currentState == STATE_RECORD) {

                    } else {
                        updatePlaySpecificData();
                        readSectionedAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
//        listRead.setFastScrollEnabled(true);
        listRead.setOnShowHide(new PinnedHeaderListView.OnShowHide() {
            @Override
            public void onShow(String text)
            {
                include_fakeHeader.setVisibility(View.VISIBLE);

                hackSectionTitle.setText(text);

                if(hackCbShowMyData.isShown()) {
                    hackCbShowMyData.setChecked(isHeaderChecked);
                }


            }

            @Override
            public void onHide(String text) {
                include_fakeHeader.setVisibility(View.VISIBLE);

                hackSectionTitle.setText(text);

                if(hackCbShowMyData.isShown()) {
                    hackCbShowMyData.setChecked(isHeaderChecked);
                }
            }




        });




        edGotoLine = (WMEdittext)layout_gotoLine.findViewById(R.id.edGotoLine);
        txtGotoLine = (WMTextView)layout_gotoLine.findViewById(R.id.txtGotoLine);


        LinearLayout listHeaderView = (LinearLayout)inflater.inflate(
                R.layout.item_header_listview, null);
        listRead.addHeaderView(listHeaderView);

        WMTextView headerTitle = (WMTextView)listHeaderView.findViewById(R.id.txtHeaderViewTitle);
        WMTextView headerSubTitle = (WMTextView)listHeaderView.findViewById(R.id.txtHeaderViewSubTitle);

        headerTitle.setText(selectedPlay.Title);
        headerTitle.setBold();
        headerSubTitle.setText(selectedPlay.SubtitleLong);


        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();
        hackCount=0;
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        txtGotoLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String lineNo = edGotoLine.getText().toString();

                if(lineNo == null || lineNo.equalsIgnoreCase("")){

                }else{

                    final int gotoL = 0;

                    try {
//                        listRead.setSelection(Integer.parseInt(edGotoLine.getText().toString())-1);
                        if(currentState == STATE_READ){
                            if(isHeaderChecked) {
                                //TODO

                                int n=0;
                                Set<PlayLines> list=new HashSet<PlayLines>();
                                for(Map.Entry<String,ArrayList<PlayLines>> entry : dicPlayLines.entrySet()){
                                    list.addAll(entry.getValue());
                                    n++;
//                                    Log.e("number of section: ",n+"");
                                }
                                ArrayList<PlayLines> playLineListData=new ArrayList<PlayLines>();
                                for(PlayLines playLines: list){
                                    playLineListData.add(playLines);

                                }


                                Collections.sort(playLineListData,new LineNumberComparator());
//                                Log.e("total size",listRead.getCount()+"");

//                                Log.e("filter size",playLineListData.size()+"");
                                for(int i=0;i<playLineListData.size();i++){
//                                    Log.e("searching : ","line number: "+playLineListData.get(i).LineCount+" position: "+i);
                                    if(edGotoLine.getText().toString().equalsIgnoreCase(playLineListData.get(i).LineCount+"")){
                                        newLinePostion=i;
//                                        Log.e("search result : ","line number: "+playLineListData.get(i).LineCount+" position: "+i);
                                    }
                                }


                                for(int i=0;i<listRead.getCount();i++) {

                                }
                                listRead.setSelection(newLinePostion+5);

                            } else {
                                listRead.setSelection(Integer.parseInt(edGotoLine.getText().toString())-1);
                            }

                        }else if(currentState == STATE_RECORD){
                            listRead.setSelection(Integer.parseInt(edGotoLine.getText().toString())-1-mSubtractionCount);
//                            listRead.setSelection(i-mSubtractionCount);
                        }
                    }catch(Exception e){}

                    edGotoLine.setText("");

                }
                enableDisableLineLayout();
            }
        });



        new AsyncTask<String,Integer,String>(){

            @Override
            protected String doInBackground(String... params) {


                updatePlaySpecificData();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

             /*   for(int i = 0 ; i<marrPlaySections.size(); i++){
                   Log.i(marrPlaySections.get(i)," count is : "+dicPlayLines.get(marrPlaySections.get(i)).size());
                }*/

                readSectionedAdapter = new ReadSectionedAdapter(getActivity());
                listRead.setAdapter(readSectionedAdapter);
                if(lineNumber !=0) {
                    listRead.setSelection(lineNumber - 1);
                }
            }
        }.execute();


        if(MusicFragment.mediaPlayer != null &&MusicFragment. mediaPlayer.isPlaying()){
            MusicFragment.mediaPlayer.stop();
        }


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // super.onCreateOptionsMenu(menu, inflater);
        this.menu = menu;
        getActivity().getMenuInflater().inflate(R.menu.menu_read,menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case android.R.id.home:

                if(MusicFragment.mediaPlayer !=null && MusicFragment.mediaPlayer.isPlaying()) {
                    MusicFragment.mediaPlayer.pause();
                    MusicFragment.mediaPlayer.stop();


                }
                getActivity().finish();

                break;


            case R.id.action_line_number:


                menu.getItem(0).setEnabled(false);
                enableDisableLineLayout();



                break;

            case R.id.action_section:

                viewSectionsView();



                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void viewSectionsView() {


        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.item_assign_role_page,null);
        dialog.setContentView(view);
        dialog.show();
        final WMTextView btnAssignUserTildel = (WMTextView)view.findViewById(R.id.btnAssignUserTildel);
        btnAssignUserTildel.setVisibility(View.INVISIBLE);
        WMTextView tildelRolle = (WMTextView)view.findViewById(R.id.tildelRolle);
        tildelRolle.setText("Sektioner");
        final WMTextView txtBackAssignRole = (WMTextView)view.findViewById(R.id.txtBackAssignRole);

        txtBackAssignRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAssignUserTildel.setTextColor(Color.parseColor("#313131"));
        btnAssignUserTildel.setEnabled(false);

        final ListView lstAssignRole = (ListView)view.findViewById(R.id.listAssignRoles);
        final ArrayList<String> msections = new ArrayList<String>();
        HashMap<String,String> mDict = new HashMap<String, String>();
        for(PlayLines pl : selectedPlay.playLinesList){

            if(pl.playLineType() == PlayLines.PlayLType.PlayLineTypeAct){

                msections.add(pl.textLinesList.get(0).LineText);
                mDict.put(pl.textLinesList.get(0).LineText,"Akt");


            }else if(pl.playLineType() == PlayLines.PlayLType.PlayLineTypeSong){
                msections.add(pl.textLinesList.get(0).LineText);
                mDict.put(pl.textLinesList.get(0).LineText,"Sang");
            }

        }

        lstAssignRole.setAdapter(new GoToSectionAdapter(getActivity(),msections,mDict));
        lstAssignRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                dialog.dismiss();
                String selectedString = msections.get(position);

                for(int i=0;i<selectedPlay.playLinesList.size();i++){
                    PlayLines pl = selectedPlay.playLinesList.get(i);
                    for(TextLines tl : pl.textLinesList){

                        if(tl.LineText.equalsIgnoreCase(selectedString)){

                            if(currentState == STATE_READ){
                                listRead.setSelection(i);
                            }else if(currentState == STATE_RECORD){

                                listRead.setSelection(i-mSubtractionCount);
                            }

                            break;
                        }
                    }


                }


            }
        });


    }

    public class GoToSectionAdapter extends BaseAdapter{

        private Context ctx;
        private ArrayList<String> marSections;
        private HashMap<String,String> map;

        public  GoToSectionAdapter(Context ctx,ArrayList<String> vals,HashMap<String,String> map){

            this.marSections = vals;
            this.map = map;
            this.ctx = ctx;

        }



        @Override
        public int getCount() {
            return marSections.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.item_gotosection, null);
            }

            WMTextView tv = (WMTextView)convertView.findViewById(R.id.txtGoToSectionList);
            ImageView img = (ImageView)convertView.findViewById(R.id.imgGoToSectionList);
            String text = marSections.get(position);
            tv.setText(text);

            if(map.get(text).equalsIgnoreCase("Akt")){
                img.setVisibility(View.GONE);
            }else if(map.get(text).equalsIgnoreCase("Sang")){
                img.setVisibility(View.VISIBLE);
            }

            return convertView;
        }


    }

    public void enableDisableLineLayout() {

        int static_height = 0;
        if(isGoToLineVisible == true){

            static_height = (int)(layout_gotoLine.getY() - layout_gotoLine.getHeight());
            start_onoff(static_height);

            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(edGotoLine.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


        }else{

            static_height = (int)(layout_gotoLine.getY() + layout_gotoLine.getHeight());
            start_onoff(static_height);

        }


    }

    private void start_onoff(int static_height) {

        ObjectAnimator animUp = ObjectAnimator.ofFloat(layout_gotoLine,"y",static_height);
        animUp.setDuration(300);
        animUp.start();
        animUp.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) { }

            @Override
            public void onAnimationEnd(Animator animation) {

                menu.getItem(0).setEnabled(true);
                isGoToLineVisible = !isGoToLineVisible;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });


    }

    public class ReadSectionedAdapter extends SectionedBaseAdapter {

        private LayoutInflater mInflater;

        public ReadSectionedAdapter(Context context) {

            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public Object getItem(int section, int position) {

            return null;
        }

        @Override
        public long getItemId(int section, int position) {

            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 14;
        }

        @Override
        public int getItemViewType(int section, int position) {

            PlayLines playLine = dicPlayLines.get(marrPlaySections.get(section)).get(position);

//          Log.e("GetItemViewType  :  Type : ","pos : "+position+" "+playLine.playLineType());

            if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeRole){

                TextLines textLines = playLine.textLinesList.get(0);
                String roleDescription = textLines.LineText;

                if(currentState == STATE_RECORD){

                    return RecordPlayRoleCell;

                }else if(currentState == STATE_PREVIEW){

                    if(roleDescription.length() == 0){
                        return EmptyPreviewPlayRoleCell;
                    }else{
                        return  PreviewPlayRoleCell;
                    }

                }else{
                    if(roleDescription.length() == 0){
                        return EmptyPlayRoleCell;
                    }else{
                        return  ReadPlayRoleCell;
                    }
                }




            }else if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeLine){

                if(currentState == STATE_RECORD) {

                    if (currentState == STATE_CHAT) {
                        isUserAudioAvailable = true;

                    } else {
                        isUserAudioAvailable = false;
                    }
                    if (!(currentState == STATE_CHAT)) {

//                        bool isSoundAvailable = [[[[StateManager sharedInstance] userAudiosTimestamps] allKeys] containsObject:playLine.lineIdString];
//                        [cell setUserAudioState:isSoundAvailable];

                        try {

                            for (int i = 0; i < recordedList.size(); i++) {
                                if (recordedList.get(i).getLineID().toString().contains(playLine.getLineID().toString())) {
                                    isUserAudioAvailable = true;
//                                    Log.e("is User Audio .......................",isUserAudioAvailable+"");
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    return RecordPlayPlayLineCell;

                }else if(currentState == STATE_PREVIEW){

                    return  PreviewPlayPlayLineCell;

                }else{
                    return ReadPlayPlayLineCell;
                }


            }else if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeNote){

                if(currentState == STATE_PREVIEW){

                    return PreviewReadPlayNoteCell;

                }else{
                    return ReadPlayNoteCell;
                }


            }else if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeInfo){

                return ReadPlayInfoCell;

            }else if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypePicutre){

                return ReadPlayPictureCell;

            }else if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeSong){

                return ReadPlaySongCell;

            }else if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeSongLine ||
                    playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeSongLineVerse){

                return ReadPlaySongLineCell;

            }else{

                if(currentState == STATE_PREVIEW){

                    return PreviewReadPlayNoteCell;
                }else{
                    return ReadPlayNoteCell;
                }



            }

        }

        @Override
        public int getSectionCount() {
            return marrPlaySections.size();
        }

        @Override
        public int getCountForSection(int section) {
            return dicPlayLines.get(marrPlaySections.get(section)).size();
        }

        @Override
        public View getItemView(final int section, final int position, View convertView, ViewGroup parent) {

            ViewHolder.HolderRecordPlayRoleCell holderRecordPlayRoleCell = null;
            ViewHolder.HolderEmptyPreviewPlayRoleCell holderEmptyPreviewPlayRoleCell = null;
            // ViewHolder.HolderPreviewPlayRoleCell holderPreviewPlayRoleCell = null;
            ViewHolder.HolderEmptyPlayRoleCell holderEmptyPlayRoleCell = null;
            ViewHolder.HolderReadPlayRoleCell holderReadPlayRoleCell = null;
            ViewHolder.HolderRecordPlayPlayLineCell holderRecordPlayPlayLineCell = null;
            ViewHolder.HolderPreviewPlayPlayLineCell holderPreviewPlayPlayLineCell = null;
            ViewHolder.HolderReadPlayPlayLineCell holderReadPlayPlayLineCell = null;
            ViewHolder.HolderPreviewReadPlayNoteCell holderPreviewReadPlayNoteCell = null;
            ViewHolder.HolderReadPlayNoteCell holderReadPlayNoteCell = null;
            ViewHolder.HolderReadPlayInfoCell holderReadPlayInfoCell = null;
            ViewHolder.HolderReadPlayPictureCell holderReadPlayPictureCell = null;
            ViewHolder.HolderReadPlaySongCell holderReadPlaySongCell = null;
            ViewHolder.HolderReadPlaySongLineCell holderReadPlaySongLineCell = null;

            final PlayLines playLine = dicPlayLines.get(marrPlaySections.get(section)).get(position);

            int type = getItemViewType(section,position);

            switch (type){

                case RecordPlayRoleCell:

                    //
                    if(convertView == null){

                        convertView = mInflater.inflate(R.layout.item_record_play_role_cell, parent,false);
                        holderRecordPlayRoleCell = new ViewHolder().new HolderRecordPlayRoleCell();
                        convertView.setTag(holderRecordPlayRoleCell);

                    }else{
                        holderRecordPlayRoleCell = (ViewHolder.HolderRecordPlayRoleCell)convertView.getTag();
                    }

                    break;

                case EmptyPreviewPlayRoleCell:
                case PreviewPlayRoleCell:
                    //

                    if(convertView == null){
                        convertView = mInflater.inflate(R.layout.item_preview_play_role_cell, parent,false);
                        holderEmptyPreviewPlayRoleCell = new ViewHolder().new HolderEmptyPreviewPlayRoleCell();
                        holderEmptyPreviewPlayRoleCell.cellEmptyPreviewPlayRole = new CellPreviewPlayRole(convertView,getActivity());
                        convertView.setTag(holderEmptyPreviewPlayRoleCell);

                    }else{
                        holderEmptyPreviewPlayRoleCell = (ViewHolder.HolderEmptyPreviewPlayRoleCell)convertView.getTag();
                    }

                    boolean isEmptyPreview = false;
                    if(type == PreviewPlayRoleCell){

                        isEmptyPreview = false;
                    }else if(type == EmptyPreviewPlayRoleCell){
                        isEmptyPreview = true;
                    }
                    holderEmptyPreviewPlayRoleCell.cellEmptyPreviewPlayRole.setupForPlayLine(playLine,isEmptyPreview);

                    break;


                case EmptyPlayRoleCell:
                case ReadPlayRoleCell:

                    //
                    if(convertView == null){

                        convertView = mInflater.inflate(R.layout.item_read_play_role_cell, parent,false);
                        holderReadPlayRoleCell = new ViewHolder().new HolderReadPlayRoleCell();
                        holderReadPlayRoleCell.cellReadPlayRole = new CellReadPlayRole(convertView,getActivity());
                        convertView.setTag(holderReadPlayRoleCell);

                    }else{
                        holderReadPlayRoleCell = (ViewHolder.HolderReadPlayRoleCell)convertView.getTag();
                    }



                    boolean isEmpty = false;
                    if(type == ReadPlayRoleCell){

                        isEmpty = false;
                    }else if(type == EmptyPlayRoleCell){
                        isEmpty = true;
                    }

                    boolean mark = false;


                    for(int i=0;i<marrMyCastMatches.size();i++){
                        String sCheck = marrMyCastMatches.get(i).toString();
                        if(sCheck.equalsIgnoreCase(playLine.RoleName)){
                            mark = true;
                        }
                    }
                    //TODO
                    holderReadPlayRoleCell.cellReadPlayRole.setupForPlayLine(playLine,currentState,convertView,isEmpty,mark);

                    holderReadPlayRoleCell.cellReadPlayRole.setAssignClicked(new setOnAssignButtonClicked() {
                        @Override
                        public void onAssignButtonClicked() {
                            gotoAssignUserList(playLine);

                        }
                    });

                    break;

                case RecordPlayPlayLineCell:

                    //
                    if(convertView == null){
                        convertView = mInflater.inflate(R.layout.item_record_play_line_cell, parent,false);
                        holderRecordPlayPlayLineCell = new ViewHolder().new HolderRecordPlayPlayLineCell();
                        holderRecordPlayPlayLineCell.cellRecordPlayPlayLine = new CellRecordPlayPlayLine(convertView,getActivity());
                        convertView.setTag(holderRecordPlayPlayLineCell);

                    }else{
                        holderRecordPlayPlayLineCell = (ViewHolder.HolderRecordPlayPlayLineCell)convertView.getTag();
                    }

                    boolean mark2 = false;
                    for(int i=0;i<marrMyCastMatches.size();i++){
                        String sCheck = marrMyCastMatches.get(i).toString();
                        if(sCheck.equalsIgnoreCase(playLine.RoleName)){
                            mark2 = true;

                        }
                    }
                    ArrayList<SharedUser> marrTeachers=new ArrayList<SharedUser>();
                    for(SharedUser au : _marrSharedWithUsers){

                        String check = "lærer";

                        if(au.userName != null && au.userName.contains(check.toString())){
                            marrTeachers.add(au);
                        }
                    }

                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setupForPlayLine(playLine,currentState,mark2,marrTeachers, isUserAudioAvailable);
                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setReloading(new CellRecordPlayPlayLine.ReloadListView() {
                        @Override
                        public void reload() {
                            readSectionedAdapter.notifyDataSetChanged();
                        }
                    });
                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setRecordDelegates(new CellRecordPlayPlayLine.RecordDelegates() {


                        @Override
                        public void onPlayClicked(PlayLines playLine, ImageView imgPlay, boolean isUserAudioAvailable, boolean isRecordButton, WMTextView endTime) {

                            if(isUserAudioAvailable==true){
                                playUserAudio(playLine,imgPlay,isRecordButton,endTime);
                            } else {
                                downloadAndPlayRecordTextToSpeech(playLine,imgPlay);
                            }
                        }
                    });

                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setStartRecording(new CellRecordPlayPlayLine.RecordingAudio() {
                        @Override
                        public void startRecording() {
//                            Log.e("recording: ","recording started");

                            new AsyncTask<Void,Void,Void>(){
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    try {
                                        mRecorder = new MediaRecorder();
                                        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                                        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                                        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                                        mFileName = fileDir.getAbsolutePath();
                                        mFileName += "/"+playLine.LineID+".aac";
//                                        Log.e("line id.............:",playLine.LineID+"");
                                        mRecorder.setOutputFile(mFileName);

                                        try {
                                            mRecorder.prepare();
                                        } catch (IllegalStateException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                        mRecorder.start();
                                    } catch (Exception e) {
                                        e.getMessage();
                                    }


                                    return null;
                                }
                            }.execute();

                        }

                        @Override
                        public void stopRecording() {
//                            Log.e("recording: ","recording ended");
                            new AsyncTask<Void,Void,Void>() {
                                @Override
                                protected Void doInBackground(Void... voids) {
                                    mRecorder.stop();
                                    mRecorder.release();
                                    mRecorder = null;
                                    return null;
                                }
                            }.execute();

                        }
                    });


                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setPlayRecording(new CellRecordPlayPlayLine.PlayRecordingAudio() {
                        @Override
                        public void startPlaying() {
                            mPlayer = new MediaPlayer();
                            try {

                                mPlayer.setDataSource(mFileName);

                                mPlayer.prepare();
                                mPlayer.start();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void stopPlaying() {
//                            mPlayer.release();
//                            mPlayer = null;
                            mPlayer.stop();
                        }

                        @Override
                        public void preparePlay() {
                            mPlayer = new MediaPlayer();
                            try {
                                mPlayer.setDataSource(mFileName);
                                mPlayer.prepare();

                            } catch (IOException e) {
                                e.printStackTrace();
                            }



                        }
                    });

//                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setUploadingAudio(new CellRecordPlayPlayLine.UploadAudioFile() {
//                        @Override
//                        public void uploadingAudio(String soundId) {
//                            Log.e("upload audio","uploading");
//
//
//                            uploadFileToServer(soundId);
//                        }
//                    });

                    break;

                case PreviewPlayPlayLineCell:
                case ReadPlayPlayLineCell:
                    //
                    if(convertView == null){
                        convertView = mInflater.inflate(R.layout.item_read_play_line_cell, parent,false);
                        holderReadPlayPlayLineCell = new ViewHolder().new HolderReadPlayPlayLineCell();
                        holderReadPlayPlayLineCell.cellReadPlayPlayLine = new CellReadPlayPlayLine(convertView,getActivity());
                        convertView.setTag(holderReadPlayPlayLineCell);

                    }else{
                        holderReadPlayPlayLineCell = (ViewHolder.HolderReadPlayPlayLineCell)convertView.getTag();
                    }

                    boolean mark22 = false;
                    for(int i=0;i<marrMyCastMatches.size();i++){
                        String sCheck = marrMyCastMatches.get(i).toString();
                        if(sCheck.equalsIgnoreCase(playLine.RoleName)){
                            mark22 = true;

                        }
                    }


                    holderReadPlayPlayLineCell.cellReadPlayPlayLine.setupForPlayLine(indexForFirstScene,section,position,playLine,currentState,mark22);
                    holderReadPlayPlayLineCell.cellReadPlayPlayLine.setOnTextLineUpdated(new CellReadPlayPlayLine.OnTextLineUpdated() {

                        // delegate method called after textline changes
                        @Override
                        public void onTextLineUpdated(String newText,int pos) {
//                            Log.e("TextLine ",""+section+":"+position);

                            playLine.textLinesList.get(pos).alteredLineText = newText;
                            playLine.textLinesList.get(pos).LineText = "";
                            callServiceForTextLineUpdate(playLine);


                        }

                        // delegate method called after comment added
                        @Override
                        public void onCommentAdded(String comment,boolean isPrivate) {

                            Comments com = new Comments();
                            com.commentText = comment;
                            com.isPrivate = isPrivate;
                            com.userName = currentUser.getUserId();

                            if(playLine.commentsList == null){
                                playLine.commentsList = new ArrayList<Comments>();
                            }
                            playLine.commentsList.add(com);
                            callServiceForCommentAdded(playLine);

                        }

                        @Override
                        public void onChatClicked() {


                            proceedMessage(playLine);


                        }
                    });



                    break;


                case PreviewReadPlayNoteCell:
                case ReadPlayNoteCell:
                    //
                    if(convertView == null){
                        convertView = mInflater.inflate(R.layout.item_read_play_note_cell, parent,false);
                        holderReadPlayNoteCell = new ViewHolder().new HolderReadPlayNoteCell();
                        holderReadPlayNoteCell.cellReadPlayNote = new CellReadPlayNote(convertView,getActivity());
                        convertView.setTag(holderReadPlayNoteCell);
                    }else{
                        holderReadPlayNoteCell = (ViewHolder.HolderReadPlayNoteCell)convertView.getTag();
                    }

                    holderReadPlayNoteCell.cellReadPlayNote.setupForPlayLine(indexForFirstScene,section,playLine,currentState);

                    holderReadPlayNoteCell.cellReadPlayNote.setOnTextLineUpdated(new CellReadPlayNote.OnTextLineUpdated() {
                        @Override
                        public void onTextLineUpdated(String newText) {

                            playLine.textLinesList.get(0).alteredLineText = newText;
                            playLine.textLinesList.get(0).LineText = "";
                            callServiceForTextLineUpdate(playLine);
                        }
                    });

                    break;

                case ReadPlayInfoCell:
                    //
                    if(convertView == null){

                        convertView = mInflater.inflate(R.layout.item_read_play_info_cell, parent,false);
                        holderReadPlayInfoCell = new ViewHolder().new HolderReadPlayInfoCell();
                        holderReadPlayInfoCell.cellReadPlayInfo = new CellReadPlayInfo(convertView,getActivity());
                        convertView.setTag(holderReadPlayInfoCell);

                    }else{
                        holderReadPlayInfoCell = (ViewHolder.HolderReadPlayInfoCell)convertView.getTag();
                    }
                    holderReadPlayInfoCell.cellReadPlayInfo.setupForPlayLine(indexForFirstScene,section,playLine,currentState);

                    break;


                case ReadPlayPictureCell:

                    //
                    if(convertView == null){
                        convertView = mInflater.inflate(R.layout.item_read_play_picture_cell, parent,false);
                        holderReadPlayPictureCell = new ViewHolder().new HolderReadPlayPictureCell();
                        holderReadPlayPictureCell.cellReadPlayPicture = new CellReadPlayPicture(convertView,getActivity());
                        convertView.setTag(holderReadPlayPictureCell);

                    }else{
                        holderReadPlayPictureCell = (ViewHolder.HolderReadPlayPictureCell)convertView.getTag();
                    }

                    holderReadPlayPictureCell.cellReadPlayPicture.setupForPlayLine(playLine);

                    break;

                case ReadPlaySongCell:
                    //
                    if(convertView == null){

                        convertView = mInflater.inflate(R.layout.item_read_play_song_cell, parent,false);
                        holderReadPlaySongCell = new ViewHolder().new HolderReadPlaySongCell();
                        holderReadPlaySongCell.cellReadPlaySong = new CellReadPlaySong(convertView,getActivity());
                        convertView.setTag(holderReadPlaySongCell);

                    }else{
                        holderReadPlaySongCell = (ViewHolder.HolderReadPlaySongCell)convertView.getTag();
                    }
                    holderReadPlaySongCell.cellReadPlaySong.setUpForPlayLine(playLine,position,section);
                    holderReadPlaySongCell.cellReadPlaySong.setReloadClicked(new CellReadPlaySong.setOnReload() {
                        @Override
                        public void onReload() {
                            readSectionedAdapter.notifyDataSetChanged();
                        }
                    });

                    break;


                case ReadPlaySongLineCell:

                    //
                    if(convertView == null){

                        convertView = mInflater.inflate(R.layout.item_read_play_song_line_cell, parent,false);
                        holderReadPlaySongLineCell = new ViewHolder().new HolderReadPlaySongLineCell();
                        holderReadPlaySongLineCell.cellReadPlaySongLine = new CellReadPlaySongLine(convertView,getActivity());
                        convertView.setTag(holderReadPlaySongLineCell);

                    }else{
                        holderReadPlaySongLineCell = (ViewHolder.HolderReadPlaySongLineCell)convertView.getTag();
                    }
                    holderReadPlaySongLineCell.cellReadPlaySongLine.setUpForPlayLine(playLine);
                    break;

                default:
                    //
                    if(convertView == null){
                        convertView = mInflater.inflate(R.layout.item_preview_read_play_note_cell, parent,false);
                        holderPreviewReadPlayNoteCell = new ViewHolder().new HolderPreviewReadPlayNoteCell();
                        convertView.setTag(holderPreviewReadPlayNoteCell);

                    }else{
                        holderPreviewReadPlayNoteCell = (ViewHolder.HolderPreviewReadPlayNoteCell)convertView.getTag();
                    }

                    break;

            }

            return convertView;

        }

        private void callServiceForTextLineUpdate(PlayLines playLine) {


            final JSONObject methodParams = new JSONObject();

            JSONArray arr = new JSONArray();

            for(TextLines textLine : playLine.textLinesList){

                JSONObject requestParams = new JSONObject();
                try {
                    requestParams.put("LineType",textLine.LineType);
                    requestParams.put("LineText",textLine.LineText);
                    if(textLine.alteredLineText == null || textLine.alteredLineText.equalsIgnoreCase("")){
                        requestParams.put("AlteredLineText","");
                    }else{
                        requestParams.put("AlteredLineText",textLine.alteredLineText);
                    }

                    arr.put(requestParams);
                } catch (JSONException je) {
                    je.printStackTrace();
                }

            }

            try {
                methodParams.put("LineCount", playLine.LineCount);
                methodParams.put("LineID",playLine.LineID);
                methodParams.put("TextLines",arr);

            }catch (Exception e){

                e.printStackTrace();

            }

            updatePlayUsingMethodParams(methodParams.toString());


        }

        @Override
        public View getSectionHeaderView(int section, View convertView, ViewGroup parent) {

            LinearLayout layout = null;
            CheckBox cbShowMyData;
            final WMTextView playPasueAll;
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.item_read_play_section_view, null);
            } else {
                layout = (LinearLayout) convertView;
            }

            if(hackCount==0){
                hackSectionTitle.setText(marrPlaySections.get(0));
                hackCount=hackCount+1;
            }

            ((TextView) layout.findViewById(R.id.readPlaySectionName)).setText(marrPlaySections.get(section));
            cbShowMyData=(CheckBox) layout.findViewById(R.id.cbShowMyData);
            playPasueAll=(WMTextView) layout.findViewById(R.id.playPauseAll);
            cbShowMyData.setVisibility(View.GONE);
            playPasueAll.setVisibility(View.GONE);
//            if(currentState == STATE_RECORD){
//
//                playPasueAll.setVisibility(View.VISIBLE);
//            } else {
//                playPasueAll.setVisibility(View.GONE);
//
//
//            }
//
//
//
//            if(isPupil && !(currentState==STATE_RECORD)) {
//                cbShowMyData.setVisibility(View.VISIBLE);
//            }
//            if(isplayPauseAudioclicked==true){
//                playPasueAll.setText("Pause");
//                playPasueAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
//
//                hackPlayPauseAll.setText("Pause");
//                hackPlayPauseAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
//            } else {
//                playPasueAll.setText("Afspil alle");
//                playPasueAll.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_play, 0, 0, 0);
//                hackPlayPauseAll.setText("Afspil alle");
//                hackPlayPauseAll.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_play, 0, 0, 0);
//            }

//            cbShowMyData.setChecked(isHeaderChecked);

            playPasueAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isplayPauseAudioclicked==false){
                        playPasueAll.setText("Pause");
                        playPasueAll.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_pause, 0, 0, 0);
                        isplayPauseAudioclicked=true;

                        for(int i=0;i<audioPlayLineList.size();i++){
//                            Log.e("isSoundAvailable",audioPlayLineList.get(i).isSoundAvailable()+"");
//                            Log.e("line number",(Integer.parseInt(audioPlayLineList.get(i).LineID.substring(audioPlayLineList.get(i).LineID.lastIndexOf("-") + 1))+""));
                        }
                            if(nextLine<audioPlayLineList.size()) {
                                nextLine = (Integer.parseInt(audioPlayLineList.get(indexPostion).LineID.substring(audioPlayLineList.get(indexPostion).LineID.lastIndexOf("-") + 1)));

                                listRead.setSelection(nextLine - mSubtractionCount);

                                if (audioPlayLineList.get(indexPostion).isSoundAvailable() == true) {
                                    playUserAudio(audioPlayLineList.get(indexPostion), null, false, null);
                                } else {
                                    downloadAndPlayRecordTextToSpeech(audioPlayLineList.get(indexPostion), null);
                                }
                            }







//                        }
                    } else {
                        playPasueAll.setText("Afspil alle");
                        playPasueAll.setCompoundDrawablesWithIntrinsicBounds( R.drawable.ic_play, 0, 0, 0);
                        isplayPauseAudioclicked=false;

                        if(mTextToSpeechPlayer !=null && mTextToSpeechPlayer.isPlaying()){
                            mTextToSpeechPlayer.stop();
                        }
                        if(downloadedSoundPlayer !=null && downloadedSoundPlayer.isPlaying()) {
                            downloadedSoundPlayer.stop();
                        }


                    }
                }
            });

            cbShowMyData.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean checkboxValue) {
                    if(checkboxValue==true){

                        isHeaderChecked=true;

                        if(currentState == STATE_RECORD) {

                        } else {

                            updatePlaySpecificData();
                            notifyDataSetChanged();
                        }
                    } else {
                        isHeaderChecked=false;

                        if(currentState == STATE_RECORD) {

                        } else {
                            updatePlaySpecificData();
                            notifyDataSetChanged();
                        }
                    }
                }
            });

            return layout;
        }

    }

    private void playUserAudio(PlayLines playLines, ImageView imgPlay,boolean isRecordButton, WMTextView endTime) {
        boolean shouldDownloadLastestVersion = true;
        String soundId;
        if(currentState==STATE_CHAT){
            soundId=playLines.LineID+"-teacher.aac";
        }else {
            soundId=playLines.LineID+".aac";
        }

        if(!(currentState==STATE_CHAT)){
//        if(currentFiledate.equal serverFileData){
//        shouldDownloadLastestVersion=false;
//            playDownloadedAudio(playLines,imgPlay,soundId);
//        }
        }

        if(shouldDownloadLastestVersion==true){
//            Log.e("playLines..........................",playLines.LineID+"");
            downloadAndPlayreordedAudio(soundId, playLines, imgPlay, isRecordButton, endTime);
        }
    }

    private void downloadAndPlayreordedAudio(final String soundId, final PlayLines playLines, final ImageView imgPlay,final boolean isRecordButton, final WMTextView endTime){
        SharedPreferences preferences = getActivity().getSharedPreferences("session_id", getActivity().MODE_PRIVATE);
        String sessionId = preferences.getString("session_id","");

        final  JSONObject mainOBJ = new JSONObject();
        final  JSONObject args=new JSONObject();
        try {
            args.put("session_id",sessionId);
            args.put("audio_id",soundId);
            args.put("codec","aac");
            args.put("sample_rate",0);

            mainOBJ.put("type","jsonwsp/request");
            mainOBJ.put("version","1.0");
            mainOBJ.put("methodname","downloadAudio");
            mainOBJ.put("args",args);

            new AsyncTask<Void,Void,Void>() {

                String responseString = "";

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    if(!isplayPauseAudioclicked) {
                        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                        dialog.title("Henter lyd");
                        dialog.show();
                    }
                }

                @Override
                protected Void doInBackground(Void... voids) {

                    try {

                        String service_url = "https://mvid-services.mv-nordic.com/theater-v1/AudioService/jsonwsp";
                        //
                        HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                        DefaultHttpClient client = new DefaultHttpClient();
                        SchemeRegistry registry = new SchemeRegistry();
                        SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                        socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                        registry.register(new Scheme("https", socketFactory, 443));
                        SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
                        DefaultHttpClient httpclient = new DefaultHttpClient(mgr, client.getParams());

// Set verifier
                        HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

//                 HttpClient httpclient = new DefaultHttpClient();
                        //  httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                        HttpPost httppost = new HttpPost(service_url);
                        String boundary = "--" + "62cd4a08872da000cf5892ad65f1ebe6";
                        httppost.setHeader("Content-type", "multipart/related; boundary=" + boundary);

                        HttpEntity entity = MultipartEntityBuilder.create()
                                .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                                .setBoundary(boundary)
                                .addPart("body", new StringBody(mainOBJ.toString()))
                                .build();



                        httppost.setEntity(entity);
                        try {
                            HttpResponse response = httpclient.execute(httppost);
                            BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                            responseString = reader.readLine();


                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }catch (Exception e){}

                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);


                    String url = "";
//                    Log.e("response string............ ", "" + responseString.toString());
                    try{
                        JSONObject jsonObject = new JSONObject(responseString.toString());
                        JSONObject inerObj = jsonObject.getJSONObject("result");
                        url = inerObj.getString("url");

//                        Log.e("URL ", "" + url.toString());

                    }catch(Exception e){}


                    downloadRecordedFile(url,playLines,imgPlay,soundId,isRecordButton,endTime);
                }
            }.execute();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void downloadRecordedFile(String url, final PlayLines playLines,final ImageView imgPlay, final String soundId,final boolean isRecordButton,final WMTextView endTime){

        final  String theURL ="https://mvid-services.mv-nordic.com/theater-v1/"+url;
//        Log.e("final url to be download ",""+theURL);

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                recordedAudiofileDir = new File(Environment.getExternalStorageDirectory()+ "/danteater/recording");
                if(!recordedAudiofileDir.exists()) {
                    recordedAudiofileDir.mkdirs();
//                        Log.e("directory:","created");
                } else {
//                        Log.e("directory:","already exist");
                }

            }

            @Override

            protected Void doInBackground(Void... voids) {

                int count;
                try{

                    URL url = new URL(theURL);
                    java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL(theURL).openStream());
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(new File(recordedAudiofileDir.getAbsolutePath()+"/"+soundId));
                    java.io.BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
                    byte[] data = new byte[1024];
                    int x=0;
                    while((x=in.read(data,0,1024))>=0){
                        bout.write(data,0,x);
                    }
                    fos.flush();
                    bout.flush();
                    fos.close();
                    bout.close();
                    in.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();

                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(!isplayPauseAudioclicked) {
                    if(dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
                if(isRecordButton==true){
                    mFileName = fileDir.getAbsolutePath();
                    mFileName += "/"+soundId;
                    mPlayer = new MediaPlayer();
                    try {

                        mPlayer.setDataSource(mFileName);

                        mPlayer.prepare();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    long totalDuration = 00;

                    try {

                        totalDuration =mPlayer.getDuration();

                    }catch (Exception e){};
                    // Displaying Total Duration time
                    endTime.setText(""+milliSecondsToTimer(totalDuration));
                } else {
                    playDownloadedAudio(playLines,imgPlay,soundId);
                }
            }
        }.execute();
    }


    private void playDownloadedAudio(PlayLines playLines, final ImageView imgPlay,final String soundId){
        downloadedSoundPlayer= new MediaPlayer();
        try {

            downloadedSoundPlayer.setDataSource(Environment.getExternalStorageDirectory()+ "/danteater/recording/"+soundId);
            downloadedSoundPlayer.prepare();
            downloadedSoundPlayer.start();
            downloadedSoundPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    if(isplayPauseAudioclicked)  {
                        mp.stop();
                        indexPostion++;
                        if(nextLine<audioPlayLineList.size()) {

                            nextLine = (Integer.parseInt(audioPlayLineList.get(indexPostion).LineID.substring(audioPlayLineList.get(indexPostion).LineID.lastIndexOf("-") + 1)));
//                            Log.e("nextLine......", nextLine + "");
                            listRead.setSelection(nextLine - mSubtractionCount);
                            if (audioPlayLineList.get(indexPostion).isSoundAvailable() == true) {
                                playUserAudio(audioPlayLineList.get(indexPostion), null, false, null);
                            } else {
                                downloadAndPlayRecordTextToSpeech(audioPlayLineList.get(indexPostion), null);
                            }
                        }
                    } else {
                        mp.stop();
                        if(imgPlay != null) {
                            imgPlay.setBackgroundResource(R.drawable.ic_recorded_voice);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void downloadAndPlayRecordTextToSpeech(final PlayLines playLine,final ImageView imgPlay){

        ArrayList<TextLines> arrTxt = playLine.textLinesList;

        StringBuffer text=new StringBuffer();
        if(arrTxt != null && arrTxt.size()>0){
            for(TextLines line : arrTxt){
                text.append(line.currentText());
                text.append(" ");
            }
            Log.e("text...",text+"");
            String textValue=text.toString();
//            textValue.replaceAll("/?", "QMQM");
            try {

                String xmlstring = "Здравей' хора";
                String utf8string = convertToUTF8(xmlstring);
                for (int i = 0; i < utf8string.length(); ++i) {
                    Log.e("new string", utf8string.charAt(i)+"");

                }

//                  textValue=new String(URLEncoder.encode(textValue, "UTF-8"));
//                textValue=new String(textValue.getBytes("UTF-8"));
//                textValue=new String(textValue.getBytes("ISO-8859-1"));
//                textValue= new String(textValue.toString().getBytes("ISO-8859-1"),"US-ASCII");
//                textValue= new String(textValue.toString().getBytes("US-ASCII"),"ISO-8859-1");
                textValue= new String(textValue.toString().getBytes("UTF-8"),"ISO-8859-1");

//                textValue=new String(textValue.getBytes("US-ASCII"));

            } catch (Exception e) {
                e.printStackTrace();
            }

//            textValue=textValue.replaceAll("/?", "");
//            textValue=textValue.replaceAll("QMQM", "?");

            Log.e("After decription value",textValue+"");
            final  JSONObject mainOBJ = new JSONObject();

            try {

                mainOBJ.put("type","jsonwsp/request");
                mainOBJ.put("version","1.0");
                mainOBJ.put("methodname","synthesize");

                // get s
                SharedPreferences preferences = getActivity().getSharedPreferences("session_id", getActivity().MODE_PRIVATE);
                String sessionId = preferences.getString("session_id","");
                JSONObject args = new JSONObject();
                args.put("session_id",sessionId);
                args.put("text",textValue);
                args.put("codec","mp3");
                args.put("sample_rate",0);
                mainOBJ.put("args",args); // add to main json

                new AsyncTask<Void,Void,Void>() {

                    String responseString = "";

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        if(!isplayPauseAudioclicked) {
                            dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                            dialog.title("Henter...");
                            dialog.show();
                        }
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {

                        try {

                            String service_url = "https://mvid-services.mv-nordic.com/theater-v1/AudioService/jsonwsp";
                            //
                            HostnameVerifier hostnameVerifier = org.apache.http.conn.ssl.SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
                            DefaultHttpClient client = new DefaultHttpClient();
                            SchemeRegistry registry = new SchemeRegistry();
                            SSLSocketFactory socketFactory = SSLSocketFactory.getSocketFactory();
                            socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);
                            registry.register(new Scheme("https", socketFactory, 443));
                            SingleClientConnManager mgr = new SingleClientConnManager(client.getParams(), registry);
                            DefaultHttpClient httpclient = new DefaultHttpClient(mgr, client.getParams());

// Set verifier
                            HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);

//                 HttpClient httpclient = new DefaultHttpClient();
                            //  httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
                            HttpPost httppost = new HttpPost(service_url);
                            String boundary = "--" + "62cd4a08872da000cf5892ad65f1ebe6";
                            httppost.setHeader("Content-type", "multipart/related; boundary=" + boundary);

                            HttpEntity entity = MultipartEntityBuilder.create()
                                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                                    .setBoundary(boundary)

                                    .setCharset(Charset.forName("UTF-8"))
                                    .addPart("body", new StringBody(mainOBJ.toString()))
                                    .build();

                            httppost.setEntity(entity);
                            try {
                                HttpResponse response = httpclient.execute(httppost);
                                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                                responseString = reader.readLine();

                            } catch (Exception e) {

                                e.printStackTrace();
                            }

                        }catch (Exception e){}

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        String url = "";
//                        Log.e("Response for retreiving song TTF ", "" + responseString.toString());
                        try{
                            JSONObject jsonObject = new JSONObject(responseString.toString());
                            JSONObject inerObj = jsonObject.getJSONObject("result");
                            url = inerObj.getString("url");

//                            Log.e("Inner URL ", "" + url.toString());

                        }catch(Exception e){}

                        downloadSpeechFile(url,playLine,imgPlay);

                    }
                }.execute();

            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    private void downloadSpeechFile(String url,final PlayLines playLines,final ImageView imgPlay) {

        final  String theURL ="https://mvid-services.mv-nordic.com/theater-v1/"+url;
//        Log.e("final url to be download ",""+theURL);

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                txtToSpeechfileDir = new File(Environment.getExternalStorageDirectory()+ "/danteater/speech");
                if(!txtToSpeechfileDir.exists()) {
                    txtToSpeechfileDir.mkdirs();
//                        Log.e("directory:","created");
                } else {
//                        Log.e("directory:","already exist");
                }
            }

            @Override
            protected Void doInBackground(Void... voids) {

                int count;
                try{

                    URL url = new URL(theURL);
                    java.io.BufferedInputStream in = new java.io.BufferedInputStream(new java.net.URL(theURL).openStream());
                    java.io.FileOutputStream fos = new java.io.FileOutputStream(new File(txtToSpeechfileDir.getAbsolutePath()+"/"+playLines.LineID+".mp3"));
                    java.io.BufferedOutputStream bout = new BufferedOutputStream(fos,1024);
                    byte[] data = new byte[1024];
                    int x=0;
                    while((x=in.read(data,0,1024))>=0){
                        bout.write(data,0,x);
                    }
                    fos.flush();
                    bout.flush();
                    fos.close();
                    bout.close();
                    in.close();
                }
                catch (Exception ex)
                {
                    ex.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if(!isplayPauseAudioclicked) {
                    if(dialog != null && dialog.isShowing()){
                        dialog.dismiss();
                    }

                }
                //TODO
                playTextToSpeechFile(playLines,imgPlay);
            }
        }.execute();

    }
    private void playTextToSpeechFile(PlayLines playLines,final ImageView imgPlay) {
        mTextToSpeechPlayer= new MediaPlayer();
        try {
            mTextToSpeechPlayer.setDataSource(Environment.getExternalStorageDirectory()+ "/danteater/speech/"+playLines.LineID+".mp3");
            mTextToSpeechPlayer.prepare();
            mTextToSpeechPlayer.start();
            mTextToSpeechPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                public void onCompletion(MediaPlayer mp) {
                    if(isplayPauseAudioclicked) {
                        mp.stop();
                        indexPostion++;
                        if(nextLine<audioPlayLineList.size()) {
                            nextLine = (Integer.parseInt(audioPlayLineList.get(indexPostion).LineID.substring(audioPlayLineList.get(indexPostion).LineID.lastIndexOf("-") + 1)));
//                            Log.e("nextLine......", nextLine + "");
                            listRead.setSelection(nextLine - mSubtractionCount);
                            if (audioPlayLineList.get(indexPostion).isSoundAvailable() == true) {
                                playUserAudio(audioPlayLineList.get(indexPostion), null, false, null);
                            } else {
                                downloadAndPlayRecordTextToSpeech(audioPlayLineList.get(indexPostion), null);
                            }
                        }
                    } else {
                        mp.stop();
                        if(imgPlay != null) {
                            imgPlay.setBackgroundResource(R.drawable.ic_play);
                        }
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();

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

    private void proceedMessage(PlayLines playLine) {

        if(currentUser.checkPupil(currentUser.getRoles())){
            ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "selected_playline", 0);
            complexPreferences.putObject("current_playline", playLine);
            complexPreferences.commit();

            Intent i=new Intent(getActivity(), SelectTeacherForChat.class);
            startActivity(i);

        }else{

            if (playLine.assignedUsersList.size() > 0) {

                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "selected_playline", 0);
                complexPreferences.putObject("current_playline", playLine);
                complexPreferences.commit();

                Intent i=new Intent(getActivity(), ChatViewFromRead.class);
                startActivity(i);

            } else {

                AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                alert.setTitle("Tildel rolle");
                alert.setMessage("Der er ikke tildelt nogle elever til denne rolle");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                    }
                });
                alert.show();

            }

        }
    }

    private void gotoAssignUserList(PlayLines playLine) {

//        Log.e("Size of shared people is ",""+_marrSharedWithUsers.size());
        boolean pupilsFound = false;

        //TODO
        for(int i=0;i<marrMyCastMatches.size();i++){
            if(marrMyCastMatches.get(i).contains(playLine.RoleName)){
                marrMyCastMatches.remove(playLine.RoleName);

            }
        }
        if(_marrSharedWithUsers == null || _marrSharedWithUsers.isEmpty() || _marrSharedWithUsers.size()<0){
            pupilsFound = false;

        }else{
            for(SharedUser au : _marrSharedWithUsers){

                String check = "lærer";

                if(au.userName != null && !au.userName.contains(check.toString())){
                    pupilsFound = true;
                }
            }

        }
        if(pupilsFound){

            showUsersAndAssignRole(playLine);

        }else{

            AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
            alert.setTitle("");
            alert.setMessage("Du kan ikke dele stykket med elever, hvis stykket ikke er bestilt til opførelse. Bestil stykket til opførelse først.");
            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });


            alert.show();

        }

    }

    private void showUsersAndAssignRole(final PlayLines playLine) {

        final ArrayList<SharedUser> alreadyAssignedUsers = new ArrayList<SharedUser>();

        if(playLine.assignedUsersList.size()>0){

            for(AssignedUsers user : playLine.assignedUsersList){
                SharedUser u = new SharedUser();
                u.userName = user.AssignedUserName;
                u.userId = user.AssignedUserId;
                alreadyAssignedUsers.add(u);
            }
        }

        ArrayList<String> arrSharedUsers = new ArrayList<String>();
        for(SharedUser au : _marrSharedWithUsers){
            arrSharedUsers.add(au.userName);

        }

        final Dialog dialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        LayoutInflater mInflater = (LayoutInflater) getActivity().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = mInflater.inflate(R.layout.item_assign_role_page,null);
        dialog.setContentView(view);
        dialog.show();

        final WMTextView btnAssignUserTildel = (WMTextView)view.findViewById(R.id.btnAssignUserTildel);
        final WMTextView txtBackAssignRole = (WMTextView)view.findViewById(R.id.txtBackAssignRole);
        txtBackAssignRole.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnAssignUserTildel.setTextColor(Color.parseColor("#313131"));
        btnAssignUserTildel.setEnabled(false);

        final ListView lstAssignRole = (ListView)view.findViewById(R.id.listAssignRoles);
        lstAssignRole.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        final ArrayAdapter adap = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_multiple_choice,arrSharedUsers);
        lstAssignRole.setAdapter(adap);

        for(int i=0;i<arrSharedUsers.size();i++){

            for(SharedUser su : alreadyAssignedUsers){

                if(su.userName.equalsIgnoreCase(arrSharedUsers.get(i))){
                    lstAssignRole.setItemChecked(i,true);
                }
            }
        }

        lstAssignRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(btnAssignUserTildel.isEnabled() == false){
                    btnAssignUserTildel.setEnabled(true);
                    btnAssignUserTildel.setTextColor(getResources().getColor(R.color.apptheme_color));
                }

            }
        });


        btnAssignUserTildel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                dialog.dismiss();
                alreadyAssignedUsers.clear();

                SparseBooleanArray boolarry = lstAssignRole.getCheckedItemPositions();

                for(int i=0;i<_marrSharedWithUsers.size();i++){

                    if(boolarry.get(i)){
                        alreadyAssignedUsers.add(_marrSharedWithUsers.get(i));
                    }

                }



                ArrayList<AssignedUsers> faul = new ArrayList<AssignedUsers>();

                for(SharedUser st : alreadyAssignedUsers){
//                    Log.e("name ",st.userName);

                    AssignedUsers u = new AssignedUsers();
                    u.AssignedUserName = st.userName;
                    u.AssignedUserId = st.userId;
                    faul.add(u);


                }


                playLine.assignedUsersList.clear();
                playLine.assignedUsersList.addAll(faul);
                callServiceForAssignedUserAdded(playLine,faul);


            }
        });

    }

    private void callServiceForAssignedUserAdded(PlayLines playLine,ArrayList<AssignedUsers> aus) {


        final JSONObject methodParams = new JSONObject();

        JSONArray arr = new JSONArray();

        for(AssignedUsers u : aus){

            JSONObject requestParams = new JSONObject();
            try {
                requestParams.put("AssignedUserName",u.AssignedUserName);
                requestParams.put("AssignedUserId",u.AssignedUserId);

                arr.put(requestParams);
            } catch (JSONException je) {
                je.printStackTrace();
            }

        }
        try {
            methodParams.put("LineCount",playLine.LineCount);
            methodParams.put("LineID",playLine.LineID);
            methodParams.put("AssignedUsers",arr);

        }catch (Exception e){

            e.printStackTrace();

        }

        assignRoleUsingMethodParams(methodParams.toString(), aus, playLine);

    }

    private void assignRoleUsingMethodParams(final String s, final ArrayList<AssignedUsers> aus, final PlayLines playLine) {

        //   Log.e("RRRRRROOOOLLLEEE ",playLine.castMatchesString);

        final HUD hud = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        hud.title("Rollen er tildelt");
        hud.show();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try

                {
                    readerForNone = API.callWebservicePost("http://api.danteater.dk/api/PlayUpdate", s);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hud.dismiss();
                try {
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
//                    Log.e("response---aus", response + " ");

                }catch (Exception e){
                    e.printStackTrace();
                }

                playLine.assignedUsersList = aus;

                for(PlayLines pl : selectedPlay.playLinesList){

                    String rn = pl.RoleName;

                    if(playLine.RoleName.equalsIgnoreCase(pl.RoleName)){
                        pl.assignedUsersList = aus;
                    }


//                    Log.e("----------CastMatches String",playLine.castMatchesString);

                    try {
                        for (String cm : playLine.castMatchesList) {
                            if (rn.equalsIgnoreCase(cm)) {
                                pl.assignedUsersList = aus;
                            }
                        }
                    } catch(NullPointerException ez){ez.printStackTrace();}

                }

                final ArrayList<String> myRoles = new ArrayList<String>();
                String currentUserId = currentUser.getUserId();

                for(PlayLines pl : selectedPlay.playLinesList){

                    if(pl.playLineType() == PlayLines.PlayLType.PlayLineTypeRole){

                        for(AssignedUsers au : pl.assignedUsersList){

                            if(au.AssignedUserId.equalsIgnoreCase(currentUserId)){

                                if(pl.RoleName != null && !pl.RoleName.equalsIgnoreCase("")){
                                    myRoles.add(pl.RoleName);
                                }
                            }
                        }

                    }else{
                        continue;
                    }
                }


                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
//                        for(int i=0;i<marrMyCastMatches.size();i++){
//                            Log.e("role ",marrMyCastMatches.get(i)+"");
//                            Log.e("role name",playLine.RoleName+"");
//                            if(marrMyCastMatches.get(i).contains(currentUser.getFirstName()+" "+currentUser.getLastName())){
//                                marrMyCastMatches.remove(playLine.RoleName);
//                            }
//                        }
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
//                        Log.e("Play ID before inserting assigned users ",""+selectedPlay.pID);
//                        Log.e("Play ID before inserting assigned users my roles",""+myRoles);
//                        dbh.openDataBase();
//                        marrMyCastMatches = dbh.getMyCastMatchesForRoleNames(myRoles,selectedPlay.pID);
//                        dbh.close();

                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);

                        for(int i=0;i<aus.size();i++){
                            if(aus.get(i).AssignedUserName.contains(currentUser.getFirstName()+" "+currentUser.getLastName())){
                                marrMyCastMatches.add(playLine.RoleName);
                            }
                        }

                        readSectionedAdapter.notifyDataSetChanged();
                    }
                }.execute();

            }
        }.execute();
    }

    private void updatePlayUsingMethodParams(final String s) {

        final HUD hud = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        hud.title("Gemmer ændringer");
        hud.show();

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try

                {
                    readerForNone = API.callWebservicePost("http://api.danteater.dk/api/PlayUpdate",s);

                } catch (Exception e) {
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                hud.dismiss();
                readSectionedAdapter.notifyDataSetChanged();

                try {
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
//                    Log.e("response", response + " ");

                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }.execute();

    }

    private void callServiceForCommentAdded(PlayLines playLine) {

        final JSONObject methodParams = new JSONObject();

        JSONArray arr = new JSONArray();

        for(Comments c : playLine.commentsList){

            String isP = c.isPrivate ? "True" : "False";

            JSONObject requestParams = new JSONObject();
            try {
                requestParams.put("UserName",c.userName);
                requestParams.put("CommentText",c.commentText);
                requestParams.put("Private",isP);

                arr.put(requestParams);
            } catch (JSONException je) {
                je.printStackTrace();
            }

        }
        try {
            methodParams.put("LineCount", playLine.LineCount);
            methodParams.put("LineID",playLine.LineID);
            methodParams.put("Comments",arr);

        }catch (Exception e){

            e.printStackTrace();

        }
        updatePlayUsingMethodParams(methodParams.toString());
    }

    public static String convertToUTF8(String s) {
        String out = null;
        try {
            out = new String(s.getBytes("UTF-8"), "ISO-8859-1");
        } catch (java.io.UnsupportedEncodingException e) {
            return null;
        }
        return out;
    }
}
