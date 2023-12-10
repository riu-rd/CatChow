package com.mobdeve.s17.catchow.models;

public class Address {

    private String label;
    private String region;
    private String postalcode;
    private String street;

    public Address(String label, String region, String postalcode, String street) {
        this.label = label;
        this.region = region;
        this.postalcode = postalcode;
        this.street = street;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getPostalCode() {
        return postalcode;
    }

    public void setPostalCode(String postalcode) {
        this.postalcode = postalcode;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }
}


