package com.example.gymbuddy;

import java.util.ArrayList;
import java.util.Map;

public class User {
    private String name;
    private String uID;
    private String location;
    private String gym;
    private String email;
    private ArrayList<String> friendlist;
    private ArrayList<String> pendingRequests;
    private ArrayList<String> pendingInvites;
    private String description;

    public User(String name, String uID, String location, String workoutType, String email, ArrayList<String> friendlist,
               ArrayList<String> pendingRequests,ArrayList<String> pendingInvites, String description) {
        this.name = name;
        this.uID = uID;
        this.location = location;
        this.gym = workoutType;
        this.email = email;
        this.friendlist = friendlist;
        this.pendingInvites = pendingInvites;
        this.pendingRequests = pendingRequests;
        this.description = description;
    }

    public String getDescription() {
        return description;
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

    public ArrayList<String> getPendingInvites() {
        return pendingInvites;
    }

    public ArrayList<String> getPendingRequests() {
        return pendingRequests;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public void setPendingInvites(ArrayList<String> pendingInvites) {
        this.pendingInvites = pendingInvites;
    }

    public void setPendingRequests(ArrayList<String> pendingRequests) {
        this.pendingRequests = pendingRequests;
    }
}
