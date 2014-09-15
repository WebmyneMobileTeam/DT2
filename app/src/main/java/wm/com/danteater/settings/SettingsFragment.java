package wm.com.danteater.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import wm.com.danteater.R;

public class SettingsFragment extends Fragment {

    private ToggleButton toggleAutomaticLogin;
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    public SettingsFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View rootView= inflater.inflate(R.layout.activity_settings, container, false);
        SharedPreferences preferences = getActivity().getSharedPreferences("settings",getActivity().MODE_PRIVATE);
        boolean shouldShowLoginView = preferences.getBoolean("shouldShowLoginView", false);
        toggleAutomaticLogin=(ToggleButton)rootView.findViewById(R.id.settingAutomatiskeLogin);
        toggleAutomaticLogin.setChecked(shouldShowLoginView);
        toggleAutomaticLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = toggleAutomaticLogin.isChecked();
                SharedPreferences preferences = getActivity().getSharedPreferences("settings",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                if (on) {
                    editor.putBoolean("shouldShowLoginView", true);
                    editor.commit();
                } else {

                    editor.putBoolean("shouldShowLoginView", false);
                    editor.commit();
                }
            }
        });
        return rootView;
    }



}
