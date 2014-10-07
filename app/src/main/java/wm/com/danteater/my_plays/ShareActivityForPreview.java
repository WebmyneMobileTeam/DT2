package wm.com.danteater.my_plays;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.StateManager;
import wm.com.danteater.tab_share.ShareFragment;

/**
 * Created by nirav on 11-09-2014.
 */
public class ShareActivityForPreview extends BaseActivity {
    private Play selectedPlay;
    private ListView list_teachers;
    private Menu menu;
    private StateManager stateManager = StateManager.getInstance();
    private static ArrayList<User> teacherSharedListForPreview=new ArrayList<User>();
    private User currentUser;
    private HUD dialog;
    public ArrayList<SharedUser> sharedTeachers;
    static boolean isSharedforPreviewChanged=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_for_preview);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        teacherSharedListForPreview.clear();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        ComplexPreferences complexPreferencesUser = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        currentUser =complexPreferencesUser.getObject("current_user", User.class);
        ((WMTextView) getActionBar().getCustomView()).setText(selectedPlay.Title);
        list_teachers = (ListView) findViewById(R.id.listTeachersShareForPreview);
        final ArrayList<User> teacherList=stateManager.teachers;
        //TODO sort here
        ArrayList<String> teacherNames=new ArrayList<String>();
        for(int i=0;i<teacherList.size();i++) {
            teacherNames.add(""+teacherList.get(i).getFirstName()+" "+teacherList.get(i).getLastName());
        }
        Log.e("teachers list: ", teacherNames + "");
        ArrayAdapter adap = new ArrayAdapter(ShareActivityForPreview.this,android.R.layout.simple_list_item_multiple_choice,teacherNames);
        list_teachers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_teachers.setAdapter(adap);
        for(int j=0;j< ShareFragment.sharedTeachersAndStudents.size();j++) {
            for (int i = 0; i < teacherList.size(); i++) {
                if (teacherList.get(i).getUserId().contains(ShareFragment.sharedTeachersAndStudents.get(j).userId))
                    list_teachers.setItemChecked(i, true);
            }
        }
        list_teachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isSharedforPreviewChanged=true;
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_selected));
                menu.getItem(0).setEnabled(true);
                User user=teacherList.get(position);
                ArrayList<String> roles=new ArrayList<String>();
                roles.add("teacher");
                user.setRoles(roles);
                if (teacherSharedListForPreview.contains(user)) {
                    teacherSharedListForPreview.remove(user);
                } else {
                    teacherSharedListForPreview.add(user);
                }
                Log.e("teacherSharedList:",teacherSharedListForPreview.size()+"");
            }

        });

        // Get Selected Teachers
        dialog = new HUD(ShareActivityForPreview.this,android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("");
        dialog.show();
        Log.e("order id:",selectedPlay.OrderId+"");
        new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                dialog.dismiss();
                Type listType = new TypeToken<List<SharedUser>>() {
                }.getType();
                sharedTeachers = new GsonBuilder().create().fromJson(response,listType);
                Log.e("sharedTeachersAndStudents: ",""+sharedTeachers);
                for(int i=0;i<sharedTeachers.size();i++) {
                    Log.e("user id: ", sharedTeachers.get(i).userId + "");
                }
            }

            @Override
            public void error(VolleyError error) {
                Log.e("error: ",error+"");

            }
        }.start();

    }

//    private void enableDisableShareOptions(SparseBooleanArray arr) {
//
//
//        boolean isAbleToShare = false;
//        for (int i = 0; i < list_teachers.getCount(); i++) {
//            if (arr.valueAt(i)) {
//
//                isAbleToShare = true;
//                break;
//
//            } else {
//
//                isAbleToShare = false;
//                continue;
//
//            }
//        }
//
//        if (isAbleToShare == true) {
//            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_selected));
//            menu.getItem(0).setEnabled(true);
//        } else {
//            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_unselected));
//            menu.getItem(0).setEnabled(false);
//
//        }
//
//    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // super.onCreateOptionsMenu(menu, inflater);

        // retrieve current displayed menu reference.
        // so that we can change menu item icon programaticaly using this new reference anywhere in this class.
        this.menu = menu;

        // inflate share menu
        getMenuInflater().inflate(R.menu.menu_share, menu);
        menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_unselected));
        menu.getItem(0).setEnabled(false);
        return super.onCreateOptionsMenu(menu);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            // going back
            case android.R.id.home:

                finish();

                break;

            // share the play
            case R.id.actionShare:
                if(isSharedforPreviewChanged==true){
                    shareWithTeachers();
                }



                break;

        }

        return true;
    }

    private void shareWithTeachers() {
        final ArrayList<User> totalUsers = new ArrayList<User>();
        totalUsers.addAll(teacherSharedListForPreview);

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