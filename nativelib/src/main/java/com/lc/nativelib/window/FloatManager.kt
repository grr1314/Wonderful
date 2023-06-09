package com.lc.nativelib.window

import android.app.AlertDialog
import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.CountDownTimer
import android.os.IBinder
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.lc.nativelib.R
import com.lc.nativelib.window.SettingsCompat.*
import com.lc.nativelib.service.FloatingService

/**
 * Created by yy on 2020/6/13.
 * function: 悬浮窗管理
 */
class FloatManager(private val builder: FloatClient.Builder) : IFloatWindowHandler {

    private var serviceConnection: FloatServiceConnection? = null

    private var floatBinder: FloatingService.FloatBinder? = null

    private var countDownTimer: CountDownTimer? = null

    init {
        initService()
    }

    private fun initService() {
        serviceConnection = FloatServiceConnection()
        serviceConnection?.let {
            val intent = Intent(builder.context, FloatingService::class.java)
            builder.context?.bindService(intent, it, Service.BIND_AUTO_CREATE)
        }
    }


    override fun requestPermission() {
        manageDrawOverlays(builder.context)
        initCountDown()
    }

    override fun showFloat() {

        if (canDrawOverlays(builder.context?.applicationContext)) {
            showFloatWindow()
            builder.callback?.onPermissionResult(true)
        } else {
            if (builder.callback != null) {
                builder.callback?.onPermissionResult(false)
            } else {
                if (builder.enableDefaultPermissionDialog) {
                    showPermissionDialog()
                }
            }
        }

    }

    lateinit var targetView: View
    var realShow = false
    private fun showFloatWindow() {
        var view = builder.view
        if (view == null) {
            var imageView = ImageView(builder.context)
            imageView.setImageResource(R.mipmap.ic_launcher_round)
            view = imageView
        }
        targetView = view
        if (floatBinder != null) {
            realShow = true
        }
        floatBinder?.show(view)
        floatBinder?.setClazz(builder.clazz)
    }


    private fun showPermissionDialog() {
        val builder = AlertDialog.Builder(builder.context)
        builder.setTitle("提示")
        builder.setMessage("开启悬浮窗权限，")
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setCancelable(true)
        builder.setPositiveButton(
            "确定"
        ) { dialog, _ ->
            requestPermission()
            dialog.dismiss()
        }
        builder.setNegativeButton(
            "取消"
        ) { dialog, _ ->
            Toast.makeText(builder.context, "你点击了取消", Toast.LENGTH_SHORT).show()
            dialog.dismiss()
        }
        builder.show()
    }

    private fun initCountDown() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                if (canDrawOverlays(builder.context)) {
                    showFloatWindow()
                    countDownTimer?.cancel()
                }
            }

            override fun onFinish() {

            }
        }
        countDownTimer?.start()
    }


    override fun dismissFloat() {
        floatBinder?.hide()
        countDownTimer?.cancel()
    }

    /**
     * 释放资源
     */
    override fun releaseResource() {
        serviceConnection?.let {
            builder.context?.unbindService(it)
        }
        countDownTimer?.cancel()
    }

    inner class FloatServiceConnection : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            floatBinder = iBinder as FloatingService.FloatBinder
            if (!realShow) {
                floatBinder?.show(targetView)
                floatBinder?.setClazz(builder.clazz)
            }

        }

        override fun onServiceDisconnected(componentName: ComponentName) {

        }
    }
}