package dk.danteater.danteater.my_plays;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import dk.danteater.danteater.Play.Play;
import dk.danteater.danteater.R;
import dk.danteater.danteater.app.BaseActivity;
import dk.danteater.danteater.model.CallWebService;
import dk.danteater.danteater.model.ComplexPreferences;


/**
 * Created by nirav on 15-10-2014.
 */
public class SelectTeacherForChat extends BaseActivity implements AdapterView.OnItemClickListener {

    ArrayList<String> teacherList=new ArrayList<String>();
    ArrayList<String> teacherIdList=new ArrayList<String>();
    ListView teacherListView;
    private Play selectedPlay;
//    PlayLines currentPlayLine;
    private ArrayList<SharedUser> _marrSharedWithUsers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_teachers_for_chat);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        teacherListView=(ListView)findViewById(R.id.selectedTeacherListForChat);
        teacherListView.setOnItemClickListener(this);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(SelectTeacherForChat.this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        new CallWebService("http://api.danteater.dk/api/PlayShare/" +selectedPlay.OrderId,CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {

                Type listType = new TypeToken<List<SharedUser>>() {
                }.getType();
                _marrSharedWithUsers = new ArrayList<SharedUser>();
                _marrSharedWithUsers = new GsonBuilder().create().fromJson(response,listType);
//                ComplexPreferences complexPreferencesForPlayLine = ComplexPreferences.getComplexPreferences(SelectTeacherForChat.this, "selected_playline", 0);
//                currentPlayLine = complexPreferencesForPlayLine.getObject("current_playline", PlayLines.class);


                for(SharedUser assignedUsers: _marrSharedWithUsers) {
                    if(assignedUsers.userName.contains("lærer")) {
                        teacherList.add(assignedUsers.userName);
                        teacherIdList.add(assignedUsers.userId);
                    }
                }

                ArrayAdapter adap = new ArrayAdapter(SelectTeacherForChat.this,android.R.layout.simple_list_item_1,teacherList);
                teacherListView.setAdapter(adap);

            }

            @Override
            public void error(VolleyError error) {
                Log.e("error: ",error+"");
            }
        }.start();

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
