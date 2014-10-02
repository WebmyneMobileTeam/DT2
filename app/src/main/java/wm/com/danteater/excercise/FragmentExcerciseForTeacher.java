package wm.com.danteater.excercise;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.util.ArrayList;

import wm.com.danteater.R;
import wm.com.danteater.customviews.WMTextView;

public class FragmentExcerciseForTeacher extends Fragment {

    private GridView tViewOne,tViewTwo,sView;

    private ArrayList<String> tViewOneList=new ArrayList<String>();
    private ArrayList<String> tViewTwoList=new ArrayList<String>();
    private ArrayList<String> sViewList=new ArrayList<String>();
    private ArrayList<Integer> tVideoViewOneList=new ArrayList<Integer>();
    private ArrayList<Integer> tVideoViewTwoList=new ArrayList<Integer>();
    private ArrayList<Integer> sVideoViewList=new ArrayList<Integer>();
    private GridAdapter gridAdapter;
    public static FragmentExcerciseForTeacher newInstance(String param1, String param2) {
        FragmentExcerciseForTeacher fragment = new FragmentExcerciseForTeacher();

        return fragment;
    }
    public FragmentExcerciseForTeacher() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_teacher_excercises, container, false);
        tViewOne=(GridView)view.findViewById(R.id.videos_one_teacherview);
        tViewTwo=(GridView)view.findViewById(R.id.videos_two_teacherview);
        sView=(GridView)view.findViewById(R.id.videos_studentview);
        tViewOneList.add("Intro");
        tViewOneList.add("Intro");
        tViewOneList.add("Intro");
        tVideoViewOneList.add(R.raw.one);
        tVideoViewOneList.add(R.raw.one);
        tVideoViewOneList.add(R.raw.one);
        tViewTwoList.add("Intro");
        tViewTwoList.add("Fokus");
        tViewTwoList.add("Taleteknik");
        tViewTwoList.add("Følelser");
        tVideoViewTwoList.add(R.raw.one);
        tVideoViewTwoList.add(R.raw.one);
        tVideoViewTwoList.add(R.raw.one);
        tVideoViewTwoList.add(R.raw.one);
        sViewList.add("Intro");
        sViewList.add("Fokus");
        sViewList.add("Taleteknik");
        sViewList.add("Følelser");
        sVideoViewList.add(R.raw.one);
        sVideoViewList.add(R.raw.one);
        sVideoViewList.add(R.raw.one);
        sVideoViewList.add(R.raw.one);
        boolean teacherViw=true;
        if(teacherViw==true) {
            tViewOne.setAdapter(new GridAdapter(getActivity(), tViewOneList,tVideoViewOneList));
            tViewTwo.setAdapter(new GridAdapter(getActivity(),tViewTwoList,tVideoViewTwoList));
        } else {
            sView.setAdapter(new GridAdapter(getActivity(), sViewList,sVideoViewList));
        }

        return view;
    }

    public class GridAdapter extends BaseAdapter {

        Context context;
        LayoutInflater inflater;
        private ArrayList<String> list;
        private ArrayList<Integer> videoList;
        public GridAdapter(Context context, ArrayList<String> list,ArrayList<Integer> videoList) {
            this.context = context;
            this.list = list;
            this.videoList=videoList;
        }


        public int getCount() {

            return list.size();
        }

        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        public class ViewHolder {
            WMTextView txtVideoName;
            ImageView imageView;
        }

        public View getView(final int position, View convertView,
                            ViewGroup parent) {

           final ViewHolder holder;
            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item_inspiration_view, parent, false);
                holder = new ViewHolder();
                holder.txtVideoName = (WMTextView) convertView.findViewById(R.id.video_title);
                holder.imageView = (ImageView) convertView.findViewById(R.id.video_thumbnail_view);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.txtVideoName.setText(list.get(position));


            Uri  videoURI = Uri.parse("android.resource://" + getActivity().getPackageName() +"/"
                    +R.raw.intro);
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(getActivity(), videoURI);
            Bitmap bitmap = retriever
                    .getFrameAtTime(10,MediaMetadataRetriever.OPTION_CLOSEST);
            Drawable drawable = new BitmapDrawable(getResources(), bitmap);
            holder.imageView.setImageDrawable(drawable);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("video path file",videoList.get(position)+"");
                    Intent intent=new Intent(getActivity(),VideoPlay.class);
                    intent.putExtra("video_path",videoList.get(position).toString());
                    startActivity(intent);
                }
            });


            return convertView;
        }

    }

}
