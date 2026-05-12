package com.example.chatapplication.Session;

import java.sql.Timestamp;

public class Message {
    private long messageId;         // msg_id
    private long roomId;            // room_id
    private int senderId;           // user_id
    private String senderUsername;  // username (from join)
    private String content;         // msg_text
    private String msgType;         // msg_type ('chat' or 'announcement')
    private Timestamp timestamp;    // sent_at

    // ✅ Full constructor (used when reading from DB)
    public Message(long messageId, long roomId, int senderId, String senderUsername,
                   String content, String msgType, Timestamp timestamp) {
        this.messageId = messageId;
        this.roomId = roomId;
        this.senderId = senderId;
        this.senderUsername = senderUsername;
        this.content = content;
        this.msgType = msgType;
        this.timestamp = timestamp;
    }

    // ✅ Constructor (used when sending new message)
    public Message(long messageId, long roomId, int senderId, String senderUsername,
                   String content, Timestamp timestamp) {
        this(messageId, roomId, senderId, senderUsername, content, "chat", timestamp);
    }

    // ✅ Getters
    public long getMessageId() { return messageId; }
    public long getRoomId() { return roomId; }
    public int getSenderId() { return senderId; }
    public String getSenderUsername() { return senderUsername; }
    public String getContent() { return content; }
    public String getMsgType() { return msgType; }
    public Timestamp getTimestamp() { return timestamp; }

    // ✅ Setters
    public void setContent(String content) { this.content = content; }
    public void setMsgType(String msgType) { this.msgType = msgType; }
}
