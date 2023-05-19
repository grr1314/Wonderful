package com.lc.im.hyphenate.listener;

import com.lc.im.model.HyConversationInfo;

import java.util.List;

/**
 * created by lvchao 2023/5/14
 * describe:
 */
public interface ConversationListener<T> {
    public void startLoad();

    public void loadSuccess(List<T> tList);

    public void newConversation(HyConversationInfo hyConversationInfo);
}
