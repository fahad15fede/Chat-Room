package com.example.chatapplication;

import com.example.chatapplication.DAO.RoomDAO;
import com.example.chatapplication.DAO.MessageDAO;
import com.example.chatapplication.Session.Message;
import com.example.chatapplication.Network.Client;
import com.example.chatapplication.Session.Room;
import com.example.chatapplication.Session.UserSession;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Stack;

public class chatRoomUI {

    private Room currentRoom;
    private Client client;
    private final String username = UserSession.getUsername();
    private final int userId = UserSession.getUserId();

    @FXML private VBox chatBox;
    @FXML private ScrollPane chatScroll; // ✅ added missing field
    @FXML private TextField textSendField;
    @FXML private Label title;
    @FXML private Label onlineShow;
    @FXML private StackPane mainContent;

    @FXML
    public void initialize() {
        try {
            client = new Client("localhost", 5000, username);
            client.connect();
            client.setMessageListener(this::addReceivedMessage); // ✅ handles String messages
        } catch (IOException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }


    }

        // ✅ Load room info and message history
        public void setRoom(Room room) {
            this.currentRoom = room;
            title.setText(currentRoom.getName());

            List<Message> history = MessageDAO.getMessagesByRoom(currentRoom.getRoomID());
            for (Message msg : history) {
                if ("announcement".equalsIgnoreCase(msg.getMsgType())){
                    continue;
                }
                else if (msg.getSenderId() == userId)
                    addSentMessage(msg);
                else
                    addReceivedMessage(msg);
            }

            // 🔽 Auto-scroll to bottom after loading
            Platform.runLater(() -> {
                chatScroll.layout();
                chatScroll.setVvalue(1.0);
            });
        }

    // ✅ Common send logic
    private void sendMessage() {
        String msg = textSendField.getText().trim();
        if (msg.isEmpty()) return;

        client.sendMessage(username + ": " + msg);

        Message message = new Message(
                0,
                currentRoom.getRoomID(),
                userId,
                username,
                msg,
                new Timestamp(System.currentTimeMillis())
        );
        MessageDAO.insertMessage(message);
        addSentMessage(message);

        textSendField.clear();
    }

    // ✅ Triggered by button click
    @FXML
    void sendMsg(ActionEvent event) {
        sendMessage();
    }

    // ✅ Triggered by Enter key
    @FXML
    void sendMsg(KeyEvent event) {
        if (event.getCode() == KeyCode.ENTER) {
            event.consume();
            sendMessage();
        }
    }


    // ✅ Sent message UI
    private void addSentMessage(Message msg) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(msg.getContent());
            msgLabel.getStyleClass().add("message-sent");
            msgLabel.setPrefWidth(600);
            msgLabel.setWrapText(true);
            VBox.setMargin(msgLabel, new Insets(10, 10, 0, 10));

            String timeOnly = new SimpleDateFormat("HH:mm").format(msg.getTimestamp());
            Label timeLabel = new Label(timeOnly);
            timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
            timeLabel.setAlignment(Pos.CENTER_RIGHT);

            String fullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(msg.getTimestamp());
            Tooltip tooltip = new Tooltip(fullDate);
            tooltip.setStyle("-fx-font-size: 11px; -fx-background-color: #222; -fx-text-fill: white; -fx-padding: 4;");
            Tooltip.install(msgLabel, tooltip);
            Tooltip.install(timeLabel, tooltip);

            VBox messageBox = new VBox(msgLabel, timeLabel);
            messageBox.setAlignment(Pos.CENTER_RIGHT);

            Circle circle = new Circle(18, Color.web("#FFD600"));
            Label initial = new Label(msg.getSenderUsername().substring(0, 1).toUpperCase());
            initial.setTextFill(Color.ROYALBLUE);
            initial.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            StackPane icon = new StackPane(circle, initial);

            HBox container = new HBox(10, messageBox, icon);
            container.setAlignment(Pos.CENTER_RIGHT);
            VBox.setMargin(container, new Insets(10, 10, 0, 10));
            chatBox.getChildren().add(container);

