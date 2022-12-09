package edu.northeastern.fall22_team34.carpool.models;

public class Passenger implements java.io.Serializable {

    public String phoneNumber;
    public String prefName;

    public Passenger() {

    }

    public Passenger(String phoneNumber, String prefName) {
        this.phoneNumber = phoneNumber;
        this.prefName = prefName;
    }
}
