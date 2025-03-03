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
    private DatabaseHelper dbHelper;

    public PropertyManager(Context context) {
        dbHelper = new DatabaseHelper(context);
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
            int managerId = cursor.getInt(cursor.getColumnIndex("manager_id"));
            double price = cursor.getDouble(cursor.getColumnIndex("price_per_night"));
            int maxOccupants = cursor.getInt(cursor.getColumnIndex("max_occupants"));
            String status = dbHelper.getPropertyStatus(id);

            properties.add(new PropertyItem(id, name, location, description,
                    status, managerId, price, maxOccupants));
        }
        cursor.close();
        return properties;
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

        return db.update(DatabaseHelper.TABLE_PROPERTIES, values,
                "id=?", new String[]{String.valueOf(property.getId())});
    }
}