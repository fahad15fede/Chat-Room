package com.example.chatapplication.DAO;

import com.example.chatapplication.Session.Membership;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MembershipDAO {

    public static void addMembership(Membership membership) throws SQLException {

        String sql = """ 
                INSERT into membership (user_id, room_id, is_active, is_leader, joined_at)
                VALUES(?,?,?,?,?)""";

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmnt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){

            stmnt.setInt(1, membership.getUserID());
            stmnt.setLong(2, membership.getRoomID());
            stmnt.setBoolean(3, membership.isActive());
            stmnt.setBoolean(4, membership.isLeader());
            stmnt.setTimestamp(5, Timestamp.valueOf(membership.getJoinedAt()));

            stmnt.executeUpdate();


            try (ResultSet rs = stmnt.getGeneratedKeys()) {
                if (rs.next()) {
                    membership.setMembershipID(rs.getInt(1));
                    System.out.println("✅ Membership created for user " + membership.getUserID());
                }
        }


    } catch (SQLException e) {
            e.printStackTrace();
}
    }

    public static boolean isUserAlreadyInRoom(int userID, long roomID) throws SQLException {

        String sql = "SELECT COUNT(*) FROM membership WHERE user_id = ? AND room_id = ? AND is_active = TRUE";
        try(Connection conn = javaDBConnection.getConn();

            PreparedStatement stmnt = conn.prepareStatement(sql)) {
            stmnt.setInt(1, userID);
            stmnt.setLong(2, roomID);

            ResultSet rs = stmnt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

            }  catch (SQLException e) {
                e.printStackTrace();
        }


            return false;
    }

//    get all members in a specific room

    public static List<Membership> getMembersByRoom(long roomID) {
        List<Membership> members = new ArrayList<>();
        String sql = "SELECT * FROM membership WHERE room_id = ?";

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, roomID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Membership member = new Membership(
                        rs.getInt("membership_id"),
                        rs.getInt("user_id"),
                        rs.getLong("room_id"),
                        rs.getBoolean("is_active"),
                        rs.getBoolean("is_leader"),
                        rs.getTimestamp("joined_at").toLocalDateTime()
                );
                members.add(member);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return members;
    }
    // ✅ Remove (deactivate) member from room
    public static void deactivateMembership(int userID, long roomID) {
        String sql = "UPDATE membership SET is_active = FALSE WHERE user_id = ? AND room_id = ?";

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            stmt.setLong(2, roomID);
            stmt.executeUpdate();

            System.out.println("🚪 User " + userID + " left room " + roomID);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ✅ Get all rooms a user is part of
    public static List<Long> getRoomIDsForUser(int userID) {
        List<Long> roomIDs = new ArrayList<>();
        String sql = "SELECT room_id FROM membership WHERE user_id = ? AND is_active = TRUE";

        try (Connection conn = javaDBConnection.getConn();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                roomIDs.add(rs.getLong("room_id"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return roomIDs;
    }
}
