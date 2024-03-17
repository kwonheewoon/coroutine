package ch24

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

// 공유 플로우
// 브로드캐스트 채널과 비슷한 MutableSharedFlow
// 공유플로우를 통해 메시지를 보내면 (내보내면) 대기하고 있는 모든 코루틴이 수신

//suspend fun main(): Unit = coroutineScope {
//    // 이 소스는 coroutineScope의 자식 코루틴이 launch로 시작된 뒤 MutableSharedFlow를 감지하고 있는 상태이므로 종료되지 않는다.
//    val mutableSharedFlow =
//        MutableSharedFlow<String>(replay = 0)
//
//    launch {
//        mutableSharedFlow.collect {
//            println("#1 received $it")
//        }
//    }
//
//    launch {
//        mutableSharedFlow.collect {
//            println("#2 received $it")
//        }
//    }
//
//    delay(1000)
//    mutableSharedFlow.emit("Message1")
//    mutableSharedFlow.emit("Message2")
//}

// MutableSharedFlow는 메시지 보내는 작업을 유지할 수 있다.
// replay 인자를 설정하면 마지막으로 전송한 값들이 정해진 수만큼 저장된다.
// 코루틴이 감지를 시작하면 저장된 값들을 먼저 받게 된다 restReplayCache를 사용하면 값을 저장한 캐시를 초기화할 수 있다.

suspend fun main(): Unit = coroutineScope {
    val mutableSharedFlow = MutableSharedFlow<String>(replay = 2)

    mutableSharedFlow.emit("Message1")
    mutableSharedFlow.emit("Message2")
    mutableSharedFlow.emit("Message3")

    println(mutableSharedFlow.replayCache)
    // [Message2, Message3]

    launch {
        mutableSharedFlow.collect {
            println("#1 received $it")
            // #1 received Message2
            // #1 received Message3
        }
    }

    delay(100)
    mutableSharedFlow.resetReplayCache()
    println(mutableSharedFlow.replayCache)
}

// 코틀린에서는 감지만 하는 인터페이스와 변경하는 인터페이스를 구분하는 것이 관행이다.
// 앞에서 SendChannel, ReceiveChannel, Channel로 구분하는 걸 예로 들 수 있다.
// MutableSharedFlow는 SharedFlow와 FlowCollector 모두를 상속한다.
// SharedFlow는 Flow를 상속하고 감지하는 목적으로 사용되며, FlowCollector는 값을 내보내는 목적으로 사용된다.

interface MutableSharedFlow<T>: SharedFlow<T>, FlowCollector<T> {
    fun tryEmit(value: T): Boolean
    val subscriptionCount: StateFlow<Int>
    fun resetReplayCache()
}

interface SharedFlow<out T> : Flow<T> {
    val replayCache: List<T>
}

interface FlowCollector<in T> {
    suspend fun emit(value: T)
}