package com.mobdeve.s17.catchow.models;

import java.util.ArrayList;

public class Restaurant {
    private String imageurl;
    private String name;
    private String type;
    private String level;
    private Double minimum;
    private Double fee;
    private String distance;
    private String duration;
    private ArrayList<Food> menu;
    private ArrayList<Rating> ratings;

    public Restaurant() {
        // Default constructor
    }

    public Restaurant(String imageurl, String name, String type, String level, Double minimum, Double fee, String distance, String duration, ArrayList<Food> menu, ArrayList<Rating> ratings) {
        this.imageurl = imageurl;
        this.name = name;
        this.type = type;
        this.level = level;
        this.minimum = minimum;
        this.fee = fee;
        this.distance = distance;
        this.duration = duration;
        this.menu = menu;
        this.ratings = ratings;
    }


    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public Double getMinimum() {
        return minimum;
    }

    public void setMinimum(Double minimum) {
        this.minimum = minimum;
    }

    public Double getFee() {
        return fee;
    }

    public void setFee(Double fee) {
        this.fee = fee;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public ArrayList<Food> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<Food> menu) {
        this.menu = menu;
    }

    public ArrayList<Rating> getRatings() {
        return ratings;
    }

    public void setRatings(ArrayList<Rating> ratings) {
        this.ratings = ratings;
    }
}
