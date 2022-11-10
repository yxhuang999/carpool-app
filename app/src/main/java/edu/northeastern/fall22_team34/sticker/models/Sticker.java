package edu.northeastern.fall22_team34.sticker.models;

import android.net.Uri;

public class Sticker {

    public String imageUri;
    public String name;
    public String sender;
    public String recipient;
    public String timeSent;

    public Sticker() {

    }

    public Sticker(String imageUri, String name, String sender, String recipient, String timeSent) {
        this.imageUri = imageUri;
        this.name = name;
        this.sender = sender;
        this.recipient = recipient;
        this.timeSent = timeSent;
    }
}
