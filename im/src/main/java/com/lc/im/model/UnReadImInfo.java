package com.lc.im.model;

import androidx.room.Entity;

/**
 * created by lvchao 2023/5/9
 * describe:
 */
@Entity(tableName = "rtm_info_unread_list_table")
public class UnReadImInfo {
    private int source;//

    private String peerId;

    private String userId;

    private String window;

    private String text;

    private long serverReceivedTs;

    private int messageType;

    private byte[] rawMessage;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getServerReceivedTs() {
        return serverReceivedTs;
    }

    public void setServerReceivedTs(long serverReceivedTs) {
        this.serverReceivedTs = serverReceivedTs;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public byte[] getRawMessage() {
        return rawMessage;
    }

    public void setRawMessage(byte[] rawMessage) {
        this.rawMessage = rawMessage;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public String getPeerId() {
        return peerId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getWindow() {
        return window;
    }

    public void setWindow(String window) {
        this.window = window;
    }
}
