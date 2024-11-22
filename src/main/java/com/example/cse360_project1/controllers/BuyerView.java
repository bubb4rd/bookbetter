package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.Order;
import com.example.cse360_project1.models.User;
import javafx.collections.FXCollections;
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
import java.time.LocalDate;
import java.util.ArrayList;

public class BuyerView {
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
    //private static final Image PLACEHOLDER_IMAGE = new Image("file:/absolute/path/to/book.jpg");
    private ObservableList<Book> books = FXCollections.observableArrayList();


    private ObservableList<Book> cart = FXCollections.observableArrayList();
    public BuyerView(User user, SceneController sceneController) {
        this.user = user;
        this.sceneController = sceneController;
        this.tab = "BROWSE";
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

        ObservableList<Book> allBooks = Book.fetchAllBooksFromDatabase();
        populateBooksGrid(booksGrid, allBooks);

        Button filterButton = new Button("Filter Books");
        filterButton.setOnAction(e -> {
            ObservableList<Book> filteredBooks = allBooks.filtered(book -> {
                boolean matchesCategory =
                        (natScienceCheckBox.isSelected() && book.getCategories().contains("Natural Science")) ||
                                (computerCheckBox.isSelected() && book.getCategories().contains("Computer")) ||
                                (mathCheckBox.isSelected() && book.getCategories().contains("Math")) ||
                                (englishLanguageCheckBox.isSelected() && book.getCategories().contains("English Language")) ||
                                (scifiCheckBox.isSelected() && book.getCategories().contains("Sci-Fi")) ||
                                (artCheckBox.isSelected() && book.getCategories().contains("Art")) ||
                                (novelCheckBox.isSelected() && book.getCategories().contains("Novel"));

                String selectedCondition =
                        conditionGroup.getSelectedToggle() != null ?
                                ((RadioButton) conditionGroup.getSelectedToggle()).getText() : "";

                boolean matchesCondition = selectedCondition.isEmpty() || book.getCondition().equals(selectedCondition);

                return matchesCategory && matchesCondition;
            });

            booksGrid.getChildren().clear();
            populateBooksGrid(booksGrid, filteredBooks);
        });

        Button refreshButton = new Button("Refresh Books");
        refreshButton.setOnAction(e -> {
            booksGrid.getChildren().clear();
            ObservableList<Book> updatedBooks = Book.fetchAllBooksFromDatabase();
            populateBooksGrid(booksGrid, updatedBooks);
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
            addToCartButton.setOnAction(event -> addToCart(book));

            bookItem.getChildren().addAll(bookImageView, bookDetails, addToCartButton);
            booksGrid.getChildren().add(bookItem);
        }
    }

    private void addToCart(Book book) {
        cart.add(book);
        System.out.println(book.getName() + " added to cart.");
    }


    public AnchorPane getOrderHistory(Scene mainScene) {
        AnchorPane pane = new AnchorPane();

        Label orderHistoryLabel = new Label("Order History"); orderHistoryLabel.getStyleClass().add("h1");
        orderHistoryLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("View your past orders");
        subtitleLabel.setPadding(new Insets(10, 20, 20, 20));

        DatePicker datePicker = new DatePicker();
        datePicker.setPromptText("Select a date");
        datePicker.setPadding(new Insets(10, 10, 10, 10));

        TableView<Order> orderTable = new TableView<>();
        TableColumn<Order, String> dateColumn = new TableColumn<>("Date");
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));

