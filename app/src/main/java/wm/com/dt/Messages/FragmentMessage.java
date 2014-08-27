package wm.com.dt.Messages;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import wm.com.dt.R;

public class FragmentMessage extends Fragment {

    private ListView chatUserList;
    private ArrayList<String> titleList=new ArrayList<String>();
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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_message, container, false);
        chatUserList=(ListView)view.findViewById(R.id.chatUserList);
        titleList.add("teacher1");
        titleList.add("teacher2");
        titleList.add("teacher3");
        titleList.add("pupil1");
        titleList.add("pupil2");
        titleList.add("pupil3");
        chatUserList.setAdapter(new MessageAdapter(getActivity(), titleList));
        return view;
    }


    public class MessageAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> titleList;


        public MessageAdapter(Context context, ArrayList<String> titleList) {

            this.context = context;
            this.titleList = titleList;

        }

        public int getCount() {

            return titleList.size();

        }

        public Object getItem(int position) {
            return titleList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }


        class ViewHolder {


            TextView txtUsername,txtMessageImage,txtBadgeValue;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_message_view, parent, false);
                holder = new ViewHolder();
                holder.txtUsername = (TextView) convertView.findViewById(R.id.txtMessageUserName);
                holder.txtMessageImage = (TextView) convertView.findViewById(R.id.txtMessageImaage);
                holder.txtBadgeValue = (TextView) convertView.findViewById(R.id.txtMessageBadgeValue);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.txtUsername.setText(titleList.get(position));
            holder.txtMessageImage.setText(titleList.get(position).substring(0,2));
            if(position==2){
                holder.txtBadgeValue.setVisibility(View.VISIBLE);
            } else {
                holder.txtBadgeValue.setVisibility(View.GONE);
            }
            return convertView;

        }

    }



}
