package com.example.notestakingapp.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class WaitFunc {
    public static void showMessageWithDelay(Context context, int milliseconds) {
        try {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {

                }
            }, milliseconds);
        } catch (Exception e) {
            Toast.makeText(context, "Error in wait func", Toast.LENGTH_SHORT).show();
        }

    }
}
