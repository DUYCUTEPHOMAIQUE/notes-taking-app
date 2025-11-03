package com.example.notestakingapp.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CurrentTime {
    public static String getCurrentTimeText() {
        String[] months = {"",
                "January", "February", "March", "April", "May", "June",
                "July", "August", "September", "October", "November", "December"
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH); // Tháng bắt đầu từ 0 (0 - January, 1 - February, ...)
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY); // 24 giờ format
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        String newStr = year + " " + months[month]+ " "+ dayOfMonth+ " "+hourOfDay+":"+minute+ " |";
        return  newStr;
    }
    public static String convertTimeFromMiliSecond(long time) {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd.HH.mm");
        String rs = formatter.format(date);
        return rs;
    }

}
