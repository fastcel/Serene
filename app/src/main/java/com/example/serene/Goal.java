package com.example.serene;

public class Goal {
    private String id;
    private String title;
    private String status;
    private String priority;
    private String date;
    private String time;
    public Goal() {}
    public Goal(String id, String title, String status,
                String priority, String date, String time) {
        this.id = id;
        this.title = title;
        this.status = status;
        this.priority = priority;
        this.date = date;
        this.time = time;
    }
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getStatus() { return status; }
    public String getPriority() { return priority; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setStatus(String status) { this.status = status; }
    public void setPriority(String priority) { this.priority = priority; }
    public void setDate(String date) { this.date = date; }
    public void setTime(String time) { this.time = time; }
}