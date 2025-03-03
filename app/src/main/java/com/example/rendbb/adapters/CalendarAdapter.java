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
    private Context context;
    private Calendar calendar;
    private Map<Integer, BookingInfo> bookings;
    private int selectedDay = -1;

    public CalendarAdapter(Context context, Calendar calendar) {
        this.context = context;
        this.calendar = calendar;
        this.bookings = new HashMap<>();
    }

    @Override
    public int getCount() {
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
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
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.calendar_day_cell, parent, false);
            holder = new ViewHolder();
            holder.dayText = convertView.findViewById(R.id.dayText);
            holder.indicatorView = convertView.findViewById(R.id.bookingIndicator);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        int day = position + 1;
        holder.dayText.setText(String.valueOf(day));

        // Style for selected day
        if (day == selectedDay) {
            convertView.setBackgroundColor(Color.argb(50, 0, 0, 255));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        // Style for booked days
        BookingInfo booking = bookings.get(day);
        if (booking != null) {
            switch (booking.getStatus()) {
                case "confirmed":
                    holder.indicatorView.setBackgroundColor(Color.GREEN);
                    break;
                case "pending":
                    holder.indicatorView.setBackgroundColor(Color.YELLOW);
                    break;
                case "completed":
                    holder.indicatorView.setBackgroundColor(Color.BLUE);
                    break;
                default:
                    holder.indicatorView.setBackgroundColor(Color.GRAY);
            }
            holder.indicatorView.setVisibility(View.VISIBLE);
        } else {
            holder.indicatorView.setVisibility(View.GONE);
        }

        return convertView;
    }

    private static class ViewHolder {
        TextView dayText;
        View indicatorView;
    }

    public void setSelectedDay(int day) {
        this.selectedDay = day;
        notifyDataSetChanged();
    }

    public void addBooking(int day, BookingInfo booking) {
        bookings.put(day, booking);
        notifyDataSetChanged();
    }

    public void removeBooking(int day) {
        bookings.remove(day);
        notifyDataSetChanged();
    }

    public BookingInfo getBooking(int day) {
        return bookings.get(day);
    }

    public void clearBookings() {
        bookings.clear();
        notifyDataSetChanged();
    }

    public void setMonth(Calendar newCalendar) {
        this.calendar = newCalendar;
        notifyDataSetChanged();
    }
}