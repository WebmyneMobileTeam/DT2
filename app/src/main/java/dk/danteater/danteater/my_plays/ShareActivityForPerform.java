package dk.danteater.danteater.my_plays;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.HashMap;

import dk.danteater.danteater.R;
import dk.danteater.danteater.app.BaseActivity;
import dk.danteater.danteater.customviews.HUD;
import dk.danteater.danteater.login.User;
import dk.danteater.danteater.tab_share.ShareFragment;


/**
 * Created by nirav on 11-09-2014.
 */
public class ShareActivityForPerform extends BaseActivity {

//    StateManager stateManager=StateManager.getInstance();
    private HUD dialog;
    public static boolean isSharedforPerformChanged=false;
    public static ArrayList<SharedUser> sharedTeachersAndStudents;
    public static Menu menu;
//    public  static ArrayList<Group> classes = new ArrayList<Group>();
//    public static ArrayList<User> teachers= new ArrayList<User>();
    public static HashMap<String, ArrayList<User>> pupils=new HashMap<String, ArrayList<User>>();
    public static boolean isFinishOnBackPressForPerform=false;
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
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    ShareFragment fragment = (ShareFragment) fragmentManager.findFragmentByTag("share_perform");
                    fragment.shareWithTeachersAndStudents();
                    if(isFinishOnBackPressForPerform==true){
                        new CountDownTimer(2500, 1000) {

                            @Override
                            public void onFinish() {
                                finish();

                            }

                            @Override
                            public void onTick(long millisUntilFinished) {

                            }
                        }.start();
                    }
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
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        teachers.clear();
//        classes.clear();
        ShareActivityForPerform.pupils.clear();
        finish();
    }
}
