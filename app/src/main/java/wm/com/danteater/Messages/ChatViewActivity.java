package wm.com.danteater.Messages;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.AdvancedSpannableString;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;

public class ChatViewActivity extends BaseActivity {

    private ArrayList<MessagesForConversation> messagesForConversationArrayList=new ArrayList<MessagesForConversation>();
    private String response;
    static final int ITEM_TYPE_ME = 0;
    static final int ITEM_TYPE_SENDER = 1;
    private User currentUser;
    ListView listChat;
    private WMTextView btnSendMessage;
    private EditText etMessageValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        btnSendMessage=(WMTextView)findViewById(R.id.btnSendMessage);
        etMessageValue=(EditText)findViewById(R.id.etChatMessage);
        listChat = (ListView)findViewById(R.id.listViewChat);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this,"user_pref", 0);
        currentUser = complexPreferences.getObject("current_user", User.class);
        response = getIntent().getExtras().getString("messages");

        txtHeader.setText(getIntent().getStringExtra("fromUser"));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call API sendMessageToUser
            }
        });
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
                listChat.setSelection(messagesForConversationArrayList.size()-1);

            }
        }.execute();


    }


    private void sendMessageToUser() {

        // Get Last Message Object
        MessagesForConversation messagesForConversationLastObject=messagesForConversationArrayList.get(messagesForConversationArrayList.size()-1);

        JSONObject requestParams=new JSONObject();
        try {
            requestParams.put("OrderId", messagesForConversationLastObject.OrderId+"");
            requestParams.put("LineId", messagesForConversationLastObject.LineId+"");
            requestParams.put("FromUserId", currentUser.getUserId()+"");
            requestParams.put("ToUserId", "");
            requestParams.put("MessageText", etMessageValue.getText().toString().trim()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //TODO
        new CallWebService("http://api.danteater.dk/api/Message", CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {


            }

            @Override
            public void error(VolleyError error) {

               Log.e("error:",error+"");

            }
        }.start();
    }
    private void initForDisplayingData() {

        Type listType=new TypeToken<List<MessagesForConversation>>(){
        }.getType();

        messagesForConversationArrayList=new GsonBuilder().create().fromJson(response,listType);
        Collections.reverse(messagesForConversationArrayList);
        setupReceivedMessages(messagesForConversationArrayList);


    }

    private void setupReceivedMessages(ArrayList<MessagesForConversation> messagesForConversationArrayList) {

        for(MessagesForConversation message: messagesForConversationArrayList){
            if(message.isRead.equalsIgnoreCase("false")){
                // call API markMessageAsRead
                Log.e("unread message","found");
                markMessageAsRead(message);
            }
        }
    }


    private void markMessageAsRead(final MessagesForConversation messagesForConversation){
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/Message?msgID=" + messagesForConversation.Id, "");
                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
                    Log.e("response", response + " ");


                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }


        }.execute();


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

            if(msg.FromUserId.equalsIgnoreCase(currentUser.getUserId())){
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
