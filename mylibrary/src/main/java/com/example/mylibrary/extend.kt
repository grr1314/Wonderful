package com.example.mylibrary

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlin.reflect.KProperty

fun <T : View> Activity.findView(viewId: Int) = FindView<T>(viewId)

@JvmInline
value class FindView<T>(private val viewId: Int) {
    operator fun getValue(activity: Activity, kProperty: KProperty<*>): T {
        return activity.findViewById(viewId)
    }
}


// loadUrl扩展函数接收者类型是ImageView
fun ImageView.loadUrl(
    context: Context, url: String = ""
) {
    // this 指代的是ImageView这个接收者对象实例, 这里this也可以省略
    Glide.with(context).load(url).into(this)
}