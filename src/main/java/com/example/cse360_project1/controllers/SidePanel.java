package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.ArrayList;

public class SidePanel {
    private final User user;
    private final SceneController sceneController;
    public SidePanel(User user, SceneController sceneController) {
        this.sceneController = sceneController;
        this.user = user;
    }
    public AnchorPane getSidePanel() {
        AnchorPane sidePanel = new AnchorPane();
        String imagePath = getClass().getResource("/com/example/cse360_project1/images/BookBetterCard.png").toExternalForm();
        Image BookBetterCard = new Image(imagePath, 155.0, 30.0, true, true);
        ImageView bookBetterImageView = new ImageView(BookBetterCard);
        bookBetterImageView.setFitWidth(155.0);
        bookBetterImageView.setFitHeight(30.0);

//        sidePanel.setAlignment(Pos.TOP_CENTER);
        sidePanel.setPrefWidth(200);
//        sidePanel.setSpacing(20);
        sidePanel.setPadding(new Insets(20, 20, 20, 0));
        sidePanel.setPrefHeight(1280);
        sidePanel.getStyleClass().add("gray-sidebar");

        VBox generalArea = new VBox(10);
        generalArea.setPadding(new Insets(20, 20, 20, 20));
        Label generalLabel = new Label("General".toUpperCase());
        generalArea.getChildren().add(generalLabel);
        if (user.getUserType().equals("SELLER")) {
            Button dashboard = new Button("Dashboard");
            dashboard.getStyleClass().add("sidepanel-button");
            if (selectDefault("BookBetter - Login")) dashboard.getStyleClass().add("selected");
            Button list = new Button("List a book");
            list.getStyleClass().add("sidepanel-button");
            Button editListing = new Button("Edit listing");
            editListing.getStyleClass().add("sidepanel-button");
            Button transactions = new Button("Transactions");
            transactions.getStyleClass().add("sidepanel-button");

            SellerView sellerView = new SellerView(user, sceneController);

            dashboard.setOnAction(e -> {
                sellerView.setTab("DASHBOARD");
                sceneController.switchScene(sellerView.getScene());
            });

            list.setOnAction(e -> {
                sellerView.setTab("LIST");
                sceneController.switchScene(sellerView.getScene());
            });

            editListing.setOnAction(e -> {
                sellerView.setTab("EDIT LISTINGS");
                sceneController.switchScene(sellerView.getScene());
            });

            transactions.setOnAction(e -> {
                sellerView.setTab("TRANSACTIONS");
                sceneController.switchScene(sellerView.getScene());
            });

            generalArea.getChildren().addAll(dashboard, list, editListing, transactions);
        }
        else if (user.getUserType().equals("ADMIN")) {
            Button dashboard = new Button("Dashboard");
            dashboard.getStyleClass().add("sidepanel-button");
            if (selectDefault("BookBetter - Login")) dashboard.getStyleClass().add("selected");

            Button orders = new Button("Orders");
            orders.getStyleClass().add("sidepanel-button");

            Button users = new Button("Users");
            users.getStyleClass().add("sidepanel-button");

            Button books = new Button("Books");
            books.getStyleClass().add("sidepanel-button");

            AdminView adminView = new AdminView(user, sceneController);

            dashboard.setOnAction(e -> {
                adminView.setTab("DASHBOARD");
                sceneController.switchScene(adminView.getScene());
            });

            orders.setOnAction(e -> {
                adminView.setTab("ORDERS");
                sceneController.switchScene(adminView.getScene());
            });

            users.setOnAction(e -> {
                adminView.setTab("USERS");
                sceneController.switchScene(adminView.getScene());
            });

            books.setOnAction(e -> {
                adminView.setTab("BOOKS");
                sceneController.switchScene(adminView.getScene());
            });

            generalArea.getChildren().addAll(dashboard, orders, users, books);
        } else if (user.getUserType().equals("BUYER")) {
            Button browse = new Button("Browse");
            browse.getStyleClass().add("sidepanel-button");
            if (selectDefault("BookBetter - Login")) browse.getStyleClass().add("selected");

            Button orderHistory = new Button("Order History");
            orderHistory.getStyleClass().add("sidepanel-button");

            BuyerView buyerView = new BuyerView(user, sceneController);

            browse.setOnAction(e -> {
                buyerView.setTab("BROWSE");
                    sceneController.switchScene(buyerView.getScene());

            });

            orderHistory.setOnAction(e -> {
                buyerView.setTab("ORDERS");
                    sceneController.switchScene(buyerView.getScene());

            });

            generalArea.getChildren().addAll(browse, orderHistory);
        }
        VBox supportArea = new VBox(10);
        supportArea.setPadding(new Insets(20, 20, 20, 20));
        Label supportLabel = new Label("Support".toUpperCase());

        Button settingsButton = new Button("Settings");
        settingsButton.getStyleClass().add("sidepanel-button");
        settingsButton.setOnAction(e -> {
            UserSettingsPage userSettingsPage = new UserSettingsPage(user, sceneController);
            sceneController.switchScene(userSettingsPage.getScene());
        });

        Button supportButton = new Button("Support");
        supportButton.getStyleClass().add("sidepanel-button");
        supportButton.setOnAction(e -> {
            SupportPage supportPage = new SupportPage(user, sceneController);
            sceneController.switchScene(supportPage.getScene());
        });
        supportArea.getChildren().addAll(generalArea, supportLabel, supportButton, settingsButton);

        VBox userArea = new VBox(5);
        if (user.getUserType().equals("ADMIN")) {
            userArea.setPadding(new Insets(20, 20, 20, 20));

        } else {
            userArea.setPadding(new Insets(20, 20, 20, 20));
        }
        Label userLabel = new Label(user.getName());

        userLabel.getStyleClass().add("focus");

        Label typeLabel = new Label(user.getUserType().toUpperCase());
        typeLabel.getStyleClass().add("type-label");

        Button logOutButton = new Button("Log out");
        logOutButton.getStyleClass().add("sidepanel-button");
        logOutButton.getStyleClass().add("secondary");
        userArea.getChildren().addAll(userLabel, typeLabel, logOutButton);

        sidePanel.getChildren().addAll(bookBetterImageView, generalArea, supportArea, userArea);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        sidePanel.getStylesheets().add(css);
        AnchorPane.setTopAnchor(bookBetterImageView, 20.0);
        AnchorPane.setTopAnchor(generalArea, 60.0);
        AnchorPane.setTopAnchor(supportArea, (80 + (generalArea.getChildren().size() * 40) + 24.0));
        userArea.setAlignment(Pos.CENTER);
        AnchorPane.setLeftAnchor(bookBetterImageView, 20.0);
        AnchorPane.setLeftAnchor(userArea, 25.0);

        AnchorPane.setBottomAnchor(userArea, 20.0);

        logOutButton.setOnAction(e -> {
            LoginRegisterPage loginRegisterPage = new LoginRegisterPage(sceneController);
            sceneController.switchScene(loginRegisterPage.getScene(sceneController.getCurrentScene()));
        });


        return sidePanel;
    }
    // selectDefault: if the Stage in sceneController is "BookBetter - Login", then the program knows it should send the user to its default page i.e. seller: dashboard,
    // buyer: browse, etc.
    private boolean selectDefault(String title) {
        return sceneController.getStage().getTitle().equals(title);
    }
}
