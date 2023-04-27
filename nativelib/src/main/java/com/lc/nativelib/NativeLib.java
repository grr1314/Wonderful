package com.lc.nativelib;

import com.lc.nativelib.listener.ISystemAnrObserver;

public class NativeLib {

    // Used to load the 'nativelib' library on application startup.
    static {
        System.loadLibrary("nativelib");
    }

    /**
     * A native method that is implemented by the 'nativelib' native library,
     * which is packaged with this application.
     */
    public native void anrMonitor(ISystemAnrObserver observed);

    private NativeLib() {

    }

    public static NativeLib getInstance() {
        return NativeLibHolder.lib;
    }

    private static class NativeLibHolder {
        private static final NativeLib lib = new NativeLib();
    }

}