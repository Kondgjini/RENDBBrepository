package com.example.rendbb.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.rendbb.utilities.DatabaseHelper;
import com.example.rendbb.models.PropertyItem;
import java.util.ArrayList;
import java.util.List;

public class PropertyManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public PropertyManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    public long addProperty(PropertyItem property) {
        ContentValues values = new ContentValues();
        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put("manager_id", property.getManagerId());
        values.put("price_per_night", property.getPricePerNight());
        values.put("max_occupants", property.getMaxOccupants());
        values.put("status", property.getStatus());
        return db.insert(DatabaseHelper.TABLE_PROPERTIES, null, values);
    }

    public int updateProperty(PropertyItem property) {
        ContentValues values = new ContentValues();
        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put("price_per_night", property.getPricePerNight());
        values.put("max_occupants", property.getMaxOccupants());
        values.put("status", property.getStatus());
        return db.update(DatabaseHelper.TABLE_PROPERTIES, values, "id=?",
                new String[]{String.valueOf(property.getId())});
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

    public List<PropertyItem> getAllProperties() {
        List<PropertyItem> propertyList = new ArrayList<>();
        String query = "SELECT * FROM " + DatabaseHelper.TABLE_PROPERTIES;
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {
                PropertyItem property = new PropertyItem(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getString(cursor.getColumnIndex("status")),
                        cursor.getInt(cursor.getColumnIndex("manager_id")),
                        cursor.getDouble(cursor.getColumnIndex("price_per_night")),
                        cursor.getInt(cursor.getColumnIndex("max_occupants"))
                );
                propertyList.add(property);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return propertyList;
    }

    public PropertyItem getPropertyById(int propertyId) {
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROPERTIES,
                null,
                "id=?",
                new String[]{String.valueOf(propertyId)},
                null,
                null,
                null);

        PropertyItem property = null;
        if (cursor.moveToFirst()) {
            property = new PropertyItem(
                    cursor.getInt(cursor.getColumnIndex("id")),
                    cursor.getString(cursor.getColumnIndex("name")),
                    cursor.getString(cursor.getColumnIndex("location")),
                    cursor.getString(cursor.getColumnIndex("description")),
                    cursor.getString(cursor.getColumnIndex("status")),
                    cursor.getInt(cursor.getColumnIndex("manager_id")),
                    cursor.getDouble(cursor.getColumnIndex("price_per_night")),
                    cursor.getInt(cursor.getColumnIndex("max_occupants"))
            );
        }
        cursor.close();
        return property;
    }

    public List<PropertyItem> getPropertiesByManagerId(int managerId) {
        List<PropertyItem> propertyList = new ArrayList<>();
        Cursor cursor = db.query(DatabaseHelper.TABLE_PROPERTIES,
                null,
                "manager_id=?",
                new String[]{String.valueOf(managerId)},
                null,
                null,
                null);

        if (cursor.moveToFirst()) {
            do {
                PropertyItem property = new PropertyItem(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        cursor.getString(cursor.getColumnIndex("status")),
                        cursor.getInt(cursor.getColumnIndex("manager_id")),
                        cursor.getDouble(cursor.getColumnIndex("price_per_night")),
                        cursor.getInt(cursor.getColumnIndex("max_occupants"))
                );
                propertyList.add(property);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return propertyList;
    }

    public void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
    }
}