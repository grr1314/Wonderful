package com.lc.im.agora;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.mylibrary.callback.ImListener;
import com.lc.im.MessageCentralStation;
import com.lc.im.MsgWatcher;
import com.lc.im.db.ImCache;
import com.lc.im.model.HistoryImInfo;
import com.lc.im.model.ImInfo;
import com.lc.im.model.UnReadImInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.agora.rtm.RtmMessage;

/**
 * created by lvchao 2023/5/7
 * describe: IM消息集散地
 */
public class AgoraMessageCentralStation extends MessageCentralStation<RtmMessage> {
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
    private final ImCache imCache;

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

    public AgoraMessageCentralStation(ImCache imCache) {
        msgWatcherList = new HashMap<>();
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        imListeners = new HashSet<>();
        //创建一个本地数据库的管理类
        this.imCache = imCache;
    }

    public void loadHistoryMessage(int page) {
        final MsgWatcher watcher = msgWatcherList.get(peerId);
        String window = selfUserId + peerId;
        if (watcher != null) {
            watcher.startLoadHistoryMessage();
        }
        executorService.execute(() -> {
            List<HistoryImInfo> historyImInfos = imCache.getHistoryInfo(page, window);
            List<ImInfo> imInfos = transform(historyImInfos);
            rtmHandler.post(() -> {
                if (watcher != null) {
                    if (imInfos != null) {
                        watcher.loadHistoryMessageSuccess(imInfos);
                    } else {
                        watcher.loadHistoryMessageFail();
                    }
                    watcher.loadHistoryMessageComplete();
                }
            });
        });

    }

