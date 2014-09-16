package wm.com.danteater.my_plays;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nirav on 16-09-2014.
 */
public class FragmentMyPlayPupil extends Fragment {

    public static FragmentMyPlayPupil newInstance(String param1, String param2) {
        FragmentMyPlayPupil fragment = new FragmentMyPlayPupil();

        return fragment;
    }

    public FragmentMyPlayPupil() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
}
