package com.lc.repository;

import com.google.gson.annotations.SerializedName;

public class BaseModel<T> {
    @SerializedName("result")
    T data;
    @SerializedName("resultcode")
    public String resultCode;
    @SerializedName("reason")
    public String reason;

    @SerializedName("error_code")
    public int errorCode;

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultCode() {
        return resultCode;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
