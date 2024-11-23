package com.example.cse360_project1.services;

import com.example.cse360_project1.*;
import com.example.cse360_project1.controllers.SceneController;
import com.example.cse360_project1.controllers.UserSettingsPage;
import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.Transaction;
import com.example.cse360_project1.models.User;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;

import java.io.*;
import java.sql.DriverManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JDBCConnection {
    Connection connection;
    ResultSet result;
    Exception error;
    private static SimpleCache cacheManager = SimpleCache.getInstance();
    static final String ALL_BOOKS_CACHE_KEY = "all_books";

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

        String fetchIDQuery = "SELECT id FROM users ORDER BY id";
        int newUserID = 1;
        Connection newConnection;
        try {
            newConnection = getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try (PreparedStatement fetchIDStatement = newConnection.prepareStatement(fetchIDQuery)) {
            ResultSet newResult = fetchIDStatement.executeQuery();

            while(newResult.next()){
                int initialID = newResult.getInt("id");

                if(initialID != newUserID){
                    break;
                }

                newUserID++;
            }

            String insertUserQuery = "INSERT INTO users (id, username, password, type) VALUES (?, ?, ?, ?)";

            try (PreparedStatement insertUserStatement = connection.prepareStatement(insertUserQuery)) {
                insertUserStatement.setInt(1, newUserID);
                insertUserStatement.setString(2, username);
                insertUserStatement.setString(3, password);
                insertUserStatement.setString(4, type);
                insertUserStatement.executeUpdate();
            }

            User newUser = new User(newUserID, username, type, password);
            return newUser;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

        /*
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
        */
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

            // Handle case where no collection_id exists
            int collectionId;
            if (collectionIds.isEmpty()) {
                System.out.println("No collection_id found for user_id: " + book.getCollectionID());
                // Create a new collection_id for the user
                String createQuery = "INSERT INTO book_collections (user_id) VALUES (?)";
                PreparedStatement createStatement = currentConnection.prepareStatement(createQuery, Statement.RETURN_GENERATED_KEYS);
                createStatement.setInt(1, book.getCollectionID());
                createStatement.executeUpdate();

                // Retrieve the generated collection_id
                ResultSet generatedKeys = createStatement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    collectionId = generatedKeys.getInt(1);
                    System.out.println("Created new collection_id: " + collectionId);
                } else {
                    System.err.println("Failed to create a new collection_id.");
                    return false;
                }
            } else {
                // If multiple collection_ids exist, delete all but the first
                collectionId = collectionIds.get(0); // Keep the first (smallest) collection_id
                for (int i = 1; i < collectionIds.size(); i++) {
                    int collectionIdToDelete = collectionIds.get(i);

                    // Check if the collection_id is referenced in the books table
                    String referenceCheckQuery = "SELECT COUNT(*) FROM books WHERE collection_id = ?";
                    PreparedStatement referenceCheckStatement = currentConnection.prepareStatement(referenceCheckQuery);
                    referenceCheckStatement.setInt(1, collectionIdToDelete);
                    ResultSet referenceResult = referenceCheckStatement.executeQuery();

                    if (referenceResult.next() && referenceResult.getInt(1) == 0) {
                        // Safe to delete if not referenced
                        String deleteQuery = "DELETE FROM book_collections WHERE collection_id = ?";
                        PreparedStatement deleteStatement = currentConnection.prepareStatement(deleteQuery);
                        deleteStatement.setInt(1, collectionIdToDelete);
                        deleteStatement.executeUpdate();
                        System.out.println("Deleted collection_id: " + collectionIdToDelete);
                    } else {
                        System.out.println("collection_id " + collectionIdToDelete + " is referenced in the books table and cannot be deleted.");
                    }
                }
            }

            // Check for a valid image file
            File imageFile = book.getImage();
            if (imageFile == null || !imageFile.exists() || !imageFile.isFile()) {
                System.err.println("Book image file is null, does not exist, or is not a valid file. Skipping image upload.");
                return false;
            }

            try (FileInputStream fileInputStream = new FileInputStream(imageFile)) {
                String query = "INSERT INTO books (collection_id, book_author, book_name, book_condition, book_categories, book_image, date, calculated_price) " +
                        "VALUES (?, ?, ?, ?, CAST(? AS JSON), ?, ?, ?)";
                PreparedStatement preparedStatement = currentConnection.prepareStatement(query);
                preparedStatement.setInt(1, collectionId);
                preparedStatement.setString(2, book.getAuthor());
                preparedStatement.setString(3, book.getName());
                preparedStatement.setString(4, book.getCondition());
                preparedStatement.setString(5, book.categoriesToJSON(book.getCategories()));
                preparedStatement.setBinaryStream(6, fileInputStream, (int) imageFile.length());
                preparedStatement.setString(7, book.getDate());
                preparedStatement.setDouble(8, book.getPrice());
                cacheManager.clear(ALL_BOOKS_CACHE_KEY);
                return preparedStatement.executeUpdate() > 0;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void invalidateCache() {
        cacheManager.clear(ALL_BOOKS_CACHE_KEY);
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
    public boolean deleteBook(int id) {
        invalidateCache();
        try {
            fetchQuery("SELECT * FROM books WHERE book_id="+id);
            if (result.next()) {
                updateQuery("DELETE FROM books WHERE id = " + id);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public ArrayList<Book> getAllBooks() {
        ArrayList<Book> cachedBooks = (ArrayList<Book>) cacheManager.get(ALL_BOOKS_CACHE_KEY);
        if (cachedBooks != null) {
            return cachedBooks;
        }
        ArrayList<Book> books = new ArrayList<>();
        try (Connection connection = getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM books");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int book_id = resultSet.getInt("book_id");
                int collection_id = resultSet.getInt("collection_id");
                String book_name = resultSet.getString("book_name");
                String book_author = resultSet.getString("book_author");
                String book_condition = resultSet.getString("book_condition");
                String categories = resultSet.getString("book_categories");
                String status = resultSet.getString("book_status");
                Book book = new Book(book_id, book_name, book_author, book_condition, categories, collection_id);
                books.add(book);
            }
            cacheManager.put(ALL_BOOKS_CACHE_KEY, books);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return books;
    }
    public static ObservableList<Book> fetchAllBooksFromDatabase() {
        ObservableList<Book> cachedBooks = (ObservableList<Book>) cacheManager.get(ALL_BOOKS_CACHE_KEY);

        if (cachedBooks != null) {
            return cachedBooks;
        }

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

            cacheManager.put(ALL_BOOKS_CACHE_KEY, books);

        } catch (SQLException e) {
            System.err.println("Error fetching books from database: " + e.getMessage());
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
    public ArrayList<Transaction> getAllTransactions(User user) {
        String cacheKey = "transactions_" + user.getId();
        ArrayList<Transaction> cachedTransactions = (ArrayList<Transaction>) cacheManager.get(cacheKey);

        if (cachedTransactions != null) {
            return cachedTransactions;
        }

        ArrayList<Transaction> transactions = new ArrayList<>();
        try(Connection connection = getConnection()) {
            String sql = "";
            if (user.getUserType().equals("ADMIN")) {
                sql = "SELECT * FROM books";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
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
            } else {
                sql = "SELECT * FROM books WHERE collection_id=?";
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                preparedStatement.setInt(1, user.getId());
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

            cacheManager.put(cacheKey, transactions);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return transactions;
    }
    public static TableView<Transaction> getTransactionTable(User user) {
        TableView<Transaction> tableView = new TableView<>();
        tableView.setStyle("fx-background-color: #fff");
        tableView.setEditable(false);
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ObservableList<Transaction> data = FXCollections.observableArrayList();

        TableColumn<Transaction, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Transaction, Integer> idColumn = new TableColumn<>("Transaction ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Transaction, String> bookNameColumn = new TableColumn<>("Book Name");
        bookNameColumn.setCellValueFactory(param -> {
            return new SimpleStringProperty(param.getValue().getBook().getName());
        });

        TableColumn<Transaction, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        TableColumn<Transaction, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        TableColumn<Transaction, Void> actionCol = new TableColumn<>("Action");

        actionCol.setCellFactory(param -> new TableCell<>() {
            private final Button actionButton = new Button("View");

            {
                actionButton.setOnAction(event -> {
                    Transaction transaction = getTableView().getItems().get(getIndex());
                    System.out.println("Processing transaction: " + transaction.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionButton);
                }
            }
        });

        tableView.getColumns().addAll(dateColumn, idColumn, bookNameColumn, statusColumn, priceColumn, actionCol);

        JDBCConnection connection = new JDBCConnection();
        ObservableList<Transaction> transactions = FXCollections.observableArrayList(
                connection.getAllTransactions(user)
        );

        tableView.setItems(transactions);
        return tableView;
    }

    public boolean updateBookStatus(int bookID, String status){
        String updateStatusQuery = "UPDATE books SET book_status = ? WHERE book_id = ?"; //query to update book status

        try  (Connection connection = getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(updateStatusQuery)){
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, bookID);

            int rowsUpdated = preparedStatement.executeUpdate();

            return rowsUpdated > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean deleteUser(int userID) {
        String deleteUserQuery = "DELETE FROM users WHERE id = ?"; //delete query for database

        try(Connection connection =  DriverManager.getConnection("jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user", "admin", "!!mqsqlhubbard2024");
            PreparedStatement deleteStatement = connection.prepareStatement((deleteUserQuery))){ //create a connection into the system and run the query

            deleteStatement.setInt(1, userID); //set the parameters of the statement, more specifically which user to delete

            int numOfRowsAffected = deleteStatement.executeUpdate(); //execute the delete and show how many rows have been affected by deleting the user (should be just 1)
            invalidateCache();

            return numOfRowsAffected > 0; //return true if at least one row has been impacted
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean changePassword(User user, String newPassword) {
        String changePasswordQuery = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection newConnection = getConnection()) {
            PreparedStatement changePasswordStatement = newConnection.prepareStatement(changePasswordQuery);
            changePasswordStatement.setString(1, newPassword);
            changePasswordStatement.setInt(2, user.getId());
            int numOfRowsAffected = changePasswordStatement.executeUpdate();
            return numOfRowsAffected > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public File fetchBookImage(int bookId) {
        final String query = "SELECT book_image FROM books WHERE book_id = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, bookId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                InputStream inputStream = resultSet.getBinaryStream("book_image");

                if (inputStream != null) {
                    File tempFile = File.createTempFile("book_image_" + bookId, ".jpg");
                    try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                    return tempFile; // Return the image file
                } else {
                    System.out.println("No binary data found for book ID: " + bookId);
                }
            } else {
                System.out.println("No result found for book ID: " + bookId);
            }

        } catch (SQLException | IOException e) {
            System.err.println("Error retrieving image for book ID " + bookId + ": " + e.getMessage());
        }

        return null; // Return null if no image is found or on error
    }
}
