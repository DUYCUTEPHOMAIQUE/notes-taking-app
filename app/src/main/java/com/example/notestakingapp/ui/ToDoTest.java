package com.example.notestakingapp.ui;

public class ToDoTest {
    private int todoId;
    private String content;
    private long createAt;
    private long duration;
    private boolean isCompleted;

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }

    public ToDoTest(int todoId, String content, long duration, boolean isCompleted) {
        this.todoId = todoId;
        this.content = content;
        this.duration = duration;
        this.createAt = System.currentTimeMillis();
        this.isCompleted = isCompleted;
    }

    public int getTodoId() {
        return todoId;
    }

    public void setTodoId(int todoId) {
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

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }
}
