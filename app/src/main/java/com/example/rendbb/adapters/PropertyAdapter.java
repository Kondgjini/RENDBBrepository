package com.example.rendbb.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rendbb.R;
import com.example.rendbb.models.Property;
import com.example.rendbb.views.PropertyDetailsActivity;

import java.util.List;

public class PropertyAdapter extends RecyclerView.Adapter<PropertyAdapter.PropertyViewHolder> {

    private Context context;
    private List<Property> properties;

    public PropertyAdapter(Context context, List<Property> properties) {
        this.context = context;
        this.properties = properties;
    }

    @NonNull
    @Override
    public PropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_property_card, parent, false);
        return new PropertyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PropertyViewHolder holder, int position) {
        Property property = properties.get(position);
        holder.propertyName.setText(property.getName());
        holder.propertyAddress.setText(property.getLocation());
        // For demonstration, we use the description field to show additional info.
        holder.propertyStatus.setText(property.getDescription());

        // Set traffic-light indicator based on property status
        if (property.getStatus().equalsIgnoreCase("free")) {
            holder.statusIndicator.setImageResource(R.drawable.status_green);
        } else if (property.getStatus().equalsIgnoreCase("nearly")) {
            holder.statusIndicator.setImageResource(R.drawable.status_yellow);
        } else if (property.getStatus().equalsIgnoreCase("occupied")) {
            holder.statusIndicator.setImageResource(R.drawable.status_red);
        } else {
            // Default to green if unknown
            holder.statusIndicator.setImageResource(R.drawable.status_green);
        }

        // Set the click listener to open PropertyDetailsActivity
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PropertyDetailsActivity.class);
                intent.putExtra("name", property.getName());
                intent.putExtra("location", property.getLocation());
                intent.putExtra("description", property.getDescription());
                intent.putExtra("status", property.getStatus());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return properties.size();
    }

    public static class PropertyViewHolder extends RecyclerView.ViewHolder {
        TextView propertyName, propertyAddress, propertyStatus;
        ImageView statusIndicator;

        public PropertyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyName = itemView.findViewById(R.id.propertyName);
            propertyAddress = itemView.findViewById(R.id.propertyAddress);
            propertyStatus = itemView.findViewById(R.id.propertyStatus);
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
        }
    }
}
