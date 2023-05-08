package com.lc.nativelib.listener

/**
 * created by lvchao 2023/5/3
 * describe:
 */
interface UiMonitorListener {
    fun onUiRenderWarning(singleFrameTime: Long)
}