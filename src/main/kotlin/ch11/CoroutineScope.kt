package ch11

import kotlinx.coroutines.*

// coroutineScope는 비차단 스코프
// runBlocking는 차단 스코프 스코프내의 모든 코루틴이 끝날때 까지 스코프의 스레드는 차단된다
// 새로운 코루틴이 끝날때 까지 호출한 코루틴은 중단됨
// 부모로부터 컨텍스트 상속받음
//fun main() = runBlocking {
//    val a = coroutineScope {
//        delay(1000)
//        10
//    }
//    println("우우우웅")
//    val b = coroutineScope {
//        delay(1000)
//        30
//    }
//
//    println(a)
//    println(b)
//}
// coroutineScope는 부모 Context를 상속받는 다는 증거
//suspend fun longTask() = coroutineScope {
//    launch {
//        delay(1000)
//        val name = coroutineContext[CoroutineName]?.name //Parent
//        println("[$name] Finished task1")
//    }
//    launch {
//        delay(2000)
//        val name = coroutineContext[CoroutineName]?.name //Parent
//        println("[$name] Finished task2")
//    }
//}
//
//fun main() = runBlocking(CoroutineName("Parent")) {
//    println("Before")
//    longTask()
//    println("After")
//}
// 부모 코루틴이 취소되면 자식 코루틴도 같이 취소 된다
suspend fun longTask() = coroutineScope {
    launch {
        delay(1000)
        val name = coroutineContext[CoroutineName]?.name //Parent
        println("[$name] Finished task1")
    }
    launch {
        delay(2000)
        val name = coroutineContext[CoroutineName]?.name //Parent
        println("[$name] Finished task2")
    }
}

fun main() = runBlocking {
    val job = launch(CoroutineName("Parent")) {
        longTask()
    }
    delay(1500)
    job.cancel()

}