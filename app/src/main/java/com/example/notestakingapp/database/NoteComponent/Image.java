package com.example.notestakingapp.database.NoteComponent;

public class Image {
    private int imageId;
    private int noteId;
    private byte[] imageData;

    public Image(int imageId, int noteId, byte[] imageData) {
        this.imageId = imageId;
        this.noteId = noteId;
        this.imageData = imageData;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public byte[] getImageData() {
        return imageData;
    }

    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }
}
