package com.example.rendbb.models;

import java.util.Calendar;

public class BookingInfo {
    private int id;
    private int propertyId;
    private int guestId;
    private String guestName;
    private Calendar checkInDate;
    private Calendar checkOutDate;
    private double totalPrice;
    private String status;
    private String notes;

    public BookingInfo(int id, int propertyId, int guestId, String guestName,
                       Calendar checkInDate, Calendar checkOutDate, double totalPrice,
                       String status, String notes) {
        this.id = id;
        this.propertyId = propertyId;
        this.guestId = guestId;
        this.guestName = guestName;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
        this.notes = notes;
    }

    // Getters
    public int getId() { return id; }
    public int getPropertyId() { return propertyId; }
    public int getGuestId() { return guestId; }
    public String getGuestName() { return guestName; }
    public Calendar getCheckInDate() { return checkInDate; }
    public Calendar getCheckOutDate() { return checkOutDate; }
    public double getTotalPrice() { return totalPrice; }
    public String getStatus() { return status; }
    public String getNotes() { return notes; }

    // Setters
    public void setStatus(String status) { this.status = status; }
    public void setNotes(String notes) { this.notes = notes; }
}