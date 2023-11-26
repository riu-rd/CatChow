package com.mobdeve.s17.catchow.models;

public class Rating {
    private String name;
    private long taste;
    private long packaging;
    private long price;
    private String review;

    public Rating() {
        // Default constructor
    }

    public Rating(String name, long taste, long packaging, long price, String review) {
        this.name = name;
        this.taste = taste;
        this.packaging = packaging;
        this.price = price;
        this.review = review;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTaste() {
        return taste;
    }

    public void setTaste(long taste) {
        this.taste = taste;
    }

    public long getPackaging() {
        return packaging;
    }

    public void setPackaging(long packaging) {
        this.packaging = packaging;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
