package com.example.notestakingapp.database.NoteComponent;

public class Audio {
    private int audioId;
    private int noteId;
    private byte[] audioData;
    private long createAt;

    public Audio(int audioId, int noteId, byte[] audioData) {
        this.audioId = audioId;
        this.noteId = noteId;
        this.audioData = audioData;
    }

    public Audio(int audioId, int noteId, byte[] audioData, long createAt) {
        this.audioId = audioId;
        this.noteId = noteId;
        this.audioData = audioData;
        this.createAt = createAt;
    }

    public int getAudioId() {
        return audioId;
    }

    public void setAudioId(int audioId) {
        this.audioId = audioId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public byte[] getAudioData() {
        return audioData;
    }

    public void setAudioData(byte[] audioData) {
        this.audioData = audioData;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }
}
