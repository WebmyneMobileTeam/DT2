package wm.com.danteater.my_plays;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.app.PlayTabActivity;
import wm.com.danteater.customviews.WMTextView;

public class OrderPlayActivity extends BaseActivity {

    private ListView orderPlayList;
    Context context;
    private String title;
    String bDate;
    ArrayList<String> nameList=new ArrayList<String>();
    private DatePickerDialog datePickerdialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_play);
        context=OrderPlayActivity.this;
        Intent i =getIntent();
        title=i.getStringExtra("title");
        txtHeader.setText(title);
        orderPlayList = (ListView)findViewById(R.id.orderPlayList);

        nameList.add("Antal opførelser");
        nameList.add("Dato for premiere");
        nameList.add("Generalprøve");
        nameList.add("Dato for sidste opførelse");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        orderPlayList.setAdapter(new ListPlayAdapterForPerform(context, nameList));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();

                break;

            default:
                break;
        }

        return super.onOptionsItemSelected(item);
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




        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if(position==2){
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_orderplay_rehersal_view, parent, false);
                    holder = new ViewHolder();
                    holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_rehersal_value);
                    holder.imgOrderPlayInfo=(ImageView) convertView.findViewById(R.id.orderPlayInfo);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.txtTitle.setText(playList.get(position));
                holder.imgOrderPlayInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(context, "Info Click", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_orderplay_view, parent, false);
                    holder = new ViewHolder();
                    holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_value);
                    holder.item_orderplay_selected_value=(WMTextView) convertView.findViewById(R.id.item_orderplay_selected_value);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.txtTitle.setText(playList.get(position));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(position==1 || position==3) {

                            processDate(position);

                        }
                    }
                });
            }




            return convertView;

        }

    }

    protected void processDate(final int position) {
        // TODO Auto-generated method stub

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        datePickerdialog = new DatePickerDialog(
                context, null, year, month, day);
        datePickerdialog.setCancelable(true);
        datePickerdialog.setTitle("Select Date");


        datePickerdialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                "Cancel	", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        datePickerdialog.dismiss();
                    }
                });
        datePickerdialog.setButton(DialogInterface.BUTTON_POSITIVE,
                "Set", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        // TODO Auto-generated method stub
                        datePickerdialog.dismiss();
                      bDate = datePickerdialog
                                .getDatePicker().getDayOfMonth()
                                + "/"
                                + datePickerdialog.getDatePicker()
                                .getMonth()
                                + "-"
                                + datePickerdialog.getDatePicker()
                                .getYear();
                       View view= orderPlayList.getChildAt(position);
                        WMTextView item_orderplay_selected_value=(WMTextView)view.findViewById(R.id.item_orderplay_selected_value);
                        item_orderplay_selected_value.setVisibility(View.VISIBLE);
                        item_orderplay_selected_value.setText(bDate);
                    }


                });

        datePickerdialog.show();

    }


    public class ViewHolder {

        WMTextView txtTitle,item_orderplay_selected_value;
        ImageView imgOrderPlayInfo;
        Switch aSwitch;

    }
}
