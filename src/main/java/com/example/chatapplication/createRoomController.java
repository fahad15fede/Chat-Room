package com.example.chatapplication;

import com.example.chatapplication.DAO.MembershipDAO;
import com.example.chatapplication.DAO.RoomDAO;
import com.example.chatapplication.Session.Membership;
import com.example.chatapplication.Session.Room;
import com.example.chatapplication.Session.UserSession;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;

public class createRoomController {

    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private ComboBox<String> AgeGroupDropDown;
    @FXML private ComboBox<String> LocationDropDown;
    @FXML private ComboBox<String> TypeDropdown;
    @FXML private CheckBox agreeConditionBox;
    @FXML private TextField maxMembers;
    @FXML private TextField roomDescription;
    @FXML private TextField roomId;
    @FXML private TextField roomName;
    @FXML private TextField roomPassKey;
    @FXML private Label roomPassKeyLabel;
    @FXML private Button createBtn;

    // regex
    private static final String ROOM_NAME_REGEX = "^[a-zA-Z0-9_ ]{3,30}$";
//    private static final String MAX_MEMBERS_REGEX = "^[0-9]{1,3}$";
    private static final String PASSKEY_REGEX = "^[a-zA-Z0-9@#$%]{4,}$";
    private static final String DESC_REGEX = "^.{0,200}$";

    @FXML
    public void initialize() {
        try {
            RoomDAO dao = new RoomDAO();
            long nextId = dao.getNextRoomId();
            roomId.setText(String.valueOf(nextId));
        } catch (Exception e) {
            e.printStackTrace();
            roomId.setText("Error");
        }

        // dropdowns
        AgeGroupDropDown.getItems().addAll("8-12", "13-19", "21-30", "30+");
        LocationDropDown.getItems().addAll( "United States",
                "Spain",
                "Pakistan",
                "Saudi Arabia",
                "United Arab Emirates",
                "India",
                "United Kingdom",
                "France",
                "Canada",
                "Germany",
                "Turkey",
                "Indonesia",
                "Malaysia",
                "Nigeria",
                "Egypt");
        TypeDropdown.getItems().addAll("Private", "Public");

        // hide passkey initially
        roomPassKey.setVisible(false);
        roomPassKeyLabel.setVisible(false);

        // disable button initially
        createBtn.setDisable(true);

        // toggle passkey field
        TypeDropdown.setOnAction(actionEvent -> {
            String selected = TypeDropdown.getValue();
            if ("Private".equals(selected)) {
                roomPassKey.setVisible(true);
                roomPassKeyLabel.setVisible(true);
            } else {
                roomPassKey.setVisible(false);
                roomPassKeyLabel.setVisible(false);
            }
        });

        // enable button only when conditions met
        agreeConditionBox.setOnAction(event -> validateForm());
    }

    private void validateForm() {
        boolean nameFilled = !roomName.getText().trim().isEmpty();
        boolean membersFilled = !maxMembers.getText().trim().isEmpty();
        boolean isPrivate = "Private".equals(TypeDropdown.getValue());
        boolean passKeyFilled = !roomPassKey.getText().trim().isEmpty();

        if (agreeConditionBox.isSelected() && nameFilled && membersFilled) {
            if (isPrivate && passKeyFilled) {
                createBtn.setDisable(false);
            } else if (!isPrivate) {
                createBtn.setDisable(false);
            } else {
                createBtn.setDisable(true);
            }
        } else {
            createBtn.setDisable(true);
        }
    }

    @FXML
    void backFromCreatePage(ActionEvent event) throws IOException {
        switchScene(event, "home.fxml", "Home Page");
    }

    @FXML
    void createRoomBtn(ActionEvent event) {
        long roomID = Long.parseLong(roomId.getText().trim());
        String name = roomName.getText().trim();
        String description = roomDescription.getText().trim();
        String type = TypeDropdown.getValue();
        String ageRange = AgeGroupDropDown.getValue();
        String location = LocationDropDown.getValue();
        String passKey = "Private".equals(type) ? roomPassKey.getText().trim() : null;

        int members;
        try {
            members = Integer.parseInt(maxMembers.getText().trim());
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Max members must be a valid number.");
            return;
        }

        // validations
        if (!name.matches(ROOM_NAME_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Room name must be 3–30 letters/numbers (underscores and spaces allowed).");
            return;
        }

        if (members < 2 || members > 100) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Max members must be between 2 and 100.");
            return;
        }

        if ("Private".equals(type) && !passKey.matches(PASSKEY_REGEX)) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Passkey must be at least 4 characters.");
            return;
        }

        if (!description.matches(DESC_REGEX)) {
            showAlert(Alert.AlertType.WARNING, "Too Long", "Description cannot exceed 200 characters.");
            return;
        }

        // ✅ Passed validation → insert into DB
        try {
            Room room = new Room(roomID, name, type, location, members, ageRange, description, passKey);
            RoomDAO dao = new RoomDAO();
            dao.insertRoom(room);

            //leader handling
            MembershipDAO mem = new MembershipDAO();
            Membership memb = new Membership(0, // membership_id will auto-increment
                    UserSession.getUserId(),
                    roomID,
                    true, // isActive
                    true, // isLeader
                    LocalDateTime.now());
            mem.addMembership(memb);

            showAlert(Alert.AlertType.INFORMATION, "Success", "Room created successfully!");

            // refresh next room ID for UI
            roomId.setText(String.valueOf(dao.getNextRoomId()));

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to create room in database.");
        }
    }
}
