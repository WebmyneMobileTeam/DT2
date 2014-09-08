package wm.com.danteater.app;


import android.app.ActionBar;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.view.Gravity;

import wm.com.danteater.customviews.WMTextView;

/**
 * Created by nirav on 25-08-2014.
 * Base activity for application.
 * Contains basic actionbar integration for all the screen which extends this Activity.
 */
public class BaseActivity extends FragmentActivity {

    public WMTextView txtHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);



        // ActionBar Style
        ActionBar.LayoutParams acBarParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        acBarParams.gravity= Gravity.CENTER| Gravity.CENTER_VERTICAL;


        // Adding the new custom textview to the actionbar that has center alined property.

        txtHeader=new WMTextView(this);

        getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActionBar().setIcon(new ColorDrawable(Color.TRANSPARENT));
        getActionBar().setDisplayShowTitleEnabled(false);
        getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        getActionBar().setDisplayShowCustomEnabled(true);

        txtHeader.setTextColor(Color.BLACK);
        txtHeader.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
        getActionBar().setCustomView(txtHeader, acBarParams);
        txtHeader.setTypeface(Typeface.createFromAsset(getAssets(),"helvetica.ttf"),Typeface.BOLD);

    }
}
