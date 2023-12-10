package com.mobdeve.s17.catchow.models;

public class HistoryM {
    private String orderId;
    private String orderName;

    public HistoryM(String orderId, String orderName) {
        this.orderId = orderId;
        this.orderName = orderName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderName() {
        return orderName;
    }

    public void setOrderName(String orderName) {
        this.orderName = orderName;
    }

    // Add other getters and setters for additional fields if needed
}
