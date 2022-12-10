package edu.northeastern.fall22_team34.carpool.models;

import java.util.List;

public class Ride {

    public String id;
    public User driver;
    public List<User> passenger;
    public double startLat;
    public double startLong;
    public double endLat;
    public double endLong;
    public String duration;
    public String time;

    public Ride() {

    }

    public Ride(String id, User driver, double startLat, double startLong, double endLat, double endLong, String duration,
                String time) {
        this.id = id;
        this.driver = driver;
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.duration = duration;
        this.time = time;
    }
}
