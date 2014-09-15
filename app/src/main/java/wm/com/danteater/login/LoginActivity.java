package wm.com.danteater.login;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.mvnordic.mviddeviceconnector.DeviceSecurity;
import org.json.JSONObject;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.guide.GuideStartup;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.my_plays.DrawerActivity;

public class LoginActivity extends BaseActivity {


    DeviceSecurity m_device_security = null;
    boolean shouldShowLoginView;
    private LinearLayout loginView, noAccessView;
    private RelativeLayout noNetworkView;
    boolean isTeacherOrAdmin;
    private WMTextView txtBottomLabel;
    BeanUser beanUser;
    WMTextView txtTryAgain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtHeader.setText("Login");

        // Alternative Views
        loginView = (LinearLayout) findViewById(R.id.LoginView);
        noAccessView = (LinearLayout) findViewById(R.id.noAccessView);
        noNetworkView = (RelativeLayout) findViewById(R.id.noNetworkView);

        txtBottomLabel = (WMTextView) findViewById(R.id.txtBottomLabel);
        txtTryAgain = (WMTextView) findViewById(R.id.txtTryAgain);

        // get login value from setting activity
        // true- automatic login
        // false- manual login
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        shouldShowLoginView = preferences.getBoolean("shouldShowLoginView", false);

