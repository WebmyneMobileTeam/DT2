package wm.com.dt.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import wm.com.dt.app.BaseActivity;
import wm.com.dt.R;
import wm.com.dt.my_plays.DrawerActivity;

public class LoginActivity extends BaseActivity{

    private TextView txtLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txtHeader.setText("Login");

        txtLogin=(TextView)findViewById(R.id.txtLogin);
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(LoginActivity.this, DrawerActivity.class);
                startActivity(i);
            }
        });
    }

}
