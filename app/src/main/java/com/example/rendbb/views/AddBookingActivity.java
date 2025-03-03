package com.example.rendbb.views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
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

        // Enable back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Booking");

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

        // Setup date pickers
        setupDatePickers();

        // Setup button listeners
        setupClickListeners();
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

        // Set property name
        // TODO: Get property name from database
        propertyNameText.setText("Property #" + propertyId);
    }

    private void setupDatePickers() {
        checkInDate = Calendar.getInstance();
        checkOutDate = Calendar.getInstance();
        checkOutDate.add(Calendar.DAY_OF_MONTH, 1);

        updateDateTexts();

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

        // TODO: Implement booking creation in DatabaseHelper
        Toast.makeText(this, "Booking saved successfully", Toast.LENGTH_SHORT).show();
        finish();
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