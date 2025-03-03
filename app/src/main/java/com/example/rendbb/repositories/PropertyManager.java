package com.example.rendbb.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.rendbb.utilities.DatabaseHelper;

public class PropertyManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public PropertyManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addProperty(String name, String location, String description, int managerId,
                            double pricePerNight, int maxOccupants) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("location", location);
        values.put("description", description);
        values.put("manager_id", managerId);
        values.put("price_per_night", pricePerNight);
        values.put("max_occupants", maxOccupants);
        values.put("status", "available"); // Default status
        return db.insert(DatabaseHelper.TABLE_PROPERTIES, null, values);
    }

    public int updateProperty(int id, String name, String location, String description,
                              double pricePerNight, int maxOccupants) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("location", location);
        values.put("description", description);
        values.put("price_per_night", pricePerNight);
        values.put("max_occupants", maxOccupants);
        return db.update(DatabaseHelper.TABLE_PROPERTIES, values, "id=?",
                new String[]{String.valueOf(id)});
    }

    public String getPropertyStatus(int id) {
        return dbHelper.getPropertyStatus(id);
    }

    public boolean updatePropertyStatus(int id, String status) {
        return dbHelper.updatePropertyStatus(id, status) > 0;
    }

    public int deleteProperty(int propertyId) {
        return db.delete(DatabaseHelper.TABLE_PROPERTIES, "id=?",
                new String[]{String.valueOf(propertyId)});
    }

    public Cursor getAllProperties() {
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PROPERTIES;
        return db.rawQuery(query, null);
    }

    public Cursor getPropertyById(int propertyId) {
        return db.query(DatabaseHelper.TABLE_PROPERTIES,
                null,
                "id=?",
                new String[]{String.valueOf(propertyId)},
                null,
                null,
                null);
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}