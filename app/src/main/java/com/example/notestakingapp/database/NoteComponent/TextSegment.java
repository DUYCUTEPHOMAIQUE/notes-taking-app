package com.example.notestakingapp.database.NoteComponent;

public class TextSegment {
    private int textId;
    private int noteId;
    private String text;

    public TextSegment(int textId, int noteId, String text) {
        this.textId = textId;
        this.noteId = noteId;
        this.text = text;
    }

    public int getTextId() {
        return textId;
    }

    public void setTextId(int textId) {
        this.textId = textId;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
