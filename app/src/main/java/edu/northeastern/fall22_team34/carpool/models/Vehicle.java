package edu.northeastern.fall22_team34.carpool.models;

public class Vehicle implements java.io.Serializable {

    public Driver driver;
    public String plate;
    public String color;
    public int seat;

    public Vehicle() {

    }

    public Vehicle(Driver driver, String plate, String color, int seat) {
        this.driver = driver;
        this.plate = plate;
        this.color = color;
        this.seat = seat;
    }
}
