package com.example.rendbb.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.rendbb.models.BookingInfo;
import com.example.rendbb.models.PropertyItem;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = "DatabaseHelper";
    public static final String DATABASE_NAME = "rendbb.db";
    public static final int DATABASE_VERSION = 1;

    // Table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_PROPERTIES = "properties";
    public static final String TABLE_BOOKINGS = "bookings";

    // Column names for external access (RegisterActivity, etc.)
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_EMAIL = "email";
    public static final String COLUMN_PASSWORD = "password";

    // Common column names for internal use
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
        try {
            Log.d(TAG, "Creating database tables");

            // Create Users table
            String CREATE_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + KEY_USERNAME + " TEXT UNIQUE NOT NULL,"
                    + KEY_PASSWORD + " TEXT NOT NULL,"
                    + KEY_EMAIL + " TEXT NOT NULL"
                    + ")";
            db.execSQL(CREATE_USERS_TABLE);

            // Create Properties table
            String CREATE_PROPERTIES_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_PROPERTIES + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "name TEXT NOT NULL,"
                    + "location TEXT NOT NULL,"
                    + "description TEXT,"
                    + KEY_STATUS + " TEXT NOT NULL DEFAULT 'available',"
                    + "manager_id INTEGER NOT NULL,"
                    + "price_per_night REAL NOT NULL,"
                    + "max_occupants INTEGER NOT NULL"
                    + ")";
            db.execSQL(CREATE_PROPERTIES_TABLE);

            // Create Bookings table
            String CREATE_BOOKINGS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_BOOKINGS + "("
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "property_id INTEGER NOT NULL,"
                    + "guest_name TEXT NOT NULL,"
                    + "guest_email TEXT,"
                    + "guest_phone TEXT,"
                    + "check_in_date DATE NOT NULL,"
                    + "check_out_date DATE NOT NULL,"
                    + "total_price REAL NOT NULL,"
                    + KEY_STATUS + " TEXT NOT NULL DEFAULT 'pending',"
                    + "notes TEXT"
                    + ")";
            db.execSQL(CREATE_BOOKINGS_TABLE);

            Log.d(TAG, "Database tables created successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error creating database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOKINGS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROPERTIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    public void clearAllTables() {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();
            db.execSQL("DELETE FROM " + TABLE_USERS);
            db.execSQL("DELETE FROM " + TABLE_PROPERTIES);
            db.execSQL("DELETE FROM " + TABLE_BOOKINGS);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e(TAG, "Error clearing tables", e);
        } finally {
            db.endTransaction();
        }
    }

    public String getPropertyStatus(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String status = "unknown";

        try {
            String[] columns = { KEY_STATUS };
            String selection = KEY_ID + "=?";
            String[] selectionArgs = { String.valueOf(id) };

            Cursor cursor = db.query(TABLE_PROPERTIES, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                if (statusIndex != -1) {
                    status = cursor.getString(statusIndex);
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting property status", e);
        }

        return status;
    }

    public int updatePropertyStatus(int id, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = 0;

        try {
            ContentValues values = new ContentValues();
            values.put(KEY_STATUS, status);

            result = db.update(TABLE_PROPERTIES,
                    values,
                    KEY_ID + "=?",
                    new String[]{String.valueOf(id)});

            Log.d(TAG, "Property status updated. Result: " + result);
        } catch (Exception e) {
            Log.e(TAG, "Error updating property status", e);
        }

        return result;
    }

    public boolean authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean result = false;
        Cursor cursor = null;

        try {
            // Hash the password first for comparison
            String hashedPassword = hashPassword(password);

            String[] columns = { KEY_ID };
            String selection = KEY_USERNAME + "=? AND " + KEY_PASSWORD + "=?";
            String[] selectionArgs = { username, hashedPassword };

            cursor = db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
            result = (cursor != null && cursor.getCount() > 0);

            if (!result) {
                // For debugging
                Log.d(TAG, "Authentication failed for user: " + username);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error during authentication", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return result;
    }

    public Cursor getUserDetails(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { KEY_ID, KEY_USERNAME, KEY_EMAIL };
        String selection = KEY_USERNAME + "=?";
        String[] selectionArgs = { username };

        return db.query(TABLE_USERS, columns, selection, selectionArgs, null, null, null);
    }

    public long addUser(String username, String password, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USERNAME, username);
        values.put(KEY_PASSWORD, hashPassword(password));
        values.put(KEY_EMAIL, email);

        try {
            return db.insertOrThrow(TABLE_USERS, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding user", e);
            return -1;
        }
    }

    // Password hashing method using SHA-256
    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            return number.toString(16); // Convert to hexadecimal
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Error hashing password", e);
            return password; // Fallback to unhashed password in case of error
        }
    }

    // Property methods
    public long addProperty(PropertyItem property) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put(KEY_STATUS, property.getStatus());
        values.put("manager_id", property.getManagerId());
        values.put("price_per_night", property.getPricePerNight());
        values.put("max_occupants", property.getMaxOccupants());

        try {
            return db.insertOrThrow(TABLE_PROPERTIES, null, values);
        } catch (Exception e) {
            Log.e(TAG, "Error adding property", e);
            return -1;
        }
    }

    public int updateProperty(PropertyItem property) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", property.getName());
        values.put("location", property.getLocation());
        values.put("description", property.getDescription());
        values.put(KEY_STATUS, property.getStatus());
        values.put("manager_id", property.getManagerId());
        values.put("price_per_night", property.getPricePerNight());
        values.put("max_occupants", property.getMaxOccupants());

        try {
            int result = db.update(TABLE_PROPERTIES,
                    values,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(property.getId())});
            Log.d(TAG, "Updated property with ID " + property.getId() + ", result: " + result);
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error updating property", e);
            return 0;
        }
    }

    public int deleteProperty(int propertyId) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // First delete all bookings for this property
            db.delete(TABLE_BOOKINGS, "property_id = ?", new String[]{String.valueOf(propertyId)});

            // Then delete the property
            return db.delete(TABLE_PROPERTIES, KEY_ID + " = ?", new String[]{String.valueOf(propertyId)});
        } catch (Exception e) {
            Log.e(TAG, "Error deleting property", e);
            return 0;
        }
    }

    public PropertyItem getProperty(int propertyId) {
        SQLiteDatabase db = this.getReadableDatabase();
        PropertyItem property = null;

        try {
            Cursor cursor = db.query(TABLE_PROPERTIES,
                    null,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(propertyId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int nameIndex = cursor.getColumnIndex("name");
                int locationIndex = cursor.getColumnIndex("location");
                int descriptionIndex = cursor.getColumnIndex("description");
                int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                int managerIdIndex = cursor.getColumnIndex("manager_id");
                int priceIndex = cursor.getColumnIndex("price_per_night");
                int occupantsIndex = cursor.getColumnIndex("max_occupants");

                if (idIndex != -1 && nameIndex != -1 && locationIndex != -1 &&
                        descriptionIndex != -1 && statusIndex != -1 && managerIdIndex != -1 &&
                        priceIndex != -1 && occupantsIndex != -1) {

                    property = new PropertyItem(
                            cursor.getInt(idIndex),
                            cursor.getString(nameIndex),
                            cursor.getString(locationIndex),
                            cursor.getString(descriptionIndex),
                            cursor.getString(statusIndex),
                            cursor.getInt(managerIdIndex),
                            cursor.getDouble(priceIndex),
                            cursor.getInt(occupantsIndex)
                    );
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting property", e);
        }

        return property;
    }

    public List<PropertyItem> getAllProperties() {
        List<PropertyItem> properties = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_PROPERTIES;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int nameIndex = cursor.getColumnIndex("name");
                    int locationIndex = cursor.getColumnIndex("location");
                    int descriptionIndex = cursor.getColumnIndex("description");
                    int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                    int managerIdIndex = cursor.getColumnIndex("manager_id");
                    int priceIndex = cursor.getColumnIndex("price_per_night");
                    int occupantsIndex = cursor.getColumnIndex("max_occupants");

                    if (idIndex != -1 && nameIndex != -1 && locationIndex != -1 &&
                            descriptionIndex != -1 && statusIndex != -1 && managerIdIndex != -1 &&
                            priceIndex != -1 && occupantsIndex != -1) {

                        PropertyItem property = new PropertyItem(
                                cursor.getInt(idIndex),
                                cursor.getString(nameIndex),
                                cursor.getString(locationIndex),
                                cursor.getString(descriptionIndex),
                                cursor.getString(statusIndex),
                                cursor.getInt(managerIdIndex),
                                cursor.getDouble(priceIndex),
                                cursor.getInt(occupantsIndex)
                        );
                        properties.add(property);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all properties", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return properties;
    }

    // Booking methods
    public long addBooking(BookingInfo booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("property_id", booking.getPropertyId());
        values.put("guest_name", booking.getGuestName());
        values.put("check_in_date", formatDateForDB(booking.getCheckInDate()));
        values.put("check_out_date", formatDateForDB(booking.getCheckOutDate()));
        values.put("total_price", booking.getTotalPrice());
        values.put(KEY_STATUS, booking.getStatus());
        values.put("notes", booking.getNotes());

        try {
            long result = db.insertOrThrow(TABLE_BOOKINGS, null, values);
            if (result != -1) {
                // Update property status to "occupied"
                updatePropertyStatus(booking.getPropertyId(), "occupied");
            }
            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error adding booking", e);
            return -1;
        }
    }

    public int updateBooking(BookingInfo booking) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("property_id", booking.getPropertyId());
        values.put("guest_name", booking.getGuestName());
        values.put("check_in_date", formatDateForDB(booking.getCheckInDate()));
        values.put("check_out_date", formatDateForDB(booking.getCheckOutDate()));
        values.put("total_price", booking.getTotalPrice());
        values.put(KEY_STATUS, booking.getStatus());
        values.put("notes", booking.getNotes());

        try {
            return db.update(TABLE_BOOKINGS,
                    values,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(booking.getId())});
        } catch (Exception e) {
            Log.e(TAG, "Error updating booking", e);
            return 0;
        }
    }

    public int deleteBooking(int bookingId) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            // Get the property ID for this booking
            int propertyId = -1;
            Cursor cursor = db.query(TABLE_BOOKINGS,
                    new String[]{"property_id"},
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(bookingId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int propertyIdIndex = cursor.getColumnIndex("property_id");
                if (propertyIdIndex != -1) {
                    propertyId = cursor.getInt(propertyIdIndex);
                }
                cursor.close();
            }

            // Delete the booking
            int result = db.delete(TABLE_BOOKINGS, KEY_ID + " = ?", new String[]{String.valueOf(bookingId)});

            // Check if there are other bookings for this property
            if (propertyId != -1) {
                cursor = db.query(TABLE_BOOKINGS,
                        new String[]{KEY_ID},
                        "property_id = ?",
                        new String[]{String.valueOf(propertyId)},
                        null, null, null);

                if (cursor != null) {
                    if (cursor.getCount() == 0) {
                        // No more bookings, update property status to "available"
                        updatePropertyStatus(propertyId, "available");
                    }
                    cursor.close();
                }
            }

            return result;
        } catch (Exception e) {
            Log.e(TAG, "Error deleting booking", e);
            return 0;
        }
    }

    public BookingInfo getBooking(int bookingId) {
        SQLiteDatabase db = this.getReadableDatabase();
        BookingInfo booking = null;

        try {
            Cursor cursor = db.query(TABLE_BOOKINGS,
                    null,
                    KEY_ID + " = ?",
                    new String[]{String.valueOf(bookingId)},
                    null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndex(KEY_ID);
                int propertyIdIndex = cursor.getColumnIndex("property_id");
                int guestNameIndex = cursor.getColumnIndex("guest_name");
                int checkInDateIndex = cursor.getColumnIndex("check_in_date");
                int checkOutDateIndex = cursor.getColumnIndex("check_out_date");
                int totalPriceIndex = cursor.getColumnIndex("total_price");
                int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                int notesIndex = cursor.getColumnIndex("notes");

                if (idIndex != -1 && propertyIdIndex != -1 && guestNameIndex != -1 &&
                        checkInDateIndex != -1 && checkOutDateIndex != -1 && totalPriceIndex != -1 &&
                        statusIndex != -1 && notesIndex != -1) {

                    booking = new BookingInfo(
                            cursor.getInt(idIndex),
                            cursor.getInt(propertyIdIndex),
                            1, // Default user ID
                            cursor.getString(guestNameIndex),
                            parseDBDate(cursor.getString(checkInDateIndex)),
                            parseDBDate(cursor.getString(checkOutDateIndex)),
                            cursor.getDouble(totalPriceIndex),
                            cursor.getString(statusIndex),
                            cursor.getString(notesIndex)
                    );
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting booking", e);
        }

        return booking;
    }

    public List<BookingInfo> getAllBookings() {
        List<BookingInfo> bookings = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BOOKINGS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int propertyIdIndex = cursor.getColumnIndex("property_id");
                    int guestNameIndex = cursor.getColumnIndex("guest_name");
                    int checkInDateIndex = cursor.getColumnIndex("check_in_date");
                    int checkOutDateIndex = cursor.getColumnIndex("check_out_date");
                    int totalPriceIndex = cursor.getColumnIndex("total_price");
                    int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                    int notesIndex = cursor.getColumnIndex("notes");

                    if (idIndex != -1 && propertyIdIndex != -1 && guestNameIndex != -1 &&
                            checkInDateIndex != -1 && checkOutDateIndex != -1 && totalPriceIndex != -1 &&
                            statusIndex != -1 && notesIndex != -1) {

                        BookingInfo booking = new BookingInfo(
                                cursor.getInt(idIndex),
                                cursor.getInt(propertyIdIndex),
                                1, // Default user ID
                                cursor.getString(guestNameIndex),
                                parseDBDate(cursor.getString(checkInDateIndex)),
                                parseDBDate(cursor.getString(checkOutDateIndex)),
                                cursor.getDouble(totalPriceIndex),
                                cursor.getString(statusIndex),
                                cursor.getString(notesIndex)
                        );
                        bookings.add(booking);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting all bookings", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return bookings;
    }

    public List<BookingInfo> getBookingsForProperty(int propertyId) {
        List<BookingInfo> bookings = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_BOOKINGS + " WHERE property_id = ?";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(query, new String[]{String.valueOf(propertyId)});

            if (cursor.moveToFirst()) {
                do {
                    int idIndex = cursor.getColumnIndex(KEY_ID);
                    int guestNameIndex = cursor.getColumnIndex("guest_name");
                    int checkInDateIndex = cursor.getColumnIndex("check_in_date");
                    int checkOutDateIndex = cursor.getColumnIndex("check_out_date");
                    int totalPriceIndex = cursor.getColumnIndex("total_price");
                    int statusIndex = cursor.getColumnIndex(KEY_STATUS);
                    int notesIndex = cursor.getColumnIndex("notes");

                    if (idIndex != -1 && guestNameIndex != -1 && checkInDateIndex != -1 &&
                            checkOutDateIndex != -1 && totalPriceIndex != -1 && statusIndex != -1 &&
                            notesIndex != -1) {

                        BookingInfo booking = new BookingInfo(
                                cursor.getInt(idIndex),
                                propertyId,
                                1, // Default user ID
                                cursor.getString(guestNameIndex),
                                parseDBDate(cursor.getString(checkInDateIndex)),
                                parseDBDate(cursor.getString(checkOutDateIndex)),
                                cursor.getDouble(totalPriceIndex),
                                cursor.getString(statusIndex),
                                cursor.getString(notesIndex)
                        );
                        bookings.add(booking);
                    }
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting bookings for property", e);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        return bookings;
    }

    private String formatDateForDB(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private Calendar parseDBDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dateFormat.parse(dateStr));
            return calendar;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing date: " + dateStr, e);
            return Calendar.getInstance();
        }
    }
}