package com.example.rendbb.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "rendbb.db";
    public static final int DATABASE_VERSION = 2;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROPERTIES = "properties";
    public static final String TABLE_BOOKINGS = "bookings";
    public static final String TABLE_GUEST_DETAILS = "guest_details";
    public static final String TABLE_SETTINGS = "settings";

    // Common column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_CREATED_AT = "created_at";
    private static final String COLUMN_UPDATED_AT = "updated_at";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL UNIQUE, " +
                "password TEXT NOT NULL, " +
                "email TEXT NOT NULL, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "last_login DATETIME" +
                ");");

        // Create Properties table
        db.execSQL("CREATE TABLE " + TABLE_PROPERTIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "description TEXT, " +
                "manager_id INTEGER, " +
                "status TEXT DEFAULT 'available', " +
                "price_per_night REAL, " +
                "max_occupants INTEGER, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(manager_id) REFERENCES " + TABLE_USERS + "(id)" +
                ");");

        // Create Guest Details table
        db.execSQL("CREATE TABLE " + TABLE_GUEST_DETAILS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "email TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");");

        // Create Bookings table
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "property_id INTEGER, " +
                "guest_id INTEGER, " +
                "check_in_date TEXT NOT NULL, " +
                "check_out_date TEXT NOT NULL, " +
                "total_price REAL, " +
                "status TEXT DEFAULT 'confirmed', " +
                "notes TEXT, " +
                "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY(property_id) REFERENCES " + TABLE_PROPERTIES + "(id), " +
                "FOREIGN KEY(guest_id) REFERENCES " + TABLE_GUEST_DETAILS + "(id)" +
                ");");

        // Create Settings table
        db.execSQL("CREATE TABLE " + TABLE_SETTINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "setting_key TEXT NOT NULL UNIQUE, " +
                "setting_value TEXT, " +
                "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                ");");

        // Insert default settings
        insertDefaultSettings(db);
    }

    private void insertDefaultSettings(SQLiteDatabase db) {
        String[] defaultSettings = {
                "INSERT INTO " + TABLE_SETTINGS + " (setting_key, setting_value) VALUES ('notification_time', '09:00')",
                "INSERT INTO " + TABLE_SETTINGS + " (setting_key, setting_value) VALUES ('default_currency', 'USD')",
                "INSERT INTO " + TABLE_SETTINGS + " (setting_key, setting_value) VALUES ('show_calendar_widget', 'true')",
                "INSERT INTO " + TABLE_SETTINGS + " (setting_key, setting_value) VALUES ('maintenance_reminder_days', '7')"
        };

        for (String setting : defaultSettings) {
            db.execSQL(setting);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Backup existing data
            db.execSQL("CREATE TABLE properties_backup AS SELECT * FROM " + TABLE_PROPERTIES);
            db.execSQL("CREATE TABLE bookings_backup AS SELECT * FROM " + TABLE_BOOKINGS);

            // Drop existing tables
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);

            // Create new tables
            onCreate(db);

            // Restore data
            db.execSQL("INSERT INTO " + TABLE_PROPERTIES + " (id, name, location, description, manager_id) " +
                    "SELECT id, name, location, description, manager_id FROM properties_backup");
            db.execSQL("INSERT INTO " + TABLE_BOOKINGS + " (id, property_id, guest_name, check_in_date, check_out_date) " +
                    "SELECT id, property_id, guest_name, check_in_date, check_out_date FROM bookings_backup");

            // Drop backup tables
            db.execSQL("DROP TABLE IF EXISTS properties_backup");
            db.execSQL("DROP TABLE IF EXISTS bookings_backup");
        }
    }

    public String getPropertyStatus(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        String status = "available";

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());

        String query = "SELECT status FROM " + TABLE_BOOKINGS +
                " WHERE property_id = ? AND ? BETWEEN date(check_in_date) AND date(check_out_date)" +
                " AND status = 'confirmed'";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(propertyId), currentDate});

        if (cursor.moveToFirst()) {
            status = "occupied";
        }
        cursor.close();

        return status;
    }

    public String getSetting(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT setting_value FROM " + TABLE_SETTINGS + " WHERE setting_key = ?";
        Cursor cursor = db.rawQuery(query, new String[]{key});
        String value = null;
        if (cursor.moveToFirst()) {
            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    public void updateSetting(String key, String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT OR REPLACE INTO " + TABLE_SETTINGS + " (setting_key, setting_value, updated_at) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP)", new String[]{key, value});
    }

    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT id FROM " + TABLE_USERS + " WHERE username=? AND password=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean result = cursor.getCount() > 0;

        if (result) {
            db.execSQL("UPDATE " + TABLE_USERS + " SET last_login = CURRENT_TIMESTAMP " +
                    "WHERE username=?", new String[]{username});
        }

        cursor.close();
        return result;
    }
}