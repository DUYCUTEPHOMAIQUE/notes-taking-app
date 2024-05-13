package com.example.notestakingapp.database.NoteComponent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notestakingapp.database.DatabaseHandler;

public class Note {
    private int noteId;
    private String title;
    private long createAt;
    private String color;

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public Note(int noteId, String title, long createAt, String color) {
        this.noteId = noteId;
        this.title = title;
        this.createAt = createAt;
        this.color = color;
    }
}
