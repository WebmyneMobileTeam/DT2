package wm.com.danteater.tab_share;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import wm.com.danteater.R;
import wm.com.danteater.customviews.PagerSlidingTabStrip;
import wm.com.danteater.customviews.SegmentedGroup;

public class ShareFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{


    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private LinearLayout layout_child_tabs;
    private ListView list_teachers;
    private SegmentedGroup segmentedTeachersPupils;
    private RadioButton rbTeacher;
    private RadioButton rbPupils;



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
        setHasOptionsMenu(true);
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
       getActivity().getMenuInflater().inflate(R.menu.menu_share,menu);



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


    private void setupPupils(){

        layout_child_tabs.setVisibility(View.VISIBLE);
        list_teachers.setVisibility(View.GONE);



    }

    private void setupTeachers() {

        layout_child_tabs.setVisibility(View.GONE);
        list_teachers.setVisibility(View.VISIBLE);

    }


    @Override
    public void onResume() {
        super.onResume();

        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);

        tabs.setViewPager(pager);
        tabs.setIndicatorColor(getResources().getColor(R.color.apptheme_color));
        tabs.setBackgroundColor(Color.parseColor("#f5f5f5"));



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



}
