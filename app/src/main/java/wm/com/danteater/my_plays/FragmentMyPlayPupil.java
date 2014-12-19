package wm.com.danteater.my_plays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wm.com.danteater.Messages.MessageUnread;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.PlayOrderDetails;
import wm.com.danteater.R;
import wm.com.danteater.app.PlayTabActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.model.StateManager;

/**
 * Created by nirav on 16-09-2014.
 */
public class FragmentMyPlayPupil extends Fragment {

    private ArrayList<MessageUnread> messageUnreadArrayList=new ArrayList<MessageUnread>();
    MessageUnread messageUnread=new MessageUnread();
    public String badgeValue;
    private Menu menu;

    public static int STATE_RECORD = 0;
    public static int STATE_PREVIEW = 1;
    public static int STATE_READ = 2;
    public static int STATE_CHAT = 3;

    Play ply;
    int plyIDAfterUpdate = 0; // hack
    private HUD dialog;
    private ListView listPlay;
    private User user;
    private ArrayList<Play> playList;
    private HUD dialog_next;

    private ArrayList<Play> playListForPerform = new ArrayList<Play>();
    private ArrayList<PlayOrderDetails> playOrderList = new ArrayList<PlayOrderDetails>();
    private StateManager state = StateManager.getInstance();

    private enum ACTIVITY_TYPE{
        TAB_ACTIVITY,ORDER_ACTIVITY
    }

    public static FragmentMyPlayPupil newInstance(String param1, String param2) {
        FragmentMyPlayPupil fragment = new FragmentMyPlayPupil();

        return fragment;
    }

