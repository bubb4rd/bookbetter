package com.example.cse360_project1.services;

import com.example.cse360_project1.*;
import com.example.cse360_project1.controllers.SceneController;
import com.example.cse360_project1.controllers.UserSettingsPage;
import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.Transaction;
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
    public boolean bookCollectionExists(int id) {
        try (Connection newConnection = getConnection()) {
            String checkCollection = "SELECT * FROM book_collections WHERE user_id = " + id;
            this.result = fetchQuery(checkCollection);
            if (result.next()) return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    };
    public boolean addBook(Book book) {
            try (Connection currentConnection = getConnection()) {
                // Check for multiple collection_ids for the user
                String checkQuery = "SELECT collection_id FROM book_collections WHERE user_id = ? ORDER BY collection_id ASC";
                PreparedStatement checkStatement = currentConnection.prepareStatement(checkQuery);
                checkStatement.setInt(1, book.getCollectionID()); // Assuming book.getCollectionID() returns the user_id
                ResultSet rs = checkStatement.executeQuery();

                List<Integer> collectionIds = new ArrayList<>();
                while (rs.next()) {
                    collectionIds.add(rs.getInt("collection_id"));
                }

                    FileInputStream inputStream = new FileInputStream(book.getImage());
                    PreparedStatement preparedStatement = connection.prepareStatement(query);
                    preparedStatement.setInt(1, book.getCollectionID());
                    preparedStatement.setString(2, book.getAuthor());
                    preparedStatement.setString(3, book.getName());
                    preparedStatement.setString(4, book.getCondition());
                    preparedStatement.setString(5, book.categoriesToJSON(book.getCategories()));
                    preparedStatement.setBinaryStream(6, inputStream, (int) book.getImage().length());
                    preparedStatement.setString(7, book.getDate());
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
    public User getUser(String username) throws SQLException {
        fetchQuery("SELECT * FROM users WHERE username=" + username);
        if (result.next()) {
            String pass = result.getString("password");
            int id = result.getInt("id");
            String type = result.getString("type");
            return new User(id, username, type, pass);
        }
        return null;
    }
    public User getUser(int id) throws SQLException {
        fetchQuery("SELECT * FROM users WHERE id=" + id);
        if (result.next()) {
            String pass = result.getString("password");
            String username = result.getString("username");
            String type = result.getString("type");
            return new User(id, username, type, pass);
        }
        return null;
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
                String categories = result.getString("book_categories");
                Book book = new Book(book_id, book_name, book_author, book_condition, categories, collection_id);
                books.add(book);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return books;
    }
    public ArrayList<Transaction> getAllTransactions(User user)  {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try(Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books WHERE collection_id=" + user.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int book_id = resultSet.getInt("book_id");
                int collection_id = resultSet.getInt("collection_id");
                String book_name = resultSet.getString("book_name");
                String book_author = resultSet.getString("book_author");
                String book_condition = resultSet.getString("book_condition");
                String categories = resultSet.getString("book_categories");
                String status = resultSet.getString("book_status");

                int buyer_id = resultSet.getInt("buyer_id");
                String date = resultSet.getString("date");
                Book book = new Book(book_id, book_name, book_author, book_condition, categories, collection_id);
                transactions.add(new Transaction(book_id, user, getUser(buyer_id), date, book, status));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;

    }
}
