package com.example.rendbb.models;

public class Property {
    private String name;
    private String location;
    private String description;
    private String status; // e.g., "free", "occupied", "nearly"
    private int managerId; // Add managerId field

    // Constructor with all five parameters
    public Property(String name, String location, String description, String status, int managerId) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.status = status;
        this.managerId = managerId;
    }

    // Overloaded constructor that defaults status to "free"
    public Property(String name, String location, String description, int managerId) {
        this(name, location, description, "free", managerId);
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public int getManagerId() {
        return managerId;
    }
}