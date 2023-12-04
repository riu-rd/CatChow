package com.mobdeve.s17.catchow.models;

import java.io.Serializable;

public class Address implements Serializable {

    private String labelAs;
    private String regionInfo;
    private String postalCode;
    private String streetInfo;

    public String getLabelAs() {
        return labelAs;
    }

    public void setLabelAs(String labelAs) {
        this.labelAs = labelAs;
    }

    public String getRegionInfo() {
        return regionInfo;
    }

    public void setRegionInfo(String regionInfo) {
        this.regionInfo = regionInfo;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getStreetInfo() {
        return streetInfo;
    }

    public void setStreetInfo(String streetInfo) {
        this.streetInfo = streetInfo;
    }
}


