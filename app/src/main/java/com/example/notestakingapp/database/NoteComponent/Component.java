package com.example.notestakingapp.database.NoteComponent;

public class Component {
    private int noteId;
    private int componentId;
    private long createAt;
    private int type;

    public Component(int noteId, int componentId, long createAt, int type) {
        this.noteId = noteId;
        this.componentId = componentId;
        this.createAt = createAt;
        this.type = type;
    }

    public int getNoteId() {
        return noteId;
    }

    public void setNoteId(int noteId) {
        this.noteId = noteId;
    }

    public int getComponentId() {
        return componentId;
    }

    public void setComponentId(int componentId) {
        this.componentId = componentId;
    }

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
