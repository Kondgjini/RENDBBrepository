package com.example.rendbb.views;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rendbb.R;
import com.example.rendbb.adapters.PropertyAdapter;
import com.example.rendbb.repositories.PropertyManager;
import com.example.rendbb.utilities.DatabaseHelper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity {

    private Button addPropertyButton, manageBookingsButton;
    private ImageButton preferencesGear;
    private RecyclerView propertiesRecyclerView;
    private PropertyManager propertyManager;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_dashboard);

        // Initialize database helpers
        propertyManager = new PropertyManager(this);
        dbHelper = new DatabaseHelper(this);

        // Initialize UI components
        initializeViews();
        setupClickListeners();
        setupRecyclerView();

        // Load initial data
        loadDashboardData();
    }

    private void initializeViews() {
        addPropertyButton = findViewById(R.id.addPropertyButton);
        manageBookingsButton = findViewById(R.id.manageBookingsButton);
        preferencesGear = findViewById(R.id.preferencesGear);
        propertiesRecyclerView = findViewById(R.id.propertiesRecyclerView);
    }

    private void setupClickListeners() {
        addPropertyButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, AddPropertyActivity.class);
            startActivity(intent);
        });

        manageBookingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, ManageBookingsActivity.class);
            startActivity(intent);
        });

        preferencesGear.setOnClickListener(v -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, PreferencesActivity.class);
            startActivity(intent);
        });
    }

    private void setupRecyclerView() {
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertiesRecyclerView.setHasFixedSize(true);
    }

    private void loadDashboardData() {
        // Get all properties
        Cursor propertiesCursor = propertyManager.getAllProperties();
        List<PropertyItem> propertyItems = new ArrayList<>();

        while (propertiesCursor.moveToNext()) {
            int id = propertiesCursor.getInt(propertiesCursor.getColumnIndex("id"));
            String name = propertiesCursor.getString(propertiesCursor.getColumnIndex("name"));
            String location = propertiesCursor.getString(propertiesCursor.getColumnIndex("location"));
            String description = propertiesCursor.getString(propertiesCursor.getColumnIndex("description"));

            // Calculate dynamic status based on bookings
            String status = dbHelper.getPropertyStatus(id);

            PropertyItem item = new PropertyItem(id, name, location, description, status);
            propertyItems.add(item);
        }
        propertiesCursor.close();

        // Sort properties based on status priority
        propertyItems.sort((p1, p2) -> {
            int p1Priority = getStatusPriority(p1.getStatus());
            int p2Priority = getStatusPriority(p2.getStatus());
            return p1Priority - p2Priority;
        });

        // Update RecyclerView
        PropertyAdapter adapter = new PropertyAdapter(propertyItems, item -> {
            Intent intent = new Intent(ManagerDashboardActivity.this, PropertyDetailsActivity.class);
            intent.putExtra("propertyId", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("status", item.getStatus());
            startActivity(intent);
        });
        propertiesRecyclerView.setAdapter(adapter);
    }

    private int getStatusPriority(String status) {
        switch (status.toLowerCase()) {
            case "occupied":
                return 1;
            case "maintenance":
                return 2;
            case "available":
                return 3;
            default:
                return 4;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter:
                showFilterDialog();
                return true;
            case R.id.action_sort:
                showSortDialog();
                return true;
            case R.id.action_refresh:
                loadDashboardData();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showFilterDialog() {
        String[] options = {"All", "Available", "Occupied", "Maintenance"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Filter Properties")
                .setItems(options, (dialog, which) -> {
                    String selectedFilter = options[which];
                    filterProperties(selectedFilter);
                })
                .show();
    }

    private void showSortDialog() {
        String[] options = {"Status", "Name", "Location", "Price"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Sort Properties")
                .setItems(options, (dialog, which) -> {
                    String selectedSort = options[which];
                    sortProperties(selectedSort);
                })
                .show();
    }

    private void filterProperties(String filter) {
        // Implementation of property filtering
        if (filter.equals("All")) {
            loadDashboardData();
            return;
        }

        Cursor cursor = propertyManager.getAllProperties();
        List<PropertyItem> filteredList = new ArrayList<>();

        while (cursor.moveToNext()) {
            String status = dbHelper.getPropertyStatus(
                    cursor.getInt(cursor.getColumnIndex("id")));

            if (status.equalsIgnoreCase(filter)) {
                filteredList.add(new PropertyItem(
                        cursor.getInt(cursor.getColumnIndex("id")),
                        cursor.getString(cursor.getColumnIndex("name")),
                        cursor.getString(cursor.getColumnIndex("location")),
                        cursor.getString(cursor.getColumnIndex("description")),
                        status
                ));
            }
        }
        cursor.close();

        PropertyAdapter adapter = new PropertyAdapter(filteredList, item -> {
            Intent intent = new Intent(this, PropertyDetailsActivity.class);
            intent.putExtra("propertyId", item.getId());
            intent.putExtra("name", item.getName());
            intent.putExtra("location", item.getLocation());
            intent.putExtra("description", item.getDescription());
            intent.putExtra("status", item.getStatus());
            startActivity(intent);
        });
        propertiesRecyclerView.setAdapter(adapter);
    }

    private void sortProperties(String sortBy) {
        PropertyAdapter adapter = (PropertyAdapter) propertiesRecyclerView.getAdapter();
        if (adapter != null) {
            List<PropertyItem> items = new ArrayList<>(adapter.getItems());

            switch (sortBy.toLowerCase()) {
                case "name":
                    items.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                    break;
                case "location":
                    items.sort((p1, p2) -> p1.getLocation().compareToIgnoreCase(p2.getLocation()));
                    break;
                case "status":
                    items.sort((p1, p2) -> {
                        int p1Priority = getStatusPriority(p1.getStatus());
                        int p2Priority = getStatusPriority(p2.getStatus());
                        return p1Priority - p2Priority;
                    });
                    break;
            }

            adapter.updateItems(items);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadDashboardData(); // Refresh data when returning to dashboard
    }
}