package com.lc.nativelib.monitors;

import android.content.Context;
import android.os.SystemClock;

import com.lc.nativelib.listener.IMonitor;
import com.lc.nativelib.listener.ISystemAnrObserver;
import com.lc.nativelib.MessageType;
import com.lc.nativelib.MonitorQueue;
import com.lc.nativelib.configs.AnrConfig;
import com.lc.nativelib.file.FileManager;
import com.lc.nativelib.listener.AnrListener;
import com.lc.nativelib.listener.MessageListener;
import com.lc.nativelib.model.MessageInfo;
import com.lc.nativelib.model.MessageRecord;
import com.lc.nativelib.service.AnrDataHandler2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 监听anr的触发
 */
public class AnrMonitor implements ISystemAnrObserver, MessageListener {
    private static final String TAG = AnrMonitor.class.getSimpleName();
    private boolean isDebug = true;
    private static final Long DEFAULT_WARNING_TIME = 1000L;
    private static final Long DEFAULT_GUP_TIME = 10L;
    private final MonitorQueue monitorQueue;
    private long currentRecordStartStamp = -1;
    private long currentRecordEndStamp = currentRecordStartStamp;
    MessageRecord currentRecord;
    AnrConfig anrConfig;
    private final long warningTime;
    private final long gupTime;
    private AnrListener anrListener;
    private Context mContext;
    private boolean showMessageInActivity = true;
    private Class<?> serviceClass;
    private FileManager anrFileManager;
    private boolean monitorState;
    private static final int DEFAULT_QUEUE_SIZE = 10;
    private ExecutorService executorService;
    private AnrDataHandler2 anrDataHandler2;


    public AnrMonitor(Context mContext, boolean isDebug, AnrConfig anrConfig) {
        this.mContext = mContext;
        this.isDebug = isDebug;
        this.anrConfig = anrConfig;
        monitorQueue = new MonitorQueue();
        int queueSize = anrConfig.getMessageQueueSize() == 0 ? DEFAULT_QUEUE_SIZE : anrConfig.getMessageQueueSize();
        warningTime = anrConfig.getWarningTime() <= 0 ? DEFAULT_WARNING_TIME : anrConfig.getWarningTime();
        gupTime = anrConfig.getWarningTime() <= 0 ? DEFAULT_GUP_TIME : anrConfig.getGupTime();
        monitorQueue.setQueueSize(queueSize);
    }

    public FileManager getAnrFileManager() {
        return anrFileManager;
    }

    public void setAnrFileManager(FileManager anrFileManager) {
        this.anrFileManager = anrFileManager;
    }

    public void setAnrListener(AnrListener anrListener) {
        this.anrListener = anrListener;
    }

    /**
     * ANR发生时该函数会被触发
     */
    @Override
    public void onANRDumped() {
        long msgActionTime = SystemClock.elapsedRealtime() - currentRecordEndStamp;
        MessageInfo newTail = monitorQueue.createNewTail();
        newTail.addRecord(currentRecord);
        newTail.setWallTime(newTail.getWallTime() + msgActionTime);
        newTail.setMessageType(MessageType.MSG_TYPE_ANR);
        //暂停监控，等待搜集数据完成之后再启动
        monitorState = false;
        executorService.execute(anrDataHandler2);
//        if (isDebug && anrListener != null) {
//            anrListener.anrEvent(monitorQueue.getQueue());
//        } else {
//
//        }
//        if (serviceClass != null) {
////            AnrDataHandler.start(mContext, serviceClass, monitorQueue.getQueue());
//        }
    }

    @Override
    public void startMonitor() {
        monitorState = true;
        anrDataHandler2 = new AnrDataHandler2(isDebug, anrListener, monitorQueue, Thread.currentThread());
    }

    @Override
    public void stopMonitor() {
        monitorState = false;
        monitorQueue.clearQueue();
        monitorQueue.close();
    }

