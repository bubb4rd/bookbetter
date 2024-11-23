package com.example.cse360_project1;

import com.example.cse360_project1.controllers.LoginRegisterPage;
import com.example.cse360_project1.controllers.SceneController;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {
    public static SceneController sceneController;
    static Scene userInfoScene;
    @Override
    public void start(Stage primaryStage) {

        VBox mainLayout = new VBox();
        Button changeButton = new Button("Go to User Info");
        mainLayout.getChildren().add(changeButton);
        Scene mainScene = new Scene(mainLayout, 1150, 800);
        primaryStage.setResizable(true);
        mainLayout.prefWidthProperty().bind(mainScene.widthProperty());
        mainLayout.prefHeightProperty().bind(mainScene.heightProperty());

        primaryStage.setTitle("BookBetter - Login");
        primaryStage.setScene(mainScene);
        sceneController = new SceneController(primaryStage);
        primaryStage.show();

        LoginRegisterPage loginRegisterPage = new LoginRegisterPage(sceneController);
        Scene loginRegisterScene = loginRegisterPage.getScene(mainScene);
        sceneController.switchScene(loginRegisterScene);
    }

    public static void main(String[] args) {
        launch();
    }
}