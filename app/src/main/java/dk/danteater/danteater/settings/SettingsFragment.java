package dk.danteater.danteater.settings;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import dk.danteater.danteater.BuildConfig;
import dk.danteater.danteater.R;


public class SettingsFragment extends Fragment {

    private ToggleButton toggleAutomaticLogin;
    private ToggleButton toggleShowLineNumber;
    private ToggleButton toggleShowCommentsForUserId;
    private TextView txtVersionNo;
    private Button btnappshare;

    private static String shareLink = "Hej med dig,\n\nJeg har prøvet Danteater-appen, og den er rigtig god.\nDu kan hente den her: https://itunes.apple.com/dk/app/danteater/id900901253?mt=8\n" +
            "Du kan hente den her: https://play.google.com/store/apps/details?id=wm.com.danteater";


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
        txtVersionNo.setText(String.format("Version %s", BuildConfig.VERSION_NAME));

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


        btnappshare = (Button)rootView.findViewById(R.id.btnappshare);
        registerForContextMenu(btnappshare);
        btnappshare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                getActivity().openContextMenu(v);
            }
        });


        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        menu.add(0,1, Menu.NONE,"Kopier");
        menu.add(0,2,Menu.NONE,"Email");


    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {



        switch (item.getItemId()){

            case 1:
                copyProcess();
              //  takePicture();
                break;

            case 2:
                shareProcess();
             //   chooseImage();
                break;
        }

        return super.onContextItemSelected(item);
    }

    private void shareProcess() {

        final Intent emailIntent = new Intent(
                android.content.Intent.ACTION_SEND);

		    /* Fill it with Data */
        emailIntent.setType("plain/text");
        /*emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,
                new String[] { "info@daryabsofe.com" });*/
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                "Danteater App");

        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                shareLink);

		    /* Send it off to the Activity-Chooser */

        startActivityForResult(
                Intent.createChooser(emailIntent, "Send mail..."),
                10);

    }

    private void copyProcess() {

        ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(getActivity().CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("",shareLink);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(getActivity(),"Kopieret", Toast.LENGTH_SHORT).show();



    }


}
