package com.example.rendbb.models;

public class Property {
    private int id;
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

    // Default constructor
    public Property() {}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getManagerId() {
        return managerId;
    }

    public void setManagerId(int managerId) {
        this.managerId = managerId;
    }
}