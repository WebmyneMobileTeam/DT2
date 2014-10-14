package wm.com.danteater.my_plays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import wm.com.danteater.Messages.MessageUnread;
import wm.com.danteater.Messages.MessagesForConversation;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.Play.PlayOrderDetails;
import wm.com.danteater.R;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.app.PlayTabActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.SegmentedGroup;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.BeanGroupInfo;
import wm.com.danteater.login.BeanGroupMemberInfo;
import wm.com.danteater.login.BeanGroupMemberResult;
import wm.com.danteater.login.BeanGroupResult;
import wm.com.danteater.login.Group;
import wm.com.danteater.login.GroupMembers;
import wm.com.danteater.login.LoginActivity;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.APIDelete;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.model.Prefs;
import wm.com.danteater.model.StateManager;
import wm.com.danteater.search.FragmentSearch;
import wm.com.danteater.tab_share.ShareFragment;


public class FragmentMyPlay extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private ArrayList<MessageUnread> messageUnreadArrayList=new ArrayList<MessageUnread>();
    MessageUnread messageUnread=new MessageUnread();
    public String badgeValue;
    private Menu menu;
    int playid = 0;
    //(int) (System.currentTimeMillis() / 1000L);
    public static int STATE_RECORD = 0;
    public static int STATE_PREVIEW = 1;
    public static int STATE_READ = 2;
    public static int STATE_CHAT = 3;
    //    private StateManager stateManager = StateManager.getInstance();
    Play ply;
    User cUser;
    String session_id;
    private Play selectedPlay;
    public  boolean finishedRetrievingTeachers = false;
    public  int numberOfClassesToBeRetrieved = 0;
    public   Group groupForTeacher = new Group();
    private enum ACTIVITY_TYPE{
        TAB_ACTIVITY,ORDER_ACTIVITY
    }

    int plyIDAfterUpdate = 0; // hack
    private HUD dialog;
    private HUD dialogForShare;
    private HUD dialog_next;
    private SegmentedGroup segmentedGroupPlays;
    private RadioButton rbBestilte;
    private RadioButton rbGennemsyn;
    private ListView listPlay;
    private ListPlayAdapterForReview listPlayAdapterForReview;
    private ListPlayAdapterForPerform listPlayAdapterForPerform;
    private ArrayList<Play> playList;
    private ArrayList<Play> playListForReview = new ArrayList<Play>();
    private ArrayList<Play> playListForPerform = new ArrayList<Play>();
    private ArrayList<PlayOrderDetails> playOrderList = new ArrayList<PlayOrderDetails>();
    private User currentUser;
    //private Play playSelectedToBeDeleted;
    private Play playSelectedToBeDeletedForReview;
    private Play playSelectedToBeDeletedForPerform;
    private StateManager state = StateManager.getInstance();
    String responseValue;

    public static FragmentMyPlay newInstance(String param1, String param2) {
        FragmentMyPlay fragment = new FragmentMyPlay();

        return fragment;
    }

    public FragmentMyPlay() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser =complexPreferences.getObject("current_user", User.class);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getAllUnreadMessagesForUser();
            }
        }, 0, 1000 * 60 *10);
    }

    private void getAllUnreadMessagesForUser() {
        Log.e("refresh","badge value refresh");
        new CallWebService("http://api.danteater.dk/api/MessageUnread/"+currentUser.getUserId(), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                Type listType=new TypeToken<List<MessageUnread>>(){
                }.getType();
                if(response !=null) {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_my_play, container, false);

        segmentedGroupPlays = (SegmentedGroup) convertView.findViewById(R.id.segmentedMyPlays);
        rbBestilte = (RadioButton) convertView.findViewById(R.id.rbBestilte);
        rbGennemsyn = (RadioButton) convertView.findViewById(R.id.rbGennemsyn);
        segmentedGroupPlays.setOnCheckedChangeListener(this);
        listPlay = (ListView) convertView.findViewById(R.id.listPlays);

        segmentedGroupPlays = (SegmentedGroup)convertView.findViewById(R.id.segmentedMyPlays);
        rbBestilte = (RadioButton)convertView.findViewById(R.id.rbBestilte);
        rbGennemsyn = (RadioButton)convertView.findViewById(R.id.rbGennemsyn);
        segmentedGroupPlays.setOnCheckedChangeListener(this);

        listPlay=(ListView)convertView.findViewById(R.id.listPlays);

//        listPlay.setAdapter(new ListPlayAdapter(getActivity(), titleList, imageList));
        return convertView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // TODO : chat API helper for counting unread messages


        // show loading

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("Mine Stykker");
                dialog.show();
            }

            @Override

            protected Void doInBackground(Void... voids) {
                new CallWebService("http://api.danteater.dk/api/MyPlays?UserId="+ currentUser.getUserId(), CallWebService.TYPE_JSONARRAY) {
                    @Override
                    public void response(String response) {
                        responseValue=response;
                        dialog.dismiss();
                        handledataafterresponseVolly(responseValue);
                    }
                    @Override
                    public void error(VolleyError error) {
                        dialog.dismissWithStatus(R.drawable.ic_navigation_cancel, "Error");
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }.start();

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
//                dialog.dismiss();
            }
        }.execute();
    }

    public void handledataafterresponseVolly(final String response) {


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

                    if (bean.OrderType.equalsIgnoreCase("Review")) {
                        playListForReview.add(bean);
                    }
                    if (bean.OrderType.equalsIgnoreCase("Perform")) {

                        playListForPerform.add(bean);
                        playOrderList.add(bean.playOrderDetails);
                        //TODO
                    }

                }

                for (int i = 0; i < playOrderList.size(); i++) {
                    if (playOrderList.get(i) != null) {
                        PlayOrderDetails bean = playOrderList.get(i);
                        Log.i("PlayOrderId", bean.PlayOrderId + "");
                    }
                }
                if(playListForPerform.size()==0 && playListForReview.size()==0){
                    FragmentManager manager = getActivity().getSupportFragmentManager();
                    FragmentTransaction ft = manager.beginTransaction();
                    FragmentSearch fragmentSearch = FragmentSearch.newInstance("", "");
                    if (manager.findFragmentByTag("search") == null) {
                        ft.replace(R.id.main_content, fragmentSearch, "search").commit();
                    }
                    ((WMTextView) getActivity().getActionBar().getCustomView()).setText("Søg skuespil");
                } else if(playListForPerform.size()==0 && playListForReview.size()!=0) {
                    rbGennemsyn.setChecked(true);
                    listPlayAdapterForReview=new ListPlayAdapterForReview(getActivity(), playListForReview);
                    listPlay.setAdapter(listPlayAdapterForReview);
                } else if(playListForPerform.size()!=0 && playListForReview.size()==0) {
                    rbBestilte.setChecked(true);
                    listPlayAdapterForPerform=   new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList);
                    listPlay.setAdapter(listPlayAdapterForPerform);
                } else {
                    rbBestilte.setChecked(true);
                    listPlayAdapterForPerform=   new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList);
                    listPlay.setAdapter(listPlayAdapterForPerform);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume",".........................");
