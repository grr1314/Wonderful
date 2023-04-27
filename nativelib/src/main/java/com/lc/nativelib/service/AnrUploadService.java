package com.lc.nativelib.service;

import android.app.IntentService;

import java.io.File;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class AnrUploadService extends AnrDataHandler {
    public AnrUploadService() {
        super("AnrUploadService");
    }


    @Override
    public void doAction(File[] tempList) {

    }

    @Override
    public void onComplete() {

    }
}