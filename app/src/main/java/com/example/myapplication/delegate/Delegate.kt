package com.example.myapplication.delegate

import kotlin.reflect.KProperty

operator fun DelegateThree.setValue(thisRef: Any?, property: KProperty<*>, value: String) {

}

operator fun DelegateThree.getValue(thisRef: Any?, property: KProperty<*>): String {
    return "DelegateThree"
}



