package com.example.notestakingapp.notification;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.notestakingapp.R;
import com.example.notestakingapp.ui.MainActivity;

public class TodoNotification {

    public static void sendNotification(Context context, String message, int notificationId, boolean check) {
        // Log khi bắt đầu gửi thông báo
        Log.d("NotificationUtils", "Sending notification with ID: " + notificationId);
        // Lấy NotificationManager từ hệ thống
        NotificationManager notificationManager = ContextCompat.getSystemService(context, NotificationManager.class);

        // Log nếu notificationManager là null
        if (notificationManager == null) {
            Log.e("NotificationUtils", "NotificationManager is null. Notification not sent.");
            return;
        }

        // Tạo Intent để mở MainActivity khi người dùng nhấn vào thông báo
        Intent intent = new Intent(context, MainActivity.class);

        String title;
        if (check) {
            title = "NOTE REMIND";
        } else {
            title = "TODO PAST DUE";
        }

        // Tạo PendingIntent để sử dụng trong thông báo
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                notificationId,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Tạo NotificationCompat.Builder để cấu hình thông báo
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, context.getString(R.string.app_name))
                .setSmallIcon(R.drawable.arrow_down_icon)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                        R.mipmap.ic_launcher))
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Gửi thông báo
        notificationManager.notify(notificationId, builder.build());

        // Log khi thông báo được gửi thành công
        Log.d("NotificationUtils", "Notification sent successfully with ID: " + notificationId);
    }

}
