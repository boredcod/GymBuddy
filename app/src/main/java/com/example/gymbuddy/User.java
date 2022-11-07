package com.example.gymbuddy;

public class User {
    private String name;
    private String uID;
    private String location;
    private String gym;


    public User(String name, String uID, String location, String workoutType) {
        this.name = name;
        this.uID = uID;
        this.location = location;
        this.gym = workoutType;

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
