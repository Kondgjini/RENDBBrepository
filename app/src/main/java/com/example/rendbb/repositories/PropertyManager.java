package com.example.rendbb.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.rendbb.models.PropertyItem;
import com.example.rendbb.utilities.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class PropertyManager {
    private SQLiteDatabase db;

    public PropertyManager(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public List<PropertyItem> getAllProperties() {
        List<PropertyItem> properties = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PROPERTIES;
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String location = cursor.getString(cursor.getColumnIndex("location"));
            String description = cursor.getString(cursor.getColumnIndex("description"));
            String status = "available"; // Default status, can be updated based on bookings

            properties.add(new PropertyItem(id, name, location, description, status));
        }
        cursor.close();
        return properties;
    }

    // ... other existing methods ...
}