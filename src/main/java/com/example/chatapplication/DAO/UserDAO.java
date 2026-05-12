package com.example.chatapplication.DAO;

import com.example.chatapplication.Session.UserSession;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    // Register new user
    public static boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users(username, password) VALUES (?, ?)";

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;

        } catch (SQLException e) {
            // If username is duplicate (unique constraint), handle gracefully
            if (e.getSQLState().equals("23505")) { // PostgreSQL unique_violation
                System.out.println("⚠️ Username already exists.");
            } else {
                e.printStackTrace();
            }
            return false;
        }
    }

    // Validate user for login
    public static boolean validateUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username=? AND password=?";

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {  // ✅ only create session if user found
                UserSession.startSession(rs.getInt("user_id"), rs.getString("username"));
                return true;
            } else {
                return false; // login failed
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
