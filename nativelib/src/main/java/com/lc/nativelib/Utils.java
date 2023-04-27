package com.lc.nativelib;

import static android.content.Context.UI_MODE_SERVICE;

import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;

public class Utils {
    /**
     * 判断当前应用是不是InstantApp
     * // Instant apps cannot show background notifications
     * // See https://github.com/square/leakcanary/issues/1197
     * // TV devices also can't do notifications
     *
     * @param context
     * @return
     */
    public static boolean isInstantApp(Context context) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && context.getPackageManager().isInstantApp();
    }

    /**
     * 判断当前设备类型
     *
     * @param context
     * @return
     */
    public static FromFactor formFactor(Context context) {
        UiModeManager uiModeManager = (UiModeManager) context.getSystemService(UI_MODE_SERVICE);
        int type = uiModeManager.getCurrentModeType();
        FromFactor result;
        switch (type) {
            case Configuration.UI_MODE_TYPE_TELEVISION: {
                result = FromFactor.TV;
            }
            break;
            case Configuration.UI_MODE_TYPE_WATCH: {
                result = FromFactor.WATCH;
            }
            break;
            default:
                result = FromFactor.MOBILE;
                break;
        }
        return result;
    }

    public static boolean canShowBackgroundNotifications(Context context) {
        return formFactor(context) == FromFactor.MOBILE && !isInstantApp(context);
    }

    public static boolean canShowNotification(Context context) {
        return canShowBackgroundNotifications(context);
    }
}
