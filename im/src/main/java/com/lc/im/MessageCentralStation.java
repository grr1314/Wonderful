package com.lc.im;

/**
 * created by lvchao 2023/5/7
 * describe:
 */
public abstract class MessageCentralStation<T> {

    public abstract void add(T message, int source, String peerId, String userId, String window, boolean toHead);

}
