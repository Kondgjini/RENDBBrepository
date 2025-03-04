package com.example.rendbb.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.example.rendbb.R;
import com.example.rendbb.models.BookingInfo;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class BookingAdapter extends ArrayAdapter<BookingInfo> {
    private final Context context;
    private final List<BookingInfo> bookings;
    private final SimpleDateFormat dateFormat;

    public BookingAdapter(Context context, List<BookingInfo> bookings) {
        super(context, R.layout.item_booking, bookings);
        this.context = context;
        this.bookings = bookings;
        this.dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_booking, parent, false);
        }

        BookingInfo booking = bookings.get(position);

        TextView propertyIdView = view.findViewById(R.id.propertyIdText);
        TextView guestNameView = view.findViewById(R.id.guestNameText);
        TextView datesView = view.findViewById(R.id.datesText);
        TextView statusView = view.findViewById(R.id.statusText);
        TextView priceView = view.findViewById(R.id.priceText);

        propertyIdView.setText("Property ID: " + booking.getPropertyId());
        guestNameView.setText(booking.getGuestName());
        datesView.setText(dateFormat.format(booking.getCheckInDate().getTime()) +
                " to " +
                dateFormat.format(booking.getCheckOutDate().getTime()));
        statusView.setText(booking.getStatus());
        priceView.setText(String.format("$%.2f", booking.getTotalPrice()));

        return view;
    }
}