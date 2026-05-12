package com.example.chatapplication;

import com.example.chatapplication.DAO.UserDAO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

public class registerController {

    // Regex constants
    private static final String USER_REGEX = "^[a-zA-Z0-9_]{3,15}$";
    private static final String PASS_REGEX = "^[a-zA-Z0-9@#$%]{4,}$";

    @FXML
    private TextField passField;

    @FXML
    private TextField rePassField;

    @FXML
    private TextField usernameField;

    @FXML
    private Label errorLabel;

    // Helper: reusable scene switch
    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    // Helper: show error in label
    private void showError(String message) {
        errorLabel.setText(message);
    }

    @FXML
    void sign_in(ActionEvent event) {
        String username = usernameField.getText() == null ? "" : usernameField.getText().trim();
        String password = passField.getText() == null ? "" : passField.getText().trim();
        String repass = rePassField.getText() == null ? "" : rePassField.getText().trim();

        if (!username.matches(USER_REGEX)) {
            showError("Invalid Username (3–15 letters, digits, or underscore)");
            return;
        }

        if (!password.matches(PASS_REGEX)) {
            showError("Password must be 4 chars with 1 letter, 1 digit, 1 special (@#$%)");
            return;
        }

        if (!password.equals(repass)) {
            showError("Passwords do not match");
            return;
        }

        boolean success = UserDAO.registerUser(username, password);
        if (success) {
            showError(""); // clear error
            System.out.println("User registered successfully!");
            try {
                switchScene(event, "loginPage.fxml", "Login Page");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            showError("Registration failed. Username may already exist.");
        }
    }

    @FXML
    void switchLogin(ActionEvent event) throws IOException {
        switchScene(event, "loginPage.fxml", "Login Page");
    }

}
