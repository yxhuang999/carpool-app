package edu.northeastern.fall22_team34.carpool.models;

public class Driver implements java.io.Serializable {

    public String phoneNumber;
    public String license;

    public Driver() {

    }

    public Driver(String phoneNumber, String license) {
        this.phoneNumber = phoneNumber;
        this.license = license;
    }
}
