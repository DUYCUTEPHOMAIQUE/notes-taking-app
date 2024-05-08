package com.example.notestakingapp.database.NoteComponent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ToDo {
    private int todoId;
    private String content;
    private String createAt;
    private String duration;

    public ToDo(int todoId, @Nullable String content, @NonNull String createAt, @Nullable  String duration) {
        this.todoId = todoId;
        this.content = content;
        this.createAt = createAt;
        this.duration = duration;
    }

    public int getId() {
        return todoId;
    }

    public void setId(int todoId) {
        this.todoId = todoId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
