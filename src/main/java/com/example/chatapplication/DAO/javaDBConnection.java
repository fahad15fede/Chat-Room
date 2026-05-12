package com.example.chatapplication.DAO;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class javaDBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/chatApp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "fahad15fede";

    // Always return a new connection
    public static Connection getConn() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
