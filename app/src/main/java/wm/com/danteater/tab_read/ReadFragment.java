package wm.com.danteater.tab_read;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;


import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.Comments;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.TextLines;
import wm.com.danteater.R;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.PinnedHeaderListView;
import wm.com.danteater.customviews.SectionedBaseAdapter;
import wm.com.danteater.customviews.WMEdittext;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.APIDelete;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.model.StateManager;


public class ReadFragment extends Fragment {

    public ArrayList marrMyCastMatches;
    public ReadSectionedAdapter readSectionedAdapter;
    Reader readerForNone;
    public int goToLineNumberFromChatLink = 0;
    public PinnedHeaderListView listRead;
    private Play selectedPlay;
    private ArrayList<PlayLines> playLinesesList;
    private ArrayList<AssignedUsers> assignedUsersesList = new ArrayList<AssignedUsers>();
    private HUD dialog;
    private View layout_gotoLine;
    private boolean isGoToLineVisible = false;
    private Menu menu;
    private User currentUser;

    // 0 for record state
    // 1 for preview state
    // 2 for chat state
    // 3 for read

    public static int STATE_RECORD = 0;
    public static int STATE_PREVIEW = 1;
    public static int STATE_READ = 2;
    public static int STATE_CHAT = 3;

    public int currentState = 0;

    public boolean isPreview = false;
    private StateManager stateManager = StateManager.getInstance();

    public ArrayList<AssignedUsers> _marrSharedWithUsers;
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

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        currentUser = complexPreferences.getObject("current_user", User.class);

         // setup actionbar methods
         ((WMTextView) getActivity().getActionBar().getCustomView()).setText(selectedPlay.Title);
         currentState = getArguments().getInt("currentState");

        if(currentState == STATE_READ || currentState == STATE_RECORD) {
            setHasOptionsMenu(true);
        }

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        ((WMTextView)getActivity().getActionBar().getCustomView()).setGravity(Gravity.LEFT);

   /*     // setup init for read
        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        marrMyCastMatches = dbh.getMyCastMatchesForUserId(currentUser.getUserId(),Integer.parseInt(selectedPlay.PlayId));
        dbh.close();*/

        Log.e("marrMyCastMatches   -- ",""+marrMyCastMatches);

