package wm.com.danteater.search;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.ListDialog;

public class FragmentSearch extends Fragment implements AdapterView.OnItemClickListener,ListDialog.setSelectedListner{

    private static int CLICKED_POSITION = 0;
    private HUD dialog;
    private TextView btnSearch;
    private ListView filterCategoryList;
    private ArrayList<Integer> imageList = new ArrayList<Integer>();
    private ArrayList<String> titleList = new ArrayList<String>();

    private SparseArray listSubItems;
    private String[] MEDVIRKENDE = {"Alle","1-5","6-10","11-15","16-20","21-30","30+"};
    private String[] ALDER = {"Alle","7-9","10-12","13-15","16+"};
    private String[] MUSIK = {"Intet","Lidt","Musical"};
    private String[] VARIGHED = {"Alle","1-30 min","30-45 min","45-60 min","60-75 min","75-120 min","Over 120 min"};


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

        listSubItems = new SparseArray();

        listSubItems.put(0,new ArrayList<String>(Arrays.asList(MEDVIRKENDE)));
        listSubItems.put(1,new ArrayList<String>(Arrays.asList(ALDER)));
        listSubItems.put(2,new ArrayList<String>(Arrays.asList(MUSIK)));
        listSubItems.put(3,new ArrayList<String>(Arrays.asList(VARIGHED)));

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

        CLICKED_POSITION = position;
        ListDialog dialog = new ListDialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Title");
        dialog.setItems((ArrayList) listSubItems.get(position));
        dialog.setSelectedListner(this);
        dialog.show();

    }


    @Override
    public void selected(String value) {

        View v = filterCategoryList.getChildAt(CLICKED_POSITION);
        TextView tv = (TextView)v.findViewById(R.id.txtSelectedValue);
        tv.setText(value);

    }
}


