package com.example.notestakingapp.shared;

import android.graphics.Bitmap;
import android.net.Uri;

import java.util.Objects;

public class Item {
    public static final int TYPE_EDIT_TEXT = 0;
    public static final int TYPE_IMAGE_VIEW = 1;
    public static final int TYPE_VOICE_VIEW = 2;
    public static final int TYPE_EDIT_TEXT_TITLE = 3;

    private int type;
    private String text;
    private Uri imageUri;
    private Uri voiceUri;

    private int textSegmentId;
    private int voiceId;
    private int imageId;
    private byte[] audioData;

    public byte[] getAudioData() {
        return audioData;
    }

    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }

    private Bitmap imageBitmap;
    private Bitmap voiceBitmap;


    //tao Item khi image selected from thu vien or camera
    public Item(int type, Uri propUri, int propId, String props) {
        if (Objects.equals(props, "image")) {
            this.type = type;
            this.text = text;
            this.imageUri = propUri;
            this.imageId = propId;

        } else if (Objects.equals(props, "voice")) {
            this.type = type;
            this.text = text;
            this.voiceUri = propUri;
            this.voiceId = propId;
        }
    }

    public Item(int type, String text, Bitmap propBitmap, int propId, String props) {
        if (Objects.equals(props, "image")) {
            this.type = type;
            this.text = text;
            this.imageBitmap = propBitmap;
            this.imageId = propId;
        } else if (Objects.equals(props, "voice")) {
            this.type = type;
            this.text = text;
            this.voiceBitmap = propBitmap;
            this.voiceId = propId;
        }
    }
    public Item(int type, byte[] audioData, int audioId) {
        this.type = type;
        this.audioData = audioData;
        this.voiceId = audioId;
    }


    //tao item text khi bat dau vao noteedit
    public Item(int type, int textSegmentId) {
        this.type = type;
        this.text = "";
        this.textSegmentId = textSegmentId;
    }

    public Item(int type, String text, int textSegmentId) {
        this.type = type;
        this.text = text;
        this.textSegmentId = textSegmentId;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public Uri getVoiceUri() {
        return voiceUri;
    }

    public void setVoiceUri(Uri voiceUri) {
        this.voiceUri = voiceUri;
    }

    public int getTextSegmentId() {
        return textSegmentId;
    }

    public void setTextSegmentId(int textSegmentId) {
        this.textSegmentId = textSegmentId;
    }

    public int getVoiceId() {
        return voiceId;
    }

    public void setVoiceId(int voiceId) {
        this.voiceId = voiceId;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public Bitmap getVoiceBitmap() {
        return voiceBitmap;
    }

    public void setVoiceBitmap(Bitmap voiceBitmap) {
        this.voiceBitmap = voiceBitmap;
    }

    //tao Item image khi draw xong


    public int getType() {
        return type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Uri getImageUri() {
        return imageUri;
    }
}
