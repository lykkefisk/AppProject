package com.janesbrain.cartracker.model;

import android.net.Uri;

import java.io.Serializable;

public class ParkingData implements Serializable {

    private double latitude;
    private double longitude;

    public void SetAutoLocation(double lat, double lon){
        this.latitude = lat;
        this.longitude = lon;
    }

    public Uri GetUrlData(){

        return Uri.parse("geo:" + String.valueOf(latitude) + "," + String.valueOf(longitude) + "?z=10");
    }
}
