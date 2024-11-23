package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Error;
import com.example.cse360_project1.models.User;
import com.example.cse360_project1.services.JDBCConnection;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class UserSettingsPage {
    private User user;
    private final SceneController sceneController;

    public UserSettingsPage(User user, SceneController sceneController) {
        this.user = user;
        this.sceneController = sceneController;

    }

    public Scene getScene() {
        Scene mainScene = sceneController.getCurrentScene();
        sceneController.getStage().setTitle("BookBetter - Seller");
        AnchorPane root = new AnchorPane();

//        HBox pieChartBox = categoriesPieChart(user);
//        HBox conditionsPieChart = conditionsPieChart(user);
//        AnchorPane.setBottomAnchor(pieChartBox, 0.0);
//        AnchorPane.setBottomAnchor(conditionsPieChart, mainScene.getHeight() / 2.0);
//        root.getChildren().addAll(pieChartBox, conditionsPieChart);
        SidePanel sidePanelObject = new SidePanel(user, sceneController);
        AnchorPane sidePanel = sidePanelObject.getSidePanel();

        AnchorPane.setLeftAnchor(sidePanel, 0.0);
        AnchorPane.setTopAnchor(sidePanel, 0.0);
        AnchorPane.setBottomAnchor(sidePanel, 0.0);
        AnchorPane contentPane = contentPane(user);
        AnchorPane.setTopAnchor(contentPane, 0.0);
        AnchorPane.setLeftAnchor(contentPane, 200.0);
        AnchorPane.setBottomAnchor(contentPane, 0.0);
        root.getChildren().addAll(sidePanel, contentPane);
        Scene scene = new Scene(root, mainScene.getWidth(), mainScene.getHeight());

        return scene;
    }
    public AnchorPane contentPane(User user) {
        AnchorPane pane = new AnchorPane();
        Label accountSettingsLabel = new Label("Account Settings");
        accountSettingsLabel.getStyleClass().add("h1");
        accountSettingsLabel.setPadding(new Insets(20, 20, 20, 20));
        VBox personalBlurb = new VBox(0);
        personalBlurb.getStyleClass().add("blurb");
        personalBlurb.setPadding(new Insets(20, 20, 20, 20));

        HBox header = new HBox(20);
        Label blurbLabel = new Label("Personal information");
        blurbLabel.getStyleClass().add("h2");
        Button editButton = new Button("Edit");
        editButton.getStyleClass().add("secondary");
        editButton.getStyleClass().add("type-label");
        header.getChildren().addAll(blurbLabel, editButton);

        VBox userStack1 = new VBox(0);
        Label userLabel = new Label("Username");
        userLabel.getStyleClass().add("blurb-text");
        userLabel.getStyleClass().add("gray");

        Label userNameLabel = new Label(user.getName());
        userNameLabel.getStyleClass().add("blurb-text");
        userStack1.getChildren().addAll(userLabel, userNameLabel);

        Label idLabel = new Label("ID");
        idLabel.getStyleClass().add("blurb-text");
        idLabel.getStyleClass().add("gray");

        String userID = user.getId() + "";
        Label userIDLabel = new Label(userID);
        userIDLabel.getStyleClass().add("blurb-text");
        VBox userStack2 = new VBox(0);
        userStack2.getChildren().addAll(idLabel, userIDLabel);

        HBox userDetails = new HBox(75);
        userDetails.getChildren().addAll(userStack1, userStack2);

        Label userTypeLabel = new Label("Type");
        userTypeLabel.getStyleClass().add("blurb-text");
        userTypeLabel.getStyleClass().add("gray");

        Label typeLabel = new Label(user.getUserType().toUpperCase());
        typeLabel.getStyleClass().add("blurb-text");

        personalBlurb.getChildren().addAll(header, userDetails, userTypeLabel, typeLabel);

        VBox actionBlurb = new VBox(10);
        actionBlurb.setPadding(new Insets(20, 20, 20, 20));
        actionBlurb.getStyleClass().add("blurb");

        Label actionBlurbLabel = new Label("Action Center");
        actionBlurbLabel.getStyleClass().add("h2");

        TextField changePasswordField = new TextField();
        changePasswordField.setEditable(false);
        Label changePasswordLabel = new Label("Change password");
        changePasswordLabel.getStyleClass().add("blurb-text");
        changePasswordLabel.getStyleClass().add("gray");

        Button changePasswordButton = new Button("Change Password");
        changePasswordButton.setPrefWidth(150);
        changePasswordButton.setPrefHeight(35);

        changePasswordButton.getStyleClass().add("primary");

        Label deleteAccountLabel = new Label("Delete Account");
        deleteAccountLabel.getStyleClass().add("blurb-text");
        deleteAccountLabel.getStyleClass().add("gray");

        Button deleteAccountButton = new Button("Delete Account");
        deleteAccountButton.setPrefWidth(150);
        deleteAccountButton.setPrefHeight(35);
        deleteAccountButton.getStyleClass().add("secondary");

        actionBlurb.getChildren().addAll(actionBlurbLabel, changePasswordField, changePasswordLabel, changePasswordButton, deleteAccountLabel, deleteAccountButton);
        pane.getChildren().addAll(accountSettingsLabel, personalBlurb, actionBlurb);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(personalBlurb, 110.0);
        AnchorPane.setTopAnchor(accountSettingsLabel, 30.0);
        AnchorPane.setTopAnchor(actionBlurb, 405.0);

        AnchorPane.setLeftAnchor(actionBlurb, 50.0);
        AnchorPane.setLeftAnchor(accountSettingsLabel, 50.0);
        AnchorPane.setLeftAnchor(personalBlurb, 50.0);
        pane.getStylesheets().add(css);

        deleteAccountButton.setOnAction(e -> {
            JDBCConnection connection = new JDBCConnection();
            if (connection.deleteUser(user.getId())) {
                LoginRegisterPage loginRegisterPage = new LoginRegisterPage(sceneController);
                sceneController.switchScene(loginRegisterPage.getScene(sceneController.getCurrentScene()));
            }
        });

        changePasswordButton.setOnAction(e -> {
            if (!changePasswordField.isEditable()) {
                changePasswordField.setEditable(true);
            }

            if (changePasswordField.isEditable()) {
                if (changePasswordField.getText().equals("")) {
                    Error noNewPassword = new Error("Password error: No new password entered");
                    noNewPassword.displayError(pane, sceneController.getCurrentScene());
                } else if (changePasswordField.getText().equals(user.getPassword())) {
                    Error samePassword = new Error("Password error: Same password");
                    samePassword.displayError(pane, sceneController.getCurrentScene());
                } else {
                    JDBCConnection connection = new JDBCConnection();
                    if (connection.changePassword(user, changePasswordField.getText())) {
                        SellerView sellerView = new SellerView(user, sceneController);
                        sceneController.switchScene(sellerView.getScene());
                    } else {
                        Error deleteError = new Error("Error changing password");
                        deleteError.displayError(pane, sceneController.getCurrentScene());
                    }
                }
            }
        });
        return pane;
    }
}

