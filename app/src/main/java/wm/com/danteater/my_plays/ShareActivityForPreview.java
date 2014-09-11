package wm.com.danteater.my_plays;

import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;

/**
 * Created by nirav on 11-09-2014.
 */
public class ShareActivityForPreview extends BaseActivity {
    private Play selectedPlay;
    private ListView list_teachers;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_for_preview);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play", Play.class);
        ((WMTextView) getActionBar().getCustomView()).setText(selectedPlay.Title);
        list_teachers = (ListView) findViewById(R.id.listTeachersShareForPreview);

        ArrayList teachers = new ArrayList();
        teachers.add("Teacher One");
        teachers.add("Teacher Two");
        teachers.add("Teacher Three");
        teachers.add("Teacher Four");
        teachers.add("Teacher Five");
        teachers.add("Teacher Six");
        teachers.add("Teacher Seven");
        teachers.add("Teacher Eight");


        ArrayAdapter adap = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, teachers);


        list_teachers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        list_teachers.setAdapter(adap);

        list_teachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                enableDisableShareOptions(list_teachers.getCheckedItemPositions());


            }
        });

    }

    private void enableDisableShareOptions(SparseBooleanArray arr) {


        boolean isAbleToShare = false;
        for (int i = 0; i < list_teachers.getCount(); i++) {
            if (arr.valueAt(i)) {

                isAbleToShare = true;
                break;

            } else {

                isAbleToShare = false;
                continue;

            }
        }

        if (isAbleToShare == true) {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_selected));
            menu.getItem(0).setEnabled(true);
        } else {
            menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_action_del_unselected));
            menu.getItem(0).setEnabled(false);

        }

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // super.onCreateOptionsMenu(menu, inflater);

        // retrieve current displayed menu reference.
        // so that we can change menu item icon programaticaly using this new reference anywhere in this class.
        this.menu = menu;

        // inflate share menu
        getMenuInflater().inflate(R.menu.menu_share, menu);
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


                break;

        }

        return true;
    }
}