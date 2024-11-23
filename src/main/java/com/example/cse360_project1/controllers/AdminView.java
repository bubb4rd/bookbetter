package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.Transaction;
import com.example.cse360_project1.models.User;
import com.example.cse360_project1.services.JDBCConnection;
import com.example.cse360_project1.services.SimpleCache;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.event.TableModelListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AdminView {
    private final User user;
    private final SceneController sceneController;
    private String tab;
    private static final SimpleCache cacheManager = SimpleCache.getInstance();
    private TableView<User> allUsersTable; //create a private users table
    private TableView<Book> allBooksTable; //create a private books table
    private TableView<Book> pendingBooksTable;
    private static final String USERS_CACHE_KEY = "admin_users";
    private static final String BOOKS_CACHE_KEY = "admin_books";
    private static final String TRANSACTIONS_CACHE_KEY = "admin_transactions";
    public AdminView(User user, SceneController sceneController) {
        this.user = user;
        this.sceneController = sceneController;
        this.tab = "DASHBOARD";
    }

    public Scene getScene() {
        Scene mainScene = sceneController.getCurrentScene();
        AnchorPane root = new AnchorPane();
        SidePanel sidePanelObject = new SidePanel(user, sceneController);
        AnchorPane sidePanel = sidePanelObject.getSidePanel();

        AnchorPane.setLeftAnchor(sidePanel, 0.0);
        AnchorPane.setTopAnchor(sidePanel, 0.0);
        AnchorPane.setBottomAnchor(sidePanel, 0.0);

        AnchorPane contentPane = getContentPane(mainScene);

        AnchorPane.setTopAnchor(contentPane, 0.0);
        AnchorPane.setLeftAnchor(contentPane, 200.0);
        AnchorPane.setBottomAnchor(contentPane, 0.0);
        root.getChildren().addAll(sidePanel, contentPane);
        Scene scene = new Scene(root, mainScene.getWidth(), mainScene.getHeight());
        sceneController.setTitle("BookBetter - Admin");

        return scene;
    }

    private AnchorPane getContentPane(Scene mainScene) {
        switch (tab) {
            case "DASHBOARD":
                return getDashboard(mainScene);
            case "ORDERS":
                return getOrders(mainScene);
            case "USERS":
                return getUsers(mainScene);
            case "BOOKS":
                return getBooks(mainScene);
            default:
                return getDashboard(mainScene);
        }
    }

    public AnchorPane getDashboard(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Admin Dashboard");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("Track and manage all Pending Book Listings");

        ObservableList<Book> pendingBooksList;

        //Create a new book table with only pending books in the table
        try{
            Book pendingBooks = new Book(0, "temp", "temp", "temp", "temp", 1);
            pendingBooksList = pendingBooks.getPendingBooks();
        } catch (SQLException e) {
            e.printStackTrace();
            pendingBooksList = FXCollections.observableArrayList();
        }

        //instantiate the new table for pending books
        pendingBooksTable = createBookTable((ObservableList<Book>) pendingBooksList);
        pendingBooksTable.setPrefWidth(600);
        pendingBooksTable.setPrefHeight(500);
        pendingBooksTable.setEditable(false);

        JDBCConnection newConnection = new JDBCConnection();

        //Create a new simple menu to either activate or deny a book in the system
        ContextMenu changeBookStatusMenu = new ContextMenu();

        MenuItem activate = new MenuItem("ACTIVATE");
        MenuItem deny = new MenuItem("DENY");

        changeBookStatusMenu.getItems().addAll(activate, deny);

        //if the activate button is clicked, change the book status to active
        activate.setOnAction(e -> {
            Book selectedBook = pendingBooksTable.getSelectionModel().getSelectedItem();

            if(selectedBook != null){
                int bookID = selectedBook.getId();

                newConnection.updateBookStatus(bookID, "ACTIVE");

                refreshPendingBooksTable();
            }
        });

        //if the deny button is clicked, change the book status to reject
        deny.setOnAction(e -> {
            Book selectedBook = pendingBooksTable.getSelectionModel().getSelectedItem();

            if(selectedBook != null){
                int bookID = selectedBook.getId();

                newConnection.updateBookStatus(bookID, "REJECT");

                refreshPendingBooksTable();
            }
        });

        //if the admin user right clicks on any row in the table they get the new context menu
        pendingBooksTable.setOnMouseClicked(event -> {
            if(event.getButton() == MouseButton.SECONDARY){
                Book selectedBook = pendingBooksTable.getSelectionModel().getSelectedItem();
                if(selectedBook != null){
                    changeBookStatusMenu.show(pendingBooksTable, event.getScreenX(), event.getScreenY());
                }
            }
        });

        //allow the admin user to view all details of pending books when double-clicking on a specific row
        viewBookDetails(pendingBooksTable);

        pane.getChildren().addAll(titleLabel, subtitleLabel, pendingBooksTable);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        AnchorPane.setTopAnchor(pendingBooksTable, 120.0);
        AnchorPane.setLeftAnchor(pendingBooksTable, 50.0);

        pane.getStylesheets().add(css);
        return pane;
    }

    public AnchorPane getOrders(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Orders");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("Manage all book orders");
        TableView<Transaction> tableView = JDBCConnection.getTransactionTable(user);
        tableView.setPrefWidth(1000);
        tableView.setPrefHeight(650);
        pane.getChildren().addAll(titleLabel, subtitleLabel, tableView);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        AnchorPane.setLeftAnchor(tableView, 50.0);
        AnchorPane.setTopAnchor(tableView, 120.0);
        pane.getStylesheets().add(css);
        return pane;
    }

    public AnchorPane getUsers(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Users");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("View and manage users");

        List<User> allUsersList;

        try{
            User allUsers = new User(0, "allUsers", "admin", "1234"); //create a new temporary user
            allUsersList = allUsers.getAllUsers(cacheManager, USERS_CACHE_KEY); //get all users from the system database
        } catch (SQLException e) { //check for any errors when getting all users in the system
            e.printStackTrace();
            allUsersList = new ArrayList<>();
        }

        //Create the new table
        allUsersTable = createUserTable(allUsersList);
        allUsersTable.setPrefWidth(600);
        allUsersTable.setPrefHeight(500);
        allUsersTable.setEditable(false);
        viewUserDetails(allUsersTable);

        pane.getChildren().addAll(titleLabel, subtitleLabel, allUsersTable);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        AnchorPane.setTopAnchor(allUsersTable, 120.0);
        AnchorPane.setLeftAnchor(allUsersTable, 50.0);
        pane.getStylesheets().add(css);
        return pane;

    }


    public AnchorPane getBooks(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Books");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("View and manage books");

        ObservableList<Book> allBooksList = JDBCConnection.fetchAllBooksFromDatabase();

        allBooksTable = createBookTable(allBooksList);
        allBooksTable.setPrefWidth(600);
        allBooksTable.setPrefHeight(500);
        allBooksTable.setEditable(false);
        viewBookDetails(allBooksTable);

        pane.getChildren().addAll(titleLabel, subtitleLabel, allBooksTable);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        AnchorPane.setTopAnchor(allBooksTable, 120.0);
        AnchorPane.setLeftAnchor(allBooksTable, 50.0);
        pane.getStylesheets().add(css);
        return pane;
    }

    //Create the Book Table for all books currently in the system
    private TableView<Book> createBookTable(ObservableList<Book> allBookList){

        TableView<Book> bookTableView = new TableView<>();

        TableColumn<Book, Integer> bookIdColumn = new TableColumn<>("Book ID");
        bookIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));

        TableColumn<Book, String> bookNameColumn = new TableColumn<>("Book Name");
        bookNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

        TableColumn<Book, String> bookAuthorColumn = new TableColumn<>("Author");
        bookAuthorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));

        TableColumn<Book, String> bookConditionColumn = new TableColumn<>("Book Condition");
        bookConditionColumn.setCellValueFactory(new PropertyValueFactory<>("condition"));

        TableColumn<Book, String> bookCategoriesColumn = new TableColumn<>("Book Categories");
        bookCategoriesColumn.setCellValueFactory(cellData -> {
            ArrayList<String> bookCategories = cellData.getValue().getCategories();
            String stringBookCategories = cellData.getValue().stringCategories(bookCategories);
            String categoriesString = String.join(", ", bookCategories);
            return new SimpleStringProperty(stringBookCategories);
        });

        bookTableView.getColumns().addAll(bookIdColumn, bookNameColumn, bookAuthorColumn, bookConditionColumn, bookCategoriesColumn);

        ObservableList<Book> bookData = FXCollections.observableArrayList(allBookList);
        bookTableView.setItems(bookData);

        return bookTableView;
    }


    //Allow the admin user to click on a book in the system and view further book information
    private void viewBookDetails(TableView<Book> bookTableView){
        bookTableView.setRowFactory(e -> {
            TableRow<Book> currentBookRow = new TableRow<>();

            //check if the admin user clicked on the row twice
            currentBookRow.setOnMouseClicked(event -> {
                if(event.getClickCount() == 2 && (!currentBookRow.isEmpty())) {
                    Book selectedBook = currentBookRow.getItem();
                    showBookDetails(selectedBook); //show the admin user the detailed book information
                }
            });
            return currentBookRow;
        });
    }

    //Show detailed book information when the admin user clicks on a book
    private void showBookDetails(Book book){
        Stage stage = new Stage(); // set the stage and stage title
        stage.setTitle("Book Details");

        //create the title label
        Label titleLabel = new Label("Book Information: ");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
        titleLabel.getStyleClass().add("h2");

        //create the book id section
        Label idLabel = new Label("ID: ");
        TextField userID = new TextField();
        userID.setText(String.valueOf(book.getId()));
        idLabel.getStyleClass().add("h2");
        idLabel.setStyle("-fx-font-size: 12px");
        userID.getStyleClass().add("h2");
        userID.setEditable(false);

        //create the book image section
        Image bookImage = new Image(book.getImage().toURI().toString()); //create a new Image using the book file
        ImageView bookImageView = new ImageView(bookImage); //create a new image view to see the image

        //set the size of the book
        bookImageView.setFitWidth(200);
        bookImageView.setFitHeight(200);
        bookImageView.setPreserveRatio(true);

        //create the book name section
        Label bookNameLabel = new Label("Book Name: ");
        TextField bookNameField = new TextField();
        bookNameField.setText(book.getName());
        bookNameLabel.getStyleClass().add("h2");
        bookNameLabel.setStyle("-fx-font-size: 12px");
        bookNameField.getStyleClass().add("h2");
        bookNameField.setEditable(false);

        //create the book author section
        Label bookAuthorLabel = new Label("Book Author: ");
        TextField bookAuthorField = new TextField();
        bookAuthorField.setText(book.getName());
        bookAuthorLabel.getStyleClass().add("h2");
        bookAuthorLabel.setStyle("-fx-font-size: 12px");
        bookAuthorField.getStyleClass().add("h2");
        bookAuthorField.setEditable(false);

        //create the book condition section
        Label bookConditionLabel = new Label("Book Condition: ");
        TextField bookConditionField = new TextField();
        bookConditionField.setText(book.getCondition());
        bookConditionLabel.getStyleClass().add("h2");
        bookConditionLabel.setStyle("-fx-font-size: 12px");
        bookConditionField.getStyleClass().add("h2");

        //create the book categories section
        Label bookCategoriesLabel = new Label("Book Categories: ");
        TextField bookCategoriesField = new TextField();
        bookCategoriesField.setText(book.stringCategories(book.getCategories())); //convert each category list to string
        bookCategoriesLabel.getStyleClass().add("h2");
        bookCategoriesLabel.setStyle("-fx-font-size: 12px");
        bookCategoriesField.getStyleClass().add("h2");

        //Create the book information grid pane and add each above section into the GridPane
        GridPane bookInformationPane = new GridPane();
        bookInformationPane.setAlignment(Pos.CENTER);
        bookInformationPane.setHgap(15);
        bookInformationPane.setVgap(15);

        bookInformationPane.add(titleLabel, 0, 0);

        bookInformationPane.add(idLabel, 0, 1);
        bookInformationPane.add(userID, 1, 1);

        bookInformationPane.add(bookImageView, 0, 2);

        bookInformationPane.add(bookNameLabel, 0, 3);
        bookInformationPane.add(bookNameField, 1, 3);

        bookInformationPane.add(bookAuthorLabel, 0, 4);
        bookInformationPane.add(bookAuthorField, 1, 4);

        bookInformationPane.add(bookConditionLabel, 0, 5);
        bookInformationPane.add(bookConditionField, 1, 5);

        bookInformationPane.add(bookCategoriesLabel, 0, 6);
        bookInformationPane.add(bookCategoriesField, 1, 6);

        Scene scene = new Scene(bookInformationPane, 400, 500); //set the new scene
        stage.setScene(scene);
        stage.show();
    }

    //Create the User Table for all users currently in the system
    private TableView<User> createUserTable(List<User> allUserList){
        TableView<User> userTableView = new TableView<>();

        //Create the unique userID column
        TableColumn<User, Integer> idColumn = new TableColumn<>("User ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setPrefWidth(25);

        //Create the username column
        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        usernameColumn.setPrefWidth(200);

        //Create the user type column
        TableColumn<User, String> typeColumn = new TableColumn<>("Type");
        typeColumn.setCellValueFactory(new PropertyValueFactory<>("userType"));
        typeColumn.setPrefWidth(200);

        //add all columns into the table
        userTableView.getColumns().addAll(idColumn, usernameColumn, typeColumn);

        //Create a new observable list for all user data using the user list
        ObservableList<User> userData = FXCollections.observableArrayList(allUserList);
        userTableView.setItems(userData);

        //return the new table view
        return userTableView;
    }

    //allow the admin user to expand the user information in the table to a new stage
    private void viewUserDetails(TableView<User> userTableView) {
        userTableView.setRowFactory(e -> {
            TableRow<User> row = new TableRow<>();
            //if the admin user clicks on a specific row, ensure that the new stage with all user informatino is shown
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    User selectedUser = row.getItem();
                    showUserDetails(selectedUser);
                }
            });
            return row;
        });
    }

    //show all users in the system
    private void showUserDetails(User user) {
        Stage stage = new Stage();
        stage.setTitle("User Details");

        //Create the main title for the page
        Label titleLabel = new Label("User Information: ");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
        titleLabel.getStyleClass().add("h2");

        //Create the ID section
        Label idLabel = new Label("ID: ");
        TextField userID = new TextField();
        userID.setText(String.valueOf(user.getId()));
        idLabel.getStyleClass().add("h2");
        idLabel.setStyle("-fx-font-size: 12px");
        userID.getStyleClass().add("h2");
        userID.setEditable(false);

        //Create the Username section
        Label usernameLabel = new Label("Username: ");
        TextField usernameField = new TextField();
        usernameField.setText(user.getName());
        usernameLabel.getStyleClass().add("h2");
        usernameLabel.setStyle("-fx-font-size: 12px");
        usernameField.getStyleClass().add("h2");
        usernameField.setEditable(false);

        //Create the User Type section
        Label typeLabel = new Label("Type: ");
        TextField userTypeField = new TextField();
        userTypeField.setText(user.getUserType());
        typeLabel.getStyleClass().add("h2");
        typeLabel.setStyle("-fx-font-size: 12px");
        userTypeField.getStyleClass().add("h2");
        userTypeField.setEditable(false);

        //Create the close button that closes the user information page
        Button closeButton = new Button("Close");
        closeButton.getStyleClass().add("h2");
        closeButton.setOnAction(e -> stage.close());

        //Create the remove button
        Button removeButton = new Button("Remove");
        removeButton.getStyleClass().add("h2");
        removeButton.setOnAction(e -> {

            //When the admin user clicks on the remove button, alert them requiring a confirmation
            Alert removeUserConfirmation = new Alert(Alert.AlertType.CONFIRMATION);
            removeUserConfirmation.setTitle("CONFIRM REMOVAL");
            removeUserConfirmation.setHeaderText("Do you want to remove this user?");
            removeUserConfirmation.setContentText("Once the user is removed, the action cannot be undone");

            //Check if the admin user confirms they want to remove the user
            removeUserConfirmation.showAndWait().ifPresent(response -> {
                if(response == ButtonType.OK) {
                    try {
                        boolean isUserDeleted = deleteUser(user.getId()); //check if the user has been successfully deleted

                        if (isUserDeleted) { //indicate that user has been deleted to the admin user
                            Alert deletedUserAlert = new Alert(Alert.AlertType.INFORMATION);
                            deletedUserAlert.setTitle("SUCCESSFUL REMOVAL");
                            deletedUserAlert.setHeaderText("USER HAS BEEN REMOVED");
                            deletedUserAlert.setContentText("The user has been removed from the system successfully");
                            deletedUserAlert.showAndWait();

                            refreshUserTable();
                        } else { //indicate that the user has NOT been deleted to the admin user
                            Alert didNotDeleteUserAlert = new Alert(Alert.AlertType.ERROR);
                            didNotDeleteUserAlert.setTitle("FAILED REMOVAL");
                            didNotDeleteUserAlert.setHeaderText("USER HAS NOT BEEN REMOVED");
                            didNotDeleteUserAlert.setContentText("The user has not been removed from the system successfully");
                            didNotDeleteUserAlert.showAndWait();
                        }
                    } catch (SQLException ex) { //catch any errors here
                        ex.printStackTrace();
                    }
                }
            });

            stage.close(); //close the stage after all processes are conducted
        });


        //Create the general page setup for User Information
        GridPane userInformationPane = new GridPane();
        userInformationPane.setAlignment(Pos.CENTER);
        userInformationPane.setHgap(15);
        userInformationPane.setVgap(15);

        userInformationPane.add(titleLabel, 0, 0);

        userInformationPane.add(idLabel, 0, 1);
        userInformationPane.add(userID, 1, 1);

        userInformationPane.add(usernameLabel, 0, 2);
        userInformationPane.add(usernameField, 1, 2);

        userInformationPane.add(typeLabel, 0, 3);
        userInformationPane.add(userTypeField, 1, 3);

        userInformationPane.add(closeButton, 0, 4);
        userInformationPane.add(removeButton, 1, 4);

        Scene scene = new Scene(userInformationPane, 400, 200);
        stage.setScene(scene);
        stage.show();
    }

    //Refresh the user table after a user is removed from the system
    private void refreshUserTable() {
        List<User> updatedUsersList; //Create a new list to hold all the updated users after removal

        try {
            User tempUser = new User(0, "allUsers", "admin", "1234");
            updatedUsersList = tempUser.getAllUsers(cacheManager, USERS_CACHE_KEY); //create a new temp user to get all users from the database after removal
        } catch (SQLException e) { //show an error that getting all users was unsuccessful
            e.printStackTrace();
            updatedUsersList = new ArrayList<>();
        }

        if(allUsersTable != null){ //check that the user table exists
            allUsersTable.getItems().clear(); //clear all items in the table
            allUsersTable.getItems().addAll(updatedUsersList); //update the table with the updated users in the system after removal
        }
    }
    private void invalidateUsersCache() {
        cacheManager.clear(USERS_CACHE_KEY);
    }
    //Ensure that once a book is accepted into the system, the pending listings table is adjusted
    private void refreshPendingBooksTable(){
        List<Book> updatedPendingBooksList;

        //create a new list of all books in the system that have a status of pending
        try{
            Book pendingBooks = new Book(0, "temp", "temp", "temp", "temp", 1);
            updatedPendingBooksList = pendingBooks.getPendingBooks();
        } catch (SQLException e) {
            e.printStackTrace();
            updatedPendingBooksList = new ArrayList<>();
        }

        //clear the old table and create the new one with the updated list of pending books
        if(pendingBooksTable != null){
            pendingBooksTable.getItems().clear();
            pendingBooksTable.getItems().addAll(updatedPendingBooksList);
        }
    }

    //delete the user from the system
    private boolean deleteUser(int userID) throws SQLException{
        String deleteUserQuery = "DELETE FROM users WHERE id = ?"; //delete query for database

        try(Connection connection =  DriverManager.getConnection("jdbc:mysql://bookbetter-aws.czoua2woyqte.us-east-2.rds.amazonaws.com:3306/user", "admin", "!!mqsqlhubbard2024");
            PreparedStatement deleteStatement = connection.prepareStatement((deleteUserQuery))){ //create a connection into the system and run the query

            deleteStatement.setInt(1, userID); //set the parameters of the statement, more specifically which user to delete

            int numOfRowsAffected = deleteStatement.executeUpdate(); //execute the delete and show how many rows have been affected by deleting the user (should be just 1)

            return numOfRowsAffected > 0; //return true if at least one row has been impacted
        }
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}