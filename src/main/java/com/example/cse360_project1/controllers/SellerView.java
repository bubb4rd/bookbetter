package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.Error;
import com.example.cse360_project1.models.Transaction;
import com.example.cse360_project1.models.User;
import com.example.cse360_project1.services.JDBCConnection;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class SellerView {
    private final User user;
    private final SceneController sceneController;
    private String tab;

    public SellerView(User user, SceneController sceneController) {
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
        sceneController.setTitle("BookBetter - Seller");

        return scene;
    }

    private AnchorPane getContentPane(Scene mainScene) {
        return switch (tab) {
            case "LIST" -> getListBook(mainScene);
            case "TRANSACTIONS" -> getTransactions(mainScene);
            case "EDIT LISTINGS" -> getEditListings(mainScene);
            case "LIST_SUCCESS" -> getListBookSuccess();
            default -> getDashboard(mainScene);
        };
    }

    public AnchorPane getDashboard(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Hey " + user.getName() + ",");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("Track and manage your orders");

        VBox totalRevenue = new VBox();
        totalRevenue.getStyleClass().add("blurb");
        totalRevenue.getStyleClass().add("mini");
        totalRevenue.setSpacing(20);

        Label totalRevenueLabel = new Label("Total Revenue");
        totalRevenueLabel.getStyleClass().add("h2");
        totalRevenue.setPadding(new Insets(20, 20, 20, 20));

        Label errorLabel = new Label("No data found.");
        errorLabel.getStyleClass().add("text-lg");

        totalRevenue.getChildren().addAll(totalRevenueLabel, errorLabel);


        VBox recentOrders = new VBox();
        recentOrders.getStyleClass().add("blurb");
        recentOrders.getStyleClass().add("wide");
        recentOrders.setPadding(new Insets(20, 20, 20, 20));
        Label recentOrdersLabel = new Label("Recent Orders");
        recentOrdersLabel.getStyleClass().add("h2");

        Button viewAllButton = new Button("View All");
        viewAllButton.getStyleClass().add("secondary");
        viewAllButton.setPadding(new Insets(10, 15, 10, 15));
        viewAllButton.setOnAction(e -> {
            this.tab = "TRANSACTIONS";
            sceneController.switchScene(getScene());
        });

        HBox headerBox = new HBox();
        headerBox.setPadding(new Insets(0, 0, 10, 0));
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(recentOrdersLabel, spacer, viewAllButton);
        JDBCConnection connection = new JDBCConnection();
        TableView<Transaction> tableView = connection.getTransactionTable(user);
        recentOrders.getChildren().addAll(headerBox, tableView);


        pane.getChildren().addAll(titleLabel, subtitleLabel, totalRevenue, recentOrders);

        String css = getClass().getResource("/com/example/cse360_project1/css/SellerView.css").toExternalForm();
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);

        AnchorPane.setTopAnchor(totalRevenue, 150.0);
        AnchorPane.setLeftAnchor(totalRevenue, 50.0);

        AnchorPane.setLeftAnchor(recentOrders, 50.0);

        AnchorPane.setBottomAnchor(recentOrders, 20.0);
        pane.getStylesheets().add(css);
        return pane;
    }

    public AnchorPane getListBook(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("List a Book");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));

        Label subtitleLabel = new Label("Sell a new book.");

        VBox listBlurb = new VBox();
        listBlurb.getStyleClass().add("blurb");
        listBlurb.getStyleClass().add("tall");
        listBlurb.setSpacing(10.0);
        listBlurb.setPadding(new Insets(20, 20, 20, 20));

        VBox bookNameVBox = new VBox();
        bookNameVBox.setSpacing(4.0);

        Label bookNameLabel = new Label("Book Name");
        bookNameLabel.getStyleClass().add("h3");

        TextField bookNameInput = new TextField();
        bookNameInput.setPromptText("Enter a book name");
        bookNameInput.getStyleClass().addAll("gray-border", "text-lg", "input");

        bookNameVBox.getChildren().addAll(bookNameLabel, bookNameInput);

        VBox author = new VBox();
        author.setSpacing(4.0);

        Label authorNameLabel = new Label("Author");
        authorNameLabel.getStyleClass().add("h3");

        TextField authorNameInput = new TextField();
        authorNameInput.setPromptText("Enter the author name");
        authorNameInput.getStyleClass().addAll("gray-border", "text-lg", "input");

        author.getChildren().addAll(authorNameLabel, authorNameInput);

        VBox conditionContainer = new VBox();
        conditionContainer.setSpacing(4.0);

        Label conditionNameLabel = new Label("Condition");
        conditionNameLabel.getStyleClass().add("h3");

        HBox condition = new HBox();
        condition.setSpacing(10);
        ComboBox<String> conditionCombo = new ComboBox();
        conditionCombo.getStyleClass().addAll("gray-border", "text-lg", "input");


        conditionCombo.setValue("Choose Book Condition");
        conditionCombo.getItems().addAll("Lightly used", "Moderately used", "Heavily used ");

        Button chooseImageButton = new Button("Choose Image + ");
        chooseImageButton.getStyleClass().add("secondary");
        chooseImageButton.setPrefWidth(150);
        chooseImageButton.setPrefHeight(60);

        AtomicReference<File> imageFile = new AtomicReference<>();
        chooseImageButton.setOnAction(e -> {
            FileChooser imageChooser = new FileChooser();
            imageChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("Image Files", "*.jpg", "*.png")
            );
            imageFile.set(imageChooser.showOpenDialog(sceneController.getStage()));
            if (imageFile.get() != null) {
                chooseImageButton.setText(imageFile.get().getName());
            }
        });


        condition.getChildren().addAll((Node) conditionCombo, chooseImageButton);
        conditionContainer.getChildren().addAll(conditionNameLabel, condition);
        ArrayList<ToggleButton> allCategories = new ArrayList<>();
        ArrayList<String> selectedCategories = new ArrayList<>();
        ToggleButton natScienceButton = new ToggleButton("Natural Science");
        natScienceButton.getStyleClass().add("toggle-button");
        allCategories.add(natScienceButton);
        ToggleButton computerButton = new ToggleButton("Computer");
        computerButton.getStyleClass().add("toggle-button");
        allCategories.add(computerButton);

        ToggleButton mathButton = new ToggleButton("Math");
        mathButton.getStyleClass().add("toggle-button");
        allCategories.add(mathButton);

        ToggleButton englishLangButton = new ToggleButton("English Language");
        englishLangButton.getStyleClass().add("toggle-button");
        allCategories.add(englishLangButton);

        ToggleButton scifiButton = new ToggleButton("Sci-Fi");
        scifiButton.getStyleClass().add("toggle-button");
        allCategories.add(scifiButton);

        ToggleButton artButton = new ToggleButton("Art");
        artButton.getStyleClass().add("toggle-button");
        allCategories.add(artButton);

        ToggleButton novelButton = new ToggleButton("Novel");
        novelButton.getStyleClass().add("toggle-button");
        allCategories.add(novelButton);

        for (ToggleButton button: allCategories) {
            button.setOnAction(e -> {
                if (button.isSelected()) {
                    selectedCategories.add(button.getText());
                }
                else {
                    selectedCategories.remove(button.getText());

                }
            });
        }
        VBox categories = new VBox();
        categories.setSpacing(5);
        Label categoriesLabel = new Label("Categories");
        categoriesLabel.getStyleClass().add("h3");
        HBox categoriesBox1 = new HBox(10, natScienceButton, computerButton);
        HBox categoriesBox2 = new HBox(10, mathButton, englishLangButton);
        HBox categoriesBox3 = new HBox(10, scifiButton, artButton, novelButton);
        categories.getChildren().addAll(categoriesLabel, categoriesBox1, categoriesBox2, categoriesBox3);

        Button submitButton = new Button("List your book");
        submitButton.getStyleClass().add("h3");
        submitButton.getStyleClass().add("button");
        submitButton.setStyle("-fx-pref-width: 300px; -fx-background-color: #640000; -fx-text-fill: white; -fx-pref-height: 50px;");

        HBox submitArea = new HBox();
        submitArea.setPadding(new Insets(20, 20, 20, 20));
        Region spacerLeft =  new Region();
        HBox.setHgrow(spacerLeft, Priority.ALWAYS);
        Region spacerRight =  new Region();
        HBox.setHgrow(spacerRight, Priority.ALWAYS);
        submitArea.getChildren().addAll(spacerLeft, submitButton, spacerRight);
        listBlurb.getChildren().addAll(bookNameVBox, author, conditionContainer, categories, submitArea);

        pane.getChildren().addAll(titleLabel, subtitleLabel, listBlurb);
        String css = getClass().getResource("/com/example/cse360_project1/css/SellerView.css").toExternalForm();
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);

        AnchorPane.setTopAnchor(listBlurb, 120.0);
        AnchorPane.setLeftAnchor(listBlurb, 50.0);
        pane.getStylesheets().add(css);

        submitButton.setOnAction(e -> {
           String bookName = bookNameInput.getText();
           String bookAuthor = authorNameInput.getText();
           String bookCondition = conditionCombo.getValue();
           String bookCategories = Arrays.toString(selectedCategories.toArray());
           if (bookName.isEmpty() || bookAuthor.isEmpty() || bookCondition.isEmpty() || bookCategories.isEmpty() || bookCondition.equals("Choose Book Condition")) {
               Error emptyFieldError = new Error("Submit error: One or more empty field");
               emptyFieldError.displayError(pane, mainScene);
           } else if (imageFile.get() == null) {
               Error imageError = new Error("Submit error: Image failed");
               imageError.displayError(pane, mainScene);
           } else {
               Book newBook = new Book(user.getId(), bookName, bookAuthor, bookCondition, bookCategories, user.getId(), imageFile.get());
               JDBCConnection connection = new JDBCConnection();
               if (connection.addBook(newBook)) {
                   System.out.println("Book added: " + newBook.getName());

                   this.tab = "LIST_SUCCESS";
                   sceneController.switchScene(getScene());
               }
           }
        });
        return pane;
    }

    public AnchorPane getListBookSuccess() {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Congrats!");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));

        VBox successBox = new VBox();
        successBox.setSpacing(5);
        successBox.setPadding(new Insets(20, 20, 20, 20));
        successBox.setAlignment(Pos.CENTER);

        HBox buttonBox = new HBox();
        buttonBox.setAlignment(Pos.CENTER);

        buttonBox.setSpacing(20);

        Button listNewBookButton = new Button("List new Book");
        listNewBookButton.getStyleClass().add("button");
        listNewBookButton.getStyleClass().add("maroon");
        listNewBookButton.getStyleClass().add("text-lg");

        listNewBookButton.setOnAction(e -> {
            this.tab = "LIST";
            sceneController.switchScene(getScene());
        });

        Button returnButton = new Button("Return to dashboard");
        returnButton.getStyleClass().add("button");
        returnButton.getStyleClass().add("secondary");
        returnButton.getStyleClass().add("text-lg");

        returnButton.setOnAction(e -> {
            this.tab = "DASHBOARD";
            sceneController.switchScene(getScene());
        });

        buttonBox.getChildren().addAll(listNewBookButton, returnButton);

        Label subLabel = new Label("Your book has been listed successfully. It is pending approval.");

        subLabel.getStyleClass().add("h2");
        subLabel.setPadding(new Insets(20, 20, 20, 20));

        successBox.getChildren().addAll(titleLabel, subLabel, buttonBox);

        pane.getChildren().addAll(successBox);

        AnchorPane.setTopAnchor(successBox, 280.0);
        AnchorPane.setLeftAnchor(successBox, 200.0);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        pane.getStylesheets().add(css);

        return pane;
    }

    public AnchorPane getEditListings(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        //Title
        Label titleLabel = new Label("Edit Listings");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        titleLabel.setPadding(new Insets(20));

        //Book list
        VBox bookListBox = new VBox(10);
        bookListBox.setPadding(new Insets(20));
        bookListBox.setSpacing(10);
        Label selectBookLabel = new Label("Select a Book");
        selectBookLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
//      bookListBox.getChildren().addAll(selectBookLabel, bookListView, editButton);

//        VBox contentBox = new VBox();
//        contentBox.setPadding(new Insets(10, 10, 10, 10));
//        contentBox.getStyleClass().add("blurb");

        //Get the user's listed books
        ListView<Book> bookListView = new ListView<>();
        JDBCConnection jdbcConnection = new JDBCConnection();
        ArrayList<Book> userBooks = jdbcConnection.getBookCollection(user);

        //If user does not have books listed
        if (userBooks.isEmpty()) {
            // No books listed
            Label noBooksLabel = new Label("You currently have no books listed for sale.");
            noBooksLabel.getStyleClass().add("h2");
            bookListBox.getChildren().addAll(selectBookLabel, noBooksLabel);
        } else {
            bookListView.getItems().addAll(userBooks);
            // Edit button
            Button editButton = new Button("Edit Selected Book");
            editButton.getStyleClass().add("primary");

            // Book details
            VBox detailsForm = new VBox(10);
            detailsForm.setPadding(new Insets(10));
            detailsForm.setVisible(false);

            // Button action for editing a book
            editButton.setOnAction(e -> {
                Book selectedBook = bookListView.getSelectionModel().getSelectedItem();
                if (selectedBook == null) {
                    Error noSelectionError = new Error("Please select a book to edit.");
                    noSelectionError.displayError(pane, mainScene);
                } else {
                    detailsForm.setVisible(true);
                    populatedEditForm(pane, detailsForm, selectedBook, jdbcConnection);
                }
            });

            bookListBox.getChildren().addAll(selectBookLabel, bookListView, editButton);
            bookListBox.getChildren().add(detailsForm); // Add the form to the layout
        }

        HBox mainContentBox = new HBox(20);
        mainContentBox.setPadding(new Insets(20));
        mainContentBox.getChildren().add(bookListBox);

        pane.getChildren().addAll(titleLabel, mainContentBox);
        AnchorPane.setTopAnchor(titleLabel, 30.0);
        AnchorPane.setLeftAnchor(titleLabel, 50.0);
        AnchorPane.setTopAnchor(mainContentBox, 100.0);
        AnchorPane.setLeftAnchor(mainContentBox, 50.0);
        return pane;
    }

    private void populatedEditForm(AnchorPane parentPane, VBox detailsForm, Book book, JDBCConnection jdbcConnection) {
        detailsForm.getChildren().clear();

        //Name of book
        Label bookNameLabel = new Label("Book Name:");
        bookNameLabel.getStyleClass().add("h3");
        TextField bookNameField = new TextField(book.getName());
        bookNameField.getStyleClass().addAll("gray-border", "input");

        //Author of book
        Label authorLabel = new Label("Author Name:");
        bookNameLabel.getStyleClass().add("h3");
        TextField authorField = new TextField(book.getAuthor());
        bookNameField.getStyleClass().addAll("gray-border", "input");

        //Condition of book
        Label conditionLabel = new Label("Book Condition:");
        bookNameLabel.getStyleClass().add("h3");
        ComboBox<String> conditionComboBox = new ComboBox<>();
        conditionComboBox.getItems().addAll("Lightly used", "Moderately used", "Heavily used");
        conditionComboBox.setValue(book.getCondition());
        conditionComboBox.getStyleClass().addAll("gray-border", "input");

        Button saveButton = new Button("Save Changes");
        saveButton.getStyleClass().add("primary");
        saveButton.setOnAction(e -> {
            String updatedName = bookNameField.getText();
            String updatedAuthor = authorField.getText();
            String updatedCondition = conditionComboBox.getValue();

            if (updatedName.isEmpty() || updatedAuthor.isEmpty() || updatedCondition.isEmpty()) {
                Error fieldError = new Error("Please fill out all fields before saving.");
                fieldError.displayError(parentPane, null);
            } else {
                book.setName(updatedName);
                book.setAuthor(updatedAuthor);
                book.setCondition(updatedCondition);

                try {
                    int result = jdbcConnection.updateQuery(
                            "UPDATE books SET book_name = '" + updatedName + "', book_author = '" + updatedAuthor +
                            "', book_condition = '" + updatedCondition + "' WHERE book_id = " + book.getId()
                    );
                    if (result > 0) {
                        Error successMessage = new Error("Book details updated successfully!");
                        successMessage.displayError(parentPane, null);
                    } else {
                        Error updateError = new Error("Failed to update book details.");
                        updateError.displayError(parentPane, null);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        detailsForm.getChildren().addAll(bookNameLabel, bookNameField, authorLabel, authorField, conditionLabel, conditionComboBox, saveButton);
    }

//        Button saveButton = new Button("Save Changes");
//        saveButton.getStyleClass().add("primary");
//        saveButton.setOnAction(e -> {
//            book.setName(bookNameField.getText());
//            book.setAuthor(authorField.getText());
//            book.setCondition(conditionComboBox.getValue());
//            String updatedName = bookNameField.getText();
//            String updatedAuthor = authorField.getText();
//            String updatedCondition = conditionComboBox.getValue();
//
//            if (updatedName.isEmpty() || updatedAuthor.isEmpty() || updatedCondition.isEmpty()) {
//                Error fieldError = new Error("Please enter all the fields to save your changes.");
//                fieldError.displayError(parentPane, null);
//            }
//            else {
//                book.setName(updatedName);
//                book.setAuthor(updatedAuthor);
//                book.setCondition(updatedCondition);
//
//                try {
//                    if (jdbcConnection.updateQuery(
//                            "UPDATE books SET book_name = '" + updatedName + "', book_author = '" + updatedAuthor +
//                            "', book_condition = '" + updatedCondition + "' WHERE book_id = " + book.getId()) > 0) {
//                        Error successMessage = new Error("Book details updated successfully!");
//                        successMessage.displayError(parentPane, null);
//                    }
//                    else {
//                        Error updateError = new Error("Failed to update book details.");
//                        updateError.displayError(parentPane, null);
//                    }
//                } catch (SQLException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//        });
//        detailsForm.getChildren().addAll(bookNameLabel, bookNameField, authorLabel, conditionLabel, conditionComboBox);
//    }
    public AnchorPane getTransactions(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label titleLabel = new Label("Transactions");
        titleLabel.getStyleClass().add("h1");
        titleLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("View your transaction history");

        JDBCConnection connection = new JDBCConnection();
        TableView<Transaction> tableView = connection.getTransactionTable(user);
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

    public void setTab(String tab) {
        this.tab = tab;
    }

    public HBox categoriesPieChart(User user) {
        HBox main = new HBox();
        main.setAlignment(Pos.CENTER); // Aligns children in the center horizontally
        main.setSpacing(10);
        // Get categories sold
        Map<String, Integer> categoryCounts = user.getCategoriesSold();

        // Create PieChart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : categoryCounts.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " - " + entry.getValue(), entry.getValue()));
        }

        // Create PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Books Sold by Category");

        // Set preferred size for PieChart
        pieChart.setPrefSize(300, 300); // Adjust these values as needed

        // Add PieChart to HBox
        main.getChildren().add(pieChart);

        return main;
    }
    public HBox conditionsPieChart(User user) {
        HBox main = new HBox();
        main.setAlignment(Pos.CENTER); // Aligns children in the center horizontally
        main.setSpacing(10);
        // Get categories sold
        Map<String, Integer> conditionsSold = user.getConditionsSold();

        // Create PieChart data
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : conditionsSold.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " - " + entry.getValue(), entry.getValue()));
        }

        // Create PieChart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setTitle("Books Sold by Condition");

        // Set preferred size for PieChart
        pieChart.setPrefSize(300, 300); // Adjust these values as needed

        // Add PieChart to HBox
        main.getChildren().add(pieChart);

        return main;
    }

}