    public FragmentMyPlayPupil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getAllUnreadMessagesForUser();
            }
        }, 0, 1000 * 60*10);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_my_play_pupil, container, false);
        listPlay = (ListView) convertView.findViewById(R.id.listPlayForPupil);
        return convertView;
    }

    private void getAllUnreadMessagesForUser() {

        new CallWebService("http://api.danteater.dk/api/MessageUnread/"+user.getUserId(), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                Type listType=new TypeToken<List<MessageUnread>>(){
                }.getType();
                if(response !=null) {
//                    Log.e("refresh","badge value refresh");

                    messageUnreadArrayList = new GsonBuilder().create().fromJson(response, listType);
                    if (getActivity() != null) {
                        SharedPreferences preferencesBadgeValue = getActivity().getSharedPreferences("badge_value", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferencesBadgeValue.edit();
                        if (messageUnreadArrayList.size() > 0) {
                            messageUnread = messageUnreadArrayList.get(messageUnreadArrayList.size() - 1);
                            editor.putString("badge_count", messageUnread.unreadMessageCount.toString().trim());
                            editor.commit();
                        } else {
                            editor.putString("badge_count", "0");
                            editor.commit();
                        }

                        badgeValue = preferencesBadgeValue.getString("badge_count", "0");
                        if (badgeValue.equalsIgnoreCase("0")) {
                            setHasOptionsMenu(false);
                        } else {
                            setHasOptionsMenu(true);
                            if (menu != null) {
                                View count = menu.findItem(R.id.badge).getActionView();
                                count.setVisibility(View.VISIBLE);
                                TextView notifCount = (TextView) count.findViewById(R.id.notif_count);
                                notifCount.setText(String.valueOf(badgeValue));
                            }
                        }


                    }
                }
            }

            @Override
            public void error(VolleyError error) {

                Log.e("error: ",error+"");

            }
        }.start();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu=menu;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // show loading
        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Mine stykker");
        dialog.show();

        new CallWebService("http://api.danteater.dk/api/MyPlays?UserId=" + user.getUserId(), CallWebService.TYPE_JSONARRAY) {
            @Override
            public void response(String response) {
                dialog.dismiss();
                Log.e("json response", response + "");
                handleDataAfterResponseVolly(response);
            }

            @Override
            public void error(VolleyError error) {
                dialog.dismissWithStatus(R.drawable.ic_navigation_cancel, "Error");
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }.start();
    }


    public void handleDataAfterResponseVolly(final String response) {


        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                Type listType = new TypeToken<List<Play>>() {

                }.getType();

                playList = new GsonBuilder().create().fromJson(
                        response, listType);


                //testing response
                for (int i = 0; i < playList.size(); i++) {

                    Play bean = playList.get(i);

                    if (bean.OrderType.equalsIgnoreCase("Perform")) {

                        playListForPerform.add(bean);
                        playOrderList.add(bean.playOrderDetails);

                    }

                }
//                Log.i("first auther", playListForPerform.get(0).getAuthor() + "");


                for (int i = 0; i < playOrderList.size(); i++) {
                    if (playOrderList.get(i) != null) {
                        PlayOrderDetails bean = playOrderList.get(i);
//                        Log.i("PlayOrderId", bean.PlayOrderId + "");
                    }
                }
//                Log.e("adapter", "before adapter call");
//                Log.e("adapter size", playList.size()+"");

                if(playList.size()==0){
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    FragmentPupilNoPlay fragmentMyPlayPupilNoPlay = FragmentPupilNoPlay.newInstance("", "");

                    if (manager.findFragmentByTag("my_plays_pupil_no_play") == null) {
                        ft.replace(R.id.main_content, fragmentMyPlayPupilNoPlay, "my_plays_pupil_no_play").commit();
                    }
                    listPlay.setVisibility(View.GONE);
                } else {
                    listPlay.setVisibility(View.VISIBLE);
                    listPlay.setAdapter(new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList));
//                    Log.e("adapter", "after adapter call");
                }

            }
        });

    }


    // My play order adapter for pupil
    public class ListPlayAdapterForPerform extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<Play> playListPupil;
        ArrayList<PlayOrderDetails> playOrderDetailListPupil;

        public ListPlayAdapterForPerform(Context context, ArrayList<Play> playList, ArrayList<PlayOrderDetails> playOrderDetailList) {
//            Log.e("adapter", "inside adapter");
            this.context = context;
            this.playListPupil = playList;
            this.playOrderDetailListPupil = playOrderDetailList;
        }

        public int getCount() {
//            Log.e("size", playListPupil.size() + "");
            return playListPupil.size();
        }

        public Object getItem(int position) {
            return playListPupil.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {

            WMTextView txtTitle, txtAuther, txtDuration1, txtDuration2, txtNumberOfPerformance;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {
//            Log.e("get view", "inside getview");
            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_my_play_order_pupil, parent, false);
                holder = new ViewHolder();
                holder.txtTitle = (WMTextView) convertView.findViewById(R.id.txtOrderTitlePupil);
                holder.txtAuther = (WMTextView) convertView.findViewById(R.id.txtOrderAutherPupil);
                holder.txtDuration1 = (WMTextView) convertView.findViewById(R.id.txtOrderDuration1Pupil);
                holder.txtDuration2 = (WMTextView) convertView.findViewById(R.id.txtOrderDuration2Pupil);
                holder.txtNumberOfPerformance = (WMTextView) convertView.findViewById(R.id.txtOrderNumberOfPerformancePupil);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtTitle.setText(playListPupil.get(position).Title);

            if (playOrderDetailListPupil.get(position).NumberOfAuditions.equalsIgnoreCase("True")) {
                holder.txtAuther.setText("Generalprøve: Ja");
            } else {
                holder.txtAuther.setText("Generalprøve: Nej");
            }

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            float performDateFirst = Float.parseFloat(playOrderDetailListPupil.get(position).PerformDateFirst);
            float performDateLast = Float.parseFloat(playOrderDetailListPupil.get(position).PerformDateLast);
            Date firstDate = float2Date(performDateFirst);
            Date lastDate = float2Date(performDateLast);

            holder.txtDuration1.setText("Første opførelse: " + format.format(firstDate));
            holder.txtDuration2.setText("Sidste opførelse: " + format.format(lastDate));
            holder.txtNumberOfPerformance.setText("Antal opførelser: " + playOrderDetailListPupil.get(position).NumberOfPerformances);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog_next = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    dialog_next.title("Henter"+"\n"+"skuespil");
                    dialog_next.show();
                    //   gotoTabActivity(position, "Read");
                    gotoSpecificPage(position,0,playListForPerform.get(position),ACTIVITY_TYPE.TAB_ACTIVITY);
                }
            });

