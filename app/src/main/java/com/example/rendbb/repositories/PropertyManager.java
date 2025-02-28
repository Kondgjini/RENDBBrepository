package com.example.rendbb.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.rendbb.utilities.DatabaseHelper;

public class PropertyManager {

    private SQLiteDatabase db;

    public PropertyManager(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addProperty(String name, String location, String description, int managerId) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("location", location);
        values.put("description", description);
        values.put("manager_id", managerId);
        return db.insert(DatabaseHelper.TABLE_PROPERTIES, null, values);
    }

    public int updateProperty(int propertyId, String name, String location, String description) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("location", location);
        values.put("description", description);
        return db.update(DatabaseHelper.TABLE_PROPERTIES, values, "id=?", new String[]{String.valueOf(propertyId)});
    }

    public int deleteProperty(int propertyId) {
        return db.delete(DatabaseHelper.TABLE_PROPERTIES, "id=?", new String[]{String.valueOf(propertyId)});
    }

    public Cursor getAllProperties() {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PROPERTIES;
        return db.rawQuery(query, null);
    }
}
