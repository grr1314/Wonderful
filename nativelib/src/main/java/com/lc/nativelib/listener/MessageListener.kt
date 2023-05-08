package com.lc.nativelib.listener

/**
 * created by lvchao 2023/4/30
 * describe:
 */
interface MessageListener : IMonitor {
    fun println(x: String?)

    fun dispatchMessageStart(x: String?)

    fun dispatchMessageEnd()
}