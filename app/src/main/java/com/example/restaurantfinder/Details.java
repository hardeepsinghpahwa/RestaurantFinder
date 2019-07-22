package com.example.restaurantfinder;

public class Details {

    String buisness,type;


    public Details(String buisness, String type) {
        this.buisness = buisness;
        this.type = type;
    }

    public Details() {
    }

    public String getBuisness() {
        return buisness;
    }

    public void setBuisness(String buisness) {
        this.buisness = buisness;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
