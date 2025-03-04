package com.example.rendbb.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "rendbb.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROPERTIES = "properties";
    public static final String TABLE_BOOKINGS = "bookings";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_STATUS = "status";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            Log.d(TAG, "Creating database tables");

            // Create Users table
            String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_USERNAME + " TEXT UNIQUE NOT NULL,"
                    + KEY_PASSWORD + " TEXT NOT NULL,"
                    + KEY_EMAIL + " TEXT NOT NULL"
                    + ")";
            db.execSQL(CREATE_USERS_TABLE);

            // Create Properties table
            String CREATE_PROPERTIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROPERTIES + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL,"
                    + "location TEXT NOT NULL,"
                    + "description TEXT,"
                    + KEY_STATUS + " TEXT NOT NULL DEFAULT 'available',"
                    + "manager_id INTEGER NOT NULL,"
                    + "price_per_night REAL NOT NULL,"
                    + "max_occupants INTEGER NOT NULL"
                    + ")";
            db.execSQL(CREATE_PROPERTIES_TABLE);

            // Create Bookings table
            String CREATE_BOOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "property_id INTEGER NOT NULL,"
                    + "guest_name TEXT NOT NULL,"
                    + "check_in_date DATE NOT NULL,"
                    + "check_out_date DATE NOT NULL,"
                    + "total_price REAL NOT NULL,"
                    + KEY_STATUS + " TEXT NOT NULL DEFAULT 'pending',"
                    + "notes TEXT"
                    + ")";
            db.execSQL(CREATE_BOOKINGS_TABLE);

            Log.d(TAG, "Database tables created successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public String getPropertyStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String status = "unknown";

        try {
            String[] columns = { KEY_STATUS };
            String selection = KEY_ID + "=?";
            String[] selectionArgs = { String.valueOf(id) };

            Cursor cursor = db.query(TABLE_PROPERTIES, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                if (statusIndex != -1) {
                    status = cursor.getString(statusIndex);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting property status", e);
        }

        return status;
    }

    public int updatePropertyStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_STATUS, status);

            result = db.update(TABLE_PROPERTIES,
                    values,
                    KEY_ID + "=?",
                    new String[]{String.valueOf(id)});

            Log.d(TAG, "Property status updated. Result: " + result);
        } catch (Exception e) {
            Log.e(TAG, "Error updating property status", e);
        }

        return result;
    }

    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        Cursor cursor = null;

        try {
            String[] columns = { KEY_ID };
            String selection = KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?";
            String[] selectionArgs = { username, password };

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            result = (cursor != null && cursor.getCount() > 0);
        } catch (Exception e) {
            Log.e(TAG, "Error during authentication", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { KEY_ID, KEY_USERNAME, KEY_EMAIL };
        String selection = KEY_USERNAME + "=?";
        String[] selectionArgs = { username };

        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }

    public long addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, password);
        values.put(KEY_EMAIL, email);

        try {
            return db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding user", e);
            return -1;
        }
    }
}