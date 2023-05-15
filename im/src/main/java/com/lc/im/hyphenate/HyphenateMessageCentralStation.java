package com.lc.im.hyphenate;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.mylibrary.callback.ImListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.lc.im.MessageCentralStation;
import com.lc.im.MsgWatcher;
import com.lc.im.Source;
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

/**
 * created by lvchao 2023/5/10
 * describe:
 */
public class HyphenateMessageCentralStation extends MessageCentralStation<EMMessage> {
    private static final String TAG = "HyphenateMessage";
    private final ExecutorService executorService;
    private static final int DEFAULT_THREAD_COUNT = 10;
    private final Map<String, MsgWatcher> msgWatcherList;
    private final Set<ImListener> imListeners;
    private boolean started = false;
    private final ImCache imCache;

    private ImInfo head;
    private ImInfo tail;

    private ImInfo currentInfo;

    private ImInfo pre;
    private final Object mLock = new Object();
    private boolean block;

    private String selfUserId;
    private String peerId;//对应conversationId

    private int unReadCount;
    private int msgCount;
    private final HyImClient.HyHandler hyHandler;

    private HyConversationManager hyConversationManager;

    private HyUserInfoManager hyUserInfoManager;

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

    public HyphenateMessageCentralStation(ImCache imCache, HyImClient.HyHandler handler, HyConversationManager hyConversationManager, HyUserInfoManager hyUserInfoManager) {
        msgWatcherList = new HashMap<>();
        executorService = Executors.newFixedThreadPool(DEFAULT_THREAD_COUNT);
        imListeners = new HashSet<>();
        this.hyHandler = handler;
        this.hyConversationManager = hyConversationManager;
        this.hyUserInfoManager = hyUserInfoManager;
        //创建一个本地数据库的管理类
        this.imCache = imCache;
    }

    @Override
    public void add(int cacheId, UnReadImInfo unReadImInfo, EMMessage message, int source, String peerId, String userId, String window, boolean toHead) {

    }

    @Override
    public void add(EMMessage message, int source, String peerId, String userId, boolean toHead) {
        Log.i(TAG, "Station add");
        synchronized (mLock) {
            ImInfo imInfo = new ImInfo(null, source, peerId, userId);
            imInfo.setEmMessage(message);
            imInfo.setSelf(selfUserId.equals(message.getFrom()));
            if (toHead) {
                addToHead(imInfo);
            } else {
                addToTail(imInfo);
            }
            msgCount++;
            if (!self(message.getFrom()) && !inRoom(peerId)) {//不是自己发的消息
                unReadCount++;
                //通知所有订阅方，未读数发生变化
                for (Map.Entry<String, MsgWatcher> next : msgWatcherList.entrySet()) {
                    hyHandler.post(() -> next.getValue().unReadCount(unReadCount));
                }
                for (ImListener imListener : imListeners) {
                    hyHandler.post(() -> imListener.unReadCountChange(unReadCount));
                }
            }
            if (source != Source.CACHE) {
                loop();
            }
        }
    }

    private void addToCache(List<EMMessage> msgs) {
        executorService.execute(() -> {
            EMClient.getInstance().chatManager().importMessages(msgs);
        });
    }

    @Override
    public void start() {
        if (started) return;
        started = true;
        EMClient.getInstance().chatManager().loadAllConversations();
    }

    @Override
    public void stop() {
        started = false;
    }


    public void addWatcher(String peerId, MsgWatcher msgWatcher) {
        msgWatcherList.put(peerId, msgWatcher);
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


    //--------------------对外暴露的三个接收消息的接口-------------------
    //addFromLocal是本地输入框点击发送之后调用
    //addFromNet是接收到好友发来的消息之后调用
    //addFromCache接收来自Cache中的消息
    public void addFromLocal(EMMessage message, int source, String peerId) {
        add(message, source, peerId, selfUserId, false);
    }

    public void addFromNet(EMMessage message, int source, String peerId) {
        add(message, source, peerId, selfUserId, false);
    }

    @SuppressLint("LongLogTag")
    public void addFromCache(EMMessage message, int source, String peerId, String userId) {
        add(message, source, peerId, selfUserId, false);
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
     * @param from
     * @return
     */
    private boolean self(String from) {
        return selfUserId.equals(from);
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
                Log.i(TAG, "current:" + imInfo.getEmMessage().getBody().toString());
            }
            return imInfo;
        }
    }

    /**
     * 遍历链表中所有的数据
     */
    private void loop() {
        Log.i(TAG, "Station start loop");
        synchronized (mLock) {
            block = false;
            executorService.execute(() -> {
                while (!block && started) {
                    Log.i(TAG, "Station  looping");
                    ImInfo imInfo = next();
                    if (imInfo == null) {
                        block = true;
                        Log.i(TAG, "Station  looping end");
                        currentInfo = head;
                    } else {
                        //去匹配对应的观察者，如果有对应的则要从链表中移除
                        catchTargetAndRemove(imInfo);
                        if (msgWatcherList.containsKey(imInfo.getPeerId())) {
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
    }

    private void dispatch(ImInfo imInfo) {
        //分发到ui
        MsgWatcher msgWatcher = msgWatcherList.get(imInfo.getPeerId());
        if (msgWatcher != null) {
            hyHandler.post(() -> msgWatcher.onMessage(imInfo));
        }
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

    @SuppressLint("LongLogTag")
    public void loadHistoryMessage() {
        executorService.execute(() -> {
            EMConversation conversation = hyConversationManager.getTargetConversation(peerId);
//            List<EMMessage> cacheList = conversation.getAllMessages();
            List<EMMessage> cacheList = conversation.loadMoreMsgFromDB(conversation.getLastMessage().getMsgId(), 40);
//            cacheList.add(conversation.getLastMessage());
            for (EMMessage emMessage : cacheList) {
                Log.i(TAG, "From Cache unReadImInfo---" + emMessage.getBody().toString());
                addFromCache(emMessage, Source.CACHE, peerId, selfUserId);
            }
            loop();
        });
    }

    public void loadMoreHistoryMessage() {
        final MsgWatcher watcher = msgWatcherList.get(peerId);
        if (watcher != null) {
            watcher.startLoadHistoryMessage();
        }
        executorService.execute(() -> {
            EMConversation conversation = hyConversationManager.getTargetConversation(peerId);
//            List<EMMessage> emMessages = conversation.getAllMessages();

            List<EMMessage> emMessages = conversation.loadMoreMsgFromDB(conversation.getLastMessage().getMsgId(), 20);
            List<ImInfo> imInfos = transform(emMessages);
            hyHandler.post(() -> {
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

    private List<ImInfo> transform(List<EMMessage> historyImInfos) {
        List<ImInfo> imInfos = null;
        for (EMMessage historyImInfo : historyImInfos) {
            if (imInfos == null) {
                imInfos = new ArrayList<>();
            }
            ImInfo currentImInfo = new ImInfo(null, Source.CACHE, peerId, selfUserId);
            currentImInfo.setEmMessage(historyImInfo);
            imInfos.add(currentImInfo);
        }
        return imInfos;
    }

    private void catchTargetAndRemove(ImInfo imInfo) {
        head = imInfo.next;
        imInfo.next = null;
        if (imInfo.getSource() != Source.CACHE) {
            List<EMMessage> emMessageList = new ArrayList<>();
            emMessageList.add(imInfo.getEmMessage());
            addToCache(emMessageList);
        }
    }
}
