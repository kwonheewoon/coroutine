package ch20

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

//fun interface FlowCollector {
//    suspend fun emit(value: String)
//}
//
//interface Flow {
//    suspend fun collect(collector: FlowCollector)
//}
//
//suspend fun main() {
//    val builder: suspend (FlowCollector).() -> Unit = {
//        emit("A")
//        emit("B")
//        emit("C")
//    }
//
//    val flow: Flow = object : Flow {
//        override suspend fun collect(collector: FlowCollector) {
//            collector.builder()
//        }
//    }
//
//    flow.collect { print(it) }
//    flow.collect { print(it) }
//}

fun interface FlowCollector<T> {
    suspend fun emit(value: T)
}

interface Flow<T> {
    suspend fun collect(collector: FlowCollector<T>)
}

fun <T> flow(
    builder: suspend FlowCollector<T>.() -> Unit // FlowCollector<String>에 대해 확장 함수
    ) = object : Flow<T> {
        override suspend fun collect(collector: FlowCollector<T>) {
            collector.builder()

            // FlowCollector의 구현체는 이런 형태이고
//            fun interface FlowCollector<T> {
//                suspend fun emit(value: T) = {
//                    print(value)
//                }
//            }
            // builder는 emit을 3번 호출하는 함수 그러니깐 print를 3번 호출하는 형태로 구현되어있다
        }
    }

fun <T,R> Flow<T>.map(
    transformation: suspend (T) -> R
): Flow<R> = flow {
    // this = Flow<T> 이니깐 collect 메소드 호출 가능
    collect {
        emit(transformation(it)) // emit 인자로 transformation 반환값을 보낸다
    }
}

suspend fun main() {
    val f: Flow<String> = flow {
        emit("A")
        emit("B")
        emit("C")
    }
    f.collect {
        print(it)
    }
    println()
    f.collect { print(it) }
    println()
    f
        .map {
            delay(1000)
            it.lowercase()
        }
        .collect{ println(it) }
}
