package wm.com.dt.app;


import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import wm.com.dt.customviews.WMTextView;

/**
 * Created by nirav on 25-08-2014.
 */
public class BaseActivity extends Activity {

    public WMTextView txtHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // ActionBar Style
        ActionBar.LayoutParams acBarParams = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.WRAP_CONTENT,
                ActionBar.LayoutParams.WRAP_CONTENT);
        acBarParams.gravity= Gravity.CENTER| Gravity.CENTER_VERTICAL;
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
        //




    }
}
