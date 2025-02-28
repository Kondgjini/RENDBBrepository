// SessionManager.java
package com.example.rendbb.views;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.HashMap;

/**
 * Session Manager class for managing user sessions in Android
 * Uses SharedPreferences which is included in Android SDK
 */
public class SessionManager {
    // SharedPreferences reference
    SharedPreferences pref;

    // Editor for SharedPreferences
    Editor editor;

    // Context
    Context _context;

    // SharedPreferences mode
    int PRIVATE_MODE = 0;

    // SharedPreferences file name
    private static final String PREF_NAME = "RENDBBSession";

    // SharedPreferences keys
    public static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_EMAIL = "email";

    // Constructor
    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     * */
    public void createLoginSession(String userId, String username, String email) {
        // Storing login value as TRUE
        editor.putBoolean(IS_LOGIN, true);

        // Store user data in pref
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);

        // Commit changes
        editor.commit();
    }

    /**
     * Store user preferences
     * */
    public void storeUserPreference(String key, String value) {
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * Get stored preference
     * */
    public String getUserPreference(String key) {
        return pref.getString(key, null);
    }

    /**
     * Get stored session data
     * */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();

        // Get user data from pref
        user.put(KEY_USER_ID, pref.getString(KEY_USER_ID, null));
        user.put(KEY_USERNAME, pref.getString(KEY_USERNAME, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));

        // Return user
        return user;
    }

    /**
     * Check login status
     * */
    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    /**
     * Clear session details
     * */
    public void logoutUser() {
        // Clear all data from SharedPreferences except user preferences
        String theme = pref.getString("user_theme", null);
        String language = pref.getString("user_language", null);

        // Clear all data
        editor.clear();
        editor.commit();

        // Restore preferences if needed
        if (theme != null) {
            editor.putString("user_theme", theme);
        }
        if (language != null) {
            editor.putString("user_language", language);
        }
        editor.commit();
    }

    /**
     * Clear all session data including preferences
     * */
    public void clearAllData() {
        // Clear all data from SharedPreferences
        editor.clear();
        editor.commit();
    }
}