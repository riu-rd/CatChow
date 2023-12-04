package com.mobdeve.s17.catchow.models;

public class Users {

    String name, email;

    public Users(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Users() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