//        ShareActivityForPreview.teachers.clear();
//        ShareActivityForPerform.classes.clear();
//        ShareActivityForPerform.teachers.clear();
//        ShareActivityForPerform.pupils.clear();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rbBestilte:
                listPlay.setAdapter(null);
                listPlayAdapterForPerform=new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList);
                listPlay.setAdapter(listPlayAdapterForPerform);
                break;

            case R.id.rbGennemsyn:
                listPlay.setAdapter(null);
                listPlayAdapterForReview=new ListPlayAdapterForReview(getActivity(), playListForReview);
                listPlay.setAdapter(listPlayAdapterForReview);
                break;
        }
    }

    // For Preview Tab
    public class ListPlayAdapterForReview extends BaseAdapter {
        Context context;
        LayoutInflater inflater;
        ArrayList<Play> playList;
        ArrayList<PlayOrderDetails> playOrderDetailList;
        public ListPlayAdapterForReview(Context context, ArrayList<Play> playList) {
            this.context = context;
            this.playList = playList;
            this.playOrderDetailList = playOrderDetailList;
        }
        public int getCount() {

            return playList.size();

        }

        public Object getItem(int position) {
            return playList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {
            WMTextView txtTitle, txtAuther, txtDuration1, txtDuration2,btnSharePreview;
            ImageView imgPreviewTrashIcon;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_myplay_preview, parent, false);
                holder = new ViewHolder();
                holder.txtTitle = (WMTextView) convertView.findViewById(R.id.txtPreviewTitle);
                holder.txtAuther = (WMTextView) convertView.findViewById(R.id.txtPreviewAuther);
                holder.txtDuration1 = (WMTextView) convertView.findViewById(R.id.txtPreviewDuration1);
                holder.txtDuration2 = (WMTextView) convertView.findViewById(R.id.txtPreviewDuration2);
//                holder.btnOrderPreview = (WMTextView) convertView.findViewById(R.id.btnOrderPreview);
                holder.btnSharePreview = (WMTextView) convertView.findViewById(R.id.btnSharePreview);
//                holder.btnReadPreview = (WMTextView) convertView.findViewById(R.id.btnReadPreview);
                holder.imgPreviewTrashIcon = (ImageView) convertView.findViewById(R.id.imgPreviewTrashIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtTitle.setText(playList.get(position).Title);
            holder.txtAuther.setText("Forfatter: " + playList.get(position).Author);
            holder.txtDuration1.setText(playList.get(position).Duration+" min., " + playList.get(position).Age + " år");
            holder.txtDuration2.setText("Musik: " + playList.get(position).Music + ", " + playList.get(position).Actors + " medvirkende");

            holder.btnSharePreview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //TODO
//
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",playListForReview.get(position));
                    complexPreferences.commit();

                    dialogForShare = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                    dialogForShare.title("");
                    dialogForShare.show();
                    ComplexPreferences complexPreferencesForUser = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                    cUser = complexPreferencesForUser.getObject("current_user", User.class);
                    SharedPreferences pre = getActivity().getSharedPreferences("session_id", getActivity().MODE_PRIVATE);
                    session_id = pre.getString("session_id", "");
                    //ShareActivityForPreview.teachers.clear();
                    retriveSchoolTeachers(session_id, cUser.getDomain());


                }
            });

            holder.imgPreviewTrashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSelectedToBeDeletedForReview=playListForReview.get(position);

                    showDialogForReviewDelete("Slet","Er du sikker på, at du vil slette?");
                }
            });


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Goto read page
                    dialog_next = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    dialog_next.title("Henter");
                    dialog_next.show();

                    gotoSpecificPage(position,0,playListForReview.get(position),ACTIVITY_TYPE.ORDER_ACTIVITY);

                }
            });
            return convertView;

        }

    }



    public void showDialogForReviewDelete(String title,String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                removePlayForReview();


                dialog.dismiss();

            }
        });

        alert.setNegativeButton("Nej", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alert.show();
    }



    public void removePlayForReview() {
        Log.e("removePlayForReview: ", "inside removePlayForReview");
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    Reader reader = APIDelete.postData("http://api.danteater.dk/api/PlayOrderReview/" + playSelectedToBeDeletedForReview.OrderId + "?userid=" + currentUser.getUserId());
                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = reader.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    reader.close();
                    Log.e("response after deleted: ", response + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                removePlayForReviewFromList();
            }
        }.execute();

    }


    public void removePlayForReviewFromList() {

        for (int i=0;i<playListForReview.size();i++) {
            if (playListForReview.get(i).PlayId.contains(playSelectedToBeDeletedForReview.getPlayId())) {
                playListForReview.remove(playListForReview.get(i));

            }
        }
        listPlayAdapterForReview.notifyDataSetChanged();
    }


    // For Order tab
    public class ListPlayAdapterForPerform extends BaseAdapter {

        Context context;
        LayoutInflater inflater;

        ArrayList<Play> playList;
        ArrayList<PlayOrderDetails> playOrderDetailList;

        public ListPlayAdapterForPerform(Context context, ArrayList<Play> playList, ArrayList<PlayOrderDetails> playOrderDetailList) {

            this.context = context;
            this.playList = playList;
            this.playOrderDetailList = playOrderDetailList;
        }

        public int getCount() {

            return playList.size();

        }

        public Object getItem(int position) {
            return playList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {

            WMTextView txtTitle, txtAuther, txtDuration1, txtDuration2, txtNumberOfPerformance,btnOrdering,btnShareOrder;
            ImageView imgOrderTrashIcon;


        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_myplay_order, parent, false);
                holder = new ViewHolder();
                holder.txtTitle = (WMTextView) convertView.findViewById(R.id.txtOrderTitle);
                holder.txtAuther = (WMTextView) convertView.findViewById(R.id.txtOrderAuther);
                holder.txtDuration1 = (WMTextView) convertView.findViewById(R.id.txtOrderDuration1);
                holder.txtDuration2 = (WMTextView) convertView.findViewById(R.id.txtOrderDuration2);
                holder.txtNumberOfPerformance = (WMTextView) convertView.findViewById(R.id.txtOrderNumberOfPerformance);
                holder.btnOrdering = (WMTextView) convertView.findViewById(R.id.btnOrdering);
                holder.btnShareOrder = (WMTextView) convertView.findViewById(R.id.btnShareOrder);

                holder.imgOrderTrashIcon=(ImageView)convertView.findViewById(R.id.imgOrderTrashIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtTitle.setText(playList.get(position).Title);

            if (playOrderDetailList.get(position).NumberOfAuditions.equalsIgnoreCase("True")) {
                holder.txtAuther.setText("Generalprøve: Ja");
            } else {
                holder.txtAuther.setText("Generalprøve: Nej");
            }
            // ios working
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            float performDateFirst = Float.parseFloat(playOrderDetailList.get(position).PerformDateFirst);
            float performDateLast = Float.parseFloat(playOrderDetailList.get(position).PerformDateLast);
            Date firstDate = float2Date(performDateFirst);
            Date lastDate = float2Date(performDateLast);
            Date currentDate=new Date();
            holder.txtDuration1.setText("Første opførelse: " + format.format(firstDate));
            holder.txtDuration2.setText("Sidste opførelse: " + format.format(lastDate));



            if(currentDate.after(lastDate)){
                holder.imgOrderTrashIcon.setVisibility(View.VISIBLE);
            } else {
                holder.imgOrderTrashIcon.setVisibility(View.GONE);
            }
            holder.txtNumberOfPerformance.setText("Antal opførelser: " + playOrderDetailList.get(position).NumberOfPerformances);

            holder.btnOrdering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",playListForPerform.get(position));
                    complexPreferences.commit();
                    //TODO
                    Intent intent=new Intent(getActivity(),OrderPlayActivityForPerformNew.class);
                    intent.putExtra("isAlreadyOrdered",true);
                    startActivity(intent);
                }
            });



            holder.btnShareOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",playListForPerform.get(position));
                    complexPreferences.commit();

                    dialogForShare = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                    dialogForShare.title("");
                    dialogForShare.show();
                    ComplexPreferences complexPreferencesForUser = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
                    cUser = complexPreferences.getObject("current_user", User.class);
                    SharedPreferences pre = getActivity().getSharedPreferences("session_id", getActivity().MODE_PRIVATE);
                    session_id = pre.getString("session_id", "");
