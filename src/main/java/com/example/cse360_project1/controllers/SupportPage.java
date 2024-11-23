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
        Label supportLabel = new Label("Support");
        supportLabel.getStyleClass().add("h1");
        supportLabel.setPadding(new Insets(20, 20, 20, 20));

        pane.getChildren().addAll(supportLabel);
        String css = getClass().getResource("/com/example/cse360_project1/css/UserSettings.css").toExternalForm();
        AnchorPane.setTopAnchor(supportLabel, 30.0);
        AnchorPane.setLeftAnchor(supportLabel, 50.0);
        pane.getStylesheets().add(css);
        return pane;
    }
}
