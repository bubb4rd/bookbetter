package com.example.cse360_project1.models;

public class Order {
    private String date;
    private String title;
    private int quantity;
    private double price;

    public Order(String date, String title, int quantity, double price) {
        this.date = date;
        this.title = title;
        this.quantity = quantity;
        this.price = price;
    }

    // Getters
    public String getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
