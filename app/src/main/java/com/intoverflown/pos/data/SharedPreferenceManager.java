package com.intoverflown.pos.data;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceManager {

    public Context context;

    public SharedPreferences preferences;
    public SharedPreferences.Editor editor;
    public String SHARED_PREF_NAME = "pos_pref";
    public String KEY_ID = "Id";
    public String KEY_FIRSTNAME = "FirstName";
    public String KEY_LASTNAME = "LastName";
    public String KEY_USERNAME = "Username";
    public String KEY_ROLE = "Role";
    public String KEY_TOKEN = "Token";

    public void writePrefResponse(String id, String fName, String lName,
                                  String userName, String role, String token) {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(KEY_ID, id);
        editor.putString(KEY_FIRSTNAME, fName);
        editor.putString(KEY_LASTNAME, lName);
        editor.putString(KEY_USERNAME, userName);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_TOKEN, token);
        editor.apply();
    }

    public String getId() {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_ID, "Id");
    }

    public String getToken() {
        preferences = context.getSharedPreferences(SHARED_PREF_NAME, MODE_PRIVATE);
        return preferences.getString(KEY_ID, "Token");
    }
}