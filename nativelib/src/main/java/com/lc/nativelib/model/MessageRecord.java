package com.lc.nativelib.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Printer 对应的实体
 * 消息样例如下：
 * Dispatching to Handler (android.view.inputmethod.InputMethodManager$H) {1edcba4} null: 4
 */
public class MessageRecord implements Parcelable, Serializable {
    private String handlerName;
    private String what;
    private String callbackName;
    private String handlerAddress;

    public MessageRecord(String handlerName, String what, String callbackName, String handlerAddress) {
        this.callbackName = callbackName;
        this.handlerAddress = handlerAddress;
        this.handlerName = handlerName;
        this.what = what;
    }

    protected MessageRecord(Parcel in) {
        handlerName = in.readString();
        what = in.readString();
        callbackName = in.readString();
        handlerAddress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(handlerName);
        dest.writeString(what);
        dest.writeString(callbackName);
        dest.writeString(handlerAddress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MessageRecord> CREATOR = new Creator<MessageRecord>() {
        @Override
        public MessageRecord createFromParcel(Parcel in) {
            return new MessageRecord(in);
        }

        @Override
        public MessageRecord[] newArray(int size) {
            return new MessageRecord[size];
        }
    };

    public void setCallbackName(String callbackName) {
        this.callbackName = callbackName;
    }

    public String getCallbackName() {
        return callbackName;
    }

    public void setHandlerAddress(String handlerAddress) {
        this.handlerAddress = handlerAddress;
    }

    public String getHandlerAddress() {
        return handlerAddress;
    }

    public void setWhat(String what) {
        this.what = what;
    }

    public String getWhat() {
        return what;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public String getHandlerName() {
        return handlerName;
    }

    @Override
    public String toString() {
        return "MessageRecord{" +
                "handlerName='" + handlerName + '\'' +
                ", what='" + what + '\'' +
                ", callbackName='" + callbackName + '\'' +
                ", handlerAddress='" + handlerAddress + '\'' +
                '}';
    }
}
