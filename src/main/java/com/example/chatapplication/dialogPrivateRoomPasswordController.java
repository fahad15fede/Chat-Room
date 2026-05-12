package com.example.chatapplication;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.PasswordField;

public class dialogPrivateRoomPasswordController {

    @FXML
    private PasswordField passkeyField;

    @FXML
    private DialogPane dialogPane; // Add this to reference the root if needed

    private String enteredPasskey;
    private boolean confirmed = false;

    // Called when user presses "Join"
    @FXML
    void onJoin(ActionEvent event) {
        enteredPasskey = passkeyField.getText().trim();

        if (enteredPasskey.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Passkey");
            alert.setHeaderText(null);
            alert.setContentText("Please enter the room passkey to join.");
            alert.showAndWait();
            return;
        }

        confirmed = true;
        closeDialog();
    }

    // Called when user presses "Cancel"
    @FXML
    void onCancel(ActionEvent event) {
        confirmed = false;
        enteredPasskey = null;
        closeDialog();
    }

    private void closeDialog() {
        // ✅ Close the parent Dialog properly
        dialogPane.getScene().getWindow().hide();
    }

    // Getters
    public boolean isConfirmed() {
        return confirmed;
    }

    public String getEnteredPasskey() {
        return enteredPasskey;
    }
}
