package com.example.chatapplication.Session;

import java.time.LocalDateTime;

public class Membership {

    private int membershipID;   // Unique membership ID (Primary Key)
    private long roomID;        // Foreign key from 'rooms' table
    private int userID;         // Foreign key from 'users' table
    private boolean isActive;   // Whether the member is currently active in the room
    private boolean isLeader;   // True if the user is the leader/creator of the room
    private LocalDateTime joinedAt; // Timestamp when the user joined the room

    // ✅ Full Constructor (used when fetching from DB)
    public Membership(int membershipID, int userID, long roomID, boolean isActive, boolean isLeader, LocalDateTime joinedAt) {
        this.membershipID = membershipID;
        this.userID = userID;
        this.roomID = roomID;
        this.isActive = isActive;
        this.isLeader = isLeader;
        this.joinedAt = joinedAt;
    }

    // ✅ Constructor for new memberships (before inserting to DB)
    public Membership(int userID, long roomID, boolean isLeader) {
        this.userID = userID;
        this.roomID = roomID;
        this.isLeader = isLeader;
        this.isActive = true; // By default, a newly joined member is active
        this.joinedAt = LocalDateTime.now();
    }

    // ✅ Getters and Setters
    public int getMembershipID() {
        return membershipID;
    }

    public void setMembershipID(int membershipID) {
        this.membershipID = membershipID;
    }

    public long getRoomID() {
        return roomID;
    }

    public void setRoomID(long roomID) {
        this.roomID = roomID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isLeader() {
        return isLeader;
    }

    public void setLeader(boolean leader) {
        isLeader = leader;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }

    @Override
    public String toString() {
        return "Membership{" +
                "membershipID=" + membershipID +
                ", userID=" + userID +
                ", roomID=" + roomID +
                ", isActive=" + isActive +
                ", isLeader=" + isLeader +
                ", joinedAt=" + joinedAt +
                '}';
    }
}
