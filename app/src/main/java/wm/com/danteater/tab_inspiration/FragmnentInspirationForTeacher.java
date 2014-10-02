package wm.com.danteater.tab_inspiration;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wm.com.danteater.R;
import wm.com.danteater.excercise.FragmentExcerciseForStudent;

/**
 * Created by nirav on 02-10-2014.
 */
public class FragmnentInspirationForTeacher  extends Fragment{



    public static FragmnentInspirationForTeacher newInstance(String param1, String param2) {
        FragmnentInspirationForTeacher fragment = new FragmnentInspirationForTeacher();

        return fragment;
    }
    public FragmnentInspirationForTeacher() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_teacher_inspiration, container, false);


        return view;
    }

}
