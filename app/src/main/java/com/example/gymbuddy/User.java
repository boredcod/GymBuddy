package com.example.gymbuddy;

import java.util.ArrayList;

public class User {
    private String name;
    private String uID;
    private String location;
    private String gym;
    private String email;
    private ArrayList<String> friendlist;

    public User(String name, String uID, String location, String workoutType, String email, ArrayList<String> friendlist) {
        this.name = name;
        this.uID = uID;
        this.location = location;
        this.gym = workoutType;
        this.email = email;
        this.friendlist = friendlist;

    }

    public ArrayList<String> getFriendlist() {
        return friendlist;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getuID() {
        return uID;
    }

    public String getLocation() {
        return location;
    }

    public String getGym() {
        return gym;
    }

    public void setFriendlist(ArrayList<String> friendlist) {
        this.friendlist = friendlist;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setGym(String gym) {
        this.gym = gym;
    }
}
