package com.lc.im;

import com.lc.im.model.ImInfo;

/**
 * created by lvchao 2023/5/7
 * describe:
 */
public interface MsgWatcher {
    public void onMessage(ImInfo imInfo);

    public void unReadCount(int count);
}
