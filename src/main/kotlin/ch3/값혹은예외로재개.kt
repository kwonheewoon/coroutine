package ch3

import java.util.concurrent.Executors
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.*
import kotlin.coroutines.suspendCoroutine

// 값으로 재
//data class User(val name: String)
//
//suspend fun requestUser(): User {
//    return suspendCoroutine<User> { continuation ->
//        requestUser{
//            user -> continuation.resume(user)
//        }
//    }
//}
//
//suspend fun main(){
//    println("Before")
//    val user = suspendCoroutine<User> { continuation ->
//        val user = continuation.resume(user)
//        println(user)
//        println("After")
//    }
//}

//class MyException : Throwable("Just an exception")
//
//suspend fun main(){
//    try {
//        suspendCoroutine<Unit> { cont ->
//            cont.resumeWithException(MyException())
//         }
//    }catch (e: MyException){
//        println("Caught!")
//    }
//}

private val executor = Executors.newSingleThreadScheduledExecutor{
    Thread(it, "scheduler").apply { isDaemon = true }
}

var continuation: Continuation<Unit>? = null

suspend fun suspendAndSetContinuation(){
    suspendCoroutine<Unit> { cont ->
        continuation = cont
    }
}

suspend fun main() = coroutineScope {
    println("Before")


    this.launch {
        delay(1000)
        continuation?.resume(Unit)
    }

    suspendAndSetContinuation()

    delay(2000)
    println("After")
}

//suspend fun main(){
//    println("Before")
//
//    suspendAndSetContinuation()
//    continuation?.resume(Unit)
//    println("After")
//}

