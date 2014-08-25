package wm.com.dt.my_plays;

import android.os.Bundle;
import android.widget.AdapterView;
import wm.com.dt.app.BaseActivity;
import wm.com.dt.R;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by nirav on 25-08-2014.
 */
public class MyPlays extends BaseActivity implements AdapterView.OnItemClickListener {

    private DrawerLayout drawer;
    private ListView leftDrawerList;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String[] leftSliderData={"Indstillinger","Beskeder","Søg","Mine stykker","Inspiration","Guide"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_plays);
        txtHeader.setText("Mine Stykker");
        initFields();
        initDrawer();
    }

    private void initFields(){
        drawer=(DrawerLayout) findViewById(R.id.drawer_layout);
        leftDrawerList=(ListView) findViewById(R.id.left_drawer);
        leftDrawerList.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,leftSliderData ));
        leftDrawerList.setOnItemClickListener(this);
    }
    private void initDrawer() {
        drawer.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MyPlays.this, drawer,R.drawable.ic_drawer, R.string.open_drawer,R.string.close_drawer) {

            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {

            }

        };
        drawer.setDrawerListener(actionBarDrawerToggle);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(this, leftSliderData[position], Toast.LENGTH_LONG).show();
        // Add your onclick logic here
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch(item.getItemId()) {
            case android.R.id.home:
                actionBarDrawerToggle.onOptionsItemSelected(item);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState)
    {
        super.onPostCreate(savedInstanceState);
        actionBarDrawerToggle.syncState();
    }


}
