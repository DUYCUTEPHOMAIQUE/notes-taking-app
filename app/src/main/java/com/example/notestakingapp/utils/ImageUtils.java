package com.example.notestakingapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

//chatGpt
public class ImageUtils {

    // drawable to byte[]
    public static byte[] convertDrawableToByteArray(Context context, int drawableId) {
        // Đọc tài nguyên drawable và tạo một đối tượng Bitmap từ nó
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);

        // Chuyển đổi Bitmap thành mảng byte
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            return stream.toByteArray();
        }
        return null;
    }

    //uri to byte[]
    public static byte[] uriToBytes(Uri uri, Context context) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //byte[] to bitmap
    public static Bitmap byteToBitmap(byte[] imageData) {
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
        return bitmap;
    }
}