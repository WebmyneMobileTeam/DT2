package wm.com.danteater.Messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.R;
import wm.com.danteater.app.BaseActivity;
import wm.com.danteater.app.PlayTabActivity;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.API;
import wm.com.danteater.model.AdvancedSpannableString;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.model.DatabaseWrapper;
import wm.com.danteater.model.RecordedAudio;
import wm.com.danteater.model.SharedPreferenceRecordedAudio;
import wm.com.danteater.model.TextWatcherAdapter;
import wm.com.danteater.model.TimeStampComparator;
import wm.com.danteater.my_plays.FragmentMyPlay;
import wm.com.danteater.my_plays.ReadActivityFromPreview;
import wm.com.danteater.my_plays.ShareActivityForPerform;
import wm.com.danteater.tab_read.ReadFragment;

public class ChatViewActivity extends BaseActivity {

    private ArrayList<MessagesForConversation> messagesForConversationArrayList=new ArrayList<MessagesForConversation>();
    MessagesForConversation messagesForConversationLastObject;
    private int lineNumber;
    private String response,toUserId;
    static final int ITEM_TYPE_ME = 0;
    static final int ITEM_TYPE_SENDER = 1;
    public static int STATE_RECORD = 0;
    public static int STATE_CHAT = 3;
    private User currentUser;
    ListView listChat;
    private WMTextView btnSendMessage;
    private EditText etMessageValue;
    private HUD dialog;
    private HUD dialog_next;
    private ChatAdapter chatAdapter;
    int plyIDAfterUpdate = 0;
    int playid = 0;
    Play ply;
    boolean isSound=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);
        btnSendMessage=(WMTextView)findViewById(R.id.btnSendMessage);
        etMessageValue=(EditText)findViewById(R.id.etChatMessage);

        etMessageValue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String string = s.toString();
                if(string.length() == 0 || string.isEmpty()){

                    btnSendMessage.setTextColor(Color.parseColor("#ababab"));

                }else{
                    btnSendMessage.setTextColor(getResources().getColor(R.color.apptheme_color));
                }
            }
        });

        listChat = (ListView)findViewById(R.id.listViewChat);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(this,"user_pref", 0);
        currentUser = complexPreferences.getObject("current_user", User.class);
        response = getIntent().getExtras().getString("messages");
        toUserId=getIntent().getStringExtra("fromUser");
        txtHeader.setText(toUserId);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // call API sendMessageToUser

                listChat.setSelection(messagesForConversationArrayList.size()-1);
                sendMessageToUser();
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
                chatAdapter=new ChatAdapter(ChatViewActivity.this);
                listChat.setAdapter(chatAdapter);
                listChat.setSelection(messagesForConversationArrayList.size()-1);

            }
        }.execute();


    }


    private void sendMessageToUser() {

        final JSONObject requestParams=new JSONObject();
        try {
            requestParams.put("OrderId", messagesForConversationLastObject.OrderId+"");
            requestParams.put("LineId", messagesForConversationLastObject.LineId+"");
            requestParams.put("FromUserId", currentUser.getUserId()+"");
            requestParams.put("ToUserId", toUserId+"");
            String[] lines = messagesForConversationLastObject.MessageText.split(System.getProperty("line.separator"));
            String firstLine = lines[0];
            String secondline = lines[1];

            requestParams.put("MessageText", firstLine+"\n"+secondline+"\n"+etMessageValue.getText().toString().trim()+"");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dialog = new HUD(ChatViewActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("");
                dialog.show();
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try
                {
                    Reader readerForNone = API.callWebservicePost("http://api.danteater.dk/api/Message", requestParams.toString());
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {
                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
//                    Log.e("response", response + " ");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                dialog.dismiss();

            }
        }.execute();
    }

    private void initForDisplayingData() {

        Type listType=new TypeToken<List<MessagesForConversation>>(){
        }.getType();

        messagesForConversationArrayList=new GsonBuilder().create().fromJson(response,listType);
        // Get Last Message Object
        Collections.sort(messagesForConversationArrayList, new TimeStampComparator());

        messagesForConversationLastObject=messagesForConversationArrayList.get(messagesForConversationArrayList.size()-1);
        setupReceivedMessages(messagesForConversationArrayList);


    }

    private void setupReceivedMessages(ArrayList<MessagesForConversation> messagesForConversationArrayList) {

        for(MessagesForConversation message: messagesForConversationArrayList){
            if(message.isRead.equalsIgnoreCase("false")){
                // call API markMessageAsRead
//                Log.e("unread message","found");
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
//                    Log.e("reader", readerForNone + "");

                    StringBuffer response = new StringBuffer();
                    int i = 0;
                    do {

                        i = readerForNone.read();
                        char character = (char) i;
                        response.append(character);

                    } while (i != -1);
                    readerForNone.close();
//                    Log.e("response", response + " ");


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

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
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
            final MessagesForConversation msg = messagesForConversationArrayList.get(position);

            switch (type){

                case ITEM_TYPE_SENDER:
                    convertView = mInflater.inflate(R.layout.item_chat_left, parent,false);

                    WMTextView txtCircle = (WMTextView)convertView.findViewById(R.id.txtCircleChat);
                    txtCircle.setText(msg.FromUserId.toString().toUpperCase());

                    WMTextView txtChatData = (WMTextView)convertView.findViewById(R.id.txtChatData);
                    //TODO
                    try {
                        String[] lines = msg.MessageText.split(System.getProperty("line.separator"));

                        String visibleLines = lines[1] + "\n" + lines[2];
                        AdvancedSpannableString spannableString = new AdvancedSpannableString(visibleLines);
                        String[] linearrays = visibleLines.split(System.getProperty("line.separator"));
                        String firstLine = linearrays[0];
                        spannableString.setUnderLine(firstLine);

                        txtChatData.setText(spannableString);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    txtChatData.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] lines = msg.MessageText.split(System.getProperty("line.separator"));
//                            Log.e("line id: ",msg.LineId.substring(0,msg.LineId.lastIndexOf("-"))+"");
                            lineNumber=Integer.parseInt(lines[0].substring(lines[0].lastIndexOf("-")+1));
//                            Log.e("line number in chat activity",lineNumber+" ");
                            Play play=new Play();
                            play.OrderId=lines[0].substring(0,lines[0].lastIndexOf("-"));

                            dialog_next = new HUD(ChatViewActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                            dialog_next.title("Henter");
                            dialog_next.show();
                            gotoSpecificPage(play);

                        }
                    });
                    break;

                case ITEM_TYPE_ME:

                    convertView = mInflater.inflate(R.layout.item_chat_right, parent,false);

                    WMTextView txtChatD = (WMTextView)convertView.findViewById(R.id.txtChatData);
                    //TODO
                    try {
                        String[] line = msg.MessageText.split(System.getProperty("line.separator"));
                        Log.e("line 0: ",line[0]+"");
                        Log.e("line 1: ",line[1]+"");

                        String visibleLine = line[1] + "\n" + line[2];
                        final AdvancedSpannableString spannableStrin = new AdvancedSpannableString(visibleLine);
                        String[] linearray = visibleLine.split(System.getProperty("line.separator"));
                        String firstLineData = linearray[0];
                        spannableStrin.setUnderLine(firstLineData);
                        txtChatD.setText(spannableStrin);
                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                    txtChatD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            String[] line = msg.MessageText.split(System.getProperty("line.separator"));
//                            Log.e("line id: ",msg.LineId.substring(0,msg.LineId.lastIndexOf("-"))+"");
                            lineNumber=Integer.parseInt(line[0].substring(line[0].lastIndexOf("-")+1));
//                            Log.e("line number in chat activity",lineNumber+" ");
                            Play play=new Play();
                            play.OrderId=line[0].substring(0,line[0].lastIndexOf("-"));
                            dialog_next = new HUD(ChatViewActivity.this,android.R.style.Theme_Translucent_NoTitleBar);
                            dialog_next.title("Henter");
                            dialog_next.show();
                            gotoSpecificPage(play);
                        }
                    });
                    break;

            }

            return convertView;
        }


    }

    private void gotoSpecificPage(final Play play){


        DatabaseWrapper dbh = new DatabaseWrapper(ChatViewActivity.this);
        if(play.OrderId.contains("sound-"))
        {
            isSound=true;
            String[] orderId=play.OrderId.split("-");
            play.OrderId=orderId[1]+"-"+orderId[2];
            Log.e("new Order id:",play.OrderId+"");
        }

        dbh.openDataBase();
        boolean hasPlay = dbh.hasPlayWithPlayOrderIdText(play.OrderId);
        dbh.close();

        if(hasPlay == true){
            dbh.openDataBase();
            plyIDAfterUpdate = dbh.getPlayIdFromDBForOrderId(play.OrderId);
            dbh.close();
            SharedPreferences pre = getSharedPreferences("Plays", MODE_PRIVATE);
            SharedPreferences.Editor edi = pre.edit();
            edi.putInt("playid",plyIDAfterUpdate);
            edi.commit();
            gotoNextPage();
        }
        else{
//            Log.e("hasplay","false");
            // insert new play to db
//            Log.e("order id:....",API.link_retrievePlayContentsForPlayOrderId +play.OrderId+"");
            new CallWebService(API.link_retrievePlayContentsForPlayOrderId +play.OrderId,CallWebService.TYPE_JSONOBJECT) {

                @Override
                public void response(final String response) {

                    Log.i("Response full play : ",""+response);
                    new AsyncTask<String,Integer,String>(){

                        @Override
                        protected String doInBackground(String... params) {

                            Play receivedPlay = new GsonBuilder().create().fromJson(response, Play.class);
                            DatabaseWrapper db = new DatabaseWrapper(ChatViewActivity.this);
                            db.openDataBase();
                            db.insertPlay(receivedPlay, false);
                            db.close();

                            return null;
                        }

                        @Override
                        protected void onPostExecute(String s) {
                            super.onPostExecute(s);


                            SharedPreferences preferences = getSharedPreferences("Plays", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            String k = "PlayLatesteUpdateDate"+play.PlayId;
                            editor.putString(k,""+(int) (System.currentTimeMillis() / 1000));
                            editor.commit();

                            gotoNextPage();

                        }
                    }.execute();
                }

                @Override
                public void error(VolleyError error) {
                    dialog_next.dismiss();
                    Log.e("error",error+"");
                    Toast.makeText(ChatViewActivity.this,error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            }.start();

        }

    }

    private void gotoNextPage() {

        new AsyncTask<String,Integer,String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                SharedPreferences preferences = getSharedPreferences("Plays", MODE_PRIVATE);
                playid = preferences.getInt("playid",0);

            }

            @Override
            protected String doInBackground(String... params) {

                DatabaseWrapper dbh = new DatabaseWrapper(ChatViewActivity.this);
                dbh.openDataBase();
                ply = dbh.retrievePlayWithId(playid);
                dbh.close();

                return null;
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(ChatViewActivity.this, "mypref",0);
                complexPreferences.putObject("selected_play",ply);
                complexPreferences.commit();


                ReadFragment.sharedPreferenceRecordedAudio=new SharedPreferenceRecordedAudio();
                new CallWebService("http://api.danteater.dk/Api/Audio?UserId=&OrderId="+ply.OrderId+"&LineId=&isTeacher=false",CallWebService.TYPE_JSONARRAY) {

                    @Override
                    public void response(final String response) {
                        dialog_next.dismiss();
//                        Log.e("Response recorded audio  : ", "" + response);
                        Type listType = new TypeToken<ArrayList<RecordedAudio>>(){}.getType();
                        ArrayList<RecordedAudio> recordedList = new GsonBuilder().create().fromJson(response, listType);
                        ReadFragment.sharedPreferenceRecordedAudio.clearAudio(ChatViewActivity.this);
                        for(int i=0;i<recordedList.size();i++){
                            ReadFragment.sharedPreferenceRecordedAudio.saveAudio(ChatViewActivity.this, recordedList.get(i));
                        }
                        Intent i1 = new Intent(ChatViewActivity.this, ReadActivityForChat.class);
                        if(isSound==true) {
                            i1.putExtra("currentState",STATE_RECORD);
                        } else {
                            i1.putExtra("currentState",STATE_CHAT);
                        }

                        i1.putExtra("line_number",lineNumber);
//                        i1.putExtra("isSound",isSound);
                        startActivity(i1);

                    }

                    @Override
                    public void error(VolleyError error) {

                        Log.e("error  : ",""+error);


                    }
                }.start();





            }
        }.execute();


    }
}