    //调用println  默认false 是start  true 是end
    private final AtomicBoolean odd = new AtomicBoolean(false);

    /**
     * 消息打印
     * 由于每一个消息都会打印一个开始和一个结束，因此主线程调度一个消息就会调用两次println函数，且传入的数据x也不一样
     *
     * @param x
     */
    @Override
    public void println(String x) {
        if (x.contains("<<<<< Finished to") && !odd.get() || !monitorState) {
            //由于这个库本身可能就是在某个Message的分发周期中启动的，所以会先收到依稀Finished
            return;
        }
        if (!odd.get()) {
            //>>>>> Dispatching to Handler (android.view.inputmethod.InputMethodManager$H) {1edcba4} null: 4
            recordStart(x);
        } else {
            //<<<<< Finished to Handler (android.view.inputmethod.InputMethodManager$H) {1edcba4} null
            recordEnd();
        }
        odd.set(!odd.get());
    }

    @Override
    public void dispatchMessageStart(String x) {
        recordStart(x);
    }

    @Override
    public void dispatchMessageEnd() {
        recordEnd();
    }

    @Override
    public boolean monitorState() {
        return monitorState;
    }

    private void recordEnd() {
        currentRecordEndStamp = SystemClock.elapsedRealtime();
        // 第一 如果该条消息的执行时间超过了某一个阈值，那么就需要单独罗列出来该消息
        // 消息的执行时间大致可以认为是currentRecordEndStamp-currentRecordStartStamp
        long msgActionTime = currentRecordEndStamp - currentRecordStartStamp;
        if (msgActionTime > warningTime) {//这个1000是暂时这么写的，后面要支持可配置
            MessageInfo newTail = monitorQueue.createNewTail();
            newTail.addRecord(currentRecord);
            newTail.setWallTime(newTail.getWallTime() + msgActionTime);
            newTail.setMessageType(MessageType.MSG_TYPE_WARN);
            if (isDebug && anrListener != null) {
                //可以回调一下，在开发的时候可以看到MessageRecord的内容
                anrListener.singleWarningMessage(newTail);
            }
        } else {
            MessageInfo tail = monitorQueue.tail();
            tail.addRecord(currentRecord);
            tail.setWallTime(tail.getWallTime() + msgActionTime);
            tail.setMessageType(MessageType.MSG_TYPE_INFO);
        }
    }

    private void recordStart(String x) {
        //第一步解析 x
        currentRecord = parseMsgStart(x);
        //记录处理时间
        currentRecordStartStamp = SystemClock.elapsedRealtime();
        long gapTime = currentRecordStartStamp - currentRecordEndStamp;
        if (gapTime > gupTime && currentRecordEndStamp != -1) {
            MessageInfo newTail = monitorQueue.createNewTail();
            newTail.addRecord(currentRecord);
            newTail.setWallTime(newTail.getWallTime() + gapTime);
            newTail.setMessageType(MessageType.MSG_TYPE_GAP);
        }

    }

    private MessageRecord parseMsgStart(String message) {
        //<<<<< Dispatching to Handler (android.view.inputmethod.InputMethodManager$H) {1edcba4} null: 4
        message = message.trim();
        String[] msgA = message.split(":");
        String what = "";
        StringBuilder newString = new StringBuilder();
        if (msgA.length > 1) {
            what = msgA[msgA.length - 1].trim();
            for (int i = 0; i < msgA.length - 1; i++) {
                newString.append(msgA[i]);
            }
        }
        String[] msgB = newString.toString().split("\\{.*\\}");
        String callback = msgB[1].trim();
        msgB = msgB[0].split("\\(");
        msgB = msgB[1].split("\\)");
        String handler = msgB[0];
        String[] msgC = message.split("\\{");
        msgC = msgC[1].split("\\}");
        String address = msgC[0];
        return new MessageRecord(handler, what, callback, address);
    }

    public void addAnrHandler(Class<?> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }
}
