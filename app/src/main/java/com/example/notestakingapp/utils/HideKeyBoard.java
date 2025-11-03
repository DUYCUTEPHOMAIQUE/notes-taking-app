package com.example.notestakingapp.utils;

import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class HideKeyBoard {
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        Log.d("duyKeyBoard", "hidekeyboard");
        Handler handler = new Handler();

        // Dùng handler để gửi một Runnable vào hàng đợi của looper
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Hành động sau khi đợi 1000ms (1 giây)
                // Đây là nơi để đặt code của bạn
            }
        }, 100);
    }
}
