package wm.com.danteater.my_plays;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.model.ComplexPreferences;

/**
 * Created by nirav on 15-10-2014.
 */
public class SelectTeacherForChat extends BaseActivity {

    ArrayList<String> teacherList=new ArrayList<String>();
    ListView teacherListView;
    PlayLines currentPlayLine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_selected_teachers_for_chat);
        ComplexPreferences complexPreferencesForPlayLine = ComplexPreferences.getComplexPreferences(SelectTeacherForChat.this, "selected_playline", 0);
        currentPlayLine = complexPreferencesForPlayLine.getObject("current_playline", PlayLines.class);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        for(AssignedUsers assignedUsers: currentPlayLine.assignedUsersList) {
            if(assignedUsers.AssignedUserName.contains("lærer"))
            teacherList.add(assignedUsers.AssignedUserName);
        }
        teacherListView=(ListView)findViewById(R.id.selectedTeacherListForChat);
        ArrayAdapter adap = new ArrayAdapter(SelectTeacherForChat.this,android.R.layout.simple_list_item_1,teacherList);
        teacherListView.setAdapter(adap);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
