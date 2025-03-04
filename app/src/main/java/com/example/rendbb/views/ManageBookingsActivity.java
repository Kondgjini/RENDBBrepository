package com.example.rendbb.views;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.adapters.BookingAdapter;
import com.example.rendbb.models.BookingInfo;
import com.example.rendbb.utilities.DatabaseHelper;
import java.util.List;

public class ManageBookingsActivity extends AppCompatActivity {
    private static final String TAG = "ManageBookingsActivity";
    private ListView bookingsListView;
    private TextView emptyView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bookings);

        try {
            // Enable back button
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Manage Bookings");
            }

            // Initialize views
            bookingsListView = findViewById(R.id.bookingsListView);
            emptyView = findViewById(R.id.emptyView);

            // Initialize database helper
            dbHelper = new DatabaseHelper(this);

            // Load bookings
            loadBookings();

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadBookings() {
        try {
            // Get all bookings from database
            List<BookingInfo> bookings = dbHelper.getAllBookings();

            if (bookings.isEmpty()) {
                // Display empty view message
                if (emptyView != null) {
                    bookingsListView.setEmptyView(emptyView);
                    emptyView.setText("No bookings found");
                }
            } else {
                // Create adapter
                BookingAdapter adapter = new BookingAdapter(this, bookings);
                bookingsListView.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading bookings", e);
            Toast.makeText(this, "Error loading bookings: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadBookings();
    }
}