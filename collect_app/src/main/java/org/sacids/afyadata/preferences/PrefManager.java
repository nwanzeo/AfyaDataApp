package org.sacids.afyadata.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

/**
 * Created by Renfrid-Sacids on 6/17/2016.
 */
public class PrefManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "AfyaData";

    // All Shared Preferences Keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER = "username";


    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    //User Id
    public void setUserId(String userId) {
        editor.putString(KEY_USER_ID, userId);
        editor.commit();
    }

    public String getUserId() {
        return pref.getString(KEY_USER_ID, null);
    }

    //Username
    public void setUsername(String username) {
        editor.putString(KEY_USER, username);
        editor.commit();
    }

    public String getUsername() {
        return pref.getString(KEY_USER, null);
    }


    public void createLogin(int userId, String username) {
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USER, username);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("user_id", pref.getString(KEY_USER_ID, null));
        profile.put("username", pref.getString(KEY_USER, null));
        return profile;
    }
}

