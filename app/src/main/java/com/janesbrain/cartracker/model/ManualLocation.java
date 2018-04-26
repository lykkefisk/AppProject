package com.janesbrain.cartracker.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity //everytime we get get the users, we will pull it from the room database
public class ManualLocation {
    @PrimaryKey(autoGenerate = true) //everytime you add a new record it will generete and add it in the room database
    private int id;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "address_no")
    private String addressNo;

    @ColumnInfo(name = "zip_code")
    private String zipCode;

    @ColumnInfo(name = "city")
    private String city;

    @ColumnInfo(name = "country")
    private String country;

    @ColumnInfo(name = "time_stamp")
    private String timeStamp;

    //Constructor
    public ManualLocation(String address, String addressNo, String zipCode, String city, String country, String timeStamp) {
        this.address = address;
        this.addressNo = addressNo;
        this.zipCode = zipCode;
        this.city = city;
        this.country = country;
        this.timeStamp = timeStamp;
    }

    public int getId() { return id; }

    public void   setId(int id) { this.id = id; }


    public String getAddress() { return address; }

    public void setAddress(String address) { this.address = address; }


    public String getAddressNo() { return addressNo; }

    public void setAddressNo(String addressNo) { this.addressNo = addressNo; }


    public String getZipCode() { return zipCode; }

    public void setZipCode(String zipCode) { this.zipCode = zipCode; }


    public String getCity() { return city; }

    public void setCity(String city) { this.city = city; }


    public String getCountry() { return country; }

    public void setCountry(String country) { this.country = country; }


    public String getTimeStamp() { return timeStamp; }

    public void setTimeStamp(String timeStamp) { this.timeStamp = timeStamp; }
}

