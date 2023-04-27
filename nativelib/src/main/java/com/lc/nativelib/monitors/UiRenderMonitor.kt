package com.lc.nativelib.monitors

import com.lc.nativelib.listener.IMonitor

/**
 * 监测UI渲染超时的监视器
 */
class UiRenderMonitor : IMonitor {
    private val monitorState = false
    override fun startMonitor() {
    }

    override fun println(x: String?) {

    }

    override fun monitorState() = monitorState

    override fun stopMonitor() {
    }
}