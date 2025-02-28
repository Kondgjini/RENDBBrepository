package com.example.rendbb.models;

public class Property {
    private String name;
    private String location;
    private String description;
    private String status; // e.g., "free", "occupied", "nearly"

    // Constructor with all four parameters
    public Property(String name, String location, String description, String status) {
        this.name = name;
        this.location = location;
        this.description = description;
        this.status = status;
    }

    // Overloaded constructor that defaults status to "free"
    public Property(String name, String location, String description) {
        this(name, location, description, "free");
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
}
