package com.mobdeve.s17.catchow.models;

public class Order {
    private String name;
    private double price;
    private int quantity;

    public Order(String name, double price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
