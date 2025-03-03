package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rendbb.R;
import com.example.rendbb.adapters.PropertyAdapter;
import com.example.rendbb.models.Property;
import com.example.rendbb.utilities.DatabaseHelper;

import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity {

    private Button addPropertyButton, manageBookingsButton;
    private ImageButton preferencesGear;
    private RecyclerView propertiesRecyclerView;
    private PropertyAdapter propertyAdapter;
    private DatabaseHelper dbHelper;
    private List<Property> propertyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        addPropertyButton = findViewById(R.id.addPropertyButton);
        manageBookingsButton = findViewById(R.id.manageBookingsButton);
        preferencesGear = findViewById(R.id.preferencesGear);
        propertiesRecyclerView = findViewById(R.id.propertiesRecyclerView);

        dbHelper = new DatabaseHelper(this);

        addPropertyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle add property button click
                Intent intent = new Intent(ManagerDashboardActivity.this, AddPropertyActivity.class);
                startActivity(intent);
            }
        });

        manageBookingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle manage bookings button click
                Intent intent = new Intent(ManagerDashboardActivity.this, ManageBookingsActivity.class);
                startActivity(intent);
            }
        });

        preferencesGear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle preferences gear click
                Intent intent = new Intent(ManagerDashboardActivity.this, PreferencesActivity.class);
                startActivity(intent);
            }
        });

        // Initialize RecyclerView (set adapter, layout manager, etc.)
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Fetch properties from the database
        propertyList = dbHelper.getAllProperties();

        // Set up the adapter with the property list
        propertyAdapter = new PropertyAdapter(this, propertyList);
        propertiesRecyclerView.setAdapter(propertyAdapter);
    }
}