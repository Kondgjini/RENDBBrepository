package com.example.rendbb.views;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.rendbb.R;
import com.example.rendbb.adapters.CalendarAdapter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class PropertyDetailsActivity extends AppCompatActivity {

    private TextView propertyDetailsText, bookingPeriodText;
    private Button selectStartDateButton, selectEndDateButton, bookNowButton;
    private GridView calendarGridView;
    private Calendar startDate, endDate;
    private List<Integer> bookedDays;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property_details);

        propertyDetailsText = findViewById(R.id.propertyDetailsText);
        bookingPeriodText = findViewById(R.id.bookingPeriodText);
        selectStartDateButton = findViewById(R.id.selectStartDateButton);
        selectEndDateButton = findViewById(R.id.selectEndDateButton);
        bookNowButton = findViewById(R.id.bookNowButton);
        calendarGridView = findViewById(R.id.calendarGridView);

        // Retrieve property details from intent extras
        String name = getIntent().getStringExtra("name");
        String location = getIntent().getStringExtra("location");
        String description = getIntent().getStringExtra("description");
        String status = getIntent().getStringExtra("status");

        propertyDetailsText.setText("Name: " + name + "\nLocation: " + location + "\n" +
                description + "\nStatus: " + status);

        // Initialize start and end dates to current date
        startDate = Calendar.getInstance();
        endDate = Calendar.getInstance();

        // Initialize bookedDays list (dummy data; in a real app, load from your database)
        bookedDays = new ArrayList<>();
        bookedDays.add(15);
        bookedDays.add(16);
        bookedDays.add(17);

        // Set up the custom calendar using GridView with CalendarAdapter
        Calendar currentCalendar = Calendar.getInstance();
        final CalendarAdapter calendarAdapter = new CalendarAdapter(this, currentCalendar, bookedDays);
        calendarGridView.setAdapter(calendarAdapter);

        final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        // Set up the Select Start Date button
        selectStartDateButton.setOnClickListener(v -> {
            Calendar current = Calendar.getInstance();
            new DatePickerDialog(PropertyDetailsActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            startDate.set(year, month, dayOfMonth);
                            selectStartDateButton.setText("Start: " + dateFormat.format(startDate.getTime()));
                            updateBookingPeriodDisplay();
                        }
                    },
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Set up the Select End Date button
        selectEndDateButton.setOnClickListener(v -> {
            Calendar current = Calendar.getInstance();
            new DatePickerDialog(PropertyDetailsActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            endDate.set(year, month, dayOfMonth);
                            selectEndDateButton.setText("End: " + dateFormat.format(endDate.getTime()));
                            updateBookingPeriodDisplay();
                        }
                    },
                    current.get(Calendar.YEAR),
                    current.get(Calendar.MONTH),
                    current.get(Calendar.DAY_OF_MONTH)
            ).show();
        });

        // Set up the Book Now button
        bookNowButton.setOnClickListener(v -> {
            String bookingInfo = "Booked from " + dateFormat.format(startDate.getTime())
                    + " to " + dateFormat.format(endDate.getTime());
            bookingPeriodText.setText(bookingInfo);
            int bookedDay = startDate.get(Calendar.DAY_OF_MONTH);
            if (!bookedDays.contains(bookedDay)) {
                bookedDays.add(bookedDay);
                calendarAdapter.notifyDataSetChanged();
            }
        });
    }

    // Method to update the displayed booking period
    private void updateBookingPeriodDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String display = "Selected booking: " + dateFormat.format(startDate.getTime())
                + " to " + dateFormat.format(endDate.getTime());
        bookingPeriodText.setText(display);
    }
}
