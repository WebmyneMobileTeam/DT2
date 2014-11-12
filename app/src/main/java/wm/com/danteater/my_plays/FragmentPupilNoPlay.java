package wm.com.danteater.my_plays;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import wm.com.danteater.R;

/**
 * Created by nirav on 02-10-2014.
 */
public class FragmentPupilNoPlay extends Fragment {




    public static FragmentPupilNoPlay newInstance(String param1, String param2) {
        FragmentPupilNoPlay fragment = new FragmentPupilNoPlay();

        return fragment;
    }
    public FragmentPupilNoPlay() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_student_no_plays, container, false);


        return view;
    }

}
