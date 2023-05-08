package com.lc.nativelib.monitors

import android.content.Context
import android.os.Looper
import android.os.SystemClock
import android.view.Choreographer
import com.lc.nativelib.ReflectUtils
import com.lc.nativelib.configs.UiConfig
import com.lc.nativelib.listener.MessageListener
import com.lc.nativelib.listener.UiMonitorListener
import java.lang.reflect.Method


/**
 * 监测UI渲染超时的监视器
 *
 * 1 记录单帧的耗时
 * （1）确定哪个消息是绘制相关的
 *     第一确定的是绘制的发起点是在Choreographer中，往主线程中发一个消息表示绘制帧，但是主线程还会处理其他的消息，所以过滤很重要。如何
 *     过滤呢？可以往Choreographer里面注册一个Callback，因为这些Callback都会在doFrame中被回调，因此Callback被回调的时候该事件一定
 *     是绘制的事件。
 *     确定好方法之后就是确定如何注册Callback了，这里使用的方法是反射拿到Choreographer中的callbackQueues，然后在
 *     反射拿到addCallbackLocked方法，最后分别往input、animation、traversal三个action中添加callback了而且都是在
 *     队列的头部。
 *
 * （2）如何计算时间
 *     由于doFrame函数中会先处理action为input的callback，再处理animation的，最后处理traversal的。那么粗略的计算
 *     input的时间=animation开始的时间-input开始的时间
 *     animation时间=traversal开始的时间-animation开始的时间
 *     traversal时间=消息结束时间-traversal开始的时间
 *
 *     那么总时间就是三者之和，当然如果不想拆分这么细那就是直接消息结束时间-input开始的时间
 *
 * 2 展示问题
 *
 *
 *
 *
 *
 *
 *
 */
class UiRenderMonitor(mContext: Context, isDebug: Boolean, uiConfig: UiConfig) : MessageListener {
    private var monitorState = false
    private lateinit var choreographer: Choreographer
    private val ADD_CALLBACK = "addCallbackLocked"
    private var callbackQueues: Array<Any>? = null
    private val CALLBACK_INPUT = 0
    private val CALLBACK_ANIMATION = 1
    private val CALLBACK_TRAVERSAL = 3
    private lateinit var inputMethod: Method
    private lateinit var animationMethod: Method
    private lateinit var traversalMethod: Method
    private lateinit var mLock: Any
    private var eventType = -1
    private var startTraversalTimeStamp: Long = 0
    private var startInputTimeStamp: Long = 0
    private var startAnimationTimeStamp: Long = 0

    private val GOOD_TIME = 16
    private val WARNING_TIME = 36
    private val BAD_TIME = 66
    private var inputTime: Long = 0
    private var animationTime: Long = 0
    private var singleFrameTime: Long = 0
    private var uiMonitorListener: UiMonitorListener? = null
    private var debug = isDebug

    /**
     * 下面是三个
     */
    private val inputRunnable = Runnable {
        eventType = CALLBACK_INPUT
        startInputTimeStamp = System.nanoTime()
    }
    private val animationRunnable = Runnable {
        eventType = CALLBACK_ANIMATION
        val currentTime = System.nanoTime()
        inputTime = currentTime - startInputTimeStamp
        startAnimationTimeStamp = currentTime
    }

    private val traversalRunnable = Runnable {
        eventType = CALLBACK_TRAVERSAL
        val currentTime = System.nanoTime()
        animationTime = currentTime - startAnimationTimeStamp
        startTraversalTimeStamp = currentTime
    }

    init {
        init()
    }

    private fun init() {
        if (Thread.currentThread() != Looper.getMainLooper().thread) {
            //如果不是在主线程里面
            throw AssertionError("UiRenderMonitor must be init in main thread!");
        }
        choreographer = Choreographer.getInstance()
        //反射去拿Choreographer内的队列
        callbackQueues = arrayOf(ReflectUtils.getAccessibleField(choreographer, "mCallbackQueues"))
        mLock = ReflectUtils.getAccessibleField(choreographer, "mLock")
        inputMethod = ReflectUtils.getAccessibleMethod(
            callbackQueues!![CALLBACK_INPUT],
            ADD_CALLBACK,
            Long::class.java,
            Any::class.java,
            Any::class.java
        )
        animationMethod = ReflectUtils.getAccessibleMethod(
            callbackQueues!![CALLBACK_ANIMATION],
            ADD_CALLBACK,
            Long::class.java,
            Any::class.java,
            Any::class.java
        )
        traversalMethod = ReflectUtils.getAccessibleMethod(
            callbackQueues!![CALLBACK_TRAVERSAL],
            ADD_CALLBACK,
            Long::class.java,
            Any::class.java,
            Any::class.java
        )
    }

    public fun setListener(uiMonitorListener: UiMonitorListener) {
        this.uiMonitorListener = uiMonitorListener
    }

    override fun startMonitor() {
        monitorState = true
        addCallback(CALLBACK_INPUT, inputRunnable, true)
        addCallback(CALLBACK_ANIMATION, animationRunnable, true)
        addCallback(CALLBACK_TRAVERSAL, traversalRunnable, true)
    }

    @Deprecated("改用dispatchMessageStart和dispatchMessageEnd两个函数来代替")
    override fun println(x: String?) {

    }

    override fun dispatchMessageStart(x: String?) {
        eventType = -1
    }


    override fun dispatchMessageEnd() {
        if (eventType > -1) {//表示是处理绘制的消息
            //现在记录了input的时间、动画的时间、绘制的时间
            val currentTime = System.nanoTime()
            val traversalTime = currentTime - startTraversalTimeStamp
            singleFrameTime = inputTime + animationTime + traversalTime
            action(singleFrameTime)
        }
    }

    private fun action(singleFrameTime: Long) {
        if (debug) {
            offlineAction(singleFrameTime)
            return
        }
        onlineAction(singleFrameTime)
    }

    private fun onlineAction(singleFrameTime: Long) {
        if (singleFrameTime >= BAD_TIME || singleFrameTime >= WARNING_TIME) {
            uiMonitorListener?.onUiRenderWarning(singleFrameTime)
        }
    }

    /**
     * 还是已生成文件为主
     */
    private fun offlineAction(singleFrameTime: Long) {
        if (singleFrameTime >= BAD_TIME) {

        } else if (singleFrameTime >= WARNING_TIME) {

        }
    }

    override fun monitorState() = monitorState

    override fun stopMonitor() {
        monitorState = false
    }

    private fun addCallback(type: Int, callback: Runnable, isAddHeader: Boolean) {
        //必须添加锁
        synchronized(mLock) {
            var method: Method? = null
            when (type) {
                CALLBACK_INPUT -> method = inputMethod
                CALLBACK_TRAVERSAL -> method = traversalMethod
                CALLBACK_ANIMATION -> method = animationMethod
            }
            method?.invoke(
                callbackQueues?.get(type),
                if (!isAddHeader) SystemClock.uptimeMillis() else -1,
                callback,
                null
            )
        }
    }
}