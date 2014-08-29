package wm.com.danteater.my_plays;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.app.PlayTabActivity;
import wm.com.danteater.customviews.WMTextView;

public class OrderPlayActivity extends BaseActivity {

    private ListView orderPlayList;
    Context context;
    private String title;
    ArrayList<String> nameList=new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_play);
        context=OrderPlayActivity.this;
        Intent i =getIntent();

        orderPlayList = (ListView)findViewById(R.id.orderPlayList);
        nameList.add("one");
        nameList.add("two");
        nameList.add("three");
        nameList.add("four");

        orderPlayList.setAdapter(new ListPlayAdapterForPerform(context, nameList));
    }



    // For Order tab
    public class ListPlayAdapterForPerform extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> playList;


        public ListPlayAdapterForPerform(Context context, ArrayList<String> playList) {
            this.context = context;
            this.playList = playList;
        }

        public int getCount() {

            return playList.size();

        }

        public Object getItem(int position) {
            return playList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {

            WMTextView txtTitle;
            ImageView imgOrderPlayInfo;
            Switch aSwitch;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_orderplay_view, parent, false);
                holder = new ViewHolder();
                holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_value);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtTitle.setText(playList.get(position));



            return convertView;

        }

    }
}
