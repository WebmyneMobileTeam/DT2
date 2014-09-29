package wm.com.danteater.model;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import wm.com.danteater.app.MyApplication;
import wm.com.danteater.login.BeanGroupInfo;
import wm.com.danteater.login.BeanGroupMemberInfo;
import wm.com.danteater.login.BeanGroupMemberResult;
import wm.com.danteater.login.BeanGroupResult;
import wm.com.danteater.login.Group;
import wm.com.danteater.login.GroupMembers;
import wm.com.danteater.login.User;

/**
 * Created by nirav on 16-09-2014.
 */
public class StateManager { //singleton class

    private static StateManager s = new StateManager();

    private StateManager() {
    }

    public static int playID = 0;
    public static StateManager getInstance() {
        return s;
    }

    public static boolean finishedRetrievingTeachers = false;
    public static int numberOfClassesToBeRetrieved = 0;
    public  static Group groupForTeacher = new Group();

    public  static ArrayList<Group> classes = new ArrayList<Group>();

    public static ArrayList<User> teachers= new ArrayList<User>();
    public static HashMap<String, ArrayList<User>> pupils=new HashMap<String, ArrayList<User>>();


    public static void retriveSchoolClasses(final String seesionId, final String domain) {

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
                    Log.e("response for retrive school classes...: ", res + "");

                    BeanGroupInfo beanGroupInfo = new GsonBuilder().create().fromJson(res, BeanGroupInfo.class);
                    BeanGroupResult beanGroupResult = beanGroupInfo.getBeanGroupResult();
                    ArrayList<Group> groupArrayList = beanGroupResult.getGroupArrayList();
                    classes.clear();
                    pupils.clear();

                    for (Group beanGroup : groupArrayList) {
                        if (beanGroup.getGroupType().equals("classtype")) {
                            classes.add(beanGroup);
                            Log.e("group domain", beanGroup.getDomain() + "");
                            numberOfClassesToBeRetrieved++;

                            retriveMembers(seesionId, domain, beanGroup);
                        }
                    }
                    Log.e("pupils after insert: ",pupils+"");
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

    public static void retriveSchoolTeachers(String seesionId, String domain) {
        groupForTeacher.setGroupId("teacher");
        teachers.clear();
        retriveMembers(seesionId, domain, groupForTeacher);
    }

    public static void retriveMembers(String seesionId, String domain, final Group group) {
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
                    Log.e("response for retrive school teachers...: ", res + "");

                    BeanGroupMemberInfo beanGroupMemberInfo = new GsonBuilder().create().fromJson(res, BeanGroupMemberInfo.class);
                    BeanGroupMemberResult beanGroupMemberResult = beanGroupMemberInfo.getBeanGroupMemberResult();

                    ArrayList<GroupMembers> groupMembersArrayList = beanGroupMemberResult.getGroupMembersArrayList();
                    ArrayList<User> userArrayList = new ArrayList<User>();
                    userArrayList.clear();
                    for (GroupMembers beanGroupMembers : groupMembersArrayList) {
                     Log.e("given name",beanGroupMembers.getGivenName()+" "+beanGroupMembers.getSn());

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
                        Log.e("pupils after insert: ",pupils.size()+"");
                        numberOfClassesToBeRetrieved--;
                    }
                    if (finishedRetrievingTeachers && numberOfClassesToBeRetrieved == 0) {
                        //TODO
                        //FinishedRetrievingTeachersAndPupils();
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
