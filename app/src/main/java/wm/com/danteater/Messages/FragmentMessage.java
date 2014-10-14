package wm.com.danteater.Messages;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.login.User;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;
import wm.com.danteater.my_plays.SharedUser;

public class FragmentMessage extends Fragment {

    private ListView chatUserListView;
    private User currentUser;
    private ArrayList<String> chatUserList=new ArrayList<String>();
    private ArrayList<MessageUnread> messageUnreadArrayList=new ArrayList<MessageUnread>();

    private HUD dialog;
    private Timer timer;
    private MessageAdapter adapter;
    public static FragmentMessage newInstance(String param1, String param2) {
        FragmentMessage fragment = new FragmentMessage();

        return fragment;
    }
    public FragmentMessage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((WMTextView) getActivity().getActionBar().getCustomView()).setText("Beskeder");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_conversation_table_view, container, false);
        chatUserListView =(ListView)view.findViewById(R.id.chatUserList);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("resume",".........................");
        //get Current User
        ComplexPreferences complexPreferencesForUser = ComplexPreferences.getComplexPreferences(getActivity(), "user_pref", 0);
        currentUser = complexPreferencesForUser.getObject("current_user", User.class);
        //call API getAllMessageUsers
        getAllMessageUsers();



//        timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//
//            }
//        },0,1000*60*10);

    }

    public void getAllMessageUsers() {

        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("");
        dialog.show();
        new CallWebService("http://api.danteater.dk/api/MessageUser/"+currentUser.getUserId(), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                dialog.dismiss();
                Type listType = new TypeToken<List<String>>() {
                }.getType();
                chatUserList = new GsonBuilder().create().fromJson(response,listType);
                adapter= new MessageAdapter(getActivity(), chatUserList);
                chatUserListView.setAdapter(adapter);
                // call API getAllUnreadMessagesForUser
                getAllUnreadMessagesForUser();
            }

            @Override
            public void error(VolleyError error) {

                Log.e("error:",error+"");

            }
        }.start();
    }


    private void getAllUnreadMessagesForUser() {

        new CallWebService("http://api.danteater.dk/api/MessageUnread/"+currentUser.getUserId(), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
            Type listType=new TypeToken<List<MessageUnread>>(){
            }.getType();
                if(response !=null) {
                    messageUnreadArrayList = new GsonBuilder().create().fromJson(response, listType);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void error(VolleyError error) {

              Log.e("error: ",error+"");

            }
        }.start();
    }




    public class MessageAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> chatUserList;
        public MessageAdapter(Context context, ArrayList<String> chatUserList) {
            this.context = context;
            this.chatUserList = chatUserList;
            Collections.sort(chatUserList);
        }


        public int getCount() {
            return chatUserList.size();
        }

        public Object getItem(int position) {

            return chatUserList.get(position);
        }

        public long getItemId(int position) {

            return position;
        }


        class ViewHolder {
            TextView txtUsername,txtMessageImage,txtBadgeValue;
        }

        public View getView(final int position, View convertView,ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_conversation_table_view_cell, parent, false);
                holder = new ViewHolder();
                holder.txtUsername = (TextView) convertView.findViewById(R.id.txtMessageUserName);
                holder.txtMessageImage = (TextView) convertView.findViewById(R.id.txtMessageImaage);
                holder.txtBadgeValue = (TextView) convertView.findViewById(R.id.txtMessageBadgeValue);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtUsername.setText(chatUserList.get(position));
            holder.txtMessageImage.setText(chatUserList.get(position).substring(0,2));
            if (messageUnreadArrayList.size() > 0) {
                for (int i = 0; i < messageUnreadArrayList.size(); i++) {
//                    Log.e("unread id:",messageUnreadArrayList.get(i).FromUserId+"");
//                    Log.e("read id:",chatUserList.get(position)+"");
                    if (messageUnreadArrayList.get(i).FromUserId.equalsIgnoreCase(chatUserList.get(position))) {
                        holder.txtBadgeValue.setVisibility(View.VISIBLE);
//                        Log.e("count value: ", messageUnreadArrayList.get(i).unreadMessageCount.toString() + "");
                        holder.txtBadgeValue.setText(messageUnreadArrayList.get(i).unreadMessageCount.toString());
                    }
//                    } else {
//                        holder.txtBadgeValue.setVisibility(View.GONE);
//                    }

                }
                notifyDataSetChanged();
            }

            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    getAllMessagesForConversation(position);
                }
            });
            return convertView;

        }

    }

    private void getAllMessagesForConversation(final int position) {
        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("");
        dialog.show();
        Log.e("generated url: ","http://api.danteater.dk/api/Message/"+currentUser.getUserId()+"?recipient="+chatUserList.get(position));
        new CallWebService("http://api.danteater.dk/api/Message/"+currentUser.getUserId()+"?recipient="+chatUserList.get(position), CallWebService.TYPE_JSONARRAY) {

            @Override
            public void response(String response) {
                dialog.dismiss();


                Intent i = new Intent(getActivity(),ChatViewActivity.class);
                i.putExtra("messages",response);
                i.putExtra("fromUser",chatUserList.get(position));
                startActivity(i);

            }

            @Override
            public void error(VolleyError error) {

                Log.e("error: ",error+"");

            }
        }.start();
    }

}
