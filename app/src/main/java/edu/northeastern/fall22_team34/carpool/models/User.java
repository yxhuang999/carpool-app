package edu.northeastern.fall22_team34.carpool.models;

import android.location.Location;


public class User {

    public String username;
    public double currLat;
    public double currLong;


    public User() {

    }

    public User(String username, Location currLocation) {
        this.username = username;
        this.currLat = currLocation.getLatitude();
        this.currLong = currLocation.getLongitude();
    }
}
