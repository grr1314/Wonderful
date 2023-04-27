package com.lc.nativelib.service

import android.os.Environment
import android.os.Looper
import android.util.Printer
import com.google.gson.Gson
import com.lc.nativelib.AppMonitor
import com.lc.nativelib.MonitorQueue
import com.lc.nativelib.file.FileManager
import com.lc.nativelib.listener.AnrListener
import com.lc.nativelib.model.MessageInfo
import com.lc.nativelib.model.MessageShow
import com.lc.nativelib.monitors.AnrMonitor
import java.io.File
import java.util.*


class AnrDataHandler2(
    isDebug: Boolean,
    anrListener: AnrListener,
    monitorQueue: MonitorQueue,
    currentThread: Thread
) : Runnable, Printer {
    private val debug: Boolean = isDebug
    val listener: AnrListener = anrListener
    private val messageQueue: MonitorQueue = monitorQueue
    private val cThread: Thread = currentThread
    private var trace = ""
    override fun run() {
        val file = newFile(messageQueue.queue,cThread)
        if (debug) {
            listener.anrEvent(messageQueue.queue)
        } else {
            listener.uploadAnrFile(file)
            file?.delete()
        }
        messageQueue.clearQueue()
    }

    private fun newFile(queue: ArrayDeque<MessageInfo>, cThread: Thread): File? {
        //创建文件夹，注意是需要权限的
        val monitor = AppMonitor.get().monitorMap[AnrMonitor::class.java] as AnrMonitor?
        val fileManager = if (monitor == null) FileManager(Gson()) else monitor.anrFileManager
        if (fileManager.checkDir(FileManager.ANR_DIR_PATH)) {
            //创建文件
            val path =
                Environment.getExternalStorageDirectory().absolutePath + FileManager.ANR_DIR_PATH
            val fileName = "anr_" + System.currentTimeMillis()
            val targetFile = fileManager.createFile("$path$fileName.txt")
            val messageShow = MessageShow()
            startTrace()
            messageShow.records = queue
            messageShow.traceMessage = ""
            messageShow.name = fileName
            messageShow.traceMessage = trace
            messageShow.stackMessage= stack(cThread)
            messageShow.id = 0.toString() + ""
            val jsonStr2 = fileManager.gson.toJson(messageShow)
            if (targetFile != null) {
                fileManager.writeToFile(targetFile, jsonStr2)
            }
            return targetFile
        }
        return null
    }

    private fun stack(cThread: Thread): String? {
        val stringBuilder = StringBuilder()
        stringBuilder.append("msgId: ")
            .append(1000)
            .append("\r\n")
        for (stackTraceElement in cThread.stackTrace) {
            stringBuilder
                .append(stackTraceElement.toString())
                .append("\r\n")
        }
        return stringBuilder.toString()
    }


    private fun startTrace() {
        Looper.getMainLooper().dump(this, "")
    }

    override fun println(x: String?) {
        if (x != null) {
            trace = x
        };
    }
}