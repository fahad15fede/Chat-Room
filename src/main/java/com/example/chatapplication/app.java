package com.example.chatapplication;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class app extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("loginPage.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Registration Page");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
