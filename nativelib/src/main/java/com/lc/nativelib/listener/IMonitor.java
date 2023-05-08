package com.lc.nativelib.listener;

public interface IMonitor {
    void startMonitor();

//    void println(String x);
//    void dispatchMessageStart(String x);
//
//    void dispatchMessageEnd();

    boolean monitorState();

    void stopMonitor();
}
