package ch9

import kotlinx.coroutines.*
import kotlin.random.Random

// 취소 과정을 기다리는 job.join을 호출하지 않았기 때문에
// Cancelled successfully 출력 후 Printing 4 출력됨
// 경쟁상태 발생 가능
//suspend fun main(): Unit = coroutineScope {
//    val job = launch {
//        repeat(1_000) {i ->
//            delay(100)
//            Thread.sleep(100)
//            println("Printing $i")
//        }
//    }
//
//    delay(1000)
//    job.cancel()
//    println("Cancelled successfully")
//}

// Job.cancelAndJoin() 확장함수는 cancel 메소드와 join메소드를 순차 호출하는 확자함수
//suspend fun main(): Unit = coroutineScope {
//    val job = Job()
//    launch(job) {
//        repeat(1_000){
//            i -> delay(200)
//            println("Printing $i")
//        }
//    }
//    delay(1100)
//    job.cancelAndJoin()
//    println("Cancelled successfully")
//}
//
//suspend fun main(): Unit = coroutineScope {
//    val job = Job()
//    launch(job) {
//        try {
//            delay(200)
//            println("Coroutine finished")
//        } finally {
//            println("Finally")
//            withContext(NonCancellable){
//                delay(1000)
//                println("Cleanup done")
//            }
//        }
//    }
//    delay(100)
//    job.cancelAndJoin()
//    println("Done")
//}
// invokeOnCompletion 잡이 Completed나 Cancelled와 같은 상태에 도달했을때 호출될 핸들러
//suspend fun main(): Unit = coroutineScope {
//    val job = launch {
//        delay(1000)
//    }
//    job.invokeOnCompletion { exception: Throwable? ->
//        println("Finished")
//    }
//    delay(400)
//    job.cancelAndJoin()
//}

//suspend fun main(): Unit = coroutineScope {
//    val job = launch {
//        delay(Random.nextLong(2400))
//        println("Finished")
//    }
//    delay(800)
//    job.invokeOnCompletion { exception: Throwable? ->
//        println("Will always be printed")
//        println("The exception was: $exception")
//    }
//    delay(800)
//    job.cancelAndJoin()
//}

// 코루틴 내부 중단점이 없는 소스
//suspend fun main(): Unit = coroutineScope {
//    val job = Job()
//    launch(job) {
//        repeat(1_000) { i ->
//            Thread.sleep(200) // 코루틴 중단지점이 아니기 때문에 잡 취소 요청 확인을 하지 못함
//            //yield() 코루틴을 중단하고 즉시 재실행 중단점이 생겼기 때문에 취소 요청 확인 가능
//            println("Printing $i")
//        }
//    }
//    delay(1000)
//    job.cancelAndJoin()
//    println("Cancelled successfully")
//    delay(1000)
//}

// 코루틴 내부 중단점이 없을때 Job의 Active 상태를 확인해 취소 요청 확인
//suspend fun main(): Unit = coroutineScope {
//    val job = Job()
//    launch(job) {
//        do {
//            Thread.sleep(200)
//            println("Printing")
//        } while (isActive)
//    }
//    delay(1000)
//    job.cancelAndJoin()
//    println("Cancelled successfully")
//}

// 코루틴 내부 중단점이 없을때 Job의 Active 상태를 확인해 아니면 ensureActive 함수를 이용해
// CancellationException 던지는 소스
suspend fun main(): Unit = coroutineScope {
    val job = Job()
    launch(job) {
        do {
            Thread.sleep(200)
            this.ensureActive()
            println("Printing")
        } while (isActive)
    }
    delay(1000)
    job.cancelAndJoin()
    println("Cancelled successfully")

    suspendCancellableCoroutine { cont ->
        cont.invokeOnCancellation {  }
    }
}