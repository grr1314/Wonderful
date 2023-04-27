package com.example.myapplication.flow

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FlowDemo {
    private val tag: String by lazy {
        FlowDemo::class.java.simpleName
    }
    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.Main)
    }
    fun CoroutineScope.produceNumbers() = produce {
        var x = 1
        while (true) send(x++) // 在流中开始从 1 生产无穷多个整数
    }
    //发射器
    fun test() {
//        runBlocking {
//            val numbers = produceNumbers()
//            produce<Int>(Dispatchers.IO){
//
//            }
//        }
        scope.produceNumbers()
        scope.produce<Int>(Dispatchers.IO){

        }
        //流是冷的这句话的意思是，flow构建器中的代码只有调用了collect函数之后才会执行，
        scope.launch {
            flowOf(1,2,3).dropWhile {
                it < 2
            }.transform<Int,String> {

            }.collect {
                println("Collect: $it")
            }
            flow {
                println("send message")//发送消息
                Log.e(tag, Thread.currentThread().toString()+"flow")
                emit(1)
                println("send message 1")//发送消息
            }.onStart{

            }.onCompletion{

            }.filter {
                Log.e(tag, Thread.currentThread().toString()+"filter")
                it == 1//这个是过滤条件
            }.retry(2).map {
                "mapped $it"
            }.flowOn(Dispatchers.IO)
                .collect {
                Log.e(tag, Thread.currentThread().toString())
                println("message is $it")//消费者处理数据
            }
        }
    }
}