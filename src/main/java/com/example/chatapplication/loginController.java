package com.example.chatapplication;

import com.example.chatapplication.DAO.UserDAO;
import com.example.chatapplication.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.io.IOException;

public class loginController {

    // Regex constants
    private static final String USER_REGEX = "^[a-zA-Z0-9_]{3,15}$";
    private static final String PASS_REGEX = "^[a-zA-Z0-9@#$%]{4,}$";

    @FXML
    private TextField passwordLogin;

    @FXML
    private TextField usernameLogin;

    @FXML
    private Label errorLabel;


    @FXML
    public void initialize() {
        // Trigger login when Enter is pressed in the password field
        passwordLogin.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                event.consume(); // prevents default behavior
                handleLogin(new ActionEvent(passwordLogin, null));
            }
        });
    }


    private void showError(String message) {
        errorLabel.setText(message);
    }

    // Reusable scene switch method
    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    // ✅ Core login logic
    private void handleLogin(ActionEvent event) {
        String usernameLog = usernameLogin.getText().trim();
        String passwordLog = passwordLogin.getText().trim();

        if (!usernameLog.matches(USER_REGEX)) {
            showError("Incorrect Username.");
            return;
        }

        if (!passwordLog.matches(PASS_REGEX)) {
            showError("Incorrect password.");
            return;
        }

        boolean success = UserDAO.validateUser(usernameLog, passwordLog);
        if (success) {
            UserSession.startSession(UserSession.getUserId(), UserSession.getUsername());
            showError(""); // clear error
            System.out.println("Welcome " + UserSession.getUsername() + " !");
            try {
                switchScene(event, "home.fxml", "Home Page");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showError("Login failed. Try again.");
        }
    }

    // ✅ Triggered by button click
    @FXML
    void sign_up(ActionEvent event) {
        handleLogin(event);
    }

    // ✅ Triggered by pressing Enter key
    @FXML
    void sign_up_new(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume(); // optional, prevents default behavior
            handleLogin(new ActionEvent(event.getSource(), event.getTarget()));
        }
    }

    @FXML
    void switchRegister(ActionEvent event) throws IOException {
        switchScene(event, "registerPage.fxml", "Register Page");
    }
}
