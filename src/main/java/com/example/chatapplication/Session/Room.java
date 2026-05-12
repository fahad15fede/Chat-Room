package com.example.chatapplication.Session;

public class Room {
    private long roomID;
    private String name;
    private String type;
    private String location;
    private int maxMembers;
    private String ageRange;
    private String description;
    private String passKey;
    private String rules;
    private String leaderName;

    public Room(long roomID, String name, String type, String location, int maxMembers,
                String ageRange, String description, String passKey) {
        this.roomID = roomID;
        this.name = name;
        this.type = type;
        this.location = location;
        this.maxMembers = maxMembers;
        this.ageRange = ageRange;
        this.description = description;
        this.passKey = passKey;
    }

    // ✅ Constructor including rules, announcements, and leader name
    public Room(long roomID, String name, String type, String location, int maxMembers,
                String ageRange, String description, String passKey, String rules, String leaderName) {
        this(roomID, name, type, location, maxMembers, ageRange, description, passKey);
        this.rules = rules;
        this.leaderName = leaderName;
    }

    // ✅ Getters
    public long getRoomID() { return roomID; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public int getMaxMembers() { return maxMembers; }
    public String getAgeRange() { return ageRange; }
    public String getDescription() { return description; }
    public String getPassKey() { return passKey; }
    public String getRules() { return rules; }
    public String getLeaderName() { return leaderName; }

    // ✅ Setters
    public void setRules(String rules) { this.rules = rules; }
    public void setLeaderName(String leaderName) { this.leaderName = leaderName; }
}
