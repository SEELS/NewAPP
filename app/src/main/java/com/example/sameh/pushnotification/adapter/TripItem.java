package com.example.sameh.pushnotification.adapter;

import android.graphics.Bitmap;

/**
 * Created by sameh on 6/22/2018.
 */

public class TripItem {
    private Bitmap Image;
    private String Date;
    private String destination;
    private float rate;
    private String TruckId;


    public Bitmap getImage() {
        return Image;
    }

    public void setImage(Bitmap image) {
        Image = image;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public float getRate() {
        return rate;
    }

    public void setRate(float rate) {
        this.rate = rate;
    }

    public String getTruckId() {
        return TruckId;
    }

    public void setTruckId(String truckId) {
        TruckId = truckId;
    }
}
