package wm.com.danteater.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;



public class SharedPreferenceRecordedAudio {

    public static final String PREF_NAME = "SHARED_DATA_AUDIO";
    public static final String PREF_VALUE = "shared_values_for_audios";

    List<RecordedAudio> recordedAudios =new ArrayList<RecordedAudio>();

    public SharedPreferenceRecordedAudio() {
        super();
    }


    public void clearAudio(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        recordedAudios.clear();
        editor.clear();
        editor.commit();
    }
    public void saveAudio(Context context, RecordedAudio product) {
        SharedPreferences sharedPref;
        Editor editor;

//        if (recordedAudios == null) {
//            recordedAudios = new ArrayList<RecordedAudio>();
//        }
        recordedAudios.add(product);
        sharedPref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(recordedAudios);
        editor.putString(PREF_VALUE, jsonFavorites);
        Log.e("teacher list:",jsonFavorites+"");
        editor.commit();
    }

    public ArrayList<RecordedAudio> loadAudio(Context context) {
        SharedPreferences sharePref;
        List<RecordedAudio> productList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        RecordedAudio[] favoriteItems = gson.fromJson(jsonFavorites,RecordedAudio[].class);
        productList = new ArrayList<RecordedAudio>(Arrays.asList(favoriteItems));
        Log.e("teacher array",productList+"");
        return (ArrayList<RecordedAudio>) productList;
    }

}
