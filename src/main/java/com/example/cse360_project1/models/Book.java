package com.example.cse360_project1.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class Book {
    private int id;
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
        this.image = getBookImageAsFile();

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

    public boolean saveBookToDatabase() {
        final String JDBC_URL = "jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user";
        final String username = "admin";
        final String password = "!!mqsqlhubbard2024";

        String query = "INSERT INTO books (name, author, condition, categories, collectionID, price, date, book_image) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, this.name);
            statement.setString(2, this.author);
            statement.setString(3, this.condition);
            statement.setString(4, categoriesToJSON(this.categories));
            statement.setInt(5, this.collectionID);
            statement.setDouble(6, this.price);
            statement.setString(7, this.date);

            if (this.image != null) {
                statement.setBinaryStream(8, new FileInputStream(this.image));
            } else {
                statement.setNull(8, Types.BLOB); // If no image, insert null
            }

            int rowsInserted = statement.executeUpdate();
            return rowsInserted > 0; // Return true if insertion is successful

        } catch (SQLException | IOException e) {
            System.err.println("Error saving book to database: " + e.getMessage());
            return false;
        }
    }
    public static ObservableList<Book> fetchAllBooksFromDatabase() {
        ObservableList<Book> books = FXCollections.observableArrayList();
        final String JDBC_URL = "jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user";
        final String username = "admin";
        final String password = "!!mqsqlhubbard2024";

        String query = "SELECT book_id, book_name, book_author, book_condition, book_categories, collection_id FROM books";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("book_id");
                String name = resultSet.getString("book_name");
                String author = resultSet.getString("book_author");
                String condition = resultSet.getString("book_condition");
                String categoriesJSON = resultSet.getString("book_categories");
                int collectionID = resultSet.getInt("collection_id");

                books.add(new Book(id, name, author, condition, categoriesJSON, collectionID));
            }

        } catch (SQLException e) {
            System.err.println("Error fetching books from database: " + e.getMessage());
        }

        return books;
    }


    public File getImage() {
        if (image == null) {
            image = getBookImageAsFile();
        }
        return image;
    }

    public void setImage(File image) {
        this.image = image;
    }
    public File getBookImageAsFile() {
        System.out.println("Fetching image for book ID: " + this.id);

        final String JDBC_URL = "jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user";
        final String username = "admin";
        final String password = "!!mqsqlhubbard2024";

        String query = "SELECT book_image FROM books WHERE book_id = ?";

        try (Connection connection = DriverManager.getConnection(JDBC_URL, username, password);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, this.id); // Use the current book's ID

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                InputStream inputStream = resultSet.getBinaryStream("book_image");
                System.out.println("Image data found for book ID: " + this.id);

                if (inputStream != null) {
                    File tempFile = File.createTempFile("book_image_" + this.id, ".jpg");
                    try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    System.out.println("Image written to temp file: " + tempFile.getAbsolutePath());
                    return tempFile;
                } else {
                    System.out.println("No binary data found for book ID: " + this.id);
                }
            } else {
                System.out.println("No result found for book ID: " + this.id);
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error retrieving image for book ID " + this.id + ": " + e.getMessage());
        }

        return null;
    }


    @Override
    public String toString() {
        return name + ", $" + price + " " + author + " " + condition + " " + stringCategories(categories);
    }

}
