package ch16

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

// SUSPEND(기본 옵션) 버퍼가 가득 찼을 때 send 메서드가 중단
// DROP_OLDEST 버퍼가 가득 찼을 때 가장 오래된 원소가 제거
// DROP_LATEST 버퍼가 가득 찼을 때 가장 최근의 원소가 제거

//suspend fun main(): Unit = coroutineScope {
//    val channel = Channel<Int>(
//        capacity = 2,
//        onBufferOverflow = BufferOverflow.DROP_OLDEST // 오래된 원소 제거
//    )
//
//    launch {
//        repeat(5) { index ->
//            channel.send(index * 2)
//            delay(100)
//            println("Sent")
//        }
//        channel.close()
//    }
//    delay(1000)
//    for (element in channel) {
//        println(element)
//        delay(1000)
//    }
//}

// onUndeliveredElement 원소가 처리되지 않을 때 콜백
//val channel = Channel<Resource>(capacity = 4, onUndeliveredElement = {resource -> resource.close() })

// 팬아웃
// 여러개의 코루틴이 하나의 채널로부터 원소를 받을 수 있다
// 원소를 적절하게 하려면 반드시 for 루프를 사용 consumerEach는 여러개의 코루틴이 사용하기에는 안전하지 않는다

//fun CoroutineScope.produceNumbers() = produce {
//    repeat(10) {
//        delay(100)
//        send(it)
//    }
//}
//
//fun CoroutineScope.launchProcessor(
//    id: Int,
//    channel: ReceiveChannel<Int>
//) = launch {
//    for (msg in channel){
//        println("#$id received $msg")
//    }
//}
//
//suspend fun main(): Unit = coroutineScope {
//    val channel = produceNumbers()
//    repeat(3) { id ->
//        //delay(10)
//        launchProcessor(id, channel)
//    }
//}

// 팬인 여러개의 코루틴이 하나의 채널로 원소를 전송할 수 있다
//suspend fun sendString(
//    channel: SendChannel<String>,
//    text: String,
//    time: Long
//) {
//    while (true) {
//        delay(time)
//        channel.send(text)
//    }
//}
//
//fun main() = runBlocking {
//    val channel = Channel<String>()
//    launch { sendString(channel, "foo", 200L) }
//    launch { sendString(channel, "BAR!", 500L) }
//    repeat(50) {
//        println(channel.receive())
//    }
//    coroutineContext.cancelChildren()
//}

// 다수의 채널을 하나의 채널로 합치는 방법은 produce 함수로 여러개의 채널을 합치는 fanIn 함수 사용 가능
fun <T> CoroutineScope.fanIn(
    channels: List<ReceiveChannel<T>>
): ReceiveChannel<T> = produce {
    for (channel in channels){
        launch {
            for (elem in channel) {
                send(elem)
            }
        }
    }
}

suspend fun sendString(
    channel: SendChannel<String>,
    text: String,
    time: Long
) {
    while (true) {
        delay(time)
        channel.send(text)
    }
}

fun main() = runBlocking {
    // 두 개의 SendChannel을 생성합니다.
    val channel1 = Channel<String>()
    val channel2 = Channel<String>()

    // `fanIn` 함수를 사용하여 두 채널을 결합합니다.
    val fanInChannel = fanIn(listOf(channel1, channel2))

    // 첫 번째 채널에서 데이터를 보내는 코루틴
    launch {
        repeat(5) {
            channel1.send("Message from channel 1: $it")
            delay(200L) // 각 메시지 사이에 약간의 지연
        }
        channel1.close() // 데이터 전송이 완료되면 채널을 닫습니다.
    }

    // 두 번째 채널에서 데이터를 보내는 코루틴
    launch {
        repeat(3) {
            channel2.send("Message from channel 2: $it")
            delay(400L) // 각 메시지 사이에 약간의 지연
        }
        channel2.close() // 데이터 전송이 완료되면 채널을 닫습니다.
    }

    // 결합된 채널로부터 데이터를 수신하고 출력하는 코루틴
    launch {
        for (msg in fanInChannel) {
            println(msg)
        }
    }

    delay(2000)
    coroutineContext.cancelChildren()
}

// 파이프라인
//fun CoroutineScope.numbers(): ReceiveChannel<Int> =
//    produce {
//        repeat(3) {
//            num -> send(num + 1)
//        }
//    }
//
//fun CoroutineScope.square(numbers: ReceiveChannel<Int>) =
//    produce {
//        for (num in numbers){
//            send(num * num)
//        }
//    }
//
//suspend fun main() = coroutineScope {
//    val numbers = numbers()
//    val squared = square(numbers)
//    for (num in squared) {
//        println(num)
//    }
//}