        TableColumn<Order, String> titleColumn = new TableColumn<>("Book Title");
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));

        TableColumn<Order, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        TableColumn<Order, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        orderTable.getColumns().addAll(dateColumn, titleColumn, quantityColumn, priceColumn);

        // Mock order data
        ObservableList<Order> allOrders = FXCollections.observableArrayList(
                new Order("2024-11-21", "Book 1", 1, 15.99),
                new Order("2024-11-20", "Book 2", 2, 29.99),
                new Order("2024-11-20", "Book 3", 1, 12.50),
                new Order("2024-11-19", "Book 4", 1, 18.75)
        );

        orderTable.setItems(allOrders);

        datePicker.setOnAction(event -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                String selectedDateString = selectedDate.toString();
                ObservableList<Order> filteredOrders = allOrders.filtered(order -> order.getDate().equals(selectedDateString));
                orderTable.setItems(filteredOrders);
            } else {
                orderTable.setItems(allOrders);
            }
        });

        AnchorPane.setTopAnchor(orderHistoryLabel, 30.0);
        AnchorPane.setLeftAnchor(orderHistoryLabel, 50.0);

        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);

        AnchorPane.setTopAnchor(datePicker, 120.0);
        AnchorPane.setLeftAnchor(datePicker, 50.0);

        AnchorPane.setTopAnchor(orderTable, 170.0);
        AnchorPane.setLeftAnchor(orderTable, 50.0);
        AnchorPane.setRightAnchor(orderTable, 50.0);
        AnchorPane.setBottomAnchor(orderTable, 50.0);

        pane.getChildren().addAll(orderHistoryLabel, subtitleLabel, datePicker, orderTable);

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

        ArrayList<Book> books = new ArrayList<Book>();
        Book book1 = new Book(1, "Up", "Me", "Heavily used ", "Fiction", 3);
        Book book2 = new Book(2, "Diary of a Wimpy Kid", "IDK", "Lightly used", "Fiction", 3);
        Book book3 = new Book(3, "Magic Treehouse", "Not sure", "Moderately used", "Fiction", 3);
        books.add(book1);
        books.add(book2);
        books.add(book3);

        VBox bookTitles = new VBox();
        bookTitles.setSpacing(10.0);
        bookTitles.setStyle("-fx-font-size: 18");
        VBox bookConditions = new VBox();
        bookConditions.setSpacing(10.0);
        bookConditions.setStyle("-fx-font-size: 18");
        VBox bookPrice = new VBox();
        bookPrice.setSpacing(10.0);
        bookPrice.setStyle("-fx-font-size: 18");

        for (Book book : books) {
            Label title = new Label(book.getName());
            //System.out.println(book.getName());
            Label condition = new Label(book.getCondition());
            //System.out.println(book.getCondition());
            Label price = new Label("$10.00");
            //System.out.println(book.getPrice());
            bookTitles.getChildren().add(title);
            bookConditions.getChildren().add(condition);
            bookPrice.getChildren().add(price);
            if (condition.equals("Heavily used ")){

            }
        }

        //update database
        clearBag.setOnAction(e -> {});
        confirm.setOnAction(e -> {});

        pane.getChildren().addAll(cartLabel, line1, line2, priceLabels, buttons, bookTitles, bookConditions, bookPrice, titles, conditions, prices);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();

        AnchorPane.setTopAnchor(cartLabel, 30.0);
        AnchorPane.setLeftAnchor(cartLabel, mainScene.getWidth() / 3.25);

        AnchorPane.setTopAnchor(priceLabels, 600.0);
        AnchorPane.setLeftAnchor(priceLabels, mainScene.getWidth() / 1.8);

        AnchorPane.setTopAnchor(buttons, 700.0);
        AnchorPane.setLeftAnchor(buttons, mainScene.getWidth() / 1.7);

        AnchorPane.setTopAnchor(bookTitles, 120.0);
        AnchorPane.setLeftAnchor(bookTitles, 50.0);
        AnchorPane.setTopAnchor(bookConditions, 120.0);
        AnchorPane.setLeftAnchor(bookConditions, mainScene.getWidth() / 2.5);
        AnchorPane.setTopAnchor(bookPrice, 120.0);
        AnchorPane.setLeftAnchor(bookPrice, mainScene.getWidth() / 1.5);

        AnchorPane.setTopAnchor(titles, 75.0);
        AnchorPane.setLeftAnchor(titles, 50.0);
        AnchorPane.setTopAnchor(conditions, 75.0);
        AnchorPane.setLeftAnchor(conditions, mainScene.getWidth() / 2.5);
        AnchorPane.setTopAnchor(prices, 75.0);
        AnchorPane.setLeftAnchor(prices, mainScene.getWidth() / 1.5);

        pane.getStylesheets().add(css);
        return pane;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}