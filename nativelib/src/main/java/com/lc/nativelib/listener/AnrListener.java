package com.lc.nativelib.listener;

import com.lc.nativelib.model.MessageInfo;

import java.io.File;
import java.util.ArrayDeque;

public interface AnrListener {
    void singleWarningMessage(MessageInfo info);

    void anrEvent(ArrayDeque<MessageInfo> arrayDeque);

    void uploadAnrFile(File anrFile);
}
