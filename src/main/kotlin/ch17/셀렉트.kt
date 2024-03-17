package ch17

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select

// select는 가장 먼저 완료되는 코루틴의 결괏값을 기다릴 때나, 여러개의 채널 중
// 전송 또는 수신 가능한 채널을 선택할 때 유용하다 주로 채널에서 작동하는 다양한 패턴을
// 구현ㄴ할 때 사용하지만 async 코루틴의경합을 구현할 때도 사용 가능

suspend fun requestData1(): String {
//    delay(100_000)
    delay(100)
    return "Data1"
}

suspend fun requestData2(): String {
    delay(11000)
    return "Data2"
}

val scope = CoroutineScope(SupervisorJob())

//suspend fun askMultipleForData(): String {
//    val defData1 = scope.async { requestData1() }
//    val defData2 = scope.async { requestData2() }
//
//    // select : 여러개의 비동기 작업중 1개의 비동기작업이 끝나는 순간 그 비동기 작업만 리턴
//    return select {
//        defData1.onAwait { it }
//        defData2.onAwait { it }
//    }
//}

// 자식 코루틴까지 기다리므로 모든 코루틴이 끝난후 제일 빨리 끝난 결괏값을 리턴
//suspend fun askMultipleForData(): String = coroutineScope {
//
//    // select : 여러개의 비동기 작업중 1개의 비동기작업이 끝나는 순간 그 비동기 작업만 리턴
//    select {
//        async { requestData1() }.onAwait { it }
//        async { requestData2() }.onAwait { it }
//    }
//}

// 코루틴끼리 경합을 발생시켜 제일 빨리 끝나는 스코프의 작업 외의 스코프의 작업을 취소
suspend fun askMultipleForData(): String = coroutineScope {

    // select : 여러개의 비동기 작업중 1개의 비동기작업이 끝나는 순간 그 비동기 작업만 리턴
    select {
        async { requestData1() }.onAwait { it }
        async { requestData2() }.onAwait { it }
    }.also { coroutineContext.cancelChildren() }
}

//suspend fun main(): Unit = coroutineScope {
//    println(askMultipleForData())
//}

// select와 channel의 활용
suspend fun CoroutineScope.produceString(
    s: String,
    time: Long
) = produce{
    while (true) {
        delay(time)
        send(s)
    }
}

// 채널에서 먼저 receive의 결괏값을 먼저 반환
//fun main() = runBlocking {
//    val fooChannel = produceString("foo", 210L)
//    val barChannel = produceString("BAR", 500)
//
//    repeat(7) {
//        select {
//            // onReceive : 채널이 값을 가지고 있을 때 선택된다
//            fooChannel.onReceive {
//                println("From fooChannel: $it")
//            }
//            barChannel.onReceive {
//                println("From barChannel: $it")
//            }
//        }
//    }
//
//    coroutineContext.cancelChildren()
//}

// 셀렉트 함수에서 onSend를 호출하면 버퍼에 공간이 있는 채널을 선택해 데이터를 전송하는 용도로 사용 가능
fun main(): Unit = runBlocking {
    val c1 = Channel<Char>(capacity = 2)
    val c2 = Channel<Char>(capacity = 2)

    // 값을 보낸다
    launch {
        for(c in 'A'..'H') {
            delay(400)
            select<Unit> {
                c1.onSend(c) { println("Sent $c to 1") }
                c2.onSend(c) { println("Sent $c to 2") }
            }
        }
    }

    // 값을 받는다
    launch {
        while (true) {
            delay(1000)
            val c = select<String> {
                c1.onReceive { "$it from 1" }
                c2.onReceive { "$it from 2" }
            }
            println("Received $c")
        }
    }
}