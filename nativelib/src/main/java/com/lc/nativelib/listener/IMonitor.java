package com.lc.nativelib.listener;

public interface IMonitor {
    void startMonitor();

    void println(String x);

    boolean monitorState();

    void stopMonitor();
}
