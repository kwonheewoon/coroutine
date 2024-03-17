package ch12

import kotlinx.coroutines.*
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random
import kotlin.system.measureTimeMillis

// 디스패처는 코루틴 스코프를 적절한 스레드에 배치하는 역할을 한다
// Dispatchers.default = cpu 집약적인 작업
// Dispatchers.IO = 블로킹 작업에 유리 (동적으로 스레드풀을 관리)

// CoroutineDispatcher, 일반적으로 Dispatchers에 의해 제공되는 것,
// 또한 ContinuationInterceptor를 확장합니다. 이는 CoroutineDispatcher가
// 코루틴의 실행을 어떤 스레드에서 처리할지를 결정할 뿐만 아니라, 코루틴의 연속(continuation)을
// 가로채어 코루틴의 실행 방식을 제어할 수도 있음을 의미합니다.

//suspend fun main() = coroutineScope {
//    repeat(1000) {
//        launch {
//            List(1000){
//                Random.nextLong()
//            }.maxOrNull()
//
//            val threadName = Thread.currentThread().name
//            println("Running on thread: $threadName")
//        }
//    }
//}

//suspend fun main(){
//    val time = measureTimeMillis {
//        coroutineScope {
//            repeat(50) {
//                launch(Dispatchers.IO){
//                    Thread.sleep(1000)
//                }
//            }
//        }
//    }
//
//    println(time)
//}

//suspend fun main() = coroutineScope {
//    repeat(1000) {
//        launch(Dispatchers.IO) {
//            Thread.sleep(200)
//
//            val threadName = Thread.currentThread().name
//            println("Running on thread: $threadName")
//        }
//    }
//}

//suspend fun main(): Unit = coroutineScope {
//    launch {
//        printCoroutinesTime(Dispatchers.IO)
//    }
//    launch {
//        val dispatcher = Dispatchers.IO.limitedParallelism(100)
//        printCoroutinesTime(dispatcher)
//    }
//}
//
//suspend fun printCoroutinesTime(dispatcher: CoroutineDispatcher){
//    val test = measureTimeMillis {
//        coroutineScope {
//            repeat(100){
//                launch(dispatcher) {
//                    Thread.sleep(1000)
//                }
//            }
//        }
//    }
//    println("$dispatcher took: $test")
//}

var i = 0

//suspend fun main(): Unit = coroutineScope {
//    repeat(10_000) {
//        launch(Dispatchers.IO) {
//            i++
//        }
//    }
//    delay(1000)
//    println(i)
//}

//suspend fun main(): Unit = coroutineScope {
//    val dispatcher = Dispatchers.Default.limitedParallelism(1)
//    repeat(10_000) {
//        launch(dispatcher) {
//            i++
//        }
//    }
//    delay(1000)
//    println(i)
//}

//suspend fun main(): Unit = coroutineScope {
//    val dispatcher = Dispatchers.Default.limitedParallelism(1)
//
//    val job = Job()
//    repeat(5) {
//        launch(dispatcher + job) {
//            Thread.sleep(1000)
//        }
//    }
//    job.complete()
//    val time = measureTimeMillis { job.join() }
//    println(time)
//}

// 가상 스레드를 사용한 커스텀 디스패치
//object LoomDispatcher : ExecutorCoroutineDispatcher() {
//    override val executor: Executor = Executor { command -> Thread.startVirtualThread(command) }
//
//    override fun dispatch(context: CoroutineContext, block: Runnable) {
//        executor.execute(block)
//    }
//
//    override fun close() {
//        error("Cannot be invoked on Dispatchers.LOOM")
//    }
//}
//
//val Dispatchers.Loom: CoroutineDispatcher
//    get() = LoomDispatcher

//suspend fun main() = measureTimeMillis {
//    coroutineScope {
//        repeat(100_000){
//            launch(Dispatchers.Loom) {
//                Thread.sleep(1000)
//            }
//        }
//    }
//}.let(::println)

//suspend fun main() = measureTimeMillis {
//    val dispatcher = Dispatchers.IO.limitedParallelism(100_000)
//    coroutineScope {
//        repeat(100_000){
//            launch(dispatcher) {
//                Thread.sleep(1000)
//            }
//        }
//    }
//}.let(::println)