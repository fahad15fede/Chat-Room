package com.example.chatapplication;

import com.example.chatapplication.DAO.RoomDAO;
import com.example.chatapplication.Session.Room;
import com.example.chatapplication.Session.UserSession;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class ruleDialogController {

    @FXML
    private TextArea rulesText;

    @FXML
    private Button editButton;

    @FXML
    private Button saveButton;

    private Room room;
    private boolean isLeader;

    // ✅ Set rules content
    public void setRules(String rules) {
        rulesText.setText(rules != null ? rules : "No rules defined yet.");
    }

    // ✅ Return updated rules text
    public String getUpdatedRules() {
        return rulesText.getText().trim();
    }

    // ✅ Control editability based on user role
    public void setEditable(boolean editable) {
        rulesText.setEditable(editable);
        editButton.setDisable(!editable);
        saveButton.setDisable(!editable);
    }

    // ✅ Initialize dialog with room info
    public void setRoom(Room room) {
        this.room = room;
        rulesText.setText(room.getRules() != null ? room.getRules() : "No rules defined yet.");

        isLeader = UserSession.getUsername().equals(room.getLeaderName());
        editButton.setDisable(!isLeader);
        saveButton.setDisable(true);
    }

    // ✅ Enable editing (leader only)
    @FXML
    private void editDialog() {
        if (isLeader) {
            rulesText.setEditable(true);
            editButton.setDisable(true);
            saveButton.setDisable(false);
        }
    }

    // ✅ Save updated rules
    @FXML
    private void onSaveClicked() {
        if (isLeader && room != null) {
            String updatedRules = rulesText.getText().trim();
            room.setRules(updatedRules);

            // Update in database
            RoomDAO.updateRoomRules(room.getRoomID(), updatedRules);

            rulesText.setEditable(false);
            saveButton.setDisable(true);
            editButton.setDisable(false);

            System.out.println("✅ Updated rules for room: " + room.getName());
        }
    }

    // ✅ Close the dialog window
    @FXML
    private void closeDialog() {
        Stage stage = (Stage) rulesText.getScene().getWindow();
        stage.close();
    }
}
