package com.example.chatapplication;

import com.example.chatapplication.DAO.MembershipDAO;
import com.example.chatapplication.DAO.RoomDAO;
import com.example.chatapplication.Session.Membership;
import com.example.chatapplication.Session.Room;
import com.example.chatapplication.Session.UserSession;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;


public class joinRoomController {

    @FXML
    private TableView<Room> joinRoomTable;
    @FXML
    private TableColumn<Room, String> colRoomID;
    @FXML
    private TableColumn<Room, String> colName;
    @FXML
    private TableColumn<Room, String> colType;
    @FXML
    private TableColumn<Room, String> colLocation;
    @FXML
    private TableColumn<Room, Integer> colMaxMembers;
    @FXML
    private TableColumn<Room, String> colAgeRange;
    @FXML
    private TableColumn<Room, String> colDescription;
    @FXML
    private TableColumn<Room, Void> colAction;

    @FXML
    private ComboBox<String> locationComboFilter;
    @FXML
    private ComboBox<String> memberLimitComboFilter;
    @FXML
    private ComboBox<String> TypeComboFilter;
    @FXML
    private ComboBox<String> age_groupComboFilter;

    @FXML
    private TextField searchBar;
    @FXML
    private Button searchbtn;

    private ObservableList<Room> roomList;

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    public void initialize() throws SQLException {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colMaxMembers.setCellValueFactory(new PropertyValueFactory<>("maxMembers"));
        colAgeRange.setCellValueFactory(new PropertyValueFactory<>("ageRange"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
        colRoomID.setCellValueFactory(new PropertyValueFactory<>("roomID"));

        roomList = FXCollections.observableArrayList(RoomDAO.getAllRooms());
        joinRoomTable.setItems(roomList);


        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button joinBtn = new Button("Join");
            private final Button leaveBtn = new Button("Leave");



            {
                joinBtn.setId("joinBtn");
                leaveBtn.setId("leaveBtn");
                joinBtn.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());
                    handleJoin(room);
                    getTableView().refresh(); // Refresh table after joining
                });

                leaveBtn.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());
                    handleLeave(room);
                    getTableView().refresh(); // Refresh table after leaving
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                Room room = getTableView().getItems().get(getIndex());
                int userId = UserSession.getUserId();

