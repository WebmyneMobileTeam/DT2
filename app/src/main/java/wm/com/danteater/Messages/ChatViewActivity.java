package wm.com.danteater.Messages;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.PinnedHeaderListView;
import wm.com.danteater.customviews.SectionedBaseAdapter;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.AdvancedSpannableString;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.tab_read.ViewHolder;

public class ChatViewActivity extends BaseActivity {

    private ArrayList<MessagesForConversation> messagesForConversationArrayList=new ArrayList<MessagesForConversation>();
    private String response;
    private ArrayList<String> sections;

    static final int ITEM_TYPE_ME = 0;
    static final int ITEM_TYPE_SENDER = 1;

    private User user;
    ListView listChat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        listChat = (ListView)findViewById(R.id.listViewChat);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this,"user_pref", 0);
        user = complexPreferences.getObject("current_user", User.class);
        response = getIntent().getExtras().getString("messages");
        txtHeader.setText(getIntent().getStringExtra("fromUser"));
        getActionBar().setDisplayHomeAsUpEnabled(true);

        new AsyncTask<Void,Void,Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();

            }

            @Override

            protected Void doInBackground(Void... voids) {
                initForDisplayingData();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

               listChat.setAdapter(new ChatAdapter(ChatViewActivity.this));


            }
        }.execute();


    }

    private void initForDisplayingData() {

        Type listType=new TypeToken<List<MessagesForConversation>>(){
        }.getType();

        messagesForConversationArrayList=new GsonBuilder().create().fromJson(response,listType);
        Collections.reverse(messagesForConversationArrayList);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static java.util.Date float2Date(float nbSeconds) {
        java.util.Date date_origine;
        java.util.Calendar date = java.util.Calendar.getInstance();
        java.util.Calendar origine = java.util.Calendar.getInstance();
        origine.set(1970, Calendar.JANUARY, 1);
        date_origine = origine.getTime();
        date.setTime(date_origine);
        date.add(java.util.Calendar.SECOND, (int) nbSeconds);
        return date.getTime();
    }


    public class ChatAdapter extends BaseAdapter {

        private LayoutInflater mInflater;

        public ChatAdapter(Context context) {

            this.mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return messagesForConversationArrayList.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }


        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {

            MessagesForConversation msg = messagesForConversationArrayList.get(position);

            if(msg.FromUserId.equalsIgnoreCase(user.getUserId())){
                return ITEM_TYPE_ME;
            }else{
                return ITEM_TYPE_SENDER;
            }

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            int type = getItemViewType(position);
            MessagesForConversation msg = messagesForConversationArrayList.get(position);

            switch (type){

                case ITEM_TYPE_SENDER:


                    convertView = mInflater.inflate(R.layout.item_chat_left, parent,false);

                    WMTextView txtCircle = (WMTextView)convertView.findViewById(R.id.txtCircleChat);
                    txtCircle.setText(msg.FromUserId.toString().toUpperCase());

                    WMTextView txtChatData = (WMTextView)convertView.findViewById(R.id.txtChatData);

                    AdvancedSpannableString spannableString = new AdvancedSpannableString(msg.LineId+"\n"+msg.MessageText);
                    spannableString.setUnderLine(msg.LineId);

                    txtChatData.setText(spannableString);

                    break;

                case ITEM_TYPE_ME:

                    convertView = mInflater.inflate(R.layout.item_chat_right, parent,false);

                    WMTextView txtChatD = (WMTextView)convertView.findViewById(R.id.txtChatData);

                    AdvancedSpannableString spannableStrin = new AdvancedSpannableString(msg.LineId+"\n"+msg.MessageText);
                    spannableStrin.setUnderLine(msg.LineId);

                    txtChatD.setText(spannableStrin);

                    break;

            }

            return convertView;
        }


    }


}
