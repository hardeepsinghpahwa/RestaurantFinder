package com.example.restaurantfinder;

public class Review {

    String rating,review,userid,date;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Review(String rating, String review, String userid, String date) {
        this.rating = rating;
        this.review = review;
        this.userid = userid;
        this.date = date;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Review() {
    }
}

