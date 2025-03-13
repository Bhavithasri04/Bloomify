package com.example.myapplication;

public class Routine {

    private int id;
    private String title;
    private String time;
    private String scheduleType;
    private boolean isCompleted;

    public Routine(int id, String title, String time, String scheduleType, boolean isCompleted) {
        this.id = id;
        this.title = title;
        this.time = time;
        this.scheduleType = scheduleType;
        this.isCompleted = isCompleted;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getTime() {
        return time;
    }

    public String getScheduleType() {
        return scheduleType;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
