package com.janesbrain.cartracker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity //everytime we get get the users, we will pull it from the room database
public class AutoLocation {

    @PrimaryKey(autoGenerate = true) //everytime you add a new record it will generete and add it in the room database
    private int id;

    @ColumnInfo(name = "addressLine")
    private String addressLine;

    @ColumnInfo(name = "latitude")
    private Double latitude;

    @ColumnInfo(name = "longitude")
    private Double longitude;

    @ColumnInfo(name = "time_stamp")
    private String timeStamp;

    //Constructor
    public AutoLocation(String addressLine, Double latitude, Double longitude, String timeStamp) {
        this.addressLine = addressLine;
        this.latitude = latitude;
        this.longitude = longitude;
        this.timeStamp = timeStamp;
    }

    public int getId() { return id; }

    public void   setId(int id) { this.id = id; }


    public String getAddressLine() { return addressLine; }

    public void  setAddressLine(String addressLine) { this.addressLine = addressLine; }


    public Double getLatitude() { return latitude; }

    public void setLatitude(Double latitude) { this.latitude = latitude; }


    public Double getLongitude() { return longitude; }

    public void setLongitude(Double longitude) { this.longitude = longitude; }


    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }
}

