package com.example.hipenjava.models;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hipenjava.Activities.Notification.NotificationDetailActivity;
import com.example.hipenjava.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.example.hipenjava.models.Notification;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder> {
    private List<Notification> notifications;
    private Context context;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_notification, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.name.setText(notification.getName());

        String userId = FirebaseAuth.getInstance().getUid();
        DatabaseReference userNotificationRef = FirebaseDatabase.getInstance()
                .getReference("user_notifications").child(userId).child(String.valueOf(notification.getId()));

        // Set icon based on type
        if ("event".equals(notification.getType())) {
            holder.icon.setImageResource(R.drawable.noti_event);
        } else if ("class".equals(notification.getType())) {
            holder.icon.setImageResource(R.drawable.noti_zoom);
        }


        // Parse the timeUp value from Firebase
        String timeUp = notification.getTimeUp();
        if (timeUp != null && !timeUp.isEmpty()) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                Date notificationDate = dateFormat.parse(timeUp);
                Date currentDate = new Date();

                // Calculate the difference in days
                long diffInMillis = currentDate.getTime() - notificationDate.getTime();
                long daysDiff = TimeUnit.MILLISECONDS.toDays(diffInMillis);

                // Display "Hôm nay" or "... ngày trước"
                String timeText = (daysDiff == 0) ? "Hôm nay" : daysDiff + " ngày trước";
                holder.timeUp.setText(timeText);

            } catch (ParseException e) {
                e.printStackTrace();
                holder.timeUp.setText("N/A"); // Handle parsing error
            }
        } else {
            holder.timeUp.setText("null"); // Handle null or empty timeUp
        }
        // Check if the notification is read for the current user
        userNotificationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean isRead = snapshot.exists() && snapshot.getValue(Boolean.class);
                if (isRead) {
                    holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.lightPink)); // Gray for read
                } else {
                    holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white)); // White for unread
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotificationDetailActivity.class);
            intent.putExtra("notificationID", notification.getId());
            intent.putExtra("notificationName", notification.getName());
            intent.putExtra("notificationDetail", notification.getDetail());
            intent.putExtra("notificationType", notification.getType());

            // Mark as read for the current user
            userNotificationRef.setValue(true);

            context.startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView name, timeUp;
        ImageView icon;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.notification_name);
            timeUp = itemView.findViewById(R.id.notification_time);
            icon = itemView.findViewById(R.id.notification_icon);
        }
    }
}