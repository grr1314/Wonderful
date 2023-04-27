package com.example.myapplication.channel

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel

class ChannelDemo {
    private val channel : Channel<Int> by lazy {
        Channel(Channel.RENDEZVOUS)
    }
    private val scope: CoroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    fun test(){
        runBlocking {
            val producer=scope.launch {
                var i = 0
                while (true) {
                    channel.send(i++)
                    delay(1000)
                }
            }
            val consumer = scope.launch {
                while (true){
                    val element = channel.receive()
                    println("result:$element")
                }
            }
//            producer.join()
//            consumer.join()
        }

    }






}