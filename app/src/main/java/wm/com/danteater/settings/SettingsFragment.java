package wm.com.danteater.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import wm.com.danteater.BuildConfig;
import wm.com.danteater.R;

public class SettingsFragment extends Fragment {

    private ToggleButton toggleAutomaticLogin;
    private ToggleButton toggleShowLineNumber;
    private ToggleButton toggleShowCommentsForUserId;
    private TextView txtVersionNo;

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
        boolean shouldShowLineNumbers = preferences.getBoolean("showLineNumber", false);
        boolean shouldShowComments= preferences.getBoolean("showComments", false);


        txtVersionNo = (TextView)rootView.findViewById(R.id.txtVersionName);
        txtVersionNo.setText(String.format("Version %s",BuildConfig.VERSION_NAME));


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

        toggleShowLineNumber=(ToggleButton)rootView.findViewById(R.id.settingRepliknumre);
        toggleShowLineNumber.setChecked(shouldShowLineNumbers);

        toggleShowLineNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = toggleShowLineNumber.isChecked();
                SharedPreferences preferences = getActivity().getSharedPreferences("settings",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                boolean showShowLine = false;
                if (on) {
                   showShowLine = true;
                } else {
                    showShowLine = false;
                }
                editor.putBoolean("showLineNumber",showShowLine);
                editor.commit();
            }
        });


        toggleShowCommentsForUserId=(ToggleButton)rootView.findViewById(R.id.settingKommentarer);
        toggleShowCommentsForUserId.setChecked(shouldShowComments);

        toggleShowCommentsForUserId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean on = toggleShowCommentsForUserId.isChecked();
                SharedPreferences preferences = getActivity().getSharedPreferences("settings",getActivity().MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                boolean showShowLine = false;
                if (on) {
                    showShowLine = true;
                } else {
                    showShowLine = false;
                }
                editor.putBoolean("showComments",showShowLine);
                editor.commit();
            }
        });


        return rootView;
    }





}
