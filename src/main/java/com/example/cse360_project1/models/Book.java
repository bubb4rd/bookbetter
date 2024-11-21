package com.example.cse360_project1.models;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;

public class Book {
    private int id;
    private int buyer_id;
    private String name;
    private String author;
    private String condition;
    private ArrayList<String> categories;
    private int collectionID;
    private double price;
    private File image;
    private String date;

    public Book(int id, String name, String author, String condition, String categoiesJSON, int collectionID) {
        LocalDate today = LocalDate.now();
        this.date = today.toString();
        this.id = id;
        this.name = name;
        this.author = author;
        this.condition = condition;
        this.categories = parseJSON(categoiesJSON);
        this.collectionID = collectionID;
        this.image = null;
        this.buyer_id = -1;
        switch (condition) {
            case "New" -> this.price = 10.50 * 2;
            case "Used" -> this.price = 10.50 * 1.5;
            case "Heavily Used" -> this.price = 10.50;
        }
    }
    public Book(String name, String author, String condition, String categoiesJSON, int collectionID, File image) {
        LocalDate today = LocalDate.now();
        this.date = today.toString();
        this.id = id;
        this.name = name;
        this.author = author;
        this.condition = condition;
        this.categories = parseJSON(categoiesJSON);
        this.collectionID = collectionID;
        this.image = image;
        this.buyer_id = -1;
        switch (condition) {
            case "New" -> this.price = 10.50 * 2;
            case "Used" -> this.price = 10.50 * 1.5;
            case "Heavily Used" -> this.price = 10.50;
        }
    }
    public Book(int id, String name, String author, String condition, String categoiesJSON, int collectionID, File image) {
        this.id = id;
        this.name = name;
        this.author = author;
        this.condition = condition;
        this.categories = parseJSON(categoiesJSON);
        this.collectionID = collectionID;
        this.image = image;
        LocalDate today = LocalDate.now();
        this.date = today.toString();
        this.buyer_id = -1;
        switch (condition) {
            case "New" -> this.price = 10.50 * 2;
            case "Used" -> this.price = 10.50 * 1.5;
            case "Heavily Used" -> this.price = 10.50;
        }
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getAuthor() {
        return author;
    }
    public void setAuthor(String author) {
        this.author = author;
    }
    public String getCondition() {
        return condition;
    }
    public void setCondition(String condition) {
        this.condition = condition;
    }
    public ArrayList<String> getCategories() {
        return categories;
    }
    public void setCategories(ArrayList<String> categories) {
        this.categories = categories;
    }
    public void addCategory(String category) {
        categories.add(category);
    }
    public void removeCategory(String category) {
        categories.remove(category);
    }
    public String stringCategories(ArrayList<String> categories) {
        String categoriesString = "Categories: ";
        for (String category : categories) {
            categoriesString += category + ", ";
        }
        if (categoriesString.equals("Categories: ")) categoriesString = "Categories: None";
        return categoriesString;
    }

    public int getCollectionID() {
        return collectionID;
    }
    public void setCollectionID(int collectionID) {
        this.collectionID = collectionID;
    }
    public int getBuyer_id() {
        return buyer_id;
    }
    public void setBuyer_id(int buyer_id) {
        this.buyer_id = buyer_id;
    }

    public ArrayList<String> parseJSON(String json) {

        String trimmed = json.substring(1, json.length() - 1);
        String[] items = trimmed.split(",\\s*");
        if (items.length == 0) return new ArrayList<>();

        ArrayList<String> list = new ArrayList<>();

        for (String item : items) {
            item = item.trim().replaceAll("^\"|\"$", "");  // Remove surrounding double quotes

            if (!item.isEmpty()) {
                list.add(item);
            }
        }

        return list;
    }
    public String categoriesToJSON(ArrayList<String> categories) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");

        for (int i = 0; i < categories.size(); i++) {
            jsonBuilder.append("\"").append(categories.get(i)).append("\"");
            if (i < categories.size() - 1) {
                jsonBuilder.append(", ");
            }
        }

        jsonBuilder.append("]");
        return jsonBuilder.toString();
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }

    public File getImage() {
        return image;
    }
    public void setImage(File image) {
        this.image = image;
    }
    @Override
    public String toString() {
        return name + ", $" + price + " " + author + " " + condition + " " + stringCategories(categories);
    }

}
