package wm.com.danteater.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;

import wm.com.danteater.R;
import wm.com.danteater.customviews.WMImageView;
import wm.com.danteater.tab_info.InfoFragment;
import wm.com.danteater.tab_music.MusicFragment;
import wm.com.danteater.tab_read.ReadFragment;
import wm.com.danteater.tab_recording.RecordingFragment;
import wm.com.danteater.tab_share.ShareFragment;

public class PlayTabActivity extends BaseActivity {

    private FragmentTabHost mTabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tab);


        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        View view = getLayoutInflater().inflate(R.layout.item_tab_info,null,false);
        View view2 = getLayoutInflater().inflate(R.layout.item_tab_music,null,false);
        View view3 = getLayoutInflater().inflate(R.layout.item_tab,null,false);
        View view4 = getLayoutInflater().inflate(R.layout.item_tab_read,null,false);
        View view5 = getLayoutInflater().inflate(R.layout.item_tab_microphone,null,false);

        mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator(view),
                InfoFragment.class, null);

        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(view2),
                MusicFragment.class, null);


        mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator(view3),
                ShareFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator(view4),
                ReadFragment.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("tab5").setIndicator(view5),
                RecordingFragment.class, null);




        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for(int i=0;i<mTabHost.getTabWidget().getTabCount();i++){

                    View v = mTabHost.getTabWidget().getChildAt(i);
                    WMImageView iv = (WMImageView)v.findViewById(R.id.imgTab);

                    if(i==mTabHost.getCurrentTab()){

                        iv.selected();


                    }else{

                        iv.normal();
                    }

                }

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play_tab, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
