package wm.com.danteater.my_plays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.BeanUser;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;

/**
 * Created by nirav on 16-09-2014.
 */
public class FragmentMyPlayPupil extends Fragment {

    private HUD dialog;
    private ListView listPlay;
    private BeanUser beanUser;
    private ArrayList<Play> playList;

    private ArrayList<Play> playListForPerform = new ArrayList<Play>();
    private ArrayList<PlayOrderDetails> playOrderList = new ArrayList<PlayOrderDetails>();

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
        beanUser = complexPreferences.getObject("current_user", BeanUser.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View convertView = inflater.inflate(R.layout.fragment_my_play_pupil, container, false);
        listPlay = (ListView) convertView.findViewById(R.id.listPlayForPupil);
        return convertView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        // show loading

        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Mine Stykker");
        dialog.show();

        new CallWebService("http://api.danteater.dk/api/MyPlays?UserId=" + beanUser.getUserId(), CallWebService.TYPE_JSONARRAY) {

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
                Log.i("first auther", playListForPerform.get(0).getAuthor() + "");


                for (int i = 0; i < playOrderList.size(); i++) {
                    if (playOrderList.get(i) != null) {
                        PlayOrderDetails bean = playOrderList.get(i);
                        Log.i("PlayOrderId", bean.PlayOrderId + "");
                    }
                }
                Log.e("adapter", "before adapter call");
                listPlay.setAdapter(new ListPlayAdapterForPerform(getActivity(), playListForPerform, playOrderList));
                Log.e("adapter", "after adapter call");

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
            Log.e("adapter", "inside adapter");
            this.context = context;
            this.playListPupil = playList;
            this.playOrderDetailListPupil = playOrderDetailList;
        }

        public int getCount() {
            Log.e("size", playListPupil.size() + "");
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
            Log.e("get view", "inside getview");
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

                    final HUD dialogRead = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                    dialogRead.title("Henter");
                    dialogRead.show();

                    new CountDownTimer(2000, 1000) {

                        @Override
                        public void onFinish() {

                            dialogRead.dismiss();

                            new Handler().post(new Runnable() {
                                @Override
                                public void run() {

                                    gotoTabActivity(position, "Read");

                                }
                            });


                        }

                        @Override
                        public void onTick(long millisUntilFinished) {

                        }
                    }.start();


                }
            });

            Log.e("get view", "end of getview");
            return convertView;


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
