package com.example.notestakingapp.database.NoteComponent;

import com.example.notestakingapp.database.DatabaseHandler;

public class Tag {
    private int tagId;
    private String tagName;

    public Tag(int tagId, String tagName) {
        this.tagId = tagId;
        this.tagName = tagName;
    }

    public Tag (){
        tagId = DatabaseHandler.TAG_ID_DEFAULT;
        tagName = DatabaseHandler.TAG_NAME_DEFAULT;
    }
    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
}
