package com.lc.im.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * created by lvchao 2023/5/9
 * describe:
 */
@Entity(tableName = "rtm_info_unread_list_table")
public class UnReadImInfo {
    //PrimaryKey主键，autoGenerate自增长
    @PrimaryKey(autoGenerate = true)
    //ColumnInfo用于指定该字段存储在表中的名字，并指定类型
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    public int id;
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

    @NonNull
    @Override
    public String toString() {
        String stringBuilder = "userId："
                + userId + "\n"
                + "window：" + window + "\n"
                + "peerId：" + peerId + "\n"
                + "text：" + text + "\n"
                + "messageType：" + messageType + "\n"
                + "source：" + source;
        return stringBuilder;
    }
}
