package ch16

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// 이런 방식은 불안전하다
// 컨슈머 입장에서 프로듀서가 몇개의 메시지를 보냈는지 알 수 없기 때문
/*
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) {index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
        }
    }

    launch {
        repeat(5) {
            val received = channel.receive()
            println("Cosuming next one $received")
        }
    }
}*/
// 이 소스도 불안정하다
// 프로듀서 부분에서 예외 발생 혹은 channel.close를 깜빡한다면 컨슈머 입장에선 무한정 기다릴수 밖에 없다
/*
suspend fun main(): Unit = coroutineScope {
    val channel = Channel<Int>()
    launch {
        repeat(5) {index ->
            delay(1000)
            println("Producing next one")
            channel.send(index * 2)
        }
        channel.close()
    }

    launch {
//        for (element in channel){
//            println(element)
//        }
        channel.consumeEach {
            element -> println(element)
        }
    }
}*/

// 이 함수는 0부터 max 까지의 양수를 가진 채널을 생성
// produce 빌더는 어떤 문제가 발생히도 채널을 close 한다
//fun CoroutineScope.produceNumbers(max: Int): ReceiveChannel<Int> = produce{
//    var x = 0
//    while (x < 5) send(x++)
//}
//
//suspend fun main(): Unit = coroutineScope {
//    val channel = produce {
//        repeat(5) {index ->
//            println("Producing next one")
//            delay(1000)
//            send(index * 2)
//        }
//    }
//
//    for (element in channel) {
//        println(element)
//    }
//}

// 채널 타입
// 무제한(Channel.UNLIMITED) send가 중단되지 않는다
// 버퍼(Channel.BUFFERD) 기본값은 64
// 랑데뷰(Channel.RENDZEVOUS) 용량이 0인 채널로 송신자와 수신자가 만날 때만 원소를 교환
// 융합(Channel.CONFLATED) 새로운 원소가 이전 원소를 대체
// 무제한 타입
//suspend fun main(): Unit = coroutineScope {
//    val channel = produce(capacity = Channel.UNLIMITED) {
//        repeat(5) {index ->
//            send(index * 2)
//            delay(100)
//            println("Sent")
//        }
//    }
//    delay(1000)
//    for (element in channel) {
//        println(element)
//        delay(1000)
//    }
//}

// 버퍼 타입
// 버퍼가 가득 찰 때까지 우너소가 생성되고
// 이후에는 생성자는 수신자가 원소를 소비하기를 기다리기 시작
//suspend fun main(): Unit = coroutineScope {
//    val channel = produce(capacity = 3) {
//        repeat(5) {index ->
//            send(index * 2)
//            delay(100)
//            println("Sent")
//        }
//    }
//
//    delay(1000)
//
//    for (element in channel) {
//        println(element)
//        delay(1000)
//    }
//}

// 랑데뷰 타입
//// 송신자는 항상 수신자를 기다린다 1 대 1 전송
//suspend fun main(): Unit = coroutineScope {
//    val channel = produce {
//        repeat(5) {index ->
//            send(index * 2)
//            delay(100)
//            println("Sent")
//        }
//    }
//
//    delay(1000)
//
//    for (element in channel) {
//        println(element)
//        delay(1000)
//    }
//}

// CONFLATED 채널 타입
// 이전 원소를 더 이상 저장하지 않는다 새로운 원소가 이전 원소를 대체하며 최근 원소만 받을 수 있게 된다
// 최신 메시지만 받는다는 개념
suspend fun main(): Unit = coroutineScope {
    val channel = produce(capacity = Channel.CONFLATED){
        repeat(5) {index ->
            send(index * 2)
            delay(100)
            println("Sent")
        }
    }

    delay(1000)

    for (element in channel){
        println(element)
        delay(1000)
    }
}

