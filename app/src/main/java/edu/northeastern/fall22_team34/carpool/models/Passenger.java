package edu.northeastern.fall22_team34.carpool.models;

public class Passenger implements java.io.Serializable {

    public User user;
    public String phoneNumber;

    public Passenger() {

    }

    public Passenger(User user, String phoneNumber) {
        this.user = user;
        this.phoneNumber = phoneNumber;
    }
}
