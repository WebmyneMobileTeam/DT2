package wm.com.danteater.my_plays;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.MenuItem;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;

import wm.com.danteater.tab_share.ShareFragment;

/**
 * Created by nirav on 11-09-2014.
 */
public class ShareActivityForPerform extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_for_perform);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ShareFragment fragmentShare = ShareFragment.newInstance("", "");

            ft.replace(R.id.main_content, fragmentShare, "share_perform").commit();


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
