package dk.danteater.danteater.excercise;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import dk.danteater.danteater.R;


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
