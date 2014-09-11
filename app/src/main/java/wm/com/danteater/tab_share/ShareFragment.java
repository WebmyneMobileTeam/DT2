package wm.com.danteater.tab_share;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.ArrayList;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.customviews.PagerSlidingTabStrip;
import wm.com.danteater.customviews.SegmentedGroup;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;


public class ShareFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{


    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private LinearLayout layout_child_tabs;
    private ListView list_teachers;
    private SegmentedGroup segmentedTeachersPupils;
    private RadioButton rbTeacher;
    private RadioButton rbPupils;
    private Menu menu;
    private Play selectedPlay;



    public static ShareFragment newInstance(String param1, String param2) {
        ShareFragment fragment = new ShareFragment();

        return fragment;
    }
    public ShareFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


       // Important line to enable options menu in fragment
        setHasOptionsMenu(true);


        // Get selected Play from my play list from shared preferences
        // Here we use complex preferences to store whole Play class object and retrieve.

       ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play",Play.class);
        ((WMTextView)getActivity().getActionBar().getCustomView()).setText(selectedPlay.Title);

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View convertView = inflater.inflate(R.layout.fragment_share, container, false);

        tabs = (PagerSlidingTabStrip)convertView.findViewById(R.id.tabs);
        pager = (ViewPager)convertView.findViewById(R.id.pager);
        layout_child_tabs = (LinearLayout)convertView.findViewById(R.id.layout_child_with_tabs);
        list_teachers = (ListView)convertView.findViewById(R.id.listTeachersShare);
        segmentedTeachersPupils = (SegmentedGroup)convertView.findViewById(R.id.segmentShare);
        rbPupils = (RadioButton)convertView.findViewById(R.id.rbPupils);
        rbTeacher = (RadioButton)convertView.findViewById(R.id.rbTeacher);
        segmentedTeachersPupils.setOnCheckedChangeListener(this);

        rbTeacher.setChecked(true);



        return convertView;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

      // super.onCreateOptionsMenu(menu, inflater);

       // retrieve current displayed menu reference.
       // so that we can change menu item icon programaticaly using this new reference anywhere in this class.
       this.menu = menu;

       // inflate share menu
       getActivity().getMenuInflater().inflate(R.menu.menu_share,menu);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            // going back
            case android.R.id.home:

                getActivity().finish();

                break;

            // share the play
            case R.id.actionShare:


                break;

        }

        return true;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){
            
            case R.id.rbPupils:
                
                setupPupils();
                
                break;
            
            case R.id.rbTeacher:
                
                setupTeachers();
                
                break;

        }
        
    }


    public void setupPupils(){

        layout_child_tabs.setVisibility(View.VISIBLE);
        list_teachers.setVisibility(View.GONE);

        try {
            menu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_del_unselected));
        }catch(Exception e){}


    }

    private void setupTeachers() {

        layout_child_tabs.setVisibility(View.GONE);
        list_teachers.setVisibility(View.VISIBLE);

    }


    @Override
    public void onResume() {
        super.onResume();


        Toast.makeText(getActivity(),"On Resume called", Toast.LENGTH_SHORT).show();

        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setIndicatorColor(getResources().getColor(R.color.greenTheme));
        tabs.setBackgroundColor(Color.parseColor("#f5f5f5"));

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);

        // Fill the static list of teachers to show the functionality

        ArrayList teachers = new ArrayList();
        teachers.add("Teacher One");
        teachers.add("Teacher Two");
        teachers.add("Teacher Three");
        teachers.add("Teacher Four");
        teachers.add("Teacher Five");
        teachers.add("Teacher Six");
        teachers.add("Teacher Seven");
        teachers.add("Teacher Eight");


        ArrayAdapter adap = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_multiple_choice,teachers);


        list_teachers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_teachers.setAdapter(adap);

        list_teachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                enableDisableShareOptions(list_teachers.getCheckedItemPositions());


            }
        });


    }

    private void enableDisableShareOptions(SparseBooleanArray arr) {


        boolean isAbleToShare = false;
        for(int i = 0; i < list_teachers.getCount() ; i++)
        {
            if (arr.valueAt(i))
            {

                isAbleToShare = true;
                break;

            }else{

                isAbleToShare = false;
                continue;

            }
        }

        if(isAbleToShare == true){
            menu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_del_selected));
            menu.getItem(0).setEnabled(true);
        }else{
            menu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_del_unselected));
            menu.getItem(0).setEnabled(false);

        }

    }


    public class MyPagerAdapter extends FragmentPagerAdapter {

	/*	private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
				"Top New Free", "Trending" };*/

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {

            return "1-A";
        }

        @Override
        public int getCount() {
            return 5;
        }


        @Override
        public Fragment getItem(int position) {

            return FragmentPupils.newInstance(position);
        }

    }

    public static interface onResetHeaderListner{

        public void resetHeader(String headerName);

    }



}
