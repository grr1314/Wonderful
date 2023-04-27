package com.lc.nativelib

internal enum class NotificationType(val nameResId: Int, val importance: Int) {

    ANR(
        R.string.anr_notification_channel, IMPORTANCE_MAX
    ),
}

private const val IMPORTANCE_LOW = 2
private const val IMPORTANCE_NORMAL = 3
private const val IMPORTANCE_MAX = 5
