package wm.com.danteater.my_plays;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import wm.com.danteater.Play.Play;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.ComplexPreferences;

public class OrderPlayActivityForPerform extends BaseActivity {

    private ListView orderPlayList;
    Context context;
    private String title;
    String bDate;
    ArrayList<String> nameList = new ArrayList<String>();
    private DatePickerDialog datePickerdialog;
    private WMTextView orderPlay;

    //views of listview at positions 0,1 and 3
    View firstView;
    WMTextView firstViewValue;
    EditText etFirstView;
    View firstDateView;
    WMTextView txtFirstDate;
    View secondDateView;
    WMTextView txtSecondDate;
    Play selectedPlay;
    //
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_play);
        context = OrderPlayActivityForPerform.this;
        Intent i = getIntent();
        title = i.getStringExtra("title");
        txtHeader.setText(title);
        orderPlayList = (ListView) findViewById(R.id.orderPlayList);
        orderPlay = (WMTextView) findViewById(R.id.btnPlayOrder);
        nameList.add("Antal opførelser");
        nameList.add("Dato for premiere");
        nameList.add("Generalprøve");
        nameList.add("Dato for sidste opførelse");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this, "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play",Play.class);
        ((WMTextView)getActionBar().getCustomView()).setText(selectedPlay.Title);

        orderPlayList.setAdapter(new ListPlayAdapterForPerform(context, nameList));
        orderPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstView = orderPlayList.getChildAt(0);
                firstViewValue = (WMTextView) firstView.findViewById(R.id.item_orderplay_selected_value);
                firstDateView = orderPlayList.getChildAt(1);
                txtFirstDate = (WMTextView) firstDateView.findViewById(R.id.item_orderplay_selected_value);
                secondDateView = orderPlayList.getChildAt(3);
                txtSecondDate = (WMTextView) secondDateView.findViewById(R.id.item_orderplay_selected_value);

                if (!((firstViewValue.getText().toString() == null || firstViewValue.getText().toString() == "" || firstViewValue.getText().toString().isEmpty()) || (txtFirstDate.getText().toString() == null || txtFirstDate.getText().toString() == "" || txtFirstDate.getText().toString().isEmpty()) || (txtSecondDate.getText().toString() == null || txtSecondDate.getText().toString() == "" || txtSecondDate.getText().toString().isEmpty()))) {
                    orderPlay.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
                }

            }
        });
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

            if (position == 2) {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_orderplay_rehersal_view, parent, false);
                    holder = new ViewHolder();
                    holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_rehersal_value);
                    holder.imgOrderPlayInfo = (ImageView) convertView.findViewById(R.id.orderPlayInfo);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.txtTitle.setText(playList.get(position));
                holder.imgOrderPlayInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showAlert("title", "message");
                    }
                });
            } else {
                if (convertView == null) {
                    convertView = mInflater.inflate(R.layout.item_orderplay_view, parent, false);
                    holder = new ViewHolder();
                    holder.txtTitle = (WMTextView) convertView.findViewById(R.id.item_orderplay_value);
                    holder.item_orderplay_selected_value = (WMTextView) convertView.findViewById(R.id.item_orderplay_selected_value);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.txtTitle.setText(playList.get(position));

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (position == 1 || position == 3) {

                            processDate(position);
                        }

                        if (position == 0) {
                            numberOfPerformances();
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


                        View view = orderPlayList.getChildAt(position);
                        WMTextView item_orderplay_selected_value = (WMTextView) view.findViewById(R.id.item_orderplay_selected_value);
                        item_orderplay_selected_value.setVisibility(View.VISIBLE);
                        item_orderplay_selected_value.setText(bDate);

                        firstView = orderPlayList.getChildAt(0);
                        firstViewValue = (WMTextView) firstView.findViewById(R.id.item_orderplay_selected_value);
                        firstDateView = orderPlayList.getChildAt(1);
                        txtFirstDate = (WMTextView) firstDateView.findViewById(R.id.item_orderplay_selected_value);
                        secondDateView = orderPlayList.getChildAt(3);
                        txtSecondDate = (WMTextView) secondDateView.findViewById(R.id.item_orderplay_selected_value);
                        if (!((firstViewValue.getText().toString() == null || firstViewValue.getText().toString() == "" || firstViewValue.getText().toString().isEmpty()) || (txtFirstDate.getText().toString() == null || txtFirstDate.getText().toString() == "" || txtFirstDate.getText().toString().isEmpty()) || (txtSecondDate.getText().toString() == null || txtSecondDate.getText().toString() == "" || txtSecondDate.getText().toString().isEmpty()))) {
                            orderPlay.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
                        }
                        if (((txtFirstDate.getText().toString() != null || txtFirstDate.getText().toString() != "") || (txtSecondDate.getText().toString() != null || txtSecondDate.getText().toString() != ""))) {
                            try {
                                Date firstDate = new SimpleDateFormat("dd/MM-yyyy", Locale.ENGLISH).parse(txtFirstDate.getText().toString());
                                Date seocndDate = new SimpleDateFormat("dd/MM-yyyy", Locale.ENGLISH).parse(txtSecondDate.getText().toString());
                                System.out.println(firstDate);
                                System.out.println(seocndDate);
                                if (firstDate.after(seocndDate) || firstDate.equals(seocndDate)) {
                                    item_orderplay_selected_value.setText("");
                                    orderPlay.setBackgroundColor(getResources().getColor(R.color.gray_color));
                                    showAlert("title", "message");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                    }


                });

        datePickerdialog.show();

    }


    public class ViewHolder {

        WMTextView txtTitle, item_orderplay_selected_value;
        ImageView imgOrderPlayInfo;
        Switch aSwitch;

    }

    void showAlert(String title, String message) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle(title);
        alert.setMessage(message);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub

                dialog.dismiss();
            }
        });
        alert.show();

    }

    void numberOfPerformances() {
        firstView = orderPlayList.getChildAt(0);
        firstViewValue = (WMTextView) firstView.findViewById(R.id.item_orderplay_selected_value);
        firstDateView = orderPlayList.getChildAt(1);
        txtFirstDate = (WMTextView) firstDateView.findViewById(R.id.item_orderplay_selected_value);
        secondDateView = orderPlayList.getChildAt(3);
        txtSecondDate = (WMTextView) secondDateView.findViewById(R.id.item_orderplay_selected_value);
        if (!((firstViewValue.getText().toString() == null || firstViewValue.getText().toString() == "" || firstViewValue.getText().toString().isEmpty()) || (txtFirstDate.getText().toString() == null || txtFirstDate.getText().toString() == "" || txtFirstDate.getText().toString().isEmpty()) || (txtSecondDate.getText().toString() == null || txtSecondDate.getText().toString() == "" || txtSecondDate.getText().toString().isEmpty()))) {
            orderPlay.setBackgroundColor(getResources().getColor(R.color.apptheme_color));
        }

        etFirstView = (EditText) firstView.findViewById(R.id.item_orderplay_selected_value_et);
        etFirstView.setVisibility(firstView.VISIBLE);
        firstViewValue.setVisibility(firstView.GONE);

        etFirstView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {

                if (Integer.parseInt(etFirstView.getText().toString()) > 4 || Integer.parseInt(etFirstView.getText().toString()) < 1) {
                    etFirstView.setText("");
                    orderPlay.setBackgroundColor(getResources().getColor(R.color.gray_color));
                    showAlert("title", "message");
                } else {
                    etFirstView.setVisibility(firstView.GONE);
                    firstViewValue.setVisibility(firstView.VISIBLE);
                    firstViewValue.setText(etFirstView.getText().toString());
                }


                return false;
            }
        });


    }
}
