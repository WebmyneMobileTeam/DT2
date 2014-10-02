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

public class FragmentExcerciseForStudent extends Fragment {


    public static FragmentExcerciseForStudent newInstance(String param1, String param2) {
        FragmentExcerciseForStudent fragment = new FragmentExcerciseForStudent();

        return fragment;
    }
    public FragmentExcerciseForStudent() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_student_excercises, container, false);


        return view;
    }



}
