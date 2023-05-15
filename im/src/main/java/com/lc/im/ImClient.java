package com.lc.im;

import android.content.Context;

import com.example.mylibrary.callback.ImListener;

/**
 * created by lvchao 2023/5/11
 * describe:
 */
public interface ImClient<T> {

    public T with(Context context);

    public void addWatcher(String peerId, MsgWatcher msgWatcher);

    public void removeWatcher(String peerId);

    public void addListener(ImListener imListener);

    public void removeListener(ImListener imListener);


}
