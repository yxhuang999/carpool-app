package edu.northeastern.fall22_team34.sticker.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    public String username;
    public String REGISTRATION_TOKEN;
    public Map<String, Integer> stickerSent;
    public List<Sticker> stickerList;
    public List<Sticker> stickerReceived;

    public User() {

    }

    public User(String username, String REGISTRATION_TOKEN) {
        this.username = username;
        this.REGISTRATION_TOKEN = REGISTRATION_TOKEN;
        this.stickerSent = new HashMap<>();
        this.stickerList = new ArrayList<>();
        this.stickerReceived = new ArrayList<>();
    }
}
