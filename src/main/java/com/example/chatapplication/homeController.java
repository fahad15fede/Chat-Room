package com.example.chatapplication;

import com.example.chatapplication.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;

public class homeController {
    // Reusable scene switch method
    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private Button createRoom;

    @FXML
    private Button enterRoom;

    @FXML
    private Button helpButton;

    @FXML
    private Button joinRoom;

    @FXML
    private Label welcomeText;

    @FXML
    private Label welcomeText1;


    @FXML
    public void initialize() {
        // Set welcome text with logged-in username
        welcomeText.setText("Welcome, " + UserSession.getUsername() + "!");
    }

    @FXML
    void createRoomPage(ActionEvent event) throws IOException {

        switchScene(event, "createRoom.fxml", "Create Your Room");

    }

    @FXML
    void enterRoomPage(ActionEvent event) throws  IOException{
        switchScene(event, "yourRoomsUI.fxml", "Enter Your Desired Room");
    }

    @FXML
    void helpTempalate(ActionEvent event) {

    }

    @FXML
    void joinRoomPage(ActionEvent event) throws IOException {
        switchScene(event, "joinRoom.fxml", "Join a Room");
    }

    @FXML
    void logout(ActionEvent event) throws IOException {

        switchScene(event, "loginPage.fxml", "Login Page");

    }

}
