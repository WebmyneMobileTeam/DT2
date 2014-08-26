package wm.com.dt.search;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


import uk.me.lewisdeane.ldialogs.CustomListDialog;
import wm.com.dt.R;
import wm.com.dt.customviews.HUD;

public class FragmentSearch extends Fragment implements AdapterView.OnItemClickListener {

    private HUD dialog;
    private TextView btnSearch;
    private ListView filterCategoryList;
    private ArrayList<Integer> imageList = new ArrayList<Integer>();
    private ArrayList<String> titleList = new ArrayList<String>();

    public static FragmentSearch newInstance(String param1, String param2) {
        FragmentSearch fragment = new FragmentSearch();
        return fragment;
    }

    public FragmentSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        imageList.add(R.drawable.ic_number_of_participants);
        imageList.add(R.drawable.ic_age);
        imageList.add(R.drawable.ic_music);
        imageList.add(R.drawable.ic_duration);

        titleList.add("Medvirkende");
        titleList.add("Alder");
        titleList.add("Musik");
        titleList.add("Varighed");
        filterCategoryList = (ListView) view.findViewById(R.id.filterCategoryList);
        filterCategoryList.setAdapter(new FilterCategoryAdapter(getActivity(), titleList, imageList));
        filterCategoryList.setOnItemClickListener(this);
        btnSearch = (TextView)view.findViewById(R.id.btnSearch);


        return view;
    }


    public class FilterCategoryAdapter extends BaseAdapter {

        Context context;

        LayoutInflater inflater;

        ArrayList<String> titleList;
        ArrayList<Integer> imageList;

        public FilterCategoryAdapter(Context context, ArrayList<String> titleList, ArrayList<Integer> imageList) {

            this.context = context;
            this.titleList = titleList;
            this.imageList = imageList;
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

            ImageView image;
            TextView txtTitle;

        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

            final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_search_filter_view, parent, false);
                holder = new ViewHolder();
                holder.txtTitle = (TextView) convertView.findViewById(R.id.txtTitle);
                holder.image = (ImageView) convertView.findViewById(R.id.filterImage);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.image.setImageResource(imageList.get(position));
            holder.txtTitle.setText(titleList.get(position));
            return convertView;

        }

    }

    @Override
    public void onResume() {
        super.onResume();

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new HUD(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
                dialog.title("Mine Stykker");
                dialog.show();
                new CountDownTimer(3500,1000){


                    @Override
                    public void onTick(long millisUntilFinished) {

                    }


                    @Override
                    public void onFinish() {

                        dialog.dismissWithStatus(R.drawable.ic_navigation_accept,"Success");


                    }
                }.start();

            }
        });




            }




    @Override
    public void onItemClick(AdapterView <?> parent, View view, int position, long id) {

        switch (position) {

            case 0:
                Log.e("hiii", "fddf");
                String[] items={"Alle (all)","1-5","6-10","11-15","16-20","21-30","30+"};
                CustomListDialog customListDialog = new CustomListDialog(getActivity());

                customListDialog.setTitle("Medvirkende")
                        .setItems(items);

                customListDialog.show();

                break;

            case 1:

                break;

            case 2:

                break;

            case 3:

                break;

        }








}
}


