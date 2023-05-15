package com.lc.im;

import com.lc.im.model.ImInfo;

import java.util.List;

/**
 * created by lvchao 2023/5/7
 * describe:
 */
public interface MsgWatcher {
    public void onMessage(ImInfo imInfo);

    public void unReadCount(int count);

    public void startLoadHistoryMessage();

    public void loadHistoryMessageSuccess(List<ImInfo> imInfoList);
    public void loadHistoryMessageFail();
    public void loadHistoryMessageComplete();
}
