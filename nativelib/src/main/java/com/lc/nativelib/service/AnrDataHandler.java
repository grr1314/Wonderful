package com.lc.nativelib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.lc.nativelib.AppMonitor;
import com.lc.nativelib.file.FileManager;
import com.lc.nativelib.listener.IDataHandle;
import com.lc.nativelib.model.MessageInfo;
import com.lc.nativelib.model.MessageShow;
import com.lc.nativelib.monitors.AnrMonitor;

import java.io.File;
import java.util.ArrayDeque;

public abstract class AnrDataHandler extends IntentService implements IDataHandle {
    public AnrDataHandler(String name) {
        super(name);
    }

    public static void start(Context context, Class<?> listenerServiceClass, ArrayDeque<MessageInfo> queue) {
        Intent intent = new Intent(context, listenerServiceClass);
        intent.putExtra("queue", queue);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        //运行在子线程中
        ArrayDeque<MessageInfo> arrayDeque = (ArrayDeque<MessageInfo>) intent.getSerializableExtra("queue");
        //创建文件夹，注意是需要权限的
        AnrMonitor monitor = (AnrMonitor) AppMonitor.get().getMonitorMap().get(AnrMonitor.class);
        FileManager fileManager = (monitor == null) ? new FileManager(new Gson()) : monitor.getAnrFileManager();
        if (fileManager.checkDir(FileManager.ANR_DIR_PATH)) {
            //创建文件
            final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + FileManager.ANR_DIR_PATH;
            String fileName = "anr_" + System.currentTimeMillis();
            File targetFile = fileManager.createFile(path + fileName + ".txt");
            MessageShow messageShow = new MessageShow();
            messageShow.setRecords(arrayDeque);
            messageShow.setTraceMessage("");
            messageShow.setName(fileName);
            messageShow.setId(0 + "");
            String jsonStr2 = fileManager.getGson().toJson(messageShow);
            if (targetFile != null) {
                fileManager.writeToFile(targetFile, jsonStr2);
            }
        }
        File[] tempList = fileManager.dir.listFiles();
        //不同的Service有不同的后续行为
        doAction(tempList);
        onComplete();
    }
}
