package com.mobdeve.s17.catchow.models;

public class Rating {
    private String name;
    private double rating;
    private String review;

    public Rating(String name, double rating, String review) {
        this.name = name;
        this.rating = rating;
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public double getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }
}

