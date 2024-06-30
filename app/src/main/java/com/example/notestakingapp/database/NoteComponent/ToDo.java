package com.example.notestakingapp.database.NoteComponent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.airbnb.lottie.L;

public class ToDo {
    private int todoId;
    private String content;
    private long createAt;
    private long duration;
    private boolean isCompleted;

    public ToDo(int todoId, String content, long createAt,@Nullable long duration, boolean isCompleted) {
        this.todoId = todoId;
        this.content = content;
        this.createAt = createAt;
        this.duration = duration;
        this.isCompleted = isCompleted;
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

    public long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(long createAt) {
        this.createAt = createAt;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
