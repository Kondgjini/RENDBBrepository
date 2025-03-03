package com.example.rendbb.utilities;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.rendbb.models.Property;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "rendbb.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROPERTIES = "properties";
    public static final String TABLE_BOOKINGS = "bookings";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create Users table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, " +
                "password TEXT NOT NULL, " +
                "email TEXT NOT NULL" +
                ");");

        // Create Properties table
        db.execSQL("CREATE TABLE " + TABLE_PROPERTIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "location TEXT NOT NULL, " +
                "description TEXT, " +
                "status TEXT, " +
                "manager_id INTEGER" +
                ");");

        // Create Bookings table
        db.execSQL("CREATE TABLE " + TABLE_BOOKINGS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "property_id INTEGER, " +
                "guest_name TEXT NOT NULL, " +
                "check_in_date TEXT NOT NULL, " +
                "check_out_date TEXT NOT NULL" +
                ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        onCreate(db);
    }

    // Add this method to allow user authentication
    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_USERS + " WHERE username=? AND password=?";
        Cursor cursor = db.rawQuery(query, new String[]{username, password});
        boolean result = (cursor.getCount() > 0);
        cursor.close();
        return result;
    }

    public List<Property> getAllProperties() {
        List<Property> propertyList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PROPERTIES, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex("id");
            int nameIndex = cursor.getColumnIndex("name");
            int locationIndex = cursor.getColumnIndex("location");
            int descriptionIndex = cursor.getColumnIndex("description");
            int statusIndex = cursor.getColumnIndex("status");
            int managerIdIndex = cursor.getColumnIndex("manager_id");

            do {
                Property property = new Property();
                property.setId(cursor.getInt(idIndex));
                property.setName(cursor.getString(nameIndex));
                property.setLocation(cursor.getString(locationIndex));
                property.setDescription(cursor.getString(descriptionIndex));
                property.setStatus(cursor.getString(statusIndex));
                property.setManagerId(cursor.getInt(managerIdIndex));
                propertyList.add(property);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return propertyList;
    }
}