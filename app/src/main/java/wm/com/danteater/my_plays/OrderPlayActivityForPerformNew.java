package wm.com.danteater.my_plays;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
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
import wm.com.danteater.customviews.WMEdittext;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.tab_info.BeanOrderPlayReview;

/**
 * Created by nirav kalola on 10/1/2014.
 */
public class OrderPlayActivityForPerformNew extends BaseActivity {

    private RelativeLayout relativeNumberOfPerformance,relativeFirstDate,relativeSecondDate;
    private EditText etNumberOfPerformanceValue;
    private WMTextView txtFirstDateValue,txtSecondDateValue,schoolId,userName,btnPlayOrder;
    private ImageView orderPlayInfo;
    private WMTextView txtOrderDetails;
    private Switch orderPlaySwitch;
    Context context;
    private DatabaseWrapper dbHelper;
    private HUD dialog_next;
    private User currentUser;
    private DatePickerDialog datePickerdialog;
    private Play selectedPlay;
    private BeanOrderPlayReview beanOrderPlayReview;
    String stringDate;
    Date firstDate;
    Date seocndDate;
    boolean rehersalBool=false;
    static boolean numberOfPerformance=false;
    static boolean isValidDate=false;
    boolean isAlreadyOrdered;
    private View lastDivider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_play_new);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent i=getIntent();

        isAlreadyOrdered=i.getBooleanExtra("isAlreadyOrdered",false);
        lastDivider= findViewById(R.id.lastDivider);
        relativeNumberOfPerformance=(RelativeLayout)findViewById(R.id.relativeNumberOfPerformance);
        relativeFirstDate=(RelativeLayout)findViewById(R.id.relativeFirstDate);
        relativeSecondDate=(RelativeLayout)findViewById(R.id.relativeSecondDate);
        etNumberOfPerformanceValue=(EditText)findViewById(R.id.etNumberOfPerformanceValue);
        btnPlayOrder=(WMTextView)findViewById(R.id.btnPlayOrderView);
        txtFirstDateValue=(WMTextView)findViewById(R.id.txtFirstDateValue);
        txtSecondDateValue=(WMTextView)findViewById(R.id.txtSecondDateValue);
        txtOrderDetails=(WMTextView)findViewById(R.id.txtOrderDetails);
        schoolId=(WMTextView)findViewById(R.id.schoolId);
        userName=(WMTextView)findViewById(R.id.userName);
        orderPlayInfo=(ImageView)findViewById(R.id.orderPlayInfo);
        orderPlaySwitch=(Switch)findViewById(R.id.orderPlaySwitch);
        ComplexPreferences complexPreferencesUser = ComplexPreferences.getComplexPreferences(OrderPlayActivityForPerformNew.this, "user_pref", 0);
        currentUser =complexPreferencesUser.getObject("current_user", User.class);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play",Play.class);
        context = OrderPlayActivityForPerformNew.this;
        dbHelper=new DatabaseWrapper(context);

        txtHeader.setText(selectedPlay.Title);
        schoolId.setText(currentUser.getDomain().toString()+"");
        userName.setText(currentUser.getFirstName()+" "+currentUser.getLastName());
        btnPlayOrder.setBackgroundColor(getResources().getColor(R.color.gray_color));
        btnPlayOrder.setEnabled(false);
        etNumberOfPerformanceValue.setFocusable(false);
        etNumberOfPerformanceValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                etNumberOfPerformanceValue.setFocusableInTouchMode(true);
                etNumberOfPerformanceValue.setFocusable(true);
                ((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
            }
        });
        //TODO
