package com.example.cse360_project1.controllers;

import com.example.cse360_project1.models.Error;
import com.example.cse360_project1.models.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class SupportPage {
    private User user;
    private final SceneController sceneController;

    public SupportPage(User user, SceneController sceneController) {
        this.sceneController = sceneController;
        this.user = user;
    }
    public Scene getScene() {
        Scene mainScene = sceneController.getCurrentScene();
        sceneController.getStage().setTitle("BookBetter - Support");
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
        AnchorPane contentPane = contentPane(mainScene, user);
        AnchorPane.setTopAnchor(contentPane, 0.0);
        AnchorPane.setLeftAnchor(contentPane, 200.0);
        AnchorPane.setBottomAnchor(contentPane, 0.0);
        root.getChildren().addAll(sidePanel, contentPane);
        Scene scene = new Scene(root, mainScene.getWidth(), mainScene.getHeight());

        return scene;
    }
    public AnchorPane contentPane(Scene mainScene, User user) {
        AnchorPane pane = new AnchorPane();
        Label supportLabel = new Label("Support");
        supportLabel.getStyleClass().add("h1");
        supportLabel.setPadding(new Insets(20, 20, 20, 20));

        VBox formBox = new VBox(15);
        formBox.setPadding(new Insets(20));
        formBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");

        //email field
        Label emailLabel = new Label("Email");
        emailLabel.getStyleClass().add("h3");
        TextField emailField = new TextField();
        emailField.setPromptText("Enter your email");

        //ID field
        Label userIdLabel = new Label("User ID");
        userIdLabel.getStyleClass().add("h3");
        TextField userIdField = new TextField();
        userIdField.setPromptText("Enter your user ID");

        //FAQs
        Label topicLabel = new Label("Help Topic");
        topicLabel.getStyleClass().add("h3");
        ComboBox<String> topicDropdown = new ComboBox<>();
        topicDropdown.getItems().addAll(
                "Technical Issue",
                "Billing Inquiry",
                "Account Issue",
                "Other"
        );

        topicDropdown.setPromptText("Please select a topic");

        //Problem description field
        Label additionalInfoLabel = new Label("Additional Information");
        additionalInfoLabel.getStyleClass().add("h3");
        TextArea additionalInfoField = new TextArea();
        additionalInfoField.setPromptText("Please provide any additional information about your problem here");
        additionalInfoField.setPrefHeight(100);
        //Submit form button
        Button submitButton = new Button("Submit Form");
        submitButton.getStyleClass().add("primary");
        submitButton.setOnAction(e ->{
            String email = emailField.getText();
            String userId = userIdField.getText();
            String topic = topicDropdown.getValue();
            String additionalInfo = additionalInfoField.getText();

            if (email.isEmpty() || userId.isEmpty() || topic == null || additionalInfo.isEmpty()) {
                Error emptyFieldError = new Error("Please fill in all fields");
                emptyFieldError.displayError(pane, mainScene);
            }
            else{
                Error successError = new Error("Support Form Submitted");
                successError.displayError(pane, mainScene);
                emailField.clear();
                userIdField.clear();
                topicDropdown.setValue(null);
                additionalInfoField.clear();
            }
        });
        formBox.getChildren().addAll(
                emailLabel, emailField,
                userIdLabel, userIdField,
                topicLabel, topicDropdown,
                additionalInfoLabel, additionalInfoField,
                submitButton
        );

        pane.getChildren().addAll(supportLabel, formBox);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(supportLabel, 20.0);
        AnchorPane.setLeftAnchor(supportLabel, 50.0);
        pane.getStylesheets().add(css);
        return pane;
    }
}
