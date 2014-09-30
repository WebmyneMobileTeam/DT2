package wm.com.danteater.tab_share;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import wm.com.danteater.R;

public class FragmentPupils extends Fragment {

    private static final String ARG_POSITION = "position";
    private int position;

    public static FragmentPupils newInstance(int position) {

        FragmentPupils fragment = new FragmentPupils();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        position = getArguments().getInt(ARG_POSITION);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View convertView = inflater.inflate(R.layout.fragment_fragment_pupils, container, false);
        TextView tvTemp = (TextView)convertView.findViewById(R.id.txtTempPupil);
        tvTemp.setText(""+position);


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



}
