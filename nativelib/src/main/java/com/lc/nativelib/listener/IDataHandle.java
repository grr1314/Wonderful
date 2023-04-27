package com.lc.nativelib.listener;

import java.io.File;

public interface IDataHandle {

    void doAction(File[] tempList);

    void onComplete();
}
