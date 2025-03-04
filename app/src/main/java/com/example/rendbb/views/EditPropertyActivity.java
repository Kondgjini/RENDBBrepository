package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.models.PropertyItem;
import com.example.rendbb.repositories.PropertyManager;
import com.example.rendbb.utilities.DatabaseHelper;
import java.util.List;

public class EditPropertyActivity extends AppCompatActivity {
    private static final String TAG = "EditPropertyActivity";
    private EditText nameEditText;
    private EditText locationEditText;
    private EditText descriptionEditText;
    private EditText priceEditText;
    private EditText maxOccupantsEditText;
    private Button updateButton;
    private Button cancelButton;

    private PropertyManager propertyManager;
    private int propertyId;
    private PropertyItem currentProperty;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        try {
            Log.d(TAG, "Starting EditPropertyActivity onCreate");
            // Enable back button in action bar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setTitle("Edit Property");
            }

            // Initialize property manager and database helper
            propertyManager = new PropertyManager(this);
            dbHelper = new DatabaseHelper(this);

            // Get property ID from intent
            propertyId = getIntent().getIntExtra("propertyId", -1);
            Log.d(TAG, "Received propertyId: " + propertyId);

            if (propertyId == -1) {
                Toast.makeText(this, "Error loading property - Invalid property ID", Toast.LENGTH_SHORT).show();
                return; // Don't finish() to prevent logout
            }

            // Initialize views
            initializeViews();

            // Load property data
            loadPropertyData();

            // Setup button listeners
            setupClickListeners();

