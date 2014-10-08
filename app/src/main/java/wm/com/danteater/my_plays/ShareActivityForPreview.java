package wm.com.danteater.my_plays;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

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
import java.util.HashMap;
import java.util.List;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.Group;
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
    //private StateManager stateManager = StateManager.getInstance();
    private static ArrayList<User> teacherSharedListForPreview=new ArrayList<User>();
    private User currentUser;
    private HUD dialog;
    public static  ArrayList<SharedUser> sharedTeachers;
    static boolean isSharedforPreviewChanged=false;

    public static ArrayList<User> teachers= new ArrayList<User>();

   ArrayList<User> teacherList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        teacherSharedListForPreview.clear();
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        ComplexPreferences complexPreferencesUser = ComplexPreferences.getComplexPreferences(this, "user_pref", 0);
        currentUser =complexPreferencesUser.getObject("current_user", User.class);
        setContentView(R.layout.activity_share_for_preview);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ((WMTextView) getActionBar().getCustomView()).setText(selectedPlay.Title);

        list_teachers = (ListView) findViewById(R.id.listTeachersShareForPreview);
        teacherList=ShareActivityForPreview.teachers;

        // Get Teacher List
        ArrayList<String> teacherNames=new ArrayList<String>();
        for(int i=0;i<teacherList.size();i++) {
            teacherNames.add(""+teacherList.get(i).getFirstName()+" "+teacherList.get(i).getLastName());
        }
        Log.e("teachers list: ", teacherNames + "");
        ArrayAdapter adap = new ArrayAdapter(ShareActivityForPreview.this,android.R.layout.simple_list_item_multiple_choice,teacherNames);
        list_teachers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_teachers.setAdapter(adap);
        for(int i=0;i<teacherList.size();i++){
            User user=teacherList.get(i);
            for(SharedUser u: ShareActivityForPreview.sharedTeachers){
                if(u.userId.equalsIgnoreCase(user.getUserId())){
                    list_teachers.setItemChecked(i,true);

                    ArrayList<String> roles=new ArrayList<String>();
                    roles.add("teacher");
                    user.setRoles(roles);
                    teacherSharedListForPreview.add(user);
                }
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



    }


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
                if(ShareActivityForPreview.isSharedforPreviewChanged==true && menu.getItem(0).isEnabled()==true) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(ShareActivityForPreview.this);
                    alert.setTitle("Gem deling");
                    alert.setMessage("Vil du gemme dine ændringer?");
                    alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            // TODO share play from here
                        }
                    });

                    alert.setNegativeButton("Nej", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });

                    alert.show();
                } else {
                    finish();
                }


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
            protected void onPreExecute() {
                dialog = new HUD(ShareActivityForPreview.this,android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("");
                dialog.show();

            }

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_unselected));
                            menu.getItem(0).setEnabled(false);
                            dialog.dismissWithStatus(R.drawable.ic_navigation_accept,"Stykker er delt");

                        }
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


        }.execute();

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(ShareActivityForPreview.isSharedforPreviewChanged==true && menu.getItem(0).isEnabled()==true) {
            AlertDialog.Builder alert = new AlertDialog.Builder(this);
            alert.setTitle("Gem deling");
            alert.setMessage("Vil du gemme dine ændringer?");
            alert.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            alert.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alert.show();
        }else {
            finish();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        ShareActivityForPreview.teachers.clear();
    }
}