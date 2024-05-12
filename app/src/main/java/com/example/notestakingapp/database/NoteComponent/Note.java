package com.example.notestakingapp.database.NoteComponent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notestakingapp.database.DatabaseHandler;

public class Note {
    private int noteId;
    private String title;
    private long createAt;
    private String color;
    private int tagId;

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

    @Deprecated
    public int getTagId() {
        return tagId;
    }
    @Deprecated
    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
    @Deprecated
    public Note(int noteId, @Nullable String title, @NonNull Long createAt, @Nullable String color, @Nullable Integer tagId) {
        this.noteId = noteId;
        this.title = title;
        this.createAt = createAt;
        this.color = color;
        if (tagId != null) this.tagId = tagId;
    }

    public Note(int noteId, String title, long createAt, String color) {
        this.noteId = noteId;
        this.title = title;
        this.createAt = createAt;
        this.color = color;
    }

    public Note(int noteId, String title, String color) {
        this.noteId = noteId;
        this.title = title;
        this.color = color;
    }
}
