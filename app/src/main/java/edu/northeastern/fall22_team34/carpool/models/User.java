package edu.northeastern.fall22_team34.carpool.models;

import android.location.Location;

import java.util.ArrayList;
import java.util.List;


public class User implements java.io.Serializable {

    public String username;
    public double currLat;
    public double currLong;
    public Driver driverProfile;
    public Passenger passengerProfile;


    public User() {

    }

    public User(String username, Location currLocation) {
        this.username = username;
        this.currLat = currLocation.getLatitude();
        this.currLong = currLocation.getLongitude();
    }
}