                try {
                    boolean alreadyMember = MembershipDAO.isUserAlreadyInRoom(userId, room.getRoomID());
                    setGraphic(alreadyMember ? leaveBtn : joinBtn);
                } catch (Exception e) {
                    e.printStackTrace();
                    setGraphic(joinBtn); // fallback
                }
            }
        });


        locationComboFilter.getItems().addAll(
                "USA", "Spain", "Pakistan", "Saudi Arabia", "United Arab Emirates",
                "India", "United Kingdom", "France", "Canada", "Germany",
                "Turkey", "Indonesia", "Malaysia", "Nigeria", "Egypt"
        );
        age_groupComboFilter.getItems().addAll("8-12", "13-19", "21-30", "30+");
        TypeComboFilter.getItems().addAll("Public", "Private");
        memberLimitComboFilter.getItems().addAll(
                "Up to 10 members", "Up to 25 members",
                "Up to 50 members", "Up to 100 members", "100 members or more"
        );

        locationComboFilter.setOnAction(e -> filterRooms());
        age_groupComboFilter.setOnAction(e -> filterRooms());
        TypeComboFilter.setOnAction(e -> filterRooms());
        memberLimitComboFilter.setOnAction(e -> filterRooms());
    }

    @FXML
    void search(ActionEvent event) {
        filterRooms();
    }

    private void filterRooms() {
        String searchText = searchBar.getText().toLowerCase().trim();
        String selectedLocation = locationComboFilter.getValue();
        String selectedType = TypeComboFilter.getValue();
        String selectedAge = age_groupComboFilter.getValue();
        String selectedMemberLimit = memberLimitComboFilter.getValue();

        ObservableList<Room> filteredList = roomList.filtered(room -> {
            boolean matchesSearch = searchText.isEmpty() ||
                    room.getName().toLowerCase().contains(searchText) ||
                    room.getDescription().toLowerCase().contains(searchText);

            boolean matchesLocation = (selectedLocation == null || selectedLocation.isEmpty()) ||
                    room.getLocation().equalsIgnoreCase(selectedLocation);

            boolean matchesType = (selectedType == null || selectedType.isEmpty()) ||
                    room.getType().equalsIgnoreCase(selectedType);

            boolean matchesAge = (selectedAge == null || selectedAge.isEmpty()) ||
                    room.getAgeRange().equalsIgnoreCase(selectedAge);

            boolean matchesLimit = true;
            if (selectedMemberLimit != null) {
                int max = room.getMaxMembers();
                switch (selectedMemberLimit) {
                    case "Up to 10 members" -> matchesLimit = max <= 10;
                    case "Up to 25 members" -> matchesLimit = max <= 25;
                    case "Up to 50 members" -> matchesLimit = max <= 50;
                    case "Up to 100 members" -> matchesLimit = max <= 100;
                    case "100 members or more" -> matchesLimit = max >= 100;
                }
            }

            return matchesSearch && matchesLocation && matchesType && matchesAge && matchesLimit;
        });

        joinRoomTable.setItems(filteredList);
    }

    @FXML
    void backtoHome(ActionEvent event) throws IOException {
        switchScene(event, "home.fxml", "Home Page");
    }

    @FXML
    void clearFilter(ActionEvent event) {
        locationComboFilter.setValue(null);
        memberLimitComboFilter.setValue(null);
        TypeComboFilter.setValue(null);
        age_groupComboFilter.setValue(null);
        searchBar.clear();
        joinRoomTable.setItems(roomList);
        showAlert(Alert.AlertType.INFORMATION, "Filters Cleared", "All filters have been reset.");
    }

    private void handleJoin(Room room) {
        System.out.println("Joining room: " + room.getName());

        if (room.getType().equalsIgnoreCase("Private")) {
            openPasswordDialog(room, true);
        } else {
            joinPrivateRoom(room); // same method used for both public/private joining
        }
    }
    private void handleLeave(Room room) {
        System.out.println("Leaving room: " + room.getName());

        if (room.getType().equalsIgnoreCase("Private")) {
            openPasswordDialog(room, false);
        } else {
            leavePrivateRoom(room); // same method used for both public/private joining
        }
    }

    private void openPasswordDialog(Room room, boolean isJoining) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("dialogPrivateRoomPassword.fxml"));
            DialogPane dialogPane = loader.load();

            dialogPrivateRoomPasswordController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Enter Room Passkey");

            dialog.showAndWait();

            if (controller.isConfirmed()) {
                String enteredKey = controller.getEnteredPasskey();

                if (enteredKey.equals(room.getPassKey())) {
                    if (isJoining)
                        joinPrivateRoom(room);
                    else
                        leavePrivateRoom(room);
                } else {
                    showAlert(Alert.AlertType.ERROR, "Invalid Passkey", "Incorrect passkey. Please try again.");
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ✅ Common join logic for both public and private rooms
    private void joinPrivateRoom(Room room) {
        int currentUserId = UserSession.getUserId();

        try {

            Membership membership = new Membership(
                    0,
                    currentUserId,
                    room.getRoomID(),
                    true,
                    false,
                    LocalDateTime.now()
            );

            MembershipDAO.addMembership(membership);

            showAlert(Alert.AlertType.INFORMATION, "Room Joined",
                    "You have successfully joined the room: " + room.getName());

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Something went wrong while joining the room. Please try again.");
        }
    }
    // ✅ Common join logic for both public and private rooms
    private void leavePrivateRoom(Room room) {
        int currentUserId = UserSession.getUserId();

        MembershipDAO.deactivateMembership(currentUserId,room.getRoomID());

        showAlert(Alert.AlertType.INFORMATION, "Room Left",
                "You have successfully left the room: " + room.getName());

    }
}
