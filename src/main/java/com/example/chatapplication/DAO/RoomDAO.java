package com.example.chatapplication.DAO;

import com.example.chatapplication.Session.Room;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoomDAO {

    // ✅ Fetch all rooms (with rules & announcements)
    public static List<Room> getAllRooms() {
        List<Room> rooms = new ArrayList<>();
        String query = """
            SELECT r.room_id, r.room_name, r.room_type, r.location, r.max_members,
                   r.age_range, r.room_description, r.room_passkey, r.rules,
                   u.username AS leader_name
            FROM rooms r
            JOIN membership m ON r.room_id = m.room_id
            JOIN users u ON m.user_id = u.user_id
            WHERE m.is_leader = TRUE
        """;

        try (Connection conn = javaDBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Room room = new Room(
                        rs.getLong("room_id"),
                        rs.getString("room_name"),
                        rs.getString("room_type"),
                        rs.getString("location"),
                        rs.getInt("max_members"),
                        rs.getString("age_range"),
                        rs.getString("room_description"),
                        rs.getString("room_passkey"),
                        rs.getString("rules"),
                        rs.getString("leader_name")
                );
                rooms.add(room);
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching rooms: " + e.getMessage());
        }

        return rooms;
    }

    // ✅ Get next room ID
    public int getNextRoomId() {
        String sql = "SELECT COALESCE(MAX(room_id), 0) + 1 AS next_id FROM rooms";
        try (Connection conn = javaDBConnection.getConn();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) return rs.getInt("next_id");

        } catch (SQLException e) {
            System.err.println("❌ Error fetching next room ID: " + e.getMessage());
        }
        return 1;
    }

    // ✅ Insert new room (includes announcements)
    public void insertRoom(Room room) throws SQLException {
        String sql = """
            INSERT INTO rooms (room_id, room_name, room_type, location, max_members,
                               age_range, room_description, room_passkey, rules)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, room.getRoomID());
            stmt.setString(2, room.getName());
            stmt.setString(3, room.getType());
            stmt.setString(4, room.getLocation());
            stmt.setInt(5, room.getMaxMembers());
            stmt.setString(6, room.getAgeRange());
            stmt.setString(7, room.getDescription());
            stmt.setString(8, "Private".equalsIgnoreCase(room.getType()) ? room.getPassKey() : null);
            stmt.setString(9, room.getRules());

            stmt.executeUpdate();
            System.out.println("✅ Room inserted successfully!");
        }
    }

    // ✅ Get rooms joined by user (with announcements)
    public static List<Room> getRoomsByUserId(int userId) {
        List<Room> rooms = new ArrayList<>();
        String sql = """
            SELECT r.room_id, r.room_name, r.room_type, r.location, r.max_members,
                   r.age_range, r.room_description, r.room_passkey, r.rules,
                   u.username AS leader_name
            FROM rooms r
            JOIN membership m ON r.room_id = m.room_id
            JOIN membership ml ON ml.room_id = r.room_id AND ml.is_leader = TRUE
            JOIN users u ON ml.user_id = u.user_id
            WHERE m.user_id = ?
        """;

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Room room = new Room(
                            rs.getLong("room_id"),
                            rs.getString("room_name"),
                            rs.getString("room_type"),
                            rs.getString("location"),
                            rs.getInt("max_members"),
                            rs.getString("age_range"),
                            rs.getString("room_description"),
                            rs.getString("room_passkey"),
                            rs.getString("rules"),
                            rs.getString("leader_name")
                    );
                    rooms.add(room);
                }
            }

        } catch (SQLException e) {
            System.err.println("❌ Error fetching user's rooms: " + e.getMessage());
        }

        return rooms;
    }

    // ✅ Fetch rules
    public static String getRoomRules(long roomId) {
        String query = "SELECT rules FROM rooms WHERE room_id = ?";
        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setLong(1, roomId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return rs.getString("rules");
        } catch (SQLException e) {
            System.err.println("❌ Error fetching room rules: " + e.getMessage());
        }
        return null;
    }

    // ✅ Update rules
    public static void updateRoomRules(long roomId, String newRules) {
        String query = "UPDATE rooms SET rules = ? WHERE room_id = ?";
        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newRules);
            stmt.setLong(2, roomId);
            stmt.executeUpdate();
            System.out.println("✅ Rules updated successfully for room ID: " + roomId);
        } catch (SQLException e) {
            System.err.println("❌ Error updating room rules: " + e.getMessage());
        }
    }

    public static boolean isLeader(int userID, long roomID){
        String sql = "SELECT 1 FROM room where room_id = ? AND leader_id = ?";
        try(Connection conn = javaDBConnection.getConn();
        PreparedStatement stmnt = conn.prepareStatement(sql)){
            stmnt.setLong(1,roomID);
            stmnt.setInt(2, userID);
            ResultSet rs = stmnt.executeQuery();
            return rs.next();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }


}
