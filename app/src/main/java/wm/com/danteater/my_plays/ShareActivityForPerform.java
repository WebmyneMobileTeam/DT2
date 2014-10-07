package wm.com.danteater.my_plays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;

import wm.com.danteater.app.MyApplication;
import wm.com.danteater.customviews.HUD;
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
import wm.com.danteater.tab_share.ShareFragment;

/**
 * Created by nirav on 11-09-2014.
 */
public class ShareActivityForPerform extends BaseActivity {

//    StateManager stateManager=StateManager.getInstance();
    private HUD dialog;
    public static boolean isSharedforPerformChanged=false;
    public static ArrayList<SharedUser> sharedTeachersAndStudents;
    public static Menu menu;
    public  static ArrayList<Group> classes = new ArrayList<Group>();
    public static ArrayList<User> teachers= new ArrayList<User>();
    public static HashMap<String, ArrayList<User>> pupils=new HashMap<String, ArrayList<User>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share_for_perform);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        ShareFragment fragmentShare = ShareFragment.newInstance("", "");
        if (manager.findFragmentByTag("share_perform") == null) {
            ft.replace(R.id.main_content, fragmentShare, "share_perform").commit();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if(ShareActivityForPerform.isSharedforPerformChanged==true && ShareActivityForPerform.menu.getItem(0).isEnabled()==true) {
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
}
