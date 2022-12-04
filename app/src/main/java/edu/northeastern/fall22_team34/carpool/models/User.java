package edu.northeastern.fall22_team34.carpool.models;

import android.location.Location;

import com.google.firebase.firestore.GeoPoint;

public class User {

    public String username;
    public GeoPoint currLocation;


    public User() {

    }

    public User(String username, Location currLocation) {
        this.username = username;
        this.currLocation = new GeoPoint(currLocation.getLatitude(), currLocation.getLongitude());
    }
}