    private List<ImInfo> transform(List<HistoryImInfo> historyImInfos) {
        List<ImInfo> imInfos = null;
        for (HistoryImInfo historyImInfo : historyImInfos) {
            if (imInfos == null) {
                imInfos = new ArrayList<>();
            }
            ImInfo currentImInfo = new ImInfo(null, historyImInfo.getSource(), historyImInfo.getPeerId(), historyImInfo.getUserId());
            imInfos.add(currentImInfo);
            currentImInfo.setText(historyImInfo.getText());
            currentImInfo.setMessageType(historyImInfo.getMessageType());
            currentImInfo.setRawMessage(historyImInfo.getRawMessage());
            currentImInfo.setWindow(historyImInfo.getWindow());
            currentImInfo.setCacheMessageId(historyImInfo.id);
            currentImInfo.setServerReceivedTs(historyImInfo.getServerReceivedTs());
        }
        return imInfos;
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
            if (imInfo != null && imInfo.getTarget() != null) {
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
                        if (imInfo.getSource() == 2 && imInfo.getCacheMessageId() != -1) {
                            //表示来自cache，然后要移除cache中对应的数据
                            removeUnReadById(imInfo.getCacheMessageId());
                        }
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
        //存到本地数据库里面，这样可以直接获取历史消息
        imCache.addHistory(transform2History(imInfo));
        //分发到ui
        MsgWatcher msgWatcher = msgWatcherList.get(imInfo.getPeerId());
        if (msgWatcher != null) {
            rtmHandler.post(() -> msgWatcher.onMessage(imInfo));
        }
    }

    private HistoryImInfo transform2History(ImInfo imInfo) {
//        List<ImInfo> imInfos = null;
//        for (HistoryImInfo historyImInfo : historyImInfos) {
//            if (imInfos == null) {
//                imInfos = new ArrayList<>();
//            }
//            ImInfo currentImInfo = new ImInfo(null, historyImInfo.getSource(), historyImInfo.getPeerId(), historyImInfo.getUserId());
//            imInfos.add(currentImInfo);
//            currentImInfo.setText(historyImInfo.getText());
//            currentImInfo.setMessageType(historyImInfo.getMessageType());
//            currentImInfo.setRawMessage(historyImInfo.getRawMessage());
//            currentImInfo.setWindow(historyImInfo.getWindow());
//            currentImInfo.setCacheMessageId(historyImInfo.id);
//            currentImInfo.setServerReceivedTs(historyImInfo.getServerReceivedTs());
//        }
//        return imInfos;
        HistoryImInfo historyImInfo = new HistoryImInfo();
        historyImInfo.setWindow(imInfo.getWindow());
        historyImInfo.setPeerId(imInfo.getPeerId());
        historyImInfo.setUserId(imInfo.getUserId());
        historyImInfo.setServerReceivedTs(imInfo.getServerReceivedTs());
        historyImInfo.setSource(imInfo.getSource());
        historyImInfo.setText(imInfo.getText());
        historyImInfo.setRawMessage(imInfo.getRawMessage());
        historyImInfo.setMessageType(imInfo.getMessageType());

        return historyImInfo;
    }

    private void catchTargetAndRemove(ImInfo imInfo) {
        if (imInfo == head) {
            //如果当前节点是head
            head = imInfo.next;
            imInfo.next = null;
            pre = head;
        } else {
            if (pre != null) {
                ImInfo t = imInfo.next;
                imInfo.next = null;
                pre.next = t;
            }
        }
    }

    //--------------------对外暴露的三个接收消息的接口-------------------
    //addFromLocal是本地输入框点击发送之后调用
    //addFromNet是接收到好友发来的消息之后调用
    //addFromUnReadCache所有未读的消息会被添加到本地数据库中，等再次启动app之后调用¬
    public void addFromLocal(RtmMessage message, int source, String peerId) {
        String window = selfUserId + peerId;
        add(-1, null, message, source, selfUserId, selfUserId, window);
    }

    public void addFromNet(RtmMessage message, int source, String peerId) {
        String window = selfUserId + peerId;
        add(-1, null, message, source, peerId, selfUserId, window);
    }

    @SuppressLint("LongLogTag")
    public void addFromUnReadCache(UnReadImInfo unReadImInfo, int source, String peerId, String userId) {
        Log.i(TAG, "addFromUnReadCache");
        add(unReadImInfo.id, unReadImInfo, null, source, peerId, userId, unReadImInfo.getWindow());
    }

    /**
     * 添加消息到列表中
     *
     * @param cacheId
     * @param message
     * @param source
     * @param peerId  peerId多数情况下是给你发消息的用户的Id或者是频道id，但是基于现有的设计peerId也可能是自己的userId
     * @param window
     */
    private void add(int cacheId, UnReadImInfo unReadImInfo, RtmMessage message, int source, String peerId, String userId, String window) {
        add(cacheId, unReadImInfo, message, source, peerId, userId, window, false);
    }

    @SuppressLint("LongLogTag")
    @Override
    public void add(int cacheId, UnReadImInfo unReadImInfo, RtmMessage message, int source, String peerId, String userId, String window, boolean toHead) {
        Log.i(TAG, "Station add");
        synchronized (mLock) {
            ImInfo imInfo = new ImInfo(message, source, peerId, userId);
            imInfo.setWindow(window);
            imInfo.setCacheMessageId(cacheId);
            imInfo.setSelf(peerId.equals(userId));
            if (message == null && unReadImInfo != null) {
                imInfo.setText(unReadImInfo.getText());
                imInfo.setMessageType(unReadImInfo.getMessageType());
                imInfo.setServerReceivedTs(unReadImInfo.getServerReceivedTs());
                imInfo.setRawMessage(unReadImInfo.getRawMessage());
            }
            if (toHead) {
                addToHead(imInfo);
            } else {
                addToTail(imInfo);
            }
            msgCount++;
            if (!self(peerId) && !inRoom(peerId)) {//不是自己发的消息
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

    @Override
    public void add(RtmMessage message, int souce, String peerId, String userId, boolean toHead) {

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

    private boolean started = false;

    @SuppressLint("LongLogTag")
    @Override
    public void start() {
        if (started) return;
        Log.i("RtmMessageCentralStation", "RtmMessageCentralStation start");
        started = true;
        //先从本地缓存里面取没有看的消息
        synchronized (mLock) {
            getFromCache();
        }
        //然后开启循环
        loop();
    }

    @SuppressLint("LongLogTag")
    private void getFromCache() {
        executorService.submit(() -> {
            List<UnReadImInfo> cacheList = imCache.getRunReadInfo();
            for (UnReadImInfo unReadImInfo : cacheList) {
                Log.i(TAG, "From Cache unReadImInfo---" + unReadImInfo.toString());
                addFromUnReadCache(unReadImInfo, unReadImInfo.getSource(), unReadImInfo.getPeerId(), unReadImInfo.getUserId());
            }
        });
    }

    private void removeUnReadById(int id) {
        imCache.removeUnReadById(id);
    }

    @SuppressLint("LongLogTag")
    private void addToCache() {
        executorService.execute(() -> {
            int count = 0;
            while (head != null) {
                count++;
                UnReadImInfo unReadImInfo = new UnReadImInfo();
                if (head.getTarget() != null) {
                    unReadImInfo.setText(head.getTarget().getText());
                    unReadImInfo.setRawMessage(head.getTarget().getRawMessage());
                    unReadImInfo.setServerReceivedTs(head.getTarget().getServerReceivedTs());
                    unReadImInfo.setMessageType(head.getTarget().getMessageType());
                } else {
                    unReadImInfo.setText(head.getText());
                    unReadImInfo.setRawMessage(head.getRawMessage());
                    unReadImInfo.setServerReceivedTs(head.getServerReceivedTs());
                    unReadImInfo.setMessageType(head.getMessageType());
                }
                unReadImInfo.setWindow(head.getWindow());
                unReadImInfo.setPeerId(head.getPeerId());
                unReadImInfo.setUserId(head.getUserId());
                unReadImInfo.setSource(2);

                Log.i(TAG, "unReadImInfo---" + Thread.currentThread() + unReadImInfo.toString());
                Log.i(TAG, "当前线程---" + Thread.currentThread());
                Log.i(TAG, "count---" + count);
                imCache.addUnread(unReadImInfo);
                head = head.next;
            }
        });
    }

    @Override
    public void stop() {
        block = true;
//        msgWatcherList.clear();
        //存到本地缓存，如果还有没来得及看的消息
        synchronized (mLock) {
            addToCache();
        }
    }

    private static class RtmHandler extends Handler {

        public RtmHandler(Looper mainLooper) {
            super(mainLooper);
        }
    }
}
