package com.example.cse360_project1.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {
    private int id;
    private String name;
    private String userType;
    private String password;
    private ArrayList<Book> books;
    public User(String name) {
        this.name = name;
        this.id = 0;
        this.userType = "SELLER";
        this.password = "";
        this.books = new ArrayList<>();
    }
    public User(int id, String name, String userType, String password) {
        this.id = id;
        this.name = name;
        this.userType = userType;
        this.password = password;
        this.books = new ArrayList<>();
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getUserType() {
        return userType.toUpperCase();
    }
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public ArrayList<Book> getBooks() {
        return books;
    }
    public void setBooks(ArrayList<Book> books) {
        this.books = books;
    }
    public void addBook(Book book) {
        books.add(book);
    }
    public void removeBook(Book book) {
        books.remove(book);
    }
    public Map<String, Integer> getCategoriesSold() {
        Map<String, Integer> categoryCount = new HashMap<>();

        // Count occurrences of each category
        for (Book book : books) {
            for (String category : book.getCategories()) {
                categoryCount.put(category, categoryCount.getOrDefault(category, 0) + 1);
            }
        }

        return categoryCount;
    }
    public Map<String, Integer> getConditionsSold() {
        Map<String, Integer> conditionsCount = new HashMap<>();

        // Count occurrences of each category
        for (Book book : books) {
            String condition = book.getCondition();
            conditionsCount.put(condition, conditionsCount.getOrDefault(condition, 0) + 1);
        }

        return conditionsCount;
    }

    //get all users in the system database and put them into an array list of users
    public List<User> getAllUsers() throws SQLException{
        List<User> users = new ArrayList<>();
        Connection connection =  DriverManager.getConnection("jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user", "admin", "!!mqsqlhubbard2024");

        String userQuery = "SELECT * FROM users"; //query to collect all users from the users table

        try(PreparedStatement statement = connection.prepareStatement(userQuery); ResultSet rs = statement.executeQuery()) { //execute the query statement
            while(rs.next()){
                users.add(new User(rs.getInt("id"), rs.getString("username"), rs.getString("type"), rs.getString("password"))); //create a new user objects and create it from the database informatino for eah uniq user
            }
        }

        return users; //return the array list
    }

    @Override
    public String toString() {
        return id + " | " + name + " " + userType;
    }
}
