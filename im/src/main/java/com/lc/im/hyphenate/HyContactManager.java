package com.lc.im.hyphenate;

import android.util.Log;

import com.hyphenate.EMContactListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.lc.im.hyphenate.listener.HyContactsListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * created by lvchao 2023/5/11
 * describe: 好友管理器
 */
public class HyContactManager {
    private List<HyContactsListener> hyContactsListeners = new ArrayList<>();
    private int allContractTimes = 0;
    private ExecutorService executorService;
    private HyImClient.HyHandler hyHandler;

    private HyUserInfoManager hyUserInfoManager;

    private HyUserInfoCache hyUserInfoCache;

    public HyContactManager(HyUserInfoManager hyUserInfoManager, HyUserInfoCache hyUserInfoCache) {
        this.hyUserInfoManager = hyUserInfoManager;
        this.hyUserInfoCache = hyUserInfoCache;
    }

    public void registerListener(HyContactsListener hyContactsListener) {
        hyContactsListeners.add(hyContactsListener);
    }

    /**
     * 获取好友列表
     * 1）环信SDK推荐先从网络中获取之后才能从本地缓存中获取，所以第一次调用该函数的时候必须要从网络获取。
     * 2）环信SDK返回300错误码的时候，从本地缓存中获取，兜底
     *
     * @param fromServer
     * @return
     */
    public void getAllContacts(boolean fromServer) {
        executorService.execute(() -> {
            List<String> usernames = null;
            if (fromServer) allContractTimes = 0;
            try {
                if (allContractTimes == 0) {
                    usernames = EMClient.getInstance().contactManager().getAllContactsFromServer();
                } else {
                    usernames = EMClient.getInstance().contactManager().getContactsFromLocal();
                }
                allContractTimes++;
                for (HyContactsListener hyContactsListener : hyContactsListeners) {
                    List<String> finalUsernames = usernames;
                    hyHandler.post(() -> hyContactsListener.getAllContractsSuccess(finalUsernames));
                }
            } catch (HyphenateException hyphenateException) {
                hyphenateException.printStackTrace();
                Log.w("HyImClient", "获取失败！原因是：+" + hyphenateException.getDescription() + " code 是" + hyphenateException.getErrorCode());
                for (HyContactsListener hyContactsListener : hyContactsListeners) {
                    hyHandler.post(() -> hyContactsListener.getAllContractsFail(hyphenateException.getErrorCode(), hyphenateException.getDescription()));
                }
                //处理300错误
                if (fromServer) {
                    switch (hyphenateException.getErrorCode()) {
                        case 300: {
                            getAllContacts(false);
                        }
                        break;
                    }
                } else {

                }
            }
        });
    }

    public void replyContact(boolean accept, String userName) {
        try {
            if (accept) {
                EMClient.getInstance().contactManager().acceptInvitation(userName);
            } else {
                EMClient.getInstance().contactManager().declineInvitation(userName);
            }
        } catch (HyphenateException hyphenateException) {
            hyphenateException.printStackTrace();
            Log.w("HyImClient", "回应失败！原因是：+" + hyphenateException.getDescription() + " code 是" + hyphenateException.getErrorCode());
        }
    }

    public void addEMContactListener(EMContactListener em) {
        EMClient.getInstance().contactManager().setContactListener(em);
    }

    public void addContact(String toAddUsername, String reason) {
        try {
            EMClient.getInstance().contactManager().addContact(toAddUsername, reason);
        } catch (HyphenateException hyphenateException) {
            Log.w("HyImClient", "添加失败！原因是：+" + hyphenateException.getDescription() + " code 是" + hyphenateException.getErrorCode());
        }
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    public void setHandler(@NotNull HyImClient.HyHandler handler) {
        this.hyHandler = handler;
    }
}
