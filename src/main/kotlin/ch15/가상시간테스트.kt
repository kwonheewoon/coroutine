package ch15

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.*

// 가상 시간
//fun main() {
//    val scheduler = TestCoroutineScheduler()
//
//    println(scheduler.currentTime)
//    scheduler.advanceTimeBy(1_000)
//    println(scheduler.currentTime)
//    scheduler.advanceTimeBy(1_000)
//    println(scheduler.currentTime)
//}
// StandardTestDispatcher는 다른 디스패처와 달리 코루틴이 실행되어야 할 스레드를 결정 할때만 사용되는 것은 아니다
// 테스트 디스패처로 시작된 코루틴으 ㄴ가상 시간만큼 진행되기 전까지 실행되지 않습니다.
// 코루틴을 시작하는 일반적인 방법은, 실제 시간처럼 작동하는 가상 시간을 흐르게 하여 그 시간동안
// 호출되었을 모든 작업을 실행하는 advanceUntilIdled을 사용해야됨
// 기본적으로 StandardTestDispatcher는 TestCoroutineScheduler를 만들기 때문에 TestCoroutineScheduler를 명시적으로 호출하지 않아도 됨
//fun main() {
//    //val scheduler = TestCoroutineScheduler()
//    //val testDispatcher = StandardTestDispatcher(scheduler)
//    val testDispatcher = StandardTestDispatcher()
//
//    CoroutineScope(testDispatcher).launch {
//        println("Some work 1")
//        delay(1000)
//        println("Some work 2")
//        delay(1000)
//        println("Coroutine done")
//    }
//
//    println("[${testDispatcher.scheduler.currentTime}] Before")
//    testDispatcher.scheduler.advanceUntilIdle()
//    println("[${testDispatcher.scheduler.currentTime}] After")
//}

// StandardTestDispatcher가 직접 시간을 흐르게 하지 않는다
// 시간을 흐르게 하지 않으면 코루틴이 다시 재개되지 않는다
//fun main() {
//    val testDispatcher = StandardTestDispatcher()
//
//    runBlocking(testDispatcher) {
//        delay(1)
//        println("Coroutine done")
//    }
//}

// 시간을 흐르게하는 또 다른 방법은 advanceTimeBy에 일정 밀리초를 인자로 넣어주는 것
// advanceTimeBy는 시간을 흐르게 하고 그동안 일어났을 모든 연산을 수행
//fun main() {
//    val testDispatcher = StandardTestDispatcher()
//
//    CoroutineScope(testDispatcher).launch {
//        delay(1)
//        println("Done1")
//    }
//
//    CoroutineScope(testDispatcher).launch {
//        delay(2)
//        println("Done2")
//    }
//    CoroutineScope(testDispatcher).launch {
//        delay(3)
//        println("Done2")
//    }
//    testDispatcher.scheduler.advanceTimeBy(3)
//    testDispatcher.scheduler.runCurrent() // 정확히 일치하는 시간에 예정된 연산을 재개
//}

//fun main() {
//    val dispatcher = StandardTestDispatcher()
//
//    CoroutineScope(dispatcher).launch {
//        delay(1000)
//        println("Coroutine done")
//    }
//
//    Thread.sleep(Random.nextLong(10000)) // Thread sleep 는 결과에 영향을 주지 않는다
//
//    val time = measureTimeMillis {
//        println("[${dispatcher.scheduler.currentTime}] Before")
//        dispatcher.scheduler.advanceUntilIdle()
//        println("[${dispatcher.scheduler.currentTime}] After")
//    }
//    println("Took $time ms")
//}

//fun main() {
//    // 이 스코프는 사용하는 스케줄러에 advanceUntilIdle, advanceTimeBy, currentTime 프로퍼티가 위임되기 때문에 간편한 사용 가능
//    // StandardTestDispatcher() 이거 써서 코루틴 스코프에 보내주는 번거로움 없어도 됨
//    // TestScope는 public val testScheduler: TestCoroutineScheduler 를 가지고 엤다
//    val scope = TestScope()
//
//    scope.launch {
//        delay(1000)
//        println("First done")
//        delay(1000)
//        println("Coroutine done")
//    }
//
//    println("[${scope.currentTime}] Before")
//    scope.advanceTimeBy(1000)
//    scope.runCurrent()
//    println("[${scope.currentTime}] Middle")
//    scope.advanceUntilIdle()
//    println("[${scope.currentTime}] After")
//}

// StandardTestDispatcher는 스케줄러를 사용하기 전까지 어떤 연산도 수행하지 않는다
// UnconfinedTestDispatcher는 코루틴을 시작했을 때 첫 번째 지연이 일어나기 전까지 모든 연산을 즉시 수행하기 때문에 다음 코드에서 'C'가 출력되는걸 확인

fun main(){
    CoroutineScope(StandardTestDispatcher()).launch {
        print("A")
        delay(1)
        println("B")
    }
    // 첫번째 지연 delay(1) 전 까지의 C 바로 출력
    CoroutineScope(UnconfinedTestDispatcher()).launch {
        println("C")
        delay(1)
        println("D")
    }
}