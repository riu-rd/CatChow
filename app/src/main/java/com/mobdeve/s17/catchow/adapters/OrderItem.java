package com.mobdeve.s17.catchow.adapters;

public class OrderItem {
    private String name;
    private float price;
    private int quantity;

    public OrderItem(String name, float price, int quantity) {
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public float getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }
}
