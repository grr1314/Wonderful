package com.lc.im.hyphenate;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMUserInfo;
import com.lc.im.UserInfoManager;
import com.lc.im.hyphenate.listener.ConversationListener;
import com.lc.im.model.HyConversationInfo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;

/**
 * created by lvchao 2023/5/11
 * describe:
 */
public class HyConversationManager {
    //记录当前页面展示了哪些会话
    private Set<String> resumeConversationSet = new HashSet<>();
    private HyUserInfoManager hyUserInfoManager;

    private HyImClient.HyHandler hyHandler;

    private HyUserInfoCache hyUserInfoCache;
    private ExecutorService executorService;
    private ConversationListener<HyConversationInfo> listener;


    public Map<String, EMConversation> getAllConversations() {
        return EMClient.getInstance().chatManager().getAllConversations();
    }

    /**
     * 获取当前用户的所有会话，并且含有用户信息
     *
     * @param listener
     */
    public void getAllConversationsWithInfo(ConversationListener<HyConversationInfo> listener) {
        if (listener != null) listener.startLoad();
        executorService.execute(() -> {
            Map<String, EMConversation> conversationMap = getAllConversations();
            List<HyConversationInfo> hyConversationInfoList = new ArrayList<>();
            for (Map.Entry<String, EMConversation> entry : conversationMap.entrySet()) {
                EMConversation emConversation = entry.getValue();
                EMConversation.EMConversationType type = emConversation.getType();
                HyConversationInfo hyConversationInfo = new HyConversationInfo();
                hyConversationInfoList.add(hyConversationInfo);
                switch (type) {
                    case Chat: {
                        String userId = emConversation.getLatestMessageFromOthers().getFrom();
                        hyUserInfoManager.getSingleUserInfo(userId, new UserInfoManager.UserInfoListener<EMUserInfo>() {
                            @Override
                            public void getAllUserInfo(List<EMUserInfo> uinfos) {

                            }

                            @Override
                            public void getSingleUserInfo(EMUserInfo emUserInfo) {
                                hyConversationInfo.setEmUserInfo(emUserInfo);
                                hyConversationInfo.setNickName(emUserInfo.getNickname());
                            }

                            @Override
                            public void setSingleInfoSuccess(String type) {

                            }

                            @Override
                            public void setUserInfoSuccess(String success) {

                            }

                            @Override
                            public void onError(String error) {

                            }
                        }, true);
                    }
                    break;
                }
                hyConversationInfo.setUnReadCount(emConversation.getUnreadMsgCount());
                hyConversationInfo.setLastTime(emConversation.getLastMessage().getMsgTime());
                hyConversationInfo.setLastTimeStamp("");
                hyConversationInfo.setConversationId(emConversation.conversationId());
                hyConversationInfo.setLastMsgContent(emConversation.getLastMessage().getBody().toString());
                resumeConversationSet.add(emConversation.conversationId());
            }
            if (listener != null) {
                hyHandler.post(() -> listener.loadSuccess(hyConversationInfoList));
            }
        });
    }

    public HyConversationInfo getTargetConversationWithInfo(String conversationId) {
        HyConversationInfo hyConversationInfo = new HyConversationInfo();
        EMConversation emConversation = getTargetConversation(conversationId);
        

        return hyConversationInfo;
    }


    public HyConversationManager(HyUserInfoManager hyUserInfoManager, HyImClient.HyHandler hyHandler, HyUserInfoCache hyUserInfoCache) {
        this.hyUserInfoManager = hyUserInfoManager;
        this.hyHandler = hyHandler;
        this.hyUserInfoCache = hyUserInfoCache;
    }


    /**
     * 获取指定的会话
     *
     * @param username 是对方的账号
     * @return
     */
    public EMConversation getTargetConversation(String username) {
        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        return conversation;
    }

    /**
     * @param username
     * @return
     */
    public List<EMMessage> getAllMessageInConversation(String username) {
        return getTargetConversation(username).getAllMessages();
    }

    public void checkConversation(String conversationId) {
        if (!resumeConversationSet.contains(conversationId)) {//表示没有被展示过
            if (listener != null) {
                listener.newConversation(getTargetConversationWithInfo(conversationId));
            }
        }
    }

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

}
