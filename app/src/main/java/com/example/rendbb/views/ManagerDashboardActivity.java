package com.example.rendbb.views;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rendbb.R;
import com.example.rendbb.adapters.PropertyAdapter;
import com.example.rendbb.models.PropertyItem;
import com.example.rendbb.repositories.PropertyManager;
import com.example.rendbb.utilities.DatabaseHelper;
import java.util.ArrayList;
import java.util.List;

public class ManagerDashboardActivity extends AppCompatActivity {

    private static final String TAG = "ManagerDashboardActivity";
    private Button addPropertyButton, manageBookingsButton, clearDatabaseButton, logoutButton;
    private ImageButton preferencesGear;
    private RecyclerView propertiesRecyclerView;
    private PropertyManager propertyManager;
    private PropertyAdapter propertyAdapter;
    private DatabaseHelper dbHelper;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_manager_dashboard);

            propertyManager = new PropertyManager(this);
            dbHelper = new DatabaseHelper(this);
            session = new SessionManager(getApplicationContext());
            initializeViews();
            setupClickListeners();
            setupRecyclerView();
            loadDashboardData();
        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            finish();
        }
    }

    private void initializeViews() {
        addPropertyButton = findViewById(R.id.addPropertyButton);
        manageBookingsButton = findViewById(R.id.manageBookingsButton);
        preferencesGear = findViewById(R.id.preferencesGear);
        propertiesRecyclerView = findViewById(R.id.propertiesRecyclerView);
        clearDatabaseButton = findViewById(R.id.clearDatabaseButton);
        logoutButton = findViewById(R.id.logoutButton);
    }

    private void setupClickListeners() {
        addPropertyButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddPropertyActivity.class);
            startActivity(intent);
        });

        manageBookingsButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, ManageBookingsActivity.class);
            startActivity(intent);
        });

        preferencesGear.setOnClickListener(v -> {
            Intent intent = new Intent(this, PreferencesActivity.class);
            startActivity(intent);
        });

        clearDatabaseButton.setOnClickListener(v -> {
            dbHelper.clearAllTables();
            Toast.makeText(this, "Database cleared", Toast.LENGTH_SHORT).show();
            loadDashboardData();
        });

        logoutButton.setOnClickListener(v -> {
            session.logoutUser();
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, LoginActivity.class);
            // Clear back stack so user can't go back to dashboard after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }

    private void setupRecyclerView() {
        propertiesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        propertiesRecyclerView.setHasFixedSize(true);
    }

    private void loadDashboardData() {
        try {
            List<PropertyItem> properties = propertyManager.getAllProperties();

            // Sort properties based on status priority
            properties.sort((p1, p2) -> {
                int p1Priority = getStatusPriority(p1.getStatus());
                int p2Priority = getStatusPriority(p2.getStatus());
                return p1Priority - p2Priority;
            });

            propertyAdapter = new PropertyAdapter(properties, item -> {
                Intent intent = new Intent(this, PropertyDetailsActivity.class);
                intent.putExtra("propertyId", item.getId());
                intent.putExtra("name", item.getName());
                intent.putExtra("location", item.getLocation());
                intent.putExtra("description", item.getDescription());
                intent.putExtra("status", item.getStatus());
                intent.putExtra("price", item.getPricePerNight());
                intent.putExtra("maxOccupants", item.getMaxOccupants());
                intent.putExtra("managerId", item.getManagerId());
                startActivity(intent);
            });

            propertiesRecyclerView.setAdapter(propertyAdapter);
        } catch (Exception e) {
            Log.e(TAG, "Error loading dashboard data", e);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_filter) {
            showFilterDialog();
            return true;
        } else if (itemId == R.id.action_sort) {
            showSortDialog();
            return true;
        } else if (itemId == R.id.action_refresh) {
            loadDashboardData();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        String[] options = {"All", "Available", "Occupied", "Maintenance"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Filter Properties")
                .setItems(options, (dialog, which) -> {
                    filterProperties(options[which]);
                })
                .show();
    }

    private void showSortDialog() {
        String[] options = {"Status", "Name", "Location", "Price"};
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Sort Properties")
                .setItems(options, (dialog, which) -> {
                    sortProperties(options[which]);
                })
                .show();
    }

    private void filterProperties(String filter) {
        List<PropertyItem> allProperties = propertyManager.getAllProperties();
        List<PropertyItem> filteredList = new ArrayList<>();

        if (filter.equals("All")) {
            filteredList = allProperties;
        } else {
            for (PropertyItem property : allProperties) {
                if (property.getStatus().equalsIgnoreCase(filter)) {
                    filteredList.add(property);
                }
            }
        }

        propertyAdapter.updateItems(filteredList);
    }

    private void sortProperties(String sortBy) {
        List<PropertyItem> items = new ArrayList<>(propertyAdapter.getItems());

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
            case "price":
                items.sort((p1, p2) -> Double.compare(p1.getPricePerNight(), p2.getPricePerNight()));
                break;
        }

        propertyAdapter.updateItems(items);
    }

    private int getStatusPriority(String status) {
        if (status == null) {
            return 4;
        }

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
    protected void onResume() {
        super.onResume();
        loadDashboardData();
    }
}