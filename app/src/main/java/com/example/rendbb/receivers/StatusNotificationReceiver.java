package com.example.rendbb.receivers;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.rendbb.R;
import java.util.ArrayList;
import java.util.List;

public class StatusNotificationReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "property_status_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        // In a real application, query your database for property statuses.
        // Here we simulate with dummy data.
        List<PropertyStatus> statusList = getDummyPropertyStatuses();

        createNotificationChannel(context);

        for (PropertyStatus ps : statusList) {
            String message = null;
            if ("yellow".equalsIgnoreCase(ps.status)) {
                message = "Property " + ps.propertyName + " is nearly free! Only "
                        + ps.daysLeft + " day" + (ps.daysLeft == 1 ? "" : "s") + " left until booking ends.";
            } else if ("green".equalsIgnoreCase(ps.status)) {
                message = "Property " + ps.propertyName + " is now free.";
            }
            if (message != null) {
                sendNotification(context, ps.propertyName, message);
            }
        }
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Property Status Channel";
            String description = "Notifications for property status updates";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void sendNotification(Context context, String propertyName, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification) // Ensure you have this icon in res/drawable
                .setContentTitle("Status Update: " + propertyName)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        int notificationId = propertyName.hashCode();
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(notificationId, builder.build());
        }
    }

    // Dummy method to simulate retrieval of property statuses.
    private List<PropertyStatus> getDummyPropertyStatuses() {
        List<PropertyStatus> list = new ArrayList<>();
        // "yellow" means nearly free; "green" means free.
        list.add(new PropertyStatus("Villa Sunshine", "yellow", 2));
        list.add(new PropertyStatus("Mountain Retreat", "green", 0));
        list.add(new PropertyStatus("Urban Apartment", "green", 0));
        return list;
    }

    // Helper class to hold property status data.
    private static class PropertyStatus {
        String propertyName;
        String status; // "green" or "yellow"
        int daysLeft;  // For yellow, number of days left until booking ends

        PropertyStatus(String propertyName, String status, int daysLeft) {
            this.propertyName = propertyName;
            this.status = status;
            this.daysLeft = daysLeft;
        }
    }
}
