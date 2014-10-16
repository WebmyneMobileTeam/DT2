package wm.com.danteater.my_plays;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
public class SelectTeacherForChat extends BaseActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> teacherList=new ArrayList<String>();
    ArrayList<String> teacherIdList=new ArrayList<String>();
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
            if(assignedUsers.AssignedUserName.contains("lærer")) {
                teacherList.add(assignedUsers.AssignedUserName);
                teacherIdList.add(assignedUsers.AssignedUserId);
            }
        }
        teacherListView=(ListView)findViewById(R.id.selectedTeacherListForChat);
        teacherListView.setOnItemClickListener(this);
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Intent i=new Intent(SelectTeacherForChat.this,ChatViewFromRead.class);
        i.putExtra("to_user_id",teacherIdList.get(position));
        startActivity(i);
    }
}
