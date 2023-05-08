package com.lc.im;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.mylibrary.callback.ImListener;
import com.lc.im.db.ImCache;
import com.lc.im.model.ImInfo;
import com.lc.im.model.UnReadImInfo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.agora.rtm.RtmMessage;

/**
 * created by lvchao 2023/5/7
 * describe: IM消息集散地
 */
public class RtmMessageCentralStation extends MessageCentralStation<RtmMessage> {
    private static final String TAG = "RtmMessageCentralStation";
    private ImInfo head;
    private ImInfo tail;

    private ImInfo currentInfo;

    private ImInfo pre;
    private final Object mLock = new Object();
    private boolean block;

    private int unReadCount;
    private int msgCount;

    private final ExecutorService executorService;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private final Map<String, MsgWatcher> msgWatcherList;
    private final Set<ImListener> imListeners;
    private final RtmHandler rtmHandler = new RtmHandler(Looper.getMainLooper());

    private String selfUserId;
    private String peerId;

    /**
     * 登录成功之后要立即设置
     *
     * @param selfUserId
     */
    public void setSelfUserId(String selfUserId) {
        this.selfUserId = selfUserId;
    }

    public void setPeerId(String peerId) {
        this.peerId = peerId;
    }

    public RtmMessageCentralStation(ImCache imCache) {
        msgWatcherList = new HashMap<>();
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        imListeners = new HashSet<>();
        //创建一个本地数据库的管理类
    }

    public void addWatcher(String peerId, MsgWatcher msgWatcher) {
        msgWatcherList.put(peerId, msgWatcher);
        loop();
    }

    public void removeWatcher(String peerId) {
        msgWatcherList.remove(peerId);
    }

    public void addListener(ImListener imListener) {
        if (imListeners != null) {
            imListeners.add(imListener);
        }
    }

    public void removeListener(ImListener imListener) {
        if (imListeners != null) {
            imListeners.remove(imListener);
        }
    }

    /**
     * @return
     */
    @SuppressLint("LongLogTag")
    private ImInfo next() {
        synchronized (mLock) {
            ImInfo imInfo = currentInfo;
            if (currentInfo != null) {
                currentInfo = currentInfo.next;
            }
            if (imInfo != null) {
                Log.i(TAG, "current:" + imInfo.getTarget().getText() + "" + "整体：" + imInfo.getTarget().toString() + "");
            }
            return imInfo;
        }
    }

    /**
     * 遍历链表中所有的数据
     */
    @SuppressLint("LongLogTag")
    private void loop() {
        Log.i(TAG, "Station start loop");
        block = false;
        executorService.execute(() -> {
            while (!block) {
                Log.i(TAG, "Station  looping");
                ImInfo imInfo = next();
                if (imInfo == null) {
                    block = true;
                    Log.i(TAG, "Station  looping end");
                    currentInfo = head;
                } else {
                    //去匹配对应的观察者，如果有对应的则要从链表中移除
                    if (msgWatcherList.containsKey(imInfo.getPeerId())) {
                        catchTargetAndRemove(imInfo);
                        dispatch(imInfo);
                        unReadCount--;
                        msgCount--;
                    } else {
                        pre = imInfo;
                    }
                }
            }
        });
    }

    private void dispatch(ImInfo imInfo) {
        MsgWatcher msgWatcher = msgWatcherList.get(imInfo.getPeerId());
        if (msgWatcher != null) {
            rtmHandler.post(() -> msgWatcher.onMessage(imInfo));
        }
        //存到本地数据库里面，这样可以直接获取历史消息
    }

    private void catchTargetAndRemove(ImInfo imInfo) {
        if (imInfo == head) {
            //如果当前节点是head
            head = imInfo.next;
            imInfo.next = null;
            pre = head;
        } else {
            ImInfo t = imInfo.next;
            imInfo.next = null;
            pre.next = t;
        }
    }

    /**
     * 添加消息到列表中
     *
     * @param message
     * @param source
     * @param peerId  peerId多数情况下是给你发消息的用户的Id或者是频道id，但是基于现有的设计peerId也可能是自己的userId
     * @param window
     */
    public void add(RtmMessage message, int source, String peerId, String userId, String window) {
        add(message, source, peerId, userId, window, false);
    }

    public void addFromLocal(RtmMessage message, int source, String peerId) {
        String window = selfUserId + peerId;
        add(message, source, selfUserId, selfUserId, window);
    }

    public void addFromNet(RtmMessage message, int source, String peerId) {
        String window = selfUserId + peerId;
        add(message, source, peerId, selfUserId, window);
    }

    public void addFromUnReadCache(UnReadImInfo message, int source, String peerId, String userId) {

    }

    @SuppressLint("LongLogTag")
    @Override
    public void add(RtmMessage message, int source, String peerId, String userId, String window, boolean toHead) {
        Log.i(TAG, "Station add");
        synchronized (mLock) {
            ImInfo imInfo = new ImInfo(message, source, peerId, userId);
            imInfo.setWindow(window);
            if (toHead) {
                addToHead(imInfo);
            } else {
                addToTail(imInfo);
            }
            msgCount++;
            if (!self(peerId) || inRoom(peerId)) {
                unReadCount++;
                //通知所有订阅方，未读数发生变化
                for (Map.Entry<String, MsgWatcher> next : msgWatcherList.entrySet()) {
                    rtmHandler.post(() -> next.getValue().unReadCount(unReadCount));
                }
                for (ImListener imListener : imListeners) {
                    rtmHandler.post(() -> imListener.unReadCountChange(unReadCount));
                }
            }
        }
        loop();
    }

    /**
     * 表示用户已经在和目标用户聊天了
     *
     * @param peerId
     * @return
     */
    private boolean inRoom(String peerId) {
        return peerId.equals(this.peerId);
    }

    /**
     * 判断是否是自己
     *
     * @param peerId
     * @return
     */
    private boolean self(String peerId) {
        return selfUserId.equals(peerId);
    }

    private void addToTail(ImInfo imInfo) {
        if (head == null) {
            head = imInfo;
            tail = head;
            currentInfo = head;
        } else {
            while (tail.next != null) tail = tail.next;
            tail.next = imInfo;
            tail = tail.next;
        }
    }

    private void addToHead(ImInfo imInfo) {
        if (head == null) {
            head = imInfo;
            tail = head;
            currentInfo = head;
        } else {
            imInfo.next = head;
            head = imInfo;
        }
    }

    public void start() {
        //先从本地缓存里面取没有看的消息
        //todo

        //然后开启循环
        loop();
    }

    public void stop() {
        block = true;
        msgWatcherList.clear();
        //存到本地缓存，如果还有没来得及看的消息
        //todo
    }

    private static class RtmHandler extends Handler {

        public RtmHandler(Looper mainLooper) {
            super(mainLooper);
        }
    }
}
