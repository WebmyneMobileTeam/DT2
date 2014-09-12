package wm.com.danteater.login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mvnordic.mviddeviceconnector.DeviceSecurity;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.guide.GuideStartup;
import wm.com.danteater.my_plays.DrawerActivity;

public class LoginActivity extends BaseActivity{

    private TextView txtLogin;
    DeviceSecurity m_device_security = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtHeader.setText("Login");
        txtLogin=(TextView)findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (isFirstTime()) {

                    Intent i=new Intent(LoginActivity.this, GuideStartup.class);
                    startActivity(i);


                } else {

                    Intent i=new Intent(LoginActivity.this, DrawerActivity.class);
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

        m_device_security.doLogin("product.ios.da.intowords",R.id.fragment_layout);

    }

    private boolean isFirstTime()
    {
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

            if (response.has_access==false) {
                m_device_security.releaseDeviceRegistration();
            }

            String session_id = m_device_security.getMVSessionID(response.access_identifier);





        }

    };

}
