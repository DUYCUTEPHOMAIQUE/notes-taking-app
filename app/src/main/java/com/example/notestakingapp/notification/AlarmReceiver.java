package com.example.notestakingapp.notification;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Log khi nhận được intent
        Log.d("AlarmReceiver", "Received alarm broadcast");

        // Lấy dữ liệu từ Intent
        String taskTitle = intent.getStringExtra("TASK_TITLE");
        String taskContent = intent.getStringExtra("TASK_CONTENT");
        int notificationId = intent.getIntExtra("NOTIFICATION_ID", 0);
        boolean check = intent.getBooleanExtra("CHECK", false);

        // Log thông tin của todo
        Log.d("AlarmReceiver", "Received task title: " + taskTitle);
        Log.d("AlarmReceiver", "Received task content: " + taskContent);
        Log.d("AlarmReceiver", "Received notification ID: " + notificationId);

        // Gửi thông báo bằng TodoNotification
        TodoNotification.sendNotification(context, taskContent, notificationId, check);
    }

}
