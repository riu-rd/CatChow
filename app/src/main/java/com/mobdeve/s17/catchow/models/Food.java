package com.mobdeve.s17.catchow.models;

public class Food {
    private String imageurl;
    private String name;
    private Double price;

    public Food() {
        // Default constructor
    }

    public Food(String imageurl, String name, Double price) {
        this.imageurl = imageurl;
        this.name = name;
        this.price = price;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
}
