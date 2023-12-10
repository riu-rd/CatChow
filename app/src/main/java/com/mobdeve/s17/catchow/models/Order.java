package com.mobdeve.s17.catchow.models;

public class Order {
    private String name;
    private Double price;
    private Double quantity;

    public Order(String name, Double price, Double quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Double getQuantity() {
        return quantity;
    }
}
