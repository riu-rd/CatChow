package com.mobdeve.s17.catchow.models;
import java.io.Serializable;

public class CartItem {
    private String productName;
    private double productOriginalPrice;
    private int productQuantity;

    public CartItem(String productName, double productOriginalPrice, int productQuantity) {
        this.productName = productName;
        this.productOriginalPrice = productOriginalPrice;
        this.productQuantity = productQuantity;
    }

    public String getName() {
        return productName;
    }

    public double getPrice() {
        return productOriginalPrice;
    }

    public int getQuantity() {
        return productQuantity;
    }
}
