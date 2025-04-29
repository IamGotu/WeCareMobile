package com.sia.android.wecare;

public class Complaint {
    private int id;
    private String title;
    private String description;
    private String status;
    private String createdAt;

    public Complaint(int id, String title, String description, String status, String createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}