package com.lc.nativelib.model;

//import static com.lc.nativelib.MessageType.MSG_TYPE_ANR;
//import static com.lc.nativelib.MessageType.MSG_TYPE_GAP;
//import static com.lc.nativelib.MessageType.MSG_TYPE_INFO;
//import static com.lc.nativelib.MessageType.MSG_TYPE_NONE;
//import static com.lc.nativelib.MessageType.MSG_TYPE_WARN;

import static com.lc.nativelib.MessageType.MSG_TYPE_NONE;

import android.os.Parcel;
import android.os.Parcelable;

import com.lc.nativelib.MessageType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageInfo implements Parcelable, Serializable {
    private int recordCount;
    private List<MessageRecord> records;
    private long wallTime;
    private int messageType = MSG_TYPE_NONE.getType();

    public MessageInfo() {
        records = new ArrayList<>();
    }

    protected MessageInfo(Parcel in) {
        recordCount = in.readInt();
        wallTime = in.readLong();
        messageType = in.readInt();
        records = in.readArrayList(Thread.currentThread().getContextClassLoader());
    }

    public static final Creator<MessageInfo> CREATOR = new Creator<MessageInfo>() {
        @Override
        public MessageInfo createFromParcel(Parcel in) {
            return new MessageInfo(in);
        }

        @Override
        public MessageInfo[] newArray(int size) {
            return new MessageInfo[size];
        }
    };

    public List<MessageRecord> getRecords() {
        return records;
    }

    public int getRecordCount() {
        return records.size();
    }

    public void setWallTime(long wallTime) {
        this.wallTime = wallTime;
    }

    public long getWallTime() {
        return wallTime;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType.getType();
    }

    public int getMessageType() {
        return messageType;
    }

    public void addRecord(MessageRecord messageRecord) {
        if (records == null) records = new ArrayList<>();
        records.add(messageRecord);
    }

    public void recycle() {
        records.clear();
        wallTime = 0;
        messageType = MSG_TYPE_NONE.getType();
    }

    @Override
    public String toString() {
        return "MessageInfo{" +
                "recordCount=" + records.size() +
                ", records=" + records +
                ", wallTime=" + wallTime +
                ", messageType=" + messageType +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recordCount);
        dest.writeLong(wallTime);
        dest.writeInt(messageType);
        dest.writeList(records);
    }

    public static String msgTypeToString(int msgType) {
        switch (msgType) {
            case 0:
                return "MSG_TYPE_NONE";
            case 1:
                return "MSG_TYPE_INFO";
            case 2:
                return "MSG_TYPE_WARN";
            case 3:
                return "MSG_TYPE_ANR";
//            case MSG_TYPE_JANK:
//                return "MSG_TYPE_JANK";
            case 4:
                return "MSG_TYPE_GAP";
//            case MSG_TYPE_ACTIVITY_THREAD_H:
//                return "MSG_TYPE_ACTIVITY_THREAD_H";
        }
        return "";
    }
}
