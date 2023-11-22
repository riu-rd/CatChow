package com.mobdeve.s17.catchow.models;

public class Rating {
    private String name;
    private Integer taste;
    private Integer packaging;
    private Integer price;
    private String review;

    public Rating() {
        // Default constructor
    }

    public Rating(String name, Integer taste, Integer packaging, Integer price, String review) {
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

    public Integer getTaste() {
        return taste;
    }

    public void setTaste(Integer taste) {
        this.taste = taste;
    }

    public Integer getPackaging() {
        return packaging;
    }

    public void setPackaging(Integer packaging) {
        this.packaging = packaging;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
}
