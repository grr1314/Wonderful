package com.lc.nativelib.configs;

import com.lc.nativelib.listener.IConfig;

/**
 * 配置ANR监控的一些基本设置
 */
public class AnrConfig implements IConfig {
    private long warningTime;
    private long gupTime;
    private int messageQueueSize;

    public void setMessageQueueSize(int messageQueueSize) {
        this.messageQueueSize = messageQueueSize;
    }

    public int getMessageQueueSize() {
        return messageQueueSize;
    }

    public void setGupTime(long gupTime) {
        this.gupTime = gupTime;
    }

    public long getGupTime() {
        return gupTime;
    }

    public void setWarningTime(long warningTime) {
        this.warningTime = warningTime;
    }

    public long getWarningTime() {
        return warningTime;
    }

    @Override
    public boolean isOpen() {
        return true;
    }
}
