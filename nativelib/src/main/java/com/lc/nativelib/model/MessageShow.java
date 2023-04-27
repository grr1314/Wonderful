package com.lc.nativelib.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayDeque;

public class MessageShow implements Serializable, Parcelable {
    private ArrayDeque<MessageInfo> records;
    private String traceMessage;
    private String id;
    private String name;
    private String stackMessage;

    public MessageShow() {

    }

    protected MessageShow(Parcel in) {
        traceMessage = in.readString();
        id = in.readString();
        name = in.readString();
        records = (ArrayDeque<MessageInfo>) in.readSerializable();
        stackMessage = in.readString();
    }

    public static final Creator<MessageShow> CREATOR = new Creator<MessageShow>() {
        @Override
        public MessageShow createFromParcel(Parcel in) {
            return new MessageShow(in);
        }

        @Override
        public MessageShow[] newArray(int size) {
            return new MessageShow[size];
        }
    };

    public void setTraceMessage(String traceMessage) {
        this.traceMessage = traceMessage;
    }

    public String getTraceMessage() {
        return traceMessage;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setRecords(ArrayDeque<MessageInfo> records) {
        this.records = records;
    }

    public ArrayDeque<MessageInfo> getRecords() {
        return records;
    }

    public void setStackMessage(String stackMessage) {
        this.stackMessage = stackMessage;
    }

    public String getStackMessage() {
        return stackMessage;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(traceMessage);
        dest.writeString(id);
        dest.writeString(name);
        dest.writeSerializable(records);
        dest.writeString(stackMessage);
    }
}