//        etNumberOfPerformanceValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus==false ){
//                    if(Integer.parseInt(etNumberOfPerformanceValue.getText().toString().trim())==1) {
//                        txtSecondDateValue.setText(txtFirstDateValue.getText().toString());
//                        seocndDate=firstDate;
//                        isValidDate=true;
//                    }
//                }
//            }
//        });

        etNumberOfPerformanceValue.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (i == EditorInfo.IME_ACTION_DONE) {

                    if (Integer.parseInt(etNumberOfPerformanceValue.getText().toString().trim()) > 4 || Integer.parseInt(etNumberOfPerformanceValue.getText().toString().trim()) < 1) {
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etNumberOfPerformanceValue.getWindowToken(), 0);
                        numberOfPerformance = false;
                        AlertDialog.Builder alert = new AlertDialog.Builder(OrderPlayActivityForPerformNew.this);
                        alert.setTitle("Antal opførelser");
                        alert.setMessage("Du kan bestille mellem 1 og 4 opførelser.");
                        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                etNumberOfPerformanceValue.setFocusable(true);
                                etNumberOfPerformanceValue.setText("");
                                dialog.dismiss();
                            }
                        });
                        alert.show();


                    } else if (Integer.parseInt(etNumberOfPerformanceValue.getText().toString().trim()) == 1) {
                        //TODO hide second date
                        lastDivider.setVisibility(View.INVISIBLE);
                        relativeSecondDate.setVisibility(View.INVISIBLE);
                        numberOfPerformance = true;
                        etNumberOfPerformanceValue.setFocusable(false);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etNumberOfPerformanceValue.getWindowToken(), 0);
                        txtSecondDateValue.setText(txtFirstDateValue.getText().toString());
                        seocndDate=firstDate;
                        if(!(txtFirstDateValue.getText().toString() == null || txtFirstDateValue.getText().toString() == "" || txtFirstDateValue.getText().toString().isEmpty()) ){
                            isValidDate=true;
                        }

                    } else {
                       // TODO show second date
                        lastDivider.setVisibility(View.VISIBLE);
                        relativeSecondDate.setVisibility(View.VISIBLE);
                        numberOfPerformance = true;
                        etNumberOfPerformanceValue.setFocusable(false);
                        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(etNumberOfPerformanceValue.getWindowToken(), 0);

                    }
                    if (!((etNumberOfPerformanceValue.getText().toString() == null || etNumberOfPerformanceValue.getText().toString() == "" || etNumberOfPerformanceValue.getText().toString().isEmpty()) || (txtFirstDateValue.getText().toString() == null || txtFirstDateValue.getText().toString() == "" || txtFirstDateValue.getText().toString().isEmpty()) || (txtSecondDateValue.getText().toString() == null || txtSecondDateValue.getText().toString() == "" || txtSecondDateValue.getText().toString().isEmpty()))) {
                        if (numberOfPerformance == true && isValidDate == true) {
                            btnPlayOrder.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
                            btnPlayOrder.setEnabled(true);
                        } else {
                            btnPlayOrder.setBackgroundColor(getResources().getColor(R.color.gray_color));
                            btnPlayOrder.setEnabled(false);
                        }
//                        Log.e("",numberOfPerformance+" "+isValidDate);
                    }

                    return true;
                }
                return false;
            }
        });

        relativeFirstDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processDate(true);
            }
        });


        relativeSecondDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processDate(false);
            }
        });




        orderPlaySwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    rehersalBool = true;
                } else {
                    rehersalBool = false;
                }
            }
        });

        orderPlayInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alert = new AlertDialog.Builder(OrderPlayActivityForPerformNew.this);
                alert.setTitle("Generalprøve");
                alert.setMessage("Generalprøve skal indberettes for sig, og skal ikke tælles med i antal opførelser");
                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });
                alert.show();
            }
        });

        btnPlayOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context,"clicked",Toast.LENGTH_LONG);
                if (!((etNumberOfPerformanceValue.getText().toString() == null || etNumberOfPerformanceValue.getText().toString() == "" || etNumberOfPerformanceValue.getText().toString().isEmpty()) || (txtFirstDateValue.getText().toString() == null || txtFirstDateValue.getText().toString() == "" || txtFirstDateValue.getText().toString().isEmpty()) || (txtSecondDateValue.getText().toString() == null || txtSecondDateValue.getText().toString() == "" || txtSecondDateValue.getText().toString().isEmpty()))) {
                    if(numberOfPerformance==true && isValidDate==true ) {

                        new AsyncTask<Void,Void,Void>(){

                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();
                                dialog_next = new HUD(OrderPlayActivityForPerformNew.this,android.R.style.Theme_Translucent_NoTitleBar);
                                dialog_next.title("");
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

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                            }
                                        });

                                        ReadActivityFromPreview.isBackFromOrder = true;
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
            }
        });

        if(isAlreadyOrdered==true){
            isValidDate=true;
            numberOfPerformance=true;
            etNumberOfPerformanceValue.setText(selectedPlay.getPlayOrderDetails().getNumberOfPerformances());
            if(Integer.parseInt(etNumberOfPerformanceValue.getText().toString())==1) {
                lastDivider.setVisibility(View.INVISIBLE);
                relativeSecondDate.setVisibility(View.INVISIBLE);
            }
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
            float performDateFirst = Float.parseFloat(selectedPlay.getPlayOrderDetails().getPerformDateFirst());
            float performDateLast = Float.parseFloat(selectedPlay.getPlayOrderDetails().getPerformDateLast());
            Date firstOneDate = float2Date(performDateFirst);
            Date lastOneDate = float2Date(performDateLast);
            txtFirstDateValue.setText(format.format(firstOneDate));

            txtSecondDateValue.setText(format.format(lastOneDate));
            try {
                firstDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(txtFirstDateValue.getText().toString());
                seocndDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(txtSecondDateValue.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(selectedPlay.getPlayOrderDetails().getNumberOfAuditions().equalsIgnoreCase("false")){
                orderPlaySwitch.setChecked(false);
                rehersalBool=false;
            } else {
                orderPlaySwitch.setChecked(true);
                rehersalBool=true;
            }

            btnPlayOrder.setText("Ret bestilling");

//            Log.e("isAlreadyOrdered","come from perform");
        }else {
            btnPlayOrder.setText("Bestil");
//            Log.e("isAlreadyOrdered","come from preview");
        }
    }


    protected void processDate(final boolean isFirstdate) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerdialog = new DatePickerDialog(context, null, year, month, day);
        datePickerdialog.setCancelable(true);
        datePickerdialog.setTitle("Vælg dato");

        datePickerdialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Annuller	", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        datePickerdialog.dismiss();
                    }
                });
        datePickerdialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "Vælg", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        datePickerdialog.dismiss();
                        stringDate = datePickerdialog.getDatePicker().getDayOfMonth()
                                + "-"
                                + (datePickerdialog.getDatePicker()
                                .getMonth()+1)
                                + "-"
                                + datePickerdialog.getDatePicker()
                                .getYear();


                        if(isFirstdate)
                        {
                            txtFirstDateValue.setText(stringDate);
                            //TODO
                            try {
                                if (Integer.parseInt(etNumberOfPerformanceValue.getText().toString()) == 1) {
                                    txtSecondDateValue.setText(stringDate);
                                }
                            } catch (NumberFormatException e) {
                                e.printStackTrace();
                            }
                        } else {

                            txtSecondDateValue.setText(stringDate);


                        }



                        try {
                            firstDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(txtFirstDateValue.getText().toString());
                            seocndDate = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).parse(txtSecondDateValue.getText().toString());
                            System.out.println(firstDate);
                            System.out.println(seocndDate);
                            if (firstDate.after(seocndDate) ) {
                                isValidDate=false;
                                if(isFirstdate) {
                                    txtSecondDateValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
//                                    txtFirstDateValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                } else {
                                    txtSecondDateValue.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                }
                            } else {
                                isValidDate=true;
//                                    if(isFirstdate) {
                                txtFirstDateValue.setTextColor(getResources().getColor(android.R.color.black));

//                                    } else {
                                txtSecondDateValue.setTextColor(getResources().getColor(android.R.color.black));

//                                    }



                            }
                            if (!((etNumberOfPerformanceValue.getText().toString() == null || etNumberOfPerformanceValue.getText().toString() == "" || etNumberOfPerformanceValue.getText().toString().isEmpty()) || (txtFirstDateValue.getText().toString() == null || txtFirstDateValue.getText().toString() == "" || txtFirstDateValue.getText().toString().isEmpty()) || (txtSecondDateValue.getText().toString() == null || txtSecondDateValue.getText().toString() == "" || txtSecondDateValue.getText().toString().isEmpty()))) {
                                if(numberOfPerformance==true && isValidDate==true ) {
                                    btnPlayOrder.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
                                    btnPlayOrder.setEnabled(true);
                                }else {
                                    btnPlayOrder.setBackgroundColor(getResources().getColor(R.color.gray_color));
                                    btnPlayOrder.setEnabled(false);
                                }
//                                    Log.e("",numberOfPerformance+" "+isValidDate);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }



                });

        datePickerdialog.show();

    }



    private void orderPlayForPerformance() {
        final JSONObject params=new JSONObject();

        try {
            params.put("PlayId",selectedPlay.getPlayId()+"");
            params.put("UserId",currentUser.getUserId()+"");
            params.put("SchoolId",currentUser.getDomain()+"");
            params.put("NumberOfPerformances",etNumberOfPerformanceValue.getText().toString()+"");
            params.put("NumberOfAuditions",rehersalBool+"");
            params.put("PerformDateFirst",firstDate.getTime()/1000+"");
            params.put("PerformDateLast",seocndDate.getTime()/1000+"");
            params.put("Comments","");
//            Log.e("params: ", params + "");

            boolean hasBeenOrderedForReviewBefore = (selectedPlay.getOrderId() != null) && (!selectedPlay.getOrderId().equals(""));
            if (hasBeenOrderedForReviewBefore) {
                params.put("PlayOrderId",selectedPlay.getOrderId()+"");
            }

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "http://api.danteater.dk/api/PlayOrderPerform", params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {

                    String res = jobj.toString();
//                    Log.e("response: ", res + "");
                    beanOrderPlayReview = new GsonBuilder()
                            .create().fromJson(res, BeanOrderPlayReview.class);

                    selectedPlay.OrderId = beanOrderPlayReview.PlayOrderId;


                    if(dbHelper.hasPlayWithPlayOrderIdText(selectedPlay.OrderId)) {

                        new AsyncTask<Void,Void,Void>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();

                            }

                            @Override

                            protected Void doInBackground(Void... voids) {


                                selectedPlay.OrderType = "Perform";

                                dbHelper.updatePlayInfo(selectedPlay);



//                        Log.e("update","updated successfully");
                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);


                            }
                        }.execute();







                    } else {

                        new AsyncTask<Void,Void,Void>() {
                            @Override
                            protected void onPreExecute() {
                                super.onPreExecute();

                            }

                            @Override

                            protected Void doInBackground(Void... voids) {
                                retrievePlayContentsForPlayOrderId();

                                return null;
                            }

                            @Override
                            protected void onPostExecute(Void aVoid) {
                                super.onPostExecute(aVoid);

                                dialog_next.dismissWithStatus(R.drawable.ic_navigation_accept,"Stykker bestilt");

                                new CountDownTimer(2500, 1000) {

                                    @Override
                                    public void onFinish() {


                                        ReadActivityFromPreview.isBackFromOrder = true;
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



//                Log.e("Response play full:", response + "");
                Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                DatabaseWrapper db = new DatabaseWrapper(OrderPlayActivityForPerformNew.this);
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
//            Log.e("total users: ", shareWithUsersArrayN + "");

        }

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {

                try

                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/" + selectedPlay.OrderId, shareWithUsersArrayN.toString());
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

                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName() + " (lærer)";
//                    nameToBeSaved = currentUser.getFirstName() + " " + currentUser.getLastName();
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
//            Log.e("total users: ", shareWithUsersArray + "");
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/"+selectedPlay.OrderId, shareWithUsersArray.toString());
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return super.onCreateOptionsMenu(menu);
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
