package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.Transaction;
import com.example.cse360_project1.models.User;
import com.example.cse360_project1.services.JDBCConnection;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.sql.DriverManager.getConnection;

public class BuyerView {
    private double subtotal;
    private double salesTax;
    private double totalPrice;
    private ObservableList<Book> itemsInCart = FXCollections.observableArrayList();
    private final User user;
    private final SceneController sceneController;
    private String tab;
    private static final Image PLACEHOLDER_IMAGE;
    static {
        try {
            PLACEHOLDER_IMAGE = new Image(BuyerView.class.getResource("/com/example/cse360_project1/images/book.jpg").toExternalForm());
        } catch (NullPointerException e) {
            throw new RuntimeException("Placeholder image not found. Ensure the path is correct.", e);
        }
    }
    private ObservableList<Book> activeBooks;

    private final ObservableList<Book> cart = FXCollections.observableArrayList();
    public BuyerView(User user, SceneController sceneController) {
        this.user = user;
        this.sceneController = sceneController;
        this.tab = "BROWSE";

        JDBCConnection connection = new JDBCConnection();

        List<Book> books = connection.getActiveBooks();
        Map<Integer, File> images = connection.fetchImagesForBooks(
                books.stream().map(Book::getId).collect(Collectors.toList())
        );

        for (Book book : books) {
            File imageFile = images.get(book.getId());
            if (imageFile != null) {
                book.setImage(imageFile); // Assign image to the book
            }
        }
        activeBooks = FXCollections.observableArrayList(books);
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
        sceneController.setTitle("BookBetter - Buyer");

        return scene;
    }

    private AnchorPane getContentPane(Scene mainScene) {
        switch (tab) {
            case "BROWSE":
                return getBrowseSection(mainScene);
            case "ORDERS":
                return getOrderHistory(mainScene);
            case "CART":
                return getCart(mainScene);
            default:
                return getBrowseSection(mainScene);
        }
    }

