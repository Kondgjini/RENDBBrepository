package com.example.rendbb.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.rendbb.R;
import com.example.rendbb.models.PropertyItem;
import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.ViewHolder> {
    private List<PropertyItem> items;
    private OnPropertyClickListener listener;

    public interface OnPropertyClickListener {
        void onPropertyClick(PropertyItem item);
    }

    public PropertyAdapter(List<PropertyItem> items, OnPropertyClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        PropertyItem item = items.get(position);
        holder.nameText.setText(item.getName());
        holder.locationText.setText(item.getLocation());
        holder.statusText.setText(item.getStatus());

        // Set status color
        switch (item.getStatus().toLowerCase()) {
            case "available":
                holder.statusText.setTextColor(Color.GREEN);
                break;
            case "occupied":
                holder.statusText.setTextColor(Color.RED);
                break;
            case "maintenance":
                holder.statusText.setTextColor(Color.BLUE);
                break;
            default:
                holder.statusText.setTextColor(Color.GRAY);
        }

        // Set price if available
        if (item.getPricePerNight() > 0) {
            holder.priceText.setText(String.format("$%.2f/night", item.getPricePerNight()));
            holder.priceText.setVisibility(View.VISIBLE);
        } else {
            holder.priceText.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onPropertyClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<PropertyItem> getItems() {
        return items;
    }

    public void updateItems(List<PropertyItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nameText;
        TextView locationText;
        TextView statusText;
        TextView priceText;

        ViewHolder(View view) {
            super(view);
            nameText = view.findViewById(R.id.property_name);
            locationText = view.findViewById(R.id.property_location);
            statusText = view.findViewById(R.id.property_status);
            priceText = view.findViewById(R.id.property_price);
        }
    }
}