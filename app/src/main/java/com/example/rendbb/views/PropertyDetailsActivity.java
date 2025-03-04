package com.example.rendbb.views;

import android.widget.Toast;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.adapters.CalendarAdapter;
import com.example.rendbb.models.BookingInfo;
import com.example.rendbb.repositories.PropertyManager;
import com.example.rendbb.utilities.DatabaseHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PropertyDetailsActivity extends AppCompatActivity {
    private static final String TAG = "PropertyDetailsActivity";
    private TextView nameTextView;
    private TextView locationTextView;
    private TextView descriptionTextView;
    private TextView statusTextView;
    private TextView priceTextView;
    private TextView occupantsTextView;
    private GridView calendarGridView;
    private Button editButton;
    private Button bookButton;

    private int propertyId;
    private PropertyManager propertyManager;
    private DatabaseHelper dbHelper;
    private CalendarAdapter calendarAdapter;
    private List<Integer> bookedDays;
    private double price;
    private int maxOccupants;
    private int managerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        try {
            // Enable back button in action bar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Property Details");
            }

            // Initialize managers
            propertyManager = new PropertyManager(this);
            dbHelper = new DatabaseHelper(this);

            // Initialize views
            initializeViews();

            // Get property details from intent
            getPropertyDetails();

            // Setup calendar
            setupCalendar();

            // Setup button listeners
            setupClickListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        nameTextView = findViewById(R.id.propertyNameTextView);
        locationTextView = findViewById(R.id.propertyLocationTextView);
        descriptionTextView = findViewById(R.id.propertyDescriptionTextView);
        statusTextView = findViewById(R.id.propertyStatusTextView);
        priceTextView = findViewById(R.id.propertyPriceTextView);
        occupantsTextView = findViewById(R.id.propertyOccupantsTextView);
        calendarGridView = findViewById(R.id.calendarGridView);
        editButton = findViewById(R.id.editButton);
        bookButton = findViewById(R.id.bookButton);
    }

    private void getPropertyDetails() {
        Intent intent = getIntent();
        propertyId = intent.getIntExtra("propertyId", -1);

        if (propertyId == -1) {
            Toast.makeText(this, "Error loading property", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String name = intent.getStringExtra("name");
        String location = intent.getStringExtra("location");
        String description = intent.getStringExtra("description");
        String status = intent.getStringExtra("status");
        price = intent.getDoubleExtra("price", 0.0);
        maxOccupants = intent.getIntExtra("maxOccupants", 0);
        managerId = intent.getIntExtra("managerId", 1);

        nameTextView.setText(name);
        locationTextView.setText(location);
        descriptionTextView.setText(description);
        statusTextView.setText(status);
        priceTextView.setText(String.format("$%.2f/night", price));
        occupantsTextView.setText(String.format("Max Occupants: %d", maxOccupants));
    }

    private void setupCalendar() {
        Calendar currentCalendar = Calendar.getInstance();
        calendarAdapter = new CalendarAdapter(this, currentCalendar);
        calendarGridView.setAdapter(calendarAdapter);

        // Load booked days
        loadBookedDays();

        // Add booked days to calendar
        for (Integer day : bookedDays) {
            Calendar bookingDate = Calendar.getInstance();
            bookingDate.set(Calendar.DAY_OF_MONTH, day);
            BookingInfo bookingInfo = new BookingInfo(0, propertyId, 0, "Guest",
                    bookingDate, bookingDate, 0.0, "confirmed", "");
            calendarAdapter.addBooking(day, bookingInfo);
        }
    }

    private void loadBookedDays() {
        bookedDays = new ArrayList<>();
        // TODO: Implement loading booked days from database
        // For now, just add some example days
        bookedDays.add(5);
        bookedDays.add(6);
        bookedDays.add(7);
    }

    private void setupClickListeners() {
        editButton.setOnClickListener(v -> {
            try {
                Intent intent = new Intent(this, EditPropertyActivity.class);
                intent.putExtra("propertyId", propertyId);
                intent.putExtra("name", nameTextView.getText().toString());
                intent.putExtra("location", locationTextView.getText().toString());
                intent.putExtra("description", descriptionTextView.getText().toString());
                intent.putExtra("status", statusTextView.getText().toString());
                intent.putExtra("price", price);
                intent.putExtra("maxOccupants", maxOccupants);
                intent.putExtra("managerId", managerId);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAG, "Error opening edit screen", e);
                Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        bookButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddBookingActivity.class);
            intent.putExtra("propertyId", propertyId);
            startActivity(intent);
        });

        calendarGridView.setOnItemClickListener((parent, view, position, id) -> {
            int day = position + 1;
            BookingInfo booking = calendarAdapter.getBooking(day);
            if (booking != null) {
                showBookingDetails(booking);
            }
        });
    }

    private void showBookingDetails(BookingInfo booking) {
        String message = String.format("Booked by: %s\nFrom: %s\nTo: %s\nNotes: %s",
                booking.getGuestName(),
                formatDate(booking.getCheckInDate()),
                formatDate(booking.getCheckOutDate()),
                booking.getNotes());

        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Booking Details")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String formatDate(Calendar calendar) {
        return String.format("%d/%d/%d",
                calendar.get(Calendar.MONTH) + 1,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.property_details_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            onBackPressed();
            return true;
        } else if (itemId == R.id.action_delete) {
            showDeleteConfirmation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmation() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Delete Property")
                .setMessage("Are you sure you want to delete this property?")
                .setPositiveButton("Yes", (dialog, which) -> deleteProperty())
                .setNegativeButton("No", null)
                .show();
    }

    private void deleteProperty() {
        // TODO: Implement property deletion
        Toast.makeText(this, "Property deleted", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh property details and calendar when returning to this screen
        getPropertyDetails();
        setupCalendar();
    }
}