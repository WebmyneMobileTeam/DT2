package wm.com.dt.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import wm.com.app.BaseActivity;
import wm.com.dt.R;
import wm.com.dt.my_plays.MyPlays;

public class LoginActivity extends BaseActivity{

    // Added comment by dhruvil
    // nirav changes

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
