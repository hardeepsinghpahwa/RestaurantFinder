package com.example.restaurantfinder;

public class Search {
    String cuisine,posi,vegnonveg,whichtype;



    public Search() {
    }

    public String getCuisine() {
        return cuisine;
    }

    public void setCuisine(String cuisine) {
        this.cuisine = cuisine;
    }

    public String getPosi() {
        return posi;
    }

    public void setPosi(String posi) {
        this.posi = posi;
    }

    public String getVegnonveg() {
        return vegnonveg;
    }

    public void setVegnonveg(String vegnonveg) {
        this.vegnonveg = vegnonveg;
    }

    public String getWhichtype() {
        return whichtype;
    }

    public void setWhichtype(String whichtype) {
        this.whichtype = whichtype;
    }

    public Search(String cuisine, String posi, String vegnonveg, String whichtype) {
        this.cuisine = cuisine;
        this.posi = posi;
        this.vegnonveg = vegnonveg;
        this.whichtype = whichtype;
    }
}
