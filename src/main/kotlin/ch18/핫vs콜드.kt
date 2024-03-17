package ch18

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow

//fun main() {
//    val ll = buildList<String> {
//        repeat(3) {
//            add("User $it")
//            println("L: Added User")
//        }
//    }
//
//    val l2 = ll.map {
//        println("LL: Processing")
//        "Processed $it"
//    }
//
//
//    // 시퀀스는 지연 계산이기 때문에 toList등 결과 함수를 호출해야 작업이 실행된다
//    val s = sequence<String> {
//        repeat(3) {
//            yield("User$it")
//            println("S: Added User")
//        }
//    }
//
//    val s2 = s.map {
//        println("S: Processing")
//        "Processed $it"
//    }
//
//    s2.toList()
//}


// 채널에 전송된 데이터는 소비하는 즉시 버퍼에서 삭제가 된다
// 그러므로 여러개의 소비자가 같은 채널을 소비하고 있어도 최초의 소비자만 데이터를 받을 수 있다
// 채널은 핫 데이터 소스
private fun CoroutineScope.makeChannel() = produce {
    println("Channel started")
    for (i in 1..3) {
        delay(1000)
        send(i)
    }
}

//suspend fun main() = coroutineScope {
//    val channel = makeChannel()
//
//    delay(1000)
//    println("Calling channel ...")
//    for(value in channel) {
//        println(value)
//    }
//    println("Consuming again ...")
//    for(value in channel){
//        println(value)
//    }
//}

// 플로우는 콜드 데이터 소스이다
// 플로우는 최종연산(collect)이 호출 될 때 원소가 어떻게 생성되어야 하는지 정의한 것에 불과
// 그래서 flow 빌더는 코루틴 스코프가 필요하지 않는다
// 플로우 빌더는 호출한 최종 연산의 스코프에서 실행됨
private fun makeFlow() = flow {
    println("Flow started")
    for (i in 1..3) {
        delay(1000)
        emit(i)
    }
}

suspend fun main() = coroutineScope {
    val flow = makeFlow()

    delay(1000)
    println("Calling flow...")
    flow.collect { value -> println(value) }
    println("Consuming again...")
    flow.collect { value -> println(value) }
}