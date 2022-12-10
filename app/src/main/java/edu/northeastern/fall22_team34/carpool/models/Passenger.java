package edu.northeastern.fall22_team34.carpool.models;

import java.util.ArrayList;
import java.util.List;

public class Passenger implements java.io.Serializable {

    public String phoneNumber;
    public String prefName;
    public List<Trip> trips;

    public Passenger() {

    }

    public Passenger(String phoneNumber, String prefName) {
        this.phoneNumber = phoneNumber;
        this.prefName = prefName;
        this.trips = new ArrayList<>();
    }
}
