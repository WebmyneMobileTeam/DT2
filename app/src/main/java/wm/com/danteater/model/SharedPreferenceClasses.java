package wm.com.danteater.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.gson.Gson;

import wm.com.danteater.login.Group;
import wm.com.danteater.login.User;

public class SharedPreferenceClasses {

    public static final String PREF_NAME = "SHARED_DATA_CLASS";
    public static final String PREF_VALUE = "shared_values_for_classes";
    List<Group> classes =new ArrayList<Group>();


    public SharedPreferenceClasses() {
        super();
    }


    public void clearClass(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        classes.clear();
        editor.clear();
        editor.commit();
    }
    public void saveClass(Context context, Group product) {
        SharedPreferences sharePref;
        Editor editor;

        if (classes == null) {
            classes = new ArrayList<Group>();
        }
        classes.add(product);
        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharePref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(classes);
        editor.putString(PREF_VALUE, jsonFavorites);
        editor.commit();
    }

    public ArrayList<Group> loadClass(Context context) {
        SharedPreferences sharePref;
        List<Group> productList;

        sharePref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);

        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        Group[] favoriteItems = gson.fromJson(jsonFavorites,Group[].class);
        productList = Arrays.asList(favoriteItems);
        productList = new ArrayList<Group>(productList);


        return (ArrayList<Group>) productList;
    }





}