        if(!isPreview){

            //TODO fetch sharing details

            new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

                @Override
                public void response(String response) {

                    Type listType = new TypeToken<List<AssignedUsers>>() {
                    }.getType();
                    _marrSharedWithUsers = new GsonBuilder().create().fromJson(response,listType);
                    Log.e("_maarSharedWithUsers ",""+_marrSharedWithUsers);
                }

                @Override
                public void error(VolleyError error) {
                Log.e("error: ",error+"");

                }
            }.start();

        }

    }

    public void updatePlaySpecificData() {

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

        // TODO implement dictionaries for y positions if nessasary

        if(_marrSharedWithUsers == null){
            _marrSharedWithUsers = new ArrayList<AssignedUsers>();
        }else{
            _marrSharedWithUsers.clear();
        }

        // TODO implement dictionary for picture

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
                if(!foundIndexOfFirstScene){

                    if(!currentKey.contains("første") ||
                            !currentKey.contains("1") ||
                            !currentKey.contains("scene") ||
                            !currentKey.contains("akt") ){

                        indexForFirstScene = marrPlaySections.size();
                        foundIndexOfFirstScene = true;
                    }
                }


            }else{

                dicPlayLines.get(currentKey).add(playLine);

                if(playLine.playLineType() == PlayLines.PlayLType.PlayLineTypeSong){

                    int section = marrPlaySections.indexOf(currentKey);
                    int row = dicPlayLines.get(currentKey).size();

                    String songTitle = playLine.textLinesList.get(0).LineText;
                    mdictSongIndexPaths.put(songTitle,section+","+row); // hack for similar to indexPath in iOS
                    // seperated by ","
                    // First object is section and second is row
                }

            }

        }

        if(currentState == STATE_RECORD){

            ArrayList<String> toDelete = new ArrayList<String>();

            for(String section : marrPlaySections){

                if(section.contains("Personerne") || section.contains("Personer")){

                    toDelete.add(section);
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

        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        marrMyCastMatches = dbh.getMyCastMatchesForUserId(currentUser.getUserId(),selectedPlay.pID);
        dbh.close();

       // Log.e("------------------ mycast read:",""+marrMyCastMatches);

       System.out.println("-------------   Sections : "+marrPlaySections);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_read, container, false);
        layout_gotoLine = (View)convertView.findViewById(R.id.layout_item_goto_line);
        listRead = (PinnedHeaderListView)convertView.findViewById(R.id.listViewRead);
        listRead.setFastScrollEnabled(true);

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

        txtGotoLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String lineNo = edGotoLine.getText().toString();
                if(lineNo == null || lineNo.equalsIgnoreCase("")){

                }else{

                    final int gotoL = 0;

                    try {
/*                        new AsyncTask<Void,Void,Void>(){

                            @Override
                            protected Void doInBackground(Void... voids) {

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                            }
                        }.execute();*/

                        listRead.setSelection(Integer.parseInt(edGotoLine.getText().toString())-1);

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


            }
        }.execute();

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

                getActivity().finish();

                break;


            case R.id.action_line_number:

                menu.getItem(0).setEnabled(false);
                enableDisableLineLayout();




                break;
        }

        return super.onOptionsItemSelected(item);
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
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public long getItemId(int section, int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public int getItemViewTypeCount() {
            return 14;
        }

        @Override
        public int getItemViewType(int section, int position) {

           PlayLines playLine = dicPlayLines.get(marrPlaySections.get(section)).get(position);

          Log.e("GetItemViewType  :  Type : ","pos : "+position+" "+playLine.playLineType());

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

                if(currentState == STATE_RECORD){

                    //todo here chat state condition

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

                    holderReadPlayRoleCell.cellReadPlayRole.setupForPlayLine(playLine,currentState,convertView,isEmpty);
                    holderReadPlayRoleCell.cellReadPlayRole.setAssignClicked(new setOnAssignButtonClicked() {
                        @Override
                        public void onAssignButtonClicked() {

                            Toast.makeText(getActivity(), "Assign clicked", Toast.LENGTH_SHORT).show();

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
                    holderRecordPlayPlayLineCell.cellRecordPlayPlayLine.setupForPlayLine(playLine,currentState);


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

                    boolean mark = false;

                    for(int i=0;i<marrMyCastMatches.size();i++){
                        String sCheck = marrMyCastMatches.get(i).toString();
                        if(sCheck.equalsIgnoreCase(playLine.RoleName)){
                            mark = true;

                        }
                    }



                    holderReadPlayPlayLineCell.cellReadPlayPlayLine.setupForPlayLine(playLine,currentState,mark);

                    holderReadPlayPlayLineCell.cellReadPlayPlayLine.setOnTextLineUpdated(new CellReadPlayPlayLine.OnTextLineUpdated() {

                        // delegate method called after textline changes
                        @Override
                        public void onTextLineUpdated(String newText) {
                            Log.e("TextLine ",""+section+":"+position);

                           playLine.textLinesList.get(0).alteredLineText = newText;
                           playLine.textLinesList.get(0).LineText = "";
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
                    holderReadPlayNoteCell.cellReadPlayNote.setupForPlayLine(playLine,currentState);

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
                    holderReadPlayInfoCell.cellReadPlayInfo.setupForPlayLine(playLine,currentState);

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
                    holderReadPlaySongCell.cellReadPlaySong.setUpForPlayLine(playLine);
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
                methodParams.put("LineCount", Integer.parseInt(playLine.LineCount));
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
            if (convertView == null) {
                LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                layout = (LinearLayout) inflator.inflate(R.layout.item_read_play_section_view, null);
            } else {
                layout = (LinearLayout) convertView;
            }
           ((TextView) layout.findViewById(R.id.readPlaySectionName)).setText(marrPlaySections.get(section));
            return layout;
        }

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
                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
                    Log.e("response", response + " ");

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
            methodParams.put("LineCount", Integer.parseInt(playLine.LineCount));
            methodParams.put("LineID",playLine.LineID);
            methodParams.put("Comments",arr);

        }catch (Exception e){

            e.printStackTrace();

        }

        updatePlayUsingMethodParams(methodParams.toString());



    }


}
