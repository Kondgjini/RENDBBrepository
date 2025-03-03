package com.example.rendbb.models;

public class PropertyItem {
    private int id;
    private String name;
    private String location;
    private String description;
    private String status;
    private int managerId;
    private double pricePerNight;
    private int maxOccupants;

    public PropertyItem(int id, String name, String location, String description,
                        String status, int managerId, double pricePerNight, int maxOccupants) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.description = description;
        this.status = status;
        this.managerId = managerId;
        this.pricePerNight = pricePerNight;
        this.maxOccupants = maxOccupants;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }
    public int getManagerId() { return managerId; }
    public double getPricePerNight() { return pricePerNight; }
    public int getMaxOccupants() { return maxOccupants; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setDescription(String description) { this.description = description; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }
}