        // automatic login is on
        if (shouldShowLoginView == true) {
            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
            startActivity(i);
            finish();
        } else {
            // automatic login is off
            proceedLogin();
        }
    }

    private void proceedLogin() {
        m_device_security = new DeviceSecurity(this);
        m_device_security.addDeviceSecurityListener(device_security_listener);

        m_device_security.setApplicationCountryCode("IntoWords_dk");
        m_device_security.excludeLoginGroup("company");
        m_device_security.excludeLoginGroup("private");
        // if automatic login is off, release the device registration
        if (shouldShowLoginView == false) {

            m_device_security.releaseDeviceRegistration();
        }
        // try to login
        if (isConnected()) {
            // TODO change to login with android
            m_device_security.doLogin("product.ios.da.intowords", R.id.fragment_layout);
            loginView.setVisibility(View.VISIBLE);
            noAccessView.setVisibility(View.GONE);
            noNetworkView.setVisibility(View.GONE);
        } else {
            m_device_security.doLogin("product.ios.da.intowords", R.id.fragment_layout);
            loginView.setVisibility(View.GONE);
            noAccessView.setVisibility(View.GONE);
            noNetworkView.setVisibility(View.GONE);
        }
    }


    private DeviceSecurity.DeviceSecurityListener device_security_listener = new DeviceSecurity.DeviceSecurityListener() {
        @Override
        public void onMVIDResponseReady(MVIDResponse response) {
            if (response.has_access == false) {
                m_device_security.releaseDeviceRegistration();
            }
            String session_id = m_device_security.getMVSessionID(response.access_identifier);
            if (session_id == "" || session_id == null) {
                m_device_security.releaseDeviceRegistration();

                // no internet connection
                // shows no network view
                //TODO open from database (Last visited play)

                txtTryAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (isConnected()) {
                            m_device_security.doLogin("product.ios.da.intowords", R.id.fragment_layout);
                            loginView.setVisibility(View.VISIBLE);
                            noAccessView.setVisibility(View.GONE);
                            noNetworkView.setVisibility(View.GONE);
                        }
                    }
                });

                showDialog("Intet netværk","Login-serveren kunne ikke kontaktes. Tjek venligst dine netværksindstillinger, og prøv at logge ind igen.");


                loginView.setVisibility(View.GONE);
                noAccessView.setVisibility(View.GONE);
                noNetworkView.setVisibility(View.VISIBLE);
            } else {

                loginView.setVisibility(View.VISIBLE);
                noAccessView.setVisibility(View.GONE);
                noNetworkView.setVisibility(View.GONE);

                // save session id in shared preferences
                SharedPreferences preferences = getSharedPreferences("session_id", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("seesion_id", session_id);
                editor.commit();

                // post webservice
                callWebServicePost(session_id, response);
            }
        }

        private void callWebServicePost(String session_id, final MVIDResponse mvid_response) {

            JSONObject params = new JSONObject();
            JSONObject request_params = new JSONObject();
            try {
                params.put("session_id", session_id);
                params.put("lookup_primary_group", true);
                params.put("extract_userroles", true);
                request_params.put("methodname", "whoami");
                request_params.put("type", "jsonwsp/request");
                request_params.put("version", "1.0");
                request_params.put("args", params);
            } catch (Exception e) {
                e.printStackTrace();
            }

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/UserService/jsonwsp", request_params, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {
                    String res = jobj.toString();
                    Log.e("response: ", res + "");
                    handlePostsList(res, mvid_response);
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }
            });
            MyApplication.getInstance().addToRequestQueue(req);
        }
    };

    private void handlePostsList(final String response, final DeviceSecurity.DeviceSecurityListener.MVIDResponse mvid_response) {

        runOnUiThread(new Runnable() {
            public void run() {

                try {
                    Intent intent = null;
                    BeanUserResult beanCustomerInfo = new GsonBuilder().create().fromJson(response, BeanUserResult.class);
                    BeanUserInfo beanUserInfo = beanCustomerInfo.getBeanUserResult();
                    beanUser = beanUserInfo.getBeanUser();

                    Log.e("user_id: ", beanUser.getUserId() + "");
                    Log.e("first_name: ", beanUser.getFirstName() + "");
                    Log.e("last_name: ", beanUser.getLastName() + "");
                    Log.e("primary_group: ", beanUser.getPrimaryGroup() + "");
                    Log.e("roles: ", beanUser.getRoles() + "");
                    Log.e("domain: ", beanUser.getDomain() + "");
                    isTeacherOrAdmin = beanUser.checkTeacherOrAdmin(beanUser.getRoles());

                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }

                //store current user and domain in shared preferences
                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(LoginActivity.this, "user_pref", 0);
                complexPreferences.putObject("current_user", beanUser);
                complexPreferences.commit();

                // if logged in user is teacher or admin and has no access
                if (!mvid_response.has_access && isTeacherOrAdmin) {

                    loginView.setVisibility(View.GONE);
                    noAccessView.setVisibility(View.VISIBLE);
                    noNetworkView.setVisibility(View.GONE);


                    txtBottomLabel.setText(Html.fromHtml("Ring til MV-Nordic på 6591 8022 eller klik <a href=\"https://www.mv-nordic.com/dk/produkter/teater-aftale\\\">her</a> for at læse mere."));
                    txtBottomLabel.setMovementMethod(LinkMovementMethod.getInstance());
                } else {

                    loginView.setVisibility(View.VISIBLE);
                    noAccessView.setVisibility(View.GONE);
                    noNetworkView.setVisibility(View.GONE);
                    //TODO start login timer
                    //TODO retrive school teacher
                    //TODO retrive school classes
                    //TODO update contents of navigation drawer

                    // go to next screen
                    if (isFirstTime()) { // show guide pages
                        Intent i = new Intent(LoginActivity.this, GuideStartup.class);
                        startActivity(i);
                        finish();
                    } else {    // show home screen
                        Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                        startActivity(i);
                        finish();
                    }
                }
            }
        });
    }

    // check user is login first time or not
    private boolean isFirstTime() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        boolean ranBefore = preferences.getBoolean("RanBefore", false);
        if (!ranBefore) {
            // first time
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("RanBefore", true);
            editor.commit();
        }
        return !ranBefore;
    }


    private void showDialog( String title,String message ) {
        AlertDialog.Builder alert = new AlertDialog.Builder(LoginActivity.this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                m_device_security.releaseDeviceRegistration();
            }
        });
        alert.show();
    }
}
