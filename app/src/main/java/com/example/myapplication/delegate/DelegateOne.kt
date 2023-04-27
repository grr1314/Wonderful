package com.example.myapplication.delegate

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 练习委托，所谓的委托其实就是代理模式只是翻译不同
 */
class DelegateOne : ReadWriteProperty<Any?, String> {
    var curValue = "default"
    override fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return curValue
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        curValue = value
    }
}