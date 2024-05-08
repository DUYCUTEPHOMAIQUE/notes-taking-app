package com.example.notestakingapp.utils;

import android.content.Context;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

//chatGpt
public class AudioUtils {

    // Đọc tệp âm thanh từ thư mục raw và chuyển đổi thành mảng byte
    public static byte[] convertAudioToByteArray(Context context, int audioResourceId) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        InputStream inputStream = context.getResources().openRawResource(audioResourceId);
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
