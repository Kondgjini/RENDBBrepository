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

    private EditText nameEditText, locationEditText, descriptionEditText;
    private Button addButton;
    private PropertyManager propertyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_property);

        nameEditText = findViewById(R.id.nameEditText);
        locationEditText = findViewById(R.id.locationEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        addButton = findViewById(R.id.addButton);
        propertyManager = new PropertyManager(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameEditText.getText().toString();
                String location = locationEditText.getText().toString();
                String description = descriptionEditText.getText().toString();

                Property property = new Property();
                property.setName(name);
                property.setLocation(location);
                property.setDescription(description);
                property.setStatus("free"); // Assuming default status is "free"

                long result = propertyManager.addProperty(property);
                if (result != -1) {
                    Toast.makeText(AddPropertyActivity.this, "Property added successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AddPropertyActivity.this, "Failed to add property", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}