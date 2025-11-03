package com.example.notestakingapp.notification;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.notestakingapp.R;
import com.example.notestakingapp.database.NoteComponent.Note;
import com.example.notestakingapp.database.NoteComponent.ToDo;
import com.example.notestakingapp.notification.AlarmReceiver;
import com.example.notestakingapp.database.DatabaseHandler;

import java.util.Objects;

public class AlarmScheduler {

    public final static boolean IS_TODO = false;
    public final static boolean IS_NOTE = true;

    @SuppressLint("ScheduleExactAlarm")
    public static void scheduleTaskAlarm(Context context, int Id, long duration, boolean check) {
        Object task = null;
       // ToDo task = DatabaseHandler.getToDoById(context, todoId);
        if (check == IS_TODO){ // if check = 0 task = todo else task = note
            task = DatabaseHandler.getToDoById(context, Id);
        } else if(check == IS_NOTE)  {
            task = DatabaseHandler.getNoteById(context, Id);
        }
        // Tạo Intent để gửi tới TaskAlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);

        assert task != null;
        String defaultValue = context.getString(R.string.you_hava_work_to_do);
        String notiString = "";
        if(task instanceof ToDo) {
            notiString = ((ToDo)task).getContent().isEmpty() ? defaultValue: ((ToDo)task).getContent();
        } else {
            notiString = ((Note)task).getTitle().isEmpty() ? defaultValue: ((Note)task).getTitle();
        }
        intent.putExtra("TASK_CONTENT", notiString);
        intent.putExtra("NOTIFICATION_ID", Id);
        intent.putExtra("CHECK", check);

        // Tạo PendingIntent để sử dụng trong AlarmManager
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context,
                Id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Lấy AlarmManager từ hệ thống
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Kiểm tra xem alarmManager có null hay không, và đặt báo thức nếu thời gian kích hoạt hợp lệ
        if (alarmManager != null) {
            if (duration > System.currentTimeMillis()) {
                Log.d("AlarmScheduler", "Setting alarm for trigger time: " + duration);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, duration, alarmIntent);
            } else {
                Log.d("AlarmScheduler", "Trigger time is not valid for setting alarm.");
            }
        } else {
            Log.e("AlarmScheduler", "AlarmManager is null, cannot set alarm.");
        }

    }

    public static void cancelTaskAlarm(Context context, int Id) {
        // Tạo Intent để gửi tới TaskAlarmReceiver
        Intent intent = new Intent(context, AlarmReceiver.class);

        // Tạo PendingIntent để sử dụng trong AlarmManager
        PendingIntent alarmIntent = PendingIntent.getBroadcast(
                context,
                Id,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Lấy AlarmManager từ hệ thống
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // Kiểm tra xem alarmManager có null hay không, và hủy báo thức nếu có
        if (alarmManager != null) {
            Log.d("AlarmScheduler", "Cancelling alarm with notificationId: " + Id);
            alarmManager.cancel(alarmIntent);
        } else {
            Log.e("AlarmScheduler", "AlarmManager is null, cannot cancel alarm.");
        }
        Log.d("TODO", "TODO DELETED");
    }
}
