package com.example.cse360_project1.models;

import java.util.Date;

public class Transaction {
    private int id;
    private User seller;
    private User buyer;
    private Book book;
    private String status;
    private String date;
    private double price;
    public Transaction(Book book, User seller) {
        this.book = book;
        this.id = book.getId();
        this.seller = seller;
        this.buyer = null;
        this.status = "PENDING";
        this.price = book.getPrice();
        this.date = book.getDate();
    }

    public Transaction(int id, User seller, String date, Book book) {

        this.id = id;
        this.seller = seller;
        this.buyer = null;
        this.date = date;
        this.book = book;
        this.status = "PENDING";
        this.price = book.getPrice();

    }
    public Transaction(int id, User seller, User buyer, String date, Book book, String status) {
        this.id = id;
        this.seller = seller;
        this.buyer = buyer;
        this.date = date;
        this.book = book;
        this.status = status;
        this.price = book.getPrice();
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public User getSeller() {
        return seller;
    }
    public void setSeller(User seller) {
        this.seller = seller;
    }
    public User getBuyer() {
        return buyer;
    }
    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public Book getBook() {
        return book;
    }
    public void setBook(Book book) {
        this.book = book;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

}
