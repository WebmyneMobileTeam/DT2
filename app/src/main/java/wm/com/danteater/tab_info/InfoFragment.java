package wm.com.danteater.tab_info;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.search.BeanSearch;
import wm.com.danteater.tab_inspiration.Inspiration;
import wm.com.danteater.tab_inspiration.InspirationDetailsView;


public class InfoFragment extends Fragment {

    private String Synopsis;
    BeanSearch beanSearch;
    RelativeLayout orderForPreview;
    private User currentUser;
    private Reader reader;
    private BeanOrderPlayReview beanOrderPlayReview;
    private String playOrderIdStr, playOrderError;
    private HUD dialog;
    private boolean wasPlayAlreadyOrderedForPreview = false;
    GridLayout gridInspiration;
    private ArrayList<Inspiration> inspirations;

    public static InfoFragment newInstance(String param1, String param2) {
        InfoFragment fragment = new InfoFragment();

        return fragment;
    }

    public InfoFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "search_result_play", 0);
        beanSearch = complexPreferences.getObject("searched_play", BeanSearch.class);
       Synopsis = beanSearch.Synopsis;
        ComplexPreferences complexPreferencesForUser = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser = complexPreferences.getObject("current_user", User.class);
    }

    @Override
    public void onResume() {
        super.onResume();
        showInspirations();
    }

    private void showInspirations() {



        inspirations = new ArrayList<Inspiration>();
        inspirations.clear();
        gridInspiration.removeAllViews();


        final HUD dialog = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Henter inspirationer");
        dialog.show();


        new CallWebService("http://api.danteater.dk/api/Inspiration/"+beanSearch.PlayId,CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {

                Log.e("Response from inspiration ",response);

                Type listType = new TypeToken<List<Inspiration>>() {}.getType();
                inspirations = new GsonBuilder().create().fromJson(response,listType);
                dialog.dismiss();
                fillInspirations();
            }

            @Override
            public void error(VolleyError error) {
                error.printStackTrace();
                dialog.dismiss();

            }
        }.start();








    }

    private void fillInspirations() {


        int w = getResources().getDisplayMetrics().widthPixels;

        for(int i=0;i<inspirations.size();i++){

            final int showInsp = i;
            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(new ViewGroup.MarginLayoutParams(w / 2, w / 4));
            final ImageView ivi = new ImageView(getActivity());
            ivi.setScaleType(ImageView.ScaleType.CENTER);
            ivi.setImageResource(R.drawable.camerax);
            gridInspiration.addView(ivi,layoutParams);

            ivi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    InspirationDetailsView inspView = new InspirationDetailsView(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                    inspView.setTypeView();
                    inspView.setupExistingInspiration(inspirations.get(showInsp));
                    inspView.show();




                }
            });
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        WebView webview = (WebView) view.findViewById(R.id.infoPage);
//        WebSettings settings = webview.getSettings();
//        settings.setDefaultTextEncodingName("utf-8");
        orderForPreview = (RelativeLayout) view.findViewById(R.id.orderForPreview);

        gridInspiration = (GridLayout)view.findViewById(R.id.gridInspiration);

        orderForPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
                        dialog.title("Gemmer til Mine stykker");
                        dialog.show();
                    }

                    @Override
                    protected Void doInBackground(Void... voids) {
                        OrderPlayForReview();
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        if (wasPlayAlreadyOrderedForPreview) {
                            dialog.dismissWithStatus(R.drawable.ic_navigation_accept, "Du har allerede gemt dette manuskript." + "\n" + "Find det i Mine stykker under MENU.");
                        } else {
                            dialog.dismissWithStatus(R.drawable.ic_navigation_accept, "Gemt til Mine stykker");
                        }
                        new CountDownTimer(2500, 1000) {

                            @Override
                            public void onFinish() {
                                getActivity().finish();

                            }

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }
                        }.start();


                    }

                }.execute();

            }
        });

        webview.loadData(Synopsis,  "text/html; charset=utf-8", "utf-8");
        return view;
    }

    private void OrderPlayForReview() {

        JSONObject params = new JSONObject();
        try {
            params.put("PlayId", beanSearch.PlayId);
            params.put("UserId", currentUser.getUserId());
//            Log.e("params: ", params + "");
            reader = API.callWebservicePost("http://api.danteater.dk/api/PlayOrderReview", params.toString());
//            Type listType = new TypeToken<List<BeanSearch>>() {
//            }.getType();
            beanOrderPlayReview = new GsonBuilder()
                    .create().fromJson(reader, BeanOrderPlayReview.class);

            playOrderIdStr = beanOrderPlayReview.PlayOrderId;
            playOrderError = beanOrderPlayReview.OrderError;


            if ((playOrderError == null) || (playOrderError.equals(""))) {
                wasPlayAlreadyOrderedForPreview = false;
            } else {
                wasPlayAlreadyOrderedForPreview = true;
            }
            retrievePlayContentsForPlayOrderId();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void retrievePlayContentsForPlayOrderId() {

        new CallWebService("http://api.danteater.dk/api/playfull/" + beanSearch.OrderId, CallWebService.TYPE_JSONOBJECT) {

            @Override
            public void response(final String response) {

//                Log.e("Response play full:", response + "");


                Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                DatabaseWrapper db = new DatabaseWrapper(getActivity());
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
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/" + beanSearch.OrderId, shareWithUsersArrayN.toString());
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

                    // TODO can't add "(lærer)"
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
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/"+beanSearch.OrderId, shareWithUsersArray.toString());
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


        }.execute();

    }


}
