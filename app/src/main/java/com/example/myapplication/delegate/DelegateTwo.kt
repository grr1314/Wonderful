package com.example.myapplication.delegate
import kotlin.reflect.KProperty

class DelegateTwo {
    var currentValue="DelegateTwo"
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return currentValue
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, s: String) {
        currentValue=s
    }
}