    public AnchorPane getBrowseSection(Scene mainScene) {
        AnchorPane pane = new AnchorPane();

        Label booksLabel = new Label("Today's Books");
        booksLabel.getStyleClass().add("h1");
        booksLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("Browse and purchase books");

        VBox filters = new VBox();
        filters.getStyleClass().add("text-lg");
        Label filtersLabel = new Label("Filters");
        filtersLabel.getStyleClass().add("h2");

        VBox categoriesBox = new VBox(5);
        Label categoriesSubtitleLabel = new Label("Categories");
        categoriesSubtitleLabel.getStyleClass().add("h3");

        CheckBox natScienceCheckBox = new CheckBox("Natural Science");
        CheckBox computerCheckBox = new CheckBox("Computer");
        CheckBox mathCheckBox = new CheckBox("Math");
        CheckBox englishLanguageCheckBox = new CheckBox("English Language");
        CheckBox scifiCheckBox = new CheckBox("Sci-Fi");
        CheckBox artCheckBox = new CheckBox("Art");
        CheckBox novelCheckBox = new CheckBox("Novel");
        categoriesBox.getChildren().addAll(categoriesSubtitleLabel, natScienceCheckBox, computerCheckBox,
                mathCheckBox, englishLanguageCheckBox, scifiCheckBox, artCheckBox, novelCheckBox);

        VBox conditionsBox = new VBox(5);
        Label conditionsSubtitleLabel = new Label("Condition");
        conditionsSubtitleLabel.getStyleClass().add("h3");

        ToggleGroup conditionGroup = new ToggleGroup();
        RadioButton usedButton = new RadioButton("Used Like New");
        RadioButton moderateButton = new RadioButton("Moderately Used");
        RadioButton heavilyButton = new RadioButton("Heavily Used");
        usedButton.setToggleGroup(conditionGroup);
        moderateButton.setToggleGroup(conditionGroup);
        heavilyButton.setToggleGroup(conditionGroup);
        conditionsBox.getChildren().addAll(conditionsSubtitleLabel, usedButton, moderateButton, heavilyButton);

        filters.setSpacing(14);
        filters.getChildren().addAll(filtersLabel, categoriesBox, conditionsBox);

        VBox booksGrid = new VBox(10);
        booksGrid.setPadding(new Insets(20));
        booksGrid.setPrefWidth(800);
        booksGrid.getChildren().clear();
        populateBooksGrid(booksGrid, activeBooks);

        Button filterButton = new Button("Filter Books");
        filterButton.setOnAction(e -> {
            ObservableList<Book> filteredBooks = activeBooks.filtered(book -> {
                // Check if any category is selected
                boolean isAnyCategorySelected =
                        natScienceCheckBox.isSelected() ||
                                computerCheckBox.isSelected() ||
                                mathCheckBox.isSelected() ||
                                englishLanguageCheckBox.isSelected() ||
                                scifiCheckBox.isSelected() ||
                                artCheckBox.isSelected() ||
                                novelCheckBox.isSelected();

                // Match category only if at least one category is selected
                boolean matchesCategory = !isAnyCategorySelected || (
                        (natScienceCheckBox.isSelected() && book.getCategories().contains("Natural Science")) ||
                                (computerCheckBox.isSelected() && book.getCategories().contains("Computer")) ||
                                (mathCheckBox.isSelected() && book.getCategories().contains("Math")) ||
                                (englishLanguageCheckBox.isSelected() && book.getCategories().contains("English Language")) ||
                                (scifiCheckBox.isSelected() && book.getCategories().contains("Sci-Fi")) ||
                                (artCheckBox.isSelected() && book.getCategories().contains("Art")) ||
                                (novelCheckBox.isSelected() && book.getCategories().contains("Novel"))
                );

                // Match condition
                String selectedCondition =
                        conditionGroup.getSelectedToggle() != null ?
                                ((RadioButton) conditionGroup.getSelectedToggle()).getText() : "";

                boolean matchesCondition = selectedCondition.isEmpty() || book.getCondition().equals(selectedCondition);

                // Return true if the book matches both category and condition filters
                return matchesCategory && matchesCondition;
            });

            // Update the grid with filtered books
            booksGrid.getChildren().clear();
            populateBooksGrid(booksGrid, filteredBooks);
        });

        Button refreshButton = new Button ("Refresh");
        refreshButton.setOnAction(e -> {
            // Reset category filters
            natScienceCheckBox.setSelected(false);
            computerCheckBox.setSelected(false);
            mathCheckBox.setSelected(false);
            englishLanguageCheckBox.setSelected(false);
            scifiCheckBox.setSelected(false);
            artCheckBox.setSelected(false);
            novelCheckBox.setSelected(false);

            // Reset condition filter
            conditionGroup.getToggles().forEach(toggle -> ((RadioButton) toggle).setSelected(false));

            // Reset the grid to show all books
            booksGrid.getChildren().clear();
            populateBooksGrid(booksGrid, activeBooks);
        });

        VBox filterSection = new VBox(10, filters, filterButton, refreshButton);

        ScrollPane scrollPane = new ScrollPane(booksGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(800);

        HBox content = new HBox(20, filterSection, scrollPane);
        content.setPadding(new Insets(20));

        AnchorPane.setTopAnchor(booksLabel, 30.0);
        AnchorPane.setLeftAnchor(booksLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        AnchorPane.setTopAnchor(content, 120.0);
        AnchorPane.setLeftAnchor(content, 50.0);
        AnchorPane.setRightAnchor(content, 50.0);

        pane.getChildren().addAll(booksLabel, subtitleLabel, content);

        String css = getClass().getResource("/com/example/cse360_project1/css/BuyerView.css").toExternalForm();
        pane.getStylesheets().add(css);

        return pane;
    }

    private void populateBooksGrid(VBox booksGrid, ObservableList<Book> books) {
        for (Book book : books) {
            HBox bookItem = new HBox(10);

            ImageView bookImageView = new ImageView();
            File bookImageFile = book.getImage();
            if (bookImageFile != null && bookImageFile.exists()) {
                bookImageView.setImage(new Image(bookImageFile.toURI().toString()));
            } else {
                bookImageView.setImage(PLACEHOLDER_IMAGE);
            }
            bookImageView.setFitWidth(100);
            bookImageView.setFitHeight(120);

            Label bookDetails = new Label(book.getName() + " by " + book.getAuthor() + " - " + book.getCondition());
            Button addToCartButton = new Button("Add to Cart");
            JDBCConnection connection = new JDBCConnection();
            addToCartButton.setOnAction(event -> {
                connection.updateCart(book.getId(), user.getId());
            });

            bookItem.getChildren().addAll(bookImageView, bookDetails, addToCartButton);
            booksGrid.getChildren().add(bookItem);
        }
    }

    public AnchorPane getOrderHistory(Scene mainScene) {
        AnchorPane pane = new AnchorPane();

        Label orderHistoryLabel = new Label("Order History"); orderHistoryLabel.getStyleClass().add("h1");
        orderHistoryLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("View your past orders");
        subtitleLabel.setPadding(new Insets(10, 20, 20, 20));

        TableView<Transaction> tableView = JDBCConnection.getTransactionTable(user);
        tableView.setPrefWidth(1000);
        tableView.setPrefHeight(650);

        AnchorPane.setTopAnchor(orderHistoryLabel, 30.0);
        AnchorPane.setLeftAnchor(orderHistoryLabel, 50.0);

        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);

        AnchorPane.setLeftAnchor(tableView, 50.0);
        AnchorPane.setTopAnchor(tableView, 120.0);

        pane.getChildren().addAll(orderHistoryLabel, subtitleLabel, tableView);

        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        pane.getStylesheets().add(css);

        return pane;
    }

    public AnchorPane getCart(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label cartLabel = new Label("Current Cart");
        cartLabel.getStyleClass().add("h1");
        cartLabel.setPadding(new Insets(20, 20, 20, 20));

        Label titles = new Label("Book");
        Label conditions = new Label("Condition");
        Label prices = new Label("Price");
        Line line1 = new Line(50, 100, 740, 100);
        Line line2 = new Line(50, 585, 740, 585);

        VBox priceLabels = new VBox();
        priceLabels.setSpacing(5.0);
        Label subtotalLabel = new Label("Subtotal:");
        Label salesTaxLabel = new Label("Sales Tax:");
        Label totalLabel = new Label("Total:");
        priceLabels.setStyle("-fx-font-weight: bold");
        priceLabels.getChildren().addAll(subtotalLabel, salesTaxLabel, totalLabel);

        HBox buttons = new HBox();
        buttons.setSpacing(30.0);
        Button clearBag = new Button("Clear All");
        Button confirm = new Button("Confirm");
        buttons.getChildren().addAll(clearBag, confirm);

        VBox cartGrid = new VBox();
        cartGrid.setPadding(new Insets(20));
        cartGrid.setPrefWidth(690);
        cartGrid.setPrefHeight(460);

        ArrayList<Book> items = new ArrayList<Book>();
        JDBCConnection connection = new JDBCConnection();
        items = connection.getCartItems(user.getId());
        itemsInCart = FXCollections.observableArrayList(items);

        VBox priceBox = new VBox(5.0);

        subtotal = populateCart(cartGrid, priceBox, itemsInCart);
        salesTax = subtotal * .07;
        totalPrice = subtotal + salesTax;
        priceBox.setStyle("-fx-font-weight: bold");
        Label subtotalCalc = new Label(String.format("$%.2f", subtotal));
        Label salesTaxCalc = new Label(String.format("$%.2f", salesTax));
        Label totalPriceCalc = new Label(String.format("$%.2f", totalPrice));
        priceBox.getChildren().addAll(subtotalCalc, salesTaxCalc, totalPriceCalc);

        ScrollPane scrollPane = new ScrollPane(cartGrid);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);

        //update database
        clearBag.setOnAction(e -> {
            connection.removeAllBooksFromCart(user.getId());
            itemsInCart.clear();
            cartGrid.getChildren().clear();
            priceBox.getChildren().clear();
        });
        confirm.setOnAction(e -> {
            if (itemsInCart.isEmpty()) {
                // Show an alert or a message when the cart is empty
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Empty Cart");
                alert.setHeaderText(null);
                alert.setContentText("Your cart is empty. Please add some books to your cart before confirming the order.");
                alert.showAndWait();
            } else {

                connection.confirmPurchase(user.getId());
                cartGrid.getChildren().clear();
                priceBox.getChildren().clear();
                displayOrder(pane);
            }
        });

        pane.getChildren().addAll(cartLabel, line1, line2, priceLabels, buttons, scrollPane, priceBox);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();

        AnchorPane.setTopAnchor(scrollPane, 110.0);
        AnchorPane.setLeftAnchor(scrollPane, 50.0);

        AnchorPane.setTopAnchor(cartLabel, 30.0);
        AnchorPane.setLeftAnchor(cartLabel, mainScene.getWidth() / 3.25);

        AnchorPane.setTopAnchor(priceLabels, 600.0);
        AnchorPane.setLeftAnchor(priceLabels, mainScene.getWidth() / 1.8);

        AnchorPane.setTopAnchor(priceBox, 600.0);
        AnchorPane.setLeftAnchor(priceBox, mainScene.getWidth() / 1.5);

        AnchorPane.setTopAnchor(buttons, 700.0);
        AnchorPane.setLeftAnchor(buttons, mainScene.getWidth() / 1.7);

        AnchorPane.setTopAnchor(titles, 75.0);
        AnchorPane.setLeftAnchor(titles, 50.0);
        AnchorPane.setTopAnchor(conditions, 75.0);
        AnchorPane.setLeftAnchor(conditions, mainScene.getWidth() / 2.5);
        AnchorPane.setTopAnchor(prices, 75.0);
        AnchorPane.setLeftAnchor(prices, mainScene.getWidth() / 1.5);

        pane.getStylesheets().add(css);
        return pane;
    }
    private double populateCart(VBox cartGrid, VBox priceBox, ObservableList<Book> itemsInCart) {
        subtotal = 0.0;
        for (Book book : itemsInCart) {
            HBox cartItem = new HBox(10);

            ImageView bookImageView = new ImageView();
            File bookImageFile = book.getImage();
            Image displayImage;

            if (bookImageFile != null && bookImageFile.exists()) {
                displayImage = new Image(bookImageFile.toURI().toString());
            } else {
                displayImage = PLACEHOLDER_IMAGE;
            }

            bookImageView.setImage(displayImage);
            bookImageView.setFitWidth(100);
            bookImageView.setFitHeight(120);

            subtotal += book.getPrice();

            Label bookDetails = new Label(book.getName() + " by " + book.getAuthor() + " - " + book.getPrice());
            bookDetails.setStyle("-fx-font-size: 16px");
            Button remove = new Button("x");
            remove.setStyle("-fx-text-fill: red");
            remove.setOnAction(event -> {
                JDBCConnection connection = new JDBCConnection();
                connection.removeOneBook(book.getId());
                itemsInCart.remove(book);

                cartGrid.getChildren().clear();
                populateCart(cartGrid, priceBox, itemsInCart);

                salesTax = subtotal * .07;
                totalPrice = subtotal + salesTax;
                priceBox.getChildren().clear();
                Label subtotalCalc = new Label(String.format("$%.2f", subtotal));
                Label salesTaxCalc = new Label(String.format("$%.2f", salesTax));
                Label totalPriceCalc = new Label(String.format("$%.2f", totalPrice));
                priceBox.getChildren().addAll(subtotalCalc, salesTaxCalc, totalPriceCalc);
            });

            cartItem.getChildren().addAll(bookImageView, bookDetails, remove);
            cartGrid.getChildren().add(cartItem);
        }
        return subtotal;
    }
    private void displayOrder(AnchorPane pane){
        pane.getChildren().clear();

        Label orderConfirmationLabel = new Label("ORDER CONFIRMED");
        orderConfirmationLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold");
        AnchorPane.setTopAnchor(orderConfirmationLabel, 30.0);
        AnchorPane.setLeftAnchor(orderConfirmationLabel, 50.0);

        Label orderSummary = new Label("ORDER SUMMARY:");
        orderSummary.setStyle("-fx-font-size: 18px; -fx-font-weight: bold");
        AnchorPane.setTopAnchor(orderSummary, 90.0);
        AnchorPane.setLeftAnchor(orderSummary, 50.0);

        VBox bookTitles = new VBox(10);
        VBox bookConditions = new VBox(10);
        VBox bookPrices = new VBox(10);

        for (Book book : itemsInCart) {
            Label bookTitle = new Label(book.getName());
            Label bookCondition = new Label(book.getCondition());
            Label bookPrice = new Label(String.format("$%.2f", book.getPrice()));
            bookTitles.getChildren().add(bookTitle);
            bookConditions.getChildren().add(bookCondition);
            bookPrices.getChildren().add(bookPrice);
        }

        VBox priceBox = new VBox(5.0);
        priceBox.setStyle("-fx-font-weight: bold");
        Label subtotalCalc = new Label(String.format("$%.2f", subtotal));
        Label salesTaxCalc = new Label(String.format("$%.2f", salesTax));
        Label totalPriceCalc = new Label(String.format("$%.2f", totalPrice));
        priceBox.getChildren().addAll(subtotalCalc, salesTaxCalc, totalPriceCalc);

        VBox priceLabels = new VBox();
        priceLabels.setSpacing(5.0);
        Label subtotalLabel = new Label("Subtotal:");
        Label salesTaxLabel = new Label("Sales Tax:");
        Label totalLabel = new Label("Total:");
        priceLabels.setStyle("-fx-font-weight: bold");
        priceLabels.getChildren().addAll(subtotalLabel, salesTaxLabel, totalLabel);

        AnchorPane.setTopAnchor(bookTitles, 120.0);
        AnchorPane.setLeftAnchor(bookTitles, 50.0);
        AnchorPane.setTopAnchor(bookConditions, 120.0);
        AnchorPane.setLeftAnchor(bookConditions, 200.0);
        AnchorPane.setTopAnchor(bookPrices, 120.0);
        AnchorPane.setLeftAnchor(bookPrices, 350.0);

        AnchorPane.setTopAnchor(priceLabels, 120.0);
        AnchorPane.setLeftAnchor(priceLabels, 500.0);
        AnchorPane.setTopAnchor(priceBox, 120.0);
        AnchorPane.setLeftAnchor(priceBox, 580.0);

        pane.getChildren().addAll(orderConfirmationLabel, orderSummary, priceLabels, priceBox, bookTitles, bookConditions, bookPrices);
    }

    public void setTab(String tab) {
        this.tab = tab;
        AnchorPane content = getContentPane(sceneController.getCurrentScene());
    }
}