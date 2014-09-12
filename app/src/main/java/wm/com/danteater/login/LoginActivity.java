package wm.com.danteater.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import wm.com.danteater.guide.GuideStartup;
import wm.com.danteater.model.AppConstants;
import wm.com.danteater.my_plays.DrawerActivity;

public class LoginActivity extends BaseActivity {

    private TextView txtLogin;
    DeviceSecurity m_device_security = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtHeader.setText("Login");
        txtLogin = (TextView) findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isFirstTime()) {

                    Intent i = new Intent(LoginActivity.this, GuideStartup.class);
                    startActivity(i);


                } else {

                    Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
                    startActivity(i);
                }


            }
        });


        proceedLogin();


    }

    private void proceedLogin() {


        m_device_security = new DeviceSecurity(this);
        m_device_security.addDeviceSecurityListener(device_security_listener);

        m_device_security.setApplicationCountryCode("IntoWords_dk");
        m_device_security.excludeLoginGroup("company");
        m_device_security.excludeLoginGroup("private");

        m_device_security.doLogin("product.ios.da.intowords", R.id.fragment_layout);

    }

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

    private DeviceSecurity.DeviceSecurityListener device_security_listener = new DeviceSecurity.DeviceSecurityListener() {

        @Override
        public void onMVIDResponseReady(MVIDResponse response) {
           /* String toast = String.format("AI: %s\nREQUEST_ID: %s\nGOT ACCESS: %s",
                    response.access_identifier,
                    response.request_id,
                    response.has_access ? "YES" : "NO" );
            Toast.makeText(getBaseContext(), toast,
                    Toast.LENGTH_SHORT).show();*/

            if (response.has_access == false) {
                m_device_security.releaseDeviceRegistration();
            }

            String session_id = m_device_security.getMVSessionID(response.access_identifier);
            if (session_id == "" || session_id == null) {

            } else {
                callWebServicePost(session_id);
            }
        }


        private void callWebServicePost(String session_id) {


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
                    Log.e("response: ",res+"");
                    handlePostsList(res);

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {

                }
            });
            MyApplication.getInstance().addToRequestQueue(req);
        }


    };

    private void handlePostsList(final String response) {

        runOnUiThread(new Runnable() {
            public void run() {

                try {

                    Intent intent=null;
                    BeanUserResult beanCustomerInfo =new GsonBuilder().create().fromJson(response, BeanUserResult.class);
                    BeanUserInfo beanUserInfo=beanCustomerInfo.getBeanUserResult();
                    BeanUser beanUser=beanUserInfo.getBeanUser();

                    Log.e("user_id: ",beanUser.getUserId()+"");
                    Log.e("first_name: ",beanUser.getFirstName()+"");
                    Log.e("last_name: ",beanUser.getLastName()+"");
                    Log.e("primary_group: ",beanUser.getPrimaryGroup()+"");
                    Log.e("roles: ",beanUser.getRoles()+"");


                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                } catch (JsonIOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
