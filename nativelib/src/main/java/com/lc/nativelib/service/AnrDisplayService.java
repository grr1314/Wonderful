package com.lc.nativelib.service;

import android.app.IntentService;

import com.google.gson.Gson;
import com.lc.nativelib.AppMonitor;
import com.lc.nativelib.file.FileManager;
import com.lc.nativelib.model.MessageShow;
import com.lc.nativelib.monitors.AnrMonitor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AnrDisplayService extends AnrDataHandler {

    private final List<MessageShow> messageShowList = new ArrayList<>();
    private String content = "";

    public AnrDisplayService() {
        super("AnrDisplayService");
    }

    @Override
    public void doAction(File[] tempList) {
        messageShowList.clear();
        AnrMonitor monitor = (AnrMonitor) AppMonitor.get().getMonitorMap().get(AnrMonitor.class);
        FileManager fileManager = (monitor == null) ? new FileManager(new Gson()) : monitor.getAnrFileManager();
        if (tempList == null) return;
        for (File targetFile : tempList) {
            String content = fileManager.readFromFile(targetFile);
            MessageShow messageShow = fileManager.getGson().fromJson(content, MessageShow.class);
            messageShowList.add(messageShow);
        }
        content = fileManager.getGson().toJson(messageShowList);
    }
    @Override
    public void onComplete() {
//        PendingIntent target = AnrDisplayActivity.createPendingIntent(getApplicationContext(), null, FileManager.ANR_DIR_PATH, content);
//        if (Utils.canShowNotification(getApplicationContext())) {
//            Notifications.INSTANCE.showNotification(getApplicationContext(), "ANR !!!",
//                    "触发了ANR请及时查看", target,
//                    0, NotificationType.ANR
//            );
//        }
    }
}