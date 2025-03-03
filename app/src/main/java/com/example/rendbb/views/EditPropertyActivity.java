package com.example.rendbb.views;

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

public class EditPropertyActivity extends AppCompatActivity {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_property);

        // Enable back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit Property");

        // Initialize property manager
        propertyManager = new PropertyManager(this);

        // Get property ID from intent
        propertyId = getIntent().getIntExtra("propertyId", -1);
        if (propertyId == -1) {
            Toast.makeText(this, "Error loading property", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Initialize views
        initializeViews();

        // Load property data
        loadPropertyData();

        // Setup button listeners
        setupClickListeners();
    }

    private void initializeViews() {
        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        priceEditText = findViewById(R.id.priceEditText);
        maxOccupantsEditText = findViewById(R.id.maxOccupantsEditText);
        updateButton = findViewById(R.id.updateButton);
        cancelButton = findViewById(R.id.cancelButton);
    }

    private void loadPropertyData() {
        // Load property details
        List<PropertyItem> properties = propertyManager.getAllProperties();
        for (PropertyItem property : properties) {
            if (property.getId() == propertyId) {
                currentProperty = property;
                break;
            }
        }

        if (currentProperty != null) {
            nameEditText.setText(currentProperty.getName());
            locationEditText.setText(currentProperty.getLocation());
            descriptionEditText.setText(currentProperty.getDescription());
            priceEditText.setText(String.valueOf(currentProperty.getPricePerNight()));
            maxOccupantsEditText.setText(String.valueOf(currentProperty.getMaxOccupants()));
        }
    }

    private void setupClickListeners() {
        updateButton.setOnClickListener(v -> updateProperty());
        cancelButton.setOnClickListener(v -> finish());
    }

    private void updateProperty() {
        if (!validateInputs()) {
            return;
        }

        try {
            PropertyItem updatedProperty = new PropertyItem(
                    propertyId,
                    nameEditText.getText().toString().trim(),
                    locationEditText.getText().toString().trim(),
                    descriptionEditText.getText().toString().trim(),
                    currentProperty.getStatus(),
                    currentProperty.getManagerId(),
                    Double.parseDouble(priceEditText.getText().toString().trim()),
                    Integer.parseInt(maxOccupantsEditText.getText().toString().trim())
            );

            int result = propertyManager.updateProperty(updatedProperty);
            if (result > 0) {
                Toast.makeText(this, "Property updated successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Error updating property", Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}