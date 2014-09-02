package wm.com.danteater.tab_read;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import wm.com.danteater.Play.AssignedUsers;
import wm.com.danteater.Play.Play;
import wm.com.danteater.Play.PlayLines;
import wm.com.danteater.R;
import wm.com.danteater.customviews.HUD;
import wm.com.danteater.customviews.WMTextView;
import wm.com.danteater.model.CallWebService;
import wm.com.danteater.model.ComplexPreferences;



public class ReadFragment extends Fragment {
    private Play selectedPlay;
    private ArrayList<PlayLines> playLinesesList;
    private ArrayList<AssignedUsers> assignedUsersesList=new ArrayList<AssignedUsers>();
    private HUD dialog;
    public static ReadFragment newInstance(String param1, String param2) {
        ReadFragment fragment = new ReadFragment();

        return fragment;
    }
    public ReadFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ComplexPreferences complexPreferences = ComplexPreferences.getComplexPreferences(getActivity(), "mypref", 0);
        selectedPlay = complexPreferences.getObject("selected_play",Play.class);
        ((WMTextView)getActivity().getActionBar().getCustomView()).setText(selectedPlay.Title);


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_read, container, false);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialog = new HUD(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        dialog.title("Mine Stykker");
        dialog.show();
        new CallWebService("http://api.danteater.dk/api/playfull/"+selectedPlay.OrderId, CallWebService.TYPE_JSONOBJECT) {

            @Override
            public void response(String response) {
                dialog.dismiss();
                Log.e("Response:", response + "");
                handledataafterresponseVolly1(response);
            }

            @Override
            public void error(VolleyError error) {
                dialog.dismissWithStatus(R.drawable.ic_navigation_cancel, "Error");
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        }.start();
    }

    public void handledataafterresponseVolly1(final String response) {
        // TODO Auto-generated method stub

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {


                Play play = new GsonBuilder().create().fromJson(
                        response, Play.class);
                playLinesesList=play.playLinesList;

                Log.e("PlayId",play.PlayId+"");
                for(int i=0;i<playLinesesList.size();i++) {

//                    Log.e("LineCount",playLinesesList.get(i).LineCount+"");
                    for(int j=0;j<playLinesesList.get(i).assignedUsersList.size();j++){

//                        Log.e("AssignedUserId",playLinesesList.get(i).assignedUsersList.get(j).AssignedUserId+"");
                    }
                    for(int j=0;j<playLinesesList.get(i).textLinesList.size();j++){
                        if(playLinesesList.get(i).textLinesList.get(j).LineText !=null) {
                            Log.e("LineText", playLinesesList.get(i).textLinesList.get(j).LineText + "");
                        }
                    }
                    for(int j=0;j<playLinesesList.get(i).castMatchesList.size();j++){

//                        Log.e("castMatchesList",playLinesesList.get(i).castMatchesList.get(j)+"");
                    }




                }

            }
        });

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
