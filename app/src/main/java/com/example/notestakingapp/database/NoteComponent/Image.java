package com.example.notestakingapp.database.NoteComponent;

public class Image {
    private int imageId;
    private int noteId;
    private byte[] imageData;
    private long createAt;

    public Image(int imageId, int noteId, byte[] imageData) {
        this.imageId = imageId;
        this.noteId = noteId;
        this.imageData = imageData;
    }

    public Image(int imageId, int noteId, byte[] imageData, long createAt) {
        this.imageId = imageId;
        this.noteId = noteId;
        this.imageData = imageData;
        this.createAt = createAt;
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

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}
