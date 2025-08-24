//package com.example.inventorymanager.Helper;
//
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.media.MediaPlayer;
//import android.os.Build;
//
//import androidx.core.app.NotificationCompat;
//
//import com.example.inventorymanager.R;
//
//public class LowStockReceiver extends BroadcastReceiver {
//    @Override
//    public void onReceive(Context context, Intent intent) {
//        String productName = intent.getStringExtra("productName");
//        showNotification(context, "Low Stock Alert",
//                "Stock for " + productName + " is low (5 or less)");
//
//        MediaPlayer mp = MediaPlayer.create(context, R.raw.low_stock_alert); // put mp3/wav in res/raw
//        mp.start();
//    }
//
//    private void showNotification(Context context, String title, String message) {
//        String channelId = "low_stock_channel";
//        NotificationManager notificationManager =
//                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (notificationManager.getNotificationChannel(channelId) == null) {
//                NotificationChannel channel = new NotificationChannel(
//                        channelId,
//                        "Low Stock Notifications",
//                        NotificationManager.IMPORTANCE_HIGH
//                );
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
//                .setSmallIcon(android.R.drawable.ic_dialog_alert)
//                .setContentTitle(title)
//                .setContentText(message)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setAutoCancel(true);
//
//        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
//    }
//}
package com.example.inventorymanager.Helper;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.example.inventorymanager.R;

public class LowStockReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String productName = intent.getStringExtra("productName");
        long intervalMillis = intent.getLongExtra("intervalMillis", 0);

        // Show notification
        showNotification(context, "Low Stock Alert",
                "Stock for " + productName + " is low!");


        MediaPlayer mp = MediaPlayer.create(context, R.raw.low_stock_alert); // put mp3/wav in res/raw
        mp.start();

        // ✅ Reschedule the alarm (so it repeats forever)
        if (intervalMillis > 0) {
            scheduleNextAlarm(context, productName, intervalMillis);
        }
    }

    private void showNotification(Context context, String title, String message) {
        String channelId = "low_stock_channel";
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager.getNotificationChannel(channelId) == null) {
                NotificationChannel channel = new NotificationChannel(
                        channelId,
                        "Low Stock Notifications",
                        NotificationManager.IMPORTANCE_HIGH
                );
                notificationManager.createNotificationChannel(channel);
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }

    @SuppressLint("ScheduleExactAlarm")
    private void scheduleNextAlarm(Context context, String productName, long intervalMillis) {
        Intent intent = new Intent(context, LowStockReceiver.class);
        intent.putExtra("productName", productName);
        intent.putExtra("intervalMillis", intervalMillis);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                productName.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis() + intervalMillis,
                pendingIntent
        );
    }
}
