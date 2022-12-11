package edu.northeastern.fall22_team34.carpool.models;

import java.util.ArrayList;
import java.util.List;

public class Trip implements java.io.Serializable {

    public String id;
    public User driver;
    public List<User> passenger;
    public double startLat;
    public double startLong;
    public double endLat;
    public double endLong;
    public String duration;
    public String time;
    public String date;

    public Trip() {

    }

    public Trip(String id, User driver, double startLat, double startLong, double endLat,
                double endLong, String duration, String time, String date) {
        this.id = id;
        this.driver = driver;
        this.passenger = new ArrayList<>();
        this.startLat = startLat;
        this.startLong = startLong;
        this.endLat = endLat;
        this.endLong = endLong;
        this.duration = duration;
        this.time = time;
        this.date = date;
    }
}
