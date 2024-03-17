package ch10

import kotlinx.coroutines.*

// 에러는 Job으로 전파되기 때문에
// try catch 문으로 잡지 못한다
// 자식의 에러 전파는 부모에 전달되고 부모의 잡은 취소되며 자식 잡까지 전부 취소된다
//fun main(): Unit = runBlocking {
//    launch {
//        launch {
//            delay(1000)
//            throw Error("Some error")
//        }
//        launch {
//            delay(2000)
//            println("Will not be printed")
//        }
//        launch {
//            delay(500)
//            println("Will be printed")
//        }
//    }
//    launch {
//        delay(2000)
//        println("Will not be printed")
//    }
//}
// 슈퍼바이저는 자식 스코프의 예외를 다른 자식 스코프에 전파하지 않는다
//fun main(): Unit = runBlocking {
//    val scope = CoroutineScope(SupervisorJob())
//    scope.launch {
//        delay(1000)
//        throw Error("Some error")
//    }
//
//    scope.launch {
//        delay(2000)
//        println("Will be printed")
//    }
//
//    delay(3000)
//}

//suspend fun main(): Unit = runBlocking {
//    launch(SupervisorJob()) {
//        launch {
//            delay(1000)
//            throw Error("Some error")
//        }
//        launch {
//            delay(2000)
//            println("Will not be printed")
//        }
//    }
//    delay(3000)
//}

//suspend fun main(): Unit = runBlocking {
//    val job = SupervisorJob()
//    launch(job) {
//        delay(1000)
//        throw Error("Some error")
//    }
//    launch(job) {
//        delay(2000)
//        println("Will not be printed")
//    }
//
//    delay(3000)
//}
// supervisorScope 스코프를 사용해 예외 전파를 막을수 있다
//suspend fun main(): Unit = runBlocking {
//    supervisorScope {
//        launch {
//            delay(1000)
//            throw Error("Some error")
//        }
//
//        launch {
//            delay(1000)
//            println("Will be printed")
//        }
//    }
//
//    delay(1000)
//    println("Done")
//}
// async 메소드를 await로 기다릴시 예외를 캐치할수 있다
//class MyException : Throwable()
//
//suspend fun main() = supervisorScope {
//    val str1 = async<String> {
//        delay(1000)
//        throw MyException()
//    }
//
//    val str2 = async {
//        delay(2000)
//        "Text2"
//    }
//
//    try {
//        println(str1.await())
//    } catch (e: MyException){
//        println(e)
//    }
//    println(str2.await())
//}
//CancellationException 은 부모까지 전파되지 않는다
//object MyNonPropagatingException : CancellationException()
//
//suspend fun main(): Unit = coroutineScope {
//    launch {
//        launch {
//            delay(1000)
//            println("Will not be printed")
//        }
//        throw MyNonPropagatingException
//    }
//    launch {
//        delay(2000)
//        println("Will be printed")
//    }
//}
// 예외를 처리하는 handler 콜백 함수
fun main(): Unit = runBlocking {
    val handler =
        CoroutineExceptionHandler{ctx, exception ->
            println("Caught $exception")
        }
    val scope = CoroutineScope(SupervisorJob() + handler)
    scope.launch {
        delay(1000)
        throw Error("Some Error")
    }

    scope.launch {
        delay(2000)
        println("Will be printed")
    }

    delay(3000)
}