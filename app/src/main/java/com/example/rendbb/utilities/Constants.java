package com.example.rendbb.utilities;

public class Constants {
    // Session related constants
    public static final String PREF_NAME = "RENDBBPrefs";
    public static final String IS_LOGGED_IN = "IsLoggedIn";
    public static final String KEY_USER_ID = "UserId";
    public static final String KEY_USERNAME = "Username";
    public static final String KEY_EMAIL = "Email";

    // Database related constants
    public static final int DB_VERSION = 1;
    public static final String DB_NAME = "rendbb.db";

    // Activity result codes
    public static final int REQUEST_ADD_PROPERTY = 1;
    public static final int REQUEST_EDIT_PROPERTY = 2;
    public static final int REQUEST_ADD_BOOKING = 3;

    // Default values
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";
    public static final String DEFAULT_ADMIN_EMAIL = "admin@rendbb.com";
}