package com.example.chatapplication;

import com.example.chatapplication.DAO.RoomDAO;
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
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;

public class yourRoomsUI {

    @FXML
    private TableView<Room> yourRoomTable;

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
    private Button backBtn;

    private ObservableList<Room> roomList;

    @FXML
    public void initialize() {
        // Set up table columns
        colRoomID.setCellValueFactory(new PropertyValueFactory<>("roomID"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colType.setCellValueFactory(new PropertyValueFactory<>("type"));
        colLocation.setCellValueFactory(new PropertyValueFactory<>("location"));
        colMaxMembers.setCellValueFactory(new PropertyValueFactory<>("maxMembers"));
        colAgeRange.setCellValueFactory(new PropertyValueFactory<>("ageRange"));
        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));

        // Load rooms for current user
        int userId = UserSession.getUserId();
        roomList = FXCollections.observableArrayList(RoomDAO.getRoomsByUserId(userId));
        yourRoomTable.setItems(roomList);

        // Add Enter Room button for each room
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button enterBtn = new Button("Enter Room");

            {
                enterBtn.setOnAction(e -> {
                    Room room = getTableView().getItems().get(getIndex());
                    enterRoom(room);
                });
                enterBtn.setOnKeyPressed(event->{
                if (event.getCode() == KeyCode.ENTER) {
                    Room room = getTableView().getItems().get(getIndex());
                    enterRoom(room);
                }});

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : enterBtn);
            }
        });
    }

    private void enterRoom(Room room) {
        try {
            // Load the chat room UI
            FXMLLoader loader = new FXMLLoader(getClass().getResource("chatRoomUI.fxml"));
            Scene scene = new Scene(loader.load());

            // Pass room info to chatRoomUI controller
            chatRoomUI controller = loader.getController();
            controller.setRoom(room); // You need a setRoom method in chatRoomUI

            Stage stage = (Stage) yourRoomTable.getScene().getWindow();
            stage.setTitle("Chat Room: " + room.getName());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Could not enter the room.");
        }
    }

    @FXML
    void backtoHome(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("home.fxml"));
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Home Page");
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
}
