package com.example.rendbb.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.rendbb.models.Property;
import com.example.rendbb.utilities.DatabaseHelper;

import java.util.List;

public class PropertyManager {

    private DatabaseHelper dbHelper;

    public PropertyManager(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    public long addProperty(Property property) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put("status", property.getStatus());
        return db.insert(DatabaseHelper.TABLE_PROPERTIES, null, values);
    }

    public int updateProperty(int propertyId, Property property) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put("status", property.getStatus());
        return db.update(DatabaseHelper.TABLE_PROPERTIES, values, "id=?", new String[]{String.valueOf(propertyId)});
    }

    public int deleteProperty(int propertyId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(DatabaseHelper.TABLE_PROPERTIES, "id=?", new String[]{String.valueOf(propertyId)});
    }

    public List<Property> getAllProperties() {
        return dbHelper.getAllProperties();
    }
}