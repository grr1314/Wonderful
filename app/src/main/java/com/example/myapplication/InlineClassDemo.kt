package com.example.myapplication

@JvmInline
value class InlineClassDemo(private val index: Int) : InlineDemo {
    override fun test() {
    }

    fun add(): Int {
        var currentIndex=index
        return currentIndex++
    }
}

@JvmInline
value class InlineClassString(private val str: String) {
    val stringLength: Int
        get() = str.length

    fun add(endStr: String): String {
        return str + endStr
    }
}

interface InlineDemo {
    fun test()
}

class InlineFunClass{
    //test inline fun
    fun inlineFun(){
        add1 {

        }
        add2 {
            println("23")
            return
        }
    }
    fun add1(block:() -> Unit){
        println("1")
    }
    private inline fun add2(block:() -> Unit){
        println("2")
        block.invoke()
    }
}