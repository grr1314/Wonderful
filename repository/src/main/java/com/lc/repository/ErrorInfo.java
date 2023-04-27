package com.lc.repository;

public class ErrorInfo {
    public int type;
    public int code;
    public String message;

    public ErrorInfo(int type, int code, String message) {
        this.code = code;
        this.type = type;
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
