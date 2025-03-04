package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.models.PropertyItem;
import com.example.rendbb.repositories.PropertyManager;

public class AddPropertyActivity extends AppCompatActivity {
    private EditText nameEditText;
    private EditText locationEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText maxOccupantsEditText;
    private Button saveButton;
    private Button cancelButton;
    private PropertyManager propertyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        // Enable back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add New Property");

        // Initialize views
        initializeViews();

        // Initialize property manager
        propertyManager = new PropertyManager(this);

        // Setup button listeners
        setupClickListeners();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        maxOccupantsEditText = findViewById(R.id.maxOccupantsEditText);
        saveButton = findViewById(R.id.saveButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void setupClickListeners() {
        saveButton.setOnClickListener(v -> saveProperty());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void saveProperty() {
        // Validate input fields
        if (!validateInputs()) {
            return;
        }

        try {
            // Create new property
            PropertyItem property = new PropertyItem(
                    0, // id will be set by database
                    nameEditText.getText().toString().trim(),
                    locationEditText.getText().toString().trim(),
                    descriptionEditText.getText().toString().trim(),
                    "available", // default status
                    getCurrentUserId(),
                    Double.parseDouble(priceEditText.getText().toString().trim()),
                    Integer.parseInt(maxOccupantsEditText.getText().toString().trim())
            );

            // Save property to database
            long result = propertyManager.addProperty(property);

            if (result != -1) {
                Toast.makeText(this, "Property added successfully", Toast.LENGTH_SHORT).show();
                // Return to dashboard
                Intent intent = new Intent(this, ManagerDashboardActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error adding property", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter valid numbers for price and occupants",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean validateInputs() {
        boolean isValid = true;

        if (TextUtils.isEmpty(nameEditText.getText())) {
            nameEditText.setError("Property name is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(locationEditText.getText())) {
            locationEditText.setError("Location is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(priceEditText.getText())) {
            priceEditText.setError("Price is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(maxOccupantsEditText.getText())) {
            maxOccupantsEditText.setError("Maximum occupants is required");
            isValid = false;
        }

        return isValid;
    }

    private int getCurrentUserId() {
        // TODO: Implement proper user management
        // For now, return a default manager ID
        return 1;
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