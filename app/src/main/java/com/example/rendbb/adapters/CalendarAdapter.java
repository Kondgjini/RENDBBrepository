package com.example.rendbb.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.rendbb.R;
import com.example.rendbb.models.BookingInfo;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class CalendarAdapter extends BaseAdapter {
    private final Context context;
    private final Calendar calendar;
    private final Map<Integer, BookingInfo> bookings;
    private int selectedDay = -1;

    private static final int DAYS_IN_WEEK = 7;
    private static final int MAX_DAYS_DISPLAYED = 35; // 5 weeks

    public CalendarAdapter(Context context, Calendar calendar) {
        this.context = context;
        this.calendar = calendar;
        this.bookings = new HashMap<>();
    }

    @Override
    public int getCount() {
        return MAX_DAYS_DISPLAYED;
    }

    @Override
    public Object getItem(int position) {
        return position + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View dayView;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            dayView = inflater.inflate(R.layout.day_cell, null);
        } else {
            dayView = convertView;
        }

        TextView dayText = dayView.findViewById(R.id.dayText);
        int day = position + 1;

        dayText.setText(String.valueOf(day));

        // Set text color based on whether the day is booked
        if (bookings.containsKey(day)) {
            dayText.setTextColor(Color.RED);
        } else {
            dayText.setTextColor(Color.BLACK);
        }

        // Highlight selected day
        if (day == selectedDay) {
            dayView.setBackgroundColor(Color.LTGRAY);
        } else {
            dayView.setBackgroundColor(Color.TRANSPARENT);
        }

        return dayView;
    }

    public void addBooking(int day, BookingInfo booking) {
        bookings.put(day, booking);
        notifyDataSetChanged();
    }

    public BookingInfo getBooking(int day) {
        return bookings.get(day);
    }

    public void setSelectedDay(int day) {
        this.selectedDay = day;
    }

    public Calendar getCalendar() {
        return calendar;
    }
}