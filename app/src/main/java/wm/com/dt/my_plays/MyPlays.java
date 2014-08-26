package wm.com.dt.my_plays;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;

import wm.com.dt.Messages.FragmentMessage;
import wm.com.dt.R;
import wm.com.dt.app.BaseActivity;
import wm.com.dt.customviews.WMTextView;
import wm.com.dt.guide.FragmentGuide;
import wm.com.dt.inspiration.FragmentInspiration;
import wm.com.dt.search.FragmentSearch;
import wm.com.dt.settings.SettingsFragment;

/**
 * Created by nirav on 25-08-2014.
 */
public class MyPlays extends BaseActivity implements AdapterView.OnItemClickListener {



    private DrawerLayout drawer;
    private ListView leftDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String[] leftSliderData = {"Indstillinger", "Beskeder", "Søg", "Mine stykker", "Inspiration", "Guide"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plays);

        //Load My Places First Time
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        FragmentMyPlay fragmentMyPlay = FragmentMyPlay.newInstance("", "");

        if (manager.findFragmentByTag("my_places") == null) {
            ft.replace(R.id.main_content, fragmentMyPlay, "my_places").commit();
        }
        txtHeader.setText("Mine Stykker");




        initFields();
        initDrawer();

    }

    private void initFields() {
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList = (ListView) findViewById(R.id.left_drawer);
        leftDrawerList.setAdapter(new NavigationDrawerAdapter(MyPlays.this, leftSliderData));
        leftDrawerList.setOnItemClickListener(this);

    }

    private void initDrawer() {
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MyPlays.this, drawer, R.drawable.ic_drawer, R.string.open_drawer, R.string.close_drawer) {

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
        FragmentManager manager = getFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();

        switch (position) {

            case 0:


                SettingsFragment fragmentSettings = SettingsFragment.newInstance("", "");

                if (manager.findFragmentByTag("Settings") == null) {
                    ft.replace(R.id.main_content, fragmentSettings, "Settings").commit();
                }
                txtHeader.setText("Indstillinger");

                break;


            case 1:


                FragmentMessage fragmentMessage = FragmentMessage.newInstance("", "");

                if (manager.findFragmentByTag("message") == null) {
                    ft.replace(R.id.main_content, fragmentMessage, "message").commit();
                }
                txtHeader.setText("Beskeder");

                break;


            case 2:


                FragmentSearch fragmentSearch = FragmentSearch.newInstance("", "");

                if (manager.findFragmentByTag("search") == null) {
                    ft.replace(R.id.main_content, fragmentSearch, "search").commit();
                }
                txtHeader.setText("Søg");

                break;

            case 3:

                FragmentMyPlay fragmentMyPlay = FragmentMyPlay.newInstance("", "");

                if (manager.findFragmentByTag("my_places") == null) {
                    ft.replace(R.id.main_content, fragmentMyPlay, "my_places").commit();
                }
                txtHeader.setText("Mine stykker");

                break;

            case 4:


                FragmentInspiration fragmentInspiration = FragmentInspiration.newInstance("", "");

                if (manager.findFragmentByTag("Inspiration") == null) {
                    ft.replace(R.id.main_content, fragmentInspiration, "Inspiration").commit();
                }
                txtHeader.setText("Inspiration");

                break;

            case 5:

                FragmentGuide fragmentGuide = FragmentGuide.newInstance("", "");

                if (manager.findFragmentByTag("Guide") == null) {
                    ft.replace(R.id.main_content, fragmentGuide, "Guide").commit();
                }

                txtHeader.setText("Guide");


                break;
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

        public NavigationDrawerAdapter(Context context, String[] leftSliderData) {
            this.context = context;
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



}
