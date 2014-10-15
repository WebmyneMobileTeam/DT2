package wm.com.danteater.Messages;

import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.my_plays.ReadActivityFromPreview;
import wm.com.danteater.tab_read.ReadFragment;

/**
 * Created by nirav on 14-10-2014.
 */
public class ReadActivityForChat extends BaseActivity {
    int currentState = 3;
    private Play play;
    private int lineNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_for_chat);
        currentState = getIntent().getExtras().getInt("currentState");
        lineNumber= getIntent().getExtras().getInt("line_number");
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ReadActivityForChat.this, "mypref", 0);
        play = complexPreferences.getObject("selected_play", Play.class);
        setUpCurrentActionBar();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        Bundle args = new Bundle();
        args.putInt("currentState",currentState);
        args.putInt("line_number",lineNumber);
        ReadFragment readFragment = ReadFragment.newInstance("","");
        readFragment.setArguments(args);

        ft.replace(R.id.containerPreviewForChat,readFragment,"chat");
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
