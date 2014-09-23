package wm.com.danteater.my_plays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.model.StateManager;
import wm.com.danteater.tab_info.BeanOrderPlayReview;

public class OrderPlayActivityForPerform extends BaseActivity {

    private ListView orderPlayList;
    Context context;
    private String title;
    Date firstDate;
    Date seocndDate;
    String bDate;
    private BeanOrderPlayReview beanOrderPlayReview;
    private String playOrderIdStr, playOrderError;
    private User currentUser;
    ArrayList<String> nameList = new ArrayList<String>();
    private DatePickerDialog datePickerdialog;
    private WMTextView orderPlay;
    DatabaseWrapper dbHelper;
    private HUD dialog_next;
    //views of listview at positions 0,1 and 3
    View firstView;
    WMTextView firstViewValue;
    EditText etFirstView;
    View firstDateView;
    WMTextView txtFirstDate;
    View secondDateView;
    WMTextView txtSecondDate;
    boolean rehersalBool=false;
    Play selectedPlay;
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_play);
        ComplexPreferences complexPreferencesUser = ComplexPreferences.getComplexPreferences(OrderPlayActivityForPerform.this, "user_pref", 0);
        currentUser =complexPreferencesUser.getObject("current_user", User.class);
        context = OrderPlayActivityForPerform.this;
        dbHelper=new DatabaseWrapper(context);
        Intent i = getIntent();
        title = i.getStringExtra("title");
        txtHeader.setText(title);
        orderPlayList = (ListView) findViewById(R.id.orderPlayList);
        orderPlay = (WMTextView) findViewById(R.id.btnPlayOrder);
        nameList.add("Antal opførelser");
        nameList.add("Dato for premiere");
        nameList.add("Generalprøve");
        nameList.add("Dato for sidste opførelse");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play",Play.class);
        ((WMTextView)getActionBar().getCustomView()).setText(selectedPlay.Title);

        orderPlayList.setAdapter(new ListPlayAdapterForPerform(context, nameList));
        orderPlay.setBackgroundColor(getResources().getColor(R.color.gray_color));
        orderPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InitViews();

                if (!((firstViewValue.getText().toString() == null || firstViewValue.getText().toString() == "" || firstViewValue.getText().toString().isEmpty()) || (txtFirstDate.getText().toString() == null || txtFirstDate.getText().toString() == "" || txtFirstDate.getText().toString().isEmpty()) || (txtSecondDate.getText().toString() == null || txtSecondDate.getText().toString() == "" || txtSecondDate.getText().toString().isEmpty()))) {
                    new AsyncTask<Void,Void,Void>(){

                        @Override
                        protected void onPreExecute() {
                            super.onPreExecute();
                            dialog_next = new HUD(OrderPlayActivityForPerform.this,android.R.style.Theme_Translucent_NoTitleBar);
                            dialog_next.title("Stykker bestilt");
                            dialog_next.show();

                        }
                        @Override
                        protected Void doInBackground(Void... voids) {

                    orderPlayForPerformance();

                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            dialog_next.dismissWithStatus(R.drawable.ic_navigation_accept,"Stykker bestilt");

                            new CountDownTimer(2500, 1000) {

                                @Override
                                public void onFinish() {
                                    finish();

                                }

                                @Override
                                public void onTick(long millisUntilFinished) {

                                }
                            }.start();
                        }
                    }.execute();
                }

            }
        });
    }

    // Views init
    private void InitViews() {
        firstView = orderPlayList.getChildAt(0);
        firstViewValue = (WMTextView) firstView.findViewById(R.id.item_orderplay_selected_value);
        firstDateView = orderPlayList.getChildAt(1);
        txtFirstDate = (WMTextView) firstDateView.findViewById(R.id.item_orderplay_selected_value);
        secondDateView = orderPlayList.getChildAt(3);
        txtSecondDate = (WMTextView) secondDateView.findViewById(R.id.item_orderplay_selected_value);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // For Order tab
    public class ListPlayAdapterForPerform extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> playList;


        public ListPlayAdapterForPerform(Context context, ArrayList<String> playList) {
            this.context = context;
            this.playList = playList;
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


        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (position == 2) {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_orderplay_rehersal_view, parent, false);
                    holder = new ViewHolder();
                    holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_rehersal_value);
                    holder.imgOrderPlayInfo = (ImageView) convertView.findViewById(R.id.orderPlayInfo);
                    holder.aSwitch=(Switch) convertView.findViewById(R.id.orderPlaySwitch);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.txtTitle.setText(playList.get(position));
                holder.aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                        if(isChecked) {
                            rehersalBool=true;
                        } else {
                            rehersalBool=false;
                        }
                    }
                });
                holder.imgOrderPlayInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert("title", "message");
                    }
                });
            } else {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_orderplay_view, parent, false);
                    holder = new ViewHolder();
                    holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_value);
                    holder.item_orderplay_selected_value = (WMTextView) convertView.findViewById(R.id.item_orderplay_selected_value);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.txtTitle.setText(playList.get(position));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        InitViews();
                        if (position == 1 || position == 3) {

                            // check for date validation
                            processDate(position);
                        }

                        if (position == 0) {

                            etFirstView = (EditText) firstView.findViewById(R.id.item_orderplay_selected_value_et);
                            etFirstView.setVisibility(firstView.VISIBLE);
                            etFirstView.setFocusable(true);
                            // check number of performance 1-4
                            numberOfPerformances();

                        }
                    }
                });
            }
            return convertView;

        }

    }

    protected void processDate(final int position) {


        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerdialog = new DatePickerDialog(
                context, null, year, month, day);
        datePickerdialog.setCancelable(true);
        datePickerdialog.setTitle("Select Date");


        datePickerdialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel	", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        datePickerdialog.dismiss();
                    }
                });
        datePickerdialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        datePickerdialog.dismiss();
                        bDate = datePickerdialog
                                .getDatePicker().getDayOfMonth()
                                + "-"
                                + datePickerdialog.getDatePicker()
                                .getMonth()
                                + "-"
                                + datePickerdialog.getDatePicker()
                                .getYear();


                        View view = orderPlayList.getChildAt(position);
                        WMTextView item_orderplay_selected_value = (WMTextView) view.findViewById(R.id.item_orderplay_selected_value);
                        item_orderplay_selected_value.setVisibility(View.VISIBLE);
                        item_orderplay_selected_value.setText(bDate);

                        InitViews();

                        if (((txtFirstDate.getText().toString() != null || txtFirstDate.getText().toString() != "") || (txtSecondDate.getText().toString() != null || txtSecondDate.getText().toString() != ""))) {
                            try {
                             firstDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(txtFirstDate.getText().toString());
                             seocndDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(txtSecondDate.getText().toString());
                                System.out.println(firstDate);
                                System.out.println(seocndDate);
                                if (firstDate.after(seocndDate) || firstDate.equals(seocndDate)) {
                                    item_orderplay_selected_value.setText("");
                                    orderPlay.setBackgroundColor(getResources().getColor(R.color.gray_color));
                                    showAlert("title", "message");
                                } else {
                                    if (!((firstViewValue.getText().toString() == null || firstViewValue.getText().toString() == "" || firstViewValue.getText().toString().isEmpty()) || (txtFirstDate.getText().toString() == null || txtFirstDate.getText().toString() == "" || txtFirstDate.getText().toString().isEmpty()) || (txtSecondDate.getText().toString() == null || txtSecondDate.getText().toString() == "" || txtSecondDate.getText().toString().isEmpty()))) {
                                        orderPlay.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }


                });

        datePickerdialog.show();

    }


    public class ViewHolder {

        WMTextView txtTitle, item_orderplay_selected_value;
        ImageView imgOrderPlayInfo;
        Switch aSwitch;

    }

    void showAlert(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {


                dialog.dismiss();
            }
        });
        alert.show();

    }

    void numberOfPerformances() {
        InitViews();




        firstViewValue.setVisibility(firstView.GONE);

        etFirstView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (Integer.parseInt(etFirstView.getText().toString()) > 4 || Integer.parseInt(etFirstView.getText().toString()) < 1) {
                    etFirstView.setText("");
                    orderPlay.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    showAlert("title", "message");
                } else {
                    etFirstView.setVisibility(firstView.GONE);
                    firstViewValue.setVisibility(firstView.VISIBLE);
                    firstViewValue.setText(etFirstView.getText().toString());
                    if (!((firstViewValue.getText().toString() == null || firstViewValue.getText().toString() == "" || firstViewValue.getText().toString().isEmpty()) || (txtFirstDate.getText().toString() == null || txtFirstDate.getText().toString() == "" || txtFirstDate.getText().toString().isEmpty()) || (txtSecondDate.getText().toString() == null || txtSecondDate.getText().toString() == "" || txtSecondDate.getText().toString().isEmpty()))) {
                        orderPlay.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
                    }
                }


                return false;
            }
        });


    }

    private void orderPlayForPerformance() {
       final JSONObject params=new JSONObject();

        try {



        params.put("PlayId",selectedPlay.getPlayId()+"");
        params.put("UserId",currentUser.getUserId()+"");
        params.put("SchoolId",currentUser.getDomain()+"");
        params.put("NumberOfPerformances",etFirstView.getText().toString()+"");
        params.put("NumberOfAuditions",rehersalBool+"");
        params.put("PerformDateFirst",firstDate.getTime()+"");
        params.put("PerformDateLast",seocndDate.getTime()+"");
        params.put("Comments","");
        Log.e("params: ",params+"");

            boolean hasBeenOrderedForReviewBefore = (selectedPlay.getOrderId() != null) && (!selectedPlay.getOrderId().equals(""));
            if (hasBeenOrderedForReviewBefore) {
                params.put("PlayOrderId",selectedPlay.getOrderId()+"");
            }

                    JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "http://api.danteater.dk/api/PlayOrderPerform", params, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject jobj) {
                            String res = jobj.toString();
                            Log.e("response: ", res + "");
                            beanOrderPlayReview = new GsonBuilder()
                                    .create().fromJson(res, BeanOrderPlayReview.class);

                            selectedPlay.OrderId = beanOrderPlayReview.PlayOrderId;


                            if(dbHelper.hasPlayWithPlayOrderIdText(selectedPlay.OrderId)) {
                                selectedPlay.OrderType = "Perform";
                                dbHelper.updatePlayInfo(selectedPlay);
                                Log.e("update","updated successfully");
                            } else {
                                retrievePlayContentsForPlayOrderId();
                            }

                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError arg0) {
                        }
                    });
                    MyApplication.getInstance().addToRequestQueue(req);


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void retrievePlayContentsForPlayOrderId() {

        new CallWebService("http://api.danteater.dk/api/playfull/" + selectedPlay.OrderId, CallWebService.TYPE_JSONOBJECT) {

            @Override
            public void response(final String response) {

                Log.e("Response play full:", response + "");


                Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                DatabaseWrapper db = new DatabaseWrapper(OrderPlayActivityForPerform.this);
                db.insertPlay(receivedPlay, false);
                db.close();

                sharePlayWithNoone();

//                sharePlayWithMe();
            }

            @Override
            public void error(VolleyError error) {
                Log.e("error", error + "");
            }
        }.start();

    }

    private void sharePlayWithNoone() {

        final ArrayList<User> totalUsers = new ArrayList<User>();
        final JSONArray shareWithUsersArrayN = new JSONArray();
        if (totalUsers != null || totalUsers.size() != 0) {


            for (User user : totalUsers) {
                String nameToBeSaved;

                if (user.checkTeacherOrAdmin(user.getRoles()) == true) {
                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName() + " (lærer)";
                } else {
                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName();
                }
                JSONObject userDict = new JSONObject();
                try {
                    userDict.put("UserId", currentUser.getUserId());
                    userDict.put("UserName", nameToBeSaved);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                shareWithUsersArrayN.put(userDict);
            }
            Log.e("total users: ", shareWithUsersArrayN + "");

        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try

                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/" + selectedPlay.OrderId, shareWithUsersArrayN.toString());
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

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                sharePlayWithMe();
            }
        }.execute();


    }


    private void sharePlayWithMe() {
        final ArrayList<User> totalUsers = new ArrayList<User>();

        final JSONArray shareWithUsersArray = new JSONArray();
        totalUsers.add(currentUser);
        if (totalUsers != null || totalUsers.size() != 0) {
            for (User user : totalUsers) {
                String nameToBeSaved;

                if (user.checkTeacherOrAdmin(user.getRoles()) == true) {
                    // TODO can't add "(lærer)"
//                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName() + " (lærer)";
                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName();
                } else {
                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName();
                }
                JSONObject userDict = new JSONObject();
                try {
                    userDict.put("UserId", currentUser.getUserId());
                    userDict.put("UserName", nameToBeSaved);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                shareWithUsersArray.put(userDict);
            }
            Log.e("total users: ", shareWithUsersArray + "");
        }
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try

                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/"+selectedPlay.OrderId, shareWithUsersArray.toString());
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

                } catch (IOException e) {
                    e.printStackTrace();
                }





                return null;
            }


        }.execute();

    }

}
