package com.example.notestakingapp.database.NoteComponent;

public class Audio {
    private int audioId;
    private int noteId;
    private byte[] audioData;

    public Audio(int audioId, int noteId, byte[] audioData) {
        this.audioId = audioId;
        this.noteId = noteId;
        this.audioData = audioData;
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
}
