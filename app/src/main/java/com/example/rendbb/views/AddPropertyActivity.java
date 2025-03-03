package com.example.rendbb.views;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.models.Property;
import com.example.rendbb.repositories.PropertyManager;

public class AddPropertyActivity extends AppCompatActivity {

    private EditText propertyNameField, propertyLocationField, propertyDescriptionField;
    private Button savePropertyButton;
    private PropertyManager propertyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        propertyNameField = findViewById(R.id.propertyNameField);
        propertyLocationField = findViewById(R.id.propertyLocationField);
        propertyDescriptionField = findViewById(R.id.propertyDescriptionField);
        savePropertyButton = findViewById(R.id.savePropertyButton);

        propertyManager = new PropertyManager(this);

        savePropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = propertyNameField.getText().toString().trim();
                String location = propertyLocationField.getText().toString().trim();
                String description = propertyDescriptionField.getText().toString().trim();

                if (name.isEmpty() || location.isEmpty()) {
                    Toast.makeText(AddPropertyActivity.this, "Name and location are required!", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Example: using a fixed manager ID (update as needed)
                Property property = new Property(name, location, description, 1); // Default status as "free"
                long result = propertyManager.addProperty(property);
                if (result != -1) {
                    Toast.makeText(AddPropertyActivity.this, "Property added successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Return to previous screen (dashboard)
                } else {
                    Toast.makeText(AddPropertyActivity.this, "Failed to add property.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}