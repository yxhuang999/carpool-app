package edu.northeastern.fall22_team34.carpool.models;

public class Driver implements java.io.Serializable {

    public String phoneNumber;
    public String license;
    public String prefName;

    public Driver() {

    }

    public Driver(String phoneNumber, String license, String prefName) {
        this.phoneNumber = phoneNumber;
        this.license = license;
        this.prefName = prefName;
    }
}
