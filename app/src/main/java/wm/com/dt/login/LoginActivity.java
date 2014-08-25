package wm.com.dt.login;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import wm.com.app.BaseActivity;
import wm.com.dt.R;
import wm.com.dt.my_plays.MyPlays;

import android.view.View;
import android.widget.TextView;

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
                Intent i=new Intent(LoginActivity.this, MyPlays.class);
                startActivity(i);
            }
        });
    }

}
