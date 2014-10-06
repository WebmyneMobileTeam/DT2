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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.StateManager;


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
    private Menu menu;
    private HUD dialog;

    private Play selectedPlay;
    ArrayList<String> classNames;
//    private StateManager stateManager = StateManager.getInstance();

    public  boolean finishedRetrievingTeachers = false;
    public  int numberOfClassesToBeRetrieved = 0;
    public   Group groupForTeacher = new Group();
    public  ArrayList<Group> classes = new ArrayList<Group>();
    public  ArrayList<User> teachers= new ArrayList<User>();
    public static HashMap<String, ArrayList<User>> pupils=new HashMap<String, ArrayList<User>>();
    User cUser;
    String session_id;

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
       ComplexPreferences complexPreference = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreference.getObject("selected_play",Play.class);
        ((WMTextView)getActivity().getActionBar().getCustomView()).setText(selectedPlay.Title);
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

        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("");
        dialog.show();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        cUser = complexPreferences.getObject("current_user", User.class);
        SharedPreferences pre = getActivity().getSharedPreferences("session_id", getActivity().MODE_PRIVATE);
        session_id = pre.getString("session_id", "");
        retriveSchoolClasses(session_id, cUser.getDomain());
        retriveSchoolTeachers(session_id, cUser.getDomain());
        return convertView;
    }

    public void handlePostData() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
                pager.setAdapter(adapter);
                final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                pager.setPageMargin(pageMargin);
                tabs.setViewPager(pager);
                tabs.setIndicatorColor(getResources().getColor(R.color.greenTheme));
                tabs.setBackgroundColor(Color.parseColor("#f5f5f5"));
                ArrayList<String> teacherNames=new ArrayList<String>();
                for(int i=0;i<teachers.size();i++) {
                    teacherNames.add(""+teachers.get(i).getFirstName()+" "+teachers.get(i).getLastName());
                }
                Collections.sort(teacherNames);
                Log.e("teachers list: ",teacherNames+"");
                ArrayAdapter adap = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_multiple_choice,teacherNames);
                list_teachers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                list_teachers.setAdapter(adap);
                list_teachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//              enableDisableShareOptions(list_teachers.getCheckedItemPositions());
                    }
                });
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

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
           classNames=new ArrayList<String>();
            for(int i=0;i<classes.size();i++) {
                classNames.add(classes.get(i).getGroupName());
            }
            Collections.sort(classNames);

            return classNames.get(position);
        }

        @Override
        public int getCount() {
            return classes.size();
        }


        @Override
        public Fragment getItem(int position) {

            return FragmentPupils.newInstance(position,classNames.get(position));
        }

    }


    public  void retriveSchoolClasses(final String seesionId, final String domain) {

        JSONObject methodParams = new JSONObject();
        JSONObject requestParams = new JSONObject();

        try {
            methodParams.put("session_id", seesionId);
            methodParams.put("domain", domain);

            requestParams.put("methodname", "listGroups");
            requestParams.put("type", "jsonwsp/request");
            requestParams.put("version", "1.0");
            requestParams.put("args", methodParams);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/GroupService/jsonwsp", requestParams, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jobj) {
                    String res = jobj.toString();
//                    Log.e("response for retrive school classes...: ", res + "");

                    BeanGroupInfo beanGroupInfo = new GsonBuilder().create().fromJson(res, BeanGroupInfo.class);
                    BeanGroupResult beanGroupResult = beanGroupInfo.getBeanGroupResult();
                    ArrayList<Group> groupArrayList = beanGroupResult.getGroupArrayList();
                    classes.clear();
                    //   pupils.clear();

                    for (Group beanGroup : groupArrayList) {
                        if (beanGroup.getGroupType().equals("classtype")) {
                            classes.add(beanGroup);
                            Log.e("group domain", beanGroup.getDomain() + "");
                            numberOfClassesToBeRetrieved++;
                            retriveMembers(seesionId, domain, beanGroup);
                        }
                    }

                    Log.e("classes...: ", classes + "");

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }
            });
            MyApplication.getInstance().addToRequestQueue(req);


        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

    public  void retriveSchoolTeachers(String seesionId, String domain) {
        groupForTeacher.setGroupId("teacher");
        teachers.clear();
        retriveMembers(seesionId, domain, groupForTeacher);

    }

    public  void retriveMembers(String seesionId, String domain, final Group group) {
        JSONObject methodParams = new JSONObject();
        JSONObject requestParams = new JSONObject();
        try {
            methodParams.put("session_id", seesionId);
            methodParams.put("domain", domain);
            methodParams.put("group_cn", group.getGroupId());
            requestParams.put("methodname", "listGroupMembers");
            requestParams.put("type", "jsonwsp/request");
            requestParams.put("version", "1.0");
            requestParams.put("args", methodParams);

            JsonObjectRequest req = new JsonObjectRequest(Request.Method.POST, "https://mvid-services.mv-nordic.com/v2/GroupService/jsonwsp", requestParams, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject jobj) {

                    String res = jobj.toString();
//                    Log.e("response for retrive school teachers...: ", res + "");

                    BeanGroupMemberInfo beanGroupMemberInfo = new GsonBuilder().create().fromJson(res, BeanGroupMemberInfo.class);
                    BeanGroupMemberResult beanGroupMemberResult = beanGroupMemberInfo.getBeanGroupMemberResult();

                    ArrayList<GroupMembers> groupMembersArrayList = beanGroupMemberResult.getGroupMembersArrayList();
                    ArrayList<User> userArrayList = new ArrayList<User>();
                    userArrayList.clear();
                    for (GroupMembers beanGroupMembers : groupMembersArrayList) {
//                     Log.e("given name",beanGroupMembers.getGivenName()+" "+beanGroupMembers.getSn());

                        userArrayList.add(new User(beanGroupMembers.getGivenName(), beanGroupMembers.getSn(), beanGroupMembers.getUid(), beanGroupMembers.getPrimaryGroup(), null, beanGroupMembers.getDomain()));
                    }

                    if (group.getGroupId().equals("teacher")) {

                        teachers.addAll(userArrayList);
                        Log.e("teachers:",teachers+"");

                        finishedRetrievingTeachers = true;
                    } else {

                        pupils.put(group.getGroupName().toString(), userArrayList);


                        for(Map.Entry<String,ArrayList<User>> entry : pupils.entrySet()){

                            Log.e("key: ",entry.getKey()+" ");
                            Log.e("vlaues: ",entry.getValue()+"  ");


                        }
                        Log.e("pupils: ",pupils+"");

                        numberOfClassesToBeRetrieved--;
                    }
                    if (finishedRetrievingTeachers && numberOfClassesToBeRetrieved == 0) {
                        dialog.dismiss();
                        handlePostData();
                    }
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError arg0) {
                }
            });
            MyApplication.getInstance().addToRequestQueue(req);

        } catch (JSONException je) {
            je.printStackTrace();
        }
    }

}
