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

import java.util.List;

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
        holder.timeUp.setText(notification.getTimeUp());

        // Set icon based on type
        if ("event".equals(notification.getType())) {
            holder.icon.setImageResource(R.drawable.noti_event);
        } else if ("class".equals(notification.getType())) {
            holder.icon.setImageResource(R.drawable.noti_zoom);
        }
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NotificationDetailActivity.class);
            intent.putExtra("notificationID", notification.getId());
            intent.putExtra("notificationName", notification.getName());
            intent.putExtra("notificationDetail", notification.getDetail());
            intent.putExtra("notificationType", notification.getType());

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
            timeUp = itemView.findViewById(R.id.notification_timeup);
            icon = itemView.findViewById(R.id.notification_icon);
        }
    }
}