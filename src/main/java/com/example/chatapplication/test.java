package com.example.chatapplication;

import com.example.chatapplication.DAO.javaDBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class test {
    private void switchScene(ActionEvent event, String fxmlFile, String title) throws IOException, IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFile));
        Scene scene = new Scene(fxmlLoader.load());
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle(title);
        stage.setScene(scene);
        stage.show();
    }


    public static void main(String[] args) throws SQLException {

        test testClass = new test();
        ActionEvent event;

        Connection conn = javaDBConnection.getConn();

        if (conn != null) {
            System.out.println("Database connection successful!");
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Failed to connect to database.");
        }
    }
}
