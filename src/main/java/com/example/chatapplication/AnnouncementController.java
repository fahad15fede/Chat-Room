package com.example.chatapplication;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class AnnouncementController {

    @FXML
    private TextArea announcementText;
    @FXML
    private Button editButton;
    @FXML
    private Button saveButton;

    private boolean editable;
    private String updatedText = null;

    @FXML
    public void initialize() {
        saveButton.setDisable(true);
    }

    public void setAnnouncements(String text) {
        announcementText.setText(text != null ? text : "No announcements yet.");
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
        editButton.setVisible(editable);
    }

    @FXML
    private void editDialog() {
        announcementText.setEditable(true);
        saveButton.setDisable(false);
    }

    @FXML
    private void onSaveClicked() {
        updatedText = announcementText.getText();
        closeDialog();
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) announcementText.getScene().getWindow();
        stage.close();
    }

    public String getUpdatedText() {
        return updatedText;
    }
}
