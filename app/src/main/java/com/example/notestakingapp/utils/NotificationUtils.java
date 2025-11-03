package com.example.notestakingapp.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class NotificationUtils {
    private static final String KEY_NOTIFICATON = "key_notification";

    public static void saveNotificationState(Context context, String state) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NOTIFICATON, state);
        editor.apply();
    }
    public static String getNotificationState(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
        return prefs.getString(KEY_NOTIFICATON, null);
    }
}
