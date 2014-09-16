package wm.com.danteater.my_plays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import wm.com.danteater.Messages.FragmentMessage;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.guide.FragmentGuide;
import wm.com.danteater.inspiration.FragmentInspiration;
import wm.com.danteater.login.BeanUser;
import wm.com.danteater.login.LoginActivity;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.search.FragmentSearch;
import wm.com.danteater.settings.SettingsFragment;

/**
 * Created by nirav on 25-08-2014.
 */
public class DrawerActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawer;
    private ListView leftDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private BeanUser beanUser;
    private String[] leftSliderDataForTeacher = {"Søg", "Mine stykker", "Beskeder", "Dramaøvelser", "Indstillinger", "Hjælp"};
    private String[] leftSliderDataForStudent = {"Mine stykker", "Beskeder", "Dramaøvelser", "Indstillinger",};
    private boolean isPupil;
    private WMTextView logoutImage, logoutUser, logoutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plays);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(DrawerActivity.this, "user_pref", 0);
        beanUser = complexPreferences.getObject("current_user", BeanUser.class);
        isPupil = beanUser.checkPupil(beanUser.getRoles());

        logoutImage = (WMTextView) findViewById(R.id.logoutImage);
        logoutButton = (WMTextView) findViewById(R.id.logoutButton);
        logoutUser = (WMTextView) findViewById(R.id.logoutUser);

        //Load My Places First
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if(isPupil) {
            FragmentMyPlayPupil fragmentMyPlayPupil = FragmentMyPlayPupil.newInstance("", "");

            if (manager.findFragmentByTag("my_plays_pupil") == null) {
                ft.replace(R.id.main_content, fragmentMyPlayPupil, "my_plays_pupil").commit();
            }
        } else {
            FragmentMyPlay fragmentMyPlay = FragmentMyPlay.newInstance("", "");

            if (manager.findFragmentByTag("my_plays") == null) {
                ft.replace(R.id.main_content, fragmentMyPlay, "my_plays").commit();
            }
        }

        // header
        txtHeader.setText("Mine Stykker");


        initFields();
        initDrawer();

    }

    private void initFields() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        if (isPupil) {
            leftDrawerList.setAdapter(new NavigationDrawerAdapter(DrawerActivity.this, leftSliderDataForStudent));
        } else {
            leftDrawerList.setAdapter(new NavigationDrawerAdapter(DrawerActivity.this, leftSliderDataForTeacher));
        }

        leftDrawerList.setOnItemClickListener(this);

    }

    private void initDrawer() {

        //logout contents
        logoutUser.setText(beanUser.getFirstName().toString()+" "+beanUser.getLastName().toString());
        logoutImage.setText(beanUser.getFirstName().toString().substring(0, 2));
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO clear other data like messages or other records...
                LoginActivity.m_device_security.releaseDeviceRegistration();
                Intent i = new Intent(DrawerActivity.this,LoginActivity.class );
                startActivity(i);
                finish();

            }
        });

        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(DrawerActivity.this, drawer, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer) {

            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {

            }

        };
        drawer.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        // Add your onclick logic here
        drawer.closeDrawers();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        //drawer items for pupil
        if (isPupil) {
            switch (position) {
                case 0:
                    FragmentMyPlayPupil fragmentMyPlayPupil = FragmentMyPlayPupil.newInstance("", "");

                    if (manager.findFragmentByTag("my_plays_pupil") == null) {
                        ft.replace(R.id.main_content, fragmentMyPlayPupil, "my_plays_pupil").commit();
                    }
                    txtHeader.setText("Mine stykker");
                    break;

                case 1:
                    FragmentMessage fragmentMessage = FragmentMessage.newInstance("", "");
                    if (manager.findFragmentByTag("message") == null) {
                        ft.replace(R.id.main_content, fragmentMessage, "message").commit();
                    }
                    txtHeader.setText("Beskeder");
                    break;

                case 2:
                    FragmentInspiration fragmentInspiration = FragmentInspiration.newInstance("", "");
                    if (manager.findFragmentByTag("Inspiration") == null) {
                        ft.replace(R.id.main_content, fragmentInspiration, "Inspiration").commit();
                    }
                    txtHeader.setText("Dramaøvelser");
                    break;

                case 3:
                    SettingsFragment fragmentSettings = SettingsFragment.newInstance("", "");
                    if (manager.findFragmentByTag("Settings") == null) {
                        ft.replace(R.id.main_content, fragmentSettings, "Settings").commit();
                    }
                    txtHeader.setText("Indstillinger");
                    break;
            }

        } else { //drawer items for teacher
            switch (position) {
                case 0:
                    FragmentSearch fragmentSearch = FragmentSearch.newInstance("", "");
                    if (manager.findFragmentByTag("search") == null) {
                        ft.replace(R.id.main_content, fragmentSearch, "search").commit();
                    }
                    txtHeader.setText("Søg skuespil");
                    break;

                case 1:
                    FragmentMyPlay fragmentMyPlay = FragmentMyPlay.newInstance("", "");
                    if (manager.findFragmentByTag("my_plays") == null) {
                        ft.replace(R.id.main_content, fragmentMyPlay, "my_plays").commit();
                    }
                    txtHeader.setText("Mine stykker");
                    break;

                case 2:
                    FragmentMessage fragmentMessage = FragmentMessage.newInstance("", "");
                    if (manager.findFragmentByTag("message") == null) {
                        ft.replace(R.id.main_content, fragmentMessage, "message").commit();
                    }
                    txtHeader.setText("Beskeder");
                    break;

                case 3:
                    FragmentInspiration fragmentInspiration = FragmentInspiration.newInstance("", "");
                    if (manager.findFragmentByTag("Inspiration") == null) {
                        ft.replace(R.id.main_content, fragmentInspiration, "Inspiration").commit();
                    }
                    txtHeader.setText("Dramaøvelser");
                    break;

                case 4:
                    SettingsFragment fragmentSettings = SettingsFragment.newInstance("", "");
                    if (manager.findFragmentByTag("Settings") == null) {
                        ft.replace(R.id.main_content, fragmentSettings, "Settings").commit();
                    }
                    txtHeader.setText("Indstillinger");
                    break;


                case 5:
                    FragmentGuide fragmentGuide = FragmentGuide.newInstance("", "");
                    if (manager.findFragmentByTag("Guide") == null) {
                        ft.replace(R.id.main_content, fragmentGuide, "Guide").commit();
                    }
                    txtHeader.setText("Hjælp");
                    break;
            }
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                actionBarDrawerToggle.onOptionsItemSelected(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();


    }


    //region Drawer code
    // Navigation Drawer Adapter
    public class NavigationDrawerAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        String[] leftSliderData;

        public NavigationDrawerAdapter(Context context, String[] leftSliderData) {
            this.context = context;
            this.leftSliderData = leftSliderData;
        }

        public int getCount() {

            return leftSliderData.length;

        }

        public Object getItem(int position) {
            return leftSliderData[position];
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            WMTextView txtDrawerItem;
        }


        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_drawer, parent, false);
                holder = new ViewHolder();
                holder.txtDrawerItem = (WMTextView) convertView.findViewById(R.id.txtDrawerItem);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtDrawerItem.setText(leftSliderData[position]);
            return convertView;

        }

    }
    //</editor-fold>


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }
}
