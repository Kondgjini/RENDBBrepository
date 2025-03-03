package com.example.rendbb.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.rendbb.models.Property;
import com.example.rendbb.utilities.DatabaseHelper;

import java.util.ArrayList;
import java.util.List;

public class PropertyManager {

    private SQLiteDatabase db;

    public PropertyManager(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addProperty(Property property) {
        ContentValues values = new ContentValues();
        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put("status", property.getStatus());
        values.put("manager_id", property.getManagerId());
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

    public List<Property> getAllProperties() {
        List<Property> propertyList = new ArrayList<>();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PROPERTIES, null);
        if (cursor.moveToFirst()) {
            do {
                Property property = new Property(
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getInt(cursor.getColumnIndex("manager_id"))
                );
                propertyList.add(property);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return propertyList;
    }
}