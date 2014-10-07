package wm.com.danteater.tab_share;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.PagerSlidingTabStrip;
import wm.com.danteater.customviews.SegmentedGroup;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.BeanGroupInfo;
import wm.com.danteater.login.BeanGroupMemberInfo;
import wm.com.danteater.login.BeanGroupMemberResult;
import wm.com.danteater.login.BeanGroupResult;
import wm.com.danteater.login.Group;
import wm.com.danteater.login.GroupMembers;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.StateManager;
import wm.com.danteater.my_plays.OrderPlayActivityForPerformNew;
import wm.com.danteater.my_plays.ShareActivityForPerform;
import wm.com.danteater.my_plays.SharedUser;


public class ShareFragment extends Fragment implements RadioGroup.OnCheckedChangeListener{

    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private MyPagerAdapter adapter;
    private LinearLayout layout_child_tabs;
    private ListView list_teachers;
    public static  String selectedClassName;
    private SegmentedGroup segmentedTeachersPupils;
    private RadioButton rbTeacher;
    private RadioButton rbPupils;
    static Menu menu;
    private Play selectedPlay;
    ArrayList<String> classNames;
    private StateManager stateManager = StateManager.getInstance();
    public ArrayList<SharedUser> sharedTeachersAndStudents;
    private HUD dialog;
    private User currentUser;
    private static ArrayList<User> teacherSharedList=new ArrayList<User>();
    public static ArrayList<User> studentSharedList= new ArrayList<User>();

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

        teacherSharedList.clear();
        studentSharedList.clear();
        // Get selected Play from my play list from shared preferences
        // Here we use complex preferences to store whole Play class object and retrieve.
       ComplexPreferences complexPreference = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreference.getObject("selected_play",Play.class);
        ComplexPreferences complexPreferencesUser = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser =complexPreferencesUser.getObject("current_user", User.class);
        ((WMTextView)getActivity().getActionBar().getCustomView()).setText(selectedPlay.Title);

        dialog = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("");
        dialog.show();
        Log.e("order id:",selectedPlay.OrderId+"");
        new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                dialog.dismiss();
                Type listType = new TypeToken<List<SharedUser>>() {
                }.getType();
                sharedTeachersAndStudents = new GsonBuilder().create().fromJson(response,listType);
                Log.e("sharedTeachersAndStudents: ",""+sharedTeachersAndStudents);
                for(int i=0;i<sharedTeachersAndStudents.size();i++) {
                    Log.e("user id: ", sharedTeachersAndStudents.get(i).userId + "");
                }
            }

            @Override
            public void error(VolleyError error) {
                Log.e("error: ",error+"");

            }
        }.start();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View convertView = inflater.inflate(R.layout.fragment_share, container, false);
        getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
        tabs = (PagerSlidingTabStrip)convertView.findViewById(R.id.tabs);
        pager = (ViewPager)convertView.findViewById(R.id.pager);
        layout_child_tabs = (LinearLayout)convertView.findViewById(R.id.layout_child_with_tabs);
        list_teachers = (ListView)convertView.findViewById(R.id.listTeachersShare);
        segmentedTeachersPupils = (SegmentedGroup)convertView.findViewById(R.id.segmentShare);
        rbPupils = (RadioButton)convertView.findViewById(R.id.rbPupils);
        rbTeacher = (RadioButton)convertView.findViewById(R.id.rbTeacher);
        segmentedTeachersPupils.setOnCheckedChangeListener(this);
        // show share with pupils tab by default
        rbPupils.setChecked(true);
        return convertView;
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager(), stateManager.classes);
        pager.setAdapter(adapter);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        pager.setPageMargin(pageMargin);
        tabs.setViewPager(pager);
        tabs.setIndicatorColor(getResources().getColor(R.color.greenTheme));
        tabs.setBackgroundColor(Color.parseColor("#f5f5f5"));
        final ArrayList<User> teacherList=stateManager.teachers;
        //TODO sort here
        ArrayList<String> teacherNames=new ArrayList<String>();
        for(int i=0;i<teacherList.size();i++) {
            teacherNames.add(""+teacherList.get(i).getFirstName()+" "+teacherList.get(i).getLastName());
        }
//        Collections.sort(teacherNames);
        Log.e("teachers list: ",teacherNames+"");

        ArrayAdapter adap = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_multiple_choice,teacherNames);
        list_teachers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_teachers.setAdapter(adap);

        list_teachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              enableDisableShareOptions(list_teachers.getCheckedItemPositions());
                ShareActivityForPerform.isSharedforPerformChanged=true;
                menu.getItem(0).setIcon(getActivity().getResources().getDrawable(R.drawable.ic_action_del_selected));
                menu.getItem(0).setEnabled(true);
                User user=teacherList.get(position);
                ArrayList<String> roles=new ArrayList<String>();
                roles.add("teacher");
                user.setRoles(roles);
                if (teacherSharedList.contains(user)) {
                    teacherSharedList.remove(user);
                } else {
                    teacherSharedList.add(user);
                }
                Log.e("teacherSharedList:",teacherSharedList.size()+"");
            }

        });
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
//                Toast.makeText(getActivity(), "click on share", Toast.LENGTH_SHORT).show();
                if(ShareActivityForPerform.isSharedforPerformChanged==true) {
                    shareWithTeachersAndStudents();
                }
                break;
        }
        return true;
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

    public class MyPagerAdapter extends FragmentPagerAdapter {

	/*	private final String[] TITLES = { "Categories", "Home", "Top Paid", "Top Free", "Top Grossing", "Top New Paid",
				"Top New Free", "Trending" };*/
    ArrayList<Group> classesList;
        public MyPagerAdapter(FragmentManager fm,ArrayList<Group> classesList ) {
            super(fm);
            this.classesList=classesList;
        }
        @Override
        public CharSequence getPageTitle(int position) {
           classNames=new ArrayList<String>();
            for(int i=0;i<classesList.size();i++) {
                classNames.add(classesList.get(i).getGroupName());
            }
            Collections.sort(classNames);

            return classNames.get(position);
        }

        @Override
        public int getCount() {
            return classesList.size();
        }


        @Override
        public Fragment getItem(int position) {

            return FragmentPupils.newInstance(position,classNames.get(position));
        }
    }

    private void shareWithTeachersAndStudents() {
        final ArrayList<User> totalUsers = new ArrayList<User>();
        totalUsers.addAll(teacherSharedList);
        totalUsers.addAll(studentSharedList);
        final JSONArray shareWithUsersArray = new JSONArray();

        if(!totalUsers.contains(currentUser.getUserId())) {
            totalUsers.add(currentUser);
        }

        if (totalUsers != null || totalUsers.size() != 0) {
            for (User user : totalUsers) {
                String nameToBeSaved;

                if (user.checkTeacherOrAdmin(user.getRoles()) == true) {
                    // TODO can't add "(lærer)"
                    nameToBeSaved = user.getFirstName() + " " + user.getLastName() + " (lærer)";
//                    nameToBeSaved = user.getFirstName() + " " + user.getLastName();
                } else {
                    nameToBeSaved = user.getFirstName() + " " + user.getLastName();
                }
                JSONObject userDict = new JSONObject();
                try {
                    userDict.put("UserId", user.getUserId());
                    userDict.put("UserName", nameToBeSaved);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                shareWithUsersArray.put(userDict);
            }
            Log.e("total users: ", shareWithUsersArray + "");
        }
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/playshare/" + selectedPlay.OrderId, shareWithUsersArray.toString());
                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
                    Log.e("response", response + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


        }.execute();

    }
}
