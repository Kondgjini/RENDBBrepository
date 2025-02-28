package com.example.rendbb.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rendbb.R;
import com.example.rendbb.adapters.PropertyAdapter;
import com.example.rendbb.models.Property;
import com.example.rendbb.repositories.PropertyManager;

import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity {

    private RecyclerView propertiesRecyclerView;
    private List<Property> propertiesList;
    private PropertyAdapter propertyAdapter;
    private Button addPropertyButton, manageBookingsButton;
    private PropertyManager propertyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        // Initialize views
        propertiesRecyclerView = findViewById(R.id.propertiesRecyclerView);
        addPropertyButton = findViewById(R.id.addPropertyButton);
        manageBookingsButton = findViewById(R.id.manageBookingsButton);

        // Initialize your PropertyManager (assumes you have implemented getAllProperties())
        propertyManager = new PropertyManager(this);

        // Initialize the property list and adapter
        propertiesList = new ArrayList<>();
        propertyAdapter = new PropertyAdapter(this, propertiesList);
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertiesRecyclerView.setAdapter(propertyAdapter);

        // Set click listener for Add Property button
        addPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerDashboardActivity.this, AddPropertyActivity.class));
            }
        });

        // Set click listener for Manage Bookings button
        manageBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ManagerDashboardActivity.this, ManageBookingsActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPropertiesFromDatabase();
    }

    /**
     * Loads properties from the database.
     * For demonstration, if the database returns no data, dummy properties are added.
     */
    private void loadPropertiesFromDatabase() {
        propertiesList.clear();
        Cursor cursor = propertyManager.getAllProperties();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // Get property details from the database cursor
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                String location = cursor.getString(cursor.getColumnIndexOrThrow("location"));
                String description = cursor.getString(cursor.getColumnIndexOrThrow("description"));
                // Determine property status. Here we use dummy logic:
                String status;
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                if (id % 3 == 0) {
                    status = "occupied";
                } else if (id % 3 == 1) {
                    status = "nearly";
                } else {
                    status = "free";
                }
                propertiesList.add(new Property(name, location, description, status));
            } while (cursor.moveToNext());
            cursor.close();
        } else {
            // If no data, add dummy properties for demonstration
            propertiesList.add(new Property("Villa Sunshine", "123 Beach Road, Miami", "Beautiful villa", "free"));
            propertiesList.add(new Property("Mountain Retreat", "45 Hilltop Street, Denver", "Cozy mountain retreat", "occupied"));
            propertiesList.add(new Property("Urban Apartment", "78 Central Ave, New York", "Modern apartment", "nearly"));
        }
        propertyAdapter.notifyDataSetChanged();
    }
}