//                    ShareActivityForPerform.classes.clear();
//                    ShareActivityForPerform.teachers.clear();
//                    ShareActivityForPerform.pupils.clear();
                    retriveSchoolClasses(session_id, cUser.getDomain());
                    retriveSchoolTeachers(session_id, cUser.getDomain());


                }
            });

            holder.imgOrderTrashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    playSelectedToBeDeletedForPerform=playListForPerform.get(position);
                    showDialogForPerformDelete("Slet","Er du sikker på, at du vil slette?");
                }
            });


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog_next = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    dialog_next.title("Henter");
                    dialog_next.show();
                    gotoSpecificPage(position,0,playListForPerform.get(position),ACTIVITY_TYPE.TAB_ACTIVITY);

                }
            });
            return convertView;
        }

    }

    public void showDialogForPerformDelete(String title,String message) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                removePlayForPerform();


                dialog.dismiss();

            }
        });

        alert.setNegativeButton("Nej", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });

        alert.show();
    }



    public void removePlayForPerform() {
        Log.e("removePlayForPerform: ", "inside removePlayForPerform");
        new AsyncTask<Void,Void,Void>(){

            @Override
            protected Void doInBackground(Void... voids) {

                try {
                    Reader reader = APIDelete.postData("http://api.danteater.dk/api/PlayOrderPerform/" + playSelectedToBeDeletedForPerform.OrderId + "?userid=" + currentUser.getUserId());
                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = reader.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    reader.close();
                    Log.e("response after deleted: ", response + " ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                removePlayForPerformFromList();
            }
        }.execute();

    }


    public void removePlayForPerformFromList() {

        for (int i=0;i<playListForPerform.size();i++) {
            if (playListForPerform.get(i).PlayId.contains(playSelectedToBeDeletedForPerform.getPlayId())) {
                playOrderList.remove(playListForPerform.get(i).playOrderDetails);
                playListForPerform.remove(playListForPerform.get(i));

            }
        }
        listPlayAdapterForPerform.notifyDataSetChanged();
    }

    private void gotoSpecificPage(final int play_index,int tab_index, final Play play, final ACTIVITY_TYPE act_type){


        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        boolean hasPlay = dbh.hasPlayWithPlayOrderIdText(play.OrderId);
        dbh.close();


        if(hasPlay == true){

            Log.i("hasplay","true");
            Log.i("Order Id",play.OrderId);
            SharedPreferences preferences = getActivity().getSharedPreferences("Plays", getActivity().MODE_PRIVATE);
            String k = "PlayLatesteUpdateDate"+play.PlayId;
            //  Toast.makeText(getActivity(),preferences.getString(k,""), Toast.LENGTH_SHORT).show();
            //  long unixTime = Long.parseLong(preferences.getString(k,""));
            long unixTime2 = Long.parseLong(preferences.getString(k,""));

            // BigDecimal bigDecimal = new BigDecimal(unixTime);

            //  String serverLink = API.link_getPlayUpdateForPlayOrderIdString+play.OrderId+"?unixTimeStamp="+unixTime;
            String serverLink = API.link_getPlayUpdateForPlayOrderIdString+play.OrderId+"?unixTimeStamp="+unixTime2;
            Log.e("update string before update : ",serverLink);
            new CallWebService(serverLink,CallWebService.TYPE_JSONARRAY) {

                @Override
                public void response(final String response) {

                    Log.i("Response update play : ",""+response);

                    if(response == null || response.equalsIgnoreCase("")){



                    }else{

                        new AsyncTask<String,Integer,String>(){

                            @Override
                            protected String doInBackground(String... params) {

                                try {
                                    JSONArray arr = new JSONArray(response);

                                    for(int i=0;i<arr.length();i++){

                                        JSONObject jsonObject = arr.getJSONObject(i);
                                        PlayLines playLine = new GsonBuilder().create().fromJson(jsonObject.toString(),PlayLines.class);

                                        DatabaseWrapper dbWrap = new DatabaseWrapper(getActivity());
                                        Log.e("Before update playId ",play.PlayId);
                                        dbWrap.updatePlayLine(playLine,Integer.parseInt(play.PlayId));
                                        dbWrap.close();


                                    }



                                    //  state.playID = plyIDAfterUpdate;


                                }catch(Exception e){
                                    e.printStackTrace();
                                }


                                return null;
                            }

                            @Override
                            protected void onPostExecute(String s) {
                                super.onPostExecute(s);
                                dialog_next.dismiss();

                                DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
                                plyIDAfterUpdate = dbh.getPlayIdFromDBForOrderId(play.OrderId);
                                dbh.close();
                                SharedPreferences pre = getActivity().getSharedPreferences("Plays", getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor edi = pre.edit();
                                edi.putInt("playid",plyIDAfterUpdate);
                                edi.commit();


                                SharedPreferences preferences = getActivity().getSharedPreferences("Plays",getActivity().MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                String k = "PlayLatesteUpdateDate"+play.PlayId;
                                editor.putString(k,""+(long) (System.currentTimeMillis() / 1000));
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
            Log.i("hasplay","false");
            // insert new play to db

            new CallWebService(API.link_retrievePlayContentsForPlayOrderId +play.OrderId,CallWebService.TYPE_JSONOBJECT) {

                @Override
                public void response(final String response) {

                    Log.i("Response full play : ",""+response);
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
                            editor.putString(k,""+(int) (System.currentTimeMillis() / 1000));
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
            protected void onPreExecute() {
                super.onPreExecute();
                SharedPreferences preferences = getActivity().getSharedPreferences("Plays", getActivity().MODE_PRIVATE);
                playid = preferences.getInt("playid",0);

            }

            @Override
            protected String doInBackground(String... params) {

                DatabaseWrapper dbh = new DatabaseWrapper(getActivity());

                ply = dbh.retrievePlayWithId(playid);
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
                        i1.putExtra("isFromLogin",false);
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


    public  void retriveSchoolClasses(final String seesionId, final String domain) {

        JSONObject methodParams = new JSONObject();
        JSONObject requestParams = new JSONObject();

        try {
            methodParams.put("session_id", seesionId);
            methodParams.put("domain", domain);

            requestParams.put("methodname", "listGroups");
            requestParams.put("type", "jsonwsp/request");
            requestParams.put("version", "1.0");
            requestParams.put("args", methodParams);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/GroupService/jsonwsp", requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jobj) {
                    String res = jobj.toString();
//                    Log.e("response for retrive school classes...: ", res + "");

                    BeanGroupInfo beanGroupInfo = new GsonBuilder().create().fromJson(res, BeanGroupInfo.class);
                    BeanGroupResult beanGroupResult = beanGroupInfo.getBeanGroupResult();
                    ArrayList<Group> groupArrayList = beanGroupResult.getGroupArrayList();
                    ShareActivityForPerform.classes.clear();
                    //   pupils.clear();

                    for (Group beanGroup : groupArrayList) {
                        if (beanGroup.getGroupType().equals("classtype")) {
                            ShareActivityForPerform.classes.add(beanGroup);
                            Log.e("group domain", beanGroup.getDomain() + "");
                            numberOfClassesToBeRetrieved++;
                            ShareActivityForPerform.pupils.clear();
                            retriveMembers(seesionId, domain, beanGroup);
                        }
                    }

                    Log.e("classes...: ", ShareActivityForPerform.classes + "");

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }
            });
            MyApplication.getInstance().addToRequestQueue(req);


        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public  void retriveSchoolTeachers(String seesionId, String domain) {
        groupForTeacher.setGroupId("teacher");
        ShareActivityForPerform.teachers.clear();
        ShareActivityForPreview.teachers.clear();
        retriveMembers(seesionId, domain, groupForTeacher);

    }

    public  void retriveMembers(String seesionId, String domain, final Group group) {
        JSONObject methodParams = new JSONObject();
        JSONObject requestParams = new JSONObject();
        try {
            methodParams.put("session_id", seesionId);
            methodParams.put("domain", domain);
            methodParams.put("group_cn", group.getGroupId());
            requestParams.put("methodname", "listGroupMembers");
            requestParams.put("type", "jsonwsp/request");
            requestParams.put("version", "1.0");
            requestParams.put("args", methodParams);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/GroupService/jsonwsp", requestParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {

                    String res = jobj.toString();
//                    Log.e("response for retrive school teachers...: ", res + "");

                    BeanGroupMemberInfo beanGroupMemberInfo = new GsonBuilder().create().fromJson(res, BeanGroupMemberInfo.class);
                    BeanGroupMemberResult beanGroupMemberResult = beanGroupMemberInfo.getBeanGroupMemberResult();

                    ArrayList<GroupMembers> groupMembersArrayList = beanGroupMemberResult.getGroupMembersArrayList();
                    ArrayList<User> userArrayList = new ArrayList<User>();
                    userArrayList.clear();
                    for (GroupMembers beanGroupMembers : groupMembersArrayList) {
//                     Log.e("given name",beanGroupMembers.getGivenName()+" "+beanGroupMembers.getSn());

                        userArrayList.add(new User(beanGroupMembers.getGivenName(), beanGroupMembers.getSn(), beanGroupMembers.getUid(), beanGroupMembers.getPrimaryGroup(), null, beanGroupMembers.getDomain()));
                    }

                    if (group.getGroupId().equals("teacher")) {

                        ShareActivityForPerform.teachers.addAll(userArrayList);
                        ShareActivityForPreview.teachers.addAll(userArrayList);
                        Log.e("teachers:",ShareActivityForPerform.teachers+"");
                        Log.e("teachers:",ShareActivityForPreview.teachers+"");

                        finishedRetrievingTeachers = true;
                    } else {

                        ShareActivityForPerform.pupils.put(group.getGroupName().toString(), userArrayList);


                        for(Map.Entry<String,ArrayList<User>> entry : ShareActivityForPerform.pupils.entrySet()){

                            Log.e("key: ",entry.getKey()+" ");
                            Log.e("vlaues: ",entry.getValue()+"  ");


                        }
                        Log.e("pupils: ",ShareActivityForPerform.pupils+"");

                        numberOfClassesToBeRetrieved--;
                    }

                    if(rbGennemsyn.isChecked()) {
                        if (finishedRetrievingTeachers) {

                            ComplexPreferences complexPreference = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
                            selectedPlay = complexPreference.getObject("selected_play",Play.class);
                            new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {
                                @Override
                                public void response(String response) {
                                    dialogForShare.dismiss();
                                    Type listType = new TypeToken<List<SharedUser>>() {
                                    }.getType();
                                    ShareActivityForPreview.sharedTeachers = new GsonBuilder().create().fromJson(response,listType);
                                    Intent i = new Intent(getActivity(), ShareActivityForPreview.class);
                                    startActivity(i);
                                }
                                @Override
                                public void error(VolleyError error) {
                                    Log.e("error: ",error+"");

                                }
                            }.start();



                        }

                    } else {
                        if (finishedRetrievingTeachers && numberOfClassesToBeRetrieved == 0) {
                            ComplexPreferences complexPreference = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
                            selectedPlay = complexPreference.getObject("selected_play",Play.class);
                            new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {
                                @Override
                                public void response(String response) {
                                    dialogForShare.dismiss();
                                    Type listType = new TypeToken<List<SharedUser>>() {
                                    }.getType();
                                    ShareActivityForPerform.sharedTeachersAndStudents = new GsonBuilder().create().fromJson(response,listType);
                                    Intent intent = new Intent(getActivity(), ShareActivityForPerform.class);
                                    startActivity(intent);
                                }
                                @Override
                                public void error(VolleyError error) {
                                    Log.e("error: ",error+"");

                                }
                            }.start();


                        }

                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }
            });
            MyApplication.getInstance().addToRequestQueue(req);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }
}
