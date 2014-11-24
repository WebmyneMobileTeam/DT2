package wm.com.danteater.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

import com.google.gson.Gson;

import wm.com.danteater.login.Group;
import wm.com.danteater.login.User;

public class SharedPreferenceTeachers {

	public static final String PREF_NAME = "SHARED_DATA_TEACHERS";
	public static final String PREF_VALUE = "shared_values_for_teachers";

    List<User> teachers =new ArrayList<User>();

	public SharedPreferenceTeachers() {
		super();
	}


    public void clearTeacher(Context context) {
        SharedPreferences sharedPref;
        Editor editor;
        sharedPref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        teachers.clear();
        editor.clear();
        editor.commit();
    }
    public void saveTeacher(Context context, User product) {
        SharedPreferences sharedPref;
        Editor editor;

        if (teachers == null) {
            teachers = new ArrayList<User>();
        }
        teachers.add(product);
        sharedPref = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE);
        editor = sharedPref.edit();
        Gson gson = new Gson();
        String jsonFavorites = gson.toJson(teachers);
        editor.putString(PREF_VALUE, jsonFavorites);
        Log.e("teacher list:",jsonFavorites+"");
        editor.commit();
    }

    public ArrayList<User> loadTeacher(Context context) {
        SharedPreferences sharePref;
        List<User> productList;
        sharePref = context.getSharedPreferences(PREF_NAME,context.MODE_PRIVATE);
        String jsonFavorites = sharePref.getString(PREF_VALUE, null);
        Gson gson = new Gson();
        User[] favoriteItems = gson.fromJson(jsonFavorites,User[].class);
        productList = new ArrayList<User>(Arrays.asList(favoriteItems));
        Log.e("teacher array",productList+"");
        return (ArrayList<User>) productList;
    }

}