//            Log.e("get view", "end of getview");
            return convertView;


        }

        private void gotoSpecificPage(final int play_index,int tab_index, final Play play, final ACTIVITY_TYPE act_type){


            DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
            boolean hasPlay = dbh.hasPlayWithPlayOrderIdText(play.OrderId);
            dbh.close();


            if(hasPlay == true){

//                Log.i("hasplay","true");
//                Log.i("Order Id",play.OrderId);
                SharedPreferences preferences = getActivity().getSharedPreferences("Plays", getActivity().MODE_PRIVATE);
                String k = "PlayLatesteUpdateDate"+play.PlayId;
                //  Toast.makeText(getActivity(),preferences.getString(k,""), Toast.LENGTH_SHORT).show();
                long unixTime = Long.parseLong(preferences.getString(k,""));
                BigDecimal bigDecimal = new BigDecimal(unixTime);

                String serverLink = API.link_getPlayUpdateForPlayOrderIdString+play.PlayId+"?unixTimeStamp="+bigDecimal;
                new CallWebService(serverLink,CallWebService.TYPE_JSONARRAY) {

                    @Override
                    public void response(final String response) {

//                        Log.i("Response update play : ",""+response);

                        if(response == null || response.equalsIgnoreCase("")){

                        }else{

                            new AsyncTask<String,Integer,String>(){

                                @Override
                                protected String doInBackground(String... params) {

                                    try {
                                        JSONArray arr = new JSONArray(response);

                                        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
                                        plyIDAfterUpdate = dbh.getPlayIdFromDBForOrderId(play.OrderId);
                                        state.playID = plyIDAfterUpdate;
                                        dbh.close();

                                        for(int i=0;i<arr.length();i++){

                                            JSONObject jsonObject = arr.getJSONObject(i);
                                            PlayLines playLine = new GsonBuilder().create().fromJson(jsonObject.toString(),PlayLines.class);

                                            DatabaseWrapper dbWrap = new DatabaseWrapper(getActivity());
                                            dbWrap.updatePlayLine(playLine,Integer.parseInt(play.PlayId));
                                            dbWrap.close();


                                        }
                                    }catch(Exception e){
                                        e.printStackTrace();
                                    }


                                    return null;
                                }

                                @Override
                                protected void onPostExecute(String s) {
                                    super.onPostExecute(s);
                                    dialog_next.dismiss();

                                    SharedPreferences preferences = getActivity().getSharedPreferences("Plays",getActivity().MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    String k = "PlayLatesteUpdateDate"+play.PlayId;
                                    editor.putString(k,""+System.currentTimeMillis());
                                    editor.commit();
                                    gotoNextPage(act_type,play_index);
                                }

                            }.execute();

                        }

                    }

                    @Override
                    public void error(VolleyError error) {

                        dialog_next.dismiss();
                        Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }.start();

            }else{
//                Log.i("hasplay","false");
                // insert new play to db

                new CallWebService(API.link_retrievePlayContentsForPlayOrderId +play.OrderId,CallWebService.TYPE_JSONOBJECT) {

                    @Override
                    public void response(final String response) {

//                        Log.i("Response full play : ",""+response);
                        new AsyncTask<String,Integer,String>(){

                            @Override
                            protected String doInBackground(String... params) {

                                Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                                DatabaseWrapper db = new DatabaseWrapper(getActivity());
                                db.insertPlay(receivedPlay, false);
                                db.close();

                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                dialog_next.dismiss();

                                SharedPreferences preferences = getActivity().getSharedPreferences("Plays",getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                String k = "PlayLatesteUpdateDate"+play.PlayId;
                                editor.putString(k,""+System.currentTimeMillis());
                                editor.commit();

                                gotoNextPage(act_type,play_index);

                            }
                        }.execute();

                    }

                    @Override
                    public void error(VolleyError error) {
                        dialog_next.dismiss();

                        Toast.makeText(getActivity(),error.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }.start();

            }

        }

        private void gotoNextPage(final ACTIVITY_TYPE act_type,int position) {

            new AsyncTask<String,Integer,String>(){

                @Override
                protected String doInBackground(String... params) {

                    DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
                    ply = dbh.retrievePlayWithId(state.playID);
                    dbh.close();

                    return null;
                }

                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",ply);
                    complexPreferences.commit();

                    switch (act_type){

                        case ORDER_ACTIVITY:


                            Intent i1 = new Intent(getActivity(), ReadActivityFromPreview.class);
                            i1.putExtra("currentState",STATE_PREVIEW);
                            startActivity(i1);

                            break;

                        case TAB_ACTIVITY:

                            Intent i = new Intent(getActivity(), PlayTabActivity.class);
                            startActivity(i);

                            break;
                    }

                }
            }.execute();


        }
    }

    private void gotoTabActivity(int position, String type) {

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        complexPreferences.putObject("selected_play", playListForPerform.get(position));
        complexPreferences.commit();


        Intent i = new Intent(getActivity(), PlayTabActivity.class);
        i.putExtra("infoData", playList.get(position).Synopsis + "");
        i.putExtra("type_navigation", type);
        startActivity(i);


    }


    /**
     * @param nbSeconds
     * @return
     */

    public static java.util.Date float2Date(float nbSeconds) {
        java.util.Date date_origine;
        java.util.Calendar date = java.util.Calendar.getInstance();
        java.util.Calendar origine = java.util.Calendar.getInstance();
        origine.set(1970, Calendar.JANUARY, 1);
        date_origine = origine.getTime();
        date.setTime(date_origine);
        date.add(java.util.Calendar.SECOND, (int) nbSeconds);
        return date.getTime();
    }
}
