package com.lc.im;

import com.lc.im.model.UnReadImInfo;

/**
 * created by lvchao 2023/5/7
 * describe:
 */
public abstract class MessageCentralStation<T> {

    public abstract void add(int cacheId, UnReadImInfo unReadImInfo, T message, int source, String peerId, String userId, String window, boolean toHead);
    public abstract void add(T message, int source, String peerId, String userId, boolean toHead);

    public abstract void start();

    public abstract void stop();
}
