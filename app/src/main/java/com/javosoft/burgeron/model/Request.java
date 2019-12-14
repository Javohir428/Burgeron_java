package com.javosoft.burgeron.model;

import java.util.List;

public class Request {
    private String phone;
    private String name;
    private String total;
    private String date;
    private String restaurantID;
    private List<Order> foods;

    public Request(){}

    public Request(String phone, String name, String total, String date, String restaurantID, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.total = total;
        this.date = date;
        this.restaurantID = restaurantID;
        this.foods = foods;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }
}
