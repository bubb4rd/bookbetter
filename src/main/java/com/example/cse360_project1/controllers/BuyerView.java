package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Book;
import com.example.cse360_project1.models.User;
import com.example.cse360_project1.services.JDBCConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

import java.sql.SQLException;
import java.util.ArrayList;

public class BuyerView {
    private final User user;
    private final SceneController sceneController;
    private String tab;

    public BuyerView(User user, SceneController sceneController) {
        this.user = user;
        this.sceneController = sceneController;
        this.tab = "BROWSE";
    }

    public Scene getScene() throws SQLException {
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

    private AnchorPane getContentPane(Scene mainScene) throws SQLException {
        switch (tab) {
            case "BROWSE":
                return getBrowseSection(mainScene);
            case "ORDERS":
                return getOrderHistory(mainScene);
            default:
                return getBrowseSection(mainScene);
        }
    }

    public AnchorPane getBrowseSection(Scene mainScene) throws SQLException {
        AnchorPane pane = new AnchorPane();
        Label booksLabel = new Label("Today's Books");
        booksLabel.getStyleClass().add("h1");
        booksLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("Browse and purchase books");

        pane.getChildren().addAll(booksLabel, subtitleLabel);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(booksLabel, 30.0);
        AnchorPane.setLeftAnchor(booksLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        pane.getStylesheets().add(css);

        JDBCConnection connection = new JDBCConnection();
        ArrayList<Book> books = connection.getAllBooks();
        for (Book book : books) {
            System.out.println(book.toString());
        }
        return pane;
    }

    public AnchorPane getOrderHistory(Scene mainScene) {
        AnchorPane pane = new AnchorPane();
        Label orderHistoryLabel = new Label("Order History");
        orderHistoryLabel.getStyleClass().add("h1");
        orderHistoryLabel.setPadding(new Insets(20, 20, 20, 20));
        Label subtitleLabel = new Label("View your past orders");

        pane.getChildren().addAll(orderHistoryLabel, subtitleLabel);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(orderHistoryLabel, 30.0);
        AnchorPane.setLeftAnchor(orderHistoryLabel, 50.0);
        AnchorPane.setTopAnchor(subtitleLabel, 75.0);
        AnchorPane.setLeftAnchor(subtitleLabel, 50.0);
        pane.getStylesheets().add(css);
        return pane;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
}