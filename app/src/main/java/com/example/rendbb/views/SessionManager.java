package com.example.rendbb.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    private static final String TAG = "SessionManager";

    private SharedPreferences pref;
    private Editor editor;
    private Context context;

    private static final String PREF_NAME = "RENDBBPref";
    private static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USERNAME = "username";

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String username) {
        try {
            editor.putBoolean(IS_LOGIN, true);
            editor.putString(KEY_USERNAME, username);
            editor.commit();
            Log.d(TAG, "Login session created for user: " + username);
        } catch (Exception e) {
            Log.e(TAG, "Error creating login session", e);
        }
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public void logoutUser() {
        try {
            editor.clear();
            editor.commit();
            Log.d(TAG, "User logged out");
        } catch (Exception e) {
            Log.e(TAG, "Error during logout", e);
        }
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }
}