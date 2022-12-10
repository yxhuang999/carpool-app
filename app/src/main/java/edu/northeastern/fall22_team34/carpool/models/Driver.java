package edu.northeastern.fall22_team34.carpool.models;

import java.util.ArrayList;
import java.util.List;

public class Driver implements java.io.Serializable {

    public String phoneNumber;
    public String license;
    public String prefName;
    public List<Trip> trips;

    public Driver() {

    }

    public Driver(String phoneNumber, String license, String prefName) {
        this.phoneNumber = phoneNumber;
        this.license = license;
        this.prefName = prefName;
        this.trips = new ArrayList<>();
    }
}
