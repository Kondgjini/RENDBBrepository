package com.example.rendbb.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

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
        // Create Users table with last_login column
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + KEY_USERNAME + " TEXT UNIQUE NOT NULL,"
                + KEY_PASSWORD + " TEXT NOT NULL,"
                + KEY_EMAIL + " TEXT NOT NULL,"
                + "last_login DATETIME"
                + ")";
        db.execSQL(CREATE_USERS_TABLE);

        // Create Properties table
        String CREATE_PROPERTIES_TABLE = "CREATE TABLE " + TABLE_PROPERTIES + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "name TEXT NOT NULL,"
                + "location TEXT NOT NULL,"
                + "description TEXT,"
                + "status TEXT NOT NULL DEFAULT 'available',"
                + "manager_id INTEGER NOT NULL,"
                + "price_per_night REAL NOT NULL,"
                + "max_occupants INTEGER NOT NULL,"
                + "FOREIGN KEY(manager_id) REFERENCES " + TABLE_USERS + "(id)"
                + ")";
        db.execSQL(CREATE_PROPERTIES_TABLE);

        // Create Bookings table
        String CREATE_BOOKINGS_TABLE = "CREATE TABLE " + TABLE_BOOKINGS + "("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "property_id INTEGER NOT NULL,"
                + "guest_name TEXT NOT NULL,"
                + "check_in_date DATE NOT NULL,"
                + "check_out_date DATE NOT NULL,"
                + "total_price REAL NOT NULL,"
                + "status TEXT NOT NULL DEFAULT 'pending',"
                + "notes TEXT,"
                + "FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTIES + "(id)"
                + ")";
        db.execSQL(CREATE_BOOKINGS_TABLE);

        // Insert a default admin user
        insertDefaultAdmin(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void insertDefaultAdmin(SQLiteDatabase db) {
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, "admin");
        values.put(KEY_PASSWORD, "admin123"); // In production, use proper password hashing
        values.put(KEY_EMAIL, "admin@rendbb.com");
        db.insert(TABLE_USERS, null, values);
    }

    // Add the missing getPropertyStatus method
    public String getPropertyStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String status = "unknown";

        String[] columns = { KEY_STATUS };
        String selection = KEY_ID + "=?";
        String[] selectionArgs = { String.valueOf(id) };

        try {
            Cursor cursor = db.query(TABLE_PROPERTIES, columns, selection, selectionArgs, null, null, null);
            if (cursor.moveToFirst()) {
                status = cursor.getString(cursor.getColumnIndex(KEY_STATUS));
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return status;
    }

    // Method to update property status
    public int updatePropertyStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_STATUS, status);
        return db.update(TABLE_PROPERTIES, values, KEY_ID + "=?", new String[]{String.valueOf(id)});
    }

    // Add the existing authentication methods
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { KEY_ID };
        String selection = KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?";
        String[] selectionArgs = { username, password };

        Cursor cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
        int count = cursor.getCount();
        cursor.close();

        return count > 0;
    }

    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { KEY_ID, KEY_USERNAME, KEY_EMAIL };
        String selection = KEY_USERNAME + "=?";
        String[] selectionArgs = { username };

        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }

    public void updateLastLogin(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("last_login", getCurrentDateTime());

        String whereClause = KEY_USERNAME + "=?";
        String[] whereArgs = { username };

        db.update(TABLE_USERS, values, whereClause, whereArgs);
    }

    private String getCurrentDateTime() {
        return java.text.DateFormat.getDateTimeInstance().format(new java.util.Date());
    }
}