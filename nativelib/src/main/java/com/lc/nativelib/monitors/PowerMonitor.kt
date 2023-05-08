package com.lc.nativelib.monitors

import com.lc.nativelib.listener.IMonitor

/**
 * created by lvchao 2023/5/3
 * describe:
 */
class PowerMonitor : IMonitor {
    private var monitorState = false
    override fun startMonitor() {
        monitorState = true
    }

    override fun monitorState() = monitorState

    override fun stopMonitor() {
        monitorState = false
    }
}