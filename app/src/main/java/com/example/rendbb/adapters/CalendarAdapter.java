package com.example.rendbb.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.example.rendbb.R;
import java.util.Calendar;
import java.util.List;

public class CalendarAdapter extends BaseAdapter {

    private Context context;
    private Calendar calendar;
    private List<Integer> bookedDays; // day numbers (1-31) that are booked

    public CalendarAdapter(Context context, Calendar calendar, List<Integer> bookedDays) {
        this.context = context;
        this.calendar = calendar;
        this.bookedDays = bookedDays;
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
        // Inflate day_cell.xml layout for each cell
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.day_cell, parent, false);
        }
        TextView dayText = convertView.findViewById(R.id.dayText);
        int day = position + 1;
        dayText.setText(String.valueOf(day));
        // Highlight booked days with a yellow background
        if (bookedDays.contains(day)) {
            dayText.setBackgroundColor(Color.YELLOW);
        } else {
            dayText.setBackgroundColor(Color.TRANSPARENT);
        }
        return convertView;
    }
}
