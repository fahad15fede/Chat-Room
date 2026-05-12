package com.example.chatapplication.Session;

public class UserSession {
    private static int userId;
    private static String username;

    // private constructor prevents accidental instantiation
    private UserSession() {}

    // Start session
    public static void startSession(int id, String user) {
        userId = id;
        username = user;
    }

    // Getters
    public static int getUserId() {
        return userId;
    }

    public static String getUsername() {
        return username;
    }

    // End session
    public static void clearSession() {
        userId = 0;
        username = null;
    }
}
