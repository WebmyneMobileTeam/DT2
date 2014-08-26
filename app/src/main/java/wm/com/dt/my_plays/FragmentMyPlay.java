package wm.com.dt.my_plays;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import wm.com.dt.R;
import wm.com.dt.customviews.SegmentedGroup;


public class FragmentMyPlay extends Fragment implements RadioGroup.OnCheckedChangeListener{

    private SegmentedGroup segmentedGroupPlays;
    private RadioButton rbBestilte;
    private RadioButton rbGennemsyn;

    public static FragmentMyPlay newInstance(String param1, String param2) {
        FragmentMyPlay fragment = new FragmentMyPlay();

        return fragment;
    }
    public FragmentMyPlay() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View convertView = inflater.inflate(R.layout.fragment_my_play, container, false);
        segmentedGroupPlays = (SegmentedGroup)convertView.findViewById(R.id.segmentedMyPlays);
        rbBestilte = (RadioButton)convertView.findViewById(R.id.rbBestilte);
        rbGennemsyn = (RadioButton)convertView.findViewById(R.id.rbGennemsyn);

        segmentedGroupPlays.setOnCheckedChangeListener(this);


        return convertView;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onResume() {
        super.onResume();
        rbGennemsyn.setChecked(true);
       
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {

        switch (checkedId){

            case R.id.rbBestilte:



                break;

            case R.id.rbGennemsyn:



                break;

        }


    }
}
