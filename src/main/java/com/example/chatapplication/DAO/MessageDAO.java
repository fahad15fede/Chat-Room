package com.example.chatapplication.DAO;

import com.example.chatapplication.Session.Message;
import com.example.chatapplication.Session.UserSession;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MessageDAO {

    // ✅ Insert a new message into the database
    public static void insertMessage(Message message) {
        String sql = "INSERT INTO messages (room_id, user_id, msg_text, msg_type, sent_at) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, message.getRoomId());
            stmt.setInt(2, message.getSenderId());
            stmt.setString(3, message.getContent());
            stmt.setString(4, message.getMsgType());
            stmt.setTimestamp(5, message.getTimestamp());
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("❌ Error inserting message: " + e.getMessage());
        }
    }

    // ✅ Retrieve all messages from a specific room (ordered by sent_at)
    public static List<Message> getMessagesByRoom(long roomId) {
        List<Message> messages = new ArrayList<>();
        String sql = """
            SELECT m.msg_id, m.room_id, m.user_id, u.username, m.msg_text, m.msg_type, m.sent_at
            FROM messages m
            JOIN users u ON m.user_id = u.user_id
            WHERE m.room_id = ?
            ORDER BY m.sent_at ASC
        """;

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Message msg = new Message(
                        rs.getLong("msg_id"),
                        rs.getLong("room_id"),
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("msg_text"),
                        rs.getString("msg_type"),
                        rs.getTimestamp("sent_at")
                );
                messages.add(msg);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching messages: " + e.getMessage());
        }

        return messages;
    }

    // ✅ Delete all messages in a specific room (e.g., when deleting the room)
    public static void deleteMessagesByRoom(long roomId) {
        String sql = "DELETE FROM messages WHERE room_id = ?";
        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomId);
            stmt.executeUpdate();
            System.out.println("✅ Messages deleted for room ID: " + roomId);

        } catch (SQLException e) {
            System.err.println("❌ Error deleting messages: " + e.getMessage());
        }
    }

    // ✅ Get the latest announcement for a room
    public static String getLatestAnnouncement(long roomID) {
        String sql = """
            SELECT msg_text 
            FROM messages 
            WHERE room_id = ? AND msg_type = 'announcement'
            ORDER BY sent_at DESC 
            LIMIT 1
        """;
        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("msg_text");
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching announcement: " + e.getMessage());
        }

        return null;
    }

    // ✅ Add a new announcement (as a message with msg_type = 'announcement')
    public static void updateRoomAnnouncement(long roomId, String text) {
        String sql = """
            INSERT INTO messages (room_id, user_id, msg_text, msg_type, sent_at)
            VALUES (?, ?, ?, 'announcement', CURRENT_TIMESTAMP)
        """;
        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomId);
            stmt.setInt(2, UserSession.getUserId());
            stmt.setString(3, text);
            stmt.executeUpdate();

            System.out.println("📢 Announcement updated for room " + roomId);

        } catch (SQLException e) {
            System.err.println("❌ Error updating announcement: " + e.getMessage());
        }
    }
}
