package com.example.mylibrary

import android.app.Activity
import android.view.View
import kotlin.reflect.KProperty

fun <T : View> Activity.findView(viewId: Int)= FindView<T>(viewId)

@JvmInline
value class FindView<T>(private val viewId: Int) {
    operator fun getValue(activity: Activity, kProperty: KProperty<*>): T {
        return activity.findViewById(viewId)
    }
}