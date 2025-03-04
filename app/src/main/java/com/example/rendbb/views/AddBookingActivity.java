package com.example.rendbb.views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.models.BookingInfo;
import com.example.rendbb.utilities.DatabaseHelper;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddBookingActivity extends AppCompatActivity {
    private static final String TAG = "AddBookingActivity";
    private TextView propertyNameText;
    private EditText guestNameEdit;
    private EditText guestEmailEdit;
    private EditText guestPhoneEdit;
    private TextView checkInDateText;
    private TextView checkOutDateText;
    private EditText notesEdit;
    private Button saveButton;
    private Button cancelButton;

    private int propertyId;
    private DatabaseHelper dbHelper;
    private Calendar checkInDate;
    private Calendar checkOutDate;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_booking);

        try {
            // Enable back button in action bar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Add Booking");
            }

            // Initialize helpers
            dbHelper = new DatabaseHelper(this);
            dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());

            // Get property ID from intent
            propertyId = getIntent().getIntExtra("propertyId", -1);
            if (propertyId == -1) {
                Toast.makeText(this, "Error loading property", Toast.LENGTH_SHORT).show();
                finish();
                return;
            }

            // Initialize views
            initializeViews();

            // Set the property name
            String propertyName = getIntent().getStringExtra("propertyName");
            if (propertyName != null) {
                propertyNameText.setText(propertyName);
            } else {
                propertyNameText.setText("Property #" + propertyId);
            }

            // Setup dates
            setupDates();

            // Setup button listeners
            setupClickListeners();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeViews() {
        propertyNameText = findViewById(R.id.propertyNameText);
        guestNameEdit = findViewById(R.id.guestNameEdit);
        guestEmailEdit = findViewById(R.id.guestEmailEdit);
        guestPhoneEdit = findViewById(R.id.guestPhoneEdit);
        checkInDateText = findViewById(R.id.checkInDateText);
        checkOutDateText = findViewById(R.id.checkOutDateText);
        notesEdit = findViewById(R.id.notesEdit);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupDates() {
        // Set the check-in date from intent if provided
        int year = getIntent().getIntExtra("checkInYear", -1);
        int month = getIntent().getIntExtra("checkInMonth", -1);
        int day = getIntent().getIntExtra("checkInDay", -1);

        if (year != -1 && month != -1 && day != -1) {
            checkInDate = Calendar.getInstance();
            checkInDate.set(year, month, day);

            // Set check-out date to the next day
            checkOutDate = (Calendar) checkInDate.clone();
            checkOutDate.add(Calendar.DAY_OF_MONTH, 1);

            updateDateTexts();
        } else {
            // Default dates if none provided
            checkInDate = Calendar.getInstance();
            checkOutDate = Calendar.getInstance();
            checkOutDate.add(Calendar.DAY_OF_MONTH, 1);

            updateDateTexts();
        }

        checkInDateText.setOnClickListener(v -> showDatePicker(true));
        checkOutDateText.setOnClickListener(v -> showDatePicker(false));
    }

    private void showDatePicker(boolean isCheckIn) {
        Calendar calendar = isCheckIn ? checkInDate : checkOutDate;
        DatePickerDialog picker = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar selected = Calendar.getInstance();
                    selected.set(year, month, dayOfMonth);

                    if (isCheckIn) {
                        if (selected.before(Calendar.getInstance())) {
                            Toast.makeText(this, "Cannot select past dates", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        checkInDate = selected;
                        if (checkInDate.after(checkOutDate)) {
                            checkOutDate = (Calendar) checkInDate.clone();
                            checkOutDate.add(Calendar.DAY_OF_MONTH, 1);
                        }
                    } else {
                        if (selected.before(checkInDate)) {
                            Toast.makeText(this, "Check-out must be after check-in",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        checkOutDate = selected;
                    }
                    updateDateTexts();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        picker.show();
    }

    private void updateDateTexts() {
        checkInDateText.setText(dateFormat.format(checkInDate.getTime()));
        checkOutDateText.setText(dateFormat.format(checkOutDate.getTime()));
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveBooking());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveBooking() {
        if (!validateInputs()) {
            return;
        }

        try {
            double pricePerNight = getIntent().getDoubleExtra("price", 0.0);

            // Calculate number of nights
            long diffInMillis = checkOutDate.getTimeInMillis() - checkInDate.getTimeInMillis();
            int nights = (int) (diffInMillis / (24 * 60 * 60 * 1000));
            double totalPrice = pricePerNight * nights;

            // Create booking object
            BookingInfo booking = new BookingInfo(
                    0, // id will be set by database
                    propertyId,
                    1, // assuming user id 1
                    guestNameEdit.getText().toString().trim(),
                    checkInDate,
                    checkOutDate,
                    totalPrice,
                    "pending", // default status
                    notesEdit.getText().toString().trim()
            );

            // Save to database
            long result = dbHelper.addBooking(booking);

            if (result != -1) {
                Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error saving booking", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving booking", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(guestNameEdit.getText())) {
            guestNameEdit.setError("Guest name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(guestEmailEdit.getText())) {
            guestEmailEdit.setError("Guest email is required");
            isValid = false;
        }

        return isValid;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}