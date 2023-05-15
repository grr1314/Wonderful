package com.lc.im.model;

import com.hyphenate.chat.EMUserInfo;

/**
 * created by lvchao 2023/5/14
 * describe:
 */
public class HyConversationInfo {
    private EMUserInfo emUserInfo;
    private String conversationId;

    private String lastMsgContent;

    private String nickName;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    private int unReadCount;


    public int getUnReadCount() {
        return unReadCount;
    }

    public String getLastMsgContent() {
        return lastMsgContent;
    }

    public void setLastMsgContent(String lastMsgContent) {
        this.lastMsgContent = lastMsgContent;
    }

    public long lastTime;

    public String lastTimeStamp;

    public String getLastTimeStamp() {
        return lastTimeStamp;
    }

    public void setLastTimeStamp(String lastTimeStamp) {
        this.lastTimeStamp = lastTimeStamp;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void setUnReadCount(int unReadCount) {
        this.unReadCount = unReadCount;
    }

    public EMUserInfo getEmUserInfo() {
        return emUserInfo;
    }

    public void setEmUserInfo(EMUserInfo emUserInfo) {
        this.emUserInfo = emUserInfo;
    }
}
