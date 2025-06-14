package net.openvpn.openvpn;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationChannelCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;

public class NotificationService
{
    private static final String CHANNEL_ID = "openvpn_notification_id";

    private final Context context;
    private final NotificationManagerCompat notificationManager;

    public static class Action {
        public final PendingIntent intent;
        public final String label;

        public Action(String label, PendingIntent intent) {
            this.label = label;
            this.intent = intent;
        }
    }

    public NotificationService(Context context) {
        this.context = context.getApplicationContext();
        this.notificationManager = NotificationManagerCompat.from(this.context);
        createChannelIfNeeded();
    }

    private void createChannelIfNeeded() {
        NotificationChannelCompat channel = new NotificationChannelCompat.Builder(CHANNEL_ID, NotificationManagerCompat.IMPORTANCE_DEFAULT)
                .setName(context.getString(R.string.channel_name))
                .setDescription(context.getString(R.string.channel_description))
                .build();
        notificationManager.createNotificationChannel(channel);
    }

    public NotificationCompat.Builder buildNotification(Intent intent) {
        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setOnlyAlertOnce(true)
                .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setWhen(new Date().getTime());
    }

    public boolean showNotification(int id, Notification notification) {
        try {
            notificationManager.notify(id, notification);
            return true;
        } catch (SecurityException e) {
            Log.e("NotificationService", "Permission denied for notification: " + e.getMessage());
            return false;
        }
    }

    public void cancelNotification(int id) {
        notificationManager.cancel(id);
    }

    public void updateNotificationActions(NotificationCompat.Builder builder, Action... actions) {
        builder.clearActions();
        for (Action action : actions) {
            if (action.label != null && action.intent != null) {
                builder.addAction(0, action.label, action.intent);
            }
        }
    }

    public void updateNotificationContent(NotificationCompat.Builder builder, int iconResId, String contentText) {
        builder.setSmallIcon(iconResId).setContentText(contentText);
    }
} 