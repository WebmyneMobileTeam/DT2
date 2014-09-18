package wm.com.danteater.my_plays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayOrderDetails;
import wm.com.danteater.R;
import wm.com.danteater.app.PlayTabActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.SegmentedGroup;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;


public class FragmentMyPlay extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private enum ACTIVITY_TYPE{
        TAB_ACTIVITY,ORDER_ACTIVITY
    }

    private HUD dialog;
    private HUD dialog_next;
    private SegmentedGroup segmentedGroupPlays;
    private RadioButton rbBestilte;
    private RadioButton rbGennemsyn;
    private ListView listPlay;


    private ArrayList<Play> playList;
    private ArrayList<Play> playListForReview = new ArrayList<Play>();
    private ArrayList<Play> playListForPerform = new ArrayList<Play>();
    private ArrayList<PlayOrderDetails> playOrderList = new ArrayList<PlayOrderDetails>();
    private User user;

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
        user =complexPreferences.getObject("current_user", User.class);
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

        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Mine Stykker");
        dialog.show();

        new CallWebService("http://api.danteater.dk/api/MyPlays?UserId="+ user.getUserId(), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                dialog.dismiss();
                Log.e("json response", response + "");
                handledataafterresponseVolly(response);
            }

            @Override
            public void error(VolleyError error) {
                dialog.dismissWithStatus(R.drawable.ic_navigation_cancel, "Error");
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }.start();




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

                    }

                }

                for (int i = 0; i < playOrderList.size(); i++) {
                    if (playOrderList.get(i) != null) {
                        PlayOrderDetails bean = playOrderList.get(i);
                        Log.i("PlayOrderId", bean.PlayOrderId + "");
                    }
                }

                listPlay.setAdapter(new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList));


            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();

        rbBestilte.setChecked(true);

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId) {

            case R.id.rbBestilte:
                listPlay.setAdapter(null);
                listPlay.setAdapter(new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList));
                break;

            case R.id.rbGennemsyn:
                listPlay.setAdapter(null);
                listPlay.setAdapter(new ListPlayAdapterForReview(getActivity(), playListForReview));
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
                    Toast.makeText(getActivity(), "Share", Toast.LENGTH_SHORT).show();
                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",playListForReview.get(position));
                    complexPreferences.commit();


                    Intent i = new Intent(getActivity(), ShareActivityForPreview.class);

                    startActivity(i);


                }
            });

            holder.imgPreviewTrashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();
                }
            });


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                 // Goto read page

               /*     final HUD dialog = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    dialog.title("Henter Seneste\nændringer");
                    dialog.show();*/

                    gotoSpecificPage(0,playListForPerform.get(position),ACTIVITY_TYPE.ORDER_ACTIVITY);

                }
            });


            return convertView;

        }

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
//                holder.btnReadOrder = (WMTextView) convertView.findViewById(R.id.btnReadOrder);
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

            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            float performDateFirst = Float.parseFloat(playOrderDetailList.get(position).PerformDateFirst);
            float performDateLast = Float.parseFloat(playOrderDetailList.get(position).PerformDateLast);
            Date firstDate = float2Date(performDateFirst);
            Date lastDate = float2Date(performDateLast);

            holder.txtDuration1.setText("Første opførelse: " + format.format(firstDate));
            holder.txtDuration2.setText("Sidste opførelse: " + format.format(lastDate));
            holder.txtNumberOfPerformance.setText("Antal opførelser: " + playOrderDetailList.get(position).NumberOfPerformances);

            holder.btnOrdering.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",playListForPerform.get(position));
                    complexPreferences.commit();

                  Intent intent=new Intent(getActivity(),OrderPlayActivityForPerform.class);
                  startActivity(intent);
                }
            });



            holder.btnShareOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
                    complexPreferences.putObject("selected_play",playListForPerform.get(position));
                    complexPreferences.commit();


                    Intent intent=new Intent(getActivity(),ShareActivityForPerform.class);
                    startActivity(intent);


                }
            });

            holder.imgOrderTrashIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Delete", Toast.LENGTH_SHORT).show();
                }
            });


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog_next = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    dialog_next.title("Henter");
                    dialog_next.show();

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            //  gotoTabActivity(position, "Read");
                            gotoSpecificPage(0,playListForPerform.get(position),ACTIVITY_TYPE.TAB_ACTIVITY);

                        }
                    });


                }
            });


            return convertView;


        }

    }

    private void gotoSpecificPage(int tab_index, final Play play,ACTIVITY_TYPE type){


        DatabaseWrapper dbh = new DatabaseWrapper(getActivity());
        boolean hasPlay = dbh.hasPlayWithPlayOrderIdText(play.OrderId);
        dbh.close();


        if(hasPlay == true){

            Log.i("hasplay","true");

            new CallWebService(API.link_retrievePlayContentsForPlayOrderId +play.OrderId,CallWebService.TYPE_JSONOBJECT) {

                @Override
                public void response(String response) {

                    Log.i("Response full play : ",""+response);

                    dialog_next.dismiss();

                    Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                    DatabaseWrapper db = new DatabaseWrapper(getActivity());
                    db.insertPlay(receivedPlay,false);

                    db.close();

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

        switch (type){

            case ORDER_ACTIVITY:

                Intent i = new Intent(getActivity(),ReadActivityFromPreview.class);
                startActivity(i);

                break;

            case TAB_ACTIVITY:

                Toast.makeText(getActivity(), "TAB", Toast.LENGTH_SHORT).show();

                break;

        }







    }

    private void gotoTabActivity(int position,String type) {

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref",0);
        complexPreferences.putObject("selected_play",playListForPerform.get(position));
        complexPreferences.commit();

        Intent i = new Intent(getActivity(), PlayTabActivity.class);
        i.putExtra("infoData",playList.get(position).Synopsis+"");
        i.putExtra("type_navigation",type);
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
