package com.lc.im;

import static io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_CONNECTED;
import static io.agora.rtm.RtmStatusCode.ConnectionState.CONNECTION_STATE_RECONNECTING;

import android.content.Context;
import android.util.Log;

import com.example.mylibrary.callback.ImListener;
import com.example.mylibrary.service.ImModuleService;

import io.agora.rtm.ErrorInfo;
import io.agora.rtm.ResultCallback;

/**
 * created by lvchao 2023/5/6
 * describe:
 */
public class ImModuleServiceImpl implements ImModuleService {
    private Context context;

    @Override
    public void attachContext(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public boolean imLoginState() {
        final ImRtmClient imRtmClient = ImRtmClient.INSTANCE;
        int state = imRtmClient.getCurrentState();
        return state == CONNECTION_STATE_CONNECTED || state == CONNECTION_STATE_RECONNECTING;
    }

    @Override
    public void imLogin(String token, String userId) {
        final ImRtmClient imRtmClient = ImRtmClient.INSTANCE;
        Log.w("ImModuleServiceImpl", " userId= " + userId);

        imRtmClient.login(token, userId, new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.w("ImModuleServiceImpl", "im login success");
                RtmMessageCentralStation rtmMessageCentralStation = imRtmClient.getRtmMessageCentralStation();
                if (rtmMessageCentralStation != null) {
                    rtmMessageCentralStation.start();
                    rtmMessageCentralStation.setSelfUserId(userId);
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.w("ImModuleServiceImpl", "im login fail with code = " + errorInfo.getErrorCode() + " and message = " + errorInfo.getErrorDescription());
            }
        });
    }

    @Override
    public void imLogout() {
        final ImRtmClient imRtmClient = ImRtmClient.INSTANCE;

        imRtmClient.logout(new ResultCallback<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.w("ImModuleServiceImpl", "im logout success");
                RtmMessageCentralStation rtmMessageCentralStation = imRtmClient.getRtmMessageCentralStation();
                if (rtmMessageCentralStation != null) {
                    rtmMessageCentralStation.stop();
                    rtmMessageCentralStation.setSelfUserId(null);
                }
            }

            @Override
            public void onFailure(ErrorInfo errorInfo) {
                Log.w("ImModuleServiceImpl", "im logout fail with code = " + errorInfo.getErrorCode() + " and message = " + errorInfo.getErrorDescription());
            }
        });
    }

    @Override
    public int unReadCount(int count) {
        return 0;
    }

    @Override
    public void addImListener(ImListener imListener) {
        final ImRtmClient imRtmClient = ImRtmClient.INSTANCE;
        imRtmClient.addListener(imListener);
    }

    @Override
    public void removeImListener(ImListener imListener) {
        final ImRtmClient imRtmClient = ImRtmClient.INSTANCE;
        imRtmClient.removeListener(imListener);
    }

}
