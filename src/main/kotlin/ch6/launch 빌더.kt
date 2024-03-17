package ch6

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun main(){
    GlobalScope.launch {
        delay(1000L)
        println("World! 11")
    }

    GlobalScope.launch {
        delay(1000L)
        println("World! 22")
    }

    GlobalScope.launch {
        delay(1000L)
        println("World! 33")
    }

    GlobalScope.launch {
        delay(1000L)
        println("World! 44")
    }
    println("Hello,")
    Thread.sleep(2000L)
}