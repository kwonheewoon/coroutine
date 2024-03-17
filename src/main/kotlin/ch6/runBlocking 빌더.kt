package ch6

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

// 코루틴을 실행한 뒤 완료될 때까지 현재 스레드를 중단 가능한 상태로 불로킹
//fun main(){
//    // 사용처
//    // 1. 프로그램이 끝나는 걸 방지하기 위해 슬레드를 블로킹할 필요가 있는 메인함수
//    // 블로킹할 필요가 있는 유닛테스트
//    runBlocking {
//        delay(1000L)
//        println("blocking 1")
//    }
//
//    runBlocking {
//        delay(1000L)
//        println("blocking 2")
//    }
//
//    runBlocking {
//        delay(1000L)
//        println("blocking 3")
//    }
//
//    println("Hello, ")
//}

// suspend 키워드를 붙여 main함수를 중단시켜 runBlocking빌더를 사용한거와 같은 효과
suspend fun main(){
    GlobalScope.launch {
        delay(1000L)
        println("World! 11")
    }

    GlobalScope.launch {
        delay(1000L)
        println("World! 22")
    }

    GlobalScope.launch {
        delay(1000L)
        println("World! 33")
    }

    GlobalScope.launch {
        delay(1000L)
        println("World! 44")
    }
    println("Hello,")
    Thread.sleep(2000L)
}