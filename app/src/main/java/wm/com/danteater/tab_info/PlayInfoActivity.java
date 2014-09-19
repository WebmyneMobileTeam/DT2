package wm.com.danteater.tab_info;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.AttributeSet;
import android.view.MenuItem;
import android.view.View;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.search.BeanSearch;

/**
 * Created by nirav on 19-09-2014.
 */
public class PlayInfoActivity extends BaseActivity {
   BeanSearch beanSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_info_view);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(PlayInfoActivity.this, "search_result_play",0);
        beanSearch=complexPreferences.getObject("searched_play", BeanSearch.class);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager manager =getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        InfoFragment infoFragment = InfoFragment.newInstance("", "");

        if (manager.findFragmentByTag("infoFragment") == null) {
            ft.replace(R.id.main_content, infoFragment, "infoFragment").commit();
        }
        txtHeader.setText(beanSearch.Title);
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
