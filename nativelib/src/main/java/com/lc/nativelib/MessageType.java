package com.lc.nativelib;

public enum MessageType {
    MSG_TYPE_NONE(0),
    MSG_TYPE_INFO(1),
    MSG_TYPE_WARN(2),
    MSG_TYPE_ANR(3),
    MSG_TYPE_GAP(4);
    public int type;

    MessageType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
