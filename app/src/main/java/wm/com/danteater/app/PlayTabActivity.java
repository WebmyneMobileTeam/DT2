package wm.com.danteater.app;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;

import wm.com.danteater.R;
import wm.com.danteater.customviews.WMImageView;
import wm.com.danteater.excercise.FragmentExcerciseForTeacher;
import wm.com.danteater.login.User;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.tab_inspiration.FragmnentInspirationForTeacher;
import wm.com.danteater.tab_music.MusicFragment;
import wm.com.danteater.tab_read.ReadFragment;


/**
 * Tab activity that inclide these following tabs functionality
 *
 *  1. Info
 *  2. Music
 *  3. Share
 *  4. Read
 *  5. Record
 *
 */

public class PlayTabActivity extends BaseActivity {

    public static int STATE_RECORD = 0;
    public static int STATE_PREVIEW = 1;
    public static int STATE_READ = 2;
    public static int STATE_CHAT = 3;

    private FragmentTabHost mTabHost;
  //  private String type_navigation;
  //  private String playinfo;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_tab);

       // Receive the extra string for specific tab selection.
    //    Intent i=getIntent();
    //    playinfo =i.getStringExtra("infoData");
    //    type_navigation = i.getStringExtra("type_navigation");


        // Helping to create a tabs to this application
        initTabHost();
        mTabHost.setCurrentTab(0);
        selectTAB(0);


        // Check weather the received extra string is read or share to select corresponding tabs;
      /*  if(type_navigation.equalsIgnoreCase("Read")){



        }else if(type_navigation.equalsIgnoreCase("Share")){

            mTabHost.setCurrentTab(2);
            selectTAB(2);
        }*/



        // Changing image and selecting functionality when tabs is changed.

        mTabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {

                for(int i=0;i<mTabHost.getTabWidget().getTabCount();i++){
                    selectTAB(i);
                }

            }
        });


    }

    private void initTabHost() {

        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(PlayTabActivity.this, "user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);

        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);

        View view2 = getLayoutInflater().inflate(R.layout.item_tab_music,null,false);
        View view3 = getLayoutInflater().inflate(R.layout.item_tab,null,false);
        View view4 = getLayoutInflater().inflate(R.layout.item_tab_read,null,false);
        View view5 = getLayoutInflater().inflate(R.layout.item_tab_microphone,null,false);
        TextView tv=(TextView)view2.findViewById(R.id.txtTab);
        tv.setTextColor(Color.BLACK);
        TextView tv1=(TextView)view3.findViewById(R.id.txtTab);
        tv1.setTextColor(Color.BLACK);
        TextView tv2=(TextView)view4.findViewById(R.id.txtTab);
        tv2.setTextColor(Color.BLACK);
        TextView tv3=(TextView)view5.findViewById(R.id.txtTab);
        tv3.setTextColor(Color.BLACK);

     //   Bundle bInfo=new Bundle();
     //   bInfo.putString("infoData",playinfo+"");


        Bundle args = new Bundle();
        args.putInt("currentState",STATE_READ);

        mTabHost.addTab(mTabHost.newTabSpec("tab4").setIndicator(view4),
                ReadFragment.class,args);


        args = new Bundle();
        args.putInt("currentState",STATE_RECORD);

        mTabHost.addTab(mTabHost.newTabSpec("tab5").setIndicator(view5),
                ReadFragment.class, args);

        mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator(view2),
                MusicFragment.class, null);


        if(user.checkTeacherOrAdmin(user.getRoles())){
            mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator(view3),
                    FragmnentInspirationForTeacher.class, null);
        }else{

        }




    }

    public void resetHeader(String text){

        txtHeader.setText(text);

    }

    private void selectTAB(int i) {

        View v = mTabHost.getTabWidget().getChildAt(i);
        WMImageView iv = (WMImageView)v.findViewById(R.id.imgTab);
        TextView tv=(TextView)v.findViewById(R.id.txtTab);

        if(i==mTabHost.getCurrentTab()){

            iv.selected();
            v.setBackgroundColor(getResources().getColor(R.color.greenTheme));
            tv.setTextColor(Color.WHITE);
        }else{

            iv.normal();
            v.setBackgroundResource(R.drawable.gradient_bg);
            tv.setTextColor(Color.BLACK);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.play_tab, menu);
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

    @Override
    protected void onPause() {
        super.onPause();

        if(MusicFragment.mediaPlayer !=null && MusicFragment.mediaPlayer.isPlaying()){
            MusicFragment.mediaPlayer.pause();
            MusicFragment.mediaPlayer.stop();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(MusicFragment.mediaPlayer !=null && MusicFragment.mediaPlayer.isPlaying()) {
            MusicFragment.mediaPlayer.pause();
            MusicFragment.mediaPlayer.stop();
        }
    }
}
