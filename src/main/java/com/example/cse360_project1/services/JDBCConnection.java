package com.example.cse360_project1.services;

import com.example.cse360_project1.*;
import com.example.cse360_project1.controllers.SceneController;
import com.example.cse360_project1.controllers.UserSettingsPage;
import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.User;
import javafx.scene.image.Image;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.DriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCConnection {
    Connection connection;
    ResultSet result;
    Exception error;
    public JDBCConnection() {

    }
    private Connection getConnection() throws SQLException {
        this.connection =  DriverManager.getConnection("jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user", "admin", "!!mqsqlhubbard2024");
        return connection;
    }
    public ResultSet fetchQuery(String query) throws SQLException {
        try {
            this.connection =  DriverManager.getConnection("jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user", "admin", "!!mqsqlhubbard2024");
            Statement statement = connection.createStatement();
            this.result = statement.executeQuery(query);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connection Started");

        return null;
    }
    public int updateQuery(String query) throws SQLException {
        try {
            this.connection =  DriverManager.getConnection("jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user", "admin", "!!mqsqlhubbard2024");

            Statement statement = connection.createStatement();
            int update = statement.executeUpdate(query);
            return update;
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Connection Started");

        return -1;
    }
    public User logInReturnUser(String username, String password) {
        try {
            this.result = fetchQuery("SELECT * FROM users WHERE username = '" + username + "'");

            if (result.next()) {
                String pass = result.getString("password");
                if (password.equals(pass)) {
                    int id = result.getInt("id");
                    String type = result.getString("type");


                    User user = new User(id, username, type, password);
                    System.out.println(user.toString());
                    return user;
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public User registerUser(String username, String password, String type) {
        try {
            ResultSet getUserIds = fetchQuery("SELECT * FROM users");
             int newUserId = 0;
            while (getUserIds.next()) {
                if (newUserId == 0) {
                    newUserId = getUserIds.getInt("id");
                } else {
                    newUserId++;
                }
                System.out.println(newUserId);
            }
            int updateResult = updateQuery("INSERT INTO users (id, username, password, type) VALUES ('" + (newUserId + 1) + "', '" + username + "', '" + password + "', '" + type + "')");
            User newUser = new User(newUserId, username, type, password);
            return newUser;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean uploadImage(File image, int id)  {


        if (image != null) {
            try {
                try (Connection currentConnection = getConnection()) {
                    FileInputStream inputStream = new FileInputStream(image);
                    String query = "UPDATE books SET book_image = ? WHERE book_id = ?";

                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setBinaryStream(1, inputStream, (int) image.length());
                    preparedStatement.setInt(2, id);
                    int rowsInserted = preparedStatement.executeUpdate();
                    if (rowsInserted > 0) return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    public boolean bookCollectionExists(Book book) {
        try (Connection newConnection = getConnection()) {
            String checkCollection = "SELECT * FROM book_collections WHERE collection_id = " + book.getCollectionID();
            this.result = fetchQuery(checkCollection);
            if (result.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    };
    public boolean addBook(Book book) {
        int results = -1;
        try {
            // Check if user has a book collection associated with their ID else create one
            if (!bookCollectionExists(book)) {
                int userId = book.getCollectionID();
                int insertResult = updateQuery("INSERT INTO book_collections (collection_id, user_id) VALUES (" + userId + ", " + userId + ")");
                if (insertResult <= 0){
                    System.out.println("Failed to create a book collection.");
                    return false;
                }
            }
            if (book.getImage() == null) results = updateQuery("INSERT INTO books (collection_id, book_author, book_name, book_condition, book_categories) VALUES ('" + book.getCollectionID() + "', '" + book.getAuthor() + "', " + book.getCondition() + ", '" + book.getCategories() + "')");
            else {
                String query = "INSERT INTO books (collection_id, book_author, book_name, book_condition, book_categories, book_image) VALUES (?, ?, ?, ?, CAST(? AS JSON), ?)";
                try (Connection currentConnection = getConnection()) {

                    FileInputStream inputStream = new FileInputStream(book.getImage());
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, book.getCollectionID());
                    preparedStatement.setString(2, book.getAuthor());
                    preparedStatement.setString(3, book.getName());
                    preparedStatement.setString(4, book.getCondition());
                    preparedStatement.setString(5, book.categoriesToJSON(book.getCategories()));
                    preparedStatement.setBinaryStream(6, inputStream, (int) book.getImage().length());
                    System.out.println(preparedStatement);
                    int newRowsInserted = preparedStatement.executeUpdate();
                    if (newRowsInserted > 0) return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public Book getBook(int id) {
        try {
            this.result = fetchQuery("SELECT * FROM books WHERE id = " + id);
            if (result.next()) {
                int book_id = result.getInt("book_id");
                int collection_id = result.getInt("collection_id");
                String book_name = result.getString("book_name");
                String book_author = result.getString("book_author");
                String book_condition = result.getString("book_condition");
                String categories = result.getString("book_categories");
                Book book = new Book(book_id, book_name, book_author, book_condition, categories, collection_id);
                return book;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> books = new ArrayList<>();
        try {
            this.result = fetchQuery("SELECT * FROM books;");
            while (result.next()) {
                int book_id = result.getInt("book_id");
                int collection_id = result.getInt("collection_id");
                String book_name = result.getString("book_name");
                String book_author = result.getString("book_author");
                String book_condition = result.getString("book_condition");
                String categories = result.getString("book_categories");
                Book book = new Book(book_id, book_name, book_author, book_condition, categories, collection_id);
                books.add(book);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return books;
    }
    public ArrayList<Book> getBookCollection(User user) {
        ArrayList<Book> books = new ArrayList<>();
        try {
            this.result = fetchQuery("SELECT * FROM users u JOIN book_collections bc ON u.id = bc.user_id JOIN  books b ON bc.collection_id = b.collection_id WHERE u.id ='" + user.getId() + "';");
            if (result.next()) {
                int book_id = result.getInt("book_id");
                int collection_id = result.getInt("collection_id");
                String book_name = result.getString("book_name");
                String book_author = result.getString("book_author");
                String book_condition = result.getString("book_condition");
                String categories = result.getString("categories");
                Book book = new Book(book_id, book_name, book_author, book_condition, categories, collection_id);
                books.add(book);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return books;
    }
}