            // 🔽 Auto-scroll
            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        });
    }

    // ✅ Received message UI (DB-based)
    private void addReceivedMessage(Message msg) {
        Platform.runLater(() -> {
            Label msgLabel = new Label(msg.getSenderUsername() + ": " + msg.getContent());
            msgLabel.getStyleClass().add("message-received");
            msgLabel.setPrefWidth(600);
            msgLabel.setWrapText(true);
            VBox.setMargin(msgLabel, new Insets(10, 10, 0, 10));

            String timeOnly = new SimpleDateFormat("HH:mm").format(msg.getTimestamp());
            Label timeLabel = new Label(timeOnly);
            timeLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-padding: 0 0 0 10;");

            String fullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(msg.getTimestamp());
            Tooltip tooltip = new Tooltip(fullDate);
            tooltip.setStyle("-fx-font-size: 11px; -fx-background-color: #222; -fx-text-fill: white; -fx-padding: 4;");
            Tooltip.install(msgLabel, tooltip);
            Tooltip.install(timeLabel, tooltip);

            VBox messageBox = new VBox(msgLabel, timeLabel);
            messageBox.setAlignment(Pos.CENTER_LEFT);

            Circle circle = new Circle(18, Color.web("#1E90FF"));
            Label initial = new Label(msg.getSenderUsername().substring(0, 1).toUpperCase());
            initial.setTextFill(Color.WHITE);
            initial.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            StackPane icon = new StackPane(circle, initial);

            HBox container = new HBox(10, icon, messageBox);
            container.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(container, new Insets(10, 10, 0, 10));
            chatBox.getChildren().add(container);

            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        });
    }

    // ✅ Real-time received messages (from server)
    private void addReceivedMessage(String raw) {
        Platform.runLater(() -> {
            if (raw == null || raw.isEmpty()) return;

            String sender;
            String content;
            int sepIndex = raw.indexOf(": ");
            if (sepIndex != -1) {
                sender = raw.substring(0, sepIndex);
                content = raw.substring(sepIndex + 2);
            } else {
                sender = "Unknown";
                content = raw;
            }

            Timestamp now = new Timestamp(System.currentTimeMillis());
            String timeOnly = new SimpleDateFormat("HH:mm").format(now);
            String fullDate = new SimpleDateFormat("yyyy-MM-dd HH:mm").format(now);

            Label msgLabel = new Label(sender + ": " + content);
            msgLabel.getStyleClass().add("message-received");
            msgLabel.setPrefWidth(600);
            msgLabel.setWrapText(true);
            msgLabel.setPadding(new Insets(5));

            Label timeLabel = new Label(timeOnly);
            timeLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

            Tooltip tooltip = new Tooltip(fullDate);
            tooltip.setStyle("-fx-font-size: 11px; -fx-background-color: #222; -fx-text-fill: white; -fx-padding: 4;");
            Tooltip.install(msgLabel, tooltip);
            Tooltip.install(timeLabel, tooltip);

            VBox messageBox = new VBox(msgLabel, timeLabel);
            messageBox.setAlignment(Pos.CENTER_LEFT);

            Circle circle = new Circle(18, Color.web("#1E90FF"));
            Label initial = new Label(sender.substring(0, 1).toUpperCase());
            initial.setTextFill(Color.WHITE);
            initial.setStyle("-fx-font-weight: bold; -fx-font-size: 18px;");
            StackPane icon = new StackPane(circle, initial);

            HBox container = new HBox(10, icon, messageBox);
            container.setAlignment(Pos.CENTER_LEFT);
            VBox.setMargin(container, new Insets(10, 10, 0, 10));
            chatBox.getChildren().add(container);

            chatScroll.layout();
            chatScroll.setVvalue(1.0);
        });
    }

    // ✅ Leave room
    @FXML
    void leaveRoom(ActionEvent event) throws IOException {
        if (client != null) client.closeConnection();
        switchScene(event, "yourRoomsUI.fxml", "Select a room to join");
    }

    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }

    @FXML private Stage openDialogStage;

    @FXML
    void announcementsPage(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("announcementDialog.fxml"));
            Scene scene = new Scene(loader.load());

            AnnouncementController controller = loader.getController();

            // 🟡 Fetch latest announcement for this room
            String announcementText = MessageDAO.getLatestAnnouncement(currentRoom.getRoomID());
            controller.setAnnouncements(announcementText);

            // 🟢 Only leader can edit
            boolean isLeader = UserSession.getUsername().equals(currentRoom.getLeaderName());
            controller.setEditable(isLeader);

            // 🪟 Open dialog
            Stage dialog = new Stage();
            dialog.setTitle("Room Announcements");
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            // 💾 If leader updated the announcement
            String updatedText = controller.getUpdatedText();
            if (updatedText != null && isLeader) {
                MessageDAO.updateRoomAnnouncement(currentRoom.getRoomID(), updatedText);
                System.out.println("📢 Announcement updated: " + updatedText);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @FXML
    void generalPage(ActionEvent event) {
        if (openDialogStage != null && openDialogStage.isShowing()) {
            openDialogStage.close();
            openDialogStage = null;
        }
    }

    @FXML
    void settings(ActionEvent event) { }

    @FXML
    private void rulesPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ruleDialog.fxml"));
            Scene scene = new Scene(loader.load());

            ruleDialogController controller = loader.getController();
            controller.setRules(currentRoom.getRules());
            controller.setEditable(UserSession.getUsername().equals(currentRoom.getLeaderName()));

            Stage dialog = new Stage();
            dialog.setTitle("Room Rules");
            dialog.setScene(scene);
            dialog.setResizable(false);
            dialog.showAndWait();

            String updatedRules = controller.getUpdatedRules();
            if (updatedRules != null && !updatedRules.equals(currentRoom.getRules())) {
                currentRoom.setRules(updatedRules);
                RoomDAO.updateRoomRules(currentRoom.getRoomID(), updatedRules);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadView(String fxmlFile) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
        Node view = loader.load();
        mainContent.getChildren().setAll(view);
    }
}