            Log.d(TAG, "EditPropertyActivity onCreate completed successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            // Don't finish() here to prevent logout
        }
    }

    private void initializeViews() {
        try {
            nameEditText = findViewById(R.id.nameEditText);
            locationEditText = findViewById(R.id.locationEditText);
            descriptionEditText = findViewById(R.id.descriptionEditText);
            priceEditText = findViewById(R.id.priceEditText);
            maxOccupantsEditText = findViewById(R.id.maxOccupantsEditText);
            updateButton = findViewById(R.id.updateButton);
            cancelButton = findViewById(R.id.cancelButton);
            Log.d(TAG, "Views initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing views", e);
            Toast.makeText(this, "Error initializing views: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadPropertyData() {
        try {
            Log.d(TAG, "Loading property data for property ID: " + propertyId);
            Intent intent = getIntent();

            // Get values from intent extras
            String name = intent.getStringExtra("name");
            String location = intent.getStringExtra("location");
            String description = intent.getStringExtra("description");
            String status = intent.getStringExtra("status");
            double price = intent.getDoubleExtra("price", 0.0);
            int maxOccupants = intent.getIntExtra("maxOccupants", 0);
            int managerId = intent.getIntExtra("managerId", 1);

            Log.d(TAG, "Intent extras - name: " + name + ", location: " + location +
                    ", price: " + price + ", status: " + status);

            // First try to get property from database
            PropertyItem dbProperty = dbHelper.getProperty(propertyId);

            // If database property exists, use it
            if (dbProperty != null) {
                Log.d(TAG, "Found property in database: " + dbProperty.getName() + ", price: " + dbProperty.getPricePerNight());
                currentProperty = dbProperty;
            }
            // Otherwise try using PropertyManager
            else {
                List<PropertyItem> properties = propertyManager.getAllProperties();
                for (PropertyItem property : properties) {
                    if (property.getId() == propertyId) {
                        currentProperty = property;
                        Log.d(TAG, "Found property in PropertyManager: " + property.getName());
                        break;
                    }
                }
            }

            // If still null, create from intent extras
            if (currentProperty == null && name != null) {
                Log.d(TAG, "Creating property from intent extras");
                currentProperty = new PropertyItem(
                        propertyId,
                        name,
                        location,
                        description,
                        status,
                        managerId,
                        price,
                        maxOccupants
                );
            }

            // Update UI
            if (currentProperty != null) {
                nameEditText.setText(currentProperty.getName());
                locationEditText.setText(currentProperty.getLocation());
                descriptionEditText.setText(currentProperty.getDescription());
                priceEditText.setText(String.valueOf(currentProperty.getPricePerNight()));
                maxOccupantsEditText.setText(String.valueOf(currentProperty.getMaxOccupants()));
                Log.d(TAG, "Successfully populated UI fields with property data");
            } else {
                Log.e(TAG, "Property not found and couldn't be created from intent extras");
                Toast.makeText(this, "Property not found", Toast.LENGTH_SHORT).show();
                // Don't finish here to prevent logout
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading property data", e);
            Toast.makeText(this, "Error loading property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            // Don't finish here to prevent logout
        }
    }

    private void setupClickListeners() {
        updateButton.setOnClickListener(v -> updateProperty());
        cancelButton.setOnClickListener(v -> finish());
        Log.d(TAG, "Click listeners set up");
    }

    private void updateProperty() {
        Log.d(TAG, "Update button clicked");
        if (!validateInputs()) {
            Log.d(TAG, "Input validation failed");
            return;
        }

        try {
            // Get updated values from UI
            String name = nameEditText.getText().toString().trim();
            String location = locationEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();
            double price = Double.parseDouble(priceEditText.getText().toString().trim());
            int maxOccupants = Integer.parseInt(maxOccupantsEditText.getText().toString().trim());

            // Get existing values that we're not changing
            String status = currentProperty.getStatus();
            int managerId = currentProperty.getManagerId();

            Log.d(TAG, "Creating updated property - id: " + propertyId + ", name: " + name +
                    ", price: " + price + ", status: " + status);

            PropertyItem updatedProperty = new PropertyItem(
                    propertyId,
                    name,
                    location,
                    description,
                    status,
                    managerId,
                    price,
                    maxOccupants
            );

            // Update in database directly first for debugging
            int dbResult = dbHelper.updateProperty(updatedProperty);
            Log.d(TAG, "Direct database update result: " + dbResult);

            // Then try through PropertyManager
            int result = propertyManager.updateProperty(updatedProperty);
            Log.d(TAG, "PropertyManager update result: " + result);

            if (result > 0 || dbResult > 0) {
                Toast.makeText(this, "Property updated successfully", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Property updated successfully");
                // Return to previous screen
                finish();
            } else {
                Log.e(TAG, "Update returned 0 - no rows affected");
                Toast.makeText(this, "Error updating property - no changes made", Toast.LENGTH_SHORT).show();
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Number format exception", e);
            Toast.makeText(this, "Please enter valid numbers for price and occupants",
                    Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "Error updating property", e);
            Toast.makeText(this, "Error updating property: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        } else {
            try {
                double price = Double.parseDouble(priceEditText.getText().toString().trim());
                if (price <= 0) {
                    priceEditText.setError("Price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                priceEditText.setError("Invalid price format");
                isValid = false;
            }
        }

        if (TextUtils.isEmpty(maxOccupantsEditText.getText())) {
            maxOccupantsEditText.setError("Maximum occupants is required");
            isValid = false;
        } else {
            try {
                int maxOccupants = Integer.parseInt(maxOccupantsEditText.getText().toString().trim());
                if (maxOccupants <= 0) {
                    maxOccupantsEditText.setError("Maximum occupants must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                maxOccupantsEditText.setError("Invalid number format");
                isValid = false;
            }
        }

        Log.d(TAG, "Input validation result: " + isValid);
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

    @Override
    public void onBackPressed() {
        // Show confirmation dialog if changes were made
        if (hasUnsavedChanges()) {
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Discard Changes")
                    .setMessage("You have unsaved changes. Are you sure you want to discard them?")
                    .setPositiveButton("Discard", (dialog, which) -> super.onBackPressed())
                    .setNegativeButton("Keep Editing", null)
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean hasUnsavedChanges() {
        if (currentProperty == null) return false;

        try {
            String currentName = nameEditText.getText().toString().trim();
            String currentLocation = locationEditText.getText().toString().trim();
            String currentDescription = descriptionEditText.getText().toString().trim();

            // Safely parse numeric fields
            double currentPrice = 0;
            int currentMaxOccupants = 0;

            try {
                if (!TextUtils.isEmpty(priceEditText.getText())) {
                    currentPrice = Double.parseDouble(priceEditText.getText().toString().trim());
                }

                if (!TextUtils.isEmpty(maxOccupantsEditText.getText())) {
                    currentMaxOccupants = Integer.parseInt(maxOccupantsEditText.getText().toString().trim());
                }
            } catch (NumberFormatException e) {
                // If there's a parsing error, assume changes were made
                return true;
            }

            boolean hasChanges = !currentProperty.getName().equals(currentName) ||
                    !currentProperty.getLocation().equals(currentLocation) ||
                    !currentProperty.getDescription().equals(currentDescription) ||
                    currentProperty.getPricePerNight() != currentPrice ||
                    currentProperty.getMaxOccupants() != currentMaxOccupants;

            Log.d(TAG, "Has unsaved changes: " + hasChanges);
            return hasChanges;
        } catch (Exception e) {
            Log.e(TAG, "Error checking for unsaved changes", e);
            // If there's any exception, assume no changes to be safe
            return false;
        }
    }
}