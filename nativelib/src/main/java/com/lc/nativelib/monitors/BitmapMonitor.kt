package com.lc.nativelib.monitors

import com.lc.nativelib.listener.IMonitor

/**
 * created by lvchao 2023/5/3
 * describe: https://www.evernote.com/shard/s411/sh/ab07e382-8297-5740-95ce-052b6dc4d3e6/g1QSkAMBsXcE8uIL1N1FisutIAEKMzVFjtvUlntsTOQODnxASYAr7czQ7g
 *
 * 1 检测内存分配
 * 2 检测
 */
class BitmapMonitor : IMonitor {
    private var monitorState = false
    override fun startMonitor() {
        monitorState = true
    }

    override fun monitorState() = monitorState

    override fun stopMonitor() {
        monitorState = false
    }
}