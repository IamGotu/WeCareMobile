package com.sia.android.wecare;

public class Complaint {
    private final int id;
    private final String title;
    private final String description;
    private final String status;
    private final String createdAt;
    private final String priority;
    private final String assignedPersonnel;
    private final String resolutionNotes;
    private final String resolvedAt;

    // Constructor for basic complaint (dashboard view)
    public Complaint(int id, String title, String description, String status, String createdAt) {
        this(id, title, description, status, createdAt,
                "", "", "", ""); // Default empty values for additional fields
    }

    // Full constructor for complaint history
    public Complaint(int id, String title, String description, String status, String createdAt,
                     String priority, String assignedPersonnel, String resolutionNotes, String resolvedAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.priority = priority;
        this.assignedPersonnel = assignedPersonnel;
        this.resolutionNotes = resolutionNotes;
        this.resolvedAt = resolvedAt;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getPriority() {
        return priority;
    }

    public String getAssignedPersonnel() {
        return assignedPersonnel;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public String getResolvedAt() {
        return resolvedAt;
    }

    // Optional: You might want to add a method to format dates or status for display
    public String getFormattedStatus() {
        switch (status) {
            case "pending":
                return "Pending";
            case "in_progress":
                return "In Progress";
            case "resolved":
                return "Resolved";
            default:
                return status;
        }
    }
}