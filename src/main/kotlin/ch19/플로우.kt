package ch19

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext

// 일반적인 컬렉션은 모든 원소가 생성되기 까지 기다렸다 연산이 이루어진다
// 동기적이라 할 수 있다

fun getList(): List<String> = List(3) {
    Thread.sleep(1000)
    "User$it"
}

// 리스트 원소가 모두 생성되는 3초 이후 println, forEach 구문 실행됨
//fun main(){
//    val list = getList()
//    println("Function started")
//    list.forEach { println(it) }
//}

// 시퀀스는 최종연산 함수를 호출하는 순간 연산이 시작되며 연산은 원소마다 차례대로 연산되며
// 연산 되는 즉시 반환된다
// 시퀀스는 cpu 집약적인 연산에 적합
// 시퀀스 최종연산은 중단함수가 아니기 때문에 스레드 블로킹될 수 있음
// 시퀀스 내부에서는 yield 와 yieldAll 중단함수 외에 다른 중단함수 사용 불가능
//fun getSequence(): Sequence<String> = sequence {
//    repeat(3) {
//        Thread.sleep(1000)
//        yield("User$it")
//    }
//}
//
//fun main() {
//    val list = getSequence()
//    println("Function started")
//    list.forEach { println(it) }
//}

// 시퀀스의 잘못된 사용 방법
fun getSequence(): Sequence<String> = sequence {
    repeat(3) {
        Thread.sleep(1000)
        yield("User$it")
    }
}
// Sequence를 사용했기 때문에 forEach가 블로킹 연산이 되며
// 같은 스레드에서 launch로 시작된 코루틴이 대기하게 되며 하나의 코루틴이 다른 코루틴을 불로킹하게 된다
//suspend fun main() {
//    withContext(newSingleThreadContext("main")) {
//        launch {
//            repeat(3) {
//                delay(100)
//                println("Processing on coroutine")
//            }
//        }
//
//        val list = getSequence()
//        list.forEach { println(it) }
//    }
//}


fun getFlow(): Flow<String> = flow {
    repeat(3) {
        delay(1000)
        emit("User$it")
    }
}

suspend fun main() {
    withContext(newSingleThreadContext("main")) {
        launch {
            repeat(3) {
                delay(100)
                println("Processing on coroutine")
            }
        }

        val list = getFlow()
        list.collect { println(it) }
    }
}
