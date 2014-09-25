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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.PinnedHeaderListView;
import wm.com.danteater.customviews.SectionedBaseAdapter;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.model.StateManager;


public class ReadFragment extends Fragment {


// LayoutInflater.from(context).inflate(R.layout.okcancelbar, this, true);
    public PinnedHeaderListView listRead;
    private Play selectedPlay;
    private ArrayList<PlayLines> playLinesesList;
    private ArrayList<AssignedUsers> assignedUsersesList = new ArrayList<AssignedUsers>();
    private HUD dialog;
    private View layout_gotoLine;
    private boolean isGoToLineVisible = false;
    private Menu menu;
    private User currentUser;
    //

    public boolean recordState = false;
    public boolean previewState = false;
    public boolean chatState = false;

    public boolean isPreview = false;
    private StateManager stateManager = StateManager.getInstance();

    public ArrayList<AssignedUsers> _marrSharedWithUsers;
    public ArrayList<String> marrPlayLines;
    public ArrayList<String> marrPlaySections;
    public HashMap<String,ArrayList<PlayLines>> dicPlayLines;

    public boolean foundIndexOfFirstScene = false;
    int indexForFirstScene = 0;



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
         setHasOptionsMenu(true);
         getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
         ((WMTextView)getActivity().getActionBar().getCustomView()).setGravity(Gravity.LEFT);
        // setup init for read

        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        dbh.getMyCastMatchesForUserId(currentUser.getUserId(),Integer.parseInt(selectedPlay.PlayId));
        dbh.close();


        if(!isPreview){

            //TODO fetch sharing details

            new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

                @Override
                public void response(String response) {

                    Type listType = new TypeToken<List<AssignedUsers>>() {
                    }.getType();
                    _marrSharedWithUsers = new GsonBuilder().create().fromJson(response,listType);
                }

                @Override
                public void error(VolleyError error) {


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

        if(dicPlayLines == null){
            dicPlayLines = new HashMap<String,ArrayList<PlayLines>>();
        }else{
            dicPlayLines.clear();
        }



        // TODO implement above methods for other parts of the play

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

            }

        }

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

        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();

        new AsyncTask<String,Integer,String>(){

            @Override
            protected String doInBackground(String... params) {

               updatePlaySpecificData();
                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);


                for(int i = 0 ; i<marrPlaySections.size(); i++){

                    Log.i(marrPlaySections.get(i)," count is : "+dicPlayLines.get(marrPlaySections.get(i)).size());


                }


                listRead.setAdapter(new ReadSectionedAdapter());

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

                int static_height = 0;

                menu.getItem(0).setEnabled(false);

                if(isGoToLineVisible == true){

                    static_height = (int)(layout_gotoLine.getY() - layout_gotoLine.getHeight());
                    start_onoff(static_height);

                }else{
                    static_height = (int)(layout_gotoLine.getY() + layout_gotoLine.getHeight());
                    start_onoff(static_height);

                }


                break;
        }

        return super.onOptionsItemSelected(item);
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
        public int getSectionCount() {
            return marrPlaySections.size();
        }

        @Override
        public int getCountForSection(int section) {
            return dicPlayLines.get(marrPlaySections.get(section)).size();
        }

        @Override
        public View getItemView(int section, int position, View convertView, ViewGroup parent) {


            LinearLayout layout = null;

            LinearLayout layoutPlayLineTypeRole = null;
            LinearLayout layoutPlayLineTypeLine = null;
            LinearLayout layoutPlayLineTypeNote = null;
            LinearLayout layoutPlayLineTypeInfo = null;
            LinearLayout layoutPlayLineTypePicutre = null;
            LinearLayout layoutPlayLineTypeSong = null;
            LinearLayout layoutPlayLineTypeSongLine = null;


            LayoutInflater inflator = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            PlayLines playLine = dicPlayLines.get(marrPlaySections.get(section)).get(position);
         //   Log.e("PlayLineType :","   Position : "+position+" : "+playLine.playLineType());


            switch (playLine.playLineType()){

                case PlayLineTypeRole:

                    if (layoutPlayLineTypeRole == null) {
                        layout =  (LinearLayout)inflator.inflate(R.layout.item_read_play_role_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }


                    break;

                case PlayLineTypeLine:

                    if (layoutPlayLineTypeLine == null) {
                        layout = (LinearLayout)inflator.inflate(R.layout.item_read_play_line_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }



                    break;

                case PlayLineTypeNote:

                    if (layoutPlayLineTypeNote == null) {

                        layout =  (LinearLayout)inflator.inflate(R.layout.item_read_play_note_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }



                    break;

                case PlayLineTypeInfo:

                    if (layoutPlayLineTypeInfo == null) {
                        layout = (LinearLayout)inflator.inflate(R.layout.item_read_play_info_cell,  parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }



                    break;

                case PlayLineTypePicutre:

                    if (layoutPlayLineTypePicutre == null) {
                        layout = (LinearLayout)inflator.inflate(R.layout.item_read_play_picture_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }



                    break;

                case PlayLineTypeSong:

                    if (layoutPlayLineTypeSong == null) {
                        layout = (LinearLayout)inflator.inflate(R.layout.item_read_play_song_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }



                    break;

                case PlayLineTypeSongLine:
                case PlayLineTypeSongLineVerse:

                    if (layoutPlayLineTypeSongLine == null) {
                        layout =  (LinearLayout)inflator.inflate(R.layout.item_read_play_song_line_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }

                    break;

                default:

                    if (layoutPlayLineTypeSongLine == null) {
                        layout  =  (LinearLayout)inflator.inflate(R.layout.item_read_play_song_line_cell, parent,false);
                    } else {
                        layout =  (LinearLayout)convertView;
                    }

                    break;
            }
            return layout;
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



}
