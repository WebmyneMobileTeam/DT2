package wm.com.danteater.my_plays;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.tab_read.ReadFragment;

public class ReadActivityFromPreview extends BaseActivity {

    public WMTextView txtHeader;
    private FrameLayout btnPlayOrderIdForPerformance;
    private Play play;
    int current_state = 1;
    private boolean isFromLogin=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_read_from_preview);
        current_state = getIntent().getExtras().getInt("currentState");
        isFromLogin= getIntent().getExtras().getBoolean("isFromLogin");
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ReadActivityFromPreview.this, "mypref", 0);
        play = complexPreferences.getObject("selected_play", Play.class);
        Log.e("Count : ",""+play.playLinesList.size());
        btnPlayOrderIdForPerformance=(FrameLayout)findViewById(R.id.btnPlayOrderIdForPerformance);
        if(isFromLogin==true) {
            btnPlayOrderIdForPerformance.setVisibility(View.GONE);
        } else {
            btnPlayOrderIdForPerformance.setVisibility(View.VISIBLE);
        }

        btnPlayOrderIdForPerformance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(ReadActivityFromPreview.this,OrderPlayActivityForPerformNew.class);
                startActivity(i);
            }
        });
        setUpCurrentActionBar();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Bundle args = new Bundle();
        args.putInt("currentState",current_state);
        ReadFragment readFragment = ReadFragment.newInstance("","");
        readFragment.setArguments(args);

        ft.replace(R.id.containerPreview,readFragment,"preview");
        ft.commit();



    }




    private void setUpCurrentActionBar() {

        ActionBar.LayoutParams acBarParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        acBarParams.gravity= Gravity.LEFT| Gravity.CENTER_VERTICAL;

        // Adding the new custom textview to the actionbar that has center alined property.

        txtHeader=new WMTextView(this);

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        getActionBar().setDisplayShowCustomEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        txtHeader.setTextColor(Color.BLACK);
        txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        getActionBar().setCustomView(txtHeader, acBarParams);
        txtHeader.setTypeface(Typeface.createFromAsset(getAssets(), "helvetica.ttf"));
        txtHeader.setText("Mine Stykker");


        txtHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){


            case android.R.id.home:

                finish();

                break;


        }


        return super.onOptionsItemSelected(item);
    }
}
