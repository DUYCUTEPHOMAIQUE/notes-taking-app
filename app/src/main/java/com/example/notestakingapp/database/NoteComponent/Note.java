package com.example.notestakingapp.database.NoteComponent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.notestakingapp.database.DatabaseHandler;

public class Note {
    private int noteId;
    private String title;
    private String createAt;
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }


    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public Note(int noteId, @Nullable String title, @NonNull String createAt, @Nullable String color, @Nullable Integer tagId) {
        this.noteId = noteId;
        this.title = title;
        this.createAt = createAt;
        this.color = color;
        if (tagId != null) this.tagId = tagId;
    }

}
