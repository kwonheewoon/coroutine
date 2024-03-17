package ch6

import kotlinx.coroutines.*

// async는 값을 반환할때 써야한다 launch와 똑같은 방식으로는 사용하지 않아야 한다

//fun main() = runBlocking {
//    val resultDeferred: Deferred<Int> = GlobalScope.async {
//        delay(1000L)
//        42
//    }
//    val result: Int = resultDeferred.await()
//    println(result)
//    println(resultDeferred.await())
//}

fun main() = runBlocking {
    val res1 = GlobalScope.async {
        delay(1000L)
        "Text 1"
    }

    val res2 = GlobalScope.async {
        delay(3000L)
        "Text 2"
    }

    val res3 = GlobalScope.async {
        delay(2000L)
        "Text 3"
    }

    println(res1.await()) // 1초 기다리고
    println(res2.await()) // 2초 기다리고
    println(res3.await()) // 이미 3초를 기다렸으니 바로 